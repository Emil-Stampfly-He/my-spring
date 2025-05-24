package com.email.spring.core;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PatternMatchingResourcePatternResolver extends DefaultResourceLoader implements ResourcePatternResolver{

    private static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        try {
            if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
                String pathPattern = locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length());
                // 1. 按 '/' 拆包路径，截取根包路径
                // 2. 用 ClassLoader 找到所有根资源 URL
                String rootPath = pathPattern.split("\\*\\*")[0];
                Enumeration<URL> urls = getClassLoader().getResources(rootPath);
                List<Resource> result = new ArrayList<>();
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    if ("file".equals(url.getProtocol())) {
                        File rootDir = new File(url.toURI());
                        scanFileSystem(rootDir, rootPath, pathPattern, result);
                    } else if ("jar".equals(url.getProtocol())) {
                        scanJarFile(url, rootPath, pathPattern, result);
                    }
                }
                return result.toArray(new Resource[0]);
            }
            // fallback：单资源
            return new Resource[]{ getResource(locationPattern) };
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void scanFileSystem(File rootDir, String rootPath, String pattern, List<Resource> res) {
        String rootDirPath = rootDir.getAbsolutePath().replace(File.separatorChar, '/');
        for (File f : Objects.requireNonNull(rootDir.listFiles())) {
            if (f.isDirectory()) {
                scanFileSystem(f, rootPath, pattern, res);
            } else {
                // 1) 计算相对于 rootDir 的路径
                String fullPath = f.getAbsolutePath().replace(File.separatorChar, '/');
                String relativeToRoot = fullPath.substring(rootDirPath.length() + 1);
                // relativeToRoot == "util/PatternMatchingResourcePatternResolver.class"

                // 2) 拼回带包前缀的 resourcePath
                String resourcePath = rootPath + relativeToRoot;
                // resourcePath == "com/email/spring/" + "util/PatternMatchingResourcePatternResolver.class"

                // 3) 再用 pattern 去 match
                if (PathMatcher.match(pattern, resourcePath)) {
                    res.add(new FileSystemResource(f.getAbsolutePath()));
                }
            }
        }
    }

    private void scanJarFile(URL url, String rootPath, String pattern, List<Resource> res) throws IOException {
        String file = url.getFile();
        String jarPath = file.substring(5, file.indexOf("!"));
        try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith(rootPath) && PathMatcher.match(pattern, name)) {
                    res.add(new ClassPathResource(name, getClassLoader()));
                }
            }
        }
    }

    private static class PathMatcher {
        public static boolean match(String pattern, String path) {
            if (pattern == null || path == null) return false;
            String[] patDirs  = tokenizeToStringArray(pattern, "/");
            String[] pathDirs = tokenizeToStringArray(path, "/");

            int patStart = 0, patEnd = patDirs.length - 1;
            int pathStart = 0, pathEnd = pathDirs.length - 1;

            // 1. 从头部开始，逐段匹配，直到遇到 ** 为止
            while (patStart <= patEnd && !patDirs[patStart].equals("**")) {
                if (pathStart > pathEnd || !matchSegment(patDirs[patStart], pathDirs[pathStart])) {
                    return false;
                }
                patStart++;
                pathStart++;
            }

            // 2. 如果已经遍历完 path，还要检查剩余的 pattern 是否全为 **
            if (pathStart > pathEnd) {
                for (int i = patStart; i <= patEnd; i++) {
                    if (!patDirs[i].equals("**")) {
                        return false;
                    }
                }
                return true;
            }

            // 3. 从尾部开始，逐段匹配，直到遇到 ** 为止
            while (patEnd >= patStart && !patDirs[patEnd].equals("**")) {
                if (pathEnd < pathStart || !matchSegment(patDirs[patEnd], pathDirs[pathEnd])) {
                    return false;
                }
                patEnd--;
                pathEnd--;
            }

            // 4. 中间部分有 **，尝试跨段匹配
            while (patStart != patEnd && pathStart <= pathEnd) {
                // 找到下一个 ** 在 pattern 中的位置
                int nextPat = -1;
                for (int i = patStart + 1; i <= patEnd; i++) {
                    if (patDirs[i].equals("**")) {
                        nextPat = i;
                        break;
                    }
                }
                // 如果两个 ** 相邻，跳过一个
                if (nextPat == patStart + 1) {
                    patStart++;
                    continue;
                }

                // 子模式长度（不含 **）
                int subLen = nextPat - patStart - 1;
                // 在 path 中尝试匹配这一段
                boolean found = false;
                for (int i = 0; i <= (pathEnd - pathStart + 1) - subLen; i++) {
                    boolean ok = true;
                    for (int j = 0; j < subLen; j++) {
                        if (!matchSegment(patDirs[patStart + j + 1], pathDirs[pathStart + i + j])) {
                            ok = false;
                            break;
                        }
                    }
                    if (ok) {
                        found = true;
                        pathStart = pathStart + i + subLen;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
                patStart = nextPat;
            }

            // 5. 剩下的 pattern 必须全是 **
            for (int i = patStart; i <= patEnd; i++) {
                if (!patDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        }

        /**
         * 匹配单条路径段（不含 '/'），支持 ? 和 *。
         */
        private static boolean matchSegment(String pattern, String str) {
            int p = 0, s = 0;
            int starIdx = -1, match = 0;
            while (s < str.length()) {
                if (p < pattern.length() &&
                        (pattern.charAt(p) == '?' || pattern.charAt(p) == str.charAt(s))) {
                    // ? 或者字符完全匹配
                    p++;
                    s++;
                } else if (p < pattern.length() && pattern.charAt(p) == '*') {
                    // 记录 * 的位置，以及此时 str 的位置
                    starIdx = p++;
                    match = s;
                } else if (starIdx != -1) {
                    // 回溯：让 * 多匹配一个字符
                    p = starIdx + 1;
                    s = ++match;
                } else {
                    return false;
                }
            }
            // 跳过尾部多余的 *
            while (p < pattern.length() && pattern.charAt(p) == '*') {
                p++;
            }
            return p == pattern.length();
        }

        /**
         * 用 StringTokenizer 按分隔符拆字符串，并去掉空项。
         */
        private static String[] tokenizeToStringArray(String str, String delim) {
            StringTokenizer st = new StringTokenizer(str, delim);
            List<String> tokens = new ArrayList<>();
            while (st.hasMoreTokens()) {
                String tok = st.nextToken();
                if (!tok.isEmpty()) {
                    tokens.add(tok);
                }
            }
            return tokens.toArray(new String[0]);
        }
    }
}

package com.email.spring.core.metadata;

import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SimpleMetadataReader implements MetadataReader {

    @Override
    public AnnotationMetadata readMetadata(InputStream classInput) throws IOException {
        ClassReader classReader = new ClassReader(classInput);
        AnnotationMetadataVisitor visitor = new AnnotationMetadataVisitor();
        classReader.accept(visitor,
                ClassReader.SKIP_DEBUG
                        | ClassReader.SKIP_FRAMES
                        | ClassReader.SKIP_CODE);
        return visitor.getAnnotationMetadata();
    }

    public String getClassName(URL url) {
        try (InputStream is = url.openStream()) {
            ClassReader cr = new ClassReader(is);
            ClassNameVisitor visitor = new ClassNameVisitor();
            // 只需要 header，不必读代码、调试信息、帧
            cr.accept(visitor,
                    ClassReader.SKIP_DEBUG
                            | ClassReader.SKIP_FRAMES
                            | ClassReader.SKIP_CODE);
            return visitor.getClassName();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static class ClassNameVisitor extends ClassVisitor {
        private String internalName;

        ClassNameVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public void visit(int version,
                          int access,
                          String name,
                          String signature,
                          String superName,
                          String[] interfaces) {
            this.internalName = name;
        }

        /**
         * 'com/foo/Bar' → 'com.foo.Bar'
         */
        String getClassName() {
            return internalName.replace('/', '.');
        }
    }

    private static class AnnotationMetadataVisitor extends ClassVisitor {
        private final AnnotationMetadata metadata = new AnnotationMetadata();

        AnnotationMetadataVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            // 1. 拿到注解的类名
            String annClass = Type.getType(desc).getClassName();
            metadata.addAnnotation(annClass);

            // 2. 用一个“代理”AnnotationVisitor 去拦截属性
            AnnotationVisitor av = super.visitAnnotation(desc, visible);
            return new AnnotationVisitor(Opcodes.ASM9, av) {
                @Override
                public void visit(String attrName, Object value) {
                    // 单个属性，可能是 String/enum/class/primitive
                    metadata.addAttribute(annClass, attrName, value);
                    super.visit(attrName, value);
                }

                @Override
                public AnnotationVisitor visitArray(String attrName) {
                    // 数组属性，比如 String[] basePackages
                    List<Object> list = new ArrayList<>();
                    AnnotationVisitor arrayAv = super.visitArray(attrName);
                    return new AnnotationVisitor(Opcodes.ASM9, arrayAv) {
                        @Override
                        public void visit(String n, Object v) {
                            list.add(v);
                            super.visit(n, v);
                        }
                        @Override
                        public void visitEnd() {
                            // toArray[String]
                            String[] arr = list.stream()
                                    .map(Object::toString)
                                    .toArray(String[]::new);
                            metadata.addAttribute(annClass, attrName, arr);
                            super.visitEnd();
                        }
                    };
                }
            };
        }

        AnnotationMetadata getAnnotationMetadata() {
            return this.metadata;
        }
    }
}


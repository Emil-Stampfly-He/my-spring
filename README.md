<div align="center">
    <h1>
        iSpring&nbsp;🍃&nbsp
    </h1>
</div>

<div align="center">
    <h3> 
        A not-so-minimal Spring framework model
    </h3>
</div>

---


## 🗺️ RoadMap

### 1. Bean
- [x] `BeanDefinition` & `Resource`
- [x] Basic `BeanFactory` & `ApplicationContext`
- [x] `BeanFactoryPostProcessor` for `@ComponentScan`， `@Component` & its derivative annotations
- [ ] `BeanFactoryPostProcessor` for `@Configuration`, `@Bean` & `@Scope`
- [ ] `BeanPostProcessor` for `@Autowired`
- [ ] `BeanPostProcessor` for `@Resource`
- [ ] Add `initMethod` & `destroyMethod` for Beans
  - [ ] Getter & setter for `BeanDefinitionBuilder` & `BeanDefinition`
  - [ ] Related methods for `BeanFactory`
  - [ ] Support for `@PostConstruct` & `@PreDestroy` together with `@Resource`
  - [ ] `initMethod` & `destroyMethod` attributes in `@Bean`
- [ ] Add `lazyInit` for Beans
    - [ ] Getter & setter for `BeanDefinitionBuilder` & `BeanDefinition`
    - [ ] Related methods for `BeanFactory`
    - [ ] Support for `@Lazy`
    - [ ] Related attributes in `@Bean`
- [ ] Add `@Primary` and its support
- [ ] `Aware`, `InitializingBean` & `DestroyBean`
### 2. AOP
Scheduling...
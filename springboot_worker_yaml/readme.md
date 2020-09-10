# yml 配置
```
1. 只有容器中的组件才能使用 @ConfigurationProperties(prefix = "person")
2. 所以 bean 需要加 @Component
```
### 拦截器
```
https://mp.weixin.qq.com/s/UVy1if4LOQSTS9sM5EM1Jw
拦截器
1. 实现接口 HandlerInterceptor
2. 实现接口 HandlerInterceptor 中的三个方法
3. 新建配置类，实现接口 WebMvcConfigurer(spring 5.0 以后)， 添加 @Configuration 注解使其变成配置类


拦截器与过滤器的区别
1.参考标准
A. 过滤器是 JavaEE 的标准，依赖于 Servlet 容器，生命周期也与容器一致，利用这一特性可以在销毁时释放资源或者数据入库。
B. 拦截器是 SpringMVC 中的内容，依赖于web框架，通常用于验证用户权限或者记录日志，但是这些功能也可以利用 AOP 来代替。

2.实现方式
A. 过滤器是基于回调函数实现，无法注入 ioc 容器中的 bean。
B. 拦截器是基于反射来实现，因此拦截器中可以注入 ioc 容器中的 bean，例如注入 Redis 的业务层来验证用户是否已经登录。
```

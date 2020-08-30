# 关闭 springboot 启动 logo
```
SpringApplication springApplication = new SpringApplication(SpringbootWorkerClosespringlogoApplication.class);
springApplication.setBannerMode(Banner.Mode.OFF);
springApplication.run(args);
```
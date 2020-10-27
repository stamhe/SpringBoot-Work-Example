# springboot 打 war 包
```
1. 继承类 SpringBootServletInitializer， 实现 configure 方法
@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringbootWorkerWarApplication.class);
    }
2. 修改 pom.xml 里面的 <packaging>war</packaging> 
3. 新加打包插件
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
4. 打包命令
mvn clean package -Dmaven.test.skip=true
```
package com.stamhe.springboot.dsconfig;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

/*
 * 由于我们在SpringBootApplication中禁掉了自动数据源配置，需要在这儿手动创建 application.properteis 中的数据源
 */
@Configuration
@MapperScan(basePackages = {"com.stamhe.springboot.mapper.user"}, sqlSessionFactoryRef = "sqlSessionFactoryObj01")
public class UserDSConfig {
    @Bean(name = "ds_db01")
    @Primary
    @ConfigurationProperties(prefix="spring.datasource.user")
    public DataSource DB01DataSource() {
    	DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
    	return setDataSource(dataSource);
    }

    @Bean(name="sqlSessionFactoryObj01")
    @Primary
    public SqlSessionFactory sqlSessionFactory1(@Qualifier("ds_db01") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        return factoryBean.getObject();
    }

    @Bean(name="sqlSessionTemplateObj01")
    public SqlSessionTemplate sqlSessionTemplate1(@Qualifier("sqlSessionFactoryObj01") SqlSessionFactory sqlSessionFactory) throws Exception {
        SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory); // 使用上面配置的Factory
        return template;
    }
    
    /**
     * 设置Druid参数
     * @param dataSource
     * @return
     * @throws SQLException 
     */
    public DruidDataSource setDataSource(DruidDataSource druidDataSource)
    {
        // 初始化大小，最小，最大
        druidDataSource.setMaxActive(10);
        druidDataSource.setInitialSize(3);
        druidDataSource.setMinIdle(3);
        
        // 配置获取连接等待超时的时间 单位: ms
        druidDataSource.setMaxWait(100);
        
        // 打开后，增强timeBetweenEvictionRunsMillis的周期性连接检查，minIdle内的空闲连接，每次检查强制验证连接有效性. 参考：https://github.com/alibaba/druid/wiki/KeepAlive_cn
        druidDataSource.setKeepAlive(true);
        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位: ms
        druidDataSource.setTimeBetweenEvictionRunsMillis(30000);
        // 配置一个连接在池中最小生存的时间，单位: ms
        druidDataSource.setMinEvictableIdleTimeMillis(300000);

        // 配置一个连接在池中最小生存的时间，单位: ms
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        
        // 用来检测连接是否有效的sql，要求是一个查询语句。 如果validationQuery为null，testOnBorrow、testOnReturn、 testWhileIdle都不会其作用。
        druidDataSource.setValidationQuery("select 'x'");
        
        // 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于 timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
        druidDataSource.setTestWhileIdle(true);
        // 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
        druidDataSource.setTestOnBorrow(false);
        // 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        druidDataSource.setTestOnReturn(false);
        
        /* 打开PSCache，并且指定每个连接上PSCache的大小。
        如果用Oracle，则把poolPreparedStatements配置为true，
        mysql可以配置为false。分库分表较多的数据库，建议配置为false */
        druidDataSource.setPoolPreparedStatements(false);
        // 指定每个连接上PSCache的大小
        druidDataSource.setMaxOpenPreparedStatements(20);
        // 合并多个DruidDataSource的监控数据
        //druidDataSource.setUseGlobalDataSourceStat(true);
        
        // 自动提交设置为 true
        druidDataSource.setDefaultAutoCommit(true);
        
        /*
         * 连接泄露检查，打开removeAbandoned功能 , 连接从连接池借出后，长时间不归还，将触发强制回连接。回收周期随timeBetweenEvictionRunsMillis进行.
         * 如果连接为从连接池借出状态，并且未执行任何sql，并且从借出时间起已超过removeAbandonedTimeout时间，则强制归还连接到连接池中。
         */
        druidDataSource.setRemoveAbandoned(true);
        // 单位: s
        druidDataSource.setRemoveAbandonedTimeout(5);
        // 关闭abanded连接时输出错误日志，这样出现连接泄露时可以通过错误日志定位忘记关闭连接的位置
        druidDataSource.setLogAbandoned(true);
        
        //配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
        /*
        属性类型是字符串，通过别名的方式配置扩展插件， 
		常用的插件有： 
		监控统计用的filter:stat  
		日志用的filter:log4j 
		防御sql注入的filter:wall
         */
        try {
        	druidDataSource.setFilters("stat,wall,slf4j");
        }catch(Exception e) {
        	e.printStackTrace();
        }
        // 配置监控统计日志的输出间隔，每次输出所有统计数据会重置，酌情开启. 单位: ms
        druidDataSource.setTimeBetweenLogStatsMillis(10000);
            
    	return druidDataSource;
    }
}

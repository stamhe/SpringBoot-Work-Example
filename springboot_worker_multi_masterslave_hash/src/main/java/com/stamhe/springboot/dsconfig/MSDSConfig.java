package com.stamhe.springboot.dsconfig;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.stamhe.springboot.algorithm.DatabaseMSShardingAlgorithm;
import com.stamhe.springboot.algorithm.TableMSShardingAlgorithm;

import io.shardingsphere.api.algorithm.masterslave.RoundRobinMasterSlaveLoadBalanceAlgorithm;
import io.shardingsphere.api.config.rule.MasterSlaveRuleConfiguration;
import io.shardingsphere.api.config.rule.ShardingRuleConfiguration;
import io.shardingsphere.api.config.rule.TableRuleConfiguration;
import io.shardingsphere.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

/*
 * 由于我们在SpringBootApplication中禁掉了自动数据源配置，需要在这儿手动创建 application.properteis 中的数据源
 * 
 * 分库分表: hash、取模、按时间
 * https://blog.csdn.net/myshy025tiankong/article/details/83063887
 * 
 * 主从读写分离
 */
@Configuration
@MapperScan(basePackages = {"com.stamhe.springboot.mapper"}, sqlSessionFactoryRef = "sqlSessionFactoryUserMSHash")
public class MSDSConfig {
    List<MasterSlaveRuleConfiguration> getMasterSlaveRuleConfigurations() {
  		MasterSlaveRuleConfiguration msUserRuleConfig = new MasterSlaveRuleConfiguration("ds_hash_user", "ds_user_master", 
  				Arrays.asList("ds_user_slave01", "ds_user_slave02"), new RoundRobinMasterSlaveLoadBalanceAlgorithm());

  		MasterSlaveRuleConfiguration msArticleRuleConfig = new MasterSlaveRuleConfiguration("ds_hash_article", "ds_article_master", 
  				Arrays.asList("ds_article_slave01", "ds_article_slave02"), new RoundRobinMasterSlaveLoadBalanceAlgorithm());

  		List<MasterSlaveRuleConfiguration> list = new ArrayList<>();
  		list.add(msUserRuleConfig);
  		list.add(msArticleRuleConfig);
  		return list;
    }
    
    
    Map<String, DataSource> createDataSourceMap() {
        final Map<String, DataSource> result = new HashMap<>();
        
        result.put("ds_user_master", DBUserDataSourceMaster());
        result.put("ds_user_slave01", DBUserDataSourceSlave01());
        result.put("ds_user_slave02", DBUserDataSourceSlave02());
        
        result.put("ds_article_master", DBArticleDataSourceMaster());
        result.put("ds_article_slave01", DBArticleDataSourceSlave01());
        result.put("ds_article_slave02", DBArticleDataSourceSlave02());
        
        return result;
    }
    

  	@Bean(name="shardingDataSourceUserMSHash")
  	@Primary
  	public DataSource shardingDataSource() throws SQLException {
  		ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        
  		//用户表配置，可以添加多个配置
  		shardingRuleConfig.getTableRuleConfigs().add(getUserTableRuleConfiguration());
  		shardingRuleConfig.getTableRuleConfigs().add(getArticleTableRuleConfiguration());
  		
//  		shardingRuleConfig.getBindingTableGroups().add("gps_table, gps_20190227_x, gps_20190227_y");
		shardingRuleConfig.getBindingTableGroups().add("t_user_x, t_article_x");
        
  		//设置数据库策略，传入的是user_id
  		shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("user_id", new DatabaseMSShardingAlgorithm()));
  		//设置数据表策略，传入的是user_id
  		shardingRuleConfig.setDefaultTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("user_id", new TableMSShardingAlgorithm()));

  		shardingRuleConfig.setMasterSlaveRuleConfigs(getMasterSlaveRuleConfigurations());
  		
  		Properties props = new Properties();
  		// 是否显示sql 和 DataSource
  		props.setProperty("sql.show", "true");
  		
        return ShardingDataSourceFactory.createDataSource(createDataSourceMap(), shardingRuleConfig, new ConcurrentHashMap<String, Object>(), props);
  	}
  	
  	
  	/**
     * 需要手动配置事务管理器
     *
     * @param shardingDataSource
     * @return
     */
    @Bean(name="transactitonManagerUserMSHash")
    public DataSourceTransactionManager transactitonManager(DataSource shardingDataSourceMSHash) {
        return new DataSourceTransactionManager(shardingDataSourceMSHash);
    }

    @Bean(name="sqlSessionFactoryUserMSHash")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("shardingDataSourceUserMSHash") DataSource shardingDataSourceMSHash) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(shardingDataSourceMSHash);
        return bean.getObject();
    }

    @Bean(name="testSqlSessionTemplateUserMSHash")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("sqlSessionFactoryUserMSHash") SqlSessionFactory sqlSessionFactoryObjGpsMSHash) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactoryObjGpsMSHash);
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
        druidDataSource.setInitialSize(4);
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
    

  	@Bean
  	public TableRuleConfiguration getUserTableRuleConfiguration() {
  		TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration();
   
  		tableRuleConfig.setLogicTable("t_user_x");
  		// 设置使用sharding-jdbc产生id的列名，如果不用sharding-jdbc产生，则不要设置任何值，否则insert报错
//		tableRuleConfig.setKeyGeneratorColumnName("id");
  		return tableRuleConfig;
  	}

  	@Bean
  	public TableRuleConfiguration getArticleTableRuleConfiguration() {
  		TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration();
   
  		tableRuleConfig.setLogicTable("t_article_x");
  		// 设置使用sharding-jdbc产生id的列名，如果不用sharding-jdbc产生，则不要设置任何值，否则insert报错
//		tableRuleConfig.setKeyGeneratorColumnName("id");
  		return tableRuleConfig;
  	}

	@Bean(name = "db_user_master")
    @ConfigurationProperties(prefix="spring.datasource.user-master")
    public DataSource DBUserDataSourceMaster() {
    	DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
    	return setDataSource(dataSource);
    }
    
    @Bean(name = "db_user_slave01")
    @ConfigurationProperties(prefix="spring.datasource.user-slave01")
    public DataSource DBUserDataSourceSlave01() {
    	DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
    	return setDataSource(dataSource);
    }
    
    @Bean(name = "db_user_slave02")
    @ConfigurationProperties(prefix="spring.datasource.user-slave02")
    public DataSource DBUserDataSourceSlave02() {
    	DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
    	return setDataSource(dataSource);
    }
    

	@Bean(name = "db_article_master")
    @ConfigurationProperties(prefix="spring.datasource.article-master")
    public DataSource DBArticleDataSourceMaster() {
    	DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
    	return setDataSource(dataSource);
    }
    
    @Bean(name = "db_article_slave01")
    @ConfigurationProperties(prefix="spring.datasource.article-slave01")
    public DataSource DBArticleDataSourceSlave01() {
    	DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
    	return setDataSource(dataSource);
    }
    
    @Bean(name = "db_article_slave02")
    @ConfigurationProperties(prefix="spring.datasource.article-slave02")
    public DataSource DBArticleDataSourceSlave02() {
    	DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
    	return setDataSource(dataSource);
    }
}

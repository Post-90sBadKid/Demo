package com.wry.multi.config;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageInterceptor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * 
* @Title: ClusterDataSourceConfig
* @Description: 
*  另一个数据源配置
 */
@Configuration
@MapperScan(basePackages = ClusterDataSourceConfig.PACKAGE, sqlSessionTemplateRef = "clusterSqlSessionTemplate")
public class ClusterDataSourceConfig {

 static final String PACKAGE = "com.wry.multi.dao.cluster";
 static final String MAPPER_LOCATION = "classpath:mapper/cluster/*.xml";

 @Value("${spring.datasource.cluster.jdbc-url}")
 private String url;

 @Value("${spring.datasource.cluster.username}")
 private String username;

 @Value("${spring.datasource.cluster.password}")
 private String password;

 @Value("${spring.datasource.cluster.driver-class-name}")
 private String driverClassName;

 
 @Value("${druid.initialSize}")
 private int initialSize;  
   
 @Value("${druid.minIdle}")
 private int minIdle;  
   
 @Value("${druid.maxActive}")
 private int maxActive;  
   
 @Value("${druid.maxWait}")
 private int maxWait;  
   
 @Value("${druid.timeBetweenEvictionRunsMillis}")
 private int timeBetweenEvictionRunsMillis;  
   
 @Value("${druid.minEvictableIdleTimeMillis}")
 private int minEvictableIdleTimeMillis;  
   
 @Value("${druid.validationQuery}")
 private String validationQuery;  
   
 @Value("${druid.testWhileIdle}")
 private boolean testWhileIdle;  
   
 @Value("${druid.testOnBorrow}")
 private boolean testOnBorrow;  
   
 @Value("${druid.testOnReturn}")
 private boolean testOnReturn;  
   
 @Value("${druid.poolPreparedStatements}")
 private boolean poolPreparedStatements;  
   
 @Value("${druid.maxPoolPreparedStatementPerConnectionSize}")
 private int maxPoolPreparedStatementPerConnectionSize;  
   
 @Value("${druid.filters}")
 private String filters;  
   
 @Value("{druid.connectionProperties}")
 private String connectionProperties;
 
 @Bean(name = "clusterDataSource")
 public DataSource clusterDataSource() {
     DruidDataSource dataSource = new DruidDataSource();
     dataSource.setUrl(url);  
     dataSource.setUsername(username);  
     dataSource.setPassword(password);  
     dataSource.setDriverClassName(driverClassName);
       
     //具体配置 
     dataSource.setInitialSize(initialSize);  
     dataSource.setMinIdle(minIdle);  
     dataSource.setMaxActive(maxActive);  
     dataSource.setMaxWait(maxWait);  
     dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);  
     dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);  
     dataSource.setValidationQuery(validationQuery);  
     dataSource.setTestWhileIdle(testWhileIdle);  
     dataSource.setTestOnBorrow(testOnBorrow);  
     dataSource.setTestOnReturn(testOnReturn);  
     dataSource.setPoolPreparedStatements(poolPreparedStatements);  
     dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);  
     try {  
         dataSource.setFilters(filters);  
     } catch (SQLException e) { 
     	e.printStackTrace();
     }  
     dataSource.setConnectionProperties(connectionProperties);  
     return dataSource;
 }

 @Bean(name = "clusterTransactionManager")
 public DataSourceTransactionManager clusterTransactionManager() {
     return new DataSourceTransactionManager(clusterDataSource());
 }

 @Bean(name = "clusterSqlSessionFactory")
 public SqlSessionFactory clusterSqlSessionFactory(@Qualifier("clusterDataSource") DataSource clusterDataSource)
         throws Exception {
     final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
     sessionFactory.setDataSource(clusterDataSource);
     //分页插件
     Interceptor interceptor = new PageInterceptor();
     Properties properties = new Properties();
     //数据库
     properties.setProperty("helperDialect", "mysql");
     //是否将参数offset作为PageNum使用
     properties.setProperty("offsetAsPageNum", "true");
     //是否进行count查询
     properties.setProperty("rowBoundsWithCount", "true");
     //是否分页合理化
     properties.setProperty("reasonable", "false");
     interceptor.setProperties(properties);
     sessionFactory.setPlugins(new Interceptor[] {interceptor});
     
     
     sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(ClusterDataSourceConfig.MAPPER_LOCATION));
     return sessionFactory.getObject();
 }
    @Bean(name = "clusterSqlSessionTemplate")
    @Primary//自动装配时当出现多个Bean候选者时，被注解为@Primary的Bean将作为首选者，否则将抛出异常
    public SqlSessionTemplate clusterSqlSessionTemplate(
            @Qualifier("clusterSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
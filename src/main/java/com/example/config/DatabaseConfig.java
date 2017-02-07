package com.example.config;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
public class DatabaseConfig {
	  @Value("${com.example.db.driver:com.mysql.jdbc.Driver}")
	  private String driver;

	  @Value("${com.example.db.validation:SELECT 1}")
	  private String validation;

	  @Value("${com.example.db.url:jdbc:mysql://localhost:3306/sovrn}")
	  private String url;

	  @Value("${com.example.db.user:root}")
	  private String user;
	  @Value("${com.example.db.password:my-secret-pw}")
	  private String password;

	  @Getter
	  @Value("${com.example.db.timeout:100}")
	  private long timeout;


	  @Value("${com.example.db.minIdle:2}")
	  private int minIdle=2;
	  @Value("${com.example.db.maxIdle:4}")
	  private int maxIdle=4;
	  @Value("${com.example.db.initialSize:2}")
	  private int initialSize=2;
	  @Value("${com.example.db.maxActive:40}")
	  private int maxActive=20;
	  @Value("${com.example.db.maxWait:10000}")
	  private int maxWait=10000;

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);

	@Bean
	 org.apache.tomcat.jdbc.pool.DataSource getNewDataSource() {
		    LOGGER.info("Creating pool");
		    org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
		    ds.setDriverClassName(driver);
		    ds.setUrl(url);
		    ds.setUsername(user);
		    ds.setPassword(password);
		    ds.setMinIdle(minIdle);
		    ds.setInitialSize(initialSize);
		    ds.setMaxIdle(maxIdle);
		    ds.setMaxActive(maxActive);
		    ds.setValidationQuery(validation);
		    ds.setTimeBetweenEvictionRunsMillis(30000);
		    ds.setMaxWait(maxWait);
		    ds.setMinEvictableIdleTimeMillis(30000);
		    ds.setLogAbandoned(true);
		    ds.setTestWhileIdle(true);
		    ds.setValidationInterval(300000);
		    // Prime the pump
		    try {
		      ds.createPool();
		    } catch (SQLException e) {
		      LOGGER.warn("Unable to connect to database", e);
		    }
		    return ds;
		  }

}

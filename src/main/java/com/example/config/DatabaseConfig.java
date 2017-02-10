package com.example.config;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);

	@Bean
	 org.apache.tomcat.jdbc.pool.DataSource getNewDataSource(DBProperties props) {
		    LOGGER.info("Creating pool");
		    org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
		    ds.setDriverClassName(props.getDriver());
		    ds.setUrl(props.getUrl());
		    ds.setUsername(props.getUser());
		    ds.setPassword(props.getPassword());
		    ds.setMinIdle(props.getMinIdle());
		    ds.setInitialSize(props.getMaxIdle());
		    ds.setMaxIdle(props.getMaxIdle());
		    ds.setMaxActive(props.getMaxActive());
		    ds.setValidationQuery(props.getValidation());
		    ds.setTimeBetweenEvictionRunsMillis(5000);
		    ds.setMaxWait(props.getMaxWait());
		    ds.setMinEvictableIdleTimeMillis(30000);
		    ds.setLogAbandoned(true);
		    ds.setTestWhileIdle(true);
		    ds.setValidationInterval(3000);
		    // Prime the pump
		    try {
		      ds.createPool();
		    } catch (SQLException e) {
		      LOGGER.warn("Unable to connect to database", e);
		    }
		    return ds;
		  }

}

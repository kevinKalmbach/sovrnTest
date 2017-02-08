package com.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
@ConfigurationProperties(prefix="com.example.db")
@Data
public class DBProperties {
	  private String driver = "com.mysql.jdbc.Driver";
	  private String validation = "SELECT 1";
	  private String url = "jdbc:mysql://localhost:3306/sovrn";
	  private String user;
	  private String password;
	  private long timeout=0;
	  private int minIdle=2;
	  private int maxIdle=4;
	  private int initialSize=2;
	  private int maxActive=20;
	  private int maxWait=10000;
}

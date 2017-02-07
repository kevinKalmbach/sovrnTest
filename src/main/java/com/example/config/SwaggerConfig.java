package com.example.config;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

import java.lang.reflect.WildcardType;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Autowired
	private TypeResolver typeResolver;

	@Value("${swagger.base.package}")
	private String basePackage;

	@Value("${swagger.api.title}")
	private String title;

	@Value("${swagger.api.description}")
	private String description;

	@Value("${swagger.api.version}")
	private String version;

	@Value("${swagger.api.termsOfServiceUrl}")
	private String termsOfServiceUrl;

	@Value("${swagger.api.contact}")
	private String contact;

	@Value("${swagger.api.license}")
	private String license;

	@Value("${swagger.api.licenseUrl}")
	private String licenseUrl;

	@Value("${swagger.api.title}")
	private String appName;

	@Bean
	public ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo(title, description, version, termsOfServiceUrl, contact, license, licenseUrl);
		return apiInfo;
	}
	
	public Docket swaggerApi() {
	    return new Docket(DocumentationType.SWAGGER_2)
	        .select()
	          .apis(RequestHandlerSelectors.basePackage(basePackage))
	          .paths(PathSelectors.any())
	          .build()
	        .pathMapping("/")
	        .directModelSubstitute(LocalDate.class, java.sql.Date.class)
	        .directModelSubstitute(LocalDateTime.class, java.util.Date.class)
	        .genericModelSubstitutes(ResponseEntity.class)
	        .alternateTypeRules(
	            newRule(typeResolver.resolve(DeferredResult.class, typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
	                typeResolver.resolve(WildcardType.class)),
	            newRule(typeResolver.resolve(DeferredResult.class, WildcardType.class),
	                typeResolver.resolve(WildcardType.class)),
	            newRule(typeResolver.resolve(ResponseEntity.class, Void.class),
	                typeResolver.resolve(Void.class)))
	        .useDefaultResponseMessages(false)
	        .globalResponseMessage(RequestMethod.GET,
	            newArrayList(new ResponseMessageBuilder()
	                .code(500)
	                .message("Unexpected Error")
	                .responseModel(new ModelRef("Error"))
	                .build()))
	        .globalOperationParameters(
	        newArrayList(new ParameterBuilder()
	            .name("Authorization")
	            .defaultValue("Bearer")
	            .description("Authorization header")
	            .modelRef(new ModelRef("string"))
	            .parameterType("header")
	            .required(false)
	            .build()))
	         .apiInfo(apiInfo());
	  }

}

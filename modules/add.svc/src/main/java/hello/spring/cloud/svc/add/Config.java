package hello.spring.cloud.svc.add;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;


import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@EnableSwagger2
@Configuration
public class Config {
	private static final String SVC_NAME = "add-svc";

	private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES =
			new HashSet<String>(Arrays.asList("application/json"));

	@LoadBalanced
	@Bean
	RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.ant("/api/*/**"))
				.build()
				.apiInfo(apiInfo())
				.consumes(DEFAULT_PRODUCES_AND_CONSUMES)
				.produces(DEFAULT_PRODUCES_AND_CONSUMES);
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"Add Operation Service",
				"Private API for adding operation, for Spring-Cloud PoC Sample purpose only",
				"v1",
				"Terms of service",
				new Contact("Zhenghui Lee", "www.example.com", "leezhenghui@gmail.com"),
				"License of API", "API license URL", Collections.emptyList());
	}

}

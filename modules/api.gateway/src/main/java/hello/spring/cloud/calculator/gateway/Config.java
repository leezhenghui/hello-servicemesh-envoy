package hello.spring.cloud.calculator.gateway;

import hello.spring.cloud.calculator.gateway.filters.ErrLoggerFilter;
import hello.spring.cloud.calculator.gateway.filters.PostLoggerFilter;
import hello.spring.cloud.calculator.gateway.filters.PreLoggerFilter;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {

	private static final String SVC_NAME = "api-gateway";

	@LoadBalanced
	@Bean
	RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Bean
	public PreLoggerFilter preLoggerFilter() {

		return new PreLoggerFilter();
	}

	@Bean
	public PostLoggerFilter postLoggerFilter() {

		return new PostLoggerFilter();
	}

	@Bean
	public ErrLoggerFilter errLoggerFilter() {

		return new ErrLoggerFilter();
	}

	@Bean
	public FallbackProvider defaultFB() {
		return new DefaultFallbackProvider();
	}

}

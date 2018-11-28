package hello.spring.cloud.svc.sub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableDiscoveryClient
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass=true)
@ComponentScan("hello.spring.cloud")
public class SubSvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubSvcApplication.class, args);
	}
}

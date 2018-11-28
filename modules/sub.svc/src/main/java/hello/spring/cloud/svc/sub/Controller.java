package hello.spring.cloud.svc.sub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;

@org.springframework.web.bind.annotation.RestController
@EnableDiscoveryClient
public class Controller {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SubOp subOp;
		
	@GetMapping(value = "/api/v1/execute")
	public int subtract(@QueryParam(value = "l") int l, @QueryParam(value = "r" ) int r) {
		return subOp.action(l,r);
	}

}
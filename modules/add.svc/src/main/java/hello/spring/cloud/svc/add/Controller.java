package hello.spring.cloud.svc.add;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@org.springframework.web.bind.annotation.RestController
@EnableDiscoveryClient
@Api(value = "AddService")
public class Controller {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AddOp addOp;
		
	@RequestMapping(value = "/api/v1/execute", method = RequestMethod.GET)
	@ApiOperation(value = "Add Operator", response = Integer.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully execution"),
		@ApiResponse(code = 401, message = "You are not authorized to execute"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 429, message = "Too Many Requests")
	})
	public int add(@RequestParam(value = "l") @ApiParam(required = true) int l, @RequestParam(value = "r" ) @ApiParam(required = false) int r) {
		return this.addOp.action(l, r);
	}

}
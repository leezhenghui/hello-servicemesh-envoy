package hello.spring.cloud.calculator.gateway.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

public class PreLoggerFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        System.out.println("[DEBUG]: [Enter]: " + String.format("[%s] -> %s", request.getMethod(),request.getRequestURL().toString()));
        String ct = request.getHeader("Content-Type");
        System.out.println("[DEBUG]: >>> Content-Type: " + ct);
        return null;
    }
}

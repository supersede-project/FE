package eu.supersede.fe.multitenant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import eu.supersede.fe.security.DatabaseUser;

public class MultiTenancyInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception 
	{
		String multiTenantId = null;
		String tmp = null;
		
		if(SecurityContextHolder.getContext().getAuthentication() != null &&
			SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null)
		{
			Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if(user instanceof DatabaseUser)
			{
				tmp = ((DatabaseUser)user).getTenantId();
			}
		}
		
		if(tmp != null)
		{
			multiTenantId = tmp;
		}
		else
		{
			multiTenantId = req.getHeader("TenantId");
		}
		
		if(multiTenantId != null)
		{
			req.setAttribute("CURRENT_TENANT_IDENTIFIER", multiTenantId);
		}
		return true;
	}
}
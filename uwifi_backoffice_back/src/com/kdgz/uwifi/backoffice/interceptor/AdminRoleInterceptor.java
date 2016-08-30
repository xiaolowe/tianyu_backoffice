package com.kdgz.uwifi.backoffice.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;

/**
 * 用户权限拦截器
 * @author lanbo
 *
 */
public class AdminRoleInterceptor implements Interceptor{

	@Override
	public void intercept(ActionInvocation ai) {

		Integer rolecode = (Integer) ai.getController()
				.getSessionAttr("roleCode");
		
		if(rolecode == null){
			ai.getController().redirect("/site/index");
		}

		ai.invoke();
	}
}

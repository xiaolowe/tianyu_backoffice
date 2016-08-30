package com.kdgz.uwifi.backoffice.controller;

import java.io.File;
import java.util.ResourceBundle;
import com.jfinal.aop.Before;
import com.jfinal.core.JFinal;
import com.kdgz.uwifi.backoffice.interceptor.ShopInterceptor;
import com.kdgz.uwifi.backoffice.model.Acauth;
import com.kdgz.uwifi.backoffice.model.Authweixinconfig;
import com.kdgz.uwifi.backoffice.model.Multiauthconfig;

@Before(ShopInterceptor.class)
public class AuthStyleController  extends BaseController {

	ResourceBundle bundle = ResourceBundle.getBundle("common");
	
	/**
	 * 认证方式设置
	 */
	public void index(){
		
		int businessid = getSessionAttr("shopid");
		Acauth acauth = Acauth.dao.selectAcauthByAcId(businessid+"");//acid to businessid
		setAttr("businessid", businessid);
		
		setAttr("uploadPath",bundle.getString("uploadPath"));
		
		setAttr("weixinUrl",bundle.getString("weixinUrl").trim());
		
		setAttr("authportalUrl", bundle.getString("authportalUrl").trim());
		

		// 微信认证
		Authweixinconfig authweixinconfig = Authweixinconfig.dao.selectAuthweixinByBusinessId(businessid+"");
		
		//多认证方式
		Multiauthconfig multiauthconfig = Multiauthconfig.dao.selectMultiauthBybusId(businessid+"");
		setAttr("acauth", acauth);
		
		//微信认证
		setAttr("authweixinconfig", authweixinconfig);
		
		//多认证方式
		setAttr("multiauthconfig", multiauthconfig);
		
		render("/admin/shop_manage/AuthenticateStyle/AuthenticatesStyle.html");
		
	}
	
	public void download(){
		File file = new File(JFinal.me().getServletContext().getRealPath("/")+"download/wxrzpzsc/微信认证配置帮助文档.doc");

		if(file.exists()) {

			renderFile(file);

			return ;

		} 
		index();
	}
}

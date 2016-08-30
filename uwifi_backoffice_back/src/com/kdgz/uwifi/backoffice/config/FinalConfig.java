package com.kdgz.uwifi.backoffice.config;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import com.kdgz.uwifi.backoffice.controller.AcconfigController;
import com.kdgz.uwifi.backoffice.controller.AccountController;
import com.kdgz.uwifi.backoffice.controller.AcinfoController;
import com.kdgz.uwifi.backoffice.controller.ActivityController;
import com.kdgz.uwifi.backoffice.controller.AppdownloadController;
import com.kdgz.uwifi.backoffice.controller.ApplyController;
import com.kdgz.uwifi.backoffice.controller.AuthStyleController;
import com.kdgz.uwifi.backoffice.controller.BindAppController;
import com.kdgz.uwifi.backoffice.controller.BusinessinfoController;
import com.kdgz.uwifi.backoffice.controller.BwlistController;
import com.kdgz.uwifi.backoffice.controller.ClassifyController;
import com.kdgz.uwifi.backoffice.controller.ContentController;
import com.kdgz.uwifi.backoffice.controller.DashboardController;
import com.kdgz.uwifi.backoffice.controller.DataAnalysisController;
import com.kdgz.uwifi.backoffice.controller.DownloadAccountController;
import com.kdgz.uwifi.backoffice.controller.FirmwareController;
import com.kdgz.uwifi.backoffice.controller.MarketingActivity;
import com.kdgz.uwifi.backoffice.controller.MemberController;
import com.kdgz.uwifi.backoffice.controller.ModuleController;
import com.kdgz.uwifi.backoffice.controller.MyRouterController;
import com.kdgz.uwifi.backoffice.controller.NavigateMenuController;
import com.kdgz.uwifi.backoffice.controller.SiteController;
import com.kdgz.uwifi.backoffice.controller.SitecarouselController;
import com.kdgz.uwifi.backoffice.controller.SiteflashController;
import com.kdgz.uwifi.backoffice.controller.SystemUserController;
import com.kdgz.uwifi.backoffice.controller.TempletController;
import com.kdgz.uwifi.backoffice.controller.TempletDescripController;
import com.kdgz.uwifi.backoffice.controller.TempletFlashController;
import com.kdgz.uwifi.backoffice.controller.UploadapkController;
import com.kdgz.uwifi.backoffice.model.Acauth;
import com.kdgz.uwifi.backoffice.model.Acbrand;
import com.kdgz.uwifi.backoffice.model.Acconfig;
import com.kdgz.uwifi.backoffice.model.Acinfo;
import com.kdgz.uwifi.backoffice.model.Acstatetemp;
import com.kdgz.uwifi.backoffice.model.Actypeinfo;
import com.kdgz.uwifi.backoffice.model.Apply;
import com.kdgz.uwifi.backoffice.model.Areainfo;
import com.kdgz.uwifi.backoffice.model.Authsmstemple;
import com.kdgz.uwifi.backoffice.model.Authweixinconfig;
import com.kdgz.uwifi.backoffice.model.Businessapp;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.model.Businesstemplet;
import com.kdgz.uwifi.backoffice.model.Bwlist;
import com.kdgz.uwifi.backoffice.model.Construtinfo;
import com.kdgz.uwifi.backoffice.model.Cuser;
import com.kdgz.uwifi.backoffice.model.Firmware;
import com.kdgz.uwifi.backoffice.model.Lottery;
import com.kdgz.uwifi.backoffice.model.Lotteryprize;
import com.kdgz.uwifi.backoffice.model.Menuinfo;
import com.kdgz.uwifi.backoffice.model.Multiauthconfig;
import com.kdgz.uwifi.backoffice.model.Roleinfo;
import com.kdgz.uwifi.backoffice.model.Rolemenu;
import com.kdgz.uwifi.backoffice.model.Sitecatemenu;
import com.kdgz.uwifi.backoffice.model.Siteclassify;
import com.kdgz.uwifi.backoffice.model.Sitecontent;
import com.kdgz.uwifi.backoffice.model.Siteflash;
import com.kdgz.uwifi.backoffice.model.Siteinfo;
import com.kdgz.uwifi.backoffice.model.Smsacconfig;
import com.kdgz.uwifi.backoffice.model.Syscode;
import com.kdgz.uwifi.backoffice.model.Templetinfo;
import com.kdgz.uwifi.backoffice.model.Templetpage;
import com.kdgz.uwifi.backoffice.model.Userbiz;
import com.kdgz.uwifi.backoffice.model.Userinfo;
import com.kdgz.uwifi.backoffice.model.Userrole;

/**
 * JFinal配置
 * 
 * @author lanbo
 * 
 */
public class FinalConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants me) {

		loadPropertyFile("jdbc.properties");
		me.setDevMode(true);
		me.setViewType(ViewType.FREE_MARKER);
		me.setUploadedFileSaveDirectory("upload");
	}

	@Override
	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("CPATH"));
	}
	@Override
	public void configInterceptor(Interceptors me) {
	}

	@Override
	public void configPlugin(Plugins me) {
		DruidPlugin cp = new DruidPlugin(getProperty("jdbcUrl"),
				getProperty("user"), getProperty("password"));
		me.add(cp);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(cp);
		me.add(arp);
		arp.addMapping("userinfo", Userinfo.class);
		arp.addMapping("acinfo", Acinfo.class);
		arp.addMapping("businessinfo", Businessinfo.class);
		arp.addMapping("acbrand", Acbrand.class);
		arp.addMapping("actypeinfo", Actypeinfo.class);
		arp.addMapping("construtinfo", Construtinfo.class);
		arp.addMapping("acconfig", Acconfig.class);
		arp.addMapping("bwlist", Bwlist.class);
		arp.addMapping("acstatetemp", Acstatetemp.class);
		arp.addMapping("templetinfo", Templetinfo.class);
		arp.addMapping("businesstemplet", Businesstemplet.class);
		arp.addMapping("templetpage", Templetpage.class);
		arp.addMapping("acauth", Acauth.class);
		arp.addMapping("authsmstemple", Authsmstemple.class);
		arp.addMapping("authweixinconfig", Authweixinconfig.class);
		arp.addMapping("smsacconfig", Smsacconfig.class);
		arp.addMapping("roleinfo", Roleinfo.class);
		arp.addMapping("rolemenu", Rolemenu.class);
		arp.addMapping("userrole", Userrole.class);
		arp.addMapping("menuinfo", Menuinfo.class);
		arp.addMapping("userbiz", Userbiz.class);
		arp.addMapping("multiauthconfig", Multiauthconfig.class);
		arp.addMapping("siteclassify", Siteclassify.class);
		arp.addMapping("sitecontent", Sitecontent.class);
		arp.addMapping("siteinfo", Siteinfo.class);
		arp.addMapping("siteflash", Siteflash.class);
		arp.addMapping("sitecatemenu", Sitecatemenu.class);
		arp.addMapping("syscode", Syscode.class);
		arp.addMapping("cuser", Cuser.class);
		arp.addMapping("lottery", Lottery.class);
		arp.addMapping("lotteryprize", Lotteryprize.class);
		arp.addMapping("firmware", Firmware.class);
		arp.addMapping("apply", Apply.class);
		arp.addMapping("businessapp", Businessapp.class);
		arp.addMapping("areainfo", Areainfo.class);
		
		// EH Cache
		me.add(new EhCachePlugin());
		
	}

	/**
	 * 路由配置
	 */
	@Override
	public void configRoute(Routes me) {
		me.add("/", SiteController.class);
		me.add("/site", SiteController.class);
		me.add("/dashboard", DashboardController.class);	
		me.add("/account", AccountController.class);
		me.add("/sysUser", SystemUserController.class);
		me.add("/businessinfo", BusinessinfoController.class);
		me.add("/myRouter", MyRouterController.class);
		me.add("/acinfo", AcinfoController.class);
		me.add("/acconfig", AcconfigController.class);
		me.add("/bwlist", BwlistController.class);
		me.add("/module", ModuleController.class);
		me.add("/activity", ActivityController.class);
		me.add("/classify", ClassifyController.class);
		me.add("/content", ContentController.class);
		me.add("/siteflash", SiteflashController.class);
		me.add("/sitecarousel", SitecarouselController.class);
		me.add("/navigatemenu", NavigateMenuController.class);
		me.add("/dataAnalysis", DataAnalysisController.class);
		me.add("/templetManage", TempletController.class);
		me.add("/templetFlashManage", TempletFlashController.class);
		me.add("/marketingActivity", MarketingActivity.class);
		me.add("/member", MemberController.class);
		me.add("/authStyle", AuthStyleController.class);
		me.add("/firmWare", FirmwareController.class);
		me.add("/apply", ApplyController.class);
		me.add("/bindApp", BindAppController.class);
		me.add("/downloadShow", DownloadAccountController.class);
		me.add("/appdownload", AppdownloadController.class);
		me.add("/uploadapk", UploadapkController.class);
		me.add("/templetdescrip", TempletDescripController.class);


	}

}

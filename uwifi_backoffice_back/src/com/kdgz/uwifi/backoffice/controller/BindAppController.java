package com.kdgz.uwifi.backoffice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.LayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Apply;
import com.kdgz.uwifi.backoffice.model.Businessapp;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.util.StringUtil;

@Before(LayoutInterceptor.class)
public class BindAppController extends BaseController {
	ResourceBundle bundle = ResourceBundle.getBundle("common");
	
	public void index() {
		
		String searchType = getPara("searchType");
		
		boolean isHomeActive = true;
		if (StringUtils.isBlank(searchType)) {
			setAttr("searchType", "");
			if (getSessionAttr("isHomeActive") != null) {
				isHomeActive = getSessionAttr("isHomeActive");
				setSessionAttr("isHomeActive", null);
			}
		} else {
			if ("2".equals(searchType)) {
				isHomeActive = false;
			}
			setAttr("searchType", searchType);
		}
		
		setAttr("isHomeActive", isHomeActive);
		
		String businessidStr = getPara("businessid", "");
		Integer businessid = -1;
		
		int allApplyPage = 1;
		int bindedApplyPage = 1;
		if(businessidStr.indexOf("@") > -1){
			String[] args = businessidStr.split("@");
			businessid = Integer.parseInt(args[0]);
			if("unbind".equals(args[1])){
				allApplyPage = Integer.parseInt(args[2].replace("-", ""));
				
			}else{
				bindedApplyPage = Integer.parseInt(args[2].replace("-", ""));
			}
		}else{
			String methodflage = getPara("lableFlage","");
			if (methodflage.length() > 0) {
				bindedApplyPage = getParaToInt(0,1);
				isHomeActive = false;
			}else{
				allApplyPage = getParaToInt("curPage1",1);
			}
			businessid = Integer.parseInt(businessidStr);
		}
		
		Businessinfo businessinfo = Businessinfo.dao.findById(businessid);
		setAttr("businessinfo", businessinfo);
		
		//往关联表里插记录
		List<Record> businessappList = Db.find("select * from businessapp where businessid = "+businessid);
		List<Apply> idList = Apply.dao.getAppIdList();
		if(businessappList.size() == 0){//关联表中未添加过
			for(Apply obj : idList){
				Businessapp businessapp = new Businessapp();
				businessapp.set("businessid", businessid);
				businessapp.set("appid", obj.getInt("id"));
				businessapp.save();
			}
		}else{
			//判断是否还有应用没添加到关联表的
			List<String> list1 = new ArrayList<String>();
			List<String> list2 = new ArrayList<String>();
			for(Apply obj : idList){
				list2.add(obj.getInt("id").toString());
			}
			for(Record obj : businessappList){
				list1.add(obj.getInt("appid").toString());
			}
			List<String> tempList = StringUtil.getDiffrent(list1, list2);
			for(String obj : tempList){
				Businessapp businessapp = new Businessapp();
				businessapp.set("businessid", businessid);
				businessapp.set("appid", obj);
				businessapp.save();
			}
		}
		setAttr("ApplicationList", Apply.dao.getAppListByPage(allApplyPage, 10,businessid));
		setAttr("bindedApplyList", Apply.dao.getBindedAppListByPage(bindedApplyPage, 10, businessid));
		
		setAttr("uploadPath",bundle.getString("uploadPath"));
		
		flashRender("/admin/bind_app/bindapp_list.html");
	}
	
	public void bindCancel(){
		String appid = getPara("appid");
		String busid = getPara("busid");
		
		String sql = "update businessapp set bindflag = 0 where businessid = ? and appid = ? ";
		Db.update(sql, busid, appid);
		
		Data rst = new Data();
		rst.setStatus(Constants.OPERATION_SUCCEED);
		rst.setMsg("取消绑定成功！");
		
		setFlashData(rst);
		
		int currentPage = getParaToInt("pageNo",1);//当前页码
		setAttr("currentPage",currentPage);//设置当前页码
		
		redirect("/bindApp?curPage1="+currentPage+"&businessid="+busid);
		//renderText("0");
	}
	
	public void addApp(){
		String appid = getPara("appid");
		String busid = getPara("busid");
		String sql = "update businessapp set bindflag = 1 where businessid = ? and appid = ? ";
		Db.update(sql, busid, appid);
		
		Data rst = new Data();
		rst.setStatus(Constants.OPERATION_SUCCEED);
		rst.setMsg("绑定应用成功！");
		
		setFlashData(rst);
		
		int currentPage = getParaToInt("pageNo",1);//当前页码
		setAttr("currentPage",currentPage);//设置当前页码
		
		redirect("/bindApp?curPage1="+currentPage+"&businessid="+busid);
		//renderText("0");
	}
	
	public void bindCancel2(){
		String appid = getPara("appid");
		String busid = getPara("busid");
		
		String sql = "update businessapp set bindflag = 0 where businessid = ? and appid = ? ";
		Db.update(sql, busid, appid);
		
		Data rst = new Data();
		rst.setStatus(Constants.OPERATION_SUCCEED);
		rst.setMsg("取消绑定成功！");
		
		setFlashData(rst);
		
		int currentPage = getParaToInt("pageNo",1);//当前页码
		setAttr("currentPage",currentPage);//设置当前页码
		
		redirect("/bindApp/"+currentPage+"?lableFlage=binded&businessid="+busid);
	}

}

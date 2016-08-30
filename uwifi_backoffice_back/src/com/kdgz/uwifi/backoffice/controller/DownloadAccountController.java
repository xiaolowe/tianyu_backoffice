package com.kdgz.uwifi.backoffice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.bean.WSResult;
import com.kdgz.uwifi.backoffice.interceptor.LayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Apply;
import com.kdgz.uwifi.backoffice.util.DateUtil;
import com.kdgz.uwifi.backoffice.util.StringUtil;
import com.kdgz.uwifi.backoffice.util.WebServiceUtil;

@Before(LayoutInterceptor.class)
public class DownloadAccountController extends Controller {

	private static final String DATASERVICE_URI = "http://10.0.36.6:10003/CTYCloudDataService/";
	
	private static final String APPDOWNLOAD = "download_appcount";//APP下载量接口
	
	public void mobileAssistant(){
		
		String tempArgs = getPara("tempArgs","");
		if (getRequest().getMethod().equals("POST")) {
			Integer bussinessidStr = getParaToInt("bussinessidStr",-1);
			if("twoDays".equals(tempArgs)){
				
				WSResult azzsDownload = WebServiceUtil.getDataByWS_tianyu(DATASERVICE_URI+APPDOWNLOAD,String.valueOf(bussinessidStr),"010","day","0");
				get2daysData(azzsDownload, "ajaxSearch");
			}
			if("sevenDays".equals(tempArgs)){
				WSResult azzsDownload7 = WebServiceUtil.getDataByWS_tianyu(DATASERVICE_URI+APPDOWNLOAD,String.valueOf(bussinessidStr),"010","week","0");
				getSevenData(azzsDownload7, "手机助手");
			}
			if("oneMonth".equals(tempArgs)){
				
				WSResult azzsDownloadMonth = WebServiceUtil.getDataByWS_tianyu(DATASERVICE_URI+APPDOWNLOAD,String.valueOf(bussinessidStr),"010","month","0");
				String[] x_array;
				String xCategories="[]";
				StringBuffer jsonStr = new StringBuffer("[");
				if(null != azzsDownloadMonth){
					if("SUCCESSED".equals(azzsDownloadMonth.getMessage())){
						LinkedHashMap<String, String> linedataMonthList = (LinkedHashMap<String, String>)azzsDownloadMonth.getData();
						int listSize = linedataMonthList.size();
						
						if(listSize > 0){
							
							int sevenDataSize = linedataMonthList.size();
							LinkedHashMap<String, String> mapSeven = linedataMonthList;
							
							String[] seriesTemp = new String[sevenDataSize];
							
							x_array = new String[sevenDataSize];
							
							int i = -1;
							Iterator iter = mapSeven.entrySet().iterator();
							while (iter.hasNext()) {
								Map.Entry entry = (Map.Entry) iter.next();
								Object key = entry.getKey();
								Object val = entry.getValue();
								i++;
								x_array[i] = (String)key;
								seriesTemp[i] = (String)val;
							}
							
							jsonStr.append("{name:'手机助手',");
							jsonStr.append("data:").append(StringUtil.getStrsplit2(seriesTemp)).append("}");
							
							xCategories = StringUtil.getStrsplit(x_array);
						}
						
					}
				}else{
					x_array = new String[30];
					for(int i=29; i >=0 ; i--){
						x_array[i]=DateUtil.getday_yymmdd(new Date(),(-(i+1)+1));
					}
					
					java.util.List<String> list=java.util.Arrays.asList(x_array);
					java.util.Collections.reverse(list);
					  
					xCategories = StringUtil.getStrsplit(list.toArray(x_array));
					
					jsonStr.append("{name:'").append("手机助手").append("',");
					jsonStr.append("data:").append("[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]").append("}");
				}
				//setAttr("x_array", xCategories);
				jsonStr.append("]");
				//setAttr("seriesData", jsonStr.toString());
				Data retMsg = new Data();
				retMsg.setStatus(xCategories);
				retMsg.setMsg(jsonStr.toString());
				renderJson(retMsg);
				
			}
		}else{
			Integer bussinessidStr = getParaToInt(0);
			if("".equals(tempArgs)){
				
				WSResult azzsDownload = WebServiceUtil.getDataByWS_tianyu(DATASERVICE_URI+APPDOWNLOAD,String.valueOf(bussinessidStr),"010","day","0");
				get2daysData(azzsDownload,"");
			}
			setAttr("tempArgs", tempArgs);
			setAttr("bussinessidStr", bussinessidStr);
			render("/admin/downloadamount/mobileAssistantN.html");
		}
		
	}

	private void getSevenData(WSResult azzsDownload7, String name) {
		String[] x_array;
		String xCategories="[]";
		StringBuffer jsonStr = new StringBuffer("[");
		if(null != azzsDownload7){
			if("SUCCESSED".equals(azzsDownload7.getMessage())){
				LinkedHashMap<String, String> linedata7List = (LinkedHashMap<String, String>)azzsDownload7.getData();
				int listSize = linedata7List.size();
				
				if(listSize > 0){
					
					int sevenDataSize = linedata7List.size();
					LinkedHashMap<String, String> mapSeven = linedata7List;
					
					String[] seriesTemp = new String[sevenDataSize];
					
					x_array = new String[sevenDataSize];
					
					int i = -1;
					Iterator iter = mapSeven.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						Object key = entry.getKey();
						Object val = entry.getValue();
						i++;
						x_array[i] = (String)key;
						seriesTemp[i] = (String)val;
					}
					
					jsonStr.append("{name:'"+name+"',");
					jsonStr.append("data:").append(StringUtil.getStrsplit2(seriesTemp)).append("}");
					
					xCategories = StringUtil.getStrsplit(x_array);
				}
				
			}
		}else{
			x_array = new String[7];
			for(int i=6; i >=0 ; i--){
				x_array[i]=DateUtil.getday_yymmdd(new Date(),(-(i+1)+1));
			}
			
			java.util.List<String> list=java.util.Arrays.asList(x_array);
			java.util.Collections.reverse(list);
			  
			xCategories = StringUtil.getStrsplit(list.toArray(x_array));
			
			jsonStr.append("{name:'").append(name).append("',");
			jsonStr.append("data:").append("[0,0,0,0,0,0,0]").append("}");
			
		}
		//setAttr("x_array", xCategories);
		jsonStr.append("]");
		//setAttr("seriesData", jsonStr.toString());
		
		Data retMsg = new Data();
		retMsg.setStatus(xCategories);
		retMsg.setMsg(jsonStr.toString());
		renderJson(retMsg);
	}

	private void get2daysData(WSResult appDownload, String isAjax) {
		String yestoday = DateUtil.getday_yymmdd(new Date(),-1);
		String today = DateUtil.getday_yymmdd(new Date(),0);
		
		String xCategories="[]";
		String[] x_array = {};
		StringBuffer jsonStr = new StringBuffer("[");
		if(null != appDownload){
			if("SUCCESSED".equals(appDownload.getMessage())){
				ArrayList<LinkedHashMap<String, String>> linedataList = (ArrayList<LinkedHashMap<String, String>>)appDownload.getData();
				int listSize = linedataList.size();
				
				if(listSize > 0){
					
					int ydataSize = linedataList.get(0).size();
					int tdataSize = linedataList.get(1).size();
					
					LinkedHashMap<String, String> map0 = linedataList.get(0);
					LinkedHashMap<String, String> map1 = linedataList.get(1);
					
					String[] series1Temp = new String[ydataSize];
					String[] series2Temp = new String[tdataSize];
					
					x_array = new String[ydataSize];
					int i = -1;
					Iterator iter = map0.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						Object key = entry.getKey();
						Object val = entry.getValue();
						i++;
						x_array[i] = (String)key;
						series1Temp[i] = (String)val;
					} 
					int j = -1;
					Iterator iter1 = map1.entrySet().iterator();
					while (iter1.hasNext()) {
						Map.Entry entry = (Map.Entry) iter1.next();
						Object key = entry.getKey();
						Object val = entry.getValue();
						j++;
						series2Temp[j] = (String)val;
					} 
					
					jsonStr.append("{name:'"+yestoday+"',");
					jsonStr.append("data:").append(StringUtil.getStrsplit2(series1Temp)).append("}");
					jsonStr.append(",{name:'"+today+"',");
					jsonStr.append("data:").append(StringUtil.getStrsplit2(series2Temp)).append("}");
					
					xCategories = StringUtil.getStrsplit(x_array);
					
				}
			}else{
				x_array = new String[24];
				for(int i=0; i < x_array.length; i++){
					x_array[i]=i+":00";
				}
				xCategories = StringUtil.getStrsplit(x_array);
				jsonStr.append("{name:'"+yestoday+"',");
				jsonStr.append("data:").append("[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]").append("}");
				jsonStr.append(",{name:'"+today+"',");
				jsonStr.append("data:").append("[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]").append("}");
				
			}
		}else{
			x_array = new String[24];
			for(int i=0; i < x_array.length; i++){
				x_array[i]=i+":00";
			}
			xCategories = StringUtil.getStrsplit(x_array);
			jsonStr.append("{name:'"+yestoday+"',");
			jsonStr.append("data:").append("[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]").append("}");
			jsonStr.append(",{name:'"+today+"',");
			jsonStr.append("data:").append("[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]").append("}");
			
		}
		jsonStr.append("]");
		if(isAjax.length() > 0){
			Data retMsg = new Data();
			retMsg.setStatus(xCategories);
			retMsg.setMsg(jsonStr.toString());
			renderJson(retMsg);
		}else{
			setAttr("x_array", xCategories);
			setAttr("seriesData", jsonStr.toString());
		}
	}
	
	
	public void appDownloadAccount(){
		
		Integer appid = getParaToInt("appid", -1);
		Apply apply = Apply.dao.findById(appid);
		setAttr("apply", apply);
		String tempArgs = getPara("tempArgs","");
		
		if (getRequest().getMethod().equals("POST")) {
			String bussinessidStr = getPara("bussinessidStr","");
			if("twoDays".equals(tempArgs)){
				WSResult appDownload = WebServiceUtil.getDataByWS_tianyu(DATASERVICE_URI+APPDOWNLOAD,bussinessidStr,"010","day",String.valueOf(appid));
				get2daysData(appDownload,"ajaxSearch");
			}
			if("sevenDays".equals(tempArgs)){
				WSResult azzsDownload7 = WebServiceUtil.getDataByWS_tianyu(DATASERVICE_URI+APPDOWNLOAD,bussinessidStr,"010","week",String.valueOf(appid));
				getSevenData(azzsDownload7, apply.getStr("appname"));
			}
			if("oneMonth".equals(tempArgs)){
				WSResult azzsDownloadMonth = WebServiceUtil.getDataByWS_tianyu(DATASERVICE_URI+APPDOWNLOAD,bussinessidStr,"010","month",String.valueOf(appid));
				
				String[] x_array;
				String xCategories="[]";
				StringBuffer jsonStr = new StringBuffer("[");
				if(null != azzsDownloadMonth){
					if("SUCCESSED".equals(azzsDownloadMonth.getMessage())){
						LinkedHashMap<String, String> linedataMonthList = (LinkedHashMap<String, String>)azzsDownloadMonth.getData();
						int listSize = linedataMonthList.size();
						
						if(listSize > 0){
							
							int sevenDataSize = linedataMonthList.size();
							LinkedHashMap<String, String> mapSeven = linedataMonthList;
							
							String[] seriesTemp = new String[sevenDataSize];
							
							x_array = new String[sevenDataSize];
							
							int i = -1;
							Iterator iter = mapSeven.entrySet().iterator();
							while (iter.hasNext()) {
								Map.Entry entry = (Map.Entry) iter.next();
								Object key = entry.getKey();
								Object val = entry.getValue();
								i++;
								x_array[i] = (String)key;
								seriesTemp[i] = (String)val;
							}
							
							jsonStr.append("{name:'"+apply.getStr("appname")+"',");
							jsonStr.append("data:").append(StringUtil.getStrsplit2(seriesTemp)).append("}");
							
							xCategories = StringUtil.getStrsplit(x_array);
						}
						
					}
				}else{
					x_array = new String[30];
					for(int i=29; i >=0 ; i--){
						x_array[i]=DateUtil.getday_yymmdd(new Date(),(-(i+1)+1));
					}
					
					java.util.List<String> list=java.util.Arrays.asList(x_array);
					java.util.Collections.reverse(list);
					  
					xCategories = StringUtil.getStrsplit(list.toArray(x_array));
					
					jsonStr.append("{name:'").append(apply.getStr("appname")).append("',");
					jsonStr.append("data:").append("[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]").append("}");
				}
				//setAttr("x_array", xCategories);
				jsonStr.append("]");
				//setAttr("seriesData", jsonStr.toString());
				Data retMsg = new Data();
				retMsg.setStatus(xCategories);
				retMsg.setMsg(jsonStr.toString());
				renderJson(retMsg);
				
			}
		} else {
			String bussinessidStr = getPara("busid","");
			if("".equals(tempArgs)){
				
				WSResult appDownload = WebServiceUtil.getDataByWS_tianyu(DATASERVICE_URI+APPDOWNLOAD,bussinessidStr,"010","day","1");
				get2daysData(appDownload,"");
			}
			setAttr("tempArgs", tempArgs);
			setAttr("bussinessidStr", bussinessidStr);
			render("/admin/downloadamount/appDownloadAccountN.html");
		}
		
	}
}

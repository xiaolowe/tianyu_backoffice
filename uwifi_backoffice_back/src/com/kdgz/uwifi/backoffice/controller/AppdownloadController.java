package com.kdgz.uwifi.backoffice.controller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.jfinal.aop.Before;
import com.jfinal.core.JFinal;
import com.kdgz.uwifi.backoffice.bean.AppDownloadCount;
import com.kdgz.uwifi.backoffice.bean.WSResult;
import com.kdgz.uwifi.backoffice.interceptor.LayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Apply;
import com.kdgz.uwifi.backoffice.util.WebServiceUtil;

@Before(LayoutInterceptor.class)
public class AppdownloadController extends BaseController {
	
	private static final String DATASERVICE_URI = "http://10.0.36.6:10003/CTYCloudDataService/";
	
	
	// APP下载量统计
	private static final String APP_DOWNLOAD_COUNT = "download_appchannelcount";
	

	public void index(){
		
		String bussinessidStr = getSessionAttr("businessids");
		
		String start = getPara("start", "");
		String end = getPara("end", "");

		Date now = new Date();
		if (StringUtils.isEmpty(start)) {
			Date lastSevenDate = DateUtils.addDays(now, -6);
			start = DateFormatUtils.ISO_DATE_FORMAT.format(lastSevenDate);
		}
		if (StringUtils.isEmpty(end)) {
			end = DateFormatUtils.ISO_DATE_FORMAT.format(now);
		}
		setAttr("start", start);
		setAttr("end", end);
		setAttr("bussinessidStr", bussinessidStr);
		
		WSResult totalUser = WebServiceUtil.getAppCountDataByWS(DATASERVICE_URI+APP_DOWNLOAD_COUNT,start,end,bussinessidStr,"");
		if(null != totalUser){
			if("SUCCESSED".equals(totalUser.getMessage())){
				ArrayList<LinkedHashMap<String, String>> listAppcount = (ArrayList<LinkedHashMap<String, String>>)totalUser.getData();
				if(listAppcount != null && listAppcount.size()>0){
					for(int i = 0; i < listAppcount.size(); i++){
						LinkedHashMap<String, String> app = listAppcount.get(i);
						String appid = app.get("appid");
						Apply apply = Apply.dao.findById(appid);
						if(apply != null && !appid.equals("0") ){
							app.put("appname", apply.getStr("appname"));
						}
						if(appid.equals("0")){
							app.put("appname", "手机助手");
						}
						
					}
					setAttr("listAppcount", listAppcount);
				}
			}
		}
		
		flashRender("/admin/apply/apply_download.html");
	}
	
	
	public String expAppDownloadCount(String businessId, String starttime, String endtime){
		String mdPath = ""; //导出文件
		String baseDir = JFinal.me().getServletContext().getRealPath("/"); // 得到虚拟目录的绝对路径
		try {
			//先删除之前的临时excel文件
			this.delExcel();
			
			//读取模板excel
			String mbPath = baseDir + "exp/mb.xls";
			InputStream is = new FileInputStream(new File(mbPath));
			HSSFWorkbook wb = new HSSFWorkbook(is);
			//处理第一张sheet
//			if(businessId.length() == 0){
//			}
			// 获取设备运营状态详情
			WSResult totalUser = WebServiceUtil.getAppCountDataByWS(DATASERVICE_URI+APP_DOWNLOAD_COUNT,starttime,endtime,businessId,"");
			ArrayList<LinkedHashMap<String, String>> listAppcount = null;
			if(null != totalUser){
				if("SUCCESSED".equals(totalUser.getMessage())){
				    listAppcount = (ArrayList<LinkedHashMap<String, String>>)totalUser.getData();
					if(listAppcount != null && listAppcount.size()>0){
						for(int i = 0; i < listAppcount.size(); i++){
							LinkedHashMap<String, String> app = listAppcount.get(i);
							String appid = app.get("appid");
							Apply apply = Apply.dao.findById(appid);
							if(apply != null && !appid.equals("0") ){
								app.put("appname", apply.getStr("appname"));
							}
							if(appid.equals("0")){
								app.put("appname", "手机助手");
							}
							
						}
					}
				}
			}
			List<AppDownloadCount> appDownList = new ArrayList<AppDownloadCount>();
			if(listAppcount != null){
				for (Map<String, String> status : listAppcount) {
					AppDownloadCount detail = new AppDownloadCount();
					detail.setAppname(status.get("appname"));
					detail.setAssistdownload(Integer.valueOf(status.get("assistdownload")));
					
					appDownList.add(detail);
				}
			}
			
			exportAppdownload(wb,appDownList);
			// 设置目标文件路径
			String uuid = UUID.randomUUID().toString();
			mdPath = baseDir + "exp/temp/" + uuid + ".xls";
			FileOutputStream os = new FileOutputStream(mdPath);
			wb.write(os);
			os.close();
		} catch (FileNotFoundException e) {
			mdPath = "-1";
		} catch (IOException e) {
			mdPath = "-1";
		} catch (Exception e) {
			mdPath = "-1";
			e.printStackTrace();
		}
		
		return mdPath;
	}
	
	
	
	private void exportAppdownload(HSSFWorkbook wb, List<AppDownloadCount> beanList) {
		HSSFRow row;
		HSSFCell cell;
		HSSFSheet sheet = wb.getSheetAt(0);
		
		//处理表头
		//row = sheet.getRow(1);
		row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("应用名");
		cell = row.createCell(1);
		cell.setCellValue("下载量");
		
		for (int i = 0; i < beanList.size(); i++) {
			row = sheet.createRow(i+1);
			cell = row.createCell(0);
			cell.setCellValue(beanList.get(i).getAppname()==null?"未知":beanList.get(i).getAppname());
			cell = row.createCell(1);
			cell.setCellValue(beanList.get(i).getAssistdownload());
		}
	}
	
	
	/**
	 * 删除导出时产生的临时excel文件
	 */
	public String delExcel() {
		try {
			String baseDir = JFinal.me().getServletContext().getRealPath("/");
			String path = baseDir + "exp/temp";
			delAllFile(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	//删除指定文件夹下所有文件
		//param path 文件夹完整绝对路径
		public static boolean delAllFile(String path) {
			boolean flag = true;
			File file = new File(path);
			if (!file.exists()) {
				flag = false;
				return flag;
			}
			if (!file.isDirectory()) {
				flag = false;
				return flag;
			}
			String[] tempList = file.list();
			File temp = null;
			for (int i = 0; i < tempList.length; i++) {
				if (path.endsWith(File.separator)) {
					temp = new File(path + tempList[i]);
				} else {
					temp = new File(path + File.separator + tempList[i]);
				}
				if (temp.isFile()) {
					temp.delete();
				}
				if (temp.isDirectory()) {
					delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
					delFolder(path + "/" + tempList[i]);//再删除空文件夹
				}
			}
			return flag;
	  }
		
	
		//删除文件夹
		//param folderPath 文件夹完整绝对路径
		public static void delFolder(String folderPath) {
			try {
				delAllFile(folderPath); //删除完里面所有内容
				String filePath = folderPath;
				filePath = filePath.toString();
				java.io.File myFilePath = new java.io.File(filePath);
			 	myFilePath.delete(); //删除空文件夹
			} catch (Exception e) {
				e.printStackTrace(); 
			}
		}
	
	
	
	
}

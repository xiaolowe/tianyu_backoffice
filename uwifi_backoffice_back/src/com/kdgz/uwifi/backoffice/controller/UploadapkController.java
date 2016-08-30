package com.kdgz.uwifi.backoffice.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.LayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.util.FileUtil;
import com.kdgz.uwifi.backoffice.util.StringUtil;
import com.kdgz.uwifi.backoffice.util.ZipUtil;

/**
 * 店铺模块
 * 
 * @author Lanbo
 * 
 */
@Before(LayoutInterceptor.class)
public class UploadapkController extends BaseController {

	/**
	 * 列表
	 */
	public void index() {

	}

	public void uploadBaseapp(){
		
		if (getRequest().getMethod().equals("POST")) {
			
			String path_tmp = "";    
			String real_path = "";
			String fileNamebase = "";
			String  apkurl = "";
			String uploadDir = File.separator + "apkmodel" + File.separator ;  
			
			path_tmp = PathKit.getWebRootPath() + uploadDir; 
			
			//判断创建文件夹  
	        File tempFile = new File(path_tmp);  
	        if(!tempFile.exists()){  
	        	tempFile.mkdir();  
	        }else{
	        	//删除零时文件
		        StringUtil.delAllFile(path_tmp);
	        }
	        
			UploadFile uploadFile = getFile("baseapk", path_tmp); 
			uploadFile.getFile().renameTo(new File(path_tmp + "market_base.apk"));
			fileNamebase = uploadFile.getFileName();    
			real_path = uploadFile.getSaveDirectory();  
			final String pathAndName = real_path + "market_base.apk";  
			String businessids = getSessionAttr("businessids");
			final String innerbusid = businessids;
				new Thread(){
					@Override
					public void run() {
//						String businessids = getSessionAttr("businessids");
						List<Businessinfo> list = Businessinfo.dao.selectBussinessinfoList(innerbusid);
						if(list != null && list.size() > 0){
							for(int i = 0; i < list.size(); i++){
								Businessinfo businfo = list.get(i);
								Businessinfo businessinfo = Businessinfo.dao.findById(businfo
										.getInt("id"));
										final String qudaohao = businessinfo.getStr("apkname");
									String  apkurl = uplaodFileToSystem(pathAndName, qudaohao);
									Date now = new Date();
									businessinfo.set("updatetime", now);
									businessinfo.set("apkurl", apkurl);
									businessinfo.update();
									super.run();
							}
						}
					
					}
				}.start();
				
			Data rst = new Data();
			rst.setStatus(Constants.OPERATION_SUCCEED);
			rst.setMsg("打包apk成功, 后台正在处理中，请稍后查看结果!");
			setFlashData(rst);		
			redirect("/uploadapk/uploadBaseapp");
			
		}else{
			flashRender("/admin/businessinfo/upload_app.html");
		}
		
	}
	
	public String uplaodFileToSystem(String pathAndName, String qudaohao){
		
		/***********添加apk start***********/
		String apkurl = "";
		String apkname = "";
		String tempDir = "";
		String file = "";
		String apkSize = "";
		
		String fileName = qudaohao;
		
		String baseDir = JFinal.me().getServletContext().getRealPath("/");
		StringBuffer apkPath = new StringBuffer();
		apkPath.append(baseDir).append("apkname").append(File.separator).append("tychannel_"+fileName);
		try {
			FileUtils.touch(new File(apkPath.toString()));
			
			String srcPath = baseDir+"apkmodel"+File.separator;
			String dirName = "com.guozhen.tianyumarket_";
			
			//零时apk文件保存目录
			tempDir = baseDir+"apktemp";
			//判断创建文件夹  
	        File tempFile = new File(tempDir);  
	        if(!tempFile.exists()){  
	        	tempFile.mkdir();  
	        }else{
	        	//删除零时文件
		        StringUtil.delAllFile(tempDir);
	        }
	        String time = StringUtil.getDateStr14();//apk文件时间戳
	        apkname = dirName+fileName+"_"+time+".apk";
	        file = tempDir+File.separator+apkname;
	        
	        File srcFile = new File(pathAndName);
	        apkSize = FileUtil.FormetFileSize(srcFile.length());
	        
	        FileUtil.copyFile(srcFile, new File(file));
	        
			ZipUtil.apkCompress(file,apkPath.toString());
			
			//上传文件服务器
			InputStream is = new FileInputStream(new File(file));
			
			URL url = new URL("http://61.191.204.180:8081/asyncUpload?qqfile=sjzs.apk&fileSize=" +is.available());  
			
	        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
	        // 设置通用的请求属性
	        urlConnection.setRequestProperty("accept", "*/*");
	        urlConnection.setRequestProperty("connection", "Keep-Alive");
	        urlConnection.setRequestProperty("user-agent",
	                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	        urlConnection.setDoOutput(true);
	        urlConnection.setDoInput(true);
	        OutputStream os = urlConnection.getOutputStream();
	        
	        IOUtils.copyLarge(is, os);
	        
	        urlConnection.connect();
	        BufferedReader in = null;
	        
	        in = new BufferedReader(
	                new InputStreamReader(urlConnection.getInputStream()));
	        String line="";
	        while ((line = in.readLine()) != null) {
	        	apkurl += line;
	        }
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//删除零时文件
		FileUtils.deleteQuietly(new File(apkPath.toString()));
		
		return apkurl;
		
//		businessinfo.set("apkname", fileName);
//		businessinfo.set("apkurl", apkurl);
//		businessinfo.set("apksize", apkSize);
		/***********添加apk end************/
	}
	


}

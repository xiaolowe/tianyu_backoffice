package com.kdgz.uwifi.backoffice.controller;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.jfinal.aop.Before;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.LayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Acauth;
import com.kdgz.uwifi.backoffice.model.Acinfo;
import com.kdgz.uwifi.backoffice.model.Areainfo;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.model.Businesstemplet;
import com.kdgz.uwifi.backoffice.model.Userbiz;
import com.kdgz.uwifi.backoffice.util.CommonUtil;
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
public class BusinessinfoController extends BaseController {

	ResourceBundle bundle = ResourceBundle.getBundle("common");
	
	/**
	 * 列表
	 */
	
	public void index() {

		String busname1 = getPara("busname1");
		String busname2 = getPara("busname2");
		String acid = getPara("acid");
		String searchType = getPara("searchType");
		
		Integer roleId = getSessionAttr("roleCode");
		
		String areaId = "", provinceId = "", cityId = "", countiesId = "";
		if(roleId.equals(Constants.ROLE_ADMIN)){
			
			 areaId = getPara("areaNAME","");
			 provinceId = getPara("provinceNAME","");
			 cityId = getPara("cityNAME","");
			 countiesId = getPara("countiesNAME","");
			
		}else{
			
			//区域设置的参数
			 areaId = StringUtil.null2String(getSessionAttr("areaID"));
			 provinceId = StringUtil.null2String(getSessionAttr("provinceID"));
			 cityId = StringUtil.null2String(getSessionAttr("cityID"));
			 countiesId = StringUtil.null2String(getSessionAttr("countiesID"));
			 
		}
		

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

		int busPage = 1;
		int acPage = 1;
		
		//控制搜索后编辑店铺
		String busName = "";
		String acName = "";
		try {
			busName = java.net.URLDecoder.decode(getPara(2,""),"UTF-8");
			acName = java.net.URLDecoder.decode(getPara(3,""),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(acName.length() > 0){
			acid = acName;
		}
		// 路由器列表分页Page
		if (getPara(1) == null || "dp".equals(getPara(1))) {
			if(busName.length() > 0){
				busname1 = busName;
			}
			busPage = getParaToInt(0, 1);
		} else {
			if(busName.length() > 0){
				busname2 = busName;
			}
			isHomeActive = false;
			acPage = getParaToInt(0, 1);
		}
		setAttr("isHomeActive", isHomeActive);

		// 店铺列表
		String businessids = getSessionAttr("businessids");
		//登录用户id
		Integer selfid = getSessionAttr("userid");
		
		setAttr("businessinfoPage", Businessinfo.dao.pageBusinessinfoWithArea(busPage,
						10, busname1, businessids, areaId, provinceId, cityId, countiesId));
		

		setAttr("acinfoPage", Acinfo.dao.pageAcInfoList(acPage, 10, busname2,
				acid, businessids, areaId, provinceId, cityId, countiesId));
				
				
				
				
		setAttr("busname1", busname1);
		setAttr("busname2", busname2);
		setAttr("acid", acid);
		setAttr("acPage", acPage);
		
		setAttr("BUSPAGE", "buspage");
		
		setAttr("roleId", roleId);
		
		setAttr("areaNAME", areaId);
		setAttr("provinceNAME", provinceId);
		setAttr("cityNAME", cityId);
		setAttr("countiesNAME", countiesId);
		
		getProvinceListByArea(areaId);
		getCityListByPorvince(provinceId);
		getCountiesListByCity(cityId);
		
		setAttr("selfid", selfid);
		setAttr("bussinessidStr", businessids);

		flashRender("/admin/businessinfo/shop_list.html");
	}

	public void checkName() {
		boolean valid = true;
		String busname = getPara("businessinfo.busname","");
		Businessinfo businfo = Businessinfo.dao.selectBusinessinfoByName(busname, 0);
		if (businfo != null) {
			valid = false;
		}
		
		renderJson("valid",valid);
	}
	/**
	 * 添加店铺信息
	 */
	public void addBusinessinfo() {

		Businessinfo businessinfo = new Businessinfo();
		
		Integer rolecode = getSessionAttr("roleCode");

		if (getRequest().getMethod().equals("POST")) {
			String area = getSessionAttr("loginUserArea");
			String province = getSessionAttr("loginUserProvince");
			String city = getSessionAttr("loginUserCity");
			String counties = getSessionAttr("loginUserCounties");
			area = area=="admin" ? "" : area;
			province = province=="admin" ? "" : province;
			city = city=="admin" ? "" : city;
			counties = counties=="admin" ? "" : counties;
			
			Businessinfo param = getModel(Businessinfo.class);
			
			//用户选择的区域
			String provinceSel = param.getStr("province");
			String citySel = param.getStr("city");
			String countiesSel = param.getStr("counties");

			businessinfo.set("busname", param.getStr("busname"));
			businessinfo.set("person", param.getStr("person"));
			businessinfo.set("phone", param.getStr("phone"));
			businessinfo.set("address", param.getStr("address"));
			
			businessinfo.set("area", area);
			
			if(Constants.ROLE_AREA.equals(rolecode)){
				if(null == provinceSel && null ==citySel && null == countiesSel){
					businessinfo.set("province", province);
					businessinfo.set("city", city);
					businessinfo.set("counties", counties);
				} else {
					businessinfo.set("province", provinceSel);
					businessinfo.set("city", citySel);
					businessinfo.set("counties", countiesSel);
				}
			}
			if(Constants.ROLE_PROVINCE.equals(rolecode)){
				businessinfo.set("province", province);
				if(null ==citySel && null == countiesSel){
					businessinfo.set("city", city);
					businessinfo.set("counties", counties);
				} else {
					businessinfo.set("city", citySel);
					businessinfo.set("counties", countiesSel);
				}
			}
			if(Constants.ROLE_CITY.equals(rolecode)){
				businessinfo.set("province", province);
				businessinfo.set("city", city);
				if(null == countiesSel){
					businessinfo.set("counties", counties);
				} else {
					businessinfo.set("counties", countiesSel);
				}
			}
			if(Constants.ROLE_COUNTIES.equals(rolecode)){
				businessinfo.set("province", province);
				businessinfo.set("city", city);
				businessinfo.set("counties", counties);
			}
			Date now = new Date();
			businessinfo.set("addtime", now);
			businessinfo.set("updatetime", now);

			Data rst = new Data();
			Businessinfo businfo = Businessinfo.dao.selectBusinessinfoByName(
					param.getStr("busname"), 0);

			if (businfo != null) {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("该店铺已经存在！");
			} else {
				
				/***********添加apk start***********/
				String apkurl = "";
				String apkname = "";
				String tempDir = "";
				String file = "";
				String apkSize = "";
				
				String fileName = CommonUtil.getRandomString16(16);
				
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
			        
			        File srcFile = new File(srcPath+"market_base.apk");
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
				
				businessinfo.set("apkname", fileName);
				businessinfo.set("apkurl", apkurl);
				businessinfo.set("apksize", apkSize);
				/***********添加apk end************/
				
				
				if (businessinfo.save()) {
					int id = businessinfo.getInt("id");
					int userid = getSessionAttr("userid");
					Userbiz userbiz = new Userbiz();
					userbiz.set("userid", userid);
					userbiz.set("businessid", id);
					userbiz.save();
					
					Businesstemplet bt = new Businesstemplet();
					bt.set("businessid", id);
					bt.set("authid", 0);  //默认app下载模板
					bt.save();
					
					Acauth acauth = new Acauth();
					acauth.set("businessid", id);
					acauth.set("authtype", 0); //默认app下载认证方式
					acauth.set("afterauth", 1);
					String portalurl = bundle.getString("authportalUrl").trim();
					acauth.set("portalurl", portalurl + "?businessid="+id);
					acauth.save();
					
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("添加店铺成功！");
					setFlashData(rst);
					removeSessionAttr("businessids");
				} else {
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("添加店铺失败！");
				}
			}

			renderJson(rst);
		} else {
			setAttr("businessinfo", businessinfo);
			getUserRole();
			setAttr("rolecode", rolecode);
			render("/admin/businessinfo/add_shop.html");
		}
	}
	
   private void getUserRole(){
		
		setAttr("admin", Constants.ROLE_ADMIN);
		setAttr("area", Constants.ROLE_AREA);
		setAttr("province", Constants.ROLE_PROVINCE);
		setAttr("city", Constants.ROLE_CITY);
		setAttr("counties", Constants.ROLE_COUNTIES);
		
	}

	/**
	 * 编辑店铺信息
	 */
	public void editBusinessinfo() {

		Integer rolecode = getSessionAttr("roleCode");
		
		if (getRequest().getMethod().equals("POST")) {
			String area = getSessionAttr("loginUserArea");
			String province = getSessionAttr("loginUserProvince");
			String city = getSessionAttr("loginUserCity");
			String counties = getSessionAttr("loginUserCounties");
			area = area=="admin" ? "" : area;
			province = province=="admin" ? "" : province;
			city = city=="admin" ? "" : city;
			counties = counties=="admin" ? "" : counties;
			
			Businessinfo param = getModel(Businessinfo.class);
			
			//用户选择的区域
			String provinceSel = param.getStr("province");
			String citySel = param.getStr("city");
			String countiesSel = param.getStr("counties");

			Businessinfo businessinfo = Businessinfo.dao.findById(param
					.getInt("id"));

			businessinfo.set("busname", param.getStr("busname"));
			businessinfo.set("person", param.getStr("person"));
			businessinfo.set("phone", param.getStr("phone"));
			businessinfo.set("address", param.getStr("address"));
			businessinfo.set("apkurl", param.getStr("apkurl"));
			
			businessinfo.set("area", area);
			
			if(Constants.ROLE_AREA.equals(rolecode)){
				if(null == provinceSel && null ==citySel && null == countiesSel){
					businessinfo.set("province", province);
					businessinfo.set("city", city);
					businessinfo.set("counties", counties);
				} else {
					businessinfo.set("province", provinceSel);
					businessinfo.set("city", citySel);
					businessinfo.set("counties", countiesSel);
				}
			}
			if(Constants.ROLE_PROVINCE.equals(rolecode)){
				businessinfo.set("province", province);
				if(null ==citySel && null == countiesSel){
					businessinfo.set("city", city);
					businessinfo.set("counties", counties);
				} else {
					businessinfo.set("city", citySel);
					businessinfo.set("counties", countiesSel);
				}
			}
			if(Constants.ROLE_CITY.equals(rolecode)){
				businessinfo.set("province", province);
				businessinfo.set("city", city);
				if(null == countiesSel){
					businessinfo.set("counties", counties);
				} else {
					businessinfo.set("counties", countiesSel);
				}
			}
			if(Constants.ROLE_COUNTIES.equals(rolecode)){
				businessinfo.set("province", province);
				businessinfo.set("city", city);
				businessinfo.set("counties", counties);
			}
			
			Date now = new Date();
			businessinfo.set("updatetime", now);
			Data rst = new Data();

			Businessinfo businfo = Businessinfo.dao.selectBusinessinfoByName(
					param.getStr("busname"), param.getInt("id"));

			if (businfo != null) {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("该店铺已经存在！");
			} else {
				if (businessinfo.update()) {
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("编辑店铺信息成功！");
					setFlashData(rst);
				} else {
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("编辑店铺信息失败！");
				}
			}

			renderJson(rst);
		} else {
			Businessinfo businessinfo = Businessinfo.dao
					.findById(getParaToInt(0));
			setAttr("businessinfo", businessinfo);
			//控制搜索后编辑
			String searchName = "";
			try {
				searchName = java.net.URLDecoder.decode(getPara(2,""),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			getUserRole();
			setAttr("rolecode", rolecode);
			
			setAttr("searchName",searchName);//添加搜索条件的传递
			setAttr("currentPage",getParaToInt(1, 1));//添加当前页num
			render("/admin/businessinfo/edit_shop.html");
		}
	}
	
	public void uploadBaseapp(){
		
		if (getRequest().getMethod().equals("POST")) {
			
			String path_tmp = "";    
			String real_path = "";
			String fileNamebase = "";
			String  apkurl = "";
			String uploadDir = File.separator + "apkmodel" + File.separator ;   
			path_tmp = PathKit.getWebRootPath() + uploadDir; 
			UploadFile uploadFile = getFile("baseapk", path_tmp);   
			fileNamebase = uploadFile.getFileName();    
			real_path = uploadFile.getSaveDirectory();  
			final String pathAndName = real_path + fileNamebase;  
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
			rst.setMsg("上传apk成功, 后台正在处理中，请稍后查看结果!");
			setFlashData(rst);		
			redirect("/businessinfo");
			
		}else{
			render("/admin/businessinfo/upload_app.html");
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
	

	/**
	 * 删除店铺信息
	 * 
	 * @throws Exception
	 */
	@Before(Tx.class)
	public void delete() throws Exception {

		int id = getParaToInt(0, 0);

		if (id != 0) {

			Data rst = new Data();
			try {

				int userid = getSessionAttr("userid");

				Businessinfo.dao.deleteBusinessInfo(id, userid);

				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("删除店铺信息成功！");
				removeSessionAttr("businessids");

			} catch (Exception e) {
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("删除店铺信息失败！");
				throw e;
			}
			setFlashData(rst);
			setSessionAttr("isHomeActive", true);
			//控制搜索后删除
			String searchName = "";
			try {
				searchName = java.net.URLDecoder.decode(getPara(2,""),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			searchName = java.net.URLEncoder.encode(searchName, "UTF-8");  
			int currentPage = getParaToInt(1, 1);//获取当前页
			redirect("/businessinfo/"+currentPage+"-dp-"+searchName);
		}
	}

	/**
	 * 店铺装修
	 */
	public void editShop() {
		Integer businessid = getParaToInt("businessid", -1);
		String acid = getPara("acid", "");
		setAttr("businessid", businessid);
		setSessionAttr("shopid", businessid);
		setAttr("acid", acid);
		forwardAction("/authStyle");

	}
	

	public void getBusinessinfoList() {

		List<Businessinfo> list = Businessinfo.dao
				.selectBussinessinfoList(null);
		renderJson(list);
	}
	
	/**
	 * 绑定应用
	 */
	public void bindApp() {
		if (getRequest().getMethod().equals("POST")) {
		
			Data rst = new Data();
			rst.setStatus(Constants.OPERATION_SUCCEED);
			rst.setMsg("添加成功！");
			renderJson(rst);
		}else{
			String businessids = getSessionAttr("businessids");
			String busname1 = getPara("busname1");
			int busPage = 1;
			if (getPara(1) == null || "dp".equals(getPara(1))) {
				busPage = getParaToInt(0, 1);
			}
			
			setAttr("businessinfoPage", Businessinfo.dao.pageBusinessinfo(busPage,
					10, busname1, businessids));
			//forwardAction("/authStyle");
			render("/admin/businessinfo/bindAppPage.html");
		}
	}
	
	/**
	 * 获取省份List
	 */
	public void getProvinceListByArea(String areaid) {
		List<Areainfo> provinceList = Areainfo.dao.selectProvinceList(areaid);
		setAttr("provinceShopList", provinceList);
	}
	
	/**
	 * 获取城市List
	 */
	public void getCityListByPorvince(String provinceid) {
		List<Areainfo> cityList = Areainfo.dao.selectCityList(provinceid);
		setAttr("cityShopList", cityList);
	}
	
	/**
	 * 获取区县List
	 */
	public void getCountiesListByCity(String cityid) {
		List<Areainfo> countiesList = Areainfo.dao.selectCityList(cityid);
		setAttr("countiesShopList", countiesList);
	}
	
	
	
	public String expBusinessinfo(String businessId){
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
			
			List<Businessinfo> buslist = Businessinfo.dao.businessinfoList(businessId);
			// 获取设备运营状态详情
			
			exportBusinessinfodownload(wb,buslist);
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
	
	
	
	private void exportBusinessinfodownload(HSSFWorkbook wb, List<Businessinfo> beanList) {
		HSSFRow row;
		HSSFCell cell;
		HSSFSheet sheet = wb.getSheetAt(0);
		
		//处理表头
		//row = sheet.getRow(1);
		row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("店铺名称");
		cell = row.createCell(1);
		cell.setCellValue("联系人");
		cell = row.createCell(2);
		cell.setCellValue("联系方式");
		cell = row.createCell(3);
		cell.setCellValue("渠道号");
		cell = row.createCell(4);
		cell.setCellValue("创建时间");
		
		for (int i = 0; i < beanList.size(); i++) {
			row = sheet.createRow(i+1);
			cell = row.createCell(0);
			cell.setCellValue(beanList.get(i).getStr("busname")==null?"未知":beanList.get(i).getStr("busname"));
			cell = row.createCell(1);
			cell.setCellValue(beanList.get(i).getStr("person")==null?"未知":beanList.get(i).getStr("person"));
			cell = row.createCell(2);
			cell.setCellValue(beanList.get(i).getStr("phone")==null?"未知":beanList.get(i).getStr("phone"));
			cell = row.createCell(3);
			cell.setCellValue(beanList.get(i).getStr("apkname")==null?"未知":beanList.get(i).getStr("apkname"));
			cell = row.createCell(4);
			cell.setCellValue(beanList.get(i).getDate("addtime")==null?"未知":beanList.get(i).getDate("addtime").toString().substring(0, 19));
		}
	}
	
	
	public String expAcinfo(String businessId){
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
			
			List<Acinfo> buslist = Acinfo.dao.acinfoListExport(businessId);
			// 获取设备运营状态详情
			
			exportAcinfodownload(wb,buslist);
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
	
	
	private void exportAcinfodownload(HSSFWorkbook wb, List<Acinfo> beanList) {
		HSSFRow row;
		HSSFCell cell;
		HSSFSheet sheet = wb.getSheetAt(0);
		
		//处理表头
		//row = sheet.getRow(1);
		row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("设备编号");
		cell = row.createCell(1);
		cell.setCellValue("所属店铺");
		cell = row.createCell(2);
		cell.setCellValue("WiFi信号名称");
		cell = row.createCell(3);
		cell.setCellValue("创建时间");
		
		for (int i = 0; i < beanList.size(); i++) {
			row = sheet.createRow(i+1);
			cell = row.createCell(0);
			cell.setCellValue(beanList.get(i).getStr("acid")==null?"未知":beanList.get(i).getStr("acid"));
			cell = row.createCell(1);
			cell.setCellValue(beanList.get(i).getStr("busname")==null?"未知":beanList.get(i).getStr("busname"));
			cell = row.createCell(2);
			cell.setCellValue(beanList.get(i).getStr("eqptssid")==null?"未知":beanList.get(i).getStr("eqptssid"));
			cell = row.createCell(3);
			cell.setCellValue(beanList.get(i).getDate("addtime")==null?"未知":beanList.get(i).getDate("addtime").toString().substring(0, 19));
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

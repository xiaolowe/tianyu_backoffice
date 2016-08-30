package com.kdgz.uwifi.backoffice.controller;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.bean.UploadData;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.model.Areainfo;
import com.kdgz.uwifi.backoffice.model.Syscode;
import com.kdgz.uwifi.backoffice.model.Userinfo;
import com.kdgz.uwifi.backoffice.util.CommonUtil;
import com.kdgz.uwifi.backoffice.util.DateUtil;
import com.kdgz.uwifi.backoffice.util.EncryptUtil;

/**
 * 站点控制器
 * 
 * @author lanbo
 * 
 */
public class SiteController extends BaseController {

	public void index() {
		
		flashRender("/admin/site/login_new.html");
	}

	/**
	 * 登陆
	 */
	public void login() {

		String username = getPara("username");
		String password = getPara("password");
		// String rememberMe = getPara("rememberMe");

		if (StrKit.isBlank(username) || StrKit.isBlank(password)) {
			
			if(StringUtils.isNotEmpty(username)) {
				setAttr("username", username);
			}
			if(StringUtils.isNotEmpty(password)) {
				setAttr("password", password);
			}
			
			setAttr("attentionMsg", "请输入用户名及密码 ");
			render("/admin/site/login_new.html");
		} else {
			Userinfo  user = Userinfo.dao.getUserInfo(username);
			 
			if (user == null) {
				setAttr("username", username);
				setAttr("password", password);
				setAttr("errorMsg", "用户账户不存在，请联系管理员！");
				render("/admin/site/login_new.html");
			}else if(!user.getBoolean("status")){
				setAttr("username", username);
				setAttr("password", password);
				setAttr("errorMsg", "用户账户已被禁用，请联系管理员！");
				render("/admin/site/login_new.html");
				
			}else {
				
				String encryptPass = EncryptUtil.md5(EncryptUtil.md5(password)
						.substring(8) + user.getStr("salt"));
				
				if (user.getStr("password").equals(encryptPass)) {

					setSessionAttr("loginName", user.getStr("loginname"));
					setSessionAttr("roleCode", user.getInt("rolecode"));
					setSessionAttr("isLogin", true);
					setSessionAttr("userid", user.getInt("id"));
					setSessionAttr("invitecode", user.getStr("invitecode"));
					
					String area="";
					String province="";
					String city="";
					String counties="";
					if(Constants.ROLE_ADMIN.equals(user.getInt("rolecode"))){
						area = Constants.ADMIN_USERNAME;
						province = Constants.ADMIN_USERNAME;
						city = Constants.ADMIN_USERNAME;
						counties = Constants.ADMIN_USERNAME;
					}else{
						area = user.get("area");
						province = user.get("province");
						city = user.get("city");
						counties = user.get("counties");
					}
					
					setSessionAttr("loginUserArea", area);
					setSessionAttr("loginUserProvince", province);
					setSessionAttr("loginUserCity", city);
					setSessionAttr("loginUserCounties", counties);
					
					if(Constants.ROLE_ADMIN.equals(user.getInt("rolecode"))){
						redirect("/businessinfo");
					}else{
//						redirect("/dashboard");
						redirect("/businessinfo");
						
					}
				} else {
					setAttr("username", username);
					setAttr("password", password);
					setAttr("errorMsg", "用户密码错误，请重新输入！");
					render("/admin/site/login_new.html");
				}
			}
		}
	}

	/**
	 * 登出
	 */
	public void logout() {

		removeSessionAttr("username");
		removeSessionAttr("name");
		removeSessionAttr("roleCode");
		removeSessionAttr("userid");
		removeSessionAttr("businessids");
		removeSessionAttr("isLogin");
		
		removeSessionAttr("areaID");
		removeSessionAttr("provinceID");
		removeSessionAttr("cityID");
		removeSessionAttr("countiesID");

		renderFreeMarker("/admin/site/login_new.html");
	}
	
	public void register(){
		
		if (getRequest().getMethod().equals("POST")) {
			Data rst = new Data();
			
			Userinfo param = getModel(Userinfo.class);
			Userinfo user = new Userinfo();
			
			user.set("loginname", param.getStr("loginname"));

			String salt = CommonUtil.getRandomString(8);
			user.set("password", CommonUtil.generateSaltPassword(
					param.getStr("password"), salt));
			user.set("salt", salt);

			user.set("rolecode", 5);//默认区县角色
			user.set("phone", param.getStr("phone"));
			user.set("status", 1);		//默认启用账户
			user.set("area", param.getStr("area"));
			user.set("province", param.getStr("province"));
			user.set("city", param.getStr("city"));
			user.set("counties", param.getStr("counties"));
//			user.set("invitecode", param.getStr("invitecode"));
			String invitecode = param.getStr("invitecode");
			if(invitecode != null){
				Userinfo user2 = Userinfo.dao.getUserIdByInvitecode(invitecode);
				if(user2 != null){
					int pid = user2.getInt("id");
					user.set("pid", pid);
				}else{
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("该邀请码不存在，请重新添加！");
					renderJson(rst);
					return;
				}
			}else{
				user.set("pid", 0);	//admin账号可查看的用户
			}
			
			
			Date now = new Date();
			user.set("addtime", now);
			user.set("updatetime", now);

			if (user.save()) {
				int userid = user.getInt("id");
				Record role = new Record();
				role.set("userid", userid);
				role.set("roleid", 5);
				Db.save("userrole", role); // 保存用户所具有的权限

				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("添加新用户成功！");
				setFlashData(rst);
			} else {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("添加新用户失败！");
			}
			renderJson(rst);
			
		}else {
			List<Syscode>  areaList = Syscode.dao.areaList();
			setAttr("arealist", areaList);
			renderFreeMarker("/admin/site/user_register.html");
		}
		
	}
	
	public void checkName() {
		boolean valid = true;
		String loginname = getPara("userinfo.loginname","");
		long count = Userinfo.dao.countExistUser(loginname);

		if (count > 0) {
			valid = false;
		}
		renderJson("valid",valid);
	}

	/**
	 * 上传图片
	 * 
	 * 100 模块图片 200 模块列表图片 300 模块列表详细图片
	 */
	public void uploadPicture() {
		
		// 图片类型
		String type = getPara("type");

		// 图片扩展名
		String randomName = CommonUtil.getRandomString(8);
		String fileExt = FilenameUtils.getExtension(getPara("qqfile"));

		String fileName = randomName + "." + fileExt;

		Date now = new Date();
		// 获取上传图片路径
		String targetPath = getPictureSavePath(type, fileName, now);

		String savePath = FilenameUtils.separatorsToUnix(targetPath);

		// 目标保存文件
		File targetFile = new File(getSession().getServletContext()
				.getRealPath("/") + targetPath);
		
		try {
			FileUtils.copyInputStreamToFile(getRequest().getInputStream(),
					targetFile);
		} catch (IOException e) {
			renderJson("error", "图片上传失败，请稍后重试");
		}

		renderJson("path", savePath);
	}
	public void loadFileImage() {
		
		try {
			
		String fileSize = getPara("fileSize");
		
		long size = (Long.valueOf(fileSize) / (1024 * 1024));
		String qqFile = getPara("qqfile");
		
		String extname = qqFile.substring(qqFile.lastIndexOf(".") + 1);
		
		StringBuffer name = new StringBuffer();
		name.append("upload.");
		name.append(extname);
		String filename = name.toString();
		
		InputStream is = getRequest().getInputStream();
		
		URL url = new URL("http://61.191.204.180:8081/asyncUpload?qqfile="+filename+"&fileSize=" +fileSize);  
		
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
        String result = "";
        in = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream()));
        String line="";
        while ((line = in.readLine()) != null) {
            result += line;
        }
        UploadData data = new UploadData();
        data.setFileId(result);
        data.setFilesize(size);
        renderJson(data);

		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static String httpPost(String url) {

		String responseBody = "";
		// 调用数据平台接口 获取统计数据
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httppost = new HttpPost(url);

			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				public String handleResponse(final HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity)
								: null;
					} else {
						throw new ClientProtocolException(
								"Unexpected response status: " + status);
					}
				}

			};
			responseBody = httpclient.execute(httppost, responseHandler);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseBody;
	}


	/**
	 * 删除图片
	 * 
	 * @param type
	 * @return
	 */
	public void deletePicture() {

		// 图片路径
		String path = getPara("path");

		// 目标保存文件
		File targetFile = new File(getSession().getServletContext()
				.getRealPath("/") + path);

		FileUtils.deleteQuietly(targetFile);

		renderText("0");

	}

	/**
	 * 生成图片存储相对路径
	 * 
	 * @param type
	 * @param fileName
	 * @param now
	 * @return
	 */
	private String getPictureSavePath(String type, String fileName, Date now) {

		StringBuilder sb = new StringBuilder();
		sb.append(Constants.UPLOADED_DIRECTORY).append(File.separator)
				.append(Constants.IMAGE_DIRECTORY.get(type))
				.append(File.separator).append(DateUtil.toYYYMMStr(now))
				.append(File.separator)
				.append(DateUtil.toStr(now, Constants.DATETIME_FORMAT_SHORT))
				.append(File.separator).append(fileName);

		return sb.toString();
	}
	
	
	
	/**
	 * 获取省份List
	 */
	public void getProvinceList() {
		String areaid = getPara("areaid");
		List<Areainfo> provinceList = Areainfo.dao.selectProvinceList(areaid);
		renderJson(provinceList);
	}
	
	/**
	 * 获取城市List
	 */
	public void getCityList() {
		String provinceid = getPara("provinceid");
		List<Areainfo> cityList = Areainfo.dao.selectCityList(provinceid);
		renderJson(cityList);
	}
	
	/**
	 * 获取区县List
	 */
	public void getCountiesList() {
		String countiesid = getPara("countiesid");
		List<Areainfo> countiesList = Areainfo.dao.selectCityList(countiesid);
		renderJson(countiesList);
	}
	
	public void setAreaToSession() {
		String areaid = getPara("areaid","");
		String provinceid = getPara("provinceid","");
		String cityid = getPara("cityid","");
		String countiesid = getPara("countiesid","");
		
		setSessionAttr("areaID", areaid);
		setSessionAttr("provinceID", provinceid);
		setSessionAttr("cityID", cityid);
		setSessionAttr("countiesID", countiesid);
		
		String curUri = getPara("curUri","");
		if("/businessinfo/index".endsWith(curUri)){
			curUri="/businessinfo";
		}
		
		//renderText("0");
		//redirect("/businessinfo");
		redirect(curUri);
	}
}

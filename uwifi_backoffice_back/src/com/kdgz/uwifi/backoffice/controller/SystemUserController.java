package com.kdgz.uwifi.backoffice.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.jfinal.aop.Before;
import com.jfinal.core.JFinal;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.AdminRoleInterceptor;
import com.kdgz.uwifi.backoffice.interceptor.LayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Areainfo;
import com.kdgz.uwifi.backoffice.model.Menuinfo;
import com.kdgz.uwifi.backoffice.model.Roleinfo;
import com.kdgz.uwifi.backoffice.model.Rolemenu;
import com.kdgz.uwifi.backoffice.model.Syscode;
import com.kdgz.uwifi.backoffice.model.Userinfo;
import com.kdgz.uwifi.backoffice.util.CommonUtil;
import com.kdgz.uwifi.backoffice.util.DateUtil;

/**
 * 系统用户
 * 
 * @author Lanbo
 * 
 */
@Before(LayoutInterceptor.class)
public class SystemUserController extends BaseController {

	/**
	 * 系统用户一览
	 */
	@Before(AdminRoleInterceptor.class)
	public void index() {
		
		int pageNum = 1;
		
		if(getPara(0) != null) {
			if(!getPara(0).equals("index")) {
				pageNum = getParaToInt(0, 1);
			}
		}
		Integer uid = getSessionAttr("userid");
		String countiesId =  getSessionAttr("loginUserCounties");
		Integer rolecode = getSessionAttr("roleCode");
		String invitecode = getSessionAttr("invitecode");
		String loginname = getPara("loginname");
		setAttr("userPage", Userinfo.dao.paginate(pageNum, 10, uid, rolecode, invitecode,loginname));
		setAttr("loginname", loginname);
		setAttr("countiesId", countiesId);
		setAttr("rolecode", rolecode);
		if (getPara(1) != null) {
			setAttr("infoMsg", getPara(1));
		}
		flashRender("/admin/system_user/user_list.html");
	}
	
	/**
	 * 我的账户
	 */
	public void account() {
		
		String username = getSessionAttr("loginName");
		setAttr("userinfo", Userinfo.dao.getUserInfo(username));

		flashRender("/admin/user/myaccount.html");
	}
	
	/**
	 * 更新我的账户
	 */
	public void update() {

		Userinfo user = getModel(Userinfo.class);

		String salt = CommonUtil.getRandomString(8);

		user.set("salt", salt);

		user.set("password",
				CommonUtil.generateSaltPassword(user.getStr("password"), salt));

		user.set("updatetime", DateUtil.toYYYMMStr(new Date()));

		user.update();

		setFlashData(Constants.OPERATION_SUCCEED, "保存成功！");

		redirect("/site/logout");
	}
	
	/**
	 * 删除用户
	 */
	@Before(AdminRoleInterceptor.class)
	public void delete() {

		int id = getParaToInt(0, 0);

		if (id != 0) {

			Userinfo user = Userinfo.dao.findById(id);

			Data rst = new Data();

			if (user.getStr("loginname").equals("admin")) {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("管理员账户不能删除！");
			} else {
				
				
				
				
				Userinfo.dao.deleteById(id);
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("删除账号成功！");

			}
			setFlashData(rst);
			
			int currentPage = getParaToInt(1, 1);// 获取当前页数

			redirect("/sysUser/"+currentPage);
		}
	}

	/**
	 * 修改密码
	 */
	@Before(AdminRoleInterceptor.class)
	public void changePass() {

		if (getRequest().getMethod().equals("POST")) {
			Userinfo user = getModel(Userinfo.class);

			Userinfo currentUser = Userinfo.dao.findById(user.get("id"));

			String salt = CommonUtil.getRandomString(8);

			currentUser.set("salt", salt);

			currentUser.set("password", CommonUtil.generateSaltPassword(
					user.getStr("password"), salt));

			currentUser.set("updatetime", new Date());

			currentUser.update();

			Data rst = new Data();
			rst.setStatus(Constants.OPERATION_SUCCEED);
			rst.setMsg("修改密码成功！");
			setFlashData(rst);
			renderJson(rst);
		} else {

			Userinfo user = Userinfo.dao.findById(getParaToInt(0));

			if (user != null) {

				setAttr("userinfo", user);
				setAttr("currentPage",getParaToInt(1, 1));//设置当前页数
				render("/admin/system_user/change_pass.html");
			}
		}
	}

	/**
	 * 添加系统用户
	 */
	@Before(AdminRoleInterceptor.class)
	public void addUser() {

		Integer rolecode = getSessionAttr("roleCode");
		Integer uid = getSessionAttr("userid");
		
		Userinfo user = new Userinfo();

		if (getRequest().getMethod().equals("POST")) {
			Userinfo param = getModel(Userinfo.class);

			Data rst = new Data();
			
			// 验证是否已经添加过
			long count = Userinfo.dao.countExistUser(param.getStr("loginname"));

			if (count > 0) {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("该账号已经存在，请重新输入！");
			}
			
			if (Constants.OPERATION_FAILED.equals(rst.getStatus())) {
				renderJson(rst);
				return;
			}
			
			user.set("loginname", param.getStr("loginname"));

			String salt = CommonUtil.getRandomString(8);
			user.set("password", CommonUtil.generateSaltPassword(
					param.getStr("password"), salt));
			user.set("salt", salt);

			user.set("rolecode", param.getInt("rolecode"));
			user.set("phone", param.getStr("phone"));
			user.set("status", 1);		//默认启用账户
			user.set("pid", uid);
			user.set("area", param.getStr("area"));
			user.set("province", param.getStr("province"));
			user.set("city", param.getStr("city"));
			user.set("counties", param.getStr("counties"));
			
			String invitecode = param.getStr("invitecode");
			
			user.set("invitecode", invitecode);
			
			Date now = new Date();
			user.set("addtime", now);
			user.set("updatetime", now);

			if (user.save()) {
				int userid = user.getInt("id");
				Record role = new Record();
				role.set("userid", userid);
				role.set("roleid", param.getInt("rolecode"));
				Db.save("userrole", role); // 保存用户所具有的权限

				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("添加系统用户成功！");
				setFlashData(rst);
			} else {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("添加系统用户失败！");
			}
			renderJson(rst);
		} else {
			getRoleList(rolecode);
			getUserRole();
			getAreaAddress();
			setAttr("rolecode", rolecode);
			setAttr("userinfo", user);
			render("/admin/system_user/add_user.html");
		}
	}

	/**
	 * 编辑用户
	 */
	@Before(AdminRoleInterceptor.class)
	public void editUser() {
		
		Integer rolecode = getSessionAttr("roleCode");
		
		if (getRequest().getMethod().equals("POST")) {
			Userinfo param = getModel(Userinfo.class);

			Data rst = new Data();
			if (Constants.ADMIN_USERNAME.equals(param.getStr("loginname"))
					&& !param.getBoolean("status").booleanValue()) {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("管理员账户不能禁用！");
			} else {
				Userinfo user = Userinfo.dao.findById(getParaToInt(0));
				user.set("phone", param.getStr("phone"));
				user.set("rolecode", param.getInt("rolecode"));
				user.set("area", param.getStr("area"));
				user.set("province", param.getStr("province"));
				user.set("city", param.getStr("city"));
				user.set("counties", param.getStr("counties"));
				
				String invitecode = param.getStr("invitecode");
				
				user.set("invitecode", invitecode);
				
				Date now = new Date();
				user.set("updatetime", now);

				
				if (user.update()) {
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("编辑系统用户成功！");
				} else {
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("编辑系统用户失败！");
				}
			}
			setFlashData(rst);
			renderJson(rst);

		} else {
			String areaId = getPara("areaId");
			String provinceId = getPara("provinceId");
			String cityId = getPara("cityId");
			Userinfo user = Userinfo.dao.findById(getParaToInt(0));
			getRoleList(rolecode);
			getUserRole();
			
			if(StrKit.notBlank(areaId)){
				List<Areainfo> listProvince = Areainfo.dao.selectProvinceList(areaId);
				setAttr("listProvince", listProvince);
			}
			if(StrKit.notBlank(provinceId)){
				List<Areainfo> listCity = Areainfo.dao.selectCityList(provinceId);
				setAttr("listCity", listCity);
			}
			if(StrKit.notBlank(cityId)){
				List<Areainfo> listCounties = Areainfo.dao.selectCountiesList(cityId);
				setAttr("listCounties", listCounties);
			}
			setAttr("rolecode", rolecode);
			setAttr("userinfo", user);
			setAttr("currentPage",getParaToInt(1, 1));//设置当前页数
			render("/admin/system_user/edit_user.html");
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
	
	public void checkInviteCode() {
		boolean valid = true;
		String invitecode = getPara("userinfo.invitecode","");
		long count = Userinfo.dao.countExistInvitecode(invitecode);

		if (count > 0) {
			valid = false;
		}
		renderJson("valid",valid);
	}
	
	public void checkInviteCodeEdit() {
		
		int id = getParaToInt(0);
		boolean valid = true;
		String invitecode = getPara("userinfo.invitecode","");
		long count = Userinfo.dao.countExistInvitecodeedit(id, invitecode);

		if (count > 0) {
			valid = false;
		}
		renderJson("valid",valid);
	}

	/**
	 * 更改用户状态
	 */
	@Before(AdminRoleInterceptor.class)
	public void changeStatus() {

		int id = getParaToInt(0);

		Userinfo user = Userinfo.dao.findById(id);

		Data rst = new Data();
		if (Constants.ADMIN_USERNAME.equals(user.getStr("username"))) {

			rst.setStatus(Constants.OPERATION_FAILED);
			rst.setMsg("管理员账户的状态不能修改！");
		} else {
			user.set("status", user.getBoolean("status") ? 0 : 1);
			if (user.update()) {
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("账号状态修改成功！");
			}
		}
		setFlashData(rst);
		int currentPage = getParaToInt(1, 1);// 获取当前页数
		redirect("/sysUser/"+currentPage);
	}
	
	public void getRoleList(Integer rolecode){
		
		List<Roleinfo> listRoleinfo = Roleinfo.dao.selectRoleinfoList(rolecode);
		setAttr("listRoleinfo", listRoleinfo);
		
		
	}
	
	public void getUserRole(){
		
		setAttr("admin", Constants.ROLE_ADMIN);
		setAttr("area", Constants.ROLE_AREA);
		setAttr("province", Constants.ROLE_PROVINCE);
		setAttr("city", Constants.ROLE_CITY);
		setAttr("counties", Constants.ROLE_COUNTIES);
		
	}
	
	public void privilege(){
		

		setAttr("privilegePage", Roleinfo.dao.paginate(getParaToInt(0, 1), 10));

		flashRender("/admin/system_user/privilege_list.html");
	}
	
	public void editPrivilege(){
		
		Integer roleid = getParaToInt(0);
		Integer[] menuids = getParaValuesToInt("menuid");
		if (getRequest().getMethod().equals("POST")) {
				List<Rolemenu> rolemenuList = Rolemenu.dao.selectRolemenuByRoleId(roleid);
				Data rst = new Data();
				if(rolemenuList != null){
					for(int i = 0; i < rolemenuList.size(); i++){
						Rolemenu rolemenu = rolemenuList.get(i);
						int menuid = rolemenu.getInt("menuid");
						if(menuid != Constants.DEFAULT_MENUID 
								&& menuid != Constants.DEFAULT_SHOUYE
								&& menuid != Constants.DEFAULT_SHOP){
							if(rolemenu.delete()){
								rst.setStatus(Constants.OPERATION_SUCCEED);
								rst.setMsg("编辑用户权限成功！");
							}
						}
					}
				}
				if(menuids != null){
					for(int j = 0; j < menuids.length; j++){
						Integer menuid = menuids[j];
						Record record = new Record();
						record.set("roleid", roleid);
						record.set("menuid", menuid);
						boolean rolemenu = Db.save("rolemenu", record);
						if(rolemenu){
							rst.setStatus(Constants.OPERATION_SUCCEED);
							rst.setMsg("编辑用户权限成功！");
						}
					}
				}
				
			setFlashData(rst);
			redirect("/sysUser/privilege/");

		} else {
			List<Menuinfo> list = new ArrayList<Menuinfo>();
			List<Menuinfo> menuallList = Menuinfo.dao.selectMenuinfoList();
			List<Menuinfo> menucheckList = Menuinfo.dao.selectMenuinfoByRoleId(roleid);
			for(int i = 0; i < menuallList.size(); i++){
				Menuinfo menuinfoall = menuallList.get(i);
				Integer menuall= menuinfoall.getInt("id");
				for(int j = 0; j < menucheckList.size(); j++){
					Menuinfo menuinfocheck = menucheckList.get(j);
					Integer menucheck= menuinfocheck.getInt("menuid");
					if(menuall == menucheck){
						menuinfoall.setIscheck(true);
						break;
					}else{
						menuinfoall.setIscheck(false);
					}
				}
				list.add(menuinfoall);
			}
			setAttr("menuList", list);
			setAttr("roleid", roleid);
			render("/admin/system_user/edit_privilege.html");
		}
		
	}
	
	/**
	 * 获取大区List
	 */
	public void getAreaList(){
		List<Syscode>  areaList = Syscode.dao.areaList();
		setAttr("areaList", areaList);
	}
	
	public void getAreaAddress(){
		
		Integer uid = getSessionAttr("userid");
		Userinfo userinfo = Userinfo.dao.findById(uid);
		String areaId = userinfo.getStr("area");
		String provinceId = userinfo.getStr("province");
		String cityId = userinfo.getStr("city");
		String countiesId = userinfo.getStr("counties");
		
		setAttr("areaId", areaId);
		setAttr("provinceId", provinceId);
		setAttr("cityId", cityId);
		setAttr("countiesId", countiesId);
		
	}
	
	public String expUserinfo(){
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
			
			List<Userinfo> userlist = Userinfo.dao.userinfoList();
			// 获取设备运营状态详情
			
			exportUserinfodownload(wb,userlist);
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
	
	
	
	private void exportUserinfodownload(HSSFWorkbook wb, List<Userinfo> beanList) {
		HSSFRow row;
		HSSFCell cell;
		HSSFSheet sheet = wb.getSheetAt(0);
		
		//处理表头
		row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("用户名");
		cell = row.createCell(1);
		cell.setCellValue("角色");
		cell = row.createCell(2);
		cell.setCellValue("手机号码");
		cell = row.createCell(3);
		cell.setCellValue("创建时间");
		
		for (int i = 0; i < beanList.size(); i++) {
			row = sheet.createRow(i+1);
			cell = row.createCell(0);
			cell.setCellValue(beanList.get(i).getStr("loginname")==null?"未知":beanList.get(i).getStr("loginname"));
			cell = row.createCell(1);
			cell.setCellValue(beanList.get(i).getStr("rolename")==null?"未知":beanList.get(i).getStr("rolename"));
			cell = row.createCell(2);
			cell.setCellValue(beanList.get(i).getStr("phone")==null?"未知":beanList.get(i).getStr("phone"));
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

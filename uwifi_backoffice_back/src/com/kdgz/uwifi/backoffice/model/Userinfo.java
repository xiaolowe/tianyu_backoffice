package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.kdgz.uwifi.backoffice.constant.Constants;

@SuppressWarnings("serial")
public class Userinfo extends Model<Userinfo> {

	/**
	 * 
	 */
	public static final Userinfo dao = new Userinfo();

	/**
	 * 获取用户信息
	 * 
	 * @param username
	 * @return
	 */
	public Userinfo getUserInfo(String username) {

		return dao.findFirst(
				"select a.*, b.rolename from userinfo a, roleinfo b where a.rolecode = b.id and loginname = ? and  delflag = 0 ",
				new Object[] { username });

	}
	
	/**
	 * 获取用户id信息
	 * 
	 * @param username
	 * @return
	 */
	public Userinfo getUserIdByInvitecode(String invitecode) {

		return dao.findFirst(
				"select a.id from userinfo a where invitecode = ? and  delflag = 0 ",
				new Object[] { invitecode });

	}

	/**
	 * 获取用户列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Userinfo> paginate(int pageNumber, int pageSize, Integer pid, Integer rolecode, String invitecode, String loginname) {
		StringBuffer sql = new StringBuffer();
		sql.append("from userinfo a, roleinfo b where  a.rolecode = b.id");
		
		if(!rolecode.equals(Constants.ROLE_ADMIN) && pid != null && pid > 0){
			sql.append(" and  a.pid = " +pid );
		}
		if(StrKit.notBlank(loginname)){
			sql.append(" and a.loginname like  '%"+loginname+"%' ");
		}
		sql.append(" and a.delflag = 0 order by  a.addtime desc");
		
		Page<Userinfo> sPage = paginate(pageNumber, pageSize, "select a.id, a.loginname, a.phone, a.rolecode, " +
				"a.area, a.province, a.city, a.counties," +
				"a.status, a.addtime, a.updatetime, b.rolename," +
				"(select b.codename from syscode b where b.dimcode = 'area' and  a.area = b.codevalue) areaName," +
				"(select b.regionname from areainfo b where a.province=b.regioncode ) provinceName,"+
				"(select b.regionname from areainfo b where a.city=b.regioncode ) cityName,"+
				"(select b.regionname from areainfo b where a.counties=b.regioncode ) countiesName",
				sql.toString());
		
		return sPage;
	}
	
	/**
	 * 获取用户列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public List<Userinfo> userinfoList() {
		StringBuffer sql = new StringBuffer();
		sql.append(" from userinfo a, roleinfo b where  a.rolecode = b.id");
		
		sql.append(" and a.delflag = 0 order by  a.addtime desc");
		
		List<Userinfo> list =  dao.find("select a.id, a.loginname, a.phone, a.rolecode, " +
				"a.area, a.province, a.city, a.counties," +
				"a.status, a.addtime, a.updatetime, b.rolename," +
				"(select b.codename from syscode b where b.dimcode = 'area' and  a.area = b.codevalue) areaName," +
				"(select b.regionname from areainfo b where a.province=b.regioncode) provinceName,"+
				"(select b.regionname from areainfo b where a.city=b.regioncode) cityName,"+
				"(select b.regionname from areainfo b where a.counties=b.regioncode) countiesName" +
				sql.toString());
		
		return list;
	}
	
	
	/**
	 * 删除用户
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteUser(int id) {

		return dao.deleteById(id);
	}
	
	/**
	 * 验证是否已经存在的用户
	 * 
	 * @param whtype
	 * @param controltype
	 * @param businessid
	 * @return
	 */
	public long countExistUser(String loginname) {

		long count = Db
				.queryLong(
						"SELECT count(1) From userinfo where loginname = ? and delflag = 0 ",
						new Object[] { loginname });

		return count;
	}
	
	
	/**
	 * 验证是否已经存在的邀请码
	 * 
	 * @return
	 */
	public long countExistInvitecode(String invitecode) {

		long count = Db
				.queryLong(
						"SELECT count(1) From userinfo where invitecode = ? and delflag = 0 ",
						new Object[] { invitecode });

		return count;
	}
	
	/**
	 * 编辑验证是否已经存在的邀请码
	 * 
	 * @return
	 */
	public long countExistInvitecodeedit(int id, String invitecode) {

		long count = Db
				.queryLong(
						"SELECT count(1) From userinfo where invitecode = ? and id <> ? and delflag = 0 ",
						new Object[] { invitecode, id });

		return count;
	}
	
	

}

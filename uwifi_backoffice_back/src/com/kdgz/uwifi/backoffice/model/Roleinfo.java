package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.kdgz.uwifi.backoffice.constant.Constants;

@SuppressWarnings("serial")
public class Roleinfo extends Model<Roleinfo>{

	public static final Roleinfo dao = new Roleinfo();
	
	/**
	 * 通过用户id获取角色信息
	 * @return
	 */
	public Roleinfo selectRoleinfoByUserId(Integer userid){
		
		Roleinfo roleinfo = dao.findFirst("select a.loginname, b.userid, b.roleid, c.rolename from userinfo a, userrole b, roleinfo c" +
				" where a.id = b.userid and b.roleid = c.id and a.id = "+userid);
		return roleinfo;
	}
	/**
	 * 获取角色列表信息
	 * @return
	 */
	public List<Roleinfo> selectRoleinfoList(Integer rolecode){
		
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id, a.rolename from roleinfo a where 1=1 ");
		if(rolecode != null){
			if(rolecode.equals(Constants.ROLE_ADMIN)){
				sql.append(" and a.id in (2,3,4,5)");
			}
			if(rolecode.equals(Constants.ROLE_AREA)){
				sql.append(" and a.id in (3,4,5)");
			}
			if(rolecode.equals(Constants.ROLE_PROVINCE)){
				sql.append(" and a.id in (4,5)");
			}
			if(rolecode.equals(Constants.ROLE_CITY)){
				sql.append(" and a.id in (5)");
			}
			if(rolecode.equals(Constants.ROLE_COUNTIES)){
				sql.append(" and a.id in (0)");
			}
		}
		
		List<Roleinfo> list = dao.find(sql.toString());
		return list;
	}
	
	/**
	 * 获取角色列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Roleinfo> paginate(int pageNumber, int pageSize) {
		StringBuffer sql = new StringBuffer();
		sql.append("from roleinfo a, menuinfo b, rolemenu c  where a.id = c.roleid and b.id = c.menuid and a.id <> 1 group by c.roleid");
		
		Page<Roleinfo> sPage = paginate(pageNumber, pageSize, "select c.roleid, a.rolename, a.privilege,  group_concat(b.menuname order by b.id) menuname",
				sql.toString());
		
		
		return sPage;
	}

}

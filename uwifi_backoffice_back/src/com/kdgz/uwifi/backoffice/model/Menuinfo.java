package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.kdgz.uwifi.backoffice.constant.Constants;

@SuppressWarnings("serial")
public class Menuinfo extends Model<Menuinfo>{

	public static final Menuinfo dao = new Menuinfo();
	
	private boolean ischeck;
	
	public boolean isIscheck() {
		return ischeck;
	}

	public void setIscheck(boolean ischeck) {
		this.ischeck = ischeck;
	}
	
	/**
	 * 通过角色id获取菜单信息
	 * 过滤不同权限的菜单信息
	 * @param roleid
	 * @return
	 */
	public List<Menuinfo> selectMenuinfoByRole(Integer roleid){
		
		StringBuffer sql = new StringBuffer();
			sql.append("select a.rolename, b.roleid, b.menuid, c.menuname, c.link from roleinfo a, rolemenu b, menuinfo c" +
				" where a.id = b.roleid and b.menuid = c.id");
		if(roleid != null && roleid.equals(Constants.ROLE_ADMIN)){
			sql.append(" and c.status != 1 and b.roleid = "+roleid);
		}
		if(roleid != null && !roleid.equals(Constants.ROLE_ADMIN)){
			sql.append(" and c.status != 0 and b.roleid = "+roleid);
		}
		
		List<Menuinfo> menuinfo = dao.find(sql.toString());
		return menuinfo;
	
	}

	/**
	 * 通过角色id获取菜单信息
	 * @param roleid
	 * @return
	 */
	public List<Menuinfo> selectMenuinfoByRoleId(Integer roleid){
		
		List<Menuinfo> menuinfo = dao.find("select a.rolename, b.roleid, b.menuid, c.menuname, c.link from roleinfo a, rolemenu b, menuinfo c" +
				" where a.id = b.roleid and b.menuid = c.id and b.roleid = "+roleid);
		return menuinfo;
	
	}
	/**
	 * 获取普通用户权限菜单
	 * @return
	 */
	public List<Menuinfo> selectMenuinfoList(){
		
		List<Menuinfo> list = dao.find("select * from menuinfo where status != 0");
		return list;
	}
}

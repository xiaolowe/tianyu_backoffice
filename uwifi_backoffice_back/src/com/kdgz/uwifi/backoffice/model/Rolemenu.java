package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Rolemenu extends Model<Rolemenu>{

	public static final Rolemenu dao = new Rolemenu();
	
	public List<Rolemenu> selectRolemenuByRoleId(Integer roleid){
		
		List<Rolemenu> list = dao.find("select * from rolemenu where roleid = ?", new Object[] { roleid } );
		return list;
	}
	

}

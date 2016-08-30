package com.kdgz.uwifi.backoffice.model;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Userrole extends Model<Userrole>{

	public static final Userrole dao = new Userrole();
	
	/**
	 * 查找Acauth信息通过ACId
	 * @param acid
	 * @return
	 */
	public Userrole selectAcauthByAcId(String acid){
		
		Userrole acauth = dao.findFirst("select * from acauth where acid = '"+acid+"' ");
		return acauth;
	}
}

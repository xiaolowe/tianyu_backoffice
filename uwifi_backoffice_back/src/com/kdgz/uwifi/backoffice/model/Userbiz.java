package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Userbiz extends Model<Userbiz>{

	public static final Userbiz dao = new Userbiz();
	
	/**
	 * 通过用户id获取该用户所拥有的商家
	 * 通过用户id获取该用户的下级用户的商家
	 * @param userid
	 */
	public List<Userbiz> selectBusinessByUid(Integer userid){
		
		StringBuilder sb = new StringBuilder();
		
		List<Userinfo> list = Userinfo.dao.find("select a.id from userinfo a where a.pid = "+userid);
		for(Userinfo userinfo : list){
			sb.append(userinfo.getInt("id"));
			sb.append(",");
		}
		sb.append(userid);
		String uidStr = sb.toString();
		List<Userbiz>  userbiz = dao.find("select * from userbiz where  userid in ("+uidStr+") " );
		return userbiz;
	}
}

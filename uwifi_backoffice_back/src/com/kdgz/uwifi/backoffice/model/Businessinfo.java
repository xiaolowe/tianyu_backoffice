package com.kdgz.uwifi.backoffice.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.kdgz.uwifi.backoffice.constant.Constants;

@SuppressWarnings("serial")
public class Businessinfo extends Model<Businessinfo> {

	/**
	 * 
	 */
	public static final Businessinfo dao = new Businessinfo();

	/**
	 * 获取列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> pageBusinessinfo(int pageNumber, int pageSize,
			String busname, String businessid) {
		StringBuilder selectSql = new StringBuilder();
		StringBuilder whereSql = new StringBuilder();

		selectSql
				.append("select *, (select count(1) from acinfo where businessid = b.id) as count");
		whereSql.append("from businessinfo b where 1=1 ");
		
		if (StrKit.notBlank(busname)) {
			whereSql.append(" and b.busname like '%" + busname + "%' ");
		}
		if (StringUtils.isBlank(businessid)) {
			whereSql.append(" and b.id = 0 ");
		} else {
			whereSql.append(" and b.id in (" + businessid + ")");
		}
		whereSql.append(" order by b.id desc");
		Page<Record> businessinfo = Db.paginate(pageNumber, pageSize,
				selectSql.toString(), whereSql.toString());
		
		//added by yan 
		if(businessinfo.getTotalPage()>0 && pageNumber>businessinfo.getTotalPage()){
			businessinfo = Db.paginate(1, pageSize,
					selectSql.toString(), whereSql.toString());
		}
		return businessinfo;
	}
	
	public Page<Record> pageBusinessinfoWithArea(int pageNumber, int pageSize,
			String busname, String businessid, String areaId, 
			String provinceId, String cityId, String countiesId) {
		
		StringBuilder selectSql = new StringBuilder();
		StringBuilder whereSql = new StringBuilder();

		selectSql
				.append("select b.*, c.userid, (select count(1) from acinfo where businessid = b.id) as count");
		whereSql.append(" from businessinfo b, userbiz c where b.id = c.businessid");
		
		if (StrKit.notBlank(busname)) {
			whereSql.append(" and b.busname like '%" + busname + "%' ");
		}
		if (StringUtils.isBlank(businessid)) {
			whereSql.append(" and b.id = 0 ");
		} else {
			whereSql.append(" and b.id in (" + businessid + ")");
		}
		if (StrKit.notBlank(areaId)) {
			whereSql.append(" and b.area = "+areaId);
		}
		if (StrKit.notBlank(provinceId)) {
			whereSql.append(" and b.province = "+provinceId);
		}
		if (StrKit.notBlank(cityId)) {
			whereSql.append(" and b.city like '%" + cityId + "%' ");
		}
		if (StrKit.notBlank(countiesId)) {
			whereSql.append(" and b.counties like '%" + countiesId + "%' ");
		}
		
		
		whereSql.append(" order by b.id desc");
		Page<Record> businessinfo = Db.paginate(pageNumber, pageSize,
 				selectSql.toString(), whereSql.toString());
		//added by yan 
		if(businessinfo.getTotalPage()>0 && pageNumber>businessinfo.getTotalPage()){
			businessinfo = Db.paginate(1, pageSize,
					selectSql.toString(), whereSql.toString());
		}
		return businessinfo;
	}
	
	/**
	 * 获取导入下载商家列表
	 */
	public List<Businessinfo> businessinfoList( String businessid ) {

		List<Businessinfo> list = dao.find("select * from businessinfo where id in (" + businessid + ") ");
		return list;
		
	}

	/**
	 * 通过id查询商家信息
	 * 
	 * @param id
	 * @return
	 */
	public List<Businessinfo> selectBussinessinfoList(String businessid) {
		List<Businessinfo> list = new ArrayList<Businessinfo>();
		
		if(StringUtils.isEmpty(businessid)) {
			return list;
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("select * from businessinfo where 1=1 ");
		if (StrKit.notBlank(businessid)) {
			sql.append(" and id in (" + businessid + ")");
		}
		sql.append(" order by id desc");
		list = dao.find(sql.toString());
		return list;

	}

	/**
	 * 根据店铺名称查找店铺
	 * 
	 * @param acid
	 * @return
	 */
	public Businessinfo selectBusinessinfoByName(String name, int id) {

		StringBuilder sb = new StringBuilder();
		sb.append("select * from businessinfo where busname = '" + name + "' ");

		if (id != 0) {
			sb.append(" and id <> '" + id + "' ");
		}

		Businessinfo businfo = dao
				.findFirst(sb.toString());
		return businfo;

	}
	
	/**
	 * 删除店铺配置信息
	 * @param id
	 */
	public void deleteBusinessInfo(int busId, int userid) {
		
		Businessinfo businessinfo = Businessinfo.dao.findById(busId);
		
		if(businessinfo != null) {
			businessinfo.delete();
			//Record userbiz = Db.findFirst("select * from userbiz where businessid = ? and userid = ?", busId, userid);
			
			//该方法删除当前用户下的关联表，若管理员删除其他用户下的店铺，则其他用户下的关联表无法删除。
			//if(userbiz != null) {
			//	Db.delete("userbiz", userbiz);
			//}
			
			//改为根据店铺id和页面传来的userid删除
			Userbiz userBiz = Userbiz.dao.findFirst(" select * from userbiz where businessid = ?", busId);
			if(userBiz != null) {
				userBiz.delete();
			}
			
			List<Record> acinfos = Db.find("select * from acinfo where businessid = ?", busId);
			
			for (Record ac : acinfos) {
				Db.delete("acinfo", ac);
			}
			
			Record acauth = Db.findFirst("select * from acauth where businessid = ?", busId);
			
			if(acauth != null) {
				Db.delete("acauth", acauth);
			}
			
			Record authweixinconfig = Db.findFirst("select * from authweixinconfig where businessid = ?", busId);
			
			if(authweixinconfig != null)
			Db.delete("authweixinconfig", authweixinconfig);
			
			Record businesstemplet = Db.findFirst("select * from businesstemplet where businessid = ?", busId);
			
			if(businesstemplet != null)
			Db.delete("businesstemplet", businesstemplet);
			
			Record bwlist = Db.findFirst("select * from bwlist where businessid = ?", busId);
			
			if(bwlist != null)
			Db.delete("bwlist", bwlist);
			
			Record smsacconfig = Db.findFirst("select * from smsacconfig where businessid = ?", busId);
			
			if(smsacconfig != null)
			Db.delete("smsacconfig", smsacconfig);
			
			List<Record> templetpages = Db.find("select * from templetpage where businessid = ?", busId);
			
			for (Record record : templetpages) {
				record.set("delflag", Constants.DELFLAG_DELETED);
				
				Db.update("templetpage", record);
			}
		}
		
	}

	/**
	 * 获取用户的店铺数量
	 * 
	 * @param userId
	 * @return
	 */
	public long countShopNum(int userId, String userRole) {

		long count = 0;

		if (Constants.ROLE_ADMIN.equals(userRole)) {
			count = Db.queryLong("Select count(1) from businessinfo");
		} else {
			count = Db
					.queryLong(
							"Select count(1) from userbiz where userid = ? and businessid <> '0'",
							new int[] { userId });
		}

		return count;
	}

	/**
	 * 获取用户的路由器数量
	 * 
	 * @param userId
	 * @return
	 */
	public long countWiFiDeviceNum(int userId, Integer userRole) {

		long count = 0;

		if (Constants.ROLE_ADMIN.equals(userRole)) {
			count = Db
					.queryLong("Select count(1) from acinfo INNER join businessinfo on acinfo.businessid = businessinfo.id");
		} else {
			count = Db
					.queryLong(
							"SELECT count(1) AS count FROM acinfo INNER join businessinfo on acinfo.businessid = businessinfo.id INNER JOIN userbiz ON acinfo.businessid = userbiz.businessid and userbiz.userid = ? and userbiz.businessid <> '0'",
							new Object[] { userId });
		}

		return count;
	}

	/**
	 * 获取用户的在线路由器数量
	 * 
	 * @param userId
	 * @return
	 */
	public long countOnlineDeviceNum(Integer userRole, String businessids) {

		long count = 0;

		if (Constants.ROLE_ADMIN.equals(userRole)) {
			count = Db
					.queryLong("select count(1) from acstatetemp where unix_timestamp(now()) - unix_timestamp(lastpingtime) < 15*60");
		} else {
			count = Db
					.queryLong("select count(1) from acstatetemp where unix_timestamp(now()) - unix_timestamp(lastpingtime) < 15*60 "
							+ "and acid in (select acid from acinfo where acinfo.businessid in("
							+ businessids + "))");
		}

		return count;
	}

	/**
	 * 获取bussinessid
	 * 
	 * @return
	 */
	public List<Businessinfo> getBussinessIdList() {
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from businessinfo ");
		List<Businessinfo> list = dao.find(sql.toString());
		return list;

	}

	/**
	 * 最新有路由的商家信息
	 * 
	 * @param id
	 * @return
	 */
	public List<Businessinfo> selectNewstBussinessinfoList(String businessid) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from businessinfo B where 1=1 ");
		if (StrKit.notBlank(businessid)) {
			sql.append(" and id in (" + businessid + ")");
		}
		sql.append(" and (SELECT count(1) from acinfo A where A.businessid = B.id) > 0");

		List<Businessinfo> list = dao.find(sql.toString());
		return list;
	}
}

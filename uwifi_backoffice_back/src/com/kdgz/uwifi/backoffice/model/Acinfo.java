package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.kdgz.uwifi.backoffice.constant.Constants;

@SuppressWarnings("serial")
public class Acinfo extends Model<Acinfo> {

	public static final Acinfo dao = new Acinfo();

	/**
	 * 获取列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Acinfo> pageinfoList(int pageNumber, int pageSize,
			String busname, String construtname, String businessid) {
		StringBuffer sql = new StringBuffer();
		sql.append("from acinfo a, businessinfo b, construtinfo c, acbrand d, actypeinfo e ");
		sql.append("where a.businessid = b.id and a.construtid = c.id and a.acbrandid = d.id and a.actypeid = e.id ");
		if (StrKit.notBlank(busname)) {
			sql.append(" and b.busname like '%" + busname + "%' ");
		}
		if (StrKit.notBlank(construtname)) {
			sql.append(" and c.construtname like '%" + construtname + "%'");
		}
		if (StrKit.notBlank(businessid)) {
			sql.append(" and b.id in (" + businessid + ")");
		}
		sql.append(" order by a.id desc");
		Page<Acinfo> acinfo = paginate(pageNumber, pageSize,
				"select a.id, a.acid, a.businessid, a.acos, a.addtime, d.acbrandname, e.typename,"
						+ " a.eqptssid, c.construtname, b.busname",
				sql.toString());
		return acinfo;
	}
	
	/**
	 * 获取导出路由列表
	 */
	public List<Acinfo> acinfoListExport(String businessid) {
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id, a.acid, a.businessid, a.acos, a.addtime, d.acbrandname, e.typename,"
						+ " a.eqptssid, b.busname, f.userid");
		sql.append(" from acinfo a, businessinfo b, acbrand d, actypeinfo e, userbiz f ");
		sql.append(" where a.businessid = b.id and a.acbrandid = d.id and a.actypeid = e.id and b.id = f.businessid ");
		if (StrKit.notBlank(businessid)) {
			sql.append(" and b.id in (" + businessid + ")");
		} 
		List<Acinfo> list = dao.find(sql.toString());
		return list;
	}

	/**
	 * 获取我的店铺路由器列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Acinfo> pageAcInfoList(int pageNumber, int pageSize,
			String busname, String acid, String businessid, String areaId, 
			String provinceId, String cityId, String countiesId) {
		StringBuffer sql = new StringBuffer();
		sql.append("from acinfo a, businessinfo b, acbrand d, actypeinfo e, userbiz f ");
		sql.append("where a.businessid = b.id and a.acbrandid = d.id and a.actypeid = e.id and b.id = f.businessid ");
		if (StrKit.notBlank(busname)) {
			sql.append(" and b.busname like '%" + busname + "%' ");
		}
		if (StrKit.notBlank(acid)) {
			sql.append(" and a.acid like '%" + acid + "%'");
		}
		if (StrKit.notBlank(businessid)) {
			sql.append(" and b.id in (" + businessid + ")");
		} else {
			sql.append(" and b.id = 0");
		}
		if (StrKit.notBlank(areaId)) {
			sql.append(" and b.area = "+areaId);
		}
		if (StrKit.notBlank(provinceId)) {
			sql.append(" and b.province = "+provinceId);
		}
		if (StrKit.notBlank(cityId)) {
			sql.append(" and b.city like '%" + provinceId + "%' ");
		}
		if (StrKit.notBlank(countiesId)) {
			sql.append(" and b.counties like '%" + cityId + "%' ");
		}
		
		sql.append(" order by a.id desc");
		Page<Acinfo> acinfo = paginate(pageNumber, pageSize,
				"select a.id, a.acid, a.businessid, a.acos, a.addtime, d.acbrandname, e.typename,"
						+ " a.eqptssid, b.busname, f.userid", sql.toString());
		
		//added by yan 
		if(acinfo.getTotalPage()>0 && pageNumber>acinfo.getTotalPage()){
			acinfo = paginate(1, pageSize,
					"select a.id, a.acid, a.businessid, a.acos, a.addtime,  d.acbrandname, e.typename,"
							+ " a.eqptssid, b.busname, f.userid", sql.toString());
		}
		return acinfo;
	}
	
	public Page<Acinfo> pageAcInfoListWithArea(int pageNumber, int pageSize,
			String busname, String acid, String businessid, String areaId, 
			String provinceId, String cityId, String countiesId, Integer userRole, String flage) {
		StringBuffer sql = new StringBuffer();
		sql.append("from acinfo a, businessinfo b, acbrand d, actypeinfo e ");
		sql.append("where a.businessid = b.id and a.acbrandid = d.id and a.actypeid = e.id ");
		if (StrKit.notBlank(busname)) {
			sql.append(" and b.busname like '%" + busname + "%' ");
		}
		if (StrKit.notBlank(acid)) {
			sql.append(" and a.acid like '%" + acid + "%'");
		}
		if (StrKit.notBlank(businessid)) {
			sql.append(" and b.id in (" + businessid + ")");
		} else {
			sql.append(" and b.id = 0");
		}
		if("sessionFlage".equals(flage)){
			if(areaId.length() > 0){
				sql.append(" and b.area = '"+areaId+"'");
			}
			if(provinceId.length() > 0){
				sql.append(" and b.province = '"+provinceId+"'");
			}
			if(cityId.length() > 0){
				sql.append(" and b.city = '"+cityId+"'");
			}
			if(countiesId.length() > 0){
				sql.append(" and b.counties = '"+countiesId+"'");
			}
		}else{
			if(Constants.ROLE_AREA.equals(userRole)){//大区用户：可查询大区下所有店铺
				if(areaId.length() > 0){
					sql.append(" and b.area = '"+areaId+"'");
				}
			}else if(Constants.ROLE_PROVINCE.equals(userRole)){//省级用户：可查询该生下所有市，区/县店铺
				if(provinceId.length() > 0){
					sql.append(" and b.province = '"+provinceId+"'");
				}
			}else if(Constants.ROLE_CITY.equals(userRole)){//市级用户：可查询该市下所有区/县店铺
				if(cityId.length() > 0){
					sql.append(" and b.city = '"+cityId+"'");
				}
			}else if(Constants.ROLE_COUNTIES.equals(userRole)){//区/县级用户
				if(countiesId.length() > 0){
					sql.append(" and b.counties = '"+countiesId+"'");
				}
			}
		}
		sql.append(" order by a.id desc");
		Page<Acinfo> acinfo = paginate(pageNumber, pageSize,
				"select a.id, a.acid, a.businessid, a.acos,a.addtime,  d.acbrandname, e.typename,"
						+ " a.eqptssid, b.busname", sql.toString());
		
		//added by yan 
		if(acinfo.getTotalPage()>0 && pageNumber>acinfo.getTotalPage()){
			acinfo = paginate(1, pageSize,
					"select a.id, a.acid, a.businessid, a.acos,a.addtime,  d.acbrandname, e.typename,"
							+ " a.eqptssid, b.busname", sql.toString());
		}
		return acinfo;
	}

	/**
	 * 获取Ac信息表，此方法只用到获取ACId
	 * 
	 * @return
	 */
	public List<Acinfo> selectAcidList(String businessid) {

		StringBuffer sql = new StringBuffer();
		sql.append("select a.id, a.acid from acinfo a ");
		if (StrKit.notBlank(businessid)) {
			sql.append(" where 1=1 and a.businessid in (" + businessid + ")");
		}else{
			sql.append(" where 1=2 ");
		}
		sql.append(" order by a.id desc");
		List<Acinfo> list = dao.find(sql.toString());
		return list;

	}

	/**
	 * 查找ac信息通过ACId
	 * 
	 * @param acid
	 * @return
	 */
	public Acinfo selectAcinfoByAcId(String acid) {

		Acinfo acinfo = dao.findFirst("select * from acinfo where acid = '"
				+ acid + "' ");
		return acinfo;

	}

	/**
	 * 判断acid唯一性
	 */
	public Acinfo selectAcinfoByIdAndAcId(int id, String acid) {

		Acinfo acinfo = dao.findFirst("select * from acinfo where id <> '" + id
				+ "' and  acid = '" + acid + "' ");
		return acinfo;
	}

	/**
	 * 获取店铺的路由器数量
	 * 
	 * @param userId
	 * @return
	 */
	public long countShopDeviceNum(int busId) {

		long count = Db
				.queryLong(
						"SELECT count(1) AS count FROM acinfo WHERE acinfo.businessid = ?",
						new int[] { busId });

		return count;
	}
	
	/**
	 * 获取一个路由器acid
	 * @param businessId
	 * @return
	 * @author jason
	 */
	public Acinfo getAcIdByBusid(Integer businessId) {

		Acinfo acinfo = dao.findFirst("select * from acinfo where businessid = "+ businessId + " limit 1 ");
		return acinfo;

	}

}

package com.kdgz.uwifi.backoffice.model;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Areainfo extends Model<Areainfo> {

	public static final Areainfo dao = new Areainfo();
	
	public List<Areainfo> selectProvinceList(String areaid) {

		StringBuffer sql = new StringBuffer();
		sql.append("select a.regioncode,a.regionlevel, a.regionname from areainfo a ");
		if (StrKit.notBlank(areaid) && !areaid.equals("undefined")) {
			sql.append(" where 1=1 and a.area = " + areaid);
		}else{
			sql.append(" where 1=2 ");
		}
		sql.append(" order by a.regioncode");
		List<Areainfo> list = dao.find(sql.toString());
		return list;

	}
	
	public List<Areainfo> selectCountiesList(String cityid) {

		StringBuffer sql = new StringBuffer();
		sql.append("select a.regioncode, a.regionname from areainfo a ");
		if (StrKit.notBlank(cityid)) {
			sql.append(" where 1=1 and a.regioncode like '"+cityid+"__'");
		}else{
			sql.append(" where 1=2 ");
		}
		sql.append(" order by a.regioncode");
		List<Areainfo> list = dao.find(sql.toString());
		return list;

	}
	public List<Areainfo> selectCityList(String provinceid) {

		StringBuffer sql = new StringBuffer();
		sql.append("select a.regioncode, a.regionname from areainfo a ");
		if (StrKit.notBlank(provinceid)) {
			sql.append(" where 1=1 and a.regioncode like '"+provinceid+"__'");
		}else{
			sql.append(" where 1=2 ");
		}
		sql.append(" order by a.regioncode");
		List<Areainfo> list = dao.find(sql.toString());
		return list;

	}
	
	public List<Areainfo> getProvinceList(String areaid) {
		List<Areainfo> list = new ArrayList<Areainfo>();
		
		StringBuffer sql = new StringBuffer();
		sql.append("select a.* from areainfo a where a.regionlevel=1  and area="+areaid);
		sql.append(" order by a.regioncode ");
		list = dao.find(sql.toString());
		return list;

	}
	
	public List<Areainfo> getCityAndCountiesList(String provincebm) {
		List<Areainfo> list = new ArrayList<Areainfo>();
		
		StringBuffer sql = new StringBuffer();
		sql.append("select a.* from areainfo a where 1=1 ");
		if(provincebm.length()==2){
			sql.append(" and a.regionlevel=2 ");
		}else{
			sql.append(" and a.regionlevel=3 ");
		}
		sql.append(" and a.regioncode like '"+provincebm+"__'");
		sql.append(" order by a.regioncode ");
		list = dao.find(sql.toString());
		return list;

	}
	
}

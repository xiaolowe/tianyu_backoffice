package com.kdgz.uwifi.backoffice.constant;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 定数类
 * 
 * @author lanbo
 * 
 */
public final class Constants {

	// 操作成功
	public static final String OPERATION_SUCCEED = "1";

	// 操作失败
	public static final String OPERATION_FAILED = "0";

	// 角色：超级管理员
	public static final Integer ROLE_ADMIN = 1;

	// 角色：大区管理员
	public static final Integer ROLE_AREA = 2;
	
	//角色：省级管理员
	public static final Integer ROLE_PROVINCE = 3;
	
	//角色：市级管理员
	public static final Integer ROLE_CITY = 4;
	
	//角色：区县级管理员
	public static final Integer ROLE_COUNTIES = 5;
	

	public static final String DATETIME_FORMAT_YMD = "yyyy-MM-dd";

	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String DATETIME_FORMAT_SHORT = "yyyyMMddHHmm";

	// admin账户名称
	public static final String ADMIN_USERNAME = "admin";

	// 图片存储的文件夹
	public static final Map<String, String> IMAGE_DIRECTORY = new LinkedHashMap<String, String>();

	static {
		IMAGE_DIRECTORY.put("100", "module_cover");
		IMAGE_DIRECTORY.put("200", "module_list_cover");
		IMAGE_DIRECTORY.put("300", "module_detail_picture");
	}

	// 上传文件夹
	public static final String UPLOADED_DIRECTORY = "upload";

	// DDWRT
	public static final String AC_TYPE_DDWRT = "1";

	// OPENWRT
	public static final String AC_TYPE_OPENWRT = "2";

	// WebService调用返回码:成功
	public static final String WS_SUCCESS = "100";

	// WebService调用返回码:没有数据
	public static final String WS_NO_DATA = "001";

	// WebService调用返回码:Accesstoken Illegal
	public static final String WS_ACCESSTOKEN_ILLEGAL = "002";

	// WebService调用返回码:Unknown Error
	public static final String WS_UNKNOWN_ERROR = "003";

	// WebService调用ACCESSTOKEN
	public static final String WS_ACCESSTOKEN = "ecw9F71uaP";

	// WebService Type 全部设备
	public static final String WS_DEVICE_TYPE_ALL = "1";

	// WebService Type 单个设备
	public static final String WS_DEVICE_TYPE_ONE = "0";

	// 黑名单
	public static final int WHTYPE_WHITE = 0;

	// 白名单
	public static final int WHTYPE_BLACK = 1;

	// 名单类型：域名
	public static final int CTLTYPE_DOMAIN = 1;

	// 名单类型：IP
	public static final int CTLTYPE_IP = 2;

	// 名单类型：MAC
	public static final int CTLTYPE_MAC = 3;

	// 认证方式:短信
	public static final int AUTHTYPE_SMS = 1;

	// 认证方式:微信
	public static final int AUTHTYPE_WEIXIN = 2;

	// 认证方式:多认证
	public static final int AUTHTYPE_MULTI = 3;
	
	// 数据接口数量最大量限制
	public static final int DATA_LIMIT = 50;
	
	// 已删除
	public static final int DELFLAG_DELETED = 1;
	
	//保存默认的菜单id   0为数据库的默认数据
	public static final int DEFAULT_MENUID = 0;
	
	public static final int DEFAULT_SHOUYE = 1;
	public static final int DEFAULT_SHOP = 2;
	
	//默认认证页模板
	public static final int DEFAULT_TEMPTYPE = 1;
	
}

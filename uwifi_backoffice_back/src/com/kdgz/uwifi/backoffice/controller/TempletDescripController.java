package com.kdgz.uwifi.backoffice.controller;
import java.util.ResourceBundle;
import com.jfinal.aop.Before;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.ShopInterceptor;
import com.kdgz.uwifi.backoffice.model.Templetinfo;

@Before(ShopInterceptor.class)
public class TempletDescripController extends BaseController{
	ResourceBundle bundle = ResourceBundle.getBundle("common");

	public void index() {
		
		setAttr("tempManage", Templetinfo.dao.getTempletByTemptypeList(Constants.DEFAULT_TEMPTYPE));
		 flashRender("/admin/shop_manage/approvePage/templetDescription.html");
	}
	
	
	public void editTempletDescript(){
		
		Integer id = getParaToInt(0);
		Templetinfo templetinfo = Templetinfo.dao.findById(id);
		Data rst = new Data();
		
		if (getRequest().getMethod().equals("POST")) {
			
			Templetinfo param = getModel(Templetinfo.class);
			templetinfo.set("displayname", param.getStr("displayname"));
			templetinfo.set("remark", param.getStr("remark"));
			if(templetinfo.update()){
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("编辑模板成功！");
				setFlashData(rst);
				redirect("/templetdescrip");
			}
			
		}else{
			
			setAttr("templetinfo", templetinfo);
			render("/admin/shop_manage/approvePage/templetDescriptionEdit.html");
			
		}
		
		
	}
	
	
}

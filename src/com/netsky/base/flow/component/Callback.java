package com.netsky.base.flow.component;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.netsky.base.baseDao.Dao;
import com.netsky.base.baseObject.QueryBuilder;
import com.netsky.base.dataObjects.Tb12_opernode;
import com.netsky.base.dataObjects.Tb13_operrelation;
import com.netsky.base.dataObjects.Tb15_docflow;
import com.netsky.base.flow.FlowConstant;
import com.netsky.base.flow.trigger.DoTrigger;
import com.netsky.base.flow.utils.MapUtil;
import com.netsky.base.service.QueryService;
import com.netsky.base.service.SaveService;

/**
 * @description:
 * 收回文档
 * @class name:com.netsky.base.flow.component.Callback
 * @author wind Jan 20, 2010
 */
@Component
public class Callback {

	/**
	 * 数据对应操作类
	 */
	@Autowired
	private SaveService saveService;

	@Autowired
	private QueryService queryService;
	
	/**
	 * 事件处理转发类
	 */
	@Autowired(required=false)
	private DoTrigger doTrigger;
	
	
	/**
	 * 日志处理类
	 */
	private  Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 收回文档，并把收回文档事件转发出去
	 * @param paraMap
	 * @return
	 * @throws Exception ModelAndView
	 */
	
	public ModelAndView handleRequest(Map paraMap) throws Exception {
		
		Tb13_operrelation tb13= null;//发送relation
		Tb12_opernode tb12 = null; //节点信息
		Tb15_docflow tb15 = null;  //文档节点信息
		Long tb12_id = null;   //当前节点tb12_id
		
		//数据操作相关变量
		StringBuffer hsql = new StringBuffer();
		List tmpList = new LinkedList();
		QueryBuilder queryBuilder;
		
		/**
		 * 获取要更新的数据
		 */
		tb12_id = MapUtil.getLong(paraMap, "opernode_id");
		hsql.append(" from Tb13_operrelation tb13 where source_id = ? order by id desc ");
		tmpList = queryService.searchList(hsql.toString(), new Object[]{tb12_id});
		if(tmpList.size() > 0 ){
			tb13 = (Tb13_operrelation)tmpList.get(0);
		}
		tmpList.clear();
		
		Session session = null; // 事务相关
		Transaction tx = null;// 事务相关
		try {
			session = saveService.getHiberbateSession();
			tx = session.beginTransaction();
			tx.begin(); // 开始事务
			
			// ******************************删除发送目标节点信息***************************/
			tb12 = (Tb12_opernode)queryService.searchById(Tb12_opernode.class, tb13.getDest_id());
			//如果目标节点已受理，返回打开表单。
			if(!FlowConstant.NODE_STATUS_NEED.equals(tb12.getNode_status())) return new ModelAndView("redirect:"+FlowConstant.CTR_OPENFORM,paraMap);
			session.delete(tb12);
			hsql.delete(0, hsql.length());
			hsql.append("delete  from Tb15_docflow tb15 where opernode_id =  ");
			hsql.append(tb13.getDest_id());
			session.createQuery(hsql.toString()).executeUpdate();
			
			//*********************************更新当前节点状态********************************/
			//获得当前节点tb12,tb15
			tb12 = (Tb12_opernode)queryService.searchById(Tb12_opernode.class, tb13.getSource_id());
			hsql.delete(0, hsql.length());
			hsql.append(" from Tb15_docflow tb15 where opernode_id = ? and user_id = ?" );
			tmpList = queryService.searchList(hsql.toString(), new Object[]{tb12_id,MapUtil.getLong(paraMap, "user_id")});
			if(tmpList.size() > 0 ){
				tb15 = (Tb15_docflow)tmpList.get(0);
			}
			tmpList.clear();
			
			//更新当前节点
			tb12.setNode_status(FlowConstant.NODE_STATUS_WORK);
			session.saveOrUpdate(tb12);
			tb15.setOper_status("收回");
			tb15.setOper_user(MapUtil.getString(paraMap, "user_name"));
			tb15.setOper_time(new Date());
			tb15.setDoc_status(FlowConstant.NODE_STATUS_WORK);
			tb15.setWrite_limit(tb15.getHWrite_limit());

			session.saveOrUpdate(tb15);
			
			//删除TB13
			session.delete(tb13);

			session.flush();
			tx.commit(); // 提交事务;
		} catch (Exception e) {
			if (tx != null)
				tx.rollback(); // 回滚事务;
			log.error("收回文档出错", e);
			throw new Exception("收回文档出错",e);
		}finally{
			if(session != null){
				session.close();
			}
		}			


		
		//******************************调用触发器*******************************************/
		paraMap.put("even_id",tb15.getNode_id());
		paraMap.put("even_type",FlowConstant.EVEN_TYPE_SEND);
		
		if(doTrigger != null){
			doTrigger.findAndExcute(paraMap);
		}
		
		return new ModelAndView("redirect:"+FlowConstant.CTR_OPENFORM,paraMap);
	}
}
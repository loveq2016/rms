package com.rms.controller.infoManage;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.netsky.base.baseDao.Dao;
import com.netsky.base.utils.convertUtil;
import com.netsky.base.utils.DateGetUtil;
import com.netsky.base.service.QueryService;
import com.netsky.base.service.SaveService;
import com.rms.dataObjects.base.Tc10_hzdw_khpz;
import com.rms.dataObjects.wxdw.Tf15_khxwh;
import com.rms.dataObjects.wxdw.Tf19_khxx;

@Controller
public class Khxwh {
	/**
	 * 数据服务
	 */
	@Autowired
	private Dao dao;

	/**
	 * 查询服务
	 */
	@Autowired
	private QueryService queryService;

	/**
	 * 保存服务
	 */
	@Autowired
	private SaveService saveService;

	/**
	 * 日志处理类
	 */
	private Logger log = Logger.getLogger(this.getClass());
 

	/**
	 * 转到考核项维护页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 *             ModelAndView
	 */
	@RequestMapping("/infoManage/khxwh.do")
	public ModelAndView khunsert(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String orderField = convertUtil.toString(request
				.getParameter("orderField"), "lb");
		String orderDirection = convertUtil.toString(request
				.getParameter("orderDirection"), "asc");
		List<Tf15_khxwh> tf15List = (List<Tf15_khxwh>) queryService
				.searchList("from Tf15_khxwh order by " + orderField + " "
						+ orderDirection);
		request.setAttribute("tf15List", tf15List);
		request.setAttribute("sort", "ASC");
		return new ModelAndView("/WEB-INF/jsp/infoManage/khxwh.jsp");

	}

  
	/**
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping("/infoManage/khpzList.do")
	public ModelAndView khpz(HttpServletRequest request,
			HttpServletResponse response) {
		ModelMap modelMap = new ModelMap();
		String view = "/WEB-INF/jsp/infoManage/khpzList.jsp";
		List khpzList = null;
		StringBuffer HSql = new StringBuffer("");
		HSql.append("select khpz from Tc10_hzdw_khpz khpz where 1=1");
		HSql.append(" order by khpz.mc");
		khpzList = queryService.searchList(HSql.toString());
		modelMap.put("khpz_list", khpzList);
		return new ModelAndView(view, modelMap);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping("/infoManage/khpzEdit.do")
	public ModelAndView khpzEdit(HttpServletRequest request,
			HttpServletResponse response) {
		ModelMap modelMap = new ModelMap();
		String view = "/WEB-INF/jsp/infoManage/khpzEdit.jsp?a=b";
		StringBuffer HSql = new StringBuffer();
		List pzmxList = null;
		Long id = convertUtil.toLong(request.getParameter("id"));
		Tc10_hzdw_khpz khpz = (Tc10_hzdw_khpz) dao.getObject(
				Tc10_hzdw_khpz.class, id);
		if (khpz != null) {
			if (khpz.getXckhsj() == null && khpz.getZhkhsj() != null
					&& khpz.getJgts() != null) {
				khpz.setXckhsj(DateGetUtil.addDay(khpz.getZhkhsj(), khpz.getJgts().intValue()));
			}
		}
		if (id != null) {
			HSql.append("select pzmx from Tc11_khpzmx pzmx where 1=1");
			HSql.append(" and pzmx.kh_id=" + id);
			pzmxList = dao.search(HSql.toString());
		}
		modelMap.put("id", id);
		modelMap.put("khpz", khpz);
		modelMap.put("pzmxList", pzmxList);
		return new ModelAndView(view, modelMap);
	}

	/**
	 * 级联删除
	 * 异步
	 * @param request
	 * @param response
	 *            void
	 * @throws IOException
	 */
	@RequestMapping("/infoManage/ajaxKhpzDel.do")
	public void ajaxKhpzDel(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String kh_id = convertUtil.toString(request.getParameter("kh_id"), "");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		StringBuffer HSql = null;
		PrintWriter out = null;
		Session session = null;
		Transaction tx = null;
		if (!kh_id.equals("")) {
			session = saveService.getHiberbateSession();
			tx = session.beginTransaction();
			tx.begin();
			out = response.getWriter();
			HSql = new StringBuffer("");
			HSql.append("delete from Tc10_hzdw_khpz khpz where khpz.id="
					+ kh_id);
			try {
				session.createQuery(HSql.toString()).executeUpdate();
				HSql.delete(0, HSql.length());
				HSql.append("delete from  Tc11_khpzmx pzmx where pzmx.kh_id="
						+ kh_id);
				session.createQuery(HSql.toString()).executeUpdate();
				session.flush();
				tx.commit();

				out.print("{\"statusCode\":\"200\"," + "\"message\":\"删除成功!\","
						+ " \"navTabId\":\"khpzList\", "
						+ "\"forwardUrl\":\"infoManage/khpzList.do\","
						+ " \"callbackType\":\"forward\"}");
			} catch (RuntimeException e) {
				log.error(e.getMessage());
				tx.rollback();
				out.print("{\"statusCode\":\"300\"," + "\"message\":\"删除失败!\"}");
			} finally {
				session.close();
			}
		}
	}
	
	
	/**
	 * 定时调用
	 * @param request
	 * @param response void
	 * @throws IOException 
	 */
	@RequestMapping("/infoManage/khxUpdate.do")
	public void khxUpdate(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Long kh_id = convertUtil.toLong(request.getParameter("kh_id"), null);
		StringBuffer tc10_hql = new StringBuffer(
				"select hzdw_khpz from Tc10_hzdw_khpz hzdw_khpz where 1=1 ");
		StringBuffer tf19_hql = new StringBuffer("");
		StringBuffer hql = new StringBuffer();
		String msg="success";
		PrintWriter out=null;

		Tc10_hzdw_khpz hzdw_khpz = null;
		Tf19_khxx khxx = null;
		List hzdw_khpz_list = null;
		List khxxList = null;
		Session session = null;
		Transaction tx = null;

		if (kh_id == null)
			tc10_hql.append(" and trunc(hzdw_khpz.xckhsj)=trunc(sysdate)");
		else
			tc10_hql.append(" and hzdw_khpz.id=" + kh_id);
		hzdw_khpz_list = queryService.searchList(tc10_hql.toString());
		
		if (hzdw_khpz_list != null && hzdw_khpz_list.size() > 0) {
			out=response.getWriter();
			Date date = new Date();
			session=saveService.getHiberbateSession();
			tx=session.beginTransaction();
			tx.begin();
			Iterator it = hzdw_khpz_list.iterator();
			try {
				while (it.hasNext()) {
					hzdw_khpz = (Tc10_hzdw_khpz) it.next();
					hzdw_khpz.setXckhsj(DateGetUtil.addDay(date, hzdw_khpz.getJgts().intValue()));
					hzdw_khpz.setZhkhsj(date);
					session.saveOrUpdate(hzdw_khpz);
					
					khxx = new Tf19_khxx();
					khxx.setKhmc("考核[" + DateGetUtil.getYear() + "年"
							+ DateGetUtil.getMonth() + "月]");
					khxx.setKhkssj(date);
					khxx.setKhjssj(DateGetUtil.addDay(date, hzdw_khpz.getDfts()
							.intValue()));
					khxx.setKh_id(hzdw_khpz.getId());
					session.save(khxx);
					tx.commit();
				}
				out.print(msg);
			} catch (Exception e) {
				// log.error(e.getMessage());
				e.printStackTrace();
				tx.rollback();
			} finally {
				session.close();
			}
		}
	}

}

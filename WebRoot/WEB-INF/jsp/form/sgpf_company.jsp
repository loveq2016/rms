<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="NetSkyTagLibs" prefix="netsky"%>
<script type="text/javascript">
	function add(id,name){ 
			$.bringBack({'SGDW':name,'SGDW_IDS':id});
	} 
	
	function bringPgsp(param0,param1,param2,param3){
		var url="form/pgsp.do?";
		var $admin=$("#admin",$.pdialog.getCurrent()); 
		url+="sys_wxdw_id="+param0;
		url+="&man_wxdw_id="+param1;
		url+="&project_id="+param2;
		url+="&module_id="+param3; 
		$.pdialog.closeCurrent(); 
		if($admin.val()){
			$.pdialog.closeCurrent();
		}else{
			navTab.openTab('pgsp', url, {title:'手动选派原因'});
		}
	}
</script>

<form id="pagerForm" action="">
	<input type="hidden" name="pageNum" value="${param.pageNum}" />
	<input type="hidden" name="numPerPage" value="${param.numPerPage}" />
	<input type="hidden" name="orderField" value="${param.orderField}" />
	<input type="hidden" name="orderDirection" value="${param.orderDirection}" />
	<input type="hidden" name="searchStr" value="${param.searchStr }"/>
	<input type="hidden" name="ids" id="ids" value="${param.ids }"/>
	<input type="hidden" name="names" id="names" value="${names }"/>
	<input type="hidden" name="xm_id" value="${param.xm_id}"/> 
</form>

<div class="pageHeader">
	<form rel="pagerForm" method="post" action="sgpd/sgpfCompany.do?reasonflag=${param.reasonflag}" onsubmit="return dwzSearch(this, 'dialog');">
	<div class="searchBar" style="height:60px">
		<ul class="searchContent" style="height:30px">
			<li>
				<label>单位名称:</label>
				<input class="textInput" name="searchStr" style="width:200px;" value="${param.searchStr }" type="text"/>
				<input type="text" name="project_id" value="${project_id }"/>
				<input type="text" name="sys_wxdw_id" value="${sys_wxdw_id }"/>
				<input type="text" name="module_id" value="112"/>
				<input type="hidden" id="admin" value="${admin }"/>
			</li>  
		</ul>
		<div class="subBar"> 
			<ul>
				<li><div class="buttonActive"><div class="buttonContent"><button type="submit">查询</button></div></div></li>
			</ul>
		</div>
	</div>
	</form>
</div>
<div class="pageContent">

	<table class="table" layoutH="118" targettype="dialog" width="100%">
		
		<thead>
			<tr>
				<th orderfield="mc">单位名称</th> 
				<th width="40">选择</th>
			</tr>
		</thead>				
		<tbody>
			<c:set var = "offset" value="0"/>
			<c:forEach items="${objList}" var="obj">
			<c:set var ="offset" value="${offset+1}"/> 
			<tr>
				<td>${obj[1]}</td> 
				<td  onclick="bringPgsp('${param.sys_wxdw_id }','${obj[0]}','${project_id }','112')">
						<a class="btnSelect" href="javascript:add('${obj[0] }','${obj[1] }')" title="查找带回">
				</td>
			</tr> 
			</c:forEach>
			<c:if test="${offset<numPerPage}">
				<c:forEach begin="${offset}" end="${numPerPage-1}">
				<tr>
				<td></td>
				<td></td> 
				</tr>
				</c:forEach>
			</c:if>
		</tbody>
	</table>

	<div class="panelBar">
		<div class="pages">
			<span>每页</span>
			<select class="combox" name="numPerPage" onchange="dialogPageBreak({numPerPage:this.value})" selectValue="${numPerPage}">
				<option value="10">10</option>
				<option value="20">20</option>
				<option value="50">50</option>
				<option value="100">100</option>
			</select>
			<span>条，共${totalCount}条</span>
		</div>
		<div class="pagination" targettype="dialog" totalCount="${totalCount}" numPerPage="${numPerPage}" orderField="${orderField }" currentPage="${param.pageNum}"></div>
	</div>
</div>
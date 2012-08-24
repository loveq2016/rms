<%@ page language="java" pageEncoding="GBK" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="now" class="java.util.Date" />

<div class="page">
	<div class="pageContent">
	<form id="form1" name="form1" class="pageForm required-validate" enctype="multipart/form-data" action="save.do" method="post" onsubmit="return iframeCallback(this, dialogAjaxDone);">
		<input type="hidden" name="tableInfomation" value="noFatherTable:com.netsky.base.dataObjects.Te02_jlfk"/>
		<input type="hidden" name="slaveTable" value="com.netsky.base.dataObjects.Te01_slave"/>
		<input type="hidden" name="slaveFatherTables" value="Te02_jlfk,ID,DOC_ID"/>
		<input type="hidden" name="_callbackType" value=""/>
		<input type="hidden" name="_forwardUrl" value=""/>
					
		<input type="hidden" name="slaveType" value="ftp"/>
		<input type="hidden" name="_navTabId" value="_current"/>

		<div class="pageFormContent" layoutH="56">
			<table border='0' width='100%' cellspacing='0' cellpadding='0' style='border-collapse:collapse;' class='alert-table'>
				<tr>
					<td><textarea id="Te02_jlfk.YJ" name="Te02_jlfk.YJ" style="width:99%;height:145px" class="required" comments="反馈内容"></textarea>
						<input type="hidden" name="Te02_jlfk.ID" value="" />
						<input type="hidden" name="Te02_jlfk.PROJECT_ID" value="${param.project_id}"/>
						<input type="hidden" name="Te02_jlfk.MODULE_ID" value="${param.module_id }"/>
						<input type="hidden" name="Te02_jlfk.USER_ID" value="${user.id}"/>
						<input type="hidden" name="Te02_jlfk.TIME" value="<fmt:formatDate value="${now}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
						<input type="hidden" name="Te02_jlfk.DOCUMENT_ID" value="${param.doc_id }"/>
					</td>
				</tr>
				<tr>
					<td style='height:10px;'>&nbsp;</td>
				</tr>
				<tr>
					<td>
						<label for="the_file">附件选择：</label>
						<input type="file" name="the_file1" id="the_file" size="27"/>
						<input type="hidden" name="Te01_slave.ID" value=""/>
						<input type="hidden" name="Te01_slave.PROJECT_ID" value="${param.project_id}"/>
						<input type="hidden" name="Te01_slave.DOC_ID" value=""/>
						<input type="hidden" name="Te01_slave.MODULE_ID" value="9003"/>
						<input type="hidden" name="Te01_slave.USER_ID" value="${user.id}"/>
						<input type="hidden" name="Te01_slave.USER_NAME" value="${user.name}"/>
						<input type="hidden" name="Te01_slave.FTP_DATE" value="<fmt:formatDate value="${now}" pattern="yyyy-MM-dd HH:mm:ss"/>" />
						<input type="hidden" name="Te01_slave.FILE_NAME" value="1"/>
						<input type="hidden" name="Te01_slave.EXT_NAME" value="1"/>
						<input type="hidden" name="Te01_slave.FTP_URL" value="1"/>
						<input type="hidden" name="Te01_slave.SLAVE_TYPE" value="其他附件" />
					</td>
				</tr>
			</table>
		</div>
		<div class="formBar">
			<ul>
				<li><div class="buttonActive"><div class="buttonContent"><button type="submit">提交反馈</button></div></div></li>	
				<li><div class="button"><div class="buttonContent"><button type="Button" class="close">取 消</button></div></div></li>
			</ul>
		</div>
	</form>
	</div>
</div>

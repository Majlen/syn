<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>

<!DOCTYPE html>
<html>
<head>
<title>Syn - Content Security Policy</title>
<link rel="stylesheet" type="text/css" href="syn.css" />
<!--<script type="text/javascript" src="syn.js" />-->
</head>
<body>

<div id="nav">
<br>
<a href="csp">Content Security Policy</a><br>
<a href="hpkp">HTTP Public Key Pinning</a>
</div>
<div id="wrapper">

<table>
	<tr>
		<th id="document-uri">Document URI</th>
		<th id="date">Date</th>
		<th id="blocked-uri">Blocked URI</th>
		<th id="effective-directive">Effective directive</th>
		<th id="original-policy">Original Policy</th>
		<th id="referrer">Referrer</th>
		<th id="status-code">Status code</th>
		<th id="violated-directive">Violated directive</th>
		<th id="source-file">Source file</th>
		<th id="line-number">Line number</th>
		<th id="column-number">Column number</th>
	</tr>
	<c:forEach items="${violations}" var="violation">
	<tr>
		<td id="document-uri">
			<c:out value="${violation.document_uri}" />
		</td>
		<td id="date">
			<javatime:format style="LL" value="${violation.date}" />
		</td>
		<td id="blocked-uri">
			<c:out value="${violation.blocked_uri}" />
		</td>
		<td id="effective-directive">
			<c:out value="${violation.effective_directive}" />
		</td>
		<td id="original-policy">
			<c:out value="${violation.original_policy}" />
		</td>
		<td id="referrer">
			<c:out value="${violation.referrer}" />
		</td>
		<td id="status-code">
			<c:out value="${violation.status_code}" />
		</td>
		<td id="violated-directive">
			<c:out value="${violation.violated_directive}" />
		</td>
		<td id="source-file">
			<c:out value="${violation.source_file}" />
		</td>
		<td id="line-number">
			<c:out value="${violation.line_number}" />
		</td>
		<td id="column-number">
			<c:out value="${violation.column_number}" />
		</td>
	</tr>
</c:forEach>
</table>
</div>
</body>
</html>

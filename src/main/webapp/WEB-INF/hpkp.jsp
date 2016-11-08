<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>

<!DOCTYPE html>
<html>
<head>
<title>Syn - HTTP Public Key Pinning</title>
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
		<th id="hostname">Hostname</th>
		<th id="date-time">Date</th>
		<th id="port">Port</th>
		<th id="effective-expiry">Effective expiration date</th>
		<th id="include-subdomains">Include subdomains?</th>
		<th id="noted-hostname">Noted hostname</th>
		<th id="served-chain">Served certificate chain</th>
		<th id="validated-chain">Validated certificate chain</th>
		<th id="known-pins">Known pins</th>
	</tr>
	<c:forEach items="${violations}" var="violation">
	<tr>
		<td id="hostname">
			<c:out value="${violation.hostname}" />
		</td>
		<td id="date-time">
			<javatime:format style="LL" value="${violation.date_time}" />
		</td>
		<td id="port">
			<c:out value="${violation.port}" />
		</td>
		<td id="effective-expiry">
			<javatime:format style="LL" value="${violation.effective_expiry}" />
		</td>
		<td id="include-subdomains">
			<c:out value="${violation.include_subdomains}" />
		</td>
		<td id="noted-hostname">
			<c:out value="${violation.noted_hostname}" />
		</td>
		<td id="served-chain">
			<c:forEach items="${violation.served_chain}" var="cert" varStatus="status">
				<c:out value="${cert}" /><c:if test="${not status.last}"><br /></c:if>
			</c:forEach>
		</td>
		<td id="validated-chain">
			<c:forEach items="${violation.violated_chain}" var="cert" varStatus="status">
				<c:out value="${cert}" /><c:if test="${not status.last}"><br /></c:if>
			</c:forEach>
		</td>
		<td id="known-pins">
			<c:forEach items="${violation.known_pins}" var="pin" varStatus="status">
				<c:out value="${pin}" /><c:if test="${not status.last}"><br /></c:if>
			</c:forEach>
		</td>
	</tr>
</c:forEach>
</table>
</div>
</body>
</html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="com.bank.app.core.service.AccountService" %>
<%@ page import="com.bank.app.core.model.Account" %>
<%@ page import="com.bank.app.core.model.Transaction" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.naming.NamingException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin | Reporting</title>
</head>
<body>
<h1>Reporting</h1>
<%
    try {
        InitialContext ic = new InitialContext();
        AccountService accountService = (AccountService) ic.lookup("com.bank.app.core.service.AccountService");
        List<Account> accounts = accountService.getAllAccounts();
        pageContext.setAttribute("accounts", accounts);
    } catch (NamingException e) {
        throw new RuntimeException(e);
    }
%>
<h2>Account Summary</h2>
<table border="1">
    <tr>
        <th>ID</th>
        <th>Account Number</th>
        <th>Balance</th>
        <th>Type</th>
        <th>User ID</th>
    </tr>
    <c:forEach var="account" items="${accounts}">
        <tr>
            <td>${account.id}</td>
            <td>${account.accountNumber}</td>
            <td>${account.balance}</td>
            <td>${account.accountType}</td>
            <td>${account.userId}</td>
        </tr>
    </c:forEach>
</table>
<br>
<a href="ExportAccountsCSV" target="_blank">Export Accounts as CSV</a>
<br><br>

<a href="index.jsp">Back to Dashboard</a>
</body>
</html> 
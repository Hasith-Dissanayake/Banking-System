<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>${param.pageTitle} - Banking System</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Local Bootstrap CSS -->
    <link href="${pageContext.request.contextPath}/styles/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- Custom Banking CSS -->
    <link href="${pageContext.request.contextPath}/styles/custom.css" rel="stylesheet">
    <!-- Additional page-specific CSS -->
    ${param.additionalCSS}
</head>
<body class="bg-light"> 
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<head>
    <title>秒杀列表</title>

    <%--    把公用的内容单独翻入到head中去--%>
    <%@include file="common/head.jsp" %>
</head>
<body>
<div class="container">

    <div class="panel panel-danger">
        <!-- Default panel contents -->
        <div class="panel-heading">秒杀列表</div>

        <!-- Table -->
        <table class="table">
            <thead>
                <tr>
                    <td>名称</td>
                    <td>库存</td>
                    <td>开始时间</td>
                    <td>结束时间</td>
                    <td>创建时间</td>
                    <td>价格</td>
                </tr>
            </thead>
            <tbody>
            <c:forEach items="${list}" var="item">
            <tr>
                <td>${item.name}</td>
                <td>${item.number}</td>
                <td><fmt:formatDate value="${item.startTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
                <td><fmt:formatDate value="${item.endTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
                <td><fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
                <td>${item.price}</td>
                <td><a href="/seckill/intoSeckill/${item.id}">进入秒杀</a></td>
            </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>

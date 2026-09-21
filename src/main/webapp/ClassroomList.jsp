<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Classrooms</title>
    <link href="//netdna.bootstrapcdn.com/bootstrap/3.1.0/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/css/All.css">
    <style>
        body { background: url("/images/reduced_opacity_bg.jpeg"); }
        nav a { color: white; }
    </style>
</head>
<body>
<%
    if (session.getAttribute("admin_login") != null || session.getAttribute("login") != null) {
%>
<nav class="navbar navbar-fixed-top navbar-light" style="background-color: #563D7C;">
    <a class="navbar-brand" href="#">IIIT-B Classroom Manager</a>
    <ul class="nav navbar-nav navbar-right">
        <li><a href="javascript:history.back()">Back</a></li>
        <li><a href="/destroy">Logout</a></li>
    </ul>
</nav>
<div class="container" style="margin-top: 75px;">
    <h3>All Classrooms</h3>
    <table class="table table-hover table-bordered" style="background: white;">
        <thead>
        <tr>
            <th>Classroom</th>
            <th>Capacity</th>
            <th>Projector</th>
            <th>Plugs</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="room" items="${classroomList}">
            <tr>
                <td>${room.classCode}</td>
                <td>${room.capacity}</td>
                <td>${room.projector ? 'Yes' : 'No'}</td>
                <td>${room.plugs}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<% } else {
    response.sendRedirect("/LoginFirst.jsp");
} %>
</body>
</html>

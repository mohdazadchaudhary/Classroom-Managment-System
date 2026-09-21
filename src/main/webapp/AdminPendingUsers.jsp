<%@ page import="com.spe.ClassroomManagementSystem.Models.Login" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pending Signups | Admin | IIIT-B Classroom Manager</title>
    <link href="//netdna.bootstrapcdn.com/bootstrap/3.1.0/css/bootstrap.min.css" rel="stylesheet">
    <script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
    <script src="//netdna.bootstrapcdn.com/bootstrap/3.1.0/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="/css/All.css">
    <style>
        body {
            font-family: Ubuntu, Arial, sans-serif;
            background: url("/images/reduced_opacity_bg.jpeg");
        }
        nav a { color: white; }
        .pending-container {
            margin-top: 80px;
            padding-bottom: 60px;
        }
        .panel-header {
            background-color: #563D7C;
            color: #fff;
            padding: 14px 20px;
            border-radius: 8px 8px 0 0;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .panel-header h4 { margin: 0; font-weight: 700; }
        .panel-header .badge-count {
            background: #fff;
            color: #563D7C;
            font-weight: 700;
            font-size: 13px;
            border-radius: 20px;
            padding: 3px 12px;
        }
        .pending-table {
            background: #fff;
            border-radius: 0 0 8px 8px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.10);
            overflow: hidden;
        }
        .pending-table table {
            margin: 0;
        }
        .pending-table table th {
            background: #f5f0fa;
            color: #563D7C;
            font-weight: 700;
            border-top: none;
        }
        .role-badge {
            display: inline-block;
            padding: 3px 10px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
        }
        .role-professor  { background: #d1ecf1; color: #0c5460; }
        .role-ta         { background: #d4edda; color: #155724; }
        .role-sac        { background: #fff3cd; color: #856404; }
        .role-committee  { background: #f8d7da; color: #721c24; }
        .btn-approve {
            background-color: #28a745;
            border-color: #28a745;
            color: #fff;
            border-radius: 4px;
            padding: 4px 14px;
            font-size: 13px;
        }
        .btn-approve:hover { background-color: #218838; color: #fff; }
        .btn-reject {
            background-color: #dc3545;
            border-color: #dc3545;
            color: #fff;
            border-radius: 4px;
            padding: 4px 14px;
            font-size: 13px;
        }
        .btn-reject:hover { background-color: #c82333; color: #fff; }
        .empty-state {
            text-align: center;
            padding: 48px 20px;
            color: #888;
            background: #fff;
            border-radius: 0 0 8px 8px;
        }
        .empty-state .icon { font-size: 48px; color: #c5b8e0; margin-bottom: 12px; }
        .flash-msg {
            margin-bottom: 16px;
        }
    </style>
</head>
<body>
<%
    if (session.getAttribute("admin_login") == null) {
        response.sendRedirect("LoginFirst.jsp");
        return;
    }
%>

<%-- Navbar --%>
<nav class="navbar navbar-fixed-top navbar-light" style="background-color: #563D7C;">
    <a class="navbar-brand" href="AdminDashboard.jsp">IIIT-B Classroom Manager</a>
    <ul class="nav navbar-nav navbar-left">
        <li><a href="/dashboard">Dashboard</a></li>
        <li><a href="RegisterUser.jsp">Add User</a></li>
        <li><a href="/getAllRequests">View Requests</a></li>
        <li><a href="AddClassroom.jsp">Add Classroom</a></li>
        <li><a href="/getAllClassrooms">Add Timetable</a></li>
        <li class="active"><a href="/getPendingUsers"><strong>Pending Signups</strong></a></li>
    </ul>
    <ul class="nav navbar-nav navbar-right">
        <li><a href="/destroy" style="margin-right: 10px">
            <span class="glyphicon glyphicon-log-in"></span> Logout</a></li>
    </ul>
</nav>

<div class="container pending-container">

    <%-- Flash message from approve/reject action --%>
    <%
        String adminMsg = (String) session.getAttribute("admin_msg");
        if (adminMsg != null) {
            session.removeAttribute("admin_msg");
    %>
    <div class="alert alert-info flash-msg"><%= adminMsg %></div>
    <%  } %>

    <%
        List<Login> pendingUsers = (List<Login>) request.getAttribute("pendingUsers");
        int count = (pendingUsers != null) ? pendingUsers.size() : 0;
    %>

    <div class="panel-header">
        <h4>&#9203; Pending Account Requests</h4>
        <span class="badge-count"><%= count %> pending</span>
    </div>

    <div class="pending-table">
        <% if (count == 0) { %>
        <div class="empty-state">
            <div class="icon">&#10003;</div>
            <h5>All caught up!</h5>
            <p>There are no pending signup requests right now.</p>
        </div>
        <% } else { %>
        <table class="table table-hover table-striped">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Username</th>
                    <th>Role</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th style="text-align:center;">Actions</th>
                </tr>
            </thead>
            <tbody>
                <% int i = 1; for (Login u : pendingUsers) { %>
                <tr>
                    <td><%= i++ %></td>
                    <td><strong><%= u.getUserName() %></strong></td>
                    <td>
                        <span class="role-badge role-<%= u.getUserType() %>">
                            <%= u.getUserType() %>
                        </span>
                    </td>
                    <td>
                        <%-- Fetch display name from the linked profile --%>
                        <%
                            String displayName = "—";
                            if ("professor".equals(u.getUserType()) && u.getProfessor() != null) {
                                displayName = u.getProfessor().getProfessorName();
                            } else if ("ta".equals(u.getUserType()) && u.getTa() != null) {
                                displayName = u.getTa().getTaName();
                            } else if ("sac".equals(u.getUserType()) && u.getSac() != null) {
                                displayName = u.getSac().getSacName();
                            } else if ("committee".equals(u.getUserType()) && u.getCommittee() != null) {
                                displayName = u.getCommittee().getCommitteeName();
                            }
                        %>
                        <%= displayName %>
                    </td>
                    <td>
                        <%
                            String displayEmail = "—";
                            if ("professor".equals(u.getUserType()) && u.getProfessor() != null) {
                                displayEmail = u.getProfessor().getProfessorEmail();
                            } else if ("ta".equals(u.getUserType()) && u.getTa() != null) {
                                displayEmail = u.getTa().getTaEmail();
                            } else if ("sac".equals(u.getUserType()) && u.getSac() != null) {
                                displayEmail = u.getSac().getSacEmail();
                            } else if ("committee".equals(u.getUserType()) && u.getCommittee() != null) {
                                displayEmail = u.getCommittee().getCommitteeEmail();
                            }
                        %>
                        <%= displayEmail %>
                    </td>
                    <td style="text-align:center;">
                        <form action="/approveUser" method="post" style="display:inline;">
                            <input type="hidden" name="loginId" value="<%= u.getLoginId() %>">
                            <button type="submit" class="btn btn-approve"
                                    onclick="return confirm('Approve account for <%= u.getUserName() %>?')">
                                &#10003; Approve
                            </button>
                        </form>
                        &nbsp;
                        <form action="/rejectUser" method="post" style="display:inline;">
                            <input type="hidden" name="loginId" value="<%= u.getLoginId() %>">
                            <button type="submit" class="btn btn-reject"
                                    onclick="return confirm('Reject and delete account for <%= u.getUserName() %>?')">
                                &#10007; Reject
                            </button>
                        </form>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
        <% } %>
    </div>
</div>

<%-- Footer --%>
<footer class="page-footer font-small blue">
    <div class="footer-copyright text-center py-3">2020 Copyright:
        <a>Students of IIIT-B</a>
    </div>
</footer>

</body>
</html>

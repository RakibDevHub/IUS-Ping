<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="bg-blue-500 p-4 text-white flex justify-between px-24">
    <a href="<%= request.getContextPath()%>/home" class="text-lg font-bold flex items-center">
        <img src="<%= request.getContextPath()%>/icon.png" alt="alt" class="mr-2 h-[20px]"/>
        IUS Ping
    </a>
    <div>
        <% if (session.getAttribute("role") == null) {%>
        <a href="<%= request.getContextPath()%>/student/register" class="mr-4">Student Register</a>
        <a href="<%= request.getContextPath()%>/login" class="mr-4">Login</a>
        <% } else {
            String role = (String) session.getAttribute("role");
            if ("admin".equals(role)) {%>
        <a href="<%= request.getContextPath()%>/admin/dashboard">Dashboard</a>
        <% } else if ("teacher".equals(role)) {%>
        <a href="<%= request.getContextPath()%>/teacher/dashboard">Dashboard</a>
        <% } else if ("student".equals(role)) {%>
        <a href="<%= request.getContextPath()%>/student/dashboard">Dashboard</a>
        <% }%>
        <a href="<%= request.getContextPath()%>/logout" class="ml-4">Logout</a>
        <% }%>
    </div>
</nav>
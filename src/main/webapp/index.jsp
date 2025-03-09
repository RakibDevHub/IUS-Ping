<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%
    String role = (String) request.getAttribute("role");
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <link rel="icon" href="<%= request.getContextPath()%>/fav-icon.ico" type="image/x-icon">
        <title>Home | IUS Ping</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-100">

        <c:import url="/WEB-INF/components/navbar.jsp"/>

        <div class="flex items-center justify-center h-[80vh] my-auto">                
            <div class="max-w-3xl mx-auto mt-10 bg-white p-6 rounded-lg shadow-md text-center px-24">
                <h1 class="text-4xl font-bold mb-4">Welcome to IUS Ping</h1>
                <p class="text-gray-600">A simple messaging system for IUS students and teachers.</p>
            </div>
        </div>
    </body>
</html>

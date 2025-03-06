<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String role = (String) request.getAttribute("role");
    if (role == null) {
        role = "student"; // Default selection
    }
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login | IUS Ping</title>
        <script src="https://cdn.tailwindcss.com"></script>
        <script>
            function updateInputField() {
                let role = document.getElementById("role").value;
                let inputField = document.getElementById("identifier");

                if (role === "student") {
                    inputField.placeholder = "Enter Student ID (e.g., 211010239)";
                    inputField.type = "number";
                } else {
                    inputField.placeholder = "Enter Email";
                    inputField.type = "text";
                }
            }

            // Ensure correct input type is set on page load
            window.onload = function () {
                updateInputField();
            };
        </script>
    </head>
    <body class="bg-gray-100">

        <c:import url="/WEB-INF/components/navbar.jsp"/>

        <div class="flex items-center justify-center h-[80vh] my-auto">
            <div class="bg-white p-6 rounded-lg shadow-md w-96">
                <h2 class="text-2xl font-bold text-center mb-4">Login</h2>

                <% if (request.getAttribute("error") != null) {%>
                <p class="mb-4 text-red-800 flex justify-center"><%= request.getAttribute("error")%></p>
                <% }%>

                <form action="<%= request.getContextPath()%>/login" method="post">
                    <label for="role" class="block mb-2 font-semibold">Login as:</label>
                    <select id="role" name="role" class="w-full p-2 border rounded mb-4" required onchange="updateInputField()">
                        <option value="student" <%= "student".equals(role) ? "selected" : ""%>>Student</option>
                        <option value="teacher" <%= "teacher".equals(role) ? "selected" : ""%>>Teacher</option>
                        <option value="admin" <%= "admin".equals(role) ? "selected" : ""%>>Admin</option>
                    </select>

                    <input type="text" id="identifier" name="identifier" placeholder="Enter Student ID (e.g., 211010239)" 
                           class="w-full mb-4 p-2 border rounded" required>

                    <input type="password" name="password" placeholder="Enter Password" 
                           class="w-full mb-4 p-2 border rounded" required>

                    <button type="submit" class="w-full bg-blue-500 text-white p-2 rounded">Login</button>
                </form>
            </div>
        </div>

    </body>
</html>

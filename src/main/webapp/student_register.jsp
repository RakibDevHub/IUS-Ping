<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Register | IUS Ping</title>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-100">
        <c:import url="/WEB-INF/components/navbar.jsp"/>

        <div class="max-w-md mx-auto mt-10 bg-white p-6 rounded-lg shadow-md">
            <h2 class="text-2xl font-bold mb-4">Register</h2>

            <% if (request.getAttribute("success") != null) {%>
            <p class="mb-4 text-green-800 flex justify-center"><%= request.getAttribute("success")%></p>
            <% }%>

            <% if (request.getAttribute("error") != null) {%>
            <p class="mb-4 text-red-800 flex justify-center"><%= request.getAttribute("error")%></p>
            <% }%>

            <form action="<%= request.getContextPath()%>/student/register" method="post">
                <div class="mb-4">
                    <label for="studentId" class="block text-gray-700 text-sm font-bold mb-2">Student ID:</label>
                    <input type="text" name="studentId" id="studentId" class="w-full p-2 border rounded" required>
                </div>

                <div class="mb-4">
                    <label for="name" class="block text-gray-700 text-sm font-bold mb-2">Name:</label>
                    <input type="text" name="name" id="name" class="w-full p-2 border rounded" required>
                </div>

                <div class="mb-4">
                    <label for="batch" class="block text-gray-700 text-sm font-bold mb-2">Batch:</label>
                    <input type="text" name="batch" id="batch" class="w-full p-2 border rounded" required>
                </div>

                <div class="mb-4">
                    <label for="department" class="block text-gray-700 text-sm font-bold mb-2">Department:</label>
                    <input type="text" name="department" id="department" class="w-full p-2 border rounded" required>
                </div>

                <div class="mb-4">
                    <label for="phoneNumber" class="block text-gray-700 text-sm font-bold mb-2">Phone Number:</label>
                    <input type="tel" name="phoneNumber" id="phoneNumber" class="w-full p-2 border rounded" required>
                </div>

                <div class="mb-4">
                    <label for="password" class="block text-gray-700 text-sm font-bold mb-2">Password:</label>
                    <input type="password" name="password" id="password" class="w-full p-2 border rounded" required>
                </div>

                <button type="submit" class="w-full bg-blue-500 text-white p-2 rounded">Register</button>
            </form>
        </div>
    </body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Add Teacher | IUS Ping</title>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-100">
        <c:import url="/WEB-INF/components/navbar.jsp"/>

        <div class="max-w-md mx-auto mt-10 bg-white p-6 rounded-lg shadow-md">
            <h2 class="text-2xl font-bold mb-4">Add Teacher</h2>

            <% if (request.getAttribute("error") != null) { %>
            <p style="color: red;">
                <% if (request.getAttribute("error").equals("MissingFields")) { %>
                Please fill in all the required fields.
                <% } else if (request.getAttribute("error").equals("DatabaseError")) { %>
                A database error occurred.
                <% } else if (request.getAttribute("error").equals("EmailExists")) { %>
                A teacher with that email already exists.
                <% } else if (request.getAttribute("error").equals("InvalidEmail")) { %>
                Please enter a valid email address.
                <% } %>
            </p>
            <% }%>

            <form action="<%= request.getContextPath()%>/admin/addTeacher" method="post">
                <div class="mb-4">
                    <label for="name" class="block text-gray-700 text-sm font-bold mb-2">Name:</label>
                    <input type="text" name="name" id="name" class="w-full p-2 border rounded" required>
                </div>

                <div class="mb-4">
                    <label for="email" class="block text-gray-700 text-sm font-bold mb-2">Email:</label>
                    <input type="email" name="email" id="email" class="w-full p-2 border rounded" required>
                </div>

                <div class="mb-4">
                    <label for="department" class="block text-gray-700 text-sm font-bold mb-2">Department: </label>
                    <input type="text" name="department" id="department" class="w-full p-2 border rounded" required>
                </div>

                <div class="mb-4">
                    <label for="password" class="block text-gray-700 text-sm font-bold mb-2">Password:</label>
                    <input type="password" name="password" id="password" class="w-full p-2 border rounded" required>
                </div>

                <button type="submit" class="w-full bg-blue-500 text-white p-2 rounded">Add Teacher</button>
            </form>
        </div>
    </body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>IUS Ping | Send Message</title>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-100">
        <c:import url="/WEB-INF/components/navbar.jsp"/>

        <div class="max-w-md mx-auto mt-10 bg-white p-6 rounded-lg shadow-md">
            <h2 class="text-2xl font-bold mb-4">Send Message to Student</h2>

            <!-- Display success or error messages -->
            <%
                String successMessage = (String) request.getAttribute("successMessage");
                String errorMessage = (String) request.getAttribute("errorMessage");
            %>

            <% if (successMessage != null) {%>
            <p class="text-green-600 text-center mb-4"><%= successMessage%></p>
            <% } %>
            <% if (errorMessage != null) {%>
            <p class="text-red-600 text-center mb-4"><%= errorMessage%></p>
            <% }%>

            <form action="<%= request.getContextPath()%>/teacher/sendMessage" method="post">
                <div class="mb-4">
                    <p><strong>Name:</strong> ${student.name}</p>
                    <p><strong>Department:</strong> ${student.department}</p>
                    <p><strong>ID:</strong> ${student.studentId}</p>
                    <input type="hidden" name="id" value="${student.id}">
                </div>

                <div class="mb-4">
                    <label for="message" class="block text-gray-700 text-sm font-bold mb-2">Message:</label>
                    <textarea name="message" id="message" class="w-full p-2 border rounded" rows="4" required></textarea>
                </div>

                <button type="submit" class="w-full bg-blue-500 text-white p-2 rounded">Send Message</button>
            </form>
        </div>
    </body>
</html>

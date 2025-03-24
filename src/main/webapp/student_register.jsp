<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String successMessage = (String) request.getAttribute("success");
    String errorMessage = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Register | IUS Ping</title>
        <link rel="icon" href="<%= request.getContextPath()%>/fav-icon.ico" type="image/x-icon">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-100">
        <c:import url="/WEB-INF/components/navbar.jsp"/>

        <div class="max-w-md mx-auto mt-10 bg-white p-6 rounded-lg shadow-md">
            <h2 class="text-2xl font-bold mb-4">Register</h2>

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

        <div id="messageModal" class="fixed inset-0 flex items-center justify-center bg-gray-800 bg-opacity-50 hidden">
            <div class="bg-white p-6 rounded-lg shadow-lg text-center">
                <p id="modalMessage" class="text-lg"></p>
                <button id="closeModal" class="mt-4 px-4 py-2 bg-gray-500 text-white rounded">Close</button>
            </div>
        </div>

        <script>
            const messageModal = document.getElementById('messageModal');
            const modalMessage = document.getElementById('modalMessage');
            const closeModal = document.getElementById('closeModal');

            <% if (successMessage != null) {%>
            modalMessage.innerText = "<%= successMessage%>";
            modalMessage.classList.add("text-green-700");
            messageModal.classList.remove("hidden");
            <% } else if (errorMessage != null) {%>
            modalMessage.innerText = "<%= errorMessage%>";
            modalMessage.classList.add("text-red-700");
            messageModal.classList.remove("hidden");
            <% }%>

            closeModal.addEventListener('click', () => {
                messageModal.classList.add("hidden");
            });
        </script>
    </body>
</html>

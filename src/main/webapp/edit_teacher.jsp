<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String successMessage = (String) request.getAttribute("success");
    if (successMessage == null) {
        successMessage = (String) session.getAttribute("success");
        if (successMessage != null) {
            session.removeAttribute("success");
        }
    }

    String errorMessage = (String) request.getAttribute("error");
    if (errorMessage == null) {
        errorMessage = (String) session.getAttribute("error");
        if (errorMessage != null) {
            session.removeAttribute("error");
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Edit Teacher | IUS Ping</title>
        <link rel="icon" href="<%= request.getContextPath()%>/fav-icon.ico" type="image/x-icon">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-100">
        <c:import url="/WEB-INF/components/navbar.jsp"/>

        <div class="max-w-md mx-auto mt-10 bg-white p-6 rounded-lg shadow-md">
            <h2 class="text-2xl font-bold mb-4">Edit Teacher Information</h2>

            <form action="<%= request.getContextPath()%>/admin/editTeacher" method="post">
                <input type="hidden" name="id" value="${teacher.id}">

                <div class="mb-4">
                    <label for="name" class="block text-gray-700 text-sm font-bold mb-2">Name:</label>
                    <input type="text" name="name" id="name" class="w-full p-2 border rounded" value="${teacher.name}" required>
                </div>
                <div class="mb-4">
                    <label for="email" class="block text-gray-700 text-sm font-bold mb-2">Email:</label>
                    <input type="email" name="email" id="email" class="w-full p-2 border rounded" value="${teacher.email}" required>
                </div>
                <div class="mb-4">
                    <label for="department" class="block text-gray-700 text-sm font-bold mb-2">Department:</label>
                    <input type="text" name="department" id="department" class="w-full p-2 border rounded" value="${teacher.department}" required>
                </div>
                <div class="w-full flex flex-row gap-4 justify-center">
                <button type="submit" class="bg-green-500 text-white py-2 px-4 rounded">Update</button>
                <a href="<%= request.getContextPath()%>/admin/dashboard" class="bg-gray-500 text-white py-2 px-4 rounded">Cancel</a>
                </div>
            </form>
        </div>
        <div id="messageModal" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 hidden">
            <div class="modal-content bg-white rounded-xl shadow-2xl text-center p-6 w-full max-w-md border border-gray-200">
                <div id="modalIcon" class="modal-icon flex justify-center items-center text-4xl mb-6">
                </div>
                <h2 id="modalTitle" class="modal-title text-2xl font-semibold text-gray-900 mb-4"></h2>
                <p id="modalText" class="modal-text text-gray-700 mb-6 text-lg"></p>
                <div class="modal-buttons flex justify-center space-x-4">
                    <button id="closeModal" class="close-button bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-8 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50">
                        OK
                    </button>
                </div>
            </div>
        </div>
        <script>
            const messageModal = document.getElementById('messageModal');
            const closeModalBtn = document.getElementById('closeModal');
            const modalTitle = document.getElementById('modalTitle');
            const modalText = document.getElementById('modalText');
            const modalIcon = document.getElementById('modalIcon');

            function showMessageModal(type, text) {
                modalTitle.innerText = type === 'success' ? 'Success' : 'Error';
                modalText.innerText = text;
                messageModal.classList.remove('hidden');

                if (type === 'success') {
                    modalIcon.innerHTML = '<i class="fas fa-check-circle text-green-500"></i>';
                } else {
                    modalIcon.innerHTML = '<i class="fas fa-exclamation-triangle text-red-500"></i>';
                }


                closeModalBtn.onclick = () => {
                    messageModal.classList.add('hidden');
                };
            }

            <% if (successMessage != null) {%>
            showMessageModal('success', '<%= successMessage%>');
            <% } else if (errorMessage != null) {%>
            showMessageModal('error', '<%= errorMessage%>');
            <% }%>
        </script>
    </body>
</html>
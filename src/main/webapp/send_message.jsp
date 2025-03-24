<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="com.rakibdevhub.iusping.model.StudentModel" %>
<%
    String successMessage = (String) request.getAttribute("success");
    String errorMessage = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Send Message | IUS Ping</title>
        <link rel="icon" href="<%= request.getContextPath()%>/fav-icon.ico" type="image/x-icon">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-100">
        <c:import url="/WEB-INF/components/navbar.jsp"/>

        <div class="max-w-2xl mx-auto mt-10 bg-white p-6 rounded-lg shadow-md">
            <h2 class="text-2xl font-bold mb-4">Send Message to Students</h2>

            <form action="<%= request.getContextPath()%>/teacher/sendMessage" method="post">
                <div class="mb-4">
                    <h3 class="text-lg font-semibold mb-2">Selected Students:</h3>
                    <ul class="list-disc list-inside">
                        <%
                            List<StudentModel> students = (List<StudentModel>) request.getAttribute("students");
                            if (students != null) {
                                for (StudentModel student : students) {
                        %>
                        <li>
                            <%= student.getName()%> (<%= student.getStudentId()%>) - <%= student.getDepartment()%>
                            <input type="hidden" name="selectedStudents" value="<%= student.getId()%>">
                        </li>
                        <%
                                }
                            }
                        %>
                    </ul>
                </div>

                <div class="mb-4">
                    <label for="message" class="block text-gray-700 text-sm font-bold mb-2">Message:</label>
                    <textarea name="message" id="message" class="w-full p-2 border rounded" rows="4" required></textarea>
                </div>

                <button type="submit" class="w-full bg-blue-500 text-white p-2 rounded">Send Message</button>
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
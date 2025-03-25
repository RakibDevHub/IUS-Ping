<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@page import="com.rakibdevhub.iusping.model.TeacherModel"%>
<%@page import="com.rakibdevhub.iusping.model.StudentModel"%>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String role = (String) request.getAttribute("role");
    List<StudentModel> students = (List<StudentModel>) request.getAttribute("students");
    List<TeacherModel> teachers = (List<TeacherModel>) request.getAttribute("teachers");

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
        <title>Admin Dashboard | IUS Ping</title>
        <link rel="icon" href="<%= request.getContextPath()%>/fav-icon.ico" type="image/x-icon">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-100">

        <c:import url="/WEB-INF/components/navbar.jsp"/>

        <div class="max-w-5xl mx-auto mt-10 bg-white p-6 rounded-lg shadow-md text-center px-24">
            <div class="flex flex-row justify-between">
                <h3 class="text-lg font-semibold mb-4">Student Lists</h3>
            </div>
            <table class="w-full border-collapse border border-gray-300">
                <thead>
                    <tr>
                        <th class="border border-gray-300 p-2">Sr. No</th>
                        <th class="border border-gray-300 p-2">Student ID</th>
                        <th class="border border-gray-300 p-2">Name</th>
                        <th class="border border-gray-300 p-2">Batch</th>
                        <th class="border border-gray-300 p-2">Department</th>
                        <th class="border border-gray-300 p-2">Status</th>
                        <th class="border border-gray-300 p-2">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (students != null) {
                            int studentSrNo = 1;
                            for (StudentModel student : students) {%>
                    <tr>
                        <td class="border border-gray-300 p-2"><%= studentSrNo++%></td>
                        <td class="border border-gray-300 p-2"><%= student.getStudentId()%></td>
                        <td class="border border-gray-300 p-2 text-left"><%= student.getName()%></td>
                        <td class="border border-gray-300 p-2"><%= student.getBatch()%></td>
                        <td class="border border-gray-300 p-2"><%= student.getDepartment()%></td>
                        <td class="border border-gray-300 p-2"><%= student.getStatus()%></td>
                        <td class="border border-gray-300 p-2 flex flex-row gap-2 justify-center">
                            <% if ("Pending".equalsIgnoreCase(student.getStatus())) {%>
                            <form method="post" action="<%= request.getContextPath()%>/admin/approveStudent">
                                <input type="hidden" name="id" value="<%= student.getId()%>">
                                <button type="submit" class="bg-green-500 text-white py-1 px-2 rounded mr-1"><i class="fas fa-check-square"></i></button>
                            </form>
                            <form method="post" action="<%= request.getContextPath()%>/admin/rejectStudent">
                                <input type="hidden" name="id" value="<%= student.getId()%>">
                                <button type="submit" class="bg-red-500 text-white py-1 px-2 rounded mr-1"><i class="fas fa-minus-square"></i></button>
                            </form>
                            <% } else if ("Approved".equalsIgnoreCase(student.getStatus())) {%>
                            <form method="post" action="<%= request.getContextPath()%>/admin/rejectStudent">
                                <input type="hidden" name="id" value="<%= student.getId()%>">
                                <button type="submit" class="bg-red-500 text-white py-1 px-2 rounded mr-1"><i class="fas fa-minus-square"></i></button>
                            </form>
                            <% } else if ("Rejected".equalsIgnoreCase(student.getStatus())) {%>
                            <form method="post" action="<%= request.getContextPath()%>/admin/approveStudent">
                                <input type="hidden" name="id" value="<%= student.getId()%>">
                                <button type="submit" class="bg-green-500 text-white py-1 px-2 rounded mr-1"><i class="fas fa-check-square"></i></button>
                            </form>
                            <% }%>
                            <button onclick="showConfirmModal('Remove Student', 'Are you sure you want to remove this student?', '<%= request.getContextPath()%>/admin/removeStudent', '<%= student.getId()%>')" class="bg-red-700 text-white py-1 px-2 rounded mr-1"><i class="fas fa-trash"></i></button>
                        </td>
                    </tr>
                    <% }
                        } %>
                </tbody>
            </table>
        </div>
        <div class="max-w-5xl mx-auto my-10 bg-white p-6 rounded-lg shadow-md text-center px-24">
            <div class="flex flex-row justify-between">
                <h3 class="text-lg font-semibold mb-4">Teacher Lists</h3>
                <a href="addTeacher" class="bg-blue-500 text-white px-4 py-2 rounded mb-4 inline-block">Add Teacher</a>
            </div>
            <table class="w-full border-collapse border border-gray-300">
                <thead>
                    <tr>
                        <th class="border border-gray-300 p-2">Sr. No</th>
                        <th class="border border-gray-300 p-2 text-left">Name</th>
                        <th class="border border-gray-300 p-2 text-left">Email</th>
                        <th class="border border-gray-300 p-2 text-left">Department</th>
                        <th class="border border-gray-300 p-2">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (teachers != null) {
                            int teacherSrNo = 1;
                            for (TeacherModel teacher : teachers) {%>
                    <tr>
                        <td class="border border-gray-300 p-2"><%= teacherSrNo++%></td>
                        <td class="border border-gray-300 p-2"><%= teacher.getName()%></td>
                        <td class="border border-gray-300 p-2"><%= teacher.getEmail()%></td>
                        <td class="border border-gray-300 p-2"><%= teacher.getDepartment()%></td>
                        <td class="border border-gray-300 p-2">
                            <a href="<%= request.getContextPath()%>/admin/editTeacher?id=<%= teacher.getId()%>" class="bg-blue-500 text-white py-1 px-2 rounded mr-1"><i class="fas fa-pen-square"></i></a>
                            <button onclick="showConfirmModal('Remove Teacher', 'Are you sure you want to remove this teacher?', '<%= request.getContextPath()%>/admin/removeTeacher', '<%= teacher.getId()%>')" class="bg-red-500 text-white py-1 px-2 rounded"><i class="fas fa-trash"></i></button>
                        </td>
                    </tr>
                    <% }
                        }%>
                </tbody>
            </table>
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
        <div id="confirmModal" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 hidden">
            <div class="modal-content bg-white rounded-xl shadow-2xl text-center p-6 w-full max-w-md border border-gray-200">
                <h2 id="confirmTitle" class="modal-title text-2xl font-semibold text-gray-900 mb-4"></h2>
                <p id="confirmText" class="modal-text text-gray-700 mb-6 text-lg"></p>
                <div class="modal-buttons flex justify-center space-x-4">
                    <button id="confirmCancel" class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-semibold py-2 px-6 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-300 focus:ring-opacity-50">
                        Cancel
                    </button>
                    <form id="confirmForm" method="post" style="display: inline;">
                        <input type="hidden" name="id" id="confirmId">
                        <button type="submit" class="bg-red-500 hover:bg-red-600 text-white font-semibold py-2 px-6 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-opacity-50">
                            Confirm
                        </button>
                    </form>
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

            const confirmModal = document.getElementById('confirmModal');
            const confirmTitle = document.getElementById('confirmTitle');
            const confirmText = document.getElementById('confirmText');
            const confirmCancel = document.getElementById('confirmCancel');
            const confirmForm = document.getElementById('confirmForm');
            const confirmId = document.getElementById('confirmId');

            function showConfirmModal(title, text, action, id) {
                confirmTitle.innerText = title;
                confirmText.innerText = text;
                confirmForm.action = action;
                confirmId.value = id;
                confirmModal.classList.remove('hidden');

                confirmCancel.onclick = () => {
                    confirmModal.classList.add('hidden');
                };
            }
        </script>
    </body>
</html>
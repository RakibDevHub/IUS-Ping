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
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Admin Dashboard | IUS Ping</title>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-100">

        <c:import url="/WEB-INF/components/navbar.jsp"/>

        <div class="max-w-5xl mx-auto mt-10 bg-white p-6 rounded-lg shadow-md text-center px-24">
            <h2 class="text-2xl font-bold mb-6">Admin Dashboard</h2>
            <a href="addTeacher" class="bg-blue-500 text-white px-4 py-2 rounded mb-4 inline-block">Add Teacher</a>

            <div class="mb-8">
                <h3 class="text-lg font-semibold mb-4">Student Lists</h3>
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
                            <td class="border border-gray-300 p-2"><%= student.getName()%></td>
                            <td class="border border-gray-300 p-2"><%= student.getBatch()%></td>
                            <td class="border border-gray-300 p-2"><%= student.getDepartment()%></td>
                            <td class="border border-gray-300 p-2"><%= student.getStatus()%></td>
                            <td class="border border-gray-300 p-2">
                                <% if ("Pending".equalsIgnoreCase(student.getStatus())) {%>
                                <a href="<%= request.getContextPath()%>/admin/approveStudent?id=<%= student.getId()%>" class="bg-green-500 text-white p-1 rounded mr-1">Approve</a>
                                <a href="<%= request.getContextPath()%>/admin/rejectStudent?id=<%= student.getId()%>" class="bg-red-500 text-white p-1 rounded mr-1">Reject</a>
                                <% } else if ("Approved".equalsIgnoreCase(student.getStatus())) { %>
                                <span class="bg-green-200 text-green-800 p-1 rounded mr-1">Approved</span>
                                <% } else if ("Rejected".equalsIgnoreCase(student.getStatus())) { %>
                                <span class="bg-red-200 text-red-800 p-1 rounded mr-1">Rejected</span>
                                <% } %>
                            </td>
                        </tr>
                        <% }
                            } %>
                    </tbody>
                </table>
            </div>

            <div>
                <h3 class="text-lg font-semibold mb-4">Teacher Lists</h3>
                <table class="w-full border-collapse border border-gray-300">
                    <thead>
                        <tr>
                            <th class="border border-gray-300 p-2">Sr. No</th>
                            <th class="border border-gray-300 p-2">Name</th>
                            <th class="border border-gray-300 p-2">Email</th>
                            <th class="border border-gray-300 p-2">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (teachers != null) {
                                int teacherSrNo = 1; // Counter for teacher rows
                                for (TeacherModel teacher : teachers) {%>
                        <tr>
                            <td class="border border-gray-300 p-2"><%= teacherSrNo++%></td>
                            <td class="border border-gray-300 p-2"><%= teacher.getName()%></td>
                            <td class="border border-gray-300 p-2"><%= teacher.getEmail()%></td>
                            <td class="border border-gray-300 p-2">
                                <a href="editTeacher?id=<%= teacher.getId()%>" class="bg-blue-500 text-white p-1 rounded mr-1">Edit</a>
                                <a href="deleteTeacher?id=<%= teacher.getId()%>" class="bg-red-500 text-white p-1 rounded">Delete</a>
                            </td>
                        </tr>
                        <% }
                            }%>
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>
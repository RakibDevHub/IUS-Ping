<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="com.rakibdevhub.iusping.model.StudentModel" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String role = (String) request.getAttribute("role");
    List<StudentModel> students = (List<StudentModel>) request.getAttribute("students");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>IUS Ping | Teacher Dashboard</title>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-100">

        <c:import url="/WEB-INF/components/navbar.jsp"/>

        <div class="max-w-5xl mx-auto mt-10 bg-white p-6 rounded-lg shadow-md text-center px-24">
            <h2 class="text-2xl font-bold mb-6">Teacher Dashboard</h2>

            <div class="mb-8">
                <h3 class="text-lg font-semibold mb-4">Student Lists</h3>
                <table class="w-full border-collapse border border-gray-300">
                    <thead>
                        <tr>
                            <th class="border border-gray-300 p-2">Sr. No</th>
                            <th class="border border-gray-300 p-2">Student ID</th>
                            <th class="border border-gray-300 p-2">Name</th>
                            <th class="border border-gray-300 p-2">Department</th>
                            <th class="border border-gray-300 p-2">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (students != null) {
                                int studentSrNo = 1; // Counter for student rows
                                for (StudentModel student : students) {%>
                        <tr>
                            <td class="border border-gray-300 p-2"><%= studentSrNo++%></td>
                            <td class="border border-gray-300 p-2"><%= student.getStudentId()%></td>
                            <td class="border border-gray-300 p-2"><%= student.getName()%></td>
                            <td class="border border-gray-300 p-2"><%= student.getDepartment()%></td>
                            <td class="border border-gray-300 p-2">
                                <a href="sendMessage?id=<%= student.getId()%>" class="bg-blue-500 text-white px-3 py-1 rounded">Send Message</a>
                            </td>
                        </tr>
                        <% }
                        } %>
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>
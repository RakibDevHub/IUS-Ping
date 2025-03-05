<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="com.rakibdevhub.iusping.model.StudentModel" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Send Message | IUS Ping</title>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-100">
        <c:import url="/WEB-INF/components/navbar.jsp"/>

        <div class="max-w-2xl mx-auto mt-10 bg-white p-6 rounded-lg shadow-md">
            <h2 class="text-2xl font-bold mb-4">Send Message to Students</h2>

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

    </body>
</html>  
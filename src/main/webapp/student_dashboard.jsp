<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Student Dashboard | IUS Ping</title>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-100">
        <c:import url="/WEB-INF/components/navbar.jsp"/>

        <div class="max-w-md mx-auto mt-10 bg-white p-6 rounded-lg shadow-md">
            <h2 class="text-2xl font-bold mb-4">Student Information</h2>

            <p><strong>Student ID:</strong> ${student.studentId}</p>
            <p><strong>Name:</strong> ${student.name}</p>
            <p><strong>Department:</strong> ${student.department}</p>
            <p><strong>Phone Number:</strong> ${student.phoneNumber}</p>
        </div>
    </body>
</html>
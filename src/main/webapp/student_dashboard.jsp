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

        <div class="container mx-auto mt-10">
            <div id="blurrable-content">
                <div class="max-w-lg mx-auto bg-white p-6 rounded-lg shadow-md">
                    <div class="flex justify-between mb-4">
                        <h2 class="text-2xl font-bold">Student Information</h2>
                        <button type="button" id="changePasswordBtn" class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded">Change Password</button>
                    </div>

                    <% if (request.getAttribute("success") != null) {%>
                    <p class="mb-4 text-green-800 flex justify-center"><%= request.getAttribute("success")%></p>
                    <% }%>

                    <% if (request.getAttribute("error") != null) {%>
                    <p class="mb-4 text-red-800 flex justify-center"><%= request.getAttribute("error")%></p>
                    <% }%>

                    <form id="profileForm" action="<%= request.getContextPath()%>/student/dashboard" method="post">
                        <input type="hidden" name="action" value="updateProfile">
                        <div class="mb-4">
                            <label for="studentId" class="block text-gray-700 text-sm font-bold mb-2">Student ID:</label>
                            <input type="text" id="studentId" name="studentId" class="w-full border rounded py-2 px-3 text-gray-700" value="${student.studentId}" disabled>
                        </div>
                        <div class="mb-4">
                            <label for="name" class="block text-gray-700 text-sm font-bold mb-2">Name:</label>
                            <input type="text" id="name" name="name" class="w-full border rounded py-2 px-3 text-gray-700" value="${student.name}" disabled>
                        </div>
                        <div class="mb-4">
                            <label for="department" class="block text-gray-700 text-sm font-bold mb-2">Department:</label>
                            <input type="text" id="department" name="department" class="w-full border rounded py-2 px-3 text-gray-700" value="${student.department}" disabled>
                        </div>
                        <div class="mb-4">
                            <label for="batch" class="block text-gray-700 text-sm font-bold mb-2">Batch:</label>
                            <input type="text" id="batch" name="batch" class="w-full border rounded py-2 px-3 text-gray-700" value="${student.batch}" disabled>
                        </div>
                        <div class="flex justify-end mt-4">
                            <button type="button" id="editProfileBtn" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mr-2">Edit Profile</button>
                            <button type="submit" id="updateProfileBtn" class="hidden bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded mr-2">Update Profile</button>
                            <button type="button" id="cancelProfileBtn" class="hidden bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">Cancel</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div id="passwordModal" class="hidden fixed inset-0 z-50 overflow-y-auto">
            <div class="flex items-center justify-center min-h-screen px-4">
                <div class="bg-white rounded-lg p-6 shadow-xl w-96">
                    <h2 class="text-xl font-bold mb-4">Change Password</h2>
                    <form action="<%= request.getContextPath()%>/student/dashboard" method="post">
                        <input type="hidden" name="action" value="updatePassword">
                        <div class="mb-4">
                            <label for="currentPassword" class="block text-gray-700 text-sm font-bold mb-2">Current Password:</label>
                            <input type="password" id="currentPassword" name="currentPassword" class="w-full border rounded py-2 px-3 text-gray-700" required>
                        </div>
                        <div class="mb-4">
                            <label for="newPassword" class="block text-gray-700 text-sm font-bold mb-2">New Password:</label>
                            <input type="password" id="newPassword" name="newPassword" class="w-full border rounded py-2 px-3 text-gray-700" required>
                        </div>
                        <div class="mb-4">
                            <label for="confirmPassword" class="block text-gray-700 text-sm font-bold mb-2">Confirm New Password:</label>
                            <input type="password" id="confirmPassword" name="confirmPassword" class="w-full border rounded py-2 px-3 text-gray-700" required>
                        </div>
                        <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Confirm</button>
                        <button type="button" id="closeModalBtn" class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded ml-2">Cancel</button>
                    </form>
                </div>
            </div>
        </div>

        <script>
            const changePasswordBtn = document.getElementById('changePasswordBtn');
            const editProfileBtn = document.getElementById('editProfileBtn');
            const updateProfileBtn = document.getElementById('updateProfileBtn');
            const passwordModal = document.getElementById('passwordModal');
            const closeModalBtn = document.getElementById('closeModalBtn');
            const profileForm = document.getElementById('profileForm');
            const cancelProfileBtn = document.getElementById('cancelProfileBtn');

            editProfileBtn.addEventListener('click', () => {
                const inputs = profileForm.querySelectorAll('input:not([type="hidden"])');

                // Enable all input fields
                inputs.forEach(input => input.disabled = false);

                // Show Update & Cancel buttons, hide Edit Profile button
                editProfileBtn.classList.add('hidden');
                updateProfileBtn.classList.remove('hidden');
                cancelProfileBtn.classList.remove('hidden');
            });

// Cancel button click handler
            cancelProfileBtn.addEventListener('click', () => {
                const inputs = profileForm.querySelectorAll('input:not([type="hidden"])');

                // Disable all input fields
                inputs.forEach(input => input.disabled = true);

                // Show Edit Profile button, hide Update & Cancel buttons
                editProfileBtn.classList.remove('hidden');
                updateProfileBtn.classList.add('hidden');
                cancelProfileBtn.classList.add('hidden');
            });


            changePasswordBtn.addEventListener('click', () => {
                passwordModal.classList.remove('hidden');
                document.getElementById('blurrable-content').classList.add('blur');
            });

            closeModalBtn.addEventListener('click', () => {
                passwordModal.classList.add('hidden');
                document.getElementById('blurrable-content').classList.remove('blur');
            });
        </script>

        <style>
            .blur {
                filter: blur(3px);
            }
        </style>

    </body>
</html>
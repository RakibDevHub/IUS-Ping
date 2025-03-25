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
        <title>Student Dashboard | IUS Ping</title>
        <link rel="icon" href="<%= request.getContextPath()%>/fav-icon.ico" type="image/x-icon">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
        <script src="https://cdn.tailwindcss.com"></script>
        <style>
            .blur {
                filter: blur(3px);
            }
        </style>
    </head>
    <body class="bg-gray-100">
        <c:import url="/WEB-INF/components/navbar.jsp"/>

        <div class="container mx-auto mt-10">
            <div id="blurrable-content">
                <div class="max-w-lg mx-auto bg-white p-6 rounded-lg shadow-md">
                    <div class="flex flex-col mb-4">
                        <h2 class="text-2xl font-bold">Student Information</h2>
                        <div class="flex justify-center gap-2 w-full mt-4">
                            <button type="button" id="changePasswordBtn" class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded">Change Password</button>
                            <button type="button" id="changeNumberBtn" class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded">Change Number</button>
                        </div>
                    </div>

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

        <div id="numberModal" class="hidden fixed inset-0 z-50 overflow-y-auto">
            <div class="flex items-center justify-center min-h-screen px-4">
                <div class="bg-white rounded-lg p-6 shadow-xl w-96">
                    <h2 class="text-xl font-bold mb-4">Change Phone Number</h2>
                    <form action="<%= request.getContextPath()%>/student/dashboard" method="post">
                        <input type="hidden" name="action" value="updateNumber">
                        <div class="mb-4">
                            <label for="newNumber" class="block text-gray-700 text-sm font-bold mb-2">New Phone Number:</label>
                            <input type="text" id="newNumber" name="newNumber" class="w-full border rounded py-2 px-3 text-gray-700" required>
                        </div>
                        <div class="mb-4">
                            <label for="password" class="block text-gray-700 text-sm font-bold mb-2">Password:</label>
                            <input type="password" id="password" name="password" class="w-full border rounded py-2 px-3 text-gray-700" required>
                        </div>
                        <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Confirm</button>
                        <button type="button" id="closeNumberModalBtn" class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded ml-2">Cancel</button>
                    </form>
                </div>
            </div>
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
            const closeModal = document.getElementById('closeModal');
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


                closeModal.onclick = () => {
                    messageModal.classList.add('hidden');
                };
            }

            const changePasswordBtn = document.getElementById('changePasswordBtn');
            const editProfileBtn = document.getElementById('editProfileBtn');
            const updateProfileBtn = document.getElementById('updateProfileBtn');
            const cancelProfileBtn = document.getElementById('cancelProfileBtn');
            const passwordModal = document.getElementById('passwordModal');
            const closeModalBtn = document.getElementById('closeModalBtn');
            const profileForm = document.getElementById('profileForm');
            const changeNumberBtn = document.getElementById('changeNumberBtn');
            const numberModal = document.getElementById('numberModal');
            const closeNumberModalBtn = document.getElementById('closeNumberModalBtn');

            // Store original values.
            let originalValues = {
                studentId: "${student.studentId}",
                name: "${student.name}",
                department: "${student.department}",
                batch: "${student.batch}"
            };

            let isEditing = false;

            editProfileBtn.addEventListener('click', () => {
                const inputs = profileForm.querySelectorAll('input:not([type="hidden"])');
                inputs.forEach(input => input.disabled = false);
                editProfileBtn.classList.add('hidden');
                updateProfileBtn.classList.remove('hidden');
                cancelProfileBtn.classList.remove('hidden');
                isEditing = true;
            });

            cancelProfileBtn.addEventListener('click', () => {
                const inputs = profileForm.querySelectorAll('input:not([type="hidden"])');
                inputs.forEach(input => {
                    input.disabled = true;
                    input.value = originalValues[input.id];
                });
                editProfileBtn.classList.remove('hidden');
                updateProfileBtn.classList.add('hidden');
                cancelProfileBtn.classList.add('hidden');
                isEditing = false;
                // Reload the page to fetch original data
                window.location.href = window.location.href;
            });

            //handle error.
            <% if (errorMessage != null) { %>
            const inputs = profileForm.querySelectorAll('input:not([type="hidden"])');
            inputs.forEach(input => input.disabled = false);
            editProfileBtn.classList.add('hidden');
            updateProfileBtn.classList.remove('hidden');
            cancelProfileBtn.classList.remove('hidden');
            isEditing = true;
            <% }%>

            changePasswordBtn.addEventListener('click', () => {
                passwordModal.classList.remove('hidden');
                document.getElementById('blurrable-content').classList.add('blur');
            });

            closeModalBtn.addEventListener('click', () => {
                passwordModal.classList.add('hidden');
                document.getElementById('blurrable-content').classList.remove('blur');
            });

            changeNumberBtn.addEventListener('click', () => {
                numberModal.classList.remove('hidden');
                document.getElementById('blurrable-content').classList.add('blur');
            });

            closeNumberModalBtn.addEventListener('click', () => {
                numberModal.classList.add('hidden');
                document.getElementById('blurrable-content').classList.remove('blur');
            });

            <% if (successMessage != null) {%>
            showMessageModal('success', '<%= successMessage%>');
            <% } else if (errorMessage != null) {%>
            showMessageModal('error', '<%= errorMessage%>');
            <% }%>

        </script>
    </body>
</html>

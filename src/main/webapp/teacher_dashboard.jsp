<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="com.rakibdevhub.iusping.model.StudentModel" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.TreeSet" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String successMessage = (String) session.getAttribute("success");
    String errorMessage = (String) session.getAttribute("error");

    String role = (String) request.getAttribute("role");
    List<StudentModel> students = (List<StudentModel>) request.getAttribute("students");

    // Get unique batches for the filter
    Set<String> uniqueBatches = new TreeSet<>();
    if (students != null) {
        for (StudentModel student : students) {
            uniqueBatches.add(student.getBatch());
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Teacher Dashboard | IUS Ping</title>
        <link rel="icon" href="<%= request.getContextPath()%>/fav-icon.ico" type="image/x-icon">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-100">

        <c:import url="/WEB-INF/components/navbar.jsp"/>

        <div class="max-w-5xl mx-auto mt-10 bg-white p-6 rounded-lg shadow-md text-center px-24">
            <h2 class="text-2xl font-bold mb-6">Teacher Dashboard</h2>

            <div class="mb-8">
                <div class="flex justify-between items-center mb-4">
                    <h3 class="text-lg font-semibold">Student Lists</h3>

                    <select id="batchFilter" class="border border-gray-300 px-3 py-2 rounded">
                        <option value="">All Batches</option>
                        <% for (String batch : uniqueBatches) {%>
                        <option value="<%= batch%>"><%= batch%></option>
                        <% }%>
                    </select>
                </div>

                <form action="<%= request.getContextPath()%>/teacher/sendMessage" method="get">
                    <table class="w-full border-collapse border border-gray-300">
                        <thead>
                            <tr>
                                <th class="border border-gray-300 p-2">
                                    <input type="checkbox" id="selectAllCheckbox">
                                </th>
                                <th class="border border-gray-300 p-2">Student ID</th>
                                <th class="border border-gray-300 p-2">Name</th>
                                <th class="border border-gray-300 p-2">Department</th>
                                <th class="border border-gray-300 p-2">Batch</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (students != null) {
                                    for (StudentModel student : students) {%>
                            <tr class="student-row" data-batch="<%= student.getBatch()%>">
                                <td class="border border-gray-300 p-2 text-center">
                                    <input type="checkbox" name="selectedStudents" value="<%= student.getId()%>">
                                </td>
                                <td class="border border-gray-300 p-2"><%= student.getStudentId()%></td>
                                <td class="border border-gray-300 p-2"><%= student.getName()%></td>
                                <td class="border border-gray-300 p-2"><%= student.getDepartment()%></td>
                                <td class="border border-gray-300 p-2"><%= student.getBatch()%></td>
                            </tr>
                            <% }
                                }%>
                        </tbody>
                    </table>
                    <div class="w-full flex justify-end mt-4">
                        <button type="submit" class="bg-blue-500 text-white px-4 py-2 rounded">Send Message</button>
                    </div>
                </form>
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
            const selectAllCheckbox = document.getElementById('selectAllCheckbox');
            const studentCheckboxes = document.querySelectorAll('input[name="selectedStudents"]');
            const batchFilter = document.getElementById('batchFilter');
            const studentRows = document.querySelectorAll('.student-row');

            // Function to get only visible checkboxes
            function getVisibleCheckboxes() {
                return [...studentCheckboxes].filter(checkbox => checkbox.closest('.student-row').style.display !== 'none');
            }

            // Update select all checkbox state when any checkbox changes
            studentCheckboxes.forEach(checkbox => {
                checkbox.addEventListener('change', () => {
                    const visibleCheckboxes = getVisibleCheckboxes();
                    selectAllCheckbox.checked = visibleCheckboxes.every(cb => cb.checked);
                });
            });

            // Select only visible checkboxes when "Select All" is clicked
            selectAllCheckbox.addEventListener('change', () => {
                const visibleCheckboxes = getVisibleCheckboxes();
                visibleCheckboxes.forEach(checkbox => {
                    checkbox.checked = selectAllCheckbox.checked;
                });
            });

            // Batch filter logic
            batchFilter.addEventListener('change', () => {
                const selectedBatch = batchFilter.value;
                studentRows.forEach(row => {
                    const rowBatch = row.dataset.batch;
                    if (selectedBatch === "" || rowBatch === selectedBatch) {
                        row.style.display = 'table-row';
                    } else {
                        row.style.display = 'none';
                    }
                });

                // Reset "Select All" checkbox state after filtering
                selectAllCheckbox.checked = false;
            });

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

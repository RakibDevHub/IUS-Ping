<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Setup</title>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-100 flex items-center justify-center min-h-screen">
        <div class="bg-white p-8 m-12 rounded shadow-md w-full max-w-3xl">
            <h1 class="text-2xl font-semibold mb-6 text-center">IUS Ping Configuration</h1>

            <form action="<%= request.getContextPath()%>/setup" method="post" class="space-y-4">
                <div id="error-message" class="text-red-500 mt-2">
                    <% if (request.getAttribute("errorMessage") != null) {%>
                    <%= request.getAttribute("errorMessage")%>
                    <% }%>
                </div>

                <div class="mb-6">
                    <h2 class="text-lg font-medium mb-4">Required Config Properties</h2>
                    <ul class="list-disc list-inside">
                        <li><strong>db.url</strong>: Database URL (e.g., jdbc:oracle:thin:@localhost:1521:XE)</li>
                        <li><strong>db.username</strong>: Database username</li>
                        <li><strong>db.password</strong>: Database password</li>
                        <li><strong>db.driver</strong>: Database driver class (e.g., oracle.jdbc.driver.OracleDriver)</li>
                        <li><strong>sms.endpoint</strong>: SMS service endpoint (e.g., http://192.168.0.104:8082/api/sms)</li>
                        <li><strong>sms.token</strong>: SMS service authentication token</li>
                    </ul>
                </div>

                <div class="mb-6">
                    <label class="block text-gray-700 text-sm font-bold mb-2" for="configPath">Config Properties File Path:</label>
                    <input class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" type="text" id="configPath" name="configPath" placeholder="/path/to/config.properties" required>
                </div>

                <div class="mb-6">
                    <h2 class="text-xl font-medium mb-4">Admin Configuration</h2>
                    <label class="block text-gray-700 text-sm font-bold mb-2" for="adminEmail">Admin Email:</label>
                    <input class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" type="email" id="adminEmail" name="adminEmail" required>
                </div>

                <div class="mb-6">
                    <label class="block text-gray-700 text-sm font-bold mb-2" for="adminPassword">Admin Password:</label>
                    <input class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" type="password" id="adminPassword" name="adminPassword" required>
                </div>

                <div class="flex items-center justify-between">
                    <button class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline" type="submit">Submit</button>
                </div>
            </form>
        </div>
        <script>
            document.querySelector('form').addEventListener('submit', function (event) {
                const inputs = document.querySelectorAll('input[required]');
                for (const input of inputs) {
                    if (!input.value.trim()) {
                        alert('Please fill in all required fields.');
                        event.preventDefault();
                        return;
                    }
                }
            });
        </script>
    </body>
</html>
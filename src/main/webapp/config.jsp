<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Configuration | IUS Ping</title>
        <script src="https://cdn.tailwindcss.com"></script>
        <script>
            function toggleAdminForm() {
                document.getElementById('adminFormPopup').classList.toggle('hidden');
            }
        </script>
    </head>
    <body class="bg-gray-100 flex items-center justify-center m-12">
        <div class="bg-white p-8 rounded shadow-md w-2/5">
            <h2 class="text-2xl font-semibold mb-6 text-center">Configuration</h2>
            <form action="config" method="post" class="space-y-4">
                <div class="flex justify-center mb-4 gap-2">
                    <button type="submit" name="action" value="testConnection_User" class="bg-indigo-500 hover:bg-indigo-600 text-white font-semibold py-2 px-4 rounded">Test User Connection</button>
                    <button type="submit" name="action" value="testConnection_Admin" class="bg-indigo-500 hover:bg-indigo-600 text-white font-semibold py-2 px-4 rounded">Test Admin Connection</button>

                    <button type="button" onclick="toggleAdminForm()" class="bg-green-500 hover:bg-green-600 text-white font-semibold py-2 px-4 rounded">Add Admin</button>
                </div>
                <p class="text-red-500 mt-4">${connectionError}</p>
                <p class="text-green-500 mt-4">${connectionSuccess ? "Connection Successful" : ""}</p>
                <p class="text-red-500 mt-4">${adminError}</p>
                <p class="text-green-500 mt-4">${adminSuccess ? "Admin inserted successfully" : ""}</p>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Database Driver</label>
                    <input type="text" name="dbDriver" value="${dbDriver}" class="w-full p-2 border rounded" required>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Database URL</label>
                    <input type="text" name="dbUrl" value="${dbUrl}" class="w-full p-2 border rounded" required>
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Database User Name</label>
                    <input type="text" name="dbUser_User" value="${dbUser_User}" class="w-full p-2 border rounded" required>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Database User Password</label>
                    <input type="text" name="dbPassword_User" value="${dbPassword_User}" class="w-full p-2 border rounded" required>
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Database Master Name</label>
                    <input type="text" name="dbUser_Admin" value="${dbUser_Admin}" class="w-full p-2 border rounded" required>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Database Master Password</label>
                    <input type="text" name="dbPassword_Admin" value="${dbPassword_Admin}" class="w-full p-2 border rounded" required>
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">SMS Endpoint</label>
                    <input type="text" name="smsEndpoint" value="${smsEndpoint}" class="w-full p-2 border rounded" required>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">SMS Token</label>
                    <input type="text" name="smsToken" value="${smsToken}" class="w-full p-2 border rounded" required>
                </div>
                <div class="flex justify-center">
                    <button type="submit" name="action" value="saveDatabaseConfig" class="bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded">Save Config</button>
                </div>
            </form>
            <div id="adminFormPopup" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full hidden">
                <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
                    <div class="relative">
                        <button onclick="toggleAdminForm()" class="absolute -top-4 -right-2 text-gray-800">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                            </svg>
                        </button>
                        <div class="mt-3 text-center">
                            <h3 class="text-lg leading-6 font-medium text-gray-900">Add Admin</h3>
                            <div class="mt-2 px-7 py-3">
                                <form action="config" method="post" class="space-y-4">
                                    <input type="hidden" name="dbDriver" value="${dbDriver}">
                                    <input type="hidden" name="dbUrl" value="${dbUrl}">
                                    <input type="hidden" name="dbUser_User" value="${dbUser_User}">
                                    <input type="hidden" name="dbPassword_User" value="${dbPassword_User}">
                                    <input type="hidden" name="dbUser_Admin" value="${dbUser_Admin}">
                                    <input type="hidden" name="dbPassword_Admin" value="${dbPassword_Admin}">
                                    <input type="hidden" name="smsEndpoint" value="${smsEndpoint}">
                                    <input type="hidden" name="smsToken" value="${smsToken}">
                                    <div>
                                        <label class="block text-sm font-medium text-gray-700 mb-2">Admin Email</label>
                                        <input type="email" name="adminEmail" class="w-full p-2 border rounded" required>
                                    </div>
                                    <div>
                                        <label class="block text-sm font-medium text-gray-700 mb-2">Admin Password</label>
                                        <input type="password" name="adminPassword" class="w-full p-2 border rounded" required>
                                    </div>
                                    <div class="items-center px-4 py-3">
                                        <button type="submit" name="action" value="createAdmin" class="w-full bg-green-500 hover:bg-green-600 text-white font-semibold py-2 px-4 rounded">Create Admin</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
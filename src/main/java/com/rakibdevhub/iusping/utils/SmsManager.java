//package com.ius.ping.util;
//
//import com.google.gson.Gson;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.util.Properties;
//
//public class SmsManager {
//
//    private static final Logger logger = LoggerFactory.getLogger(SmsManager.class);
//    private static String TRACCAR_ENDPOINT;
//    private static String AUTH_TOKEN;
//    private static final Gson gson = new Gson();
//    private static final String AUTHORIZATION_HEADER = "Authorization";
//    private static final String CONTENT_TYPE_HEADER = "Content-Type";
//    private static final String CONTENT_TYPE_JSON = "application/json";
//    private static final int CONNECT_TIMEOUT = 5000;
//    private static final int READ_TIMEOUT = 5000;
//
//    static {
//        Properties props = new Properties();
//        try (InputStream input = SmsManager.class.getClassLoader().getResourceAsStream("sms.properties")) {
//            if (input == null) {
//                throw new RuntimeException("sms.properties file not found.");
//            }
//            props.load(input);
//
//            TRACCAR_ENDPOINT = props.getProperty("traccar.endpoint");
//            AUTH_TOKEN = props.getProperty("traccar.auth.token");
//
//        } catch (IOException e) {
//            throw new RuntimeException("Error loading SMS properties", e);
//        }
//    }
//
//    public static boolean sendSMS(String phoneNumber, String message) {
//        HttpURLConnection conn = null;
//
//        try {
//            URL url = new URL(TRACCAR_ENDPOINT);
//            logger.info("Sending SMS to URL: {}", TRACCAR_ENDPOINT);
//
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty(AUTHORIZATION_HEADER, AUTH_TOKEN);
//            conn.setRequestProperty(CONTENT_TYPE_HEADER, CONTENT_TYPE_JSON);
//            conn.setDoOutput(true);
//            conn.setConnectTimeout(CONNECT_TIMEOUT);
//            conn.setReadTimeout(READ_TIMEOUT);
//
//            String jsonInput = gson.toJson(new SMSRequest(phoneNumber, message));
//            try (OutputStream os = conn.getOutputStream()) {
//                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
//            }
//
//            int responseCode = conn.getResponseCode();
//            logger.info("Traccar Response Code: {}", responseCode);
//
//            String responseMessage = readResponse(conn, responseCode);
//            logger.info("Traccar Response Body: {}", responseMessage);
//
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                // Handle empty response body as success
//                if (responseMessage.isEmpty()) {
//                    logger.info("SMS sent successfully (empty response body).");
//                    return true;
//                }
//                // If there's a body, try to parse it (though it's unexpected here)
//                return handleJsonResponse(responseMessage);
//            }
//
//            if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
//                logger.error("Authentication failed. Please verify your Traccar API Key.");
//                return false;
//            }
//
//            logger.error("Traccar API returned error code: {}, Response Body: {}", responseCode, responseMessage);
//            return false;
//
//        } catch (IOException e) {
//            logger.error("Error sending SMS via Traccar", e);
//            return false;
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//    }
//
//    private static String readResponse(HttpURLConnection conn, int responseCode) throws IOException {
//        InputStream stream = (responseCode == HttpURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream();
//        if (stream == null) {
//            return "";
//        }
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
//            return reader.lines().reduce("", (acc, line) -> acc + line);
//        }
//    }
//
//    private static boolean handleJsonResponse(String responseMessage) {
//        try {
//            // Attempt to parse JSON (though it's unexpected)
//            SMSResponse response = gson.fromJson(responseMessage, SMSResponse.class);
//            if (response != null && "success".equalsIgnoreCase(response.status)) {
//                return true;
//            } else {
//                logger.error("SMS failed: {}", response != null ? response.error : "Unknown error");
//                return false;
//            }
//        } catch (Exception e) {
//            logger.error("Failed to parse Traccar API response (unexpected JSON)", e);
//            return false;
//        }
//    }
//
//    private static class SMSResponse {
//
//        String status;
//        String error;
//    }
//
//    private static class SMSRequest {
//
//        String to;
//        String message;
//
//        SMSRequest(String to, String message) {
//            this.to = to;
//            this.message = message;
//        }
//    }
//}
package com.rakibdevhub.iusping.utils;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class SmsManager {

    private static final Logger logger = LoggerFactory.getLogger(SmsManager.class);
    private static String TRACCAR_ENDPOINT;
    private static String AUTH_TOKEN;
    private static final Gson gson = new Gson();
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    static {
        Properties props = new Properties();
        try (InputStream input = SmsManager.class.getClassLoader().getResourceAsStream("sms.properties")) {
            if (input == null) {
                throw new RuntimeException("sms.properties file not found.");
            }
            props.load(input);

            TRACCAR_ENDPOINT = props.getProperty("traccar.endpoint");
            AUTH_TOKEN = props.getProperty("traccar.auth.token");

        } catch (IOException e) {
            throw new RuntimeException("Error loading SMS properties", e);
        }
    }

    public static boolean sendSMS(String phoneNumber, String message) {
        HttpURLConnection conn = null;
        String requestLogId = generateRequestId(); // Generate a unique ID for this request

        try {
            URL url = new URL(TRACCAR_ENDPOINT);
            logger.info("[{}] Sending SMS to URL: {}", requestLogId, TRACCAR_ENDPOINT); // Include request ID in log

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty(AUTHORIZATION_HEADER, AUTH_TOKEN);
            conn.setRequestProperty(CONTENT_TYPE_HEADER, CONTENT_TYPE_JSON);
            conn.setDoOutput(true);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

            String jsonInput = gson.toJson(new SMSRequest(phoneNumber, message));
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            logger.info("[{}] Traccar Response Code: {}", requestLogId, responseCode);

            //  Do NOT log the response body directly!
            String responseMessage = readResponse(conn, responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                logger.info("[{}] SMS sent successfully.", requestLogId); // More generic success message
                return true; // Assume success if 200 OK
            }

            if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                logger.error("[{}] Authentication failed. Please verify your Traccar API Key.", requestLogId);
                return false;
            }

            // Log error with request ID and response code, BUT NOT the body
            logger.error("[{}] Traccar API returned error code: {}", requestLogId, responseCode);
            return false;

        } catch (IOException e) {
            logger.error("[{}] Error sending SMS via Traccar", requestLogId, e); // Include request ID
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String readResponse(HttpURLConnection conn, int responseCode) throws IOException {
        InputStream stream = (responseCode == HttpURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream();
        if (stream == null) {
            return "";
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return reader.lines().reduce("", (acc, line) -> acc + line);
        }
    }

    private static String generateRequestId() {
        return java.util.UUID.randomUUID().toString(); // Simple UUID generation
    }

    private static class SMSRequest {

        String to;
        String message;

        SMSRequest(String to, String message) {
            this.to = to;
            this.message = message;
        }
    }
}

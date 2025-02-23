package com.rakibdevhub.iusping.utils;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
    private static final int READ_TIMEOUT = 15000;

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

    public static boolean sendBulkSMS(List<String> phoneNumbers, String message) {
        boolean allMessagesSent = true;

        for (String phoneNumber : phoneNumbers) {
            boolean sent = sendSMS(phoneNumber, message);
            if (!sent) {
                allMessagesSent = false;
            }
        }

        return allMessagesSent;
    }

    public static boolean sendSMS(String phoneNumber, String message) {
        HttpURLConnection conn = null;
        String requestLogId = generateRequestId();

        try {
            URL url = new URL(TRACCAR_ENDPOINT);
            logger.info("[{}] Sending SMS to {}", requestLogId, phoneNumber);

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
            logger.info("[{}] Response Code: {}", requestLogId, responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                logger.info("[{}] SMS sent successfully.", requestLogId);
                return true;
            }

            logger.error("[{}] Failed to send SMS. Response Code: {}", requestLogId, responseCode);
            return false;

        } catch (IOException e) {
            logger.error("[{}] Error sending SMS", requestLogId, e);
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String generateRequestId() {
        return java.util.UUID.randomUUID().toString();
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

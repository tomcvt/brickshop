package com.tomcvt.brickshop.utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class TestUtils {
    public static void sendInvalidSessionRequest(String targetUrl) throws Exception {
    URL url = new URL(targetUrl);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Cookie", "JSESSIONID=INVALIDSESSIONID123");
    int responseCode = conn.getResponseCode();
    System.out.println("Response Code: " + responseCode);
/*
    // Print response headers
    for (Map.Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
        System.out.println(header.getKey() + ": " + header.getValue());
    }
        */
 /*
    // Print response body
    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine).append("\n");
        }
        System.out.println("Response Body:\n" + response);
    } catch (Exception e) {
        System.out.println("No response body or error: " + e.getMessage());
    }
        */
    conn.disconnect();
}
}

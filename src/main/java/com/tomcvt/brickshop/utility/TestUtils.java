package com.tomcvt.brickshop.utility;

import java.net.HttpURLConnection;
import java.net.URL;

public class TestUtils {
    public static void sendInvalidSessionRequest(String targetUrl) throws Exception {
        URL url = new URL(targetUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setInstanceFollowRedirects(false);
        conn.setRequestProperty("Cookie", "JSESSIONID=INVALIDSESSIONID123");
        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        String setCookie = conn.getHeaderField("Set-Cookie");
        System.out.println("Set-Cookie Header: " + setCookie);

        conn.disconnect();
    }
}

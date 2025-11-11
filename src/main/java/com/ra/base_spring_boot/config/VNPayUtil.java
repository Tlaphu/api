package com.ra.base_spring_boot.config;

import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

public class VNPayUtil {

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA512");
            hmacSHA512.init(secretKey);
            byte[] result = hmacSHA512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException | InvalidKeyException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }


    public static Map<String, String> sortParams(Map<String, String> params) {

        Map<String, String> sortedParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedParams.putAll(params);
        return sortedParams;
    }


    public static String buildHashData(Map<String, String> sortedParams) throws UnsupportedEncodingException {
        StringBuilder hashData = new StringBuilder();

        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append("=");
                // ✅ Sử dụng UTF-8 cho mã hóa
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                hashData.append("&");
            }
        }
        if (hashData.length() > 0) {
            hashData.setLength(hashData.length() - 1);
        }
        return hashData.toString();
    }


    public static String createQueryUrl(Map<String, String> sortedParams) throws UnsupportedEncodingException {

        StringBuilder query = new StringBuilder();

        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                query.append(fieldName);
                query.append("=");

                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                query.append("&");
            }
        }
        if (query.length() > 0) {
            query.setLength(query.length() - 1);
        }
        return query.toString();
    }
}
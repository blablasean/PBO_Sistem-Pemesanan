package com.example.backend.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static Map<String, String> parseJsonBody(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8).trim();
        Map<String, String> map = new HashMap<>();

        if (body.isEmpty() || !body.startsWith("{") || !body.endsWith("}")) {
            return map;
        }

        body = body.substring(1, body.length() - 1).trim();

        if (body.isEmpty()) {
            return map;
        }

        String[] pairs = body.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 2);

            if (kv.length != 2) {
                continue;
            }

            String key = trimJson(kv[0]);
            String value = trimJson(kv[1]);
            map.put(key, value);
        }

        return map;
    }

    private static String trimJson(String value) {
        String result = value.trim();

        if (result.startsWith("\"") && result.endsWith("\"") && result.length() >= 2) {
            result = result.substring(1, result.length() - 1);
            result = result.replace("\\\"", "\"").replace("\\\\", "\\");
        }

        return result;
    }

    public static void sendJson(HttpExchange exchange, int status, Object data) throws IOException {
        String body = toJson(data);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        sendJson(exchange, 405, Map.of("success", false, "message", "Metode tidak diizinkan."));
    }

    public static void sendNotFound(HttpExchange exchange) throws IOException {
        sendJson(exchange, 404, Map.of("success", false, "message", "Halaman tidak ditemukan."));
    }

    @SuppressWarnings("unchecked")
    private static String toJson(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof String string) {
            return quote(string);
        }

        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }

        if (value instanceof Map) {
            StringBuilder builder = new StringBuilder();
            builder.append('{');

            boolean first = true;
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                if (!first) {
                    builder.append(',');
                }

                builder.append(quote(entry.getKey()))
                        .append(':')
                        .append(toJson(entry.getValue()));

                first = false;
            }

            builder.append('}');
            return builder.toString();
        }

        if (value instanceof List) {
            StringBuilder builder = new StringBuilder();
            builder.append('[');

            boolean first = true;
            for (Object item : (List<Object>) value) {
                if (!first) {
                    builder.append(',');
                }

                builder.append(toJson(item));
                first = false;
            }

            builder.append(']');
            return builder.toString();
        }

        return quote(value.toString());
    }

    private static String quote(String text) {
        return '"' + text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                + '"';
    }
}
package com.example.backend.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.example.backend.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

public final class StaticFileHandler {
    private StaticFileHandler() {
    }

    public static void handleStatic(HttpExchange exchange, Path webRoot) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();

        if (requestPath.equals("/")) {
            sendRedirect(exchange, "/login.html");
            return;
        }

        Path resolved = webRoot.resolve(requestPath.substring(1)).normalize();

        if (!resolved.startsWith(webRoot) || !Files.exists(resolved) || Files.isDirectory(resolved)) {
            JsonUtil.sendNotFound(exchange);
            return;
        }

        String contentType = URLConnection.guessContentTypeFromName(resolved.toString());
        byte[] body = Files.readAllBytes(resolved);

        exchange.getResponseHeaders().set("Content-Type", contentType != null ? contentType : "application/octet-stream");
        exchange.sendResponseHeaders(200, body.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }

    public static Path locateWebRoot() {
        Path current = Paths.get("").toAbsolutePath().normalize();

        for (int i = 0; i < 5; i++) {
            Path candidate = current.resolve("web").normalize();

            if (Files.isDirectory(candidate)) {
                return candidate;
            }

            current = current.getParent();

            if (current == null) {
                break;
            }
        }

        return Paths.get("web").toAbsolutePath().normalize();
    }

    private static void sendRedirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1);
    }
}
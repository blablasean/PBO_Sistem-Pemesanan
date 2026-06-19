package com.example.backend.handler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import com.example.backend.database.DatabaseHelper;
import com.example.backend.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

public final class AuthHandler {
    private AuthHandler() {
    }

    public static void handleLogin(HttpExchange exchange, DatabaseHelper db) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            JsonUtil.sendMethodNotAllowed(exchange);
            return;
        }

        try {
            Map<String, String> request = JsonUtil.parseJsonBody(exchange);
            String email = request.get("email");
            String password = request.get("password");

            if (email == null || password == null) {
                JsonUtil.sendJson(exchange, 400, Map.of(
                        "success", false,
                        "message", "Email dan password wajib diisi."
                ));
                return;
            }

            Map<String, Object> user = db.getUserByEmail(email);

            if (user == null || !password.equals(user.get("password"))) {
                JsonUtil.sendJson(exchange, 401, Map.of(
                        "success", false,
                        "message", "Email atau password salah."
                ));
                return;
            }

            JsonUtil.sendJson(exchange, 200, Map.of(
                    "success", true,
                    "userId", user.get("id"),
                    "name", user.get("name"),
                    "role", user.get("role")
            ));
        } catch (SQLException e) {
            JsonUtil.sendJson(exchange, 500, Map.of(
                    "success", false,
                    "message", "Terjadi kesalahan database."
            ));
        }
    }

    public static void handleRegister(HttpExchange exchange, DatabaseHelper db) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            JsonUtil.sendMethodNotAllowed(exchange);
            return;
        }

        try {
            Map<String, String> request = JsonUtil.parseJsonBody(exchange);
            String name = request.get("name");
            String email = request.get("email");
            String password = request.get("password");

            if (name == null || email == null || password == null) {
                JsonUtil.sendJson(exchange, 400, Map.of(
                        "success", false,
                        "message", "Nama, email, dan password wajib diisi."
                ));
                return;
            }

            if (db.getUserByEmail(email) != null) {
                JsonUtil.sendJson(exchange, 409, Map.of(
                        "success", false,
                        "message", "Email sudah terdaftar."
                ));
                return;
            }

            String userId = generateId("u");
            db.insertUser(userId, name, email, "Mahasiswa", password);

            JsonUtil.sendJson(exchange, 201, Map.of(
                    "success", true,
                    "message", "Pendaftaran berhasil."
            ));
        } catch (SQLException e) {
            JsonUtil.sendJson(exchange, 500, Map.of(
                    "success", false,
                    "message", "Gagal menyimpan pengguna."
            ));
        }
    }

    private static String generateId(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
package com.example.backend.handler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.example.backend.database.DatabaseHelper;
import com.example.backend.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

public final class RiwayatHandler {
    private RiwayatHandler() {
    }

    public static void handleRiwayat(HttpExchange exchange, DatabaseHelper db) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            JsonUtil.sendMethodNotAllowed(exchange);
            return;
        }

        try {
            List<Map<String, Object>> records = db.getAllTransaksi();
            JsonUtil.sendJson(exchange, 200, records);
        } catch (SQLException e) {
            JsonUtil.sendJson(exchange, 500, Map.of(
                    "success", false,
                    "message", "Gagal memuat riwayat."
            ));
        }
    }
}
package com.example.backend.handler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import com.example.backend.database.DatabaseHelper;
import com.example.backend.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

public final class PeminjamanHandler {
    private PeminjamanHandler() {
    }

    public static void handlePeminjaman(HttpExchange exchange, DatabaseHelper db) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            JsonUtil.sendMethodNotAllowed(exchange);
            return;
        }

        try {
            Map<String, String> request = JsonUtil.parseJsonBody(exchange);

            String userId = request.get("userId");
            String barangId = request.get("barang_id");
            String borrowDate = request.get("borrow_date");
            String returnDate = request.get("return_date");
            String status = request.getOrDefault("status", "Dipinjam");
            int quantity = parseInt(request.get("quantity"), 1);
            String note = request.getOrDefault("note", "");

            if (userId == null || barangId == null || borrowDate == null || returnDate == null) {
                JsonUtil.sendJson(exchange, 400, Map.of(
                        "success", false,
                        "message", "Data peminjaman tidak lengkap."
                ));
                return;
            }

            Map<String, Object> barang = db.getBarangById(barangId);

            if (barang == null) {
                JsonUtil.sendJson(exchange, 404, Map.of(
                        "success", false,
                        "message", "Barang tidak ditemukan."
                ));
                return;
            }

            if (!"Tersedia".equalsIgnoreCase(barang.get("status").toString())) {
                JsonUtil.sendJson(exchange, 409, Map.of(
                        "success", false,
                        "message", "Barang sedang tidak tersedia untuk dipinjam."
                ));
                return;
            }

            String transactionId = generateId("t");
            double totalCost = calculateTotalCost(barang, quantity);

            db.insertTransaksi(transactionId, userId, barangId, borrowDate, returnDate,
                    status, quantity, note, totalCost);

            JsonUtil.sendJson(exchange, 201, Map.of(
                    "success", true,
                    "message", "Peminjaman dicatat.",
                    "barangId", barangId
            ));
        } catch (SQLException e) {
            JsonUtil.sendJson(exchange, 500, Map.of(
                    "success", false,
                    "message", "Gagal menyimpan data peminjaman."
            ));
        }
    }

    private static double calculateTotalCost(Map<String, Object> barang, int quantity) {
        Object price = barang.get("price");

        if (price == null) {
            return 0;
        }

        return Double.parseDouble(price.toString()) * quantity;
    }

    private static int parseInt(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return Integer.parseInt(value);
    }

    private static String generateId(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
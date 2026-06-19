package com.example.backend.handler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.backend.database.DatabaseHelper;
import com.example.backend.model.Barang;
import com.example.backend.model.Kamera;
import com.example.backend.model.Motor;
import com.example.backend.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

public final class BarangHandler {
    private static final String BASE_PATH = "/api/barang";

    private BarangHandler() {
    }

    public static void handleBarang(HttpExchange exchange, DatabaseHelper db) throws IOException {
        String method = exchange.getRequestMethod();
        String id = extractId(exchange);

        try {
            if (method.equalsIgnoreCase("GET") && id == null) {
                handleGetAll(exchange, db);
                return;
            }

            if (method.equalsIgnoreCase("POST") && id == null) {
                handleCreate(exchange, db);
                return;
            }

            if (id != null && method.equalsIgnoreCase("PUT")) {
                handleUpdate(exchange, db, id);
                return;
            }

            if (id != null && method.equalsIgnoreCase("DELETE")) {
                handleDelete(exchange, db, id);
                return;
            }

            JsonUtil.sendMethodNotAllowed(exchange);
        } catch (SQLException e) {
            JsonUtil.sendJson(exchange, 500, Map.of(
                    "success", false,
                    "message", "Kesalahan database saat memproses barang."
            ));
        }
    }

    private static void handleGetAll(HttpExchange exchange, DatabaseHelper db) throws IOException, SQLException {
        List<Map<String, Object>> items = db.getAllBarang();
        JsonUtil.sendJson(exchange, 200, items);
    }

    private static void handleCreate(HttpExchange exchange, DatabaseHelper db) throws IOException, SQLException {
        Map<String, String> request = JsonUtil.parseJsonBody(exchange);
        Barang barang = createBarangFromRequest(generateId("b"), request);

        if (barang == null) {
            JsonUtil.sendJson(exchange, 400, Map.of(
                    "success", false,
                    "message", "Kategori harus Motor atau Kamera."
            ));
            return;
        }

        String imageUrl = request.get("image_url");
        String imageData = request.get("image_data");

        barang.setImageUrl(imageUrl != null && !imageUrl.isBlank()
                ? imageUrl
                : "https://via.placeholder.com/400x300?text=" + barang.getCategory());
        barang.setImageData(imageData != null && !imageData.isBlank() ? imageData : null);

        saveBarang(db, barang);
        JsonUtil.sendJson(exchange, 201, Map.of("success", true, "id", barang.getId()));
    }

    private static void handleUpdate(HttpExchange exchange, DatabaseHelper db, String id)
            throws IOException, SQLException {
        Map<String, Object> existingBarang = db.getBarangById(id);

        if (existingBarang == null) {
            JsonUtil.sendJson(exchange, 404, Map.of(
                    "success", false,
                    "message", "Barang tidak ditemukan."
            ));
            return;
        }

        Map<String, String> request = JsonUtil.parseJsonBody(exchange);
        Barang barang = createBarangFromExisting(id, request, existingBarang);

        if (barang == null) {
            JsonUtil.sendJson(exchange, 400, Map.of(
                    "success", false,
                    "message", "Kategori harus Motor atau Kamera."
            ));
            return;
        }

        setEditedImage(request, existingBarang, barang);
        updateBarang(db, barang);

        JsonUtil.sendJson(exchange, 200, Map.of(
                "success", true,
                "message", "Barang diperbarui."
        ));
    }

    private static void handleDelete(HttpExchange exchange, DatabaseHelper db, String id)
            throws IOException, SQLException {
        db.deleteTransaksiByBarangId(id);
        db.deleteBarang(id);

        JsonUtil.sendJson(exchange, 200, Map.of(
                "success", true,
                "message", "Barang dihapus."
        ));
    }

    private static Barang createBarangFromRequest(String id, Map<String, String> request) throws IOException {
        String name = request.get("name");
        String category = request.get("category");
        String status = request.get("status");

        if (name == null || category == null || status == null) {
            return null;
        }

        String merk = request.getOrDefault("merk", "Unknown");
        double price = parseDouble(request.get("price"), 0);
        int cc = parseInt(request.get("cc"), 0);
        String tipe = request.getOrDefault("tipe", "Standard");
        double megapixel = parseDouble(request.get("megapixel"), 0.0);
        String resolusi = request.getOrDefault("resolusi", "Unknown");

        return createBarang(id, name, merk, price, status, category, cc, tipe, megapixel, resolusi);
    }

    private static Barang createBarangFromExisting(String id, Map<String, String> request,
                                                   Map<String, Object> existingBarang) throws IOException {
        String name = request.getOrDefault("name", existingBarang.get("name").toString());
        String category = request.getOrDefault("category", existingBarang.get("category").toString());
        String status = request.getOrDefault("status", existingBarang.get("status").toString());
        String merk = request.getOrDefault("merk", valueOrDefault(existingBarang.get("merk"), "Unknown"));
        double price = parseDouble(request.get("price"), parseDouble(existingBarang.get("price"), 0));
        int cc = parseInt(request.get("cc"), parseInt(existingBarang.get("cc"), 0));
        String tipe = request.getOrDefault("tipe", valueOrDefault(existingBarang.get("tipe"), "Standard"));
        double megapixel = parseDouble(request.get("megapixel"), parseDouble(existingBarang.get("megapixel"), 0.0));
        String resolusi = request.getOrDefault("resolusi", valueOrDefault(existingBarang.get("resolusi"), "Unknown"));

        return createBarang(id, name, merk, price, status, category, cc, tipe, megapixel, resolusi);
    }

    private static Barang createBarang(String id, String name, String merk, double price,
                                       String status, String category, int cc, String tipe,
                                       double megapixel, String resolusi) {
        if ("Motor".equalsIgnoreCase(category)) {
            return new Motor(id, name, merk, price, status, cc, tipe);
        }

        if ("Kamera".equalsIgnoreCase(category)) {
            return new Kamera(id, name, merk, price, status, megapixel, resolusi);
        }

        return null;
    }

    private static void setEditedImage(Map<String, String> request, Map<String, Object> existingBarang, Barang barang) {
        String imageUrl = request.get("image_url");
        String imageData = request.get("image_data");

        if ((imageUrl == null || imageUrl.isBlank()) && (imageData == null || imageData.isBlank())) {
            imageUrl = valueOrDefault(existingBarang.get("image_url"),
                    "https://via.placeholder.com/400x300?text=" + barang.getCategory());
            imageData = valueOrDefault(existingBarang.get("image_data"), null);
        }

        barang.setImageUrl(imageUrl);
        barang.setImageData(imageData);
    }

    private static void saveBarang(DatabaseHelper db, Barang barang) throws SQLException {
        db.insertBarang(barang.getId(), barang.getNama(), barang.getMerk(), barang.getStatusPeminjaman(),
                barang.getCategory(), barang.getHarga(), barang.getImageUrl(), barang.getImageData(),
                barang instanceof Motor motor ? motor.getCc() : 0,
                barang instanceof Motor motor ? motor.getTipe() : null,
                barang instanceof Kamera kamera ? kamera.getMegapixel() : 0.0,
                barang instanceof Kamera kamera ? kamera.getResolusi() : null);
    }

    private static void updateBarang(DatabaseHelper db, Barang barang) throws SQLException {
        db.updateBarang(barang.getId(), barang.getNama(), barang.getMerk(), barang.getStatusPeminjaman(),
                barang.getCategory(), barang.getHarga(), barang.getImageUrl(), barang.getImageData(),
                barang instanceof Motor motor ? motor.getCc() : 0,
                barang instanceof Motor motor ? motor.getTipe() : null,
                barang instanceof Kamera kamera ? kamera.getMegapixel() : 0.0,
                barang instanceof Kamera kamera ? kamera.getResolusi() : null);
    }

    private static String extractId(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();

        if (path.length() > BASE_PATH.length()) {
            return path.substring(BASE_PATH.length() + 1);
        }

        return null;
    }

    private static String generateId(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private static String valueOrDefault(Object value, String defaultValue) {
        return value != null ? value.toString() : defaultValue;
    }

    private static int parseInt(Object value, int defaultValue) {
        if (value == null || value.toString().isBlank()) {
            return defaultValue;
        }

        return Integer.parseInt(value.toString());
    }

    private static double parseDouble(Object value, double defaultValue) {
        if (value == null || value.toString().isBlank()) {
            return defaultValue;
        }

        return Double.parseDouble(value.toString());
    }
}
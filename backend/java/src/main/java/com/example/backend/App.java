package com.example.backend;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class App {
    private static final int PORT = 8080;
    private static final Path WEB_ROOT = locateWebRoot();

    public static void main(String[] args) throws Exception {
        DatabaseHelper db = new DatabaseHelper();
        db.initSchema();
        seedData(db);

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/login", exchange -> handleLogin(exchange, db));
        server.createContext("/api/register", exchange -> handleRegister(exchange, db));
        server.createContext("/api/barang", exchange -> handleBarang(exchange, db));
        server.createContext("/api/peminjaman", exchange -> handlePeminjaman(exchange, db));
        server.createContext("/api/riwayat", exchange -> handleRiwayat(exchange, db));
        server.createContext("/", App::handleStatic);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.printf("Server backend berjalan di http://localhost:%d/ (root web: %s)%n", PORT, WEB_ROOT);
    }

    private static void seedData(DatabaseHelper db) throws SQLException {
        db.insertUser("u1", "Mahasiswa Instansi", "user@instansi.com", "Mahasiswa", "12345678");
        db.insertUser("u2", "Admin Toko", "admin@toko.com", "Admin", "admin123");
        db.insertUser("u3", "Admin Telkom University", "admin@telkomuniversity.ac.id", "Admin", "123456");
        db.insertBarang("b1", "Kamera Sony", "Tersedia", "Elektronik", 3500000, "https://via.placeholder.com/400x300?text=Kamera", null);
        db.insertBarang("b2", "Meja Lipat", "Tersedia", "Furniture", 250000, "https://via.placeholder.com/400x300?text=Meja", null);
    }

    private static void handleStatic(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        if (requestPath.equals("/")) {
            sendRedirect(exchange, "/login.html");
            return;
        }

        Path resolved = WEB_ROOT.resolve(requestPath.substring(1)).normalize();
        if (!resolved.startsWith(WEB_ROOT) || !Files.exists(resolved) || Files.isDirectory(resolved)) {
            sendNotFound(exchange);
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

    private static void handleLogin(HttpExchange exchange, DatabaseHelper db) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendMethodNotAllowed(exchange);
            return;
        }
        try {
            Map<String, String> request = parseJsonBody(exchange);
            String email = request.get("email");
            String password = request.get("password");
            if (email == null || password == null) {
                sendJson(exchange, 400, Map.of("success", false, "message", "Email dan password wajib diisi."));
                return;
            }
            Map<String, Object> user = db.getUserByEmail(email);
            if (user == null || !password.equals(user.get("password"))) {
                sendJson(exchange, 401, Map.of("success", false, "message", "Email atau password salah."));
                return;
            }
            sendJson(exchange, 200, Map.of(
                    "success", true,
                    "userId", user.get("id"),
                    "name", user.get("name"),
                    "role", user.get("role")
            ));
        } catch (SQLException e) {
            sendJson(exchange, 500, Map.of("success", false, "message", "Terjadi kesalahan database."));
        }
    }

    private static void handleRegister(HttpExchange exchange, DatabaseHelper db) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendMethodNotAllowed(exchange);
            return;
        }
        try {
            Map<String, String> request = parseJsonBody(exchange);
            String name = request.get("name");
            String email = request.get("email");
            String password = request.get("password");
            if (name == null || email == null || password == null) {
                sendJson(exchange, 400, Map.of("success", false, "message", "Nama, email, dan password wajib diisi."));
                return;
            }
            if (db.getUserByEmail(email) != null) {
                sendJson(exchange, 409, Map.of("success", false, "message", "Email sudah terdaftar."));
                return;
            }
            String userId = "u" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 8);
            db.insertUser(userId, name, email, "Mahasiswa", password);
            sendJson(exchange, 201, Map.of("success", true, "message", "Pendaftaran berhasil."));
        } catch (SQLException e) {
            sendJson(exchange, 500, Map.of("success", false, "message", "Gagal menyimpan pengguna."));
        }
    }

    private static void handleBarang(HttpExchange exchange, DatabaseHelper db) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String base = "/api/barang";
        String id = null;
        if (path.length() > base.length()) {
            id = path.substring(base.length() + 1);
        }
        try {
            if (method.equalsIgnoreCase("GET") && id == null) {
                List<Map<String, Object>> items = db.getAllBarang();
                sendJson(exchange, 200, items);
                return;
            }
            if (method.equalsIgnoreCase("POST") && id == null) {
                Map<String, String> request = parseJsonBody(exchange);
                String name = request.get("name");
                String category = request.get("category");
                String status = request.get("status");
                String imageUrl = request.get("image_url");
                String imageData = request.get("image_data");
                double price = request.getOrDefault("price", "0").isBlank() ? 0 : Double.parseDouble(request.get("price"));
                if (name == null || category == null || status == null) {
                    sendJson(exchange, 400, Map.of("success", false, "message", "Nama, kategori, dan status barang wajib diisi."));
                    return;
                }
                String itemId = "b" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 8);
                db.insertBarang(itemId, name, status, category, price,
                        imageUrl != null && !imageUrl.isBlank() ? imageUrl : "https://via.placeholder.com/400x300?text=Barang+Baru",
                        imageData != null && !imageData.isBlank() ? imageData : null);
                sendJson(exchange, 201, Map.of("success", true, "id", itemId));
                return;
            }
            if (id != null && method.equalsIgnoreCase("PUT")) {
                Map<String, String> request = parseJsonBody(exchange);
                Map<String, Object> existingBarang = db.getBarangById(id);
                if (existingBarang == null) {
                    sendJson(exchange, 404, Map.of("success", false, "message", "Barang tidak ditemukan."));
                    return;
                }
                String name = request.getOrDefault("name", existingBarang.get("name").toString());
                String category = request.getOrDefault("category", existingBarang.get("category").toString());
                String status = request.getOrDefault("status", existingBarang.get("status").toString());
                String imageUrl = request.get("image_url");
                String imageData = request.get("image_data");
                double price = request.getOrDefault("price", existingBarang.get("price").toString()).isBlank() ? 0 : Double.parseDouble(request.getOrDefault("price", existingBarang.get("price").toString()));
                if ((imageUrl == null || imageUrl.isBlank()) && (imageData == null || imageData.isBlank())) {
                    imageUrl = existingBarang.get("image_url") != null ? existingBarang.get("image_url").toString() : "https://via.placeholder.com/400x300?text=Barang";
                    imageData = existingBarang.get("image_data") != null ? existingBarang.get("image_data").toString() : null;
                }
                db.updateBarang(id, name, status, category, price,
                        imageUrl != null && !imageUrl.isBlank() ? imageUrl : "https://via.placeholder.com/400x300?text=Barang",
                        imageData);
                sendJson(exchange, 200, Map.of("success", true, "message", "Barang diperbarui."));
                return;
            }
            if (id != null && method.equalsIgnoreCase("DELETE")) {
                db.deleteBarang(id);
                sendJson(exchange, 200, Map.of("success", true, "message", "Barang dihapus."));
                return;
            }
            sendMethodNotAllowed(exchange);
        } catch (SQLException e) {
            sendJson(exchange, 500, Map.of("success", false, "message", "Kesalahan database saat memproses barang."));
        }
    }

    private static void handlePeminjaman(HttpExchange exchange, DatabaseHelper db) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendMethodNotAllowed(exchange);
            return;
        }
        try {
            Map<String, String> request = parseJsonBody(exchange);
            String userId = request.get("userId");
            String barangId = request.get("barang_id");
            String borrowDate = request.get("borrow_date");
            String returnDate = request.get("return_date");
            String status = request.getOrDefault("status", "Dipinjam");
            int quantity = Integer.parseInt(request.getOrDefault("quantity", "1"));
            String note = request.getOrDefault("note", "");
            if (userId == null || barangId == null || borrowDate == null || returnDate == null) {
                sendJson(exchange, 400, Map.of("success", false, "message", "Data peminjaman tidak lengkap."));
                return;
            }
            Map<String, Object> barang = db.getBarangById(barangId);
            if (barang == null) {
                sendJson(exchange, 404, Map.of("success", false, "message", "Barang tidak ditemukan."));
                return;
            }
            if (!"Tersedia".equalsIgnoreCase(barang.get("status").toString())) {
                sendJson(exchange, 409, Map.of("success", false, "message", "Barang sedang tidak tersedia untuk dipinjam."));
                return;
            }
            String transactionId = "t" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 8);
            double totalCost = 0;
            if (barang.get("price") != null) {
                totalCost = Double.parseDouble(barang.get("price").toString()) * quantity;
            }
            db.insertTransaksi(transactionId, userId, barangId, borrowDate, returnDate, status, quantity, note, totalCost);
            sendJson(exchange, 201, Map.of("success", true, "message", "Peminjaman dicatat.", "barangId", barangId));
        } catch (SQLException e) {
            sendJson(exchange, 500, Map.of("success", false, "message", "Gagal menyimpan data peminjaman."));
        }
    }

    private static void handleRiwayat(HttpExchange exchange, DatabaseHelper db) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            sendMethodNotAllowed(exchange);
            return;
        }
        try {
            List<Map<String, Object>> records = db.getAllTransaksi();
            sendJson(exchange, 200, records);
        } catch (SQLException e) {
            sendJson(exchange, 500, Map.of("success", false, "message", "Gagal memuat riwayat."));
        }
    }

    private static Map<String, String> parseJsonBody(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8).trim();
        Map<String, String> map = new HashMap<>();
        if (body.isEmpty() || !body.startsWith("{") || !body.endsWith("}")) {
            return map;
        }
        body = body.substring(1, body.length() - 1).trim();
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

    private static void sendJson(HttpExchange exchange, int status, Object data) throws IOException {
        String body = toJson(data);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
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
                if (!first) builder.append(',');
                builder.append(quote(entry.getKey())).append(':').append(toJson(entry.getValue()));
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
                if (!first) builder.append(',');
                builder.append(toJson(item));
                first = false;
            }
            builder.append(']');
            return builder.toString();
        }
        return quote(value.toString());
    }

    private static String quote(String text) {
        return '"' + text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + '"';
    }

    private static void sendRedirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1);
    }

    private static void sendNotFound(HttpExchange exchange) throws IOException {
        sendJson(exchange, 404, Map.of("success", false, "message", "Halaman tidak ditemukan."));
    }

    private static void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        sendJson(exchange, 405, Map.of("success", false, "message", "Metode tidak diizinkan."));
    }

    private static Path locateWebRoot() {
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
}

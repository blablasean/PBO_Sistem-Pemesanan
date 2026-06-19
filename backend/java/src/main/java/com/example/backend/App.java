package com.example.backend;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.concurrent.Executors;

import com.example.backend.database.DatabaseHelper;
import com.example.backend.handler.AuthHandler;
import com.example.backend.handler.BarangHandler;
import com.example.backend.handler.PeminjamanHandler;
import com.example.backend.handler.RiwayatHandler;
import com.example.backend.handler.StaticFileHandler;
import com.example.backend.model.Kamera;
import com.example.backend.model.Motor;
import com.sun.net.httpserver.HttpServer;

public class App {
    private static final int PORT = 8082;
    private static final Path WEB_ROOT = StaticFileHandler.locateWebRoot();

    public static void main(String[] args) throws Exception {
        DatabaseHelper db = new DatabaseHelper();
        db.initSchema();
        seedData(db);

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/login", exchange -> AuthHandler.handleLogin(exchange, db));
        server.createContext("/api/register", exchange -> AuthHandler.handleRegister(exchange, db));
        server.createContext("/api/barang", exchange -> BarangHandler.handleBarang(exchange, db));
        server.createContext("/api/peminjaman", exchange -> PeminjamanHandler.handlePeminjaman(exchange, db));
        server.createContext("/api/riwayat", exchange -> RiwayatHandler.handleRiwayat(exchange, db));
        server.createContext("/", exchange -> StaticFileHandler.handleStatic(exchange, WEB_ROOT));

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.printf("Server backend berjalan di http://localhost:%d/ (root web: %s)%n", PORT, WEB_ROOT);
    }

    private static void seedData(DatabaseHelper db) throws Exception {
        if (db.getUserByEmail("admin@toko.com") != null) {
            return;
        }

        db.insertUser("u1", "Mahasiswa Instansi", "user@instansi.com", "Mahasiswa", "12345678");
        db.insertUser("u2", "Admin Toko", "admin@toko.com", "Admin", "admin123");
        db.insertUser("u3", "Admin Telkom University", "admin@telkomuniversity.ac.id", "Admin", "123456");

        Kamera kamera = new Kamera("b1", "Kamera Sony A7III", "Sony", 350000, "Tersedia", 24.2, "4K");
        kamera.setImageUrl("https://via.placeholder.com/400x300?text=Kamera");

        Motor motor = new Motor("b2", "Yamaha NMAX 155", "Yamaha", 150000, "Tersedia", 155, "Matic");
        motor.setImageUrl("https://via.placeholder.com/400x300?text=Motor");

        db.insertBarang(kamera.getId(), kamera.getNama(), kamera.getMerk(), kamera.getStatusPeminjaman(),
                kamera.getCategory(), kamera.getHarga(), kamera.getImageUrl(), kamera.getImageData(),
                0, null, kamera.getMegapixel(), kamera.getResolusi());

        db.insertBarang(motor.getId(), motor.getNama(), motor.getMerk(), motor.getStatusPeminjaman(),
                motor.getCategory(), motor.getHarga(), motor.getImageUrl(), motor.getImageData(),
                motor.getCc(), motor.getTipe(), 0.0, null);
    }
}
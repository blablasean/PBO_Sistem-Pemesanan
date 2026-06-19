package com.example.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:h2:./data/peminjaman;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private final SchemaManager schemaManager;
    private final UserRepository userRepository;
    private final BarangRepository barangRepository;
    private final TransaksiRepository transaksiRepository;

    public DatabaseHelper() {
        this.schemaManager = new SchemaManager(this);
        this.userRepository = new UserRepository(this);
        this.barangRepository = new BarangRepository(this);
        this.transaksiRepository = new TransaksiRepository(this);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public void initSchema() throws SQLException {
        schemaManager.initSchema();
    }

    public void insertUser(String id, String name, String email, String role, String password) throws SQLException {
        userRepository.insertUser(id, name, email, role, password);
    }

    public Map<String, Object> getUserByEmail(String email) throws SQLException {
        return userRepository.getUserByEmail(email);
    }

    public Map<String, Object> getUserById(String id) throws SQLException {
        return userRepository.getUserById(id);
    }

    public void insertBarang(String id, String name, String merk, String status, String category,
                             double price, String imageUrl, String imageData,
                             int cc, String tipe, double megapixel, String resolusi) throws SQLException {
        barangRepository.insertBarang(id, name, merk, status, category, price, imageUrl, imageData,
                cc, tipe, megapixel, resolusi);
    }

    public List<Map<String, Object>> getAllBarang() throws SQLException {
        return barangRepository.getAllBarang();
    }

    public Map<String, Object> getBarangById(String id) throws SQLException {
        return barangRepository.getBarangById(id);
    }

    public void updateBarang(String id, String name, String merk, String status, String category,
                             double price, String imageUrl, String imageData,
                             int cc, String tipe, double megapixel, String resolusi) throws SQLException {
        barangRepository.updateBarang(id, name, merk, status, category, price, imageUrl, imageData,
                cc, tipe, megapixel, resolusi);
    }

    public void deleteBarang(String id) throws SQLException {
        barangRepository.deleteBarang(id);
    }

    public void updateBarangStatus(String id, String status) throws SQLException {
        barangRepository.updateBarangStatus(id, status);
    }

    public void insertTransaksi(String id, String userId, String barangId, String borrowDate,
                                String returnDate, String status, int quantity,
                                String note, double totalCost) throws SQLException {
        transaksiRepository.insertTransaksi(id, userId, barangId, borrowDate, returnDate,
                status, quantity, note, totalCost);
    }

    public void deleteTransaksiByBarangId(String barangId) throws SQLException {
        transaksiRepository.deleteTransaksiByBarangId(barangId);
    }

    public List<Map<String, Object>> getAllTransaksi() throws SQLException {
        return transaksiRepository.getAllTransaksi();
    }
}
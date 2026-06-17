package com.example.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:h2:./data/peminjaman;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public void initSchema() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS users (id VARCHAR PRIMARY KEY, name VARCHAR, email VARCHAR, role VARCHAR, password VARCHAR)");
            statement.execute("CREATE TABLE IF NOT EXISTS barang (id VARCHAR PRIMARY KEY, name VARCHAR, status VARCHAR, category VARCHAR, price DOUBLE, image_url VARCHAR, image_data CLOB)");
            statement.execute("CREATE TABLE IF NOT EXISTS transaksi (id VARCHAR PRIMARY KEY, user_id VARCHAR, barang_id VARCHAR, borrow_date DATE, return_date DATE, status VARCHAR, quantity INT DEFAULT 1, note VARCHAR(1024), total_cost DOUBLE, FOREIGN KEY (user_id) REFERENCES users(id), FOREIGN KEY (barang_id) REFERENCES barang(id))");
            ensureColumn("transaksi", "quantity INT DEFAULT 1");
            ensureColumn("transaksi", "note VARCHAR(1024)");
            ensureColumn("barang", "image_data CLOB");
        }
    }

    private void ensureColumn(String tableName, String columnExpression) throws SQLException {
        String sql = String.format("ALTER TABLE %s ADD COLUMN %s", tableName, columnExpression);
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException ignored) {
            // Ignore column already exists errors or other schema upgrade issues.
        }
    }

    public void insertUser(String id, String name, String email, String role, String password) throws SQLException {
        String sql = "MERGE INTO users (id, name, email, role, password) KEY(id) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, name);
            statement.setString(3, email);
            statement.setString(4, role);
            statement.setString(5, password);
            statement.executeUpdate();
        }
    }

    public void insertBarang(String id, String name, String status, String category, double price, String imageUrl, String imageData) throws SQLException {
        String sql = "MERGE INTO barang (id, name, status, category, price, image_url, image_data) KEY(id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, name);
            statement.setString(3, status);
            statement.setString(4, category);
            statement.setDouble(5, price);
            statement.setString(6, imageUrl);
            statement.setString(7, imageData);
            statement.executeUpdate();
        }
    }

    public void printAllUsers() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT id, name, email, role FROM users")) {
            while (resultSet.next()) {
                System.out.printf("User[id=%s, name=%s, email=%s, role=%s]%n",
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("role"));
            }
        }
    }

    public void printAllBarang() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT id, name, status, category, price FROM barang")) {
            while (resultSet.next()) {
                System.out.printf("Barang[id=%s, name=%s, status=%s, category=%s, price=%.0f]%n",
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("status"),
                        resultSet.getString("category"),
                        resultSet.getDouble("price"));
            }
        }
    }

    public Map<String, Object> getUserByEmail(String email) throws SQLException {
        String sql = "SELECT id, name, email, role, password FROM users WHERE email = ?";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", resultSet.getString("id"));
                    row.put("name", resultSet.getString("name"));
                    row.put("email", resultSet.getString("email"));
                    row.put("role", resultSet.getString("role"));
                    row.put("password", resultSet.getString("password"));
                    return row;
                }
            }
        }
        return null;
    }

    public Map<String, Object> getUserById(String id) throws SQLException {
        String sql = "SELECT id, name, email, role FROM users WHERE id = ?";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", resultSet.getString("id"));
                    row.put("name", resultSet.getString("name"));
                    row.put("email", resultSet.getString("email"));
                    row.put("role", resultSet.getString("role"));
                    return row;
                }
            }
        }
        return null;
    }

    public List<Map<String, Object>> getAllBarang() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT id, name, status, category, price, image_url, image_data FROM barang";
        try (Connection connection = getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", resultSet.getString("id"));
                row.put("name", resultSet.getString("name"));
                row.put("status", resultSet.getString("status"));
                row.put("category", resultSet.getString("category"));
                row.put("price", resultSet.getDouble("price"));
                String imageData = resultSet.getString("image_data");
                String imageUrl = resultSet.getString("image_url");
                row.put("image_url", imageData != null && !imageData.isBlank() ? imageData : imageUrl);
                row.put("image_data", imageData);
                list.add(row);
            }
        }
        return list;
    }

    public Map<String, Object> getBarangById(String id) throws SQLException {
        String sql = "SELECT id, name, status, category, price, image_url, image_data FROM barang WHERE id = ?";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", resultSet.getString("id"));
                    row.put("name", resultSet.getString("name"));
                    row.put("status", resultSet.getString("status"));
                    row.put("category", resultSet.getString("category"));
                    row.put("price", resultSet.getDouble("price"));
                    String imageData = resultSet.getString("image_data");
                    String imageUrl = resultSet.getString("image_url");
                    row.put("image_url", imageData != null && !imageData.isBlank() ? imageData : imageUrl);
                    row.put("image_data", imageData);
                    return row;
                }
            }
        }
        return null;
    }

    public void updateBarang(String id, String name, String status, String category, double price, String imageUrl, String imageData) throws SQLException {
        String sql = "UPDATE barang SET name = ?, status = ?, category = ?, price = ?, image_url = ?, image_data = ? WHERE id = ?";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, status);
            statement.setString(3, category);
            statement.setDouble(4, price);
            statement.setString(5, imageUrl);
            statement.setString(6, imageData);
            statement.setString(7, id);
            statement.executeUpdate();
        }
    }

    public void deleteBarang(String id) throws SQLException {
        String sql = "DELETE FROM barang WHERE id = ?";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        }
    }

    public void updateBarangStatus(String id, String status) throws SQLException {
        String sql = "UPDATE barang SET status = ? WHERE id = ?";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setString(2, id);
            statement.executeUpdate();
        }
    }

    public void insertTransaksi(String id, String userId, String barangId, String borrowDate, String returnDate, String status, int quantity, String note, double totalCost) throws SQLException {
        String sql = "MERGE INTO transaksi (id, user_id, barang_id, borrow_date, return_date, status, quantity, note, total_cost) KEY(id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, userId);
            statement.setString(3, barangId);
            statement.setString(4, borrowDate);
            statement.setString(5, returnDate);
            statement.setString(6, status);
            statement.setInt(7, quantity);
            statement.setString(8, note);
            statement.setDouble(9, totalCost);
            statement.executeUpdate();
        }
        updateBarangStatus(barangId, "Dipinjam");
    }

    public List<Map<String, Object>> getAllTransaksi() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT t.id, t.user_id, u.name AS user_name, t.barang_id, b.name AS barang_name, b.image_url, t.borrow_date, t.return_date, t.status, t.total_cost, t.quantity, t.note " +
                "FROM transaksi t " +
                "LEFT JOIN users u ON u.id = t.user_id " +
                "LEFT JOIN barang b ON b.id = t.barang_id " +
                "ORDER BY t.borrow_date DESC";
        try (Connection connection = getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", resultSet.getString("id"));
                row.put("user_id", resultSet.getString("user_id"));
                row.put("user_name", resultSet.getString("user_name"));
                row.put("barang_id", resultSet.getString("barang_id"));
                row.put("barang_name", resultSet.getString("barang_name"));
                row.put("image_url", resultSet.getString("image_url"));
                row.put("borrow_date", resultSet.getDate("borrow_date") != null ? resultSet.getDate("borrow_date").toString() : null);
                row.put("return_date", resultSet.getDate("return_date") != null ? resultSet.getDate("return_date").toString() : null);
                row.put("status", resultSet.getString("status"));
                row.put("total_cost", resultSet.getDouble("total_cost"));
                row.put("quantity", resultSet.getInt("quantity"));
                row.put("note", resultSet.getString("note"));
                list.add(row);
            }
        }
        return list;
    }
}

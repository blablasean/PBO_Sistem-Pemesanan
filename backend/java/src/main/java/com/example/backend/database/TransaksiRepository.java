package com.example.backend.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransaksiRepository {
    private final DatabaseHelper databaseHelper;

    public TransaksiRepository(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void insertTransaksi(String id, String userId, String barangId, String borrowDate,
                                String returnDate, String status, int quantity,
                                String note, double totalCost) throws SQLException {
        String sql = """
                MERGE INTO transaksi
                (id, user_id, barang_id, borrow_date, return_date, status, quantity, note, total_cost)
                KEY(id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

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

        databaseHelper.updateBarangStatus(barangId, "Dipinjam");
    }

    public void deleteTransaksiByBarangId(String barangId) throws SQLException {
        String sql = "DELETE FROM transaksi WHERE barang_id = ?";

        try (Connection connection = databaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, barangId);
            statement.executeUpdate();
        }
    }

    public List<Map<String, Object>> getAllTransaksi() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();

        String sql = """
                SELECT t.id, t.user_id, u.name AS user_name, t.barang_id,
                       b.name AS barang_name, b.category, b.image_url, b.image_data,
                       t.borrow_date, t.return_date, t.status, t.total_cost,
                       t.quantity, t.note
                FROM transaksi t
                LEFT JOIN users u ON u.id = t.user_id
                LEFT JOIN barang b ON b.id = t.barang_id
                ORDER BY t.borrow_date DESC
                """;

        try (Connection connection = databaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                list.add(mapTransaksi(resultSet));
            }
        }

        return list;
    }

    private Map<String, Object> mapTransaksi(ResultSet resultSet) throws SQLException {
        Map<String, Object> row = new HashMap<>();

        row.put("id", resultSet.getString("id"));
        row.put("user_id", resultSet.getString("user_id"));
        row.put("user_name", resultSet.getString("user_name"));
        row.put("barang_id", resultSet.getString("barang_id"));
        row.put("barang_name", resultSet.getString("barang_name"));
        row.put("category", resultSet.getString("category"));
        row.put("image_url", resultSet.getString("image_url"));
        row.put("image_data", resultSet.getString("image_data"));
        row.put("borrow_date", resultSet.getDate("borrow_date") != null ? resultSet.getDate("borrow_date").toString() : null);
        row.put("return_date", resultSet.getDate("return_date") != null ? resultSet.getDate("return_date").toString() : null);
        row.put("status", resultSet.getString("status"));
        row.put("total_cost", resultSet.getDouble("total_cost"));
        row.put("quantity", resultSet.getInt("quantity"));
        row.put("note", resultSet.getString("note"));

        return row;
    }
}
package com.example.backend.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.backend.model.Barang;
import com.example.backend.model.Kamera;
import com.example.backend.model.Motor;

public class BarangRepository {
    private final DatabaseHelper databaseHelper;

    public BarangRepository(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void insertBarang(String id, String name, String merk, String status, String category,
                             double price, String imageUrl, String imageData,
                             int cc, String tipe, double megapixel, String resolusi) throws SQLException {
        String sql = """
                MERGE INTO barang
                (id, name, merk, status, category, price, image_url, image_data, cc, tipe, megapixel, resolusi)
                KEY(id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            fillBarangStatement(statement, id, name, merk, status, category, price, imageUrl, imageData,
                    cc, tipe, megapixel, resolusi);
            statement.executeUpdate();
        }
    }

    public List<Map<String, Object>> getAllBarang() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
                SELECT id, name, merk, status, category, price, image_url, image_data,
                       cc, tipe, megapixel, resolusi
                FROM barang
                """;

        try (Connection connection = databaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                list.add(mapBarang(resultSet));
            }
        }

        return list;
    }

    public Map<String, Object> getBarangById(String id) throws SQLException {
        String sql = """
                SELECT id, name, merk, status, category, price, image_url, image_data,
                       cc, tipe, megapixel, resolusi
                FROM barang
                WHERE id = ?
                """;

        try (Connection connection = databaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapBarang(resultSet);
                }
            }
        }

        return null;
    }

    public void updateBarang(String id, String name, String merk, String status, String category,
                             double price, String imageUrl, String imageData,
                             int cc, String tipe, double megapixel, String resolusi) throws SQLException {
        String sql = """
                UPDATE barang
                SET name = ?, merk = ?, status = ?, category = ?, price = ?,
                    image_url = ?, image_data = ?, cc = ?, tipe = ?,
                    megapixel = ?, resolusi = ?
                WHERE id = ?
                """;

        try (Connection connection = databaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, merk);
            statement.setString(3, status);
            statement.setString(4, category);
            statement.setDouble(5, price);
            statement.setString(6, imageUrl);
            statement.setString(7, imageData);
            statement.setInt(8, cc);
            statement.setString(9, tipe);
            statement.setDouble(10, megapixel);
            statement.setString(11, resolusi);
            statement.setString(12, id);
            statement.executeUpdate();
        }
    }

    public void deleteBarang(String id) throws SQLException {
        String sql = "DELETE FROM barang WHERE id = ?";

        try (Connection connection = databaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);
            statement.executeUpdate();
        }
    }

    public void updateBarangStatus(String id, String status) throws SQLException {
        String sql = "UPDATE barang SET status = ? WHERE id = ?";

        try (Connection connection = databaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setString(2, id);
            statement.executeUpdate();
        }
    }

    private void fillBarangStatement(PreparedStatement statement, String id, String name, String merk,
                                     String status, String category, double price,
                                     String imageUrl, String imageData,
                                     int cc, String tipe, double megapixel, String resolusi) throws SQLException {
        statement.setString(1, id);
        statement.setString(2, name);
        statement.setString(3, merk);
        statement.setString(4, status);
        statement.setString(5, category);
        statement.setDouble(6, price);
        statement.setString(7, imageUrl);
        statement.setString(8, imageData);
        statement.setInt(9, cc);
        statement.setString(10, tipe);
        statement.setDouble(11, megapixel);
        statement.setString(12, resolusi);
    }

    private Map<String, Object> mapBarang(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        String name = resultSet.getString("name");
        String merk = resultSet.getString("merk");
        String status = resultSet.getString("status");
        String category = resultSet.getString("category");
        double price = resultSet.getDouble("price");
        String imageUrl = resultSet.getString("image_url");
        String imageData = resultSet.getString("image_data");
        int cc = resultSet.getInt("cc");
        String tipe = resultSet.getString("tipe");
        double megapixel = resultSet.getDouble("megapixel");
        String resolusi = resultSet.getString("resolusi");

        Barang barang;

        if ("Motor".equalsIgnoreCase(category)) {
            barang = new Motor(id, name, merk, price, status, cc, tipe);
        } else {
            barang = new Kamera(id, name, merk, price, status, megapixel, resolusi);
        }

        barang.setStatusPeminjaman(status);
        barang.setImageUrl(imageUrl);
        barang.setImageData(imageData);

        return barang.toMap();
    }
}
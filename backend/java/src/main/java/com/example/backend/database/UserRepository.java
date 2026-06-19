package com.example.backend.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final DatabaseHelper databaseHelper;

    public UserRepository(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void insertUser(String id, String name, String email, String role, String password) throws SQLException {
        String sql = "MERGE INTO users (id, name, email, role, password) KEY(id) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = databaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);
            statement.setString(2, name);
            statement.setString(3, email);
            statement.setString(4, role);
            statement.setString(5, password);
            statement.executeUpdate();
        }
    }

    public Map<String, Object> getUserByEmail(String email) throws SQLException {
        String sql = "SELECT id, name, email, role, password FROM users WHERE email = ?";

        try (Connection connection = databaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUserWithPassword(resultSet);
                }
            }
        }

        return null;
    }

    public Map<String, Object> getUserById(String id) throws SQLException {
        String sql = "SELECT id, name, email, role FROM users WHERE id = ?";

        try (Connection connection = databaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        }

        return null;
    }

    private Map<String, Object> mapUser(ResultSet resultSet) throws SQLException {
        Map<String, Object> row = new HashMap<>();
        row.put("id", resultSet.getString("id"));
        row.put("name", resultSet.getString("name"));
        row.put("email", resultSet.getString("email"));
        row.put("role", resultSet.getString("role"));
        return row;
    }

    private Map<String, Object> mapUserWithPassword(ResultSet resultSet) throws SQLException {
        Map<String, Object> row = mapUser(resultSet);
        row.put("password", resultSet.getString("password"));
        return row;
    }
}
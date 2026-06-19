package com.example.backend.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaManager {
    private final DatabaseHelper databaseHelper;

    public SchemaManager(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void initSchema() throws SQLException {
        try (Connection connection = databaseHelper.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id VARCHAR PRIMARY KEY,
                        name VARCHAR,
                        email VARCHAR,
                        role VARCHAR,
                        password VARCHAR
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS barang (
                        id VARCHAR PRIMARY KEY,
                        name VARCHAR,
                        merk VARCHAR,
                        status VARCHAR,
                        category VARCHAR,
                        price DOUBLE,
                        image_url VARCHAR,
                        image_data CLOB,
                        cc INT,
                        tipe VARCHAR,
                        megapixel DOUBLE,
                        resolusi VARCHAR
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS transaksi (
                        id VARCHAR PRIMARY KEY,
                        user_id VARCHAR,
                        barang_id VARCHAR,
                        borrow_date DATE,
                        return_date DATE,
                        status VARCHAR,
                        quantity INT DEFAULT 1,
                        note VARCHAR(1024),
                        total_cost DOUBLE,
                        FOREIGN KEY (user_id) REFERENCES users(id),
                        FOREIGN KEY (barang_id) REFERENCES barang(id)
                    )
                    """);
        }

        ensureColumn("transaksi", "quantity INT DEFAULT 1");
        ensureColumn("transaksi", "note VARCHAR(1024)");
        ensureColumn("barang", "image_data CLOB");
        ensureColumn("barang", "merk VARCHAR");
        ensureColumn("barang", "cc INT DEFAULT 0");
        ensureColumn("barang", "tipe VARCHAR");
        ensureColumn("barang", "megapixel DOUBLE DEFAULT 0.0");
        ensureColumn("barang", "resolusi VARCHAR");
    }

    private void ensureColumn(String tableName, String columnExpression) throws SQLException {
        String columnName = columnExpression.trim().split("\\s+")[0];

        if (columnExists(tableName, columnName)) {
            return;
        }

        String sql = String.format("ALTER TABLE %s ADD COLUMN %s", tableName, columnExpression);

        try (Connection connection = databaseHelper.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private boolean columnExists(String tableName, String columnName) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS total
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_NAME = ?
                AND COLUMN_NAME = ?
                """;

        try (Connection connection = databaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, tableName.toUpperCase());
            statement.setString(2, columnName.toUpperCase());

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt("total") > 0;
            }
        }
    }
}
package com.arthur.marchetti.repo;

import com.arthur.marchetti.interfaces.TaxCalculator.ReceiptDBRepository;

import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SqliteReceiptRepository implements ReceiptDBRepository {
    private final String url; // jdbc:sqlite:/absolute/path/to/receipts.db or jdbc:sqlite::memory:
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SqliteReceiptRepository(Path dbFile){
        this.url = "jdbc:sqlite:" + dbFile.toAbsolutePath();
    }
    public SqliteReceiptRepository(String jdbcUrl){ this.url = jdbcUrl; }

    private Connection get() throws SQLException { return DriverManager.getConnection(url); }

    private void ensureSchema(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS receipts (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "created_at TEXT NOT NULL, " +
                    "customer_name TEXT NOT NULL, " +
                    "subtotal TEXT NOT NULL, tax TEXT NOT NULL, total TEXT NOT NULL, " +
                    "content TEXT NOT NULL)");
        }
    }

    @Override
    public long save(List<String> lines, String customerName, String subtotal, String tax, String total) throws Exception {
        if (lines == null || lines.isEmpty()) throw new IllegalArgumentException("lines required");
        String content = String.join(System.lineSeparator(), lines);
        try (Connection c = get()) {
            ensureSchema(c);
            String sql = "INSERT INTO receipts(created_at, customer_name, subtotal, tax, total, content) VALUES(?,?,?,?,?,?)";
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, LocalDateTime.now().format(fmt));
                ps.setString(2, customerName);
                ps.setString(3, subtotal);
                ps.setString(4, tax);
                ps.setString(5, total);
                ps.setString(6, content);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
        }
        throw new IllegalStateException("insert failed");
    }
}
package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Sesuaikan konfigurasi database kamu
    private static final String URL = "jdbc:mysql://localhost:3306/db_tmd";
    private static final String USER = "root"; // Default xampp biasanya root
    private static final String PASSWORD = ""; // Default xampp biasanya kosong

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Register driver (opsional untuk versi baru, tapi aman dipakai)
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Koneksi Gagal: " + e.getMessage());
        }
        return conn;
    }
}
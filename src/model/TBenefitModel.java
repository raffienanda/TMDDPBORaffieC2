package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TBenefitModel {

    // Mengambil semua data untuk ditampilkan di Tabel Menu Awal [cite: 92]
    public List<TBenefit> getAllBenefits() {
        List<TBenefit> list = new ArrayList<>();
        String query = "SELECT * FROM tbenefit";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(new TBenefit(
                    rs.getString("username"),
                    rs.getInt("skor"),
                    rs.getInt("peluru_meleset"),
                    rs.getInt("sisa_peluru")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Mengecek apakah username sudah ada
    public boolean isUsernameExist(String username) {
        String query = "SELECT 1 FROM tbenefit WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Menyimpan atau Update data setelah game selesai
    // Logika: Skor & Peluru Meleset diakumulasi (ditambah), Sisa Peluru ditimpa (update terakhir) [cite: 83, 84]
    public void updateOrInsert(TBenefit data) {
        if (isUsernameExist(data.getUsername())) {
            // Kalau user sudah ada, update (Skor += skorBaru, Meleset += melesetBaru, Sisa = sisaBaru)
            String query = "UPDATE tbenefit SET skor = skor + ?, peluru_meleset = peluru_meleset + ?, sisa_peluru = ? WHERE username = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setInt(1, data.getSkor());
                pstmt.setInt(2, data.getPeluruMeleset());
                pstmt.setInt(3, data.getSisaPeluru());
                pstmt.setString(4, data.getUsername());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Kalau user baru, insert data baru
            String query = "INSERT INTO tbenefit (username, skor, peluru_meleset, sisa_peluru) VALUES (?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setString(1, data.getUsername());
                pstmt.setInt(2, data.getSkor());
                pstmt.setInt(3, data.getPeluruMeleset());
                pstmt.setInt(4, data.getSisaPeluru());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
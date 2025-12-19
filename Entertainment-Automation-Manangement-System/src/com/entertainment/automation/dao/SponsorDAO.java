package com.entertainment.automation.dao;

import com.entertainment.automation.DatabaseConnection;
import com.entertainment.automation.models.Sponsor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SponsorDAO {
    
    public List<Sponsor> getAllSponsors() {
        List<Sponsor> sponsors = new ArrayList<>();
        String sql = "SELECT * FROM Sponsors ORDER BY CreatedAt DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Sponsor sponsor = new Sponsor();
                sponsor.setSponsorID(rs.getInt("SponsorID"));
                sponsor.setCompanyName(rs.getString("CompanyName"));
                sponsor.setContactPerson(rs.getString("ContactPerson"));
                sponsor.setEmail(rs.getString("Email"));
                sponsor.setPhone(rs.getString("Phone"));
                sponsor.setSponsorshipLevel(rs.getString("SponsorshipLevel"));
                sponsor.setAmount(rs.getDouble("Amount"));
               
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                if (createdAt != null) {
                    sponsor.setCreatedAt(createdAt.toLocalDateTime());
                } else {
                    sponsor.setCreatedAt(null);
                }
                
                sponsors.add(sponsor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sponsors;
    }
    
    public boolean addSponsor(Sponsor sponsor) {
        String sql = "INSERT INTO Sponsors (CompanyName, ContactPerson, Email, Phone, SponsorshipLevel, Amount) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, sponsor.getCompanyName());
            pstmt.setString(2, sponsor.getContactPerson());
            pstmt.setString(3, sponsor.getEmail());
            pstmt.setString(4, sponsor.getPhone());
            pstmt.setString(5, sponsor.getSponsorshipLevel());
            pstmt.setDouble(6, sponsor.getAmount());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
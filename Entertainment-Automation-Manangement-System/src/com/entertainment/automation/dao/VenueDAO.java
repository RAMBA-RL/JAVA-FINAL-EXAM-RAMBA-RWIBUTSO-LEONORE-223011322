package com.entertainment.automation.dao;

import com.entertainment.automation.DatabaseConnection;
import com.entertainment.automation.models.Venue;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenueDAO {
    
    public List<Venue> getAllVenues() {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM Venues ORDER BY CreatedAt DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Venue venue = new Venue();
                venue.setVenueID(rs.getInt("VenueID"));
                venue.setName(rs.getString("Name"));
                venue.setAddress(rs.getString("Address"));
                venue.setCapacity(rs.getInt("Capacity"));
                venue.setManager(rs.getString("Manager"));
                venue.setContact(rs.getString("Contact"));
                
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                if (createdAt != null) {
                    venue.setCreatedAt(createdAt.toLocalDateTime());
                } else {
                    venue.setCreatedAt(null);
                }
                
                venues.add(venue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venues;
    }
    
    public boolean addVenue(Venue venue) {
        String sql = "INSERT INTO Venues (Name, Address, Capacity, Manager, Contact) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, venue.getName());
            pstmt.setString(2, venue.getAddress());
            pstmt.setInt(3, venue.getCapacity());
            pstmt.setString(4, venue.getManager());
            pstmt.setString(5, venue.getContact());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Venue getVenueById(int venueID) {
        Venue venue = null;
        String sql = "SELECT * FROM Venues WHERE VenueID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, venueID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                venue = new Venue();
                venue.setVenueID(rs.getInt("VenueID"));
                venue.setName(rs.getString("Name"));
                venue.setAddress(rs.getString("Address"));
                venue.setCapacity(rs.getInt("Capacity"));
                venue.setManager(rs.getString("Manager"));
                venue.setContact(rs.getString("Contact"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venue;
    }
}

package com.entertainment.automation.dao;

import com.entertainment.automation.DatabaseConnection;
import com.entertainment.automation.models.Event;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.*, v.Name as VenueName FROM Events e " +
                    "LEFT JOIN Venues v ON e.VenueID = v.VenueID " +
                    "ORDER BY e.EventDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Event event = new Event();
                event.setEventID(rs.getInt("EventID"));
                event.setReferenceID(rs.getString("ReferenceID"));
                event.setTitle(rs.getString("Title"));
                event.setDescription(rs.getString("Description"));
                event.setEventDate(rs.getTimestamp("EventDate").toLocalDateTime());
                event.setVenueID(rs.getInt("VenueID"));
                event.setStatus(rs.getString("Status"));
                event.setTicketPrice(rs.getDouble("TicketPrice"));
                event.setAvailableTickets(rs.getInt("AvailableTickets"));
                event.setCreatedBy(rs.getInt("CreatedBy"));
                event.setRemarks(rs.getString("Remarks"));
                event.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                event.setVenueName(rs.getString("VenueName"));
                
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
    
    public boolean addEvent(Event event) {
        String sql = "INSERT INTO Events (ReferenceID, Title, Description, EventDate, " +
                    "VenueID, Status, TicketPrice, AvailableTickets, CreatedBy, Remarks) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, event.getReferenceID());
            pstmt.setString(2, event.getTitle());
            pstmt.setString(3, event.getDescription());
            pstmt.setTimestamp(4, Timestamp.valueOf(event.getEventDate()));
            pstmt.setInt(5, event.getVenueID());
            pstmt.setString(6, event.getStatus());
            pstmt.setDouble(7, event.getTicketPrice());
            pstmt.setInt(8, event.getAvailableTickets());
            pstmt.setInt(9, event.getCreatedBy());
            pstmt.setString(10, event.getRemarks());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public int addEventAndGetId(Event event) {
        String sql = "INSERT INTO Events (ReferenceID, Title, Description, EventDate, " +
                    "VenueID, Status, TicketPrice, AvailableTickets, CreatedBy, Remarks) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, event.getReferenceID());
            pstmt.setString(2, event.getTitle());
            pstmt.setString(3, event.getDescription());
            pstmt.setTimestamp(4, Timestamp.valueOf(event.getEventDate()));
            pstmt.setInt(5, event.getVenueID());
            pstmt.setString(6, event.getStatus());
            pstmt.setDouble(7, event.getTicketPrice());
            pstmt.setInt(8, event.getAvailableTickets());
            pstmt.setInt(9, event.getCreatedBy());
            pstmt.setString(10, event.getRemarks());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error in addEventAndGetId: " + e.getMessage());
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
    
    public Event getEventById(int eventId) {
        String sql = "SELECT e.*, v.Name as VenueName FROM Events e " +
                    "LEFT JOIN Venues v ON e.VenueID = v.VenueID " +
                    "WHERE e.EventID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Event event = new Event();
                event.setEventID(rs.getInt("EventID"));
                event.setReferenceID(rs.getString("ReferenceID"));
                event.setTitle(rs.getString("Title"));
                event.setDescription(rs.getString("Description"));
                event.setEventDate(rs.getTimestamp("EventDate").toLocalDateTime());
                event.setVenueID(rs.getInt("VenueID"));
                event.setStatus(rs.getString("Status"));
                event.setTicketPrice(rs.getDouble("TicketPrice"));
                event.setAvailableTickets(rs.getInt("AvailableTickets"));
                event.setCreatedBy(rs.getInt("CreatedBy"));
                event.setRemarks(rs.getString("Remarks"));
                event.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                event.setVenueName(rs.getString("VenueName"));
                
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Event getEventByReferenceId(String referenceId) {
        String sql = "SELECT e.*, v.Name as VenueName FROM Events e " +
                    "LEFT JOIN Venues v ON e.VenueID = v.VenueID " +
                    "WHERE e.ReferenceID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, referenceId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Event event = new Event();
                event.setEventID(rs.getInt("EventID"));
                event.setReferenceID(rs.getString("ReferenceID"));
                event.setTitle(rs.getString("Title"));
                event.setDescription(rs.getString("Description"));
                event.setEventDate(rs.getTimestamp("EventDate").toLocalDateTime());
                event.setVenueID(rs.getInt("VenueID"));
                event.setStatus(rs.getString("Status"));
                event.setTicketPrice(rs.getDouble("TicketPrice"));
                event.setAvailableTickets(rs.getInt("AvailableTickets"));
                event.setCreatedBy(rs.getInt("CreatedBy"));
                event.setRemarks(rs.getString("Remarks"));
                event.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                event.setVenueName(rs.getString("VenueName"));
                
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updateEvent(Event event) {
        String sql = "UPDATE Events SET Title = ?, Description = ?, EventDate = ?, " +
                    "VenueID = ?, Status = ?, TicketPrice = ?, AvailableTickets = ?, " +
                    "Remarks = ? WHERE EventID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDescription());
            pstmt.setTimestamp(3, Timestamp.valueOf(event.getEventDate()));
            pstmt.setInt(4, event.getVenueID());
            pstmt.setString(5, event.getStatus());
            pstmt.setDouble(6, event.getTicketPrice());
            pstmt.setInt(7, event.getAvailableTickets());
            pstmt.setString(8, event.getRemarks());
            pstmt.setInt(9, event.getEventID());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteEvent(int eventId) {
        String sql = "DELETE FROM Events WHERE EventID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, eventId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
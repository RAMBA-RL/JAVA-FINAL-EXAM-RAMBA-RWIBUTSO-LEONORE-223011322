package com.entertainment.automation.dao;

import com.entertainment.automation.DatabaseConnection;
import com.entertainment.automation.models.Ticket;
import com.entertainment.automation.models.TicketType;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TicketDAO {
    
    public boolean addTicket(Ticket ticket) {
        String sql = "INSERT INTO Tickets (EventID, UserID, TicketTypeID, TicketNumber, SeatNumber, TicketType, Price, Status, PurchaseDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ticket.getEventID());
            pstmt.setInt(2, ticket.getUserID());
            pstmt.setInt(3, ticket.getTicketTypeID()); 
            pstmt.setString(4, ticket.getTicketNumber());
            pstmt.setString(5, ticket.getSeatNumber());
            pstmt.setString(6, ticket.getTicketType());
            pstmt.setDouble(7, ticket.getPrice());
            pstmt.setString(8, ticket.getStatus());
            pstmt.setTimestamp(9, Timestamp.valueOf(ticket.getPurchaseDate()));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.*, e.Title as EventTitle, u.FullName as UserName " +
                "FROM Tickets t " +
                "INNER JOIN Events e ON t.EventID = e.EventID " +
                "INNER JOIN Users u ON t.UserID = u.UserID " +
                "ORDER BY t.PurchaseDate DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setTicketID(rs.getInt("TicketID"));
                ticket.setEventID(rs.getInt("EventID"));
                ticket.setUserID(rs.getInt("UserID"));
                ticket.setTicketTypeID(rs.getInt("TicketTypeID"));  
                ticket.setTicketNumber(rs.getString("TicketNumber"));
                ticket.setSeatNumber(rs.getString("SeatNumber"));
                ticket.setTicketType(rs.getString("TicketType"));
                ticket.setPrice(rs.getDouble("Price"));
                ticket.setStatus(rs.getString("Status"));
                ticket.setPurchaseDate(rs.getTimestamp("PurchaseDate").toLocalDateTime());
                ticket.setEventTitle(rs.getString("EventTitle"));
                ticket.setUserName(rs.getString("UserName"));
                tickets.add(ticket);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tickets;
    }

   
    public List<Ticket> getTicketsByUser(int userID) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.*, e.Title as EventTitle, u.FullName as UserName " +
                "FROM Tickets t " +
                "INNER JOIN Events e ON t.EventID = e.EventID " +
                "INNER JOIN Users u ON t.UserID = u.UserID " +
                "WHERE t.UserID = ? ORDER BY t.PurchaseDate DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setTicketID(rs.getInt("TicketID"));
                ticket.setEventID(rs.getInt("EventID"));
                ticket.setUserID(rs.getInt("UserID"));
                ticket.setTicketTypeID(rs.getInt("TicketTypeID"));  
                ticket.setTicketNumber(rs.getString("TicketNumber"));
                ticket.setSeatNumber(rs.getString("SeatNumber"));
                ticket.setTicketType(rs.getString("TicketType"));
                ticket.setPrice(rs.getDouble("Price"));
                ticket.setStatus(rs.getString("Status"));
                ticket.setPurchaseDate(rs.getTimestamp("PurchaseDate").toLocalDateTime());
                ticket.setEventTitle(rs.getString("EventTitle"));
                ticket.setUserName(rs.getString("UserName"));
                tickets.add(ticket);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tickets;
    }

    
    public List<TicketType> getTicketTypesByEvent(int eventID) {
        List<TicketType> ticketTypes = new ArrayList<>();
        String sql = "SELECT * FROM TicketTypes WHERE EventID = ? ORDER BY Price ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, eventID);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                TicketType ticketType = new TicketType();
                ticketType.setTicketTypeID(rs.getInt("TicketTypeID"));
                ticketType.setEventID(rs.getInt("EventID"));
                ticketType.setTypeName(rs.getString("TypeName"));
                ticketType.setPrice(rs.getDouble("Price"));
                ticketType.setDescription(rs.getString("Description"));
                ticketTypes.add(ticketType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ticketTypes;
    }
    
 
    public TicketType getTicketTypeById(int ticketTypeID) {
        String sql = "SELECT * FROM TicketTypes WHERE TicketTypeID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ticketTypeID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                TicketType ticketType = new TicketType();
                ticketType.setTicketTypeID(rs.getInt("TicketTypeID"));
                ticketType.setEventID(rs.getInt("EventID"));
                ticketType.setTypeName(rs.getString("TypeName"));
                ticketType.setPrice(rs.getDouble("Price"));
                ticketType.setDescription(rs.getString("Description"));
                return ticketType;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addTicketTypesForEvent(int eventID, double regularPrice, double vipPrice) {
        String sql = "INSERT INTO TicketTypes (EventID, TypeName, Price, Description) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            // Add Regular ticket type
            pstmt.setInt(1, eventID);
            pstmt.setString(2, "REGULAR");
            pstmt.setDouble(3, regularPrice);
            pstmt.setString(4, "Standard seating");
            pstmt.executeUpdate();
            
            // Add VIP ticket type
            pstmt.setInt(1, eventID);
            pstmt.setString(2, "VIP");
            pstmt.setDouble(3, vipPrice);
            pstmt.setString(4, "Premium seating with extra benefits");
            pstmt.executeUpdate();
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error in addTicketTypesForEvent: " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean isSeatTaken(int eventID, String seatNumber) {
        String query = "SELECT COUNT(*) FROM Tickets WHERE EventID = ? AND SeatNumber = ? AND Status != 'Cancelled'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, eventID);
            stmt.setString(2, seatNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getAvailableSeats(int eventID, int totalSeats) {
        List<String> availableSeats = new ArrayList<>();
        String query = "SELECT SeatNumber FROM Tickets WHERE EventID = ? AND Status != 'Cancelled'";
        Set<String> takenSeats = new HashSet<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, eventID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                takenSeats.add(rs.getString("SeatNumber"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Generate available seats list
        for (int i = 1; i <= totalSeats; i++) {
            String seat = "S" + i;
            if (!takenSeats.contains(seat)) {
                availableSeats.add(seat);
            }
        }
        return availableSeats;
    }
    
    // NEW: Get total number of taken seats for an event
    public int getTakenSeatsCount(int eventID) {
        String query = "SELECT COUNT(*) FROM Tickets WHERE EventID = ? AND Status != 'Cancelled'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, eventID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public boolean purchaseTicket(Ticket ticket) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String insertSQL = "INSERT INTO Tickets (EventID, UserID, TicketTypeID, TicketNumber, SeatNumber, TicketType, Price, Status, PurchaseDate) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt1 = conn.prepareStatement(insertSQL);
            pstmt1.setInt(1, ticket.getEventID());
            pstmt1.setInt(2, ticket.getUserID());
            pstmt1.setInt(3, ticket.getTicketTypeID()); 
            pstmt1.setString(4, ticket.getTicketNumber());
            pstmt1.setString(5, ticket.getSeatNumber());
            pstmt1.setString(6, ticket.getTicketType());
            pstmt1.setDouble(7, ticket.getPrice());
            pstmt1.setString(8, ticket.getStatus());
            pstmt1.setTimestamp(9, Timestamp.valueOf(ticket.getPurchaseDate()));
            
            pstmt1.executeUpdate(); 
            
            String updateSQL = "UPDATE TicketTypes SET AvailableQuantity = " +
                              "AvailableQuantity - 1 WHERE TicketTypeID = ? AND AvailableQuantity > 0";
            PreparedStatement pstmt2 = conn.prepareStatement(updateSQL);
            pstmt2.setInt(1, ticket.getTicketTypeID());
            int updated = pstmt2.executeUpdate(); 
            
            if (updated == 0) {
                conn.rollback(); 
                return false;
            }
            
            conn.commit(); 
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
            }
        }
    }
}
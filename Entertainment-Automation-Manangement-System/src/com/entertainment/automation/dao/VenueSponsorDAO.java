package com.entertainment.automation.dao;

import com.entertainment.automation.DatabaseConnection;
import com.entertainment.automation.models.VenueSponsor;
import com.entertainment.automation.models.Sponsor;
import com.entertainment.automation.models.Venue;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenueSponsorDAO {
    private Connection connection;

    public VenueSponsorDAO(Connection connection) {
        this.connection = connection;
    }

    public VenueSponsorDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<VenueSponsor> getSponsorsByVenue(int venueID) {
        List<VenueSponsor> list = new ArrayList<>();
        String sql = "SELECT * FROM Venue_Sponsors WHERE VenueID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, venueID);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                VenueSponsor vs = new VenueSponsor();
                vs.setVenueID(rs.getInt("VenueID"));
                vs.setSponsorID(rs.getInt("SponsorID"));
                
                Date sponsorshipStart = rs.getDate("SponsorshipStart");
                if (sponsorshipStart != null) {
                    vs.setSponsorshipStart(sponsorshipStart);
                } else {
                    vs.setSponsorshipStart(null);
                }
                
                Date sponsorshipEnd = rs.getDate("SponsorshipEnd");
                if (sponsorshipEnd != null) {
                    vs.setSponsorshipEnd(sponsorshipEnd);
                } else {
                    vs.setSponsorshipEnd(null);
                }
                
                list.add(vs);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting sponsors by venue: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<VenueSponsor> getVenuesBySponsor(int sponsorID) {
        List<VenueSponsor> list = new ArrayList<>();
        String sql = "SELECT * FROM Venue_Sponsors WHERE SponsorID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, sponsorID);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                VenueSponsor vs = new VenueSponsor();
                vs.setVenueID(rs.getInt("VenueID"));
                vs.setSponsorID(rs.getInt("SponsorID"));
                
                Date sponsorshipStart = rs.getDate("SponsorshipStart");
                if (sponsorshipStart != null) {
                    vs.setSponsorshipStart(sponsorshipStart);
                } else {
                    vs.setSponsorshipStart(null);
                }
                
                Date sponsorshipEnd = rs.getDate("SponsorshipEnd");
                if (sponsorshipEnd != null) {
                    vs.setSponsorshipEnd(sponsorshipEnd);
                } else {
                    vs.setSponsorshipEnd(null);
                }
                
                list.add(vs);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting venues by sponsor: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Sponsor> getSponsorDetailsForVenue(int venueID) {
        List<Sponsor> sponsors = new ArrayList<>();
        String sql = "SELECT s.* FROM Sponsors s " +
                     "INNER JOIN Venue_Sponsors vs ON s.SponsorID = vs.SponsorID " +
                     "WHERE vs.VenueID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, venueID);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Sponsor sponsor = new Sponsor();
                sponsor.setSponsorID(rs.getInt("SponsorID"));
                sponsor.setCompanyName(rs.getString("CompanyName"));
                sponsor.setContactPerson(rs.getString("ContactPerson"));
                sponsor.setEmail(rs.getString("Email"));
                sponsor.setPhone(rs.getString("Phone"));
                sponsor.setSponsorshipLevel(rs.getString("SponsorshipLevel"));
                sponsor.setAmount(rs.getDouble("Amount"));
                sponsors.add(sponsor);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting sponsor details for venue: " + e.getMessage());
            e.printStackTrace();
        }
        return sponsors;
    }
    public List<Venue> getVenueDetailsForSponsor(int sponsorID) {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT v.* FROM Venues v " +
                     "INNER JOIN Venue_Sponsors vs ON v.VenueID = vs.VenueID " +
                     "WHERE vs.SponsorID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, sponsorID);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Venue venue = new Venue();
                venue.setVenueID(rs.getInt("VenueID"));
                venue.setName(rs.getString("Name"));
                venue.setAddress(rs.getString("Address"));
                venue.setCapacity(rs.getInt("Capacity"));
                venue.setManager(rs.getString("Manager"));
                venue.setContact(rs.getString("Contact"));
                venues.add(venue);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting venue details for sponsor: " + e.getMessage());
            e.printStackTrace();
        }
        return venues;
    }

    // Add a new venue-sponsor relationship
    public boolean addVenueSponsor(VenueSponsor venueSponsor) {
        String sql = "INSERT INTO Venue_Sponsors (VenueID, SponsorID, SponsorshipStart, SponsorshipEnd) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, venueSponsor.getVenueID());
            ps.setInt(2, venueSponsor.getSponsorID());
            
            if (venueSponsor.getSponsorshipStart() != null) {
                ps.setDate(3, (Date) venueSponsor.getSponsorshipStart());
            } else {
                ps.setNull(3, Types.DATE);
            }
            
            if (venueSponsor.getSponsorshipEnd() != null) {
                ps.setDate(4, (Date) venueSponsor.getSponsorshipEnd());
            } else {
                ps.setNull(4, Types.DATE);
            }
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding venue sponsor: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Remove a venue-sponsor relationship
    public boolean removeVenueSponsor(int venueID, int sponsorID) {
        String sql = "DELETE FROM Venue_Sponsors WHERE VenueID = ? AND SponsorID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, venueID);
            ps.setInt(2, sponsorID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing venue sponsor: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Get all venue-sponsor relationships
    public List<VenueSponsor> getAllVenueSponsors() {
        List<VenueSponsor> list = new ArrayList<>();
        String sql = "SELECT * FROM Venue_Sponsors";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                VenueSponsor vs = new VenueSponsor();
                vs.setVenueID(rs.getInt("VenueID"));
                vs.setSponsorID(rs.getInt("SponsorID"));
                
                Date sponsorshipStart = rs.getDate("SponsorshipStart");
                if (sponsorshipStart != null) {
                    vs.setSponsorshipStart(sponsorshipStart);
                } else {
                    vs.setSponsorshipStart(null);
                }
                
                Date sponsorshipEnd = rs.getDate("SponsorshipEnd");
                if (sponsorshipEnd != null) {
                    vs.setSponsorshipEnd(sponsorshipEnd);
                } else {
                    vs.setSponsorshipEnd(null);
                }
                
                list.add(vs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all venue sponsors: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // NEW: Check if a specific venue-sponsor relationship exists
    public boolean relationshipExists(int venueID, int sponsorID) {
        String sql = "SELECT COUNT(*) FROM Venue_Sponsors WHERE VenueID = ? AND SponsorID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, venueID);
            ps.setInt(2, sponsorID);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking relationship existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    public int countSponsorsForVenue(int venueID) {
        String sql = "SELECT COUNT(*) FROM Venue_Sponsors WHERE VenueID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, venueID);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting sponsors for venue: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    public int countVenuesForSponsor(int sponsorID) {
        String sql = "SELECT COUNT(*) FROM Venue_Sponsors WHERE SponsorID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, sponsorID);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting venues for sponsor: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}
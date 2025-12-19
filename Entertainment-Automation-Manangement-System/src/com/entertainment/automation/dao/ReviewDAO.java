package com.entertainment.automation.dao;

import com.entertainment.automation.DatabaseConnection;
import com.entertainment.automation.models.Review;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public List<Review> getReviewsByEvent(int eventID) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, e.Title as EventTitle, u.FullName as UserName " +
            "FROM Reviews r " +
            "INNER JOIN Events e ON r.EventID = e.EventID " +
            "INNER JOIN Users u ON r.UserID = u.UserID " +
            "WHERE r.EventID = ? ORDER BY ReviewDate DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Review review = new Review();
                review.setReviewID(rs.getInt("ReviewID"));
                review.setEventID(rs.getInt("EventID"));
                review.setUserID(rs.getInt("UserID"));
                review.setRating(rs.getInt("Rating"));
                review.setComment(rs.getString("Comment"));
                review.setReviewDate(rs.getTimestamp("ReviewDate").toLocalDateTime());
                review.setEventTitle(rs.getString("EventTitle")); 
                review.setUserName(rs.getString("UserName"));     
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, e.Title as EventTitle, u.FullName as UserName " +
            "FROM Reviews r " +
            "INNER JOIN Events e ON r.EventID = e.EventID " +
            "INNER JOIN Users u ON r.UserID = u.UserID " +
            "ORDER BY ReviewDate DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Review review = new Review();
                review.setReviewID(rs.getInt("ReviewID"));
                review.setEventID(rs.getInt("EventID"));
                review.setUserID(rs.getInt("UserID"));
                review.setRating(rs.getInt("Rating"));
                review.setComment(rs.getString("Comment"));
                review.setReviewDate(rs.getTimestamp("ReviewDate").toLocalDateTime());
                review.setEventTitle(rs.getString("EventTitle")); 
                review.setUserName(rs.getString("UserName"));   
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public boolean addReview(Review review) {
        String sql = "INSERT INTO Reviews (EventID, UserID, Rating, Comment) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, review.getEventID());
            pstmt.setInt(2, review.getUserID());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getComment());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
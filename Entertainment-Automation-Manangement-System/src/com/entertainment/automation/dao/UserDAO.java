package com.entertainment.automation.dao;

import com.entertainment.automation.DatabaseConnection;

import com.entertainment.automation.models.User;
import com.entertainment.automation.utils.SecurityUtils;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public User authenticate(String username, String password) {
        
        String hashedPassword = SecurityUtils.hashPassword(password);
        
        String sql = "SELECT * FROM Users WHERE Username = ? AND PasswordHash = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("UserID"));
                user.setUsername(rs.getString("Username"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setEmail(rs.getString("Email"));
                user.setFullName(rs.getString("FullName"));
                user.setRole(rs.getString("Role"));
              
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                if (createdAt != null) {
                    user.setCreatedAt(createdAt.toLocalDateTime());
                } else {
                    user.setCreatedAt(null);
                }
                
                Timestamp lastLogin = rs.getTimestamp("LastLogin");
                if (lastLogin != null) {
                    user.setLastLogin(lastLogin.toLocalDateTime());
                } else {
                    user.setLastLogin(null);
                }
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;// Authentication failed
    }

    public void updateLastLogin(int userID) {
        String sql = "UPDATE Users SET LastLogin = NOW() WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY CreatedAt DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("UserID"));
                user.setUsername(rs.getString("Username"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setEmail(rs.getString("Email"));
                user.setFullName(rs.getString("FullName"));
                user.setRole(rs.getString("Role"));
                
             
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                if (createdAt != null) {
                    user.setCreatedAt(createdAt.toLocalDateTime());
                } else {
                    user.setCreatedAt(null);
                }
                
               
                Timestamp lastLogin = rs.getTimestamp("LastLogin");
                if (lastLogin != null) {
                    user.setLastLogin(lastLogin.toLocalDateTime());
                } else {
                    user.setLastLogin(null);
                }
                
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
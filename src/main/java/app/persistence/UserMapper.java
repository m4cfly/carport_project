package app.persistence;

import app.entities.Users;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    public static Users login(String name, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE name = ? AND password = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, name);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("user_id");
                String role = rs.getString("role");
                int balance = rs.getInt("balance");
                // Assuming User class is correctly set up to handle these fields
                return new Users(id, name, password, balance, role);
            } else {
                throw new DatabaseException("Login error. Please try again.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error", e.getMessage());
        }
    }

    public static void createUser(String userName, String password, String fullName, int balance, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO users (name, password, balance) VALUES (?, ?, ?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, fullName);
            ps.setString(2, password);
            ps.setInt(3, balance);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Error creating new user.");
            }
        } catch (SQLException e) {
            String message = "An error occurred. Please try again.";
            if (e.getMessage().contains("duplicate key value")) {
                message = "An account with this username already exists. Please use a different username.";
            }
            throw new DatabaseException(message, e.getMessage());
        }
    }
}

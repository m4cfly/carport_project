package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserMapper
{

    public static User login(String userName, String password, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "select * from users where user_name=? and user_password=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        )
        {
            ps.setString(1, userName);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                int id = rs.getInt("user_id");
                int balance = rs.getInt("user_balance");
                String role = rs.getString("user_role");
                return new User(id, userName, password,balance,role);
            } else
            {
                throw new DatabaseException("Fejl i login. Prøv igen");
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("DB fejl", e.getMessage());
        }
    }

    public static void createuser(String userName, String password, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "insert into users (user_name, user_password) values (?,?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        )
        {
            ps.setString(1, userName);
            ps.setString(2, password);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1)
            {
                throw new DatabaseException("Fejl ved oprettelse af ny bruger");
            }
        }
        catch (SQLException e)
        {
            String msg = "Der er sket en fejl. Prøv igen";
            // Check SQL state code for duplicate key value
            if ("23505".equals(e.getSQLState()))
            {
                msg = "Brugernavnet findes allerede. Vælg et andet";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }
   /* public static Map< String, User> getAllUsers(ConnectionPool connectionPool ) throws DatabaseException
    {
        String sql = "SELECT * FROM users WHERE role = 'admin';";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement( sql )
        ) {

            ResultSet rs = ps.executeQuery();

            Map< String, User> allUsers = new LinkedHashMap<>();
            User user;

            while ( rs.next() ) {

                int user_id = rs.getInt( "user_id" );
                String username = rs.getString( "user_name" );
                String password = rs.getString( "user_password" );
                int balance = rs.getInt( "balance" );
                String role = rs.getString( "role" );

                user = new User( user_id, username, password, balance,role);
                allUsers.put( user.getUserName(), user);
            }

            return allUsers;

        } catch ( SQLException e ) {
            throw new DatabaseException( "DB fejl", e.getMessage() );
        }
    }

    */
}

package dao;

import models.User;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao extends BaseDao<User> {
    public UserDao() throws Exception {
        super();
    }


    @Override
    public String getTableName() {
        return "User";
    }

    /**
     * Validate user credentials based on the provided phone number and password.
     *
     * @param phone    the phone number of the user.
     * @param password the password of the user.
     * @return true if the credentials are valid, false otherwise.
     */
    public User validateCredentials(String phone, String password) {
        String sqlStatement = "SELECT * FROM User WHERE phone = ? AND password = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setString(1, phone);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                int userID = resultSet.getInt("userID");

                return new User(userID, firstName, lastName, phone, password);
            }
        } catch (SQLException e) {
            logger.warning("Error validating user credentials: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Integer delete(int id) throws SQLException {
        // Delete a user from the database based on its ID
        Integer rowsAffected = null;
        String sqlStatement = "DELETE FROM User WHERE userID = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, id);
            rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.warning("Error deleting user: " + e.getMessage());
            throw e;
        }

        return rowsAffected;
    }


    @Override
    public List<User> all() {
        // Return all users from the database
        List<User> users = new ArrayList<>();
        String sqlStatement = "SELECT * FROM User";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userID = resultSet.getInt("userID");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String phone = resultSet.getString("phone");
                String password = resultSet.getString("password");

                User user = new User(userID, firstName, lastName, phone, password);
                users.add(user);
            }
        } catch (SQLException e) {
            logger.warning("Error retrieving users: " + e.getMessage());
        }

        return users;
    }

    @Override
    public User update(User user) {
        // Update a user in the database
        String sqlStatement = "UPDATE User SET firstName=?, lastName=?, phone=?, password=? WHERE userID=?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setInt(5, user.getUserID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.warning("Error updating user: " + e.getMessage());
        }

        return user;
    }

    @Override
    public User add(User user) throws SQLException {
        // Add a new user to the database
        String sqlStatement = "INSERT INTO User (firstName, lastName, phone, password) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.executeUpdate();

            // Retrieve the auto-generated userID
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setUserID(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            logger.warning("Error adding user: " + e.getMessage());
            throw e;
        }

        return user;
    }

    @Override
    public User read(int id) {
        // Retrieve a user from the database based on its ID
        User user = null;
        String sqlStatement = "SELECT * FROM User WHERE userID = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String phone = resultSet.getString("phone");
                String password = resultSet.getString("password");

                user = new User(id, firstName, lastName, phone, password);
            }
        } catch (SQLException e) {
            logger.warning("Error reading user: " + e.getMessage());
        }

        return user;
    }

    public User read(String phone) {
        // Retrieve a user from the database based on its ID
        User user = null;
        String sqlStatement = "SELECT * FROM User WHERE phone = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setString(1, phone);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                int id = resultSet.getInt("userID");
                String lastName = resultSet.getString("lastName");
                String password = resultSet.getString("password");

                user = new User(id, firstName, lastName, phone, password);
            }
        } catch (SQLException e) {
            logger.warning("Error reading user: " + e.getMessage());
        }

        return user;
    }

    @Override
    public void setUpTable() throws SQLException {
        String sqlStatement = """
                CREATE TABLE IF NOT EXISTS User (
                    userID INT AUTO_INCREMENT PRIMARY KEY,
                    firstName VARCHAR(100) NOT NULL,
                    lastName VARCHAR(100) NOT NULL,
                    phone VARCHAR(15) UNIQUE,
                    password VARCHAR(255)
                )""";

        logger.info("Executing SQL Statement: " + sqlStatement);

        try (FileWriter writer = new FileWriter(CREATE_SQL_FILE_PATH, true)) {
            writer.append("--- Creating Table ").append(getTableName()).append("\n").append(sqlStatement).append(";\n\n");
            logger.info("SQL statement appended to create.sql file successfully.");
        } catch (IOException e) {
            logger.warning("Error writing SQL statement to create.sql file: " + e.getMessage());
        }

        // Execute the SQL statement
        conn.prepareStatement(sqlStatement).execute();
    }
}

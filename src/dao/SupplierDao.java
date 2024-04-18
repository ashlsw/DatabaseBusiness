package dao;

import models.Supplier;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDao extends BaseDao<Supplier> {

    public SupplierDao() throws Exception {
        super();
    }

    @Override
    public List<Supplier> all() {
        // Return all suppliers from the database
        List<Supplier> suppliers = new ArrayList<>();
        String sqlStatement = "SELECT * FROM Supplier";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int supplierID = resultSet.getInt("supplierID");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");

                Supplier supplier = new Supplier(supplierID, name, email, phone);
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            logger.warning("Error retrieving suppliers: " + e.getMessage());
        }

        return suppliers;
    }

    @Override
    public String getTableName() {
        return "Supplier";
    }

    @Override
    public Integer delete(int id) throws SQLException {
        // Delete a supplier from the database based on its ID
        Integer rowsAffected = null;
        String sqlStatement = "DELETE FROM Supplier WHERE supplierID = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, id);
            rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.warning("Error deleting supplier: " + e.getMessage());
            throw e;
        }

        return rowsAffected;
    }

    @Override
    public Supplier update(Supplier supplier) throws SQLException {
        // Update a supplier in the database
        String sqlStatement = "UPDATE Supplier SET name=?, email=?, phone=? WHERE supplierID=?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setString(1, supplier.getName());
            preparedStatement.setString(2, supplier.getEmail());
            preparedStatement.setString(3, supplier.getPhone());
            preparedStatement.setInt(4, supplier.getSupplierID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.warning("Error updating supplier: " + e.getMessage());
            throw e;
        }

        return supplier;
    }

    @Override
    public Supplier add(Supplier supplier) throws SQLException {
        // Add a new supplier to the database
        String sqlStatement = "INSERT INTO Supplier (name, email, phone) VALUES (?, ?, ?)";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, supplier.getName());
            preparedStatement.setString(2, supplier.getEmail());
            preparedStatement.setString(3, supplier.getPhone());
            preparedStatement.executeUpdate();

            // Retrieve the auto-generated supplierID
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                supplier.setSupplierID(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            logger.warning("Error adding supplier: " + e.getMessage());
            throw e;
        }

        return supplier;
    }

    @Override
    public Supplier read(int id) {
        // Retrieve a supplier from the database based on its ID
        Supplier supplier = null;
        String sqlStatement = "SELECT * FROM Supplier WHERE supplierID = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");

                supplier = new Supplier(id, name, email, phone);
            }
        } catch (SQLException e) {
            logger.warning("Error reading supplier: " + e.getMessage());

        }

        return supplier;
    }

    @Override
    public void setUpTable() throws SQLException {
        // Create the Supplier table
        String sqlStatement = """
                CREATE TABLE IF NOT EXISTS Supplier (
                    supplierID INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL UNIQUE,
                    email VARCHAR(255) NOT NULL UNIQUE,
                    phone VARCHAR(15) NOT NULL UNIQUE
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

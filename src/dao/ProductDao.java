package dao;

import models.Product;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDao extends BaseDao<Product> {
    public ProductDao() throws Exception {
        super();
    }


    @Override
    public String getTableName() {
        return "Product";
    }


    @Override
    public List<Product> all() {
        List<Product> productList = new ArrayList<>();
        String sqlStatement = "SELECT * FROM Product";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int productID = rs.getInt("productID");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int quantityInStock = rs.getInt("quantityInStock");

                Product product = new Product(productID, name, description, price, quantityInStock);
                productList.add(product);
            }
        } catch (SQLException e) {
            logger.warning("Error retrieving products: " + e.getMessage());
        }

        return productList;
    }

    @Override
    public Integer delete(int id) throws SQLException {
        Integer rowsAffected = null;
        String sqlStatement = "DELETE FROM Product WHERE productID = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, id);
            rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.warning("Error deleting product: " + e.getMessage());
            throw e;
        }

        return rowsAffected;
    }

    @Override
    public Product update(Product product) throws SQLException {
        String sqlStatement = "UPDATE Product SET name=?, description=?, price=?, quantityInStock=? WHERE productID=?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setInt(4, product.getQuantityInStock());
            preparedStatement.setInt(5, product.getProductID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.warning("Error updating product: " + e.getMessage());
            throw e;
        }

        return product;
    }

    @Override
    public Product add(Product product) throws SQLException {
        String sqlStatement = "INSERT INTO Product (name, description, price, quantityInStock) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setInt(4, product.getQuantityInStock());
            preparedStatement.executeUpdate();

            // Retrieve the auto-generated productID
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                product.setProductID(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            logger.warning("Error adding product: " + e.getMessage());
            throw e;
        }

        return product;
    }

    @Override
    public Product read(int id) throws SQLException {
        Product product = null;
        String sqlStatement = "SELECT * FROM Product WHERE productID = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                int quantityInStock = resultSet.getInt("quantityInStock");

                product = new Product(id, name, description, price, quantityInStock);
            }
        } catch (SQLException e) {
            logger.warning("Error reading product: " + e.getMessage());
            throw e;
        }

        return product;
    }

    public Product read(String name) throws SQLException {
        Product product = null;
        String sqlStatement = "SELECT * FROM Product WHERE name = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String description = resultSet.getString("description");
                int id = resultSet.getInt("productID");
                double price = resultSet.getDouble("price");
                int quantityInStock = resultSet.getInt("quantityInStock");

                product = new Product(id, name, description, price, quantityInStock);
            }
        } catch (SQLException e) {
            logger.warning("Error reading product: " + e.getMessage());
            throw e;
        }

        return product;
    }

    @Override
    public void setUpTable() throws SQLException {
        String sqlStatement = """
                CREATE TABLE IF NOT EXISTS Product (
                    productID INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL UNIQUE,
                    description TEXT NOT NULL,
                    price DECIMAL(10, 2) NOT NULL,
                    quantityInStock INT NOT NULL DEFAULT 0
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

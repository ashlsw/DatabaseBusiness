package dao;

import models.SalesProduct;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ProductSaleDao extends BaseDao<SalesProduct> {
    public ProductSaleDao() throws Exception {
        super();
    }

    @Override
    public List<SalesProduct> all() {
        return null;
    }

    @Override
    public String getTableName() {
        return "Sales_Products";
    }

    @Override
    public Integer delete(int id) {
        return null;
    }

    @Override
    public SalesProduct update(SalesProduct objectIn) {
        return null;
    }

    @Override
    public SalesProduct add(SalesProduct objectIn) throws SQLException {
        String sqlStatement = "INSERT INTO Sales_Products (saleID, productID, quantitySold) VALUES (?, ?, ?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, objectIn.getSaleID());
            preparedStatement.setInt(2, objectIn.getProduct().getProductID());
            preparedStatement.setInt(3, objectIn.getQuantitySold());
            preparedStatement.executeUpdate();

            return objectIn;
        } catch (SQLException e) {
            logger.warning("Error adding sales product for saleID " + objectIn.getSaleID() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public SalesProduct read(int id) {
        return null;
    }

    @Override
    public void setUpTable() throws SQLException {
        String sqlStatement = """
                CREATE TABLE IF NOT EXISTS Sales_Products (
                     saleID INT NOT NULL,
                     productID INT NOT NULL,
                     quantitySold INT NOT NULL,
                     PRIMARY KEY (saleID, productID),
                     FOREIGN KEY (saleID) REFERENCES Sales(saleID) ON DELETE CASCADE ON UPDATE CASCADE,
                     FOREIGN KEY (productID) REFERENCES Product(productID) ON DELETE RESTRICT ON UPDATE CASCADE
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

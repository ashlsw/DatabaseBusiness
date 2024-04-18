package dao;

import models.Supplier;
import models.SupplierProduct;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SupplierProductDao extends BaseDao<Supplier> {

    public SupplierProductDao() throws Exception {
        super();
    }

    @Override
    public List<Supplier> all() {
        return null;
    }

    @Override
    public String getTableName() {
        return "Supplier_Products";
    }

    @Override
    public Integer delete(int id) {
        return null;
    }

    @Override
    public Supplier update(Supplier objectIn) {
        return null;
    }

    @Override
    public Supplier add(Supplier objectIn) {
        return null;
    }

    @Override
    public Supplier read(int id) {
        return null;
    }

    @Override
    public void setUpTable() throws SQLException {
        String sqlStatement = """
                CREATE TABLE IF NOT EXISTS Supplier_Products (
                    supplierID INT NOT NULL,
                    productID INT NOT NULL,
                    PRIMARY KEY (supplierID, productID),
                    FOREIGN KEY (supplierID) REFERENCES Supplier(supplierID) ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY (productID) REFERENCES Product(productID) ON DELETE CASCADE ON UPDATE CASCADE
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

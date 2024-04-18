package dao;

import models.Sale;
import models.SalesProduct;
import models.Stat;
import models.User;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleDao extends BaseDao<Sale> {
    public final UserDao userDao;
    public final ProductDao productDao;
    public final ProductSaleDao productSaleDao;

    public SaleDao() throws Exception {
        super();
        userDao = new UserDao();
        productSaleDao = new ProductSaleDao();
        productDao = new ProductDao();
    }

    @Override
    public List<Sale> all() {
        List<Sale> sales = new ArrayList<>();
        String sqlStatement = "SELECT * FROM Sales";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int saleID = resultSet.getInt("saleID");
                int customerID = resultSet.getInt("customerID");
                java.sql.Timestamp saleDateTimestamp = resultSet.getTimestamp("saleDate");
                LocalDateTime saleDate = saleDateTimestamp.toLocalDateTime();
                int sellerID = resultSet.getInt("sellerID");


                User customer = userDao.read(customerID);
                List<SalesProduct> products = getSalesProductsForSale(saleID);

                Sale sale = new Sale(saleID, customer, saleDate, sellerID, products);
                sales.add(sale);
            }
        } catch (Exception e) {
            logger.warning("Error retrieving sales: " + e.getMessage());
        }

        return sales;
    }

    private List<SalesProduct> getSalesProductsForSale(int saleID) throws SQLException {
        List<SalesProduct> salesProducts = new ArrayList<>();
        String sqlStatement = "SELECT * FROM Sales_Products WHERE saleID = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, saleID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int productID = resultSet.getInt("productID");
                int quantitySold = resultSet.getInt("quantitySold");

                SalesProduct salesProduct = new SalesProduct(saleID, productDao.read(productID), quantitySold);
                salesProducts.add(salesProduct);
            }
        } catch (SQLException e) {
            logger.warning("Error retrieving sales products for saleID " + saleID + ": " + e.getMessage());
        }

        return salesProducts;
    }

    @Override
    public String getTableName() {
        return "Sales";
    }

    @Override
    public Integer delete(int id) throws SQLException {
        Integer deletedRows = null;
        String sqlStatement = "DELETE FROM Sales WHERE saleID = ?";

        try {
            // Delete the products
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, id);

            deletedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.warning("Error deleting sale with ID " + id + ": " + e.getMessage());
            throw e;
        }

        return deletedRows;
    }

    @Override
    public Sale update(Sale objectIn) {
        throw new UnsupportedOperationException("This method has not been implemented");
    }

    @Override
    public Sale add(Sale sale) throws SQLException {
        String sqlStatement = "INSERT INTO Sales (customerID, saleDate, sellerID) VALUES (?, ?, ?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, sale.getCustomer().getUserID());
            preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(sale.getSaleDate()));
            preparedStatement.setInt(3, sale.getSellerID());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int saleID = generatedKeys.getInt(1);
                sale.setSaleID(saleID);
                for (SalesProduct salesProduct : sale.getProducts()) {
                    salesProduct.setSaleID(saleID);
                    productSaleDao.add(salesProduct);
                }
                return sale;
            }
        } catch (SQLException e) {
            logger.warning("Error adding sale: " + e.getMessage());
            throw e;
        }
        return null;
    }


    @Override
    public Sale read(int id) {
        String sqlStatement = "SELECT * FROM Sales WHERE saleID = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int customerID = resultSet.getInt("customerID");
                java.sql.Timestamp saleDateTimestamp = resultSet.getTimestamp("saleDate");
                LocalDateTime saleDate = saleDateTimestamp.toLocalDateTime();
                int sellerID = resultSet.getInt("sellerID");

                UserDao userDao = new UserDao();
                User customer = userDao.read(customerID);
                List<SalesProduct> products = getSalesProductsForSale(id);

                return new Sale(id, customer, saleDate, sellerID, products);
            }
        } catch (Exception e) {
            logger.warning("Error reading sale with ID " + id + ": " + e.getMessage());

        }
        return null;
    }

    @Override
    public void setUpTable() throws SQLException {
        String sqlStatement = """
                CREATE TABLE IF NOT EXISTS Sales (
                    saleID INT AUTO_INCREMENT PRIMARY KEY,
                    customerID INT NULL,
                    saleDate DATETIME NOT NULL,
                    sellerID INT NULL,
                    FOREIGN KEY (customerID) REFERENCES User(userID) ON DELETE SET NULL ON UPDATE CASCADE,
                    FOREIGN KEY (sellerID) REFERENCES User(userID) ON DELETE SET NULL ON UPDATE CASCADE
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

    public Stat[] getSalesStat() {
        Stat[] stats = new Stat[12];
        for (int i = 0; i < 12; i++) {
            stats[i] = new Stat(monthToString(i + 1), 0.0);
        }
        String query = """
                SELECT
                    YEAR(s.saleDate) AS Year,
                    MONTH(s.saleDate) AS Month,
                    SUM(p.price * sp.quantitySold) AS Monthly_Sales_Total
                FROM
                    Sales s
                JOIN
                    Sales_Products sp ON s.saleID = sp.saleID
                JOIN
                    Product p ON sp.productID = p.productID
                GROUP BY
                    YEAR(s.saleDate), MONTH(s.saleDate)
                ORDER BY
                    Year, Month
                """;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int month = rs.getInt("Month");
                double salesTotal = rs.getDouble("Monthly_Sales_Total");
                // Update the corresponding entry in the stats array
                stats[month - 1].setAmount(salesTotal);
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }

        return stats;
    }

    String monthToString(int month) {
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "";
        }
    }
}

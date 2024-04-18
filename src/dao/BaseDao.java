package dao;

import models.Sale;
import models.SalesProduct;
import models.User;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public abstract class BaseDao<T> {
    public static final String CREATE_SQL_FILE_PATH = "./sql/create.sql";
    public static final String INSERT_SQL_FILE_PATH = "./sql/insert.sql";
    public static Logger logger;
    protected static Connection conn;
    private String url;
    private String username;
    private String password;
    private String databaseDriver;

    /**
     * Default constructor to load database configuration from "config.properties".
     *
     * @throws Exception if an error occurs while loading the configuration.
     */
    public BaseDao() throws Exception {
        this("config.properties");
    }

    /**
     * Constructor to load database configuration from the specified file.
     *
     * @param configFileName the name of the configuration file.
     * @throws Exception if an error occurs while loading the configuration.
     */
    public BaseDao(String configFileName) throws Exception {
        Properties props = new Properties();
        try {
            FileInputStream input = new FileInputStream(configFileName);
            props.load(input);
            this.url = props.getProperty("DATABASE_URL");
            this.username = props.getProperty("USERNAME");
            this.password = props.getProperty("PASSWORD");
            this.databaseDriver = props.getProperty("DATABASE_DRIVER");
        } catch (IOException ex) {
            logger.warning(ex.getMessage());
        }
        logger = Logger.getLogger(BaseDao.class.getName());
        connect();
    }

    /**
     * Set up the database by creating necessary directories, generating SQL statements for table creation,
     * and inserting data from CSV files.
     *
     * @throws Exception if an error occurs during database setup.
     */
    public static void setUp() throws Exception {
        File directory = new File("./sql");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Failed to create directory: " + directory.getAbsolutePath());
            }
        }

        // Autogenerate the SQL statements for creating the database
        try (FileWriter writer = new FileWriter(CREATE_SQL_FILE_PATH, false)) {
            writer.write("CREATE DATABASE IF NOT EXISTS business_management;\n");
            writer.write("USE business_management;\n\n");
        } catch (IOException e) {
            System.err.println("Error writing SQL statement to create.sql file: " + e.getMessage());
        }

        // Create DAO instances and set up the database
        List<BaseDao> daos = new ArrayList<>(
                List.of(new ProductDao(), new UserDao(), new SaleDao(), new SupplierDao(), new ProductSaleDao(), new SupplierProductDao())
        );

        for (BaseDao dao : daos) {
            dao.setUpTable();
        }
        String sqlStatement;
        // Extract data from csv files and populate the tables

        // Read users, suppliers, and products from CSV files, add them to the database, and generate insert.sql file
        try (FileWriter insertWriter = new FileWriter(INSERT_SQL_FILE_PATH, false)) {

            // Read and insert users
            List<String> userLines = readCSVFile("./data/users.csv");
            // delete all existing users
            conn.prepareStatement("DELETE FROM User").execute();
            insertWriter.write("-- Insert data into User Table\n");
            for (String line : userLines) {
                String[] parts = line.split(",");
                String firstName = parts[0].trim();
                String lastName = parts[1].trim();
                String phone = cleanPhoneNumber(parts[2]);
                String password = parts[3].trim();
                sqlStatement = "INSERT INTO User (firstName, lastName, phone, password) VALUES ('" + firstName + "', '" + lastName + "', '" + phone + "', '" + password + "')";
                conn.prepareStatement(sqlStatement).execute();
                insertWriter.write(sqlStatement + ";\n");
            }

            // Read and insert suppliers
            List<String> supplierLines = readCSVFile("./data/suppliers.csv");
            conn.prepareStatement("DELETE FROM Supplier").execute();
            insertWriter.write("\n-- Insert data into Supplier Table\n");
            for (String line : supplierLines) {
                String[] parts = line.split(",");
                String name = parts[0].trim();
                String email = parts[1].trim();
                String phone = cleanPhoneNumber(parts[2].trim());
                sqlStatement = "INSERT INTO Supplier (name, email, phone) VALUES ('" + name + "', '" + email + "', '" + phone + "')";
                conn.prepareStatement(sqlStatement).execute();
                insertWriter.write(sqlStatement + ";\n");
            }

            // Read and insert products
            List<String> productLines = readCSVFile("./data/products.csv");
            conn.prepareStatement("DELETE FROM Product").execute();
            insertWriter.write("\n-- Insert data into Product Table\n");
            for (String line : productLines) {
                String[] parts = line.split(";");
                String name = parts[0].trim();
                String description = parts[1].trim();
                String price = parts[2].trim();
                String quantityInStock = parts[3].trim();
                sqlStatement = "INSERT INTO Product (name, description, price, quantityInStock) VALUES ('" + name + "', '" + description + "', " + price + ", " + quantityInStock + ")";
                insertWriter.write(sqlStatement + ";\n");
                conn.prepareStatement(sqlStatement).execute();
            }

            conn.prepareStatement("DELETE FROM Sales").execute();
            insertWriter.write("\n-- Insert data into Sales Table\n");
            conn.prepareStatement("DELETE FROM Sales_Products").execute();
            insertWriter.write("\n-- Insert data into Sales_Products Table\n");
            for (Sale s : groupSalesByDate("./data/sales_data.csv")
            ) {
                // Insert sale into the database
                sqlStatement = "INSERT INTO Sales (customerID, saleDate, sellerID) VALUES (" + s.getCustomer().getUserID() + ", '" + s.getSaleDate()
                        + "', " + s.getSellerID() + ")";
                insertWriter.write(sqlStatement + ";\n");
                conn.prepareStatement(sqlStatement).execute();

                // Get the saleID of the inserted sale
                ResultSet rs = conn.createStatement().executeQuery("SELECT LAST_INSERT_ID()");
                rs.next();
                int saleID = rs.getInt(1);

                for (SalesProduct p : s.getProducts()
                ) {
                    // Insert sales product into the database
                    sqlStatement = "INSERT INTO Sales_Products (saleID, productID, quantitySold) VALUES (" + saleID + ", "
                            + p.getProduct().getProductID() + ", " + p.getQuantitySold() + ")";
                    insertWriter.write(sqlStatement + ";\n");

                    conn.prepareStatement(sqlStatement).execute();
                }


            }

            logger.info("Data inserted successfully.");
        } catch (IOException e) {
            logger.warning("Error writing SQL statements to insert.sql file: " + e.getMessage());
        }
    }

    /**
     * Reads the content of a CSV file and returns it as a list of lines.
     *
     * @param filePath the path to the CSV file.
     * @return a list of lines read from the CSV file.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    private static List<String> readCSVFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // skip the file header
            br.readLine();
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    /**
     * Cleans the phone number by removing any non-numeric characters.
     *
     * @param phoneNumber the phone number to clean.
     * @return the cleaned phone number containing only numeric characters.
     */
    public static String cleanPhoneNumber(String phoneNumber) {
        // Remove any non-numeric characters from the phone number
        return phoneNumber.replaceAll("[^0-9]", "");
    }

    public static List<Sale> groupSalesByDate(String filePath) {
        Map<LocalDateTime, Sale> salesByDateTime = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            UserDao userDao = new UserDao();
            ProductDao productDao = new ProductDao();

            String line;
            // skip header
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                LocalDateTime saleDateTime = LocalDateTime.parse(parts[0]+"T00:00:00");
                String customerPhone = parts[1].trim();
                String sellerPhone = parts[2].trim();
                String productName = parts[3].trim();
                int quantitySold = Integer.parseInt(parts[4]);

                // Group sales by date and time
                if (!salesByDateTime.containsKey(saleDateTime)) {
                    int sellerID = userDao.read(cleanPhoneNumber(sellerPhone)).getUserID();
                    User customer = userDao.read(cleanPhoneNumber(customerPhone));
                    List<SalesProduct> products = new ArrayList<>();
                    salesByDateTime.put(saleDateTime, new Sale(
                            customer, saleDateTime, sellerID, products
                    ));
                }
                salesByDateTime.get(saleDateTime).addProduct(
                        productDao.read(productName), quantitySold
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(salesByDateTime.values());
    }

    /**
     * Connect to the database using configured connection parameters.
     *
     * @throws Exception if connection to the database fails.
     */
    protected void connect() throws Exception {
        if (conn == null || conn.isClosed()) {
            Class.forName(databaseDriver);
            try {
                logger.info("Connecting to the database....");
                conn = DriverManager.getConnection(url, username, password);
                logger.info("Database connected successfully ");
            } catch (SQLException ex) {
                logger.warning("Database connection to " +
                        url + " Using username=" + username +
                        " and password=" + password + " Failed ");

                logger.warning(ex.getMessage());
            }

        }
    }

    /**
     * Close the database connection.
     *
     * @throws Exception if an error occurs while closing the connection.
     */
    protected void close() throws Exception {

        try {

            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
            }
            logger.info("Database Connection Closed");
        } catch (Exception ex) {

            logger.warning("Failed to close the database with error " + ex.getMessage());

        }

    }

    /**
     * Retrieve all objects from the database.
     *
     * @return a list of objects retrieved from the database.
     */
    abstract public List<T> all();

    /**
     * Get the name of the database table associated with the DAO.
     *
     * @return the name of the database table.
     */
    abstract public String getTableName();

    /**
     * Delete an object from the database based on its ID.
     *
     * @param id the ID of the object to be deleted.
     * @return the number of rows affected by the delete operation.
     */
    abstract public Integer delete(int id) throws SQLException;

    /**
     * Update an object in the database.
     *
     * @param objectIn the object to be updated.
     * @return the updated object.
     */
    abstract public T update(T objectIn) throws SQLException;

    /**
     * Add a new object to the database.
     *
     * @param objectIn the object to be added.
     * @return the added object.
     */
    abstract public T add(T objectIn) throws SQLException;

    /**
     * Read an object from the database based on its ID.
     *
     * @param id the ID of the object to be read.
     * @return the object read from the database.
     */
    abstract public T read(int id) throws SQLException;

    /**
     * Create the database table associated with the DAO.
     *
     * @throws SQLException if an error occurs while creating the table.
     */
    abstract public void setUpTable() throws SQLException, IOException;
}

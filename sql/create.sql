CREATE DATABASE IF NOT EXISTS business_management;
USE business_management;

--- Creating Table Product
CREATE TABLE IF NOT EXISTS Product (
    productID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantityInStock INT NOT NULL DEFAULT 0
);

--- Creating Table User
CREATE TABLE IF NOT EXISTS User (
    userID INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(100) NOT NULL,
    lastName VARCHAR(100) NOT NULL,
    phone VARCHAR(15) UNIQUE,
    password VARCHAR(255)
);

--- Creating Table Sales
CREATE TABLE IF NOT EXISTS Sales (
    saleID INT AUTO_INCREMENT PRIMARY KEY,
    customerID INT NULL,
    saleDate DATETIME NOT NULL,
    sellerID INT NULL,
    FOREIGN KEY (customerID) REFERENCES User(userID) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (sellerID) REFERENCES User(userID) ON DELETE SET NULL ON UPDATE CASCADE
);

--- Creating Table Supplier
CREATE TABLE IF NOT EXISTS Supplier (
    supplierID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL UNIQUE
);

--- Creating Table Sales_Products
CREATE TABLE IF NOT EXISTS Sales_Products (
     saleID INT NOT NULL,
     productID INT NOT NULL,
     quantitySold INT NOT NULL,
     PRIMARY KEY (saleID, productID),
     FOREIGN KEY (saleID) REFERENCES Sales(saleID) ON DELETE CASCADE ON UPDATE CASCADE,
     FOREIGN KEY (productID) REFERENCES Product(productID) ON DELETE RESTRICT ON UPDATE CASCADE
);

--- Creating Table Supplier_Products
CREATE TABLE IF NOT EXISTS Supplier_Products (
    supplierID INT NOT NULL,
    productID INT NOT NULL,
    PRIMARY KEY (supplierID, productID),
    FOREIGN KEY (supplierID) REFERENCES Supplier(supplierID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (productID) REFERENCES Product(productID) ON DELETE CASCADE ON UPDATE CASCADE
);


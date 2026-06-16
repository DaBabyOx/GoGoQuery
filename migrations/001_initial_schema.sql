-- This is the same schema from GoGoQuery.sql
CREATE DATABASE IF NOT EXISTS GoGoQuery;
USE GoGoQuery;

CREATE TABLE IF NOT EXISTS MsUser(
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    UserDOB DATE NOT NULL,
    UserEmail VARCHAR(255) NOT NULL UNIQUE,
    UserPassword VARCHAR(255) NOT NULL,
    UserGender VARCHAR(255) NOT NULL,
    UserRole VARCHAR(255) NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (UserEmail)
);

CREATE TABLE IF NOT EXISTS MsItem(
    ItemID INT AUTO_INCREMENT PRIMARY KEY,
    ItemName VARCHAR(255) NOT NULL,
    ItemCategory VARCHAR(255) NOT NULL,
    ItemPrice DECIMAL(10,2) NOT NULL,
    ItemDesc VARCHAR(255) NOT NULL,
    ItemStock INT NOT NULL DEFAULT 0,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (ItemCategory),
    INDEX idx_stock (ItemStock)
);

CREATE TABLE IF NOT EXISTS MsCart(
    UserID INT NOT NULL,
    ItemID INT NOT NULL,
    Quantity INT NOT NULL,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES MsUser(UserID) ON DELETE CASCADE,
    FOREIGN KEY (ItemID) REFERENCES MsItem(ItemID) ON DELETE CASCADE,
    PRIMARY KEY (UserID, ItemID),
    INDEX idx_user (UserID)
);

CREATE TABLE IF NOT EXISTS TransactionHeader(
    TransactionID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    DateCreated DATE NOT NULL,
    Status VARCHAR(255) NOT NULL DEFAULT 'In Queue',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES MsUser(UserID),
    INDEX idx_status (Status),
    INDEX idx_user_date (UserID, DateCreated)
);

CREATE TABLE IF NOT EXISTS TransactionDetail(
    TransactionID INT NOT NULL,
    ItemID INT NOT NULL,
    Quantity INT NOT NULL,
    FOREIGN KEY (TransactionID) REFERENCES TransactionHeader(TransactionID) ON DELETE CASCADE,
    FOREIGN KEY (ItemID) REFERENCES MsItem(ItemID),
    PRIMARY KEY(TransactionID, ItemID),
    INDEX idx_transaction (TransactionID)
);

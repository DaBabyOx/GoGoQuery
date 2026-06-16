package test;

import org.junit.jupiter.api.*;
import src.DbConnect;
import src.Product;
import src.getCart;
import src.Transaction;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DbConnectTest {
    private static Connection testConnection;
    private static final String TEST_URL = "jdbc:mysql://localhost:3306/gogoquery_test";
    private static final String USER = "root";
    private static final String PASSWORD = "Ayasjago1@";
    
    @BeforeAll
    static void setUpDatabase() throws SQLException {
        // Create test database
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USER, PASSWORD);
        Statement stmt = conn.createStatement();
        
        try {
            stmt.execute("DROP DATABASE IF EXISTS gogoquery_test");
            stmt.execute("CREATE DATABASE gogoquery_test");
        } finally {
            stmt.close();
            conn.close();
        }
        
        // Connect to test database and create tables
        testConnection = DriverManager.getConnection(TEST_URL, USER, PASSWORD);
        createTestTables(testConnection);
        
        // Insert test data
        insertTestData(testConnection);
    }
    
    private static void createTestTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        String[] createTables = {
            "CREATE TABLE MsUser(" +
            "    UserID int auto_increment primary key," +
            "    UserDOB date not null," +
            "    UserEmail varchar(255) not null," +
            "    UserPassword varchar(255) not null," +
            "    UserGender varchar(255) not null," +
            "    UserRole varchar(255) not null)",
            
            "CREATE TABLE MsItem(" +
            "    ItemID int auto_increment primary key," +
            "    ItemName varchar(255) not null," +
            "    ItemCategory varchar(255) not null," +
            "    ItemPrice decimal(10,2) not null," +
            "    ItemDesc varchar(255) not null," +
            "    ItemStock int not null default 0)",
            
            "CREATE TABLE MsCart(" +
            "    UserID int not null," +
            "    ItemID int not null," +
            "    Quantity int not null," +
            "    foreign key (UserID) references MsUser(UserID)," +
            "    foreign key (ItemID) references MsItem(ItemID)," +
            "    primary key (UserID, ItemID))",
            
            "CREATE TABLE TransactionHeader(" +
            "  TransactionID int auto_increment primary key," +
            "  UserID int not null," +
            "  DateCreated date not null," +
            "  Status varchar(255) not null," +
            "  foreign key (UserID) references MsUser(UserID))",
            
            "CREATE TABLE TransactionDetail(" +
            "    TransactionID int not null," +
            "    ItemID int not null," +
            "    Quantity int not null," +
            "    foreign key (TransactionID) references TransactionHeader(TransactionID)," +
            "    foreign key (ItemID) references MsItem(ItemID)," +
            "    primary key(TransactionID, ItemID))"
        };
        
        for (String sql : createTables) {
            stmt.execute(sql);
        }
        stmt.close();
    }
    
    private static void insertTestData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        // Insert test users
        stmt.execute("INSERT INTO MsUser (UserDOB, UserEmail, UserPassword, UserGender, UserRole) " +
                    "VALUES ('1990-01-01', 'test@gomail.com', 'password123', 'Male', 'Shopper')");
        
        stmt.execute("INSERT INTO MsUser (UserDOB, UserEmail, UserPassword, UserGender, UserRole) " +
                    "VALUES ('1985-05-15', 'manager@gomail.com', 'manager123', 'Female', 'Manager')");
        
        // Insert test items
        stmt.execute("INSERT INTO MsItem (ItemName, ItemCategory, ItemPrice, ItemDesc, ItemStock) " +
                    "VALUES ('Test Laptop', 'Electronics', 999.99, 'High-performance laptop', 10)");
        
        stmt.execute("INSERT INTO MsItem (ItemName, ItemCategory, ItemPrice, ItemDesc, ItemStock) " +
                    "VALUES ('Test Mouse', 'Electronics', 29.99, 'Wireless mouse', 50)");
        
        stmt.execute("INSERT INTO MsItem (ItemName, ItemCategory, ItemPrice, ItemDesc, ItemStock) " +
                    "VALUES ('Test Book', 'Books', 19.99, 'Programming book', 100)");
        
        stmt.close();
    }
    
    @AfterAll
    static void tearDownDatabase() throws SQLException {
        if (testConnection != null && !testConnection.isClosed()) {
            testConnection.close();
        }
        
        // Clean up test database
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USER, PASSWORD);
        Statement stmt = conn.createStatement();
        stmt.execute("DROP DATABASE IF EXISTS gogoquery_test");
        stmt.close();
        conn.close();
    }
    
    @BeforeEach
    void setUp() throws SQLException {
        // Clear cart and transaction data before each test
        Statement stmt = testConnection.createStatement();
        stmt.execute("DELETE FROM MsCart");
        stmt.execute("DELETE FROM TransactionDetail");
        stmt.execute("DELETE FROM TransactionHeader");
        stmt.close();
    }
    
    @Test
    void testConnect() throws SQLException {
        Connection conn = DbConnect.connect();
        assertNotNull(conn);
        assertFalse(conn.isClosed());
        conn.close();
    }
    
    @Test
    void testValidateLogin_Success() throws SQLException {
        boolean result = DbConnect.validateLogin("test@gomail.com", "password123");
        assertTrue(result);
    }
    
    @Test
    void testValidateLogin_Failure() throws SQLException {
        boolean result = DbConnect.validateLogin("test@gomail.com", "wrongpassword");
        assertFalse(result);
    }
    
    @Test
    void testCheckEmail_Exists() throws SQLException {
        boolean result = DbConnect.checkEmail("test@gomail.com");
        assertTrue(result);
    }
    
    @Test
    void testCheckEmail_NotExists() throws SQLException {
        boolean result = DbConnect.checkEmail("nonexistent@gomail.com");
        assertFalse(result);
    }
    
    @Test
    void testGetUserID() throws SQLException {
        int userId = DbConnect.getUserID("test@gomail.com");
        assertTrue(userId > 0);
    }
    
    @Test
    void testGetUserID_ThrowsExceptionForNonExistentUser() {
        assertThrows(SQLException.class, () -> {
            DbConnect.getUserID("nonexistent@gomail.com");
        });
    }
    
    @Test
    void testGetUserRole() throws SQLException {
        String role = DbConnect.getUserRole("test@gomail.com");
        assertEquals("Shopper", role);
    }
    
    @Test
    void testGetItemCategories() throws SQLException {
        List<String> categories = DbConnect.getItemCategories();
        assertNotNull(categories);
        assertTrue(categories.contains("Select a category"));
        assertTrue(categories.contains("Electronics"));
        assertTrue(categories.contains("Books"));
    }
    
    @Test
    void testGetProducts() throws SQLException {
        ObservableList<Product> products = DbConnect.getProducts();
        assertNotNull(products);
        assertFalse(products.isEmpty());
        
        Product firstProduct = products.get(0);
        assertNotNull(firstProduct.getName());
        assertTrue(firstProduct.getPrice() > 0);
        assertTrue(firstProduct.getStock() >= 0);
    }
    
    @Test
    void testGetProductStock() throws SQLException {
        // Assuming item with ID 1 exists (Test Laptop with stock 10)
        int stock = DbConnect.getProductStock(1);
        assertEquals(10, stock);
    }
    
    @Test
    void testAddToCart_NewItem() throws SQLException {
        String result = DbConnect.addToCart("test@gomail.com", 1, 2);
        assertEquals("added", result);
        
        // Verify item was added
        ObservableList<getCart> cart = DbConnect.getCart("test@gomail.com");
        assertEquals(1, cart.size());
        assertEquals(2, cart.get(0).getQty());
    }
    
    @Test
    void testAddToCart_UpdateExisting() throws SQLException {
        // First add
        DbConnect.addToCart("test@gomail.com", 1, 2);
        
        // Add more of same item
        String result = DbConnect.addToCart("test@gomail.com", 1, 3);
        assertEquals("updated", result);
        
        ObservableList<getCart> cart = DbConnect.getCart("test@gomail.com");
        assertEquals(1, cart.size());
        assertEquals(5, cart.get(0).getQty()); // 2 + 3
    }
    
    @Test
    void testAddToCart_NotEnoughStock() throws SQLException {
        // Try to add more than available stock (stock is 10)
        String result = DbConnect.addToCart("test@gomail.com", 1, 15);
        assertEquals("not enough stock", result);
    }
    
    @Test
    void testGetCart() throws SQLException {
        DbConnect.addToCart("test@gomail.com", 1, 2);
        DbConnect.addToCart("test@gomail.com", 2, 1);
        
        ObservableList<getCart> cart = DbConnect.getCart("test@gomail.com");
        assertEquals(2, cart.size());
    }
    
    @Test
    void testGetQty() throws SQLException {
        DbConnect.addToCart("test@gomail.com", 1, 3);
        int qty = DbConnect.getQty("test@gomail.com", 1);
        assertEquals(3, qty);
    }
    
    @Test
    void testGetQty_NotInCart() throws SQLException {
        int qty = DbConnect.getQty("test@gomail.com", 999); // Non-existent item
        assertEquals(0, qty);
    }
    
    @Test
    void testRemoveItem() throws SQLException {
        DbConnect.addToCart("test@gomail.com", 1, 2);
        
        // Verify item exists
        ObservableList<getCart> cartBefore = DbConnect.getCart("test@gomail.com");
        assertEquals(1, cartBefore.size());
        
        // Remove item
        DbConnect.removeItem("test@gomail.com", 1);
        
        // Verify item removed
        ObservableList<getCart> cartAfter = DbConnect.getCart("test@gomail.com");
        assertTrue(cartAfter.isEmpty());
    }
    
    @Test
    void testUpdateItemQty() throws SQLException {
        DbConnect.addToCart("test@gomail.com", 1, 2);
        
        // Update quantity
        DbConnect.updateItemQty("test@gomail.com", 1, 5);
        
        // Verify update
        int qty = DbConnect.getQty("test@gomail.com", 1);
        assertEquals(5, qty);
    }
    
    @Test
    void testGetTotalCartPrice() throws SQLException {
        DbConnect.addToCart("test@gomail.com", 1, 2); // Laptop: 999.99 * 2 = 1999.98
        DbConnect.addToCart("test@gomail.com", 2, 3); // Mouse: 29.99 * 3 = 89.97
        
        double total = DbConnect.getTotalCartPrice("test@gomail.com");
        assertEquals(2089.95, total, 0.01); // Allow small floating point difference
    }
    
    @Test
    void testFilterProducts_ByCategory() throws SQLException {
        ObservableList<Product> electronics = DbConnect.filterProducts("Electronics", null);
        assertNotNull(electronics);
        
        for (Product product : electronics) {
            assertEquals("Electronics", product.getCategory());
        }
    }
    
    @Test
    void testFilterProducts_BySearchText() throws SQLException {
        ObservableList<Product> laptopResults = DbConnect.filterProducts(null, "laptop");
        assertNotNull(laptopResults);
        
        for (Product product : laptopResults) {
            assertTrue(product.getName().toLowerCase().contains("laptop"));
        }
    }
    
    @Test
    void testFilterProducts_Combined() throws SQLException {
        ObservableList<Product> results = DbConnect.filterProducts("Electronics", "test");
        assertNotNull(results);
        
        for (Product product : results) {
            assertEquals("Electronics", product.getCategory());
            assertTrue(product.getName().toLowerCase().contains("test"));
        }
    }
    
    @Test
    void testCheckout() throws SQLException {
        // Add items to cart
        DbConnect.addToCart("test@gomail.com", 1, 2);
        DbConnect.addToCart("test@gomail.com", 2, 1);
        
        // Get initial stock
        int initialStockLaptop = DbConnect.getProductStock(1);
        int initialStockMouse = DbConnect.getProductStock(2);
        
        // Perform checkout
        DbConnect.checkout("test@gomail.com");
        
        // Verify cart is empty
        ObservableList<getCart> cartAfter = DbConnect.getCart("test@gomail.com");
        assertTrue(cartAfter.isEmpty());
        
        // Verify stock reduced
        int finalStockLaptop = DbConnect.getProductStock(1);
        int finalStockMouse = DbConnect.getProductStock(2);
        
        assertEquals(initialStockLaptop - 2, finalStockLaptop);
        assertEquals(initialStockMouse - 1, finalStockMouse);
        
        // Verify transaction created
        List<Transaction> transactions = DbConnect.fetchAllTransactions();
        assertFalse(transactions.isEmpty());
        
        Transaction transaction = transactions.get(0);
        assertEquals("In Queue", transaction.getStatus());
        assertTrue(transaction.getAmount() > 0);
    }
    
    @Test
    void testFetchAllTransactions() {
        List<Transaction> transactions = DbConnect.fetchAllTransactions();
        assertNotNull(transactions);
        // Initially empty since we clear transactions in @BeforeEach
        assertTrue(transactions.isEmpty());
    }
    
    @Test
    void testInsertItem() {
        // Test inserting a new item
        DbConnect.insertItem("New Test Item", "Test Category", 49.99, "Test Description", 25);
        
        // Verify by fetching products
        try {
            ObservableList<Product> products = DbConnect.getProducts();
            boolean found = false;
            for (Product product : products) {
                if (product.getName().equals("New Test Item")) {
                    found = true;
                    assertEquals(49.99, product.getPrice(), 0.01);
                    assertEquals("Test Category", product.getCategory());
                    assertEquals(25, product.getStock());
                    break;
                }
            }
            assertTrue(found, "New item should be in the database");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testUpdateTransactionStatus() throws SQLException {
        // First create a transaction through checkout
        DbConnect.addToCart("test@gomail.com", 1, 1);
        DbConnect.checkout("test@gomail.com");
        
        // Get the created transaction
        List<Transaction> transactions = DbConnect.fetchAllTransactions();
        assertFalse(transactions.isEmpty());
        
        int transactionId = transactions.get(0).getId();
        
        // Update status
        DbConnect.updateTransactionStatus(transactionId, "Sent");
        
        // Verify update
        transactions = DbConnect.fetchAllTransactions();
        assertEquals("Sent", transactions.get(0).getStatus());
    }
    
    @Test
    void testRegisterUser() throws SQLException {
        boolean result = DbConnect.registerUser(
            "1995-03-15",
            "newuser@gomail.com",
            "newpass123",
            "Female",
            "Shopper"
        );
        
        assertTrue(result);
        
        // Verify user can login
        boolean canLogin = DbConnect.validateLogin("newuser@gomail.com", "newpass123");
        assertTrue(canLogin);
    }
}

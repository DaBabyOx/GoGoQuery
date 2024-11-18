package src;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.*;

public class DbConnect {
    private static final String URL = "jdbc:mysql://localhost:3306/gogoquery";
    private static final String USER = "root";
    private static final String PASSWORD ="Ayasjago1@";

    public static Connection connect() throws SQLException{
        return DriverManager.getConnection(URL, USER, PASSWORD);}

    public static boolean validateLogin(String email, String pass) throws SQLException{
        String query = "SELECT COUNT(*) FROM MsUser WHERE UserEmail = ? AND UserPassword = ?";
        try(Connection conn  = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, email);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1) >0;}}
        return false;}

    public static boolean registerUser(String dob, String email, String password, String gender, String role) throws SQLException{
        String query = "INSERT INTO MsUser (UserDOB, UserEmail, UserPassword, UserGender, UserRole) VALUES (?, ?, ?, ?, ?)";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, dob);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, gender);
            ps.setString(5, role);
            return ps.executeUpdate() > 0;}}

    public static boolean checkEmail(String email) throws SQLException{
        String query = "SELECT COUNT(*) FROM MsUser WHERE UserEmail = ?";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1) > 0;}}
        return false;}

    public static int getUserID(String email) throws SQLException {
        String query = "SELECT UserID FROM MsUser WHERE UserEmail = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("UserID");}
            else {
                throw new SQLException("User not found for email: " + email);}}}

    public static List<String> getItemCategories() throws SQLException {
        String query = "SELECT DISTINCT ItemCategory FROM MsItem";
        List<String> categories = new ArrayList<>();
        categories.add("Select a category");

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("ItemCategory"));}}
        return categories;}

    public static ObservableList<Product> getProducts() throws SQLException {
        String query = "SELECT ItemID, ItemName, ItemCategory, ItemPrice, ItemDesc, ItemStock FROM MsItem";
        ObservableList<Product> products = FXCollections.observableArrayList();

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("ItemID"),
                        rs.getString("ItemName"),
                        rs.getString("ItemCategory"),
                        rs.getDouble("ItemPrice"),
                        rs.getString("ItemDesc"),
                        rs.getInt("ItemStock")));}
        } catch (SQLException e) {
            e.printStackTrace();}
        return products;}

    public static String getUserRole(String email) throws SQLException {
        String query = "SELECT UserRole FROM MsUser WHERE UserEmail = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("UserRole");}
                else {
                    throw new SQLException("Role not found for email: " + email);}}}}

    public static int getProductStock(int itemId) throws SQLException {
        String query = "SELECT ItemStock FROM MsItem WHERE ItemID = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ItemStock");}}
        return 0;}

    public static ObservableList<getCart> getCart(String email) throws SQLException {
        String query = "SELECT MsItem.ItemID, MsItem.ItemName, MsItem.ItemPrice, MsCart.Quantity " +
                "FROM MsItem JOIN MsCart ON MsItem.ItemID = MsCart.ItemID " +
                "WHERE MsCart.UserID = ?";
        ObservableList<getCart> cart = FXCollections.observableArrayList();

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, getUserID(email));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cart.add(new getCart(
                            rs.getInt("ItemID"),
                            rs.getInt("ItemID"),
                            rs.getString("ItemName"),
                            rs.getDouble("ItemPrice"),
                            rs.getInt("Quantity")));}}
        } catch (SQLException e) {
            e.printStackTrace();}
        return cart;}

    public static ObservableList<Product> filterProducts(String category, String searchText) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT ItemID, ItemName, ItemCategory, ItemPrice, ItemDesc, ItemStock FROM MsItem WHERE ItemStock > 0");

        List<Object> parameters = new ArrayList<>();
        if (category != null && !category.equals("Select a category")) {
            query.append(" AND ItemCategory = ?");
            parameters.add(category);}
        if (searchText != null && !searchText.isEmpty()) {
            query.append(" AND LOWER(ItemName) LIKE ?");
            parameters.add("%" + searchText + "%");}

        ObservableList<Product> products = FXCollections.observableArrayList();

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));}
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product(
                            rs.getInt("ItemID"),
                            rs.getString("ItemName"),
                            rs.getString("ItemCategory"),
                            rs.getDouble("ItemPrice"),
                            rs.getString("ItemDesc"),
                            rs.getInt("ItemStock")));}}}
        return products;}

    public static void removeItem(String email, int itemID) throws SQLException {
        String query = "DELETE FROM MsCart WHERE UserID = ? AND ItemID = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, getUserID(email));
            ps.setInt(2, itemID);
            ps.executeUpdate();}}

    public static void updateItemQty(String email, int itemID, int quantity) throws SQLException {
        String query = "UPDATE MsCart SET Quantity = ? WHERE UserID = ? AND ItemID = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, quantity);
            ps.setInt(2, getUserID(email));
            ps.setInt(3, itemID);
            ps.executeUpdate();}}

    public static double getTotalCartPrice(String email) throws SQLException {
        double totalPrice = 0.0;
        String query = "SELECT SUM(p.ItemPrice * c.Quantity) AS total " + "FROM MsCart c " + "JOIN MsItem p ON c.ItemID = p.ItemID "+ "JOIN MsUser u ON c.UserID = u.UserID " + "WHERE u.UserEmail = ?";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {totalPrice = rs.getDouble("total");}}}
        return totalPrice;}

    public static String addToCart(String email, int itemID, int quantity) throws SQLException {
        Connection conn = connect();
        String userIDQuery = "SELECT UserID FROM MsUser WHERE UserEmail = ?";
        String stockQuery = "SELECT ItemStock FROM MsItem WHERE ItemID = ?";
        String cartQuery = "SELECT Quantity FROM MsCart WHERE UserID = ? AND ItemID = ?";
        String insertQuery = "INSERT INTO MsCart (UserID, ItemID, Quantity) VALUES (?, ?, ?)";
        String updateCartQuery = "UPDATE MsCart SET Quantity = ? WHERE UserID = ? AND ItemID = ?";

        PreparedStatement stmtUserID = conn.prepareStatement(userIDQuery);
        stmtUserID.setString(1, email);
        ResultSet rsUserID = stmtUserID.executeQuery();

        if (!rsUserID.next()) {
            throw new SQLException("User not found for email: " + email);}
        int userID = rsUserID.getInt("UserID");

        PreparedStatement stmtStock = conn.prepareStatement(stockQuery);
        stmtStock.setInt(1, itemID);
        ResultSet rsStock = stmtStock.executeQuery();

        if (!rsStock.next()) {
            throw new SQLException("Item not found for itemID: " + itemID);}
        int stock = rsStock.getInt("ItemStock");

        PreparedStatement stmtCart = conn.prepareStatement(cartQuery);
        stmtCart.setInt(1, userID);
        stmtCart.setInt(2, itemID);
        ResultSet rsCart = stmtCart.executeQuery();

        if (rsCart.next()) {
            int currentQuantity = rsCart.getInt("Quantity");
            int newQuantity = currentQuantity + quantity;

            if (newQuantity > stock) {
                newQuantity = stock;}

            PreparedStatement stmtUpdateCart = conn.prepareStatement(updateCartQuery);
            stmtUpdateCart.setInt(1, newQuantity);
            stmtUpdateCart.setInt(2, userID);
            stmtUpdateCart.setInt(3, itemID);
            stmtUpdateCart.executeUpdate();

            if (newQuantity == stock) {
                return "not enough stock";}

            return "updated";}
        else {
            PreparedStatement stmtInsert = conn.prepareStatement(insertQuery);
            stmtInsert.setInt(1, userID);
            stmtInsert.setInt(2, itemID);
            stmtInsert.setInt(3, quantity);
            stmtInsert.executeUpdate();

            return "added";}}

    public static int getQty(String email, int itemID) throws SQLException {
        String query = "SELECT Quantity FROM MsCart WHERE UserID = ? AND ItemID = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, getUserID(email));
            ps.setInt(2, itemID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("Quantity");}
            else {
                return 0;}}}

    public static void checkout(String email) throws SQLException {
        String insertHeaderQuery = "INSERT INTO TransactionHeader (UserID, DateCreated, Status) VALUES (?, CURDATE(), 'In Queue')";
        String insertDetailQuery = "INSERT INTO TransactionDetail (TransactionID, ItemID, Quantity) SELECT ?, ItemID, Quantity FROM MsCart WHERE UserID = ? GROUP BY ItemID";
        String deleteCartQuery = "DELETE FROM MsCart WHERE UserID = ?";
        String updateStockQuery = "UPDATE MsItem SET ItemStock = ItemStock - (SELECT Quantity FROM MsCart WHERE MsCart.ItemID = MsItem.ItemID AND MsCart.UserID = ?) WHERE EXISTS (SELECT 1 FROM MsCart WHERE MsCart.ItemID = MsItem.ItemID AND MsCart.UserID = ?)";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(insertHeaderQuery, Statement.RETURN_GENERATED_KEYS); PreparedStatement ps2 = conn.prepareStatement(insertDetailQuery); PreparedStatement ps3 = conn.prepareStatement(deleteCartQuery); PreparedStatement ps4 = conn.prepareStatement(updateStockQuery)) {

            ps.setInt(1, getUserID(email));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int transactionID = rs.getInt(1);

                    ps2.setInt(1, transactionID);
                    ps2.setInt(2, getUserID(email));
                    ps2.executeUpdate();}}

            ps4.setInt(1, getUserID(email));
            ps4.setInt(2, getUserID(email));
            ps4.executeUpdate();

            ps3.setInt(1, getUserID(email));
            ps3.executeUpdate();}}

    public static List<Transaction> fetchAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String query = " SELECT th.TransactionID AS id, th.UserID AS customerId, mu.UserEmail AS customerEmail, th.DateCreated AS date, (SELECT SUM(td.Quantity * mi.ItemPrice) FROM TransactionDetail td JOIN MsItem mi ON td.ItemID = mi.ItemID WHERE td.TransactionID = th.TransactionID) AS amount, th.Status AS status FROM TransactionHeader th JOIN MsUser mu ON th.UserID = mu.UserID;";

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getInt("customerId"),
                        rs.getString("customerEmail"),
                        rs.getString("date"),
                        rs.getDouble("amount"),
                        rs.getString("status")));}
        } catch (SQLException e) {
            e.printStackTrace();}

        return transactions;}

    public static void updateTransactionStatus(int transactionId, String newStatus) {
        String query = "UPDATE TransactionHeader SET Status = ? WHERE TransactionID = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, transactionId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();}}

    public static void insertItem(String name, String category, double price, String desc, int stock) {
        String query = "INSERT INTO MsItem (ItemName, ItemCategory, ItemPrice, ItemDesc, ItemStock) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setDouble(3, price);
            stmt.setString(4, desc);
            stmt.setInt(5, stock);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();}}}
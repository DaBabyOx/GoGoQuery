package src;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

public class Detail {
    private final Stage primaryStage;
    private Product product;

    public Detail(Product pr, Stage primaryStage) throws SQLException{
        this.primaryStage = primaryStage;
        this.product = pr;

        Region placeHolder = new Region();
        placeHolder.setPrefSize(300 , 300);
        placeHolder.setStyle("-fx-background-color: #666; -fx-background-radius: 10;");

        Label name = new Label(product.getName());
        name.setStyle("-fx-font-size: 30px; -fx-text-fill: white; -fx-font-weight: bold;");
        name.setWrapText(true);
        name.setMaxWidth(383);

        Label price = new Label("$ " + product.getPrice());
        price.setStyle("-fx-font-size: 45px; -fx-text-fill: #FD9D22; -fx-font-weight: bold;");

        Label cat = new Label("Category :");
        cat.setStyle("-fx-font-size: 20px; -fx-text-fill: #707387; -fx-font-weight: bold;");

        Label category = new Label(product.getCategory());
        category.setStyle("-fx-font-size: 20px; -fx-text-fill: #FFAD1F; -fx-font-weight: bold;");

        HBox cats = new HBox(5, cat, category);

        Label iDetail = new Label("Item Detail");
        iDetail.setStyle("-fx-font-size: 20px; -fx-text-fill: #FFAD1F; -fx-font-weight: bold;");

        Label Spec = new Label("Specification :\n\n"+product.getDesc());
        Spec.setStyle("-fx-font-size: 15px; -fx-text-fill: white; -fx-font-weight: bold;");
        Spec.setWrapText(true);
        Spec.setMaxWidth(383);

        HBox separator = new HBox();
        separator.setStyle("-fx-background-color: #FFAD1F;");
        separator.setMinHeight(2);
        separator.setMaxWidth(Double.MAX_VALUE);

        VBox midBox = new VBox(10, name, price, cats, iDetail, separator, Spec);
        midBox.setAlignment(Pos.TOP_LEFT);

        Label bestSell = new Label("Best Seller");
        bestSell.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: linear-gradient(to right, #B265E2, #34346D); -fx-padding: 5px 10px; -fx-background-radius: 5px;");
        bestSell.setMinWidth(315);
        bestSell.setMinHeight(55);

        Label setIQ = new Label("Set item quantity");
        setIQ.setStyle("-fx-font-size: 15px; -fx-text-fill: #B6A8A7; -fx-font-weight: bold;");

        Spinner<Integer> spinner = new Spinner<>(1, product.getStock(), 1);
        spinner.setStyle("-fx-font-size: 15px;");
        spinner.setMaxWidth(110);

        Label st = new Label("Stock :");
        st.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");

        Label stock = new Label(String.valueOf(product.getStock()));
        stock.setStyle("-fx-font-size: 18px; -fx-text-fill: #FD9D22; -fx-font-weight: bold;");

        HBox stockBox = new HBox(5, st, stock);

        HBox spinnerBox = new HBox(10, spinner, stockBox);
        spinnerBox.setAlignment(Pos.CENTER_LEFT);

        Button addCart = new Button("Add to Cart");
        addCart.setStyle("-fx-font-size: 15px; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: #FD9D22; -fx-background-radius: 5px;");
        addCart.setMinWidth(275);
        addCart.setOnAction(e -> {
            try{
                String email = getSession.getInstance().getEmail();
                int itemID = product.getId();
                int quantity = spinner.getValue();

                String l = ("The item " + product.getName() + " has been added to your cart with the quantity of "+quantity+"unit(s).");
                String ls = (product.getName() + "is already in your cart ["+ DbConnect.getQty(email, itemID) +"]. The quantity has been updated to "+ (DbConnect.getQty(email, itemID)+ quantity) +"unit(s).");
                String lsc = ("There are "+ product.getStock() +" units left in stock for this item, and you already have "+ DbConnect.getQty(email, itemID)+" units in your cart. The quantity in your cart has been adjusted to the maximum available stock.");

                String r = DbConnect.addToCart(email, itemID, quantity);
                if(r.equals("added")){
                    showInfo("Item Added to Cart", "Item Added Successfully",l);}
                else if(r.equals("updated")){
                    showInfo("Item Already in Cart", "Quantity Updated", ls);}
                else if (r.equals("not enough stock")){
                    showInfo("Quantity Exceeds Stock", "Not enough stock available", lsc);}
            }catch (SQLException ex){
                ex.printStackTrace();}});

        VBox rBoxtp = new VBox(5, setIQ, spinnerBox);
        rBoxtp.setAlignment(Pos.CENTER_LEFT);

        VBox rBox = new VBox(75, rBoxtp, addCart);
        rBox.setAlignment(Pos.CENTER);
        rBox.setStyle("-fx-background-color: #373745; -fx-padding: 10px; -fx-border-color: #6B6B76; -fx-border-width: 2px; -fx-background-radius: 5px; -fx-border-radius: 5px;");
        rBox.setMinWidth(315);
        rBox.setMinHeight(220);

        VBox rBoxt = new VBox(25, bestSell, rBox);

        HBox fullLayout = new HBox(35, placeHolder, midBox, rBoxt);
        fullLayout.setMaxSize(1920, 1080);
        fullLayout.setAlignment(Pos.TOP_CENTER);

        VBox Scr = new VBox(50, navBar(), fullLayout);
        Scr.setAlignment(Pos.TOP_CENTER);
        Scr.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 0%, #1B1A20, #323345);");

        Scene scene = new Scene(Scr, 1920, 1080);
        primaryStage.setTitle("ProductDetail");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();}

    private HBox navBar() {
        HBox navbar = new HBox(10);
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setPadding(new Insets(10));
        navbar.setStyle("-fx-background-color: #303243; -fx-background-radius: 50 0 0 50;");
        navbar.setMaxHeight(100);
        navbar.setMaxWidth(1150);

        Label go = new Label("Go Go");
        go.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-style: italic; -fx-font-weight: bold;");
        go.setTranslateY(-5);
        go.setTranslateX(17);

        Label query = new Label("Query");
        query.setStyle("-fx-font-size: 25px; -fx-text-fill: #FD9D22; -fx-font-style: italic; -fx-font-weight: bold;");
        query.setTranslateY(7);
        query.setTranslateX(34);

        StackPane title = new StackPane();
        title.getChildren().addAll(go, query);
        title.setAlignment(Pos.CENTER);

        title.setOnMouseClicked(event -> {
            try {
                new Home(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();}});

        TextField search = new TextField();
        search.setPromptText("Search items in GoGoQuery Store");
        search.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: #545877");
        search.setMinWidth(750);
        search.setMinHeight(30);

        Button searchBut = new Button("Search");
        searchBut.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: #7278B2; -fx-border-radius: 10");
        searchBut.setMinHeight(30);

        StackPane searchPane = new StackPane();
        searchPane.getChildren().addAll(search, searchBut);
        StackPane.setAlignment(searchBut, Pos.CENTER_RIGHT);
        StackPane.setMargin(searchBut, new Insets(0, 0, 0, 0));

        HBox leftSide = new HBox(50, title, searchPane);

        HBox sep = new HBox();
        sep.setMaxHeight(50);
        sep.setMinWidth(2);
        sep.setStyle("-fx-background-color: #42455E;");

        HBox midSide = new HBox(55, leftSide, sep);
        midSide.setAlignment(Pos.CENTER_LEFT);

        Button cartButton = new Button("My Cart");
        cartButton.setStyle("-fx-font-size: 15px; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: transparent;");
        cartButton.setMinHeight(30);
        cartButton.setOnAction(e -> {
            try {
                new Cart(primaryStage);
            } catch (SQLException ex) {
                ex.printStackTrace();}});

        Button logoutBut = new Button("Logout");
        logoutBut.setStyle("-fx-font-size: 15px; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: #FF2121;");
        logoutBut.setMinHeight(30);
        logoutBut.setOnAction(e ->{
            primaryStage.close();
            new Auth().start(primaryStage);});

        navbar.getChildren().addAll(midSide, cartButton, logoutBut);

        return navbar;}

    private void showInfo(String Title, String Header, String Message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Title);
        alert.setHeaderText(Header);
        alert.setContentText(Message);
        alert.showAndWait();}}
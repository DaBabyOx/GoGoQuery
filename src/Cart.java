package src;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.Optional;

public class Cart {
    private final Stage primaryStage;
    private String email = getSession.getInstance().getEmail();
    private Label totAmount;
    private Label proCount;
    private HBox proCountBox;
    private ListView<getCart> lv;

    public Cart(Stage primaryStage) throws SQLException {
        this.primaryStage = primaryStage;
        String uName = email.split("@")[0];

        Label uNameLabel = new Label(uName);
        uNameLabel.setStyle("-fx-font-size: 50px; -fx-text-fill: #FD9D22; -fx-font-style: italic;");

        Label cartLabel = new Label("'s Cart");
        cartLabel.setStyle("-fx-font-size: 50px; -fx-text-fill: white; -fx-font-style: italic;");

        HBox title = new HBox(uNameLabel, cartLabel);
        title.setAlignment(Pos.CENTER_LEFT);
        title.setPadding(new Insets(20));
        title.setSpacing(10);

        VBox listView = new VBox(productListViewWithCount());
        listView.setAlignment(Pos.TOP_LEFT);
        listView.setMinWidth(850);

        Label bilSum = new Label("Billing summary");
        bilSum.setStyle("-fx-font-size: 15px; -fx-text-fill: #B6A8A7; -fx-font-weight: bold;");

        Label total = new Label("Total : ");
        total.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

        totAmount = new Label("$" + DbConnect.getTotalCartPrice(email));
        totAmount.setStyle("-fx-font-size: 25px; -fx-text-fill: #FFAD1F; -fx-font-weight: bold;");

        HBox totalBox = new HBox(total, totAmount);
        totalBox.setAlignment(Pos.CENTER_LEFT);
        totalBox.setSpacing(10);

        Label inf = new Label("*Tax and delivery cost included");
        inf.setStyle("-fx-font-size: 12px; -fx-text-fill: #655E63; -fx-font-weight: bold;");

        HBox separator = new HBox();
        separator.setStyle("-fx-background-color: #80808F;");
        separator.setMinHeight(2);
        separator.setMaxWidth(Double.MAX_VALUE);

        Button co = new Button("Checkout Items");
        co.setStyle("-fx-font-size: 15px; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: #FD9D22; -fx-background-radius: 5px;");
        co.setMinWidth(275);
        co.setOnAction(e -> {
            try {
                boolean confirm = showConfirmation("Checkout Confirmation", "Are you sure you want to checkout your cart?", "Please confirm your choice.");
                if (confirm) {
                    DbConnect.checkout(email);
                    updateProductCount(proCount, proCountBox);
                    updateTotalPrice(totAmount);
                    ObservableList<getCart> updatedCartItems = DbConnect.getCart(email);
                    lv.setItems(updatedCartItems);
                    showInfo();}
            } catch (SQLException ex) {
                ex.printStackTrace();}});

        VBox bLFinal = new VBox(10, bilSum, totalBox, inf);
        bLFinal.setAlignment(Pos.CENTER_LEFT);

        VBox bLFins = new VBox(10, bLFinal, separator, co);
        bLFins.setAlignment(Pos.CENTER);
        bLFins.setPadding(new Insets(20));
        bLFins.setStyle("-fx-background-color: #303243; -fx-background-radius: 10;");
        bLFins.setMinWidth(350);
        bLFins.setMaxHeight(200);

        HBox main = new HBox(30, listView, bLFins);
        main.setAlignment(Pos.TOP_CENTER);
        main.setPadding(new Insets(20));

        VBox root = new VBox(navBar(), title, main);
        root.setAlignment(Pos.TOP_CENTER);
        root.setSpacing(20);

        GridPane r = new GridPane();
        r.addRow(0, root);
        r.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 0%, #1B1A20, #323345);");
        r.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(r, 1920, 1080);
        primaryStage.setTitle("My Cart");
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

        Button logoutBut = new Button("Logout");
        logoutBut.setStyle("-fx-font-size: 15px; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: #FF2121;");
        logoutBut.setMinHeight(30);
        logoutBut.setOnAction(e ->{
            primaryStage.close();
            new Auth().start(primaryStage);});

        navbar.getChildren().addAll(midSide, cartButton, logoutBut);

        return navbar;}

    public VBox productListViewWithCount() throws SQLException {
        Label proCountStart = new Label("Showing ");
        proCountStart.setStyle("-fx-font-size: 14px; -fx-text-fill: #707387; -fx-font-weight: bold;");


        proCount = new Label(String.valueOf(DbConnect.getCart(email).size()));
        proCount.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFAD1F; -fx-font-weight: bold;");

        Label proCountEnd = new Label(" products");
        proCountEnd.setStyle("-fx-font-size: 14px; -fx-text-fill: #707387; -fx-font-weight: bold;");

        proCountBox = new HBox(proCountStart, proCount, proCountEnd);
        proCountBox.setSpacing(5);
        proCountBox.setPadding(new Insets(10, 0, 10, 0));
        proCountBox.setAlignment(Pos.CENTER_LEFT);

        lv = new ListView<>();
        ObservableList<getCart> cartItems;

        try {
            cartItems = DbConnect.getCart(email);
        } catch (SQLException e) {
            e.printStackTrace();
            cartItems = FXCollections.observableArrayList();}

        lv.setMinWidth(850);
        lv.setMinHeight(750);
        lv.setStyle("-fx-background-color: transparent;");
        lv.setItems(cartItems);

        Label emptyMessage = new Label("Your cart is empty!");
        emptyMessage.setStyle("-fx-font-size: 24px; -fx-text-fill: #707387; -fx-font-style: italic;");
        VBox placeholderBox = new VBox(emptyMessage);
        placeholderBox.setSpacing(10);
        placeholderBox.setPadding(new Insets(20, 0, 0, 20));
        placeholderBox.setAlignment(Pos.TOP_LEFT);
        placeholderBox.setStyle("-fx-background-color: transparent;");

        lv.setPlaceholder(placeholderBox);

        lv.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(getCart cartItem, boolean empty) {
                super.updateItem(cartItem, empty);

                if (empty || cartItem == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");}
                else {
                    Region imagePlaceholder = new Region();
                    imagePlaceholder.setPrefSize(150, 150);
                    imagePlaceholder.setStyle("-fx-background-color: #666; -fx-background-radius: 10;");

                    Label nameLabel = new Label(cartItem.getName());
                    nameLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

                    Label priceLabel = new Label("$" + cartItem.getPrice());
                    priceLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: #FFAD1F; -fx-font-weight: bold;");

                    VBox productDetails = new VBox(5, nameLabel, priceLabel);
                    productDetails.setAlignment(Pos.CENTER_LEFT);

                    int stock;
                    try {
                        stock = DbConnect.getProductStock(cartItem.getUid());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);}

                    Spinner<Integer> quantitySpinner = new Spinner<>(0, stock, cartItem.getQty());
                    quantitySpinner.setPrefSize(75, 30);
                    quantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                        try {
                            if (newValue == 0) {
                                DbConnect.removeItem(email, cartItem.getUid());
                                lv.getItems().remove(cartItem);
                                lv.refresh();
                                updateProductCount(proCount, proCountBox);
                                updateTotalPrice(totAmount);}
                            else {
                                DbConnect.updateItemQty(email, cartItem.getUid(), newValue);
                                updateTotalPrice(totAmount);}

                        } catch (SQLException e) {
                            e.printStackTrace();}});

                    Button removeButton = new Button("x");
                    removeButton.setAlignment(Pos.CENTER);
                    removeButton.setMinWidth(30);
                    removeButton.setStyle("-fx-font-size: 12px; -fx-background-color: #FF2121; -fx-text-fill: white; -fx-font-weight: bold;");
                    removeButton.setOnAction(event -> {
                        try {
                            boolean confirm = showConfirmation("Item Removal Confirmation", "Do you want to remove this item from your cart?", "Please confirm your choice.");
                            if (confirm) {
                                DbConnect.removeItem(email, cartItem.getUid());
                                lv.getItems().remove(cartItem);
                                lv.refresh();
                                updateProductCount(proCount, proCountBox);
                                updateTotalPrice(totAmount);}
                        } catch (SQLException e) {
                            e.printStackTrace();}});

                    VBox qtyBox = new VBox(50, removeButton, quantitySpinner);
                    qtyBox.setAlignment(Pos.CENTER_RIGHT);

                    HBox productLayout = new HBox(10, imagePlaceholder, productDetails, qtyBox);
                    productLayout.setAlignment(Pos.CENTER_LEFT);
                    HBox.setHgrow(productDetails, Priority.ALWAYS);
                    productLayout.setPadding(new Insets(10));
                    productLayout.setStyle("-fx-background-color: #303243; -fx-padding: 10; -fx-spacing: 10;");

                    productLayout.setMinHeight(150);

                    setGraphic(productLayout);
                    setStyle("-fx-background-color: transparent;");}}});

        VBox root = new VBox();
        root.setAlignment(Pos.TOP_LEFT);

        if (!cartItems.isEmpty()) {
            root.getChildren().add(proCountBox);}
        root.getChildren().add(lv);

        return root;}

    private void updateProductCount(Label proCount, HBox proCountBox) {
        try {
            int count = DbConnect.getCart(email).size();
            proCount.setText(String.valueOf(count));
            if (count == 0) {
                ((VBox) proCountBox.getParent()).getChildren().remove(proCountBox);}
        } catch (SQLException e) {
            e.printStackTrace();
            proCount.setText("0");}}

    private void updateTotalPrice(Label totAmount) {
        try {
            double totalPrice = DbConnect.getTotalCartPrice(email);
            totAmount.setText("$" + totalPrice);
        } catch (SQLException e) {
            e.printStackTrace();
            totAmount.setText("$0.00");}}


    private boolean showConfirmation(String Title, String Header, String Message){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(Title);
        alert.setHeaderText(Header);
        alert.setContentText(Message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;}

    private void showInfo(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transaction Information");
        alert.setHeaderText("Transaction success!");
        alert.setContentText("Your order is now in queue.");
        alert.showAndWait();}}
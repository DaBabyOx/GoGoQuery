package src;

import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.sql.*;
import java.util.*;

public class Home {
    private final Stage primaryStage;
    private String email = getSession.getInstance().getEmail();
    private String selectedCategory = "Select a category";
    private TextField search = new TextField();
    private ListView<Product> lv = new ListView<>();
    private Label proCountNumber;
    private Label proCountDescription;

    public Home(Stage primaryStage) throws SQLException {
        this.primaryStage = primaryStage;
        String uName = email.split("@")[0];
        Label welcLabel = new Label("Welcome,");
        welcLabel.setStyle("-fx-font-size: 50px; -fx-text-fill: white; -fx-font-style: italic;");
        Label uNameLabel = new Label(uName);
        uNameLabel.setStyle("-fx-font-size: 50px; -fx-text-fill: #FD9D22; -fx-font-style: italic;");
        HBox welcome = new HBox(10, welcLabel, uNameLabel);
        welcome.setAlignment(Pos.CENTER_LEFT);
        welcome.setPadding(new Insets(10));

        Label Filter = new Label("Filter");
        Filter.setStyle("-fx-font-size: 14px; -fx-text-fill: #707387; -fx-font-weight: bold;");

        Label category = new Label("Category");
        category.setStyle("-fx-font-size: 14px; -fx-text-fill: #707387; -fx-font-weight: bold;");

        Button apply = new Button("Apply");
        apply.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #FF9C24; -fx-border-radius: 10;");
        apply.setMinHeight(30);
        apply.setOnAction(e -> {
            refreshProductListView(selectedCategory, search.getText().trim().toLowerCase());});

        HBox buttons = new HBox(10, CatCombox(), apply);

        VBox cat = new VBox(10, category, buttons);
        cat.setAlignment(Pos.CENTER_LEFT);
        cat.setPadding(new Insets(10));
        cat.setStyle("-fx-background-color: #303243; -fx-border-radius: 10; -fx-background-radius: 10;");
        cat.setMaxSize(255, 120);
        cat.setMinHeight(110);

        VBox filcat = new VBox(10, Filter, cat);
        filcat.setAlignment(Pos.TOP_LEFT);
        filcat.setPadding(new Insets(10));

        proCountNumber = new Label(String.valueOf(DbConnect.getProducts().size()) + " ");
        proCountNumber.setStyle("-fx-font-size: 14px; -fx-text-fill: #EF9A24; -fx-font-weight: bold;");

        proCountDescription = new Label();
        proCountDescription.setStyle("-fx-font-size: 14px; -fx-text-fill: #707387; -fx-font-weight: bold;");
        refreshProductListView(selectedCategory, search.getText().trim().toLowerCase());

        Label proCountStart = new Label("Showing ");
        proCountStart.setStyle("-fx-font-size: 14px; -fx-text-fill: #707387; -fx-font-weight: bold;");

        HBox proCountBox = new HBox(proCountStart, proCountNumber, proCountDescription);
        proCountBox.setAlignment(Pos.CENTER_LEFT);
        proCountBox.setPadding(new Insets(10));

        VBox productBox = new VBox(proCountBox, productListView());

        HBox l2 = new HBox(5, filcat, productBox);
        l2.setAlignment(Pos.TOP_LEFT);
        l2.setPadding(new Insets(10));

        VBox l12 = new VBox(10, welcome, l2);
        l12.setAlignment(Pos.TOP_LEFT);

        GridPane gp = new GridPane();
        gp.setPadding(new Insets(10));
        gp.setHgap(10);
        gp.setVgap(50);
        gp.setAlignment(Pos.TOP_CENTER);
        gp.setMaxWidth(1920);
        gp.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 0%, #1B1A20, #323345);");
        gp.addRow(0, navBar());
        gp.addRow(1, l12);

        Scene scene = new Scene(gp, 1920, 1080);
        primaryStage.setTitle("Home");
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

        search.setPromptText("Search items in GoGoQuery Store");
        search.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: #545877");
        search.setMinWidth(750);
        search.setMinHeight(30);

        Button searchBut = new Button("Search");
        searchBut.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: #7278B2; -fx-border-radius: 10");
        searchBut.setMinHeight(30);
        searchBut.setOnAction(e -> {
            refreshProductListView(selectedCategory, search.getText().trim().toLowerCase());});

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

    private ComboBox<String> CatCombox() {
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.setPromptText("Select a category");
        categoryComboBox.setMinHeight(30);
        categoryComboBox.setStyle("-fx-font-size: 14px; -fx-text-fill: #949BC7; -fx-font-weight: bold; -fx-background-color: #545877; -fx-border-radius: 10;");

        categoryComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);}
                else {
                    setText(item);
                    setStyle("-fx-text-fill: #949BC7;");}}});

        try {
            List<String> categories = DbConnect.getItemCategories();
            categoryComboBox.getItems().addAll(categories);
        } catch (SQLException e) {
            e.printStackTrace();}

        categoryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedCategory = newValue;}});

        categoryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                categoryComboBox.setPromptText(null);}});

        return categoryComboBox;}

    private ListView<Product> productListView() {
        lv.setMinWidth(850);
        lv.setMinHeight(750);
        lv.setPadding(new Insets(10));

        lv.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 0;");

        lv.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                lv.lookupAll(".scroll-bar").forEach(scrollBar -> scrollBar.setStyle("-fx-opacity: 0; -fx-max-width: 0; -fx-max-height: 0;"));}});

        lv.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);

                if (empty || product == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");}
                else {
                    Region imagePlaceholder = new Region();
                    imagePlaceholder.setPrefSize(150 , 150);
                    imagePlaceholder.setStyle("-fx-background-color: #666; -fx-background-radius: 10;");

                    Label nameLabel = new Label(product.getName());
                    nameLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

                    Label priceLabel = new Label("$" + product.getPrice());
                    priceLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: #FFAD1F; -fx-font-weight: bold;");

                    Label stockLabel = new Label(product.getStock() + " Left");
                    stockLabel.setStyle(
                            "-fx-font-size: 18px; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-background-color: #FF2121; " +
                                    "-fx-padding: 2; " +
                                    "-fx-border-radius: 10; " +
                                    "-fx-background-radius: 10; " +
                                    "-fx-font-weight: bold;");

                    HBox priceStock = new HBox(10, priceLabel, stockLabel);
                    priceStock.setAlignment(Pos.CENTER_LEFT);

                    VBox productDetails = new VBox(5, nameLabel, priceStock);
                    productDetails.setAlignment(Pos.CENTER_LEFT);

                    HBox productLayout = new HBox(10, imagePlaceholder, productDetails);
                    productLayout.setAlignment(Pos.CENTER_LEFT);
                    productLayout.setPadding(new Insets(10));
                    productLayout.setStyle(
                            "-fx-background-color: #303243; " +
                                    "-fx-padding: 10; " +
                                    "-fx-spacing: 10;");

                    productLayout.setMinHeight(150);

                    productLayout.setOnMouseEntered(event ->
                            productLayout.setStyle("-fx-background-color: #252633;"));

                    productLayout.setOnMouseExited(event ->
                            productLayout.setStyle("-fx-background-color: #303243;"));

                    productLayout.setOnMouseClicked(event -> {
                        try {
                            new Detail(product, primaryStage);
                        } catch (SQLException e) {
                            e.printStackTrace();}});

                    VBox productWrapper = new VBox(20,productLayout);
                    productWrapper.setStyle("-fx-padding: 20;" + "-fx-spacing: 20;");
                    setGraphic(productLayout);
                    setStyle("-fx-background-color: transparent;");}}});

        try {
            List<Product> availableProducts = DbConnect.getProducts();
            availableProducts.removeIf(product -> product.getStock() < 1);
            lv.getItems().addAll(availableProducts);
        } catch (SQLException e) {
            e.printStackTrace();}

        return lv;}

    private void refreshProductListView(String category, String searchText) {
        try {
            ObservableList<Product> filteredProducts = DbConnect.filterProducts(category, searchText);
            lv.getItems().setAll(filteredProducts);

            int count = filteredProducts.size();

            TextFlow descriptionFlow = new TextFlow();

            boolean isSearchTextEmpty = (searchText == null || searchText.isEmpty());
            boolean isCategoryDefault = (category == null || category.equals("Select a category"));

            if (!isSearchTextEmpty && !isCategoryDefault) {
                descriptionFlow.getChildren().addAll(
                        DefaultText("for '"),
                        coloredText(searchText),
                        DefaultText("' and in '"),
                        coloredText(category),
                        DefaultText("' category"));}
            else if (!isSearchTextEmpty) {
                descriptionFlow.getChildren().addAll(
                        DefaultText("for '"),
                        coloredText(searchText),
                        DefaultText("'"));}
            else if (!isCategoryDefault) {
                descriptionFlow.getChildren().addAll(
                        DefaultText("in '"),
                        coloredText(category),
                        DefaultText("' category"));}
            else {
                descriptionFlow.getChildren().add(DefaultText("products"));}

            proCountNumber.setText(count + " ");
            proCountDescription.setText("");
            proCountDescription.setGraphic(descriptionFlow);
        } catch (SQLException e) {
            e.printStackTrace();}}

    private Text coloredText(String text) {
        Text colored = new Text(text);
        colored.setStyle("-fx-fill: #EF9A24;");
        return colored;}

    private Text DefaultText(String text) {
        Text defaultText = new Text(text);
        defaultText.setStyle("-fx-fill: #707387;");
        return defaultText;}}
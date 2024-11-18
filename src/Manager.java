package src;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import jfxtras.scene.control.window.Window;

public class Manager {
    private final Stage primaryStage;
    private final StackPane root;
    private final Label label;
    private Window currentWindow;

    public Manager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.root = new StackPane();

        label = new Label("Welcome to GoGoQuery Manager 2.0");
        label.setStyle("-fx-font-size: 48px;");
        label.setAlignment(Pos.CENTER);

        VBox mainContent = new VBox(label);
        mainContent.setAlignment(Pos.CENTER);

        root.getChildren().add(mainContent);

        BorderPane layout = new BorderPane();
        layout.setTop(createMenuBar());
        layout.setCenter(root);

        Scene scene = new Scene(layout, 1920, 1080);
        primaryStage.setTitle("GoGoQuery Manager 2.0");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();}

    private MenuBar createMenuBar() {
        MenuBar mb = new MenuBar();
        Menu pageMenu = new Menu("Menu");

        MenuItem addi = new MenuItem("Add Item");
        addi.setOnAction(e -> openWindow(addItem()));

        MenuItem qm = new MenuItem("Queue Manager");
        qm.setOnAction(e -> openWindow(queueManager()));

        MenuItem logout = new MenuItem("Logout");
        logout.setOnAction(e -> {
            primaryStage.close();
            new Auth().start(primaryStage);});

        pageMenu.getItems().addAll(addi, qm, logout);
        mb.getMenus().add(pageMenu);

        return mb;}

    private void openWindow(Window newWindow) {
        if (currentWindow != null) {
            root.getChildren().remove(currentWindow);
        }
        currentWindow = newWindow;
        root.getChildren().add(currentWindow);

        label.setVisible(false);}

    private Window addItem() {
        Window popUp = new Window("Add Item");
        popUp.getLeftIcons().clear();
        popUp.setPrefSize(1280, 720);
        popUp.setMaxSize(1280, 720);

        Label title = new Label("Add Item");
        title.setStyle("-fx-font-size: 48px;");

        Label name = new Label("Item Name: ");
        name.setStyle("-fx-font-size: 24px;");
        TextField nameField = new TextField();
        nameField.setMinSize(300, 30);

        Label desc = new Label("Item Desc: ");
        desc.setStyle("-fx-font-size: 24px;");
        TextArea descField = new TextArea();
        descField.setWrapText(true);
        descField.setPrefSize(300, 200);

        Label cat = new Label("Item Category: ");
        cat.setStyle("-fx-font-size: 24px;");
        TextField catField = new TextField();
        catField.setMinSize(300, 30);

        Label price = new Label("Item Price: ");
        price.setStyle("-fx-font-size: 24px;");
        TextField priceField = new TextField();
        priceField.setMinSize(300, 30);

        Label quant = new Label("Quantity: ");
        quant.setStyle("-fx-font-size: 24px;");
        Spinner<Integer> quantField = new Spinner<>(1, 300, 1);
        quantField.setMinSize(300, 30);

        Button add = new Button("Add Item");
        add.setStyle("-fx-background-color: #5CB85C; -fx-text-fill: white; -fx-font-size: 24px;");
        add.setOnAction(e -> {
            String itemName = nameField.getText();
            String itemDesc = descField.getText();
            String itemCat = catField.getText();
            String priceInp = priceField.getText();
            int itemQuant = quantField.getValue();

            double itemPrice;
            try{
                itemPrice = Double.parseDouble(priceInp);}
            catch(NumberFormatException ex){
                showError("Error", "Insert error!", "Item price must be a valid number.");
                return;}

            if(itemName.isEmpty() || itemDesc.isEmpty() || itemCat.isEmpty() || itemPrice == 0 || itemQuant == 0){
                showError("Error", "Inser error!", "All fields must be filled out.");}
            else if(itemName.length()<5 || itemName.length()>70){
                showError("Error", "Insert error!", "Item name must be between 5 and 70 characters.");}
            else if(itemDesc.length()<10 || itemDesc.length()>255){
                showError("Error", "Insert error!", "Item description must be between 10 and 255 characters.");}
            else if(itemPrice<0.50 || itemPrice >900000){
                showError("Error", "Insert error!", "Item price must be between $0.50 and $900,000.");}
            else{
                DbConnect.insertItem(itemName, itemCat, itemPrice, itemDesc, itemQuant);
                showSuccess();
                nameField.clear();
                descField.clear();
                catField.clear();
                priceField.clear();
                quantField.getValueFactory().setValue(1);}});

        GridPane gp = new GridPane();
        gp.setHgap(20);
        gp.setVgap(10);
        gp.addRow(0, name, nameField);
        gp.addRow(1, desc, descField);
        gp.addRow(2, cat, catField);
        gp.addRow(3, price, priceField);
        gp.addRow(4, quant, quantField);
        gp.add(add, 1, 5);
        gp.setAlignment(Pos.CENTER_LEFT);

        VBox layout = new VBox(10, title, gp);
        layout.setAlignment(Pos.CENTER_LEFT);
        popUp.getContentPane().getChildren().add(layout);

        return popUp;}

    private Window queueManager() {
        Window popUp = new Window("Queue Manager");
        popUp.getLeftIcons().clear();
        popUp.setPrefSize(1280, 720);
        popUp.setMaxSize(1280, 720);

        Label title = new Label("Queue Manager");
        title.setStyle("-fx-font-size: 48px;");

        TableView<Transaction> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefSize(1156, 600);

        TableColumn<Transaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Transaction, Integer> customerIdCol = new TableColumn<>("Customer ID");
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<Transaction, String> customerEmailCol = new TableColumn<>("Customer Email");
        customerEmailCol.setCellValueFactory(new PropertyValueFactory<>("customerEmail"));

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Transaction, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getItems().setAll(DbConnect.fetchAllTransactions());

        table.getColumns().addAll(idCol, customerIdCol, customerEmailCol, dateCol, amountCol, statusCol);

        Button send = new Button("Send Package");
        send.setStyle("-fx-background-color: #5CB85C; -fx-text-fill: white; -fx-font-size: 24px;");
        send.setOnAction(e -> {
            Transaction selected = table.getSelectionModel().getSelectedItem();
            if(selected == null){
                showError("Reference Error", "Reference error due to no transaction selected", "Please select a transaction");}
            else if(!"In Queue".equals(selected.getStatus())){
                showError("Reference Error", "Reference error due to invalid transaction status", "Please select a transaction that is still \"In Queue\"");}
            else{
                DbConnect.updateTransactionStatus(selected.getId(), "Sent");
                table.getItems().setAll(DbConnect.fetchAllTransactions());
                table.getSelectionModel().clearSelection();}});

        VBox talay = new VBox(table);
        talay.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, title, talay, send);
        layout.setAlignment(Pos.CENTER_LEFT);
        popUp.getContentPane().getChildren().add(layout);

        return popUp;}

    private void showError(String Title, String Header, String Message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Title);
        alert.setHeaderText(Header);
        alert.setContentText(Message);
        alert.showAndWait();}

    private void showSuccess(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Insert Success!");
        alert.setContentText("Item added to product catalog.");
        alert.showAndWait();}}
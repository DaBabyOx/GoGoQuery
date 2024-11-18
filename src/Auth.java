package src;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.*;

public class Auth extends Application {
    private Stage primaryStage;
    public Scene loginscn;
    private Scene registersscn;
    private Scene rolescn;
    private String emailStr;
    private String passStr;
    private String confpassStr;
    private String dobStr;
    private String genderStr;


    @Override
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Authentication Page");

        VBox loginbox = loginScene();
        loginscn = new Scene(sp(loginbox), 1920, 1080);

        VBox registerbox = registerScene();
        registersscn = new Scene(sp(registerbox), 1920, 1080);

        VBox rolebox = selRole();
        rolescn = new Scene(sp(rolebox), 1920, 1080);

        primaryStage.setScene(loginscn);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();}

    private VBox loginScene(){
        Label go = new Label("Go Go");
        go.setStyle("-fx-font-size: 75px; -fx-text-fill: white; -fx-font-style: italic; -fx-font-weight: bold;");

        go.setTranslateX(-20);
        go.setTranslateY(0);

        Label query = new Label("Query");
        query.setStyle("-fx-font-size: 100px; -fx-text-fill: #FD9D22; -fx-font-style: italic; -fx-font-weight: bold;");

        query.setTranslateX(20);
        query.setTranslateY(50);

        StackPane title = new StackPane();
        title.getChildren().addAll(go, query);
        title.setAlignment(Pos.CENTER);

        Label loginText = new Label("Login");
        loginText.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        HBox sep = new HBox();
        sep.setMaxWidth(245);
        sep.setMinHeight(2);
        sep.setStyle("-fx-background-color: black;");

        Label emailLabel = new Label("Email");
        TextField email = new TextField();
        email.setMaxWidth(300);

        Label passwordLabel = new Label("Password");
        PasswordField password = new PasswordField();
        password.setMaxWidth(300);

        Button login = new Button("Login");
        login.setStyle("-fx-background-color: #FD9D22; -fx-text-fill: white; -fx-font-weight: bold;");
        login.setPrefWidth(300);
        login.setOnAction(e -> {
            String emailStr = email.getText();
            String passStr = password.getText();
            if (emailStr.isEmpty() || passStr.isEmpty()) {
                showError("Invalid Login", "Log in failed", "Please fill in all fields");}
            else {
                try {
                    if (DbConnect.validateLogin(emailStr, passStr)) {
                        String role = DbConnect.getUserRole(emailStr);
                        getSession.getInstance().setEmail(emailStr);
                        if ("Shopper".equals(role)) {
                            new Home(primaryStage);}
                        else if ("Manager".equals(role)) {
                            new Manager(primaryStage);}}
                    else {
                        showError("Invalid Login", "Wrong Credentials", "You entered a wrong email or password");}
                } catch (SQLException ex) {
                    ex.printStackTrace();}}});

        Hyperlink register = new Hyperlink("Here!");
        register.setStyle("-fx-font-weight: bold;");
        register.setOnAction(e -> {primaryStage.setScene(registersscn);primaryStage.setFullScreen(true);});

        Label newAccount = new Label("Are you new? Register");
        HBox reg = new HBox(1, newAccount, register);
        reg.setAlignment(Pos.CENTER);

        VBox loginBox = new VBox(10, loginText, sep, emailLabel, email, passwordLabel, password, login, reg);
        loginBox.setPadding(new Insets(20));
        loginBox.setAlignment(Pos.TOP_LEFT);

        VBox whitebox = new VBox(loginBox);
        whitebox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 10, 0.5, 0, 0);");
        whitebox.setMaxWidth(400);
        whitebox.setMaxHeight(400);
        whitebox.setAlignment(Pos.CENTER);

        VBox loginMain = new VBox(50, title, whitebox);
        loginMain.setAlignment(Pos.CENTER);
        loginMain.setPadding(new Insets(50));

        return loginMain;}

    private VBox registerScene(){
        Label go = new Label("Go Go");
        go.setStyle("-fx-font-size: 75px; -fx-text-fill: white; -fx-font-style: italic; -fx-font-weight: bold;");

        go.setTranslateX(-20);
        go.setTranslateY(0);

        Label query = new Label("Query");
        query.setStyle("-fx-font-size: 100px; -fx-text-fill: #FD9D22; -fx-font-style: italic; -fx-font-weight: bold;");

        query.setTranslateX(20);
        query.setTranslateY(50);

        StackPane title = new StackPane();
        title.getChildren().addAll(go, query);
        title.setAlignment(Pos.CENTER);

        Label registerText = new Label("Register");
        registerText.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        HBox sep = new HBox();
        sep.setMaxWidth(245);
        sep.setMinHeight(2);
        sep.setStyle("-fx-background-color: black;");

        Label emailLabel = new Label("Email");
        TextField email = new TextField();
        email.setMaxWidth(300);

        Label passwordLabel = new Label("Password");
        PasswordField password = new PasswordField();
        password.setMaxWidth(300);

        Label confpassLabel = new Label("Confirm Password");
        PasswordField confpass = new PasswordField();
        confpass.setMaxWidth(300);

        Label dobLabel = new Label("Date of Birth");
        DatePicker dob = new DatePicker();
        dob.setMaxWidth(150);

        Label genderLabel = new Label("Gender");
        RadioButton mRadBut = new RadioButton("Male");
        RadioButton fRadBut = new RadioButton("Female");

        ToggleGroup gender = new ToggleGroup();
        mRadBut.setToggleGroup(gender);
        fRadBut.setToggleGroup(gender);

        HBox genders = new HBox(10, mRadBut, fRadBut);
        genders.setAlignment(Pos.CENTER_LEFT);

        CheckBox agree = new CheckBox("I agree to");
        Hyperlink terms = new Hyperlink("Terms and Conditions");
        HBox termsBox = new HBox(1, agree, terms);
        termsBox.setAlignment(Pos.CENTER_LEFT);

        Button regBut = new Button("Register");
        regBut.setStyle("-fx-background-color: #FD9D22; -fx-text-fill: white; -fx-font-weight: bold;");
        regBut.setPrefWidth(300);
        regBut.setOnAction(e -> {
            emailStr = email.getText();
            passStr = password.getText();
            confpassStr = confpass.getText();
            LocalDate dobs = dob.getValue();
            dobStr = (dobs != null) ? dobs.toString() : null;
            genderStr = mRadBut.isSelected() ? "Male" : fRadBut.isSelected() ? "Female" : null;

            if(emailStr.isEmpty()){
                showError("Register Failed", "Register Error", "Email must be filled");}
            else if(!emailStr.endsWith("@gomail.com")){
                showError("Register Failed", "Register Error", "Email must end with '@gomail.com'");}

            else {
                try {
                    if(DbConnect.checkEmail(emailStr)){
                        showError("Register Failed", "Register Error", "Email already exists");}}
                catch (SQLException ex) {
                    throw new RuntimeException(ex);}}

            if(passStr.isEmpty()){
                showError("Register Failed", "Register Error", "Password must be filled");}
            else if(!alphanumericval(passStr)){
                showError("Register Failed", "Register Error", "Password must be alphanumeric");}
            else if(!passStr.equals(confpassStr)){
                showError("Register Failed", "Register Error", "Passwords don't match");}

            else if(dobStr == null){
                showError("Register Failed", "Register Error", "Please select date of birth");}

            else if(Period.between(dobs, LocalDate.now()).getYears() < 17){
                showError("Register Failed", "Register Error", "You must be at least 17 years old");}

            else if((!mRadBut.isSelected() && !fRadBut.isSelected())){
                showError("Register Failed", "Register Error", "Select your gender");}

            else if(!agree.isSelected()){
                showError("Register Failed", "Register Error", "You must agree to terms and conditions");}

            else{
            primaryStage.setScene(rolescn);
            primaryStage.setFullScreen(true);}});

        Hyperlink logins = new Hyperlink("Here!");
        logins.setStyle("-fx-font-weight: bold;");
        logins.setOnAction(e -> {primaryStage.setScene(loginscn);primaryStage.setFullScreen(true);});

        Label oldAccount = new Label("Already have an account? Sign in");
        HBox log = new HBox(1, oldAccount, logins);
        log.setAlignment(Pos.CENTER);

        VBox regbox = new VBox(10, registerText, sep, emailLabel, email, passwordLabel, password, confpassLabel, confpass, dobLabel, dob, genderLabel, genders, termsBox, regBut, log);
        regbox.setPadding(new Insets(20));
        regbox.setAlignment(Pos.TOP_LEFT);

        VBox whitebox = new VBox(regbox);
        whitebox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 10, 0.5, 0, 0);");
        whitebox.setMaxWidth(400);
        whitebox.setMaxHeight(700);
        whitebox.setAlignment(Pos.CENTER);

        VBox registerMain = new VBox(50, title, whitebox);
        registerMain.setAlignment(Pos.CENTER);
        registerMain.setPadding(new Insets(50));

        return registerMain;}

    private VBox selRole(){
        Label go = new Label("Go Go");
        go.setStyle("-fx-font-size: 75px; -fx-text-fill: white; -fx-font-style: italic; -fx-font-weight: bold;");

        go.setTranslateX(-20);
        go.setTranslateY(0);

        Label query = new Label("Query");
        query.setStyle("-fx-font-size: 100px; -fx-text-fill: #FD9D22; -fx-font-style: italic; -fx-font-weight: bold;");

        query.setTranslateX(20);
        query.setTranslateY(50);

        StackPane title = new StackPane();
        title.getChildren().addAll(go, query);
        title.setAlignment(Pos.CENTER);

        Circle managerCircle = new Circle(50);
        managerCircle.setStyle("-fx-fill: #415261;");

        Label managerInitial = new Label("M");
        managerInitial.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

        Circle managerStatusCircle = new Circle(10);
        managerStatusCircle.setStyle("-fx-fill: #00FF00;");

        StackPane managerAvatar = new StackPane(managerCircle, managerInitial);
        StackPane managerAvatarWithStatus = new StackPane(managerAvatar, managerStatusCircle);
        StackPane.setAlignment(managerStatusCircle, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(managerStatusCircle, new Insets(0, -5, -5, 0));
        managerAvatarWithStatus.setMaxWidth(100);
        managerStatusCircle.setTranslateX(-10);
        managerStatusCircle.setTranslateY(-5);

        Label managerRole = new Label("Manager");
        managerRole.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox sep1 = new HBox();
        sep1.setMinWidth(250);
        sep1.setMinHeight(1);
        sep1.setStyle("-fx-background-color: #E3E3E3;");

        Label managerDesc = new Label("Manage products and deliveries, be the ruler!");

        Button managerBtn = new Button("Register as Manager");
        managerBtn.setStyle("-fx-background-color: #FD9D22; -fx-text-fill: white; -fx-font-weight: bold;");
        managerBtn.setMinWidth(300);
        managerBtn.setMinHeight(50);
        managerBtn.setOnAction(e ->{
            String role = "Manager";
            try{
                if(DbConnect.registerUser(dobStr, emailStr, passStr, genderStr, role)){
                    showSuccess();
                    primaryStage.setScene(loginscn);primaryStage.setFullScreen(true);}
                else{
                    showError("Register Failed", "Register Error", "Registration failed, please try again");}}
            catch (SQLException ex){
                showError("Register Failed", "Register Error", "Something went wrong.");}});


        VBox managerBox = new VBox(10, managerAvatarWithStatus, managerRole, sep1, managerDesc, managerBtn);
        managerBox.setAlignment(Pos.CENTER);
        managerBox.setStyle("-fx-background-color: white; -fx-padding: 20;");
        managerBox.setMinWidth(500);

        Circle shopperCircle = new Circle(50);
        shopperCircle.setStyle("-fx-fill: #7A425C;");

        Label shopperInitial = new Label("S");
        shopperInitial.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

        Circle shopperStatusCircle = new Circle(10);
        shopperStatusCircle.setStyle("-fx-fill: #00FF00;");

        StackPane shopperAvatar = new StackPane(shopperCircle, shopperInitial);
        StackPane shopperAvatarWithStatus = new StackPane(shopperAvatar, shopperStatusCircle);
        StackPane.setAlignment(shopperStatusCircle, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(shopperStatusCircle, new Insets(0, -5, -5, 0));
        shopperAvatarWithStatus.setMaxWidth(100);
        shopperStatusCircle.setTranslateX(-10);
        shopperStatusCircle.setTranslateY(-5);

        Label shopperRole = new Label("Shopper");
        shopperRole.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox sep2 = new HBox();
        sep2.setMinWidth(250);
        sep2.setMinHeight(1);
        sep2.setStyle("-fx-background-color: #E3E3E3;");

        Label shopperDesc = new Label("Search products, manage your cart, go shopping!");

        Button shopperBtn = new Button("Register as Shopper");
        shopperBtn.setStyle("-fx-background-color: #FD9D22; -fx-text-fill: white; -fx-font-weight: bold;");
        shopperBtn.setMinWidth(300);
        shopperBtn.setMinHeight(50);
        shopperBtn.setOnAction(e ->{
            String role = "Shopper";
            try{
                if(DbConnect.registerUser(dobStr, emailStr, passStr, genderStr, role)){
                    showSuccess();
                    primaryStage.setScene(loginscn);primaryStage.setFullScreen(true);}
                else{
                    showError("Register Failed", "Register Error", "Registration failed, please try again");}}
            catch (SQLException ex){
                showError("Register Failed", "Register Error", "Something went wrong.");}});

        VBox shopperBox = new VBox(10, shopperAvatarWithStatus, shopperRole, sep2, shopperDesc, shopperBtn);
        shopperBox.setAlignment(Pos.CENTER);
        shopperBox.setStyle("-fx-background-color: white; -fx-padding: 20;");
        shopperBox.setMinWidth(500);

        HBox rolesBox = new HBox(50, managerBox, shopperBox);
        rolesBox.setAlignment(Pos.CENTER);

        VBox mainBox = new VBox(50, title, rolesBox);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(50));

        return mainBox;}

    private StackPane sp (VBox a){
        StackPane sp = new StackPane();
        sp.getChildren().add(a);
        sp.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 0%, #1B1A20, #323345);");
        return sp;}

    private void showError(String Title, String Header, String Message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Title);
        alert.setHeaderText(Header);
        alert.setContentText(Message);
        alert.showAndWait();}

    private void showSuccess(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Register Information");
        alert.setHeaderText("Register success!");
        alert.setContentText("please log in with your newly created account.");
        alert.showAndWait();}

    private boolean alphanumericval(String name) {
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : name.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;}
            else if (Character.isDigit(c)) {
                hasDigit = true;}
            if (hasLetter && hasDigit) {
                return true;}}
        return false;}

    public static void main(String[] args){
        launch(args);}}
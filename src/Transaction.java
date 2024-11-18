package src;

public class Transaction {
    private int id;
    private int customerId;
    private String customerEmail;
    private String date;
    private double amount;
    private String status;

    public Transaction(int id, int customerId, String customerEmail, String date, double amount, String status) {
        this.id = id;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.date = date;
        this.amount = amount;
        this.status = status;}

    public int getId() {
        return id;}

    public int getCustomerId() {
        return customerId;}

    public String getCustomerEmail() {
        return customerEmail;}

    public String getDate() {
        return date;}

    public double getAmount() {
        return amount;}

    public String getStatus() {
        return status;}}
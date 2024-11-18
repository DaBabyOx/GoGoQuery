package src;

public class Product {
    private int id;
    private String name;
    private String category;
    private double price;
    private String desc;
    private int stock;

    public Product(int id, String name, String category, double price, String desc, int stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.desc = desc;
        this.stock = stock;}

    public int getId() {
        return id;}

    public String getName() {
        return name;}

    public String getCategory() {
        return category;}

    public double getPrice() {
        return price;}

    public String getDesc() {
        return desc;}

    public int getStock() {
        return stock;}}
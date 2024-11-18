package src;

public class getCart {
    private int uid, pid;
    private double price;
    private String name ;
    private int qty;

    public getCart(int uid, int pid, String name, double price, int qty) {
        this.uid = uid;
        this.pid = pid;
        this.name = name;
        this.price = price;
        this.qty = qty;}

    public int getUid() {
        return uid;}

    public int getPid() {
        return pid;}

    public String getName() {
        return name;}

    public double getPrice() {
        return price;}

    public int getQty() {
        return qty;}}
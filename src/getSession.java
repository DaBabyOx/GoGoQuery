package src;

public class getSession {
    private static getSession instance;
    private String email;

    public static getSession getInstance(){
        if(instance == null){
            instance = new getSession();}
        return instance;}

    public void setEmail(String email){
        this.email = email;}

    public String getEmail(){
        return email;}}
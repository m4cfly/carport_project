package app.entities;


public class User {

    int userID;

    String name;

    String password;

    int balance;

    String role;

    public User(int userID, String name,String password,int balance, String role) {
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.balance = balance;
        this.role = role;
    }

    public int getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getBalance() {
        return balance;
    }

    public String getRole() {
        return role;
    }

}

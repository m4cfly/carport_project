package app.entities;

public class Users {

    int userID;

    String name;

    String role;

    int balance;

    public Users(int userID, String name, String role, int balance) {
        this.userID = userID;
        this.name = name;
        this.role = role;
        this.balance = balance;
    }

    public int getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public int getBalance() {
        return balance;
    }
}

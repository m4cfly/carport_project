package app.entities;

public class Users {

    int userID;

    String name;

    String role;


    public Users(int userID, String name, String role) {
        this.userID = userID;
        this.name = name;
        this.role = role;
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
}

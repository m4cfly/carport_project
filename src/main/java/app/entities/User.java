package app.entities;

import java.util.Objects;

public class User {

    int userId;
    String userName;
    String password;
    int userBalance;
    String userRole;

    public User() {
    }

    public User(int userId, String userName, String password, int userBalance, String userRole)
    {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.userBalance = userBalance;
        this.userRole = userRole;
    }

    public User(int userId, int userBalance, String userRole) {
        this.userId = userId;
        this.userBalance = userBalance;
        this.userRole = userRole;
    }


    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public int getUserBalance() {
        return userBalance;
    }

    public String getUserRole() {
        return userRole;
    }

    @Override
    public String toString() {
        return "Users{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPassword='" + password + '\'' +
                ", userBalance=" + userBalance +
                ", userRole='" + userRole + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return getUserId() == user.getUserId() && getUserBalance() == user.getUserBalance() && Objects.equals(getUserName(), user.getUserName()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getUserRole(), user.getUserRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getUserName(), getPassword(), getUserBalance(), getUserRole());
    }
}

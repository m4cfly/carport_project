package app.entities;

public class User {

    int userId;
    String userName;
    String password;
    int userBalance;
    String userRole;

    public User(int userId, String userName, String password, int userBalance, String userRole)
    {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
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
}

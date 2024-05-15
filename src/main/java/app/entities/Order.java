package app.entities;

public class Order {

    int orderID;

    int length;

    int width;

    int totalPrice;

    int statusID;

    User user;

    public Order(int orderID, int length, int width, int totalPrice, int statusID, int userID) {
        this.orderID = orderID;
        this.length = length;
        this.width = width;
        this.totalPrice = totalPrice;
        this.statusID = statusID;
        this.user = user;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getStatusID() {
        return statusID;
    }

    public User getUser() {
        return user;
    }
}

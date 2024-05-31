package app.entities;

import java.util.Objects;

public class Order {

    int orderID;

    int length;

    int width;

    int totalPrice;

    int statusID;

    User user;

    public Order(int orderID, int length, int width, int totalPrice, int statusID, User user) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return getOrderID() == order.getOrderID() && getLength() == order.getLength() && getWidth() == order.getWidth() && getTotalPrice() == order.getTotalPrice() && getStatusID() == order.getStatusID() && Objects.equals(getUser(), order.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrderID(), getLength(), getWidth(), getTotalPrice(), getStatusID(), getUser());
    }
}

package app.entities;

public class Material_Item {

    int materialItemId;

    int quantity;

    String description;

    int orderID;

    int materialVariantID;

    public Material_Item(int materialItemId, int quantity, String description, int orderID, int materialVariantID) {
        this.materialItemId = materialItemId;
        this.quantity = quantity;
        this.description = description;
        this.orderID = orderID;
        this.materialVariantID = materialVariantID;
    }

    public int getMaterialItemId() {
        return materialItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getMaterialVariantID() {
        return materialVariantID;
    }
}

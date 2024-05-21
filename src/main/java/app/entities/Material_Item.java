package app.entities;

public class Material_Item {

    int materialItemId;

    int quantity;

    String description;

    Order order;

    MaterialVariant materialVariant;

    public Material_Item(int materialItemId, int quantity, String description, Order order, MaterialVariant materialVariant) {
        this.materialItemId = materialItemId;
        this.quantity = quantity;
        this.description = description;
        this.order = order;
        this.materialVariant = materialVariant;
    }

    public Material_Item(int anInt, int quantity, String description, int orderID, int materialVariantID) {
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

    public Order getOrder() {
        return order;
    }

    public MaterialVariant getMaterialVariant() {
        return materialVariant;
    }
}

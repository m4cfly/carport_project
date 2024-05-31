package app.entities;

import java.util.Objects;

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

    public Material_Item(int m_item_id, int quantity, String description, int orderID, int materialVariantID) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Material_Item that)) return false;
        return getMaterialItemId() == that.getMaterialItemId() && getQuantity() == that.getQuantity() && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getOrder(), that.getOrder()) && Objects.equals(getMaterialVariant(), that.getMaterialVariant());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaterialItemId(), getQuantity(), getDescription(), getOrder(), getMaterialVariant());
    }
}

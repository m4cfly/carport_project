package app.entities;

import java.util.Objects;

public class Material {

    int materialID;

    String name;

    int price;

    String unit;

    public Material(int materialID, String name, int price, String unit) {
        this.materialID = materialID;
        this.name = name;
        this.price = price;
        this.unit = unit;
    }

    public int getMaterialID() {
        return materialID;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getUnit() {
        return unit;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Material material)) return false;
        return getMaterialID() == material.getMaterialID() && getPrice() == material.getPrice() && Objects.equals(getName(), material.getName()) && Objects.equals(getUnit(), material.getUnit());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaterialID(), getName(), getPrice(), getUnit());
    }
}

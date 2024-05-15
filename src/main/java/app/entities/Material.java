package app.entities;

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
}

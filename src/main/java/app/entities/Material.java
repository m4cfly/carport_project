package app.entities;

public class Material {

    int materialID;

    String name;

    int price;

    int unitID;

    public Material(int materialID, String name, int price, int unitID) {
        this.materialID = materialID;
        this.name = name;
        this.price = price;
        this.unitID = unitID;
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

    public int getUnitID() {
        return unitID;
    }
}

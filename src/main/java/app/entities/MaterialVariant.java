package app.entities;

public class MaterialVariant {

    int materialVariantID;

    int materialID;

    int length;


    public MaterialVariant(int materialVariantID, int materialID, int length) {
        this.materialVariantID = materialVariantID;
        this.materialID = materialID;
        this.length = length;
    }


    public int getMaterialVariantID() {
        return materialVariantID;
    }

    public int getMaterialID() {
        return materialID;
    }

    public int getLength() {
        return length;
    }
}

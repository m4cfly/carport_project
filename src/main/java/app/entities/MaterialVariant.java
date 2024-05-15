package app.entities;

public class MaterialVariant {

    int materialVariantID;

    Material material;

    int length;


    public MaterialVariant(int materialVariantID, Material material, int length) {
        this.materialVariantID = materialVariantID;
        this.material = material;
        this.length = length;
    }


    public int getMaterialVariantID() {
        return materialVariantID;
    }

    public Material getMaterial() {
        return material;
    }

    public int getLength() {
        return length;
    }
}

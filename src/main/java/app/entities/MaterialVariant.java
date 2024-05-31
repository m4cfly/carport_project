package app.entities;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaterialVariant that)) return false;
        return getMaterialVariantID() == that.getMaterialVariantID() && getLength() == that.getLength() && Objects.equals(getMaterial(), that.getMaterial());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaterialVariantID(), getMaterial(), getLength());
    }
}

package app.services;

import app.entities.Order;
import app.entities.Material_Item;
import app.entities.MaterialVariant;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;

import java.util.ArrayList;
import java.util.List;

public class Calculator {
    private static final int POSTS = 1;
    private static final int RAFTERS = 2;
    private static final int BEAMS = 2;

    private List<Material_Item> materialItems = new ArrayList<>();
    private int width;
    private int length;
    private ConnectionPool connectionPool;

    public Calculator(int width, int length, ConnectionPool connectionPool) {
        this.width = width;
        this.length = length;
        this.connectionPool = connectionPool;
    }

    public void calcCarport(Order order) throws DatabaseException {
        calcPost(order);
        calcBeams(order);
        calcRafters(order);
    }

    // Stolper
    private void calcPost(Order order) throws DatabaseException {
        // Antal stolper
        int quantity = calcPostQuantity();
        // Længde på stolper - dvs variant
        List<MaterialVariant> materialVariants = MaterialMapper.getVariantsByProductIdAndMinLength(0, POSTS, connectionPool);
        MaterialVariant materialVariant = materialVariants.get(0);
        Material_Item materialItem = new Material_Item(0, quantity, "Stolper nedgraves 90 cm. i jord", order, materialVariant);
        materialItems.add(materialItem);
    }

    public int calcPostQuantity() {
        return 2 * (2 + (length - 130) / 340);
    }

    // Remme
    private void calcBeams(Order order) throws DatabaseException {
//       int beamLength = calcBeamLength();
//
//       if ()
//
//       if (600 < length && length <= 780) {
//           int beamLength = calcBeamLength() / 3
//       }


        // Find variant
        List<MaterialVariant> materialVariants = MaterialMapper.getVariantsByProductIdAndMinLength(length, BEAMS, connectionPool);

        boolean found = false;
        int lengthDifference = length;
        MaterialVariant chosenVariant = null;

        while (!found) {
            for (MaterialVariant variant : materialVariants) {
//            MaterialVariant materialVariant = materialVariants.get(materialVariants.indexOf(variant));
                boolean foundInCurrentLoop = false;
                if (length % variant.getLength() == 0) {
                    int quantity = (length / variant.getLength()) * 2;
                    Material_Item materialItem = new Material_Item(0, quantity, "Rem placeres ovenpå stolper", order, variant);
                    materialItems.add(materialItem);
                    found = true;
                    break;
                } else if (variant.getLength() - length < lengthDifference) {
                    lengthDifference = variant.getLength() - length;
                    chosenVariant = variant;
                    foundInCurrentLoop = true;

                }


                if (chosenVariant.getLength() > length && foundInCurrentLoop) {
                    int quantity = (length % chosenVariant.getLength()) * 2;
                    Material_Item materialItem = new Material_Item(0, quantity, "Rem placeres ovenpå stolper, og resten saves af", order, chosenVariant);
                    materialItems.add(materialItem);
                    found = true;
                    break;

                }


                if (!found) {

                    List<MaterialVariant> allMaterialVariants = MaterialMapper.getVariantsByProductIdAndMinLength(0, BEAMS, connectionPool);
                    int variantSpareWood = length;
                    chosenVariant = null;
                    for (MaterialVariant allmaterialVariant : allMaterialVariants) {
                        if (length == allmaterialVariant.getLength()) {
                            int beamQuantity = (length % allmaterialVariant.getLength()) * 2;
                            Material_Item materialItem = new Material_Item(0, beamQuantity, "Rem placeres ovenpå stolper", order, allmaterialVariant);
                            materialItems.add(materialItem);
                            found = true;
                            break;
                        }

                        if (length % allmaterialVariant.getLength() == 0) {
                            int beamQuantity = (length % allmaterialVariant.getLength()) * 2;
                            Material_Item materialItem = new Material_Item(0, beamQuantity, "Rem placeres ovenpå stolper", order, allmaterialVariant);
                            materialItems.add(materialItem);
                            found = true;
                            break;

                        } else if (length % allmaterialVariant.getLength() < variantSpareWood) {
                            variantSpareWood = length % allmaterialVariant.getLength();
                            chosenVariant = allmaterialVariant;
                            break;
                        }


                    }
                    if (chosenVariant != null && variantSpareWood % chosenVariant.getLength() == 0) {
                        int beamQuantity = (length % chosenVariant.getLength()) * 2;
                        Material_Item materialItem = new Material_Item(0, beamQuantity, "Rem placeres ovenpå stolper", order, chosenVariant);
                        materialItems.add(materialItem);
                        found = true;

                    }

                }

            }
        }
    }


    public int calcBeamLength() {
        return length * 2;

    }

    // Spær
    private void calcRafters(Order order) throws DatabaseException {
        double data = calcRaftQuantity();
        Double newData = Double.valueOf(data);
        int quantity = newData.intValue();

        // Find variant
        List<MaterialVariant> materialVariants = MaterialMapper.getVariantsByProductIdAndMinLength(width, RAFTERS, connectionPool);


        boolean found = false;
        MaterialVariant chosenVariant = null;

        while (!found) {
            for (MaterialVariant variant : materialVariants) {
//                MaterialVariant materialVariant = materialVariants.get(materialVariants.indexOf(variant));
                if (width == variant.getLength()) {
                    Material_Item materialItem = new Material_Item(0, quantity, "Spær placeres ovenpå rem", order, variant);
                    materialItems.add(materialItem);
                    found = true;
                    break;
                } else if (variant.getLength() > width) {
//                    int finalQuantity = (variant.getLength() / width) * quantity;
                    Material_Item materialItem = new Material_Item(0, quantity, "Spær placeres ovenpå rem, rest saves af", order, variant);
                    materialItems.add(materialItem);
                    found = true;
                    break;

                }

            }
            if (!found) {
                List<MaterialVariant> allMaterialVariants = MaterialMapper.getVariantsByProductIdAndMinLength(0, RAFTERS, connectionPool);
                int variantSpareWood = width;

                for (MaterialVariant allmaterialVariant : allMaterialVariants) {
                    if (width == allmaterialVariant.getLength()) {
                        int raftQuantity = (width % allmaterialVariant.getLength()) * 2;
                        Material_Item materialItem = new Material_Item(0, raftQuantity, "Spær placeres ovenpå rem", order, allmaterialVariant);
                        materialItems.add(materialItem);
                        found = true;
                        break;
                    }

                    if (width % allmaterialVariant.getLength() == 0) {
                        int raftQuantity = (width % allmaterialVariant.getLength()) * 2;
                        Material_Item materialItem = new Material_Item(0, raftQuantity, "Spær placeres ovenpå rem", order, allmaterialVariant);
                        materialItems.add(materialItem);
                        found = true;
                        break;

                    } else if (width % allmaterialVariant.getLength() < variantSpareWood) {
                        variantSpareWood = width % allmaterialVariant.getLength();
                        chosenVariant = allmaterialVariant;
                        break;
                    }


                }
                if (chosenVariant != null && variantSpareWood % chosenVariant.getLength() == 0) {
                    int raftQuantity = (width % chosenVariant.getLength()) * 2;
                    Material_Item materialItem = new Material_Item(0, raftQuantity, "Spær placeres ovenpå rem", order, chosenVariant);
                    materialItems.add(materialItem);
                    found = true;


                }



            }

        }


    }

    public double calcRaftQuantity() {
        return 2 + (length / (4.5 + 55));
    }


    public List<Material_Item> getMaterialItems() {
        return materialItems;
    }
}

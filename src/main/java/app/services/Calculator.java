package app.services;

import app.entities.Order;
import app.entities.Material_Item;
import app.entities.MaterialVariant;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import app.persistence.OrderMapper;

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
        // Længde på stolper - dvs varianter fra databasens material_variant liste
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


        // Er der trælængder større end carport længden, hvis ja = true, nej = false
        List<MaterialVariant> materialVariantsCheck = MaterialMapper.getVariantsByProductIdAndMinLength(0, BEAMS, connectionPool);
        boolean variantLengthBiggerThanLength = false;
        for (MaterialVariant materialVariantCheck : materialVariantsCheck) {
            if (materialVariantCheck.getLength() >= length) {
                variantLengthBiggerThanLength = true;
            }

        }


        boolean found = false;
        int lengthDifference = length;
        MaterialVariant chosenVariant = null;
        boolean foundInCurrentLoop = false;

        if (variantLengthBiggerThanLength) { // nu løber vi kun varianter igennem, hvis der er nogen der er længere end carport længden
            List<MaterialVariant> materialVariants = MaterialMapper.getVariantsByProductIdAndMinLength(length, BEAMS, connectionPool);
            while (!found) {
                // vi løber alle trælængder igennem og finder hvad der er tættest på længden
                for (MaterialVariant variant : materialVariants) {
//            MaterialVariant materialVariant = materialVariants.get(materialVariants.indexOf(variant));
                    if (variant.getLength() % length == 0) {
                        int quantity = (length / variant.getLength()) * 2;
                        Material_Item materialItem = new Material_Item(1, quantity, "Rem placeres ovenpå stolper", order, variant);
                        materialItems.add(materialItem);
                        found = true;
                        break;

                    } else if (variant.getLength() - length < lengthDifference) { // vi finder den variant med mindst længdeforskel til carportlængden
                        lengthDifference = variant.getLength() - length;
                        chosenVariant = variant;
                        foundInCurrentLoop = true;

                    }
                }

                // chosenVariant er den variant der er tættest på længden, men som samtidig er større end længden - for at minimere spild
                if (chosenVariant != null) {
                    if (chosenVariant.getLength() > length && foundInCurrentLoop) {
                        int quantity = (chosenVariant.getLength() / length) * 2;
                        Material_Item materialItem = new Material_Item(1, quantity, "Rem placeres ovenpå stolper, og resten saves af", order, chosenVariant);
                        materialItems.add(materialItem);
                        found = true;
                        break;

                    }
                }
            }
        }

//              Hvis der ikke er nogen træbrædder der er større end carportens længde, finder vi en alternativ løsning:
        while (!found) {

            // vi løber alle trælængder igennem - OGSÅ dem der ikke er større end længden, så vi kan finde bedst mulige kombination med mindst træspild
            List<MaterialVariant> allMaterialVariants = MaterialMapper.getVariantsByProductIdAndMinLength(0, BEAMS, connectionPool);
            double variantSpareWood = length; // double for at gøre vores senere division mere præcis

            for (MaterialVariant allmaterialVariant : allMaterialVariants) {
                if (length == allmaterialVariant.getLength()) { // fanger hvis der stadig er en trælængde, der er det samme som længden
                    int beamQuantity = (length / allmaterialVariant.getLength()) * 2;
                    Material_Item materialItem = new Material_Item(1, beamQuantity, "Rem placeres ovenpå stolper", order, allmaterialVariant);
                    materialItems.add(materialItem);
                    found = true;
                    break;
                }

                if (length % allmaterialVariant.getLength() == 0) { // går nuværende variants længde op i længden - hvor resten = 0?
                    int beamQuantity = (length / allmaterialVariant.getLength()) * 2;
                    Material_Item materialItem = new Material_Item(1, beamQuantity, "Rem placeres ovenpå stolper", order, allmaterialVariant);
                    materialItems.add(materialItem);
                    found = true;
                    break;

                } else if ((double) length / (double) allmaterialVariant.getLength() < variantSpareWood) { // hvis der er en rest ser vi hvor meget træ der ville være til overs
                    // er resten mindre en variantSpareWood, erstattes variantSpareWood med denne
                    // caster til double fordi vi vil have at der divideres med decimaler
                    variantSpareWood = (double) length / (double) allmaterialVariant.getLength();
                    chosenVariant = allmaterialVariant;

                }


            }

            if (chosenVariant != null && variantSpareWood == (double) length / (double) chosenVariant.getLength()) { // løsningen med mindst mulig træ-rest indsættes, hvis vi har beregnet den i iter-loopet
                double beamQuantitydouble = ((double)length / (double) chosenVariant.getLength()) * 2;
                int beamQuantity = (int) Math.ceil(beamQuantitydouble); // vi vil altid runde antal rem op, hvilket Math.ceil gør
                Material_Item materialItem = new Material_Item(1, beamQuantity, "Rem placeres ovenpå stolper, rest saves af", order, chosenVariant);
                materialItems.add(materialItem);
                found = true;
                break;

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
        int quantity = newData.intValue(); // vi fjerner decimalerne helt her, fordi vi skal have et bestemt mellemrum (55 cm) mellem hver spær,
        // dvs. vi ikke kan runde op, men må runde ned.


//        ligesom med spær checker vi først om carportlængden er mindre end nogen af varianterne
        List<MaterialVariant> materialVariantsCheck = MaterialMapper.getVariantsByProductIdAndMinLength(0, BEAMS, connectionPool);
        boolean variantLengthBiggerThanLength = false;
        for (MaterialVariant materialVariantCheck : materialVariantsCheck) {
            if (materialVariantCheck.getLength() >= width) {
                variantLengthBiggerThanLength = true;
            }

        }


        boolean found = false;
        MaterialVariant chosenVariant = null;

        if (variantLengthBiggerThanLength) { // hvis der er varianter der er større end bredde

            // Find varianter med minimumslængden (ift. carport bredde) eller større
            List<MaterialVariant> materialVariants = MaterialMapper.getVariantsByProductIdAndMinLength(width, RAFTERS, connectionPool);

            while (!found) {
                for (MaterialVariant variant : materialVariants) {

                    if (width == variant.getLength()) { // hvis en træbredde er ligeså lang som bredden
                        Material_Item materialItem = new Material_Item(2, quantity, "Spær placeres ovenpå rem", order, variant);
                        materialItems.add(materialItem);
                        found = true;
                        break;
                    } else if (variant.getLength() > width) { // ellers tager vi den alligevel hvis den stadig er større (da længderne er stillet i længdeorden)

                        Material_Item materialItem = new Material_Item(2, quantity, "Spær placeres ovenpå rem, rest saves af", order, variant);
                        materialItems.add(materialItem);
                        found = true;
                        break;

                    }

                }
            }
        }
            if (!found) { // hvis en passende løsning endnu ikke er fundet, løber vi alle muligheder igennem.
                // Især hvis bredden er længere end træbredderne
                List<MaterialVariant> allMaterialVariants = MaterialMapper.getVariantsByProductIdAndMinLength(0, RAFTERS, connectionPool);
                double variantSpareWood = width;

                for (MaterialVariant allmaterialVariant : allMaterialVariants) {
                    if (width == allmaterialVariant.getLength()) { // for en sikkerhedsskyld, hvis der stadig er en træbredde der har samme længde som bredden
                        int raftQuantity = (width / allmaterialVariant.getLength()) * quantity;
                        Material_Item materialItem = new Material_Item(2, raftQuantity, "Spær placeres ovenpå rem", order, allmaterialVariant);
                        materialItems.add(materialItem);
                        found = true;
                        break;
                    }

                    if (width % allmaterialVariant.getLength() == 0) { // hvis længden på træbredden går op i bredden med en rest = 0
                        int raftQuantity = (width / allmaterialVariant.getLength()) * quantity;
                        Material_Item materialItem = new Material_Item(2, raftQuantity, "Spær placeres ovenpå rem", order, allmaterialVariant);
                        materialItems.add(materialItem);
                        found = true;
                        break;

                    } else if ((double) width / (double) allmaterialVariant.getLength() < variantSpareWood) { // ellers finder vi træresten, hvor den mindst rest af alle varianter = chosenVariant
                        variantSpareWood = (double) width / (double) allmaterialVariant.getLength();
                        chosenVariant = allmaterialVariant;

                    }


                }
                if (chosenVariant != null && variantSpareWood == (double) width / (double) chosenVariant.getLength()) { // når vi skal indsætte de træbredder der giver mindst i træ-rest
                    int raftQuantity = (width / chosenVariant.getLength()) + quantity;
                    Material_Item materialItem = new Material_Item(2, raftQuantity, "Spær placeres ovenpå rem", order, chosenVariant);
                    materialItems.add(materialItem);
                    found = true;



                }
            }


    }

    public double calcRaftQuantity() { // beregning af antal af spær der kan være på den valgte længde
        return 2 + (length / (4.5 + 55));
    }


    public List<Material_Item> getMaterialItems() {
        return materialItems;
    }
}

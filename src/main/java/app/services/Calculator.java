package app.services;

import app.entities.Order;
import app.entities.Material_Item;
import app.entities.MaterialVariant;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;

import java.util.ArrayList;
import java.util.List;

public class Calculator
{
    private static final int POSTS = 1;
    private static final int RAFTERS = 2;
    private static final int BEAMS = 2;

    private List<Material_Item> materialItems = new ArrayList<>();
    private int width;
    private int length;
    private ConnectionPool connectionPool;

    public Calculator(int width, int length, ConnectionPool connectionPool)
    {
        this.width = width;
        this.length = length;
        this.connectionPool = connectionPool;
    }

    public void calcCarport(Order order) throws DatabaseException
    {
        calcPost(order);
        calcBeams(order);
        calcRafters(order);
    }

    // Stolper
    private void calcPost(Order order) throws DatabaseException
    {
        // Antal stolper
        int quantity = calcPostQuantity();
        // Længde på stolper - dvs variant
        List<MaterialVariant> materialVariants = MaterialMapper.getVariantsByProductIdAndMinLength(0, POSTS, connectionPool);
        MaterialVariant materialVariant = materialVariants.get(0);
        Material_Item materialItem = new Material_Item(0, order, materialVariant, quantity, "Stolper nedgraves 90 cm. i jord");
        materialItems.add(materialItem);
    }

    public int calcPostQuantity()
    {
        return 2 * (2 + (length - 130) / 340);
    }

   // Remme
   private void calcBeams(Order order) throws DatabaseException {
       int beamLength = calcBeamLength();
       int beamQuantity = calcBeamLength() % 2 + 2;

       // Find variant
       List<MaterialVariant> materialVariants = MaterialMapper.getVariantsByProductIdAndMinLength(0, BEAMS, connectionPool);
       MaterialVariant materialVariant = materialVariants.get(0);
       Material_Item materialItem = new Material_Item(0, order, materialVariant, beamQuantity, "Stolper nedgraves 90 cm. i jord");
       materialItems.add(materialItem);



   }

    private int calcBeamLength() {
        return length * 2;

    }

    // Spær
   private void calcRafters(Order order)
   {
       double data = calcRaftQuantity();
       Double newData = Double.valueOf(data);
       int value = newData.intValue();

       // Find variant
       List<MaterialVariant> materialVariants = MaterialMapper.getVariantsByProductIdAndMinLength(0, BEAMS, connectionPool);
       MaterialVariant materialVariant = materialVariants.get(0);
       Material_Item materialItem = new Material_Item(0, order, materialVariant, value, "Stolper nedgraves 90 cm. i jord");
       materialItems.add(materialItem);
   }

    private double calcRaftQuantity() {
        return 2 + (length / (4.5 + 55));
    }


    public List<Material_Item> getMaterialItems()
    {
        return materialItems;
    }
}

package app.controllers;

import app.entities.Material_Item;
import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import app.services.Calculator;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class OrderController {

    public static void addRoutes (Javalin app, ConnectionPool connectionPool) {
        app.post("/search", ctx -> addRoutes((Javalin) ctx, connectionPool));
        app.get("confirmation", ctx -> ctx.render("confirmation.html"));
        app.post("/confirmation", ctx -> calculateCarport(ctx, connectionPool));
        app.post("/showsketch", ctx -> showSketch(ctx, connectionPool));
        app.get("/sendrequest", ctx -> sendRequest(ctx, connectionPool));
        app.post("/showbom", ctx -> showBom(ctx, connectionPool));
        app.get("/showorders", ctx -> showOrders(ctx, connectionPool));
    }

    private static void calculateCarport(Context ctx, ConnectionPool connectionPool)
    {
        ctx.render("confirmation.html");
    }
    private static void showOrders(Context ctx, ConnectionPool connectionPool)
    {
        // Get orders from DB
        try
        {
            List<Order> orders = OrderMapper.getAllOrders(connectionPool);
            ctx.attribute("orders", orders);
            ctx.render("orders/showorders.html");
        }
        catch (DatabaseException e)
        {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
    }

    private static void showBom(Context ctx, ConnectionPool connectionPool)
    {

        int orderId = Integer.parseInt(ctx.formParam("orderId"));
        try
        {
            List<Material_Item> materialItems = OrderMapper.getMaterialItemsByOrderId(orderId, connectionPool);

            if (materialItems.size() == 0)
            {
                ctx.render("orders/showbom.html");
                return;
            }

            Material_Item materialItem = materialItems.get(0);

            ctx.attribute("width", materialItem.getOrder().getWidth());
            ctx.attribute("length", materialItem.getOrder().getLength());
            ctx.attribute("materialItems", materialItems);
            ctx.render("orders/showbom.html");
        }
        catch (DatabaseException e)
        {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
    }

    private static void sendRequest(Context ctx, ConnectionPool connectionPool)
    {
        // Get order details from front-end
        int width = ctx.sessionAttribute("width");
        int length = ctx.sessionAttribute("length");
        int status = 1; // hardcoded for now
        int totalPrice = 0; // hardcoded for now
        User user = new User(1, "", "", 20000, "customer"); // hardcoded for now

        Order order = new Order(0, status, width, length, totalPrice, user);

        // TODO: Insert order in database
        try
        {
            order = OrderMapper.insertOrder(order, connectionPool);

            // TODO: Calculate order items (stykliste)
            Calculator calculator = new Calculator(width, length, connectionPool);
            calculator.calcCarport(order);

            // TODO: Save order items in database (stykliste)
            OrderMapper.insertMaterialItems(calculator.getMaterialItems(), connectionPool);

            // TODO: Create message to customer and render order / request confirmation

            ctx.render("orderflow/requestconfirmation.html");
        }
        catch (DatabaseException e)
        {
            // TODO: handle exception later
            throw new RuntimeException(e);
        }
    }

    private static void showSketch(Context ctx, ConnectionPool connectionPool)
    {
        int width = Integer.parseInt(ctx.formParam("width"));
        int length = Integer.parseInt(ctx.formParam("length"));
        ctx.sessionAttribute("width", width);
        ctx.sessionAttribute("length", length);
        ctx.render("showSketch.html");
    }

}

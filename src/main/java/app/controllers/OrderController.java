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
        app.post("/payForOrder", ctx -> payForOrder(ctx, connectionPool));
        app.get("/payForOrder", ctx -> payForOrder(ctx, connectionPool));
    }

    private static void calculateCarport(Context ctx, ConnectionPool connectionPool)
    {
        ctx.render("confirmation.html");
    }
    public static void showOrder(Context ctx)
    {
        // TODO: Create a SVG Drawing and inject into the showOrder.html template as a string

        String svgText = "<svg width=\"600\" height=\"850\" viewBox=\"-50 -50 700 900\">\n" +
                "            <!-- Rammen -->\n" +
                "            <rect x=\"0\" y=\"0\" width=\"600\" height=\"780\" style=\"stroke:#000000; fill: none\" />\n" +
                "\n" +
                "            <!-- Spære -->\n" +
                "            <rect x=\"0\" y=\"0\" width=\"600\" height=\"10\" style=\"stroke:#000000; fill: #ffffff\" />\n" +
                "            <rect x=\"0\" y=\"770\" width=\"600\" height=\"10\" style=\"stroke:#000000; fill: #ffffff\" />\n" +
                "\n" +
                "            <!-- Rem -->\n" +
                "            <rect x=\"0\" y=\"150\" width=\"600\" height=\"10\" style=\"stroke:#000000; fill: #ffffff\" />\n" +
                "            <rect x=\"0\" y=\"620\" width=\"600\" height=\"10\" style=\"stroke:#000000; fill: #ffffff\" />\n" +
                "\n" +
                "            <!-- Krydset i midten af tegningen -->\n" +
                "            <line x1=\"0\" y1=\"150\" x2=\"600\" y2=\"620\" style=\"stroke:#000000; stroke-dasharray: 10 10;\" />\n" +
                "            <line x1=\"0\" y1=\"620\" x2=\"600\" y2=\"150\" style=\"stroke:#000000; stroke-dasharray: 10 10;\" />\n" +
                "\n" +
                "            <!-- Stolper -->\n" +
                "            <rect x=\"50\" y=\"140\" width=\"10\" height=\"10\" style=\"stroke:#000000; fill: #ffffff\" />\n" +
                "            <rect x=\"540\" y=\"140\" width=\"10\" height=\"10\" style=\"stroke:#000000; fill: #ffffff\" />\n" +
                "            <rect x=\"50\" y=\"610\" width=\"10\" height=\"10\" style=\"stroke:#000000; fill: #ffffff\" />\n" +
                "            <rect x=\"540\" y=\"610\" width=\"10\" height=\"10\" style=\"stroke:#000000; fill: #ffffff\" />\n" +
                "\n" +
                "            <!-- Pilen i koden.(Den der sidder på enden af X og Y linjerne til at indikere længden)  -->\n" +
                "            <defs>\n" +
                "                <marker id=\"arrow\" markerWidth=\"10\" markerHeight=\"10\" refX=\"0\" refY=\"3\" orient=\"auto\" markerUnits=\"strokeWidth\">\n" +
                "                    <path d=\"M0,0 L0,6 L9,3 z\" fill=\"#000000\" />\n" +
                "                </marker>\n" +
                "            </defs>\n" +
                "\n" +
                "            <!-- Vandret pil -->\n" +
                "            <line x1=\"0\" y1=\"800\" x2=\"600\" y2=\"800\" style=\"stroke:#000000; marker-start:url(#arrow); marker-end:url(#arrow);\" />\n" +
                "            <text x=\"300\" y=\"825\" font-family=\"Arial\" font-size=\"20\" text-anchor=\"middle\">600 cm</text>\n" +
                "\n" +
                "            <!-- Lodret pil -->\n" +
                "            <line x1=\"-20\" y1=\"0\" x2=\"-20\" y2=\"780\" style=\"stroke:#000000; marker-start:url(#arrow); marker-end:url(#arrow);\" />\n" +
                "            <text x=\"-65\" y=\"390\" font-family=\"Arial\" font-size=\"20\" text-anchor=\"middle\" >780 cm</text>\n" +
                "        </svg>";
        ctx.attribute("svg", svgText);
        ctx.render("showOrder.html");
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

            ctx.render("requestconfirmation.html");
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

    private static void payForOrder (Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        Order userOrder = ctx.sessionAttribute("userOrder");
        int userId = user.getUserId();

        try {
            OrderMapper.payForOrder(userOrder, userId, connectionPool);

            if (user.getUserBalance() >= userOrder.getTotalPrice()) {
                ctx.attribute("message", "Du har betalt for din bestilling. Tak for handlen, vi vender tilbage hurtigst muligt");
                ctx.render("/confirmation.html");
            }
            else {
                ctx.attribute("message", "Du har ikke penge nok på din konto");
                ctx.render("/cart.html");
            }

        } catch (DatabaseException e) {
            ctx.attribute("message", "Noget gik galt mens betalingen blev udført, pengene er ikke blevet trukket");
            ctx.render("/cart.html");
        }
    }


}

package app.controllers;

import app.entities.Material_Item;
import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import app.services.Calculator;
import app.services.CarportSvg;
import app.services.Svg;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Locale;

public class OrderController {

    public static void addRoutes (Javalin app, ConnectionPool connectionPool) {
        app.post("/showsketch", ctx -> showSketch(ctx, connectionPool));
        app.get("/sendrequest", ctx -> sendRequest(ctx, connectionPool));
        app.post("/showbom", ctx -> showBom(ctx, connectionPool));
        app.get("/showorders", ctx -> showOrders(ctx, connectionPool));
        app.post("/showorders", ctx -> showOrders(ctx, connectionPool));
        app.post("/payForOrder", ctx -> payForOrder(ctx, connectionPool));
        app.get("/payForOrder", ctx -> payForOrder(ctx, connectionPool));
//        app.get("/showOrder", ctx -> showOrder(ctx));
    }

    private static void calculateCarport(Context ctx, ConnectionPool connectionPool)
    {
        ctx.render("/index.html");
    }
    public static void showOrder(Context ctx)
    {
        // TODO: Create a SVG Drawing and inject into the showorders.html template as a string
        Locale.setDefault(new Locale("US"));
        CarportSvg svg = new CarportSvg(600, 780);
        ctx.attribute("svg", svg.toString());
        ctx.render("showorders.html");
    }

    private static void showOrders(Context ctx, ConnectionPool connectionPool)
    {
        // Get orders from DB
        try
        {
            List<Order> orders = OrderMapper.getAllOrders(connectionPool);
            ctx.attribute("orders", orders);
            ctx.render("order/showorders.html");
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
                ctx.render("order/bom.html");
                return;
            }

            Material_Item materialItem = materialItems.get(0);

            ctx.attribute("width", materialItem.getOrder().getWidth());
            ctx.attribute("length", materialItem.getOrder().getLength());
            ctx.attribute("materialItems", materialItems);
            ctx.render("order/bom.html");
        }
        catch (DatabaseException e)
        {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
    }

    private static void sendRequest(Context ctx, ConnectionPool connectionPool) throws DatabaseException
    {
        // Get order details from front-end
        User user = ctx.sessionAttribute("currentUser");
        int width = ctx.sessionAttribute("width");
        int length = ctx.sessionAttribute("length");
        ctx.sessionAttribute("width", width);
        ctx.sessionAttribute("length", length);
        int status = 1; // hardcoded for now
        int totalPrice = 0; // hardcoded for now
        int userId = user.getUserId();

        Order order = new Order(0, length, width, totalPrice, status, user);

        // TODO: Insert order in database
        try
        {
            order = OrderMapper.insertOrder(order, connectionPool);
            ctx.sessionAttribute("order", order);

            // TODO: Calculate order items (stykliste)
            Calculator calculator = new Calculator(width, length, connectionPool);
            calculator.calcCarport(order);

            // TODO: Save order items in database (stykliste)
            OrderMapper.insertMaterialItems(calculator.getMaterialItems(), connectionPool);

            ctx.attribute("message", "Du har nu indsendt din forespørgsel.");
            ctx.render("index.html");
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
        ctx.render("draw/showsketch.html");
    }

    private static void payForOrder (Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        Order userOrder = ctx.sessionAttribute("userOrder");
        int userId = user.getUserId();

        try {
            OrderMapper.payForOrder(userOrder, userId, connectionPool);

            if (user.getUserBalance() >= userOrder.getTotalPrice()) {
                ctx.attribute("message", "Du har betalt for din bestilling. Tak for handlen, vi vender tilbage hurtigst muligt");
                ctx.render("/index.html");
            }
            else {
                ctx.attribute("message", "Du har ikke penge nok på din konto");
                ctx.render("/index.html");
            }

        } catch (DatabaseException e) {
            ctx.attribute("message", "Noget gik galt mens betalingen blev udført, pengene er ikke blevet trukket");
            ctx.render("/cart.html");
        }
    }


}

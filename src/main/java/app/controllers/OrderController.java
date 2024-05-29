package app.controllers;

import app.entities.Material_Item;
import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.services.Calculator;
import app.services.CarportSvg;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("/showsketch", ctx -> showSketch(ctx, connectionPool));
        app.get("/sendrequestSite", ctx -> ctx.render("order/sendrequest"));
        app.get("/sendrequest", ctx -> sendRequest(ctx, connectionPool));
        app.post("/sendrequest", ctx -> sendRequest(ctx, connectionPool));
        app.get("/showbom", ctx -> showBom(ctx, connectionPool));
        app.post("/showbom", ctx -> showBom(ctx, connectionPool));
        app.get("/showorders", ctx -> showOrders(ctx, connectionPool));
        app.post("/showorders", ctx -> showOrders(ctx, connectionPool));
        app.get("/showordersbyid", ctx -> showOrdersByID(ctx, connectionPool));
        app.post("/showordersbyid", ctx -> showOrdersByID(ctx, connectionPool));
        app.get("/goToPayment", ctx -> ctx.render("order/payfororder.html"));
        app.post("/goToPayment", ctx -> ctx.render("order/payfororder.html"));
        app.post("/payForOrder", ctx -> payForOrder(ctx, connectionPool));
        app.get("/payForOrder", ctx -> payForOrder(ctx, connectionPool));
        app.get("/payForOrderUpdate", ctx -> ctx.render("order/payfororderupdate.html"));
        app.post("/payForOrderUpdate", ctx -> ctx.render("order/payfororderupdate.html"));
    }

    private static void showOrdersByID(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        if (user == null) {
            ctx.status(500).result("User session attribute is null");
            return;
        }

        int userId = user.getUserId();

        try {
            List<Order> orders = OrderMapper.viewShoppingCart(userId, connectionPool);
            ctx.attribute("orders", orders);
            ctx.render("order/showorders.html");
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void calculateCarport(Context ctx, ConnectionPool connectionPool) {
        ctx.render("/index.html");
    }

    public static void showOrder(Context ctx) {
        Locale.setDefault(new Locale("US"));
        CarportSvg svg = new CarportSvg(600, 780);
        ctx.attribute("svg", svg.toString());
        ctx.render("showorders.html");
    }

    private static void showOrders(Context ctx, ConnectionPool connectionPool) {
        try {
            List<Order> orders = OrderMapper.getAllOrders(connectionPool);
            ctx.attribute("orders", orders);
            ctx.render("order/showorders.html");
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void showBom(Context ctx, ConnectionPool connectionPool) {

        int orderId = ctx.sessionAttribute("orderID");
        try {
            List<Material_Item> materialItems = OrderMapper.getMaterialItemsByOrderId(orderId, connectionPool);

            if (materialItems.size() != 3) {
                ctx.render("/sendrequest.html");
                return;
            }

            int totalPrice = OrderMapper.calculatePrice(orderId, connectionPool);
            ctx.attribute("materialItems", materialItems);
            ctx.sessionAttribute("totalPrice", totalPrice);
            ctx.render("order/bom.html");

        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendRequest(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        User user = ctx.sessionAttribute("currentUser");
        if (user == null) {
            ctx.status(500).result("User session attribute is null");
            return;
        }

        int width = ctx.sessionAttribute("width");
        int length = ctx.sessionAttribute("length");
        int status = 1; // 1 = aktiv
        int totalPrice = 0; // hardcoded
        int userId = user.getUserId();

        Order order = new Order(0, length, width, totalPrice, status, user);
        ctx.sessionAttribute("order", order);

        try {
            order = OrderMapper.insertOrder(order, connectionPool);
            ctx.sessionAttribute("order", order);
            ctx.sessionAttribute("orderID", order.getOrderID());

            Calculator calculator = new Calculator(width, length, connectionPool);
            calculator.calcCarport(order);

            OrderMapper.insertMaterialItems(calculator.getMaterialItems(), connectionPool);

            ctx.attribute("message", "Du har nu indsendt din forespørgsel.");
            ctx.render("/customerinfo.html");
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void showSketch(Context ctx, ConnectionPool connectionPool) throws DatabaseException {

        String widthString = ctx.formParam("width");
        String lengthString = ctx.formParam("length");

        if (!("0").equals(widthString) && !("0").equals(lengthString) ) {
            int width = Integer.parseInt(ctx.formParam("width"));
            int length = Integer.parseInt(ctx.formParam("length"));
            ctx.sessionAttribute("width", width);
            ctx.sessionAttribute("length", length);
            ctx.render("draw/showsketch.html");
        }
        else {
            ctx.attribute("message", "Husk at vælge bredde og længde!");
            ctx.render("index.html");

        }



    }

    private static void payForOrder(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        if (user == null) {
            ctx.status(500).result("User session attribute is null");
            return;
        }

        Order userOrder = ctx.sessionAttribute("order");
        if (userOrder == null) {
            ctx.status(500).result("Order session attribute is null");
            return;
        }

        int userId = user.getUserId();

        try {
            int totalPrice = OrderMapper.calculatePrice(userOrder.getOrderID(), connectionPool);

            Order updatedOrder = new Order(userOrder.getOrderID(), userOrder.getLength(), userOrder.getWidth(), totalPrice, userOrder.getStatusID(), userOrder.getUser());
            ctx.sessionAttribute("order", updatedOrder);

            if (user.getUserBalance() >= updatedOrder.getTotalPrice()) {
                OrderMapper.payForOrder(updatedOrder, totalPrice, userId, connectionPool);

                int userBalance = user.getUserBalance() - totalPrice;

                User updatedUser = new User(userId, user.getUserName(), user.getPassword(), userBalance, user.getUserRole());

                ctx.sessionAttribute("currentUser", updatedUser);

                sendEmail(); // Send email after successful payment

                ctx.attribute("message", "Du har betalt for din bestilling. Tak for handlen, vi vender tilbage hurtigst muligt");
                ctx.render("order/requestconfirm.html");
            } else {
                ctx.attribute("message", "Du har ikke penge nok på din konto");
                ctx.render("order/insertmoney.html");
            }

        } catch (DatabaseException e) {
            ctx.attribute("message", "Noget gik galt mens betalingen blev udført, pengene er ikke blevet trukket");
            ctx.render("order/payfororder.html");
        }
    }

    private static void sendEmail() {
        System.out.println("Preparing to send email");
        Email from = new Email("Drassuil@gmail.com");
        from.setName("Johannes Fog Byggemarked");

        Mail mail = new Mail();
        mail.setFrom(from);

        String API_KEY = System.getenv("SENDGRID_API_KEY");
        if (API_KEY == null) {
            System.out.println("Error: SENDGRID_API_KEY environment variable not set.");
            return;
        }

        System.out.println("SENDGRID_API_KEY: " + API_KEY); // Debugging statement

        Personalization personalization = new Personalization();
        personalization.addTo(new Email("Drassuil@gmail.com"));
        personalization.addDynamicTemplateData("name", "David Vig");
        personalization.addDynamicTemplateData("email", "Drassuil@gmail.com");
        personalization.addDynamicTemplateData("zip", "2100");
        mail.addPersonalization(personalization);

        mail.addCategory("carportapp");

        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            mail.setTemplateId("d-d75882e1ca844bf18add2bc944aaf40e");
            request.setBody(mail.build());
            System.out.println("Request Body: " + request.getBody()); // Debugging statement
            Response response = sg.api(request);
            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
            System.out.println("Response Headers: " + response.getHeaders());
        } catch (IOException ex) {
            System.out.println("Error sending mail");
            ex.printStackTrace();
        }
    }
}

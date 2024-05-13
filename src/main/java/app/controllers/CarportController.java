package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

public class CarportController {
    /*
    public static void addRoutes(Javalin app, ConnectionPool connectionPool)
    {
        app.get("/", ctx -> index(ctx, connectionPool));
        app.post("addtocart", ctx -> addToCart(ctx));
        app.post("deletefromcart", ctx -> deleteFromCart(ctx));
        app.post("checkout", ctx -> checkout(ctx, connectionPool));
    }
    private static void checkout(Context ctx, ConnectionPool connectionPool)
    {
        Cart cart = ctx.sessionAttribute("cart");

        if (cart.getCount() > 0)
        {

            // TODO: Opret ordre og få orderId tilbage
            System.out.println("Opret ordre ********* ");
            // Loop igennem kurven
            for (CartItem cartItem : cart.getCartItemList())
            {
                // TODO: Insæt ordrelinier i databasen en adgangen
                System.out.println("Orderline: " + cartItem.getBottom().getBottomId() + ": " + cartItem.getTop().getTopId());
            }

            ctx.render("checkout.html");
        }else{
            System.out.println("kurven er tom!");
            ctx.render("index.html");
        }
    }
    private static void deleteFromCart(Context ctx)
    {
        int rowIndex = Integer.parseInt(ctx.formParam("rowindex"));
        Cart cart = ctx.sessionAttribute("cart");
        cart.delete(rowIndex - 1);
        ctx.sessionAttribute("cart", cart);
        ctx.render("index.html");
    }
    private static void addToCart(Context ctx)
    {
        int topId = Integer.parseInt(ctx.formParam("top"));
        int bottomId = Integer.parseInt(ctx.formParam("bottom"));
        int quantity = Integer.parseInt(ctx.formParam("quantity"));
        Map<Integer, Top> topMap = ctx.sessionAttribute("topMap");
        Map<Integer, Bottom> bottomMap = ctx.sessionAttribute("bottomMap");
        Top top = topMap.get(topId);
        Bottom bottom = bottomMap.get(bottomId);
        Cart cart = ctx.sessionAttribute("cart");
        cart.add(top, bottom, quantity);
        ctx.sessionAttribute("cart", cart);
        ctx.render("index");
    }

    private static void index(Context ctx, ConnectionPool connectionPool)
    {
        // Hent alle top og bunde og gem i session attibutes (som hashmap)
        try
        {
            Map<Integer, Top> topMap = CupcakeMapper.getTopMap(connectionPool);
            Map<Integer, Bottom> bottomMap = CupcakeMapper.getBottomMap(connectionPool);
            Cart cart = ctx.sessionAttribute("cart");
            if (cart == null)
            {
                cart = new Cart();
            }
            ctx.sessionAttribute("cart", cart);
            ctx.sessionAttribute("topMap", topMap);
            ctx.sessionAttribute("bottomMap", bottomMap);
            ctx.render("index");
        }
        catch (DatabaseException e)
        {
            // TODO: skal sende besked ud til forsiden
            throw new RuntimeException(e);
        }

    }

     */
}

package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class OrderController {

    public static void addRoutes (Javalin app, ConnectionPool connectionPool) {
        app.post("/search", ctx -> addRoutes((Javalin) ctx, connectionPool));
        app.get("confirmation", ctx -> ctx.render("confirmation.html"));
        app.post("/confirmation", ctx -> calculateCarport(ctx, connectionPool));
    }

    private static void calculateCarport(Context ctx, ConnectionPool connectionPool)
    {
        ctx.render("confirmation.html");
    }


}

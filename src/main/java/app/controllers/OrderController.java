package app.controllers;

import io.javalin.Javalin;
import org.postgresql.jdbc2.optional.ConnectionPool;

public class OrderController {

    public static void addRoutes (Javalin app, ConnectionPool connectionPool) {
        app.post("/search", ctx -> addRoutes((Javalin) ctx, connectionPool));
        app.get("/search", ctx -> ctx.render("index.html"));
    }


}

package app.controllers;

import io.javalin.Javalin;
import org.postgresql.jdbc2.optional.ConnectionPool;

public class UserController {

    public static void addRoutes (Javalin app, ConnectionPool connectionPool) {
        app.post("/search", ctx -> search(ctx, connectionPool));
        app.get("/search", ctx -> ctx.render("index.html"));
    }


}

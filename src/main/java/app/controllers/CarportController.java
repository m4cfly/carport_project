package app.controllers;

public class CarportController {

    public static void addRoutes (Javalin app, ConnectionPool connectionPool) {
        app.post("/search", ctx -> search(ctx, connectionPool));
        app.get("/search", ctx -> ctx.render("index.html"));
    }

}

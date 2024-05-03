package app.controllers;

public class MaterialController {

    public static void addRoutes (Javalin app, ConnectionPool connectionPool) {
        app.post("/search", ctx -> search(ctx, connectionPool));
        app.get("/search", ctx -> ctx.render("index.html"));
    }

}

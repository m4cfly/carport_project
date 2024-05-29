package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;


public class UserController {

    public static void addRoutes (Javalin app, ConnectionPool connectionPool)
    {
        app.get("/", ctx -> index(ctx, connectionPool));
        app.get("/login", ctx -> index(ctx, connectionPool));
        app.post("login", ctx -> login(ctx, connectionPool));
        app.get("/logout", ctx -> logout(ctx));
        app.get("/createuser", ctx -> ctx.render("createuser.html"));
        app.post("/createuser", ctx -> createUser(ctx, connectionPool));
        app.get("/insertmoney", ctx -> ctx.render("order/insertmoney.html"));
        app.post("/inputmoney", ctx -> inputMoney(ctx, connectionPool));
        app.post("/saveuserinfo", ctx -> customerInfo(ctx, connectionPool));
    }

    private static void customerInfo(Context ctx, ConnectionPool connectionPool) {
        ctx.render("order/payfororder.html");
    }

    private static void inputMoney(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");

        int userId = user.getUserId();
        if(ctx.formParam("inputMoney").isEmpty()) {
            ctx.attribute("message", "Husk at skrive et beløb");
            ctx.render("order/insertmoney.html");
            return;
        }
            int moneyInput = Integer.parseInt(ctx.formParam("inputMoney"));

        try {
            UserMapper.inputMoney(moneyInput, userId, connectionPool);

            if (moneyInput > 0) {
                int userBalance = user.getUserBalance() + moneyInput;
                User updatedUser = new User(userId, user.getUserName(), user.getPassword(), userBalance, user.getUserRole());
                ctx.sessionAttribute("currentUser", updatedUser);
                ctx.attribute("message", "Du har lagt " + moneyInput + " kr. ind på din konto");
                ctx.attribute("message", "Du har nu " + updatedUser.getUserBalance() + " kr. på din konto");


                ctx.render("order/insertmoney.html");

            }
            else {
                ctx.attribute("message", "Noget gik galt");
                ctx.render("order/insertmoney.html");
            }

        } catch (DatabaseException e) {
            ctx.attribute("message", "Noget gik galt mens betalingen blev udført, pengene er ikke blevet trukket");
            ctx.render("order/payfororder.html");
        }
    }

    private static void createUser(Context ctx, ConnectionPool connectionPool)
    {
        String username = ctx.formParam("username");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");



        if (password1.equals(password2)){
            try{
                UserMapper.createuser(username,password1, connectionPool);
                ctx.attribute("message", "Bruger oprettet! " + username + " Nu skal du logge ind!");
                ctx.render("createuser.html");
            }catch (DatabaseException e){
                ctx.attribute("message", "Dit brugernavn findes allerede. Prøv igen, eller log ind");
                ctx.render("createuser.html");
            }
        }else{
            ctx.attribute("message", "Dine to passwords matcher ikke! Prøv igen");
            ctx.render("createuser.html");
        }
    }

    private static void logout(Context ctx)
    {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    private static void login(Context ctx, ConnectionPool connectionPool) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        try{
            User user = UserMapper.login(username,password,connectionPool);
            ctx.sessionAttribute("currentUser", user);

           // List<Order> orderList = OrderMapper.getAllOrdersPerUser(user.getUserId(), connectionPool);
            //ctx.attribute("orderList", orderList);
            ctx.render("index.html");
        }
        catch (DatabaseException e)
        {
            ctx.attribute("message", e.getMessage());
            ctx.render("index.html");
        }
    }
    private static void index(Context ctx, ConnectionPool connectionPool) {
        ctx.render("index.html");
    }
}

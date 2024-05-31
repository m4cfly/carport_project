package test;

import app.entities.Material_Item;
import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


// Integrationstest for OrderMapper - brugt database fra localhost, så den skal forbindes først
// husk at lave test Schema i Postgres
class OrderMapperTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "carport";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll // køres før testene køres
    static void setupClass()
    {
        try (Connection connection = connectionPool.getConnection())
        {
            try (Statement stmt = connection.createStatement())
            {
                // The test schema is already created, so we only need to delete/create test tables
                stmt.execute("DROP TABLE IF EXISTS test.users");
                stmt.execute("DROP TABLE IF EXISTS test.orders");
                stmt.execute("DROP SEQUENCE IF EXISTS test.users_user_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.orders_order_id_seq CASCADE;");
                // Create tables as copy of original public schema structure
                stmt.execute("CREATE TABLE test.users AS (SELECT * from public.users) WITH NO DATA");
                stmt.execute("CREATE TABLE test.orders AS (SELECT * from public.orders) WITH NO DATA");
                // Create sequences for auto generating id's for users and orders
                stmt.execute("CREATE SEQUENCE test.users_user_id_seq");
                stmt.execute("ALTER TABLE test.users ALTER COLUMN user_id SET DEFAULT nextval('test.users_user_id_seq')");
                stmt.execute("CREATE SEQUENCE test.orders_order_id_seq");
                stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            fail("Database connection failed");
        }
    }


    @BeforeEach // køres før hver eneste test
    void setUp() {
        try (Connection connection = connectionPool.getConnection())
        {
            try (Statement stmt = connection.createStatement())
            {
                // Remove all rows from all tables
                stmt.execute("DELETE FROM test.orders");
                stmt.execute("DELETE FROM test.users");

                stmt.execute("INSERT INTO test.users (user_id, user_name, user_password, user_role, user_balance) " +
                        "VALUES  (1, 'jon', '1234', 'customer', 25000), (2, 'benny', '1234', 'admin', 45000)");

                stmt.execute("INSERT INTO test.orders (order_id, width, length, status, total_price, user_id) " +
                        "VALUES (1, 600, 780, 1, 20000, 1), (2, 540, 700, 2, 15000, 2), (3, 480, 600, 1, 14000, 1)") ;
                // Set sequence to continue from the largest member_id
                stmt.execute("SELECT setval('test.orders_order_id_seq', COALESCE((SELECT MAX(order_id) + 1 FROM test.orders), 1), false)");
                stmt.execute("SELECT setval('test.users_user_id_seq', COALESCE((SELECT MAX(user_id) + 1 FROM test.users), 1), false)");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            fail("Database connection failed");
        }
    }

    @Test
    void testGetAllOrders() {
        try
        {
            int expected = 3;
            List<Order> orders = OrderMapper.getAllOrders(connectionPool);
            assertEquals(expected, orders.size());
        }
        catch (DatabaseException e)
        {
            fail("Database fejl: " + e.getMessage());
        }
    }

//    @Test
//    void testGetMaterialItemsByOrderId() {
//        try
//        {
//            User user = new User(1, "jon", "1234", 25000, "customer");
//            Material_Item materialItem = new Material_Item(1, 4, "hej", 1, 2);
//            Material_Item materialItem2 = new Material_Item(2, 8, "hej", 1, 2);
//            Material_Item materialItem3 = new Material_Item(3, 6, "hej", 1, 2);
//            List <Material_Item> expected = OrderMapper.insertMaterialItems();
//            List <Material_Item> orderList =  OrderMapper.getMaterialItemsByOrderId(1, connectionPool);
//            assertEquals(expected, orderList);
//        }
//        catch (DatabaseException e)
//        {
//            fail("Database fejl: " + e.getMessage());
//        }
//
//    }

    @Test
    void testInsertOrder() {
        try
        {
            User user = new User(1, "jon", "1234", 25000, "customer");
            Order newOrder = new Order(2, 550, 750, 20000, 1, user);
//            UserMapper.createuser(user.getUserName(), user.getPassword(), connectionPool);
            newOrder = OrderMapper.insertOrder(newOrder, connectionPool);
            Order dbOrder = OrderMapper.getOrderByOrderID(newOrder.getOrderID(), connectionPool);
            assertEquals(newOrder, dbOrder);
        }
        catch (DatabaseException e)
        {
            fail("Database fejl: " + e.getMessage());
        }
    }


//    @Test
//    void testInsertMaterialItems() {
//    }

    @Test
    void testPayForOrder() {

        try
        {
            User user = new User(1, "jon", "1234", 25000, "customer");
            Order newOrder = new Order(2, 550, 750, 20000, 1, user);
            UserMapper.createuser(user.getUserName(), user.getPassword(), connectionPool);
            OrderMapper.insertOrder(newOrder, connectionPool);
            int expectedBalance = 5000;
            OrderMapper.payForOrder(newOrder, newOrder.getTotalPrice(), user.getUserId(), connectionPool);
            User updatedUser = OrderMapper.getUpdatedUser(user.getUserId(), connectionPool);
            assertEquals(expectedBalance, updatedUser.getUserBalance());
        }
        catch (DatabaseException e)
        {
            fail("Database fejl: " + e.getMessage());
        }

    }


    @Test
    void testInputMoney() {

        try
        {
            User user = new User(1, "jon", "1234", 25000, "customer");
            UserMapper.createuser(user.getUserName(), user.getPassword(), connectionPool);

            int expectedBalance = 65000;
            UserMapper.inputMoney(40000, user.getUserId(), connectionPool);
            User updatedUser = OrderMapper.getUpdatedUser(user.getUserId(), connectionPool);
            assertEquals(expectedBalance, updatedUser.getUserBalance());
        }
        catch (DatabaseException e)
        {
            fail("Database fejl: " + e.getMessage());
        }

    }


//    @Test
//    void testCalculatePrice() {
//    }
}
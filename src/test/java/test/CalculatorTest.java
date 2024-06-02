package test;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.services.Calculator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {


    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "carport";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    static void setup() {
        {
            try (Connection connection = connectionPool.getConnection()) {
                try (Statement stmt = connection.createStatement()) {
                    // The test schema is already created, so we only need to delete/create test tables
                    stmt.execute("DROP TABLE IF EXISTS test.users");
                    stmt.execute("DROP TABLE IF EXISTS test.orders");
                    stmt.execute("DROP TABLE IF EXISTS test.customer_info");
                    stmt.execute("DROP TABLE IF EXISTS test.material_item");
                    stmt.execute("DROP TABLE IF EXISTS test.material_variant");
                    stmt.execute("DROP TABLE IF EXISTS test.materials");
                    stmt.execute("DROP SEQUENCE IF EXISTS test.users_user_id_seq CASCADE;");
                    stmt.execute("DROP SEQUENCE IF EXISTS test.customer_info_customer_id_seq CASCADE;");
                    stmt.execute("DROP SEQUENCE IF EXISTS test.material_item_m_item_id_seq CASCADE;");
                    stmt.execute("DROP SEQUENCE IF EXISTS test.material_variant_mv_id_seq CASCADE;");
                    stmt.execute("DROP SEQUENCE IF EXISTS test.materials_m_id_seq CASCADE;");
                    stmt.execute("DROP SEQUENCE IF EXISTS test.orders_order_id_seq CASCADE;");
                    // Create tables as copy of original public schema structure
                    stmt.execute("CREATE TABLE test.users AS (SELECT * from public.users) WITH NO DATA");
                    stmt.execute("CREATE TABLE test.orders AS (SELECT * from public.orders) WITH NO DATA");
                    stmt.execute("CREATE TABLE test.customer_info AS (SELECT * from public.customer_info) WITH NO DATA");
                    stmt.execute("CREATE TABLE test.material_item AS (SELECT * from public.material_item) WITH NO DATA");
                    stmt.execute("CREATE TABLE test.material_variant AS (SELECT * from public.material_variant) WITH NO DATA");
                    stmt.execute("CREATE TABLE test.materials AS (SELECT * from public.materials) WITH NO DATA");
                    // Create sequences for auto generating id's for users and orders
                    stmt.execute("CREATE SEQUENCE test.users_user_id_seq");
                    stmt.execute("ALTER TABLE test.users ALTER COLUMN user_id SET DEFAULT nextval('test.users_user_id_seq')");
                    stmt.execute("CREATE SEQUENCE test.orders_order_id_seq");
                    stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");
                    stmt.execute("CREATE SEQUENCE test.customer_info_customer_id_seq");
                    stmt.execute("ALTER TABLE test.customer_info ALTER COLUMN customer_id SET DEFAULT nextval('test.customer_info_customer_id_seq')");
                    stmt.execute("CREATE SEQUENCE test.material_item_m_item_id_seq");
                    stmt.execute("ALTER TABLE test.material_item ALTER COLUMN m_item_id SET DEFAULT nextval('test.material_item_m_item_id_seq')");
                    stmt.execute("CREATE SEQUENCE test.material_variant_mv_id_seq");
                    stmt.execute("ALTER TABLE test.material_variant ALTER COLUMN mv_id SET DEFAULT nextval('test.material_variant_mv_id_seq')");
                    stmt.execute("CREATE SEQUENCE test.materials_m_id_seq");
                    stmt.execute("ALTER TABLE test.materials ALTER COLUMN m_id SET DEFAULT nextval('test.materials_m_id_seq')");

                    // foreign keys
//                    stmt.execute("ALTER TABLE IF EXISTS public.customer_info ADD CONSTRAINT user_id_customer_info_users_fkey FOREIGN KEY (user_id) REFERENCES public.users (user_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION NOT VALID;");
//                    stmt.execute("ALTER TABLE IF EXISTS public.material_item\n" +
//                            "                    ADD CONSTRAINT material_item_orders_id_fkey FOREIGN KEY (orders_id)\n" +
//                            "                    REFERENCES public.orders (order_id) MATCH SIMPLE\n" +
//                            "                    ON UPDATE NO ACTION\n" +
//                            "                    ON DELETE NO ACTION;");
//                    stmt.execute("ALTER TABLE IF EXISTS public.material_item\n" +
//                            "                    ADD CONSTRAINT material_item_variants_mv_id_fkey FOREIGN KEY (mv_id)\n" +
//                            "                    REFERENCES public.material_variant (mv_id) MATCH SIMPLE\n" +
//                            "                    ON UPDATE NO ACTION\n" +
//                            "                    ON DELETE NO ACTION\n" +
//                            "                    NOT VALID;");
//
//                    stmt.execute("ALTER TABLE IF EXISTS public.material_variant\n" +
//                            "                    ADD CONSTRAINT material_variant_m_id_fkey FOREIGN KEY (m_id)\n" +
//                            "                    REFERENCES public.materials (m_id) MATCH SIMPLE\n" +
//                            "                    ON UPDATE NO ACTION\n" +
//                            "                    ON DELETE NO ACTION;");
//
//                    stmt.execute("ALTER TABLE IF EXISTS public.orders\n" +
//                            "                    ADD CONSTRAINT orders_user_id_fkey FOREIGN KEY (user_id)\n" +
//                            "                    REFERENCES public.users (user_id) MATCH SIMPLE\n" +
//                            "                    ON UPDATE NO ACTION\n" +
//                            "                    ON DELETE NO ACTION;");


                }
            } catch (SQLException e) {
                e.printStackTrace();
                fail("Database connection failed");
            }
        }
    }

//    @BeforeEach
//        // køres før hver eneste test
//    void setUp() {
//        try (Connection connection = connectionPool.getConnection())
//        {
//            try (Statement stmt = connection.createStatement())
//            {
//                // Remove all rows from all tables
//                stmt.execute("DELETE FROM test.materi");
//                stmt.execute("DELETE FROM test.users");
//
//                stmt.execute("INSERT INTO test.users (user_id, user_name, user_password, user_role, user_balance) " +
//                        "VALUES  (1, 'jon', '1234', 'customer', 25000), (2, 'benny', '1234', 'admin', 45000)");
//
//                stmt.execute("INSERT INTO test.orders (order_id, width, length, status, total_price, user_id) " +
//                        "VALUES (1, 600, 780, 1, 20000, 1), (2, 540, 700, 2, 15000, 2), (3, 480, 600, 1, 14000, 1)") ;
//                // Set sequence to continue from the largest member_id
//                stmt.execute("SELECT setval('test.orders_order_id_seq', COALESCE((SELECT MAX(order_id) + 1 FROM test.orders), 1), false)");
//                stmt.execute("SELECT setval('test.users_user_id_seq', COALESCE((SELECT MAX(user_id) + 1 FROM test.users), 1), false)");
//            }
//        }
//        catch (SQLException e)
//        {
//            e.printStackTrace();
//            fail("Database connection failed");
//        }
//    }

    @Test
    void calcPostQuantity()
    {
        Calculator calculator = new Calculator(600, 780, connectionPool);
        assertEquals(6, calculator.calcPostQuantity());
    }


    @Test
    void calcBeamLength() {
        Calculator calculator = new Calculator(600, 780, connectionPool);
        assertEquals(1560, calculator.calcBeamLength());
    }


    @Test
    void calcRaftQuantity() {
        Calculator calculator = new Calculator(600, 780, connectionPool);
        assertEquals(15.109243697478991, calculator.calcRaftQuantity());
    }

//    @Test
//    void calcCarport() throws DatabaseException {
//        User user = new User(0, "Nemo", "1234", 30000, "customer");
//        Order order = new Order(0, 780, 600, 15000, 1, user);
//        Calculator calculator = new Calculator(600, 780, connectionPool);
//
//
//
//
//
//    }







}
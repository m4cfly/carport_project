package test;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.services.Calculator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {


    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "carport";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    static void setup()
    {

    }

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
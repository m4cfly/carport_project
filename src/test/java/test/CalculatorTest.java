package test;

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
        assertEquals(4, calculator.calcBeamLength());
    }


    @Test
    void calcRaftQuantity() {
        Calculator calculator = new Calculator(600, 780, connectionPool);
        assertEquals(5, calculator.calcRaftQuantity());
    }







}
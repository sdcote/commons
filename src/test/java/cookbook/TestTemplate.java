package cookbook;

import org.junit.jupiter.api.*;

public class TestTemplate {

    @BeforeAll
    public static void setUpClass() {
        System.out.println("Before All");
    }

    @AfterAll
    public static void tearDownClass() {
        System.out.println("After All");
    }

    @BeforeEach
    public void setUp() {
        System.out.println("Before Each");
    }

    @AfterEach
    public void tearDown() {
        System.out.println("After Each");
    }

    @Test
    void testTemplate2() {
        System.out.println("testTemplate2");
    }

    @Test
    void testTemplate1() {
        System.out.println("testTemplate1");
    }
}

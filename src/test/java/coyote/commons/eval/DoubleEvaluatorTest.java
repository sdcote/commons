package coyote.commons.eval;


import coyote.commons.eval.DoubleEvaluator.Style;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DoubleEvaluatorTest {

    private static final DoubleEvaluator evaluator = new DoubleEvaluator();


    @Test
    public void excelLike() {
        DoubleEvaluator excelLike = new DoubleEvaluator(DoubleEvaluator.getDefaultParameters(Style.EXCEL));
        assertEquals(4, excelLike.evaluate("-2^2"), 0.001);
    }


    @Test
    public void testResults() {
        assertEquals(-2, evaluator.evaluate("2+-2^2"), 0.001);
        assertEquals(2, evaluator.evaluate("6 / 3"), 0.001);
        assertEquals(Double.POSITIVE_INFINITY, evaluator.evaluate("2/0"), 0.001);
        assertEquals(2, evaluator.evaluate("7 % 2.5"), 0.001);
        assertEquals(-1., evaluator.evaluate("-1"), 0.001);
        assertEquals(1., evaluator.evaluate("1"), 0.001);
        assertEquals(-3, evaluator.evaluate("1+-4"), 0.001);
        assertEquals(2, evaluator.evaluate("3-1"), 0.001);
        assertEquals(-4, evaluator.evaluate("-2^2"), 0.001);
        assertEquals(2, evaluator.evaluate("4^0.5"), 0.001);

        assertEquals(1, evaluator.evaluate("sin ( pi /2)"), 0.001);
        assertEquals(-1, evaluator.evaluate("cos(pi)"), 0.001);
        assertEquals(1, evaluator.evaluate("tan(pi/4)"), 0.001);
        assertEquals(Math.PI, evaluator.evaluate("acos( -1)"), 0.001);
        assertEquals(Math.PI / 2, evaluator.evaluate("asin(1)"), 0.001);
        assertEquals(Math.PI / 4, evaluator.evaluate("atan(1)"), 0.001);

        assertEquals(1, evaluator.evaluate("ln(e)"), 0.001);
        assertEquals(2, evaluator.evaluate("log(100)"), 0.001);
        assertEquals(-1, evaluator.evaluate("min(1,-1)"), 0.001);
        assertEquals(-1, evaluator.evaluate("min(8,3,1,-1)"), 0.001);
        assertEquals(11, evaluator.evaluate("sum(8,3,1,-1)"), 0.001);
        assertEquals(3, evaluator.evaluate("avg(8,3,1,0)"), 0.001);

        assertEquals(3, evaluator.evaluate("abs(-3)"), 0.001);
        assertEquals(3, evaluator.evaluate("ceil(2.45)"), 0.001);
        assertEquals(2, evaluator.evaluate("floor(2.45)"), 0.001);
        assertEquals(2, evaluator.evaluate("round(2.45)"), 0.001);

        double rnd = evaluator.evaluate("random()");
        assertTrue(rnd >= 0 && rnd <= 1.0);

        assertEquals(evaluator.evaluate("tanh(5)"), evaluator.evaluate("sinh(5)/cosh(5)"), 0.001);

        assertEquals(-1, evaluator.evaluate("min(1,min(3+2,2))+-round(4.1)*0.5"), 0.001);
    }


    @Test
    public void testWithVariable() {
        String expression = "x+2";
        StaticVariableSet<Double> variables = new StaticVariableSet<Double>();

        variables.set("x", 1.);
        assertEquals(3, evaluator.evaluate(expression, variables), 0.001);
        variables.set("x", -1.);
        assertEquals(1, evaluator.evaluate(expression, variables), 0.001);
    }


    @Test
    public void test2ValuesFollowing() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evaluator.evaluate("10 5 +");
        });
    }


    @Test
    public void test2ValuesFollowing2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evaluator.evaluate("(10) (5)");
        });
    }


    @Test
    public void test2OperatorsFollowing() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evaluator.evaluate("10**5");
        });
    }


    @Test
    public void testMissingEndValue() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evaluator.evaluate("10*");
        });
    }


    @Test
    public void testNoFunctionArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evaluator.evaluate("sin()");
        });
    }


    @Test
    public void testEmptyFunctionArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evaluator.evaluate("min(,2)");
        });
    }


    //@Test
    public void TestFunctionSeparatorHiddenByBrackets() {
        //Assertions.assertThrows(IllegalArgumentException.class, () -> {
            System.out.println(new DoubleEvaluator().evaluate("max((10,15),20)"));
        //});
    }


    @Test
    public void testSomethingBetweenFunctionAndOpenBracket() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evaluator.evaluate("sin3(45)");
        });
    }


    @Test
    public void testMissingArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evaluator.evaluate("min(1,)");
        });
    }


    @Test
    public void testInvalidArgumentACos() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evaluator.evaluate("acos(2)");
        });
    }


    @Test
    public void testInvalidArgumentASin() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evaluator.evaluate("asin(2)");
        });
    }


    @Test
    public void testOnlyCloseBracket() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evaluator.evaluate(")");
        });
    }


    @Test
    public void testDSuffixInLiteral() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evaluator.evaluate("3d+4");
        });
    }


    @Test
    public void testStartWithFunctionSeparator() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evaluator.evaluate(",3");
        });
    }
}

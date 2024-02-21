package algo.divide_and_conquer;

import old.algo.divide_and_conquer.FastExponentiation;
import org.junit.Assert;
import org.junit.Test;

public class FastExponentiationTest {

    @Test
    public void fastExponentiation_giveCorrectValue() {

        Assert.assertTrue(8 * 8 * 8 * 8 * 8 == FastExponentiation.calculateFastExponentiation(8, 5));

        long t1 = System.currentTimeMillis();
        double a = 8 * 8 * 8 * 8 * 8;
        long t2 = System.currentTimeMillis();
        System.out.println("t2 - t1 " + (t2 - t1));
        long t3 = System.currentTimeMillis();
        double c = FastExponentiation.calculateFastExponentiation(8, 5);
        long t4 = System.currentTimeMillis();
        System.out.println("t4 - t3 " + (t4 - t3));
        System.out.println("a - c " + a + " " + c);
        //note that the function's execution time right here is a little bit higher but with bigger value of n, it will make diff
    }
}

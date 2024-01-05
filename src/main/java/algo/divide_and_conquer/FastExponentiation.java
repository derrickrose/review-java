package algo.divide_and_conquer;

public class FastExponentiation {


    public static final int calculateFastExponentiation(int var, int pow) {
        if (pow == 0) {
            return 1;
        }
        else if (pow == 1) {
            return var;
        }
        else if (pow % 2 != 0) {
            return var * calculateFastExponentiation(var, pow - 1);
        } else {
            int value = calculateFastExponentiation(var, pow / 2);
            return value * value;
        }
    }
}

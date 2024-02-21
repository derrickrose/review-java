package old.algo.zero_of_function;

import java.util.function.Function;

public class ZeroOfFunction {

    public static final double[] doSearchRange(Function<Double, Double> func, double min, double max, double err) {
        if (max < min) {
            return null;
        }
        double middle = (max + min) / 2;

        if (func.apply(middle) == 0) {
            return new double[]{middle, middle + err};
        }

        if (func.apply(min) * func.apply(max) < 0 && (max - min) <= err) {
            return new double[]{min, max};
        }

        if (func.apply(min) * func.apply(middle) < 0) {
            return doSearchRange(func, min, middle, err);
        } else {
            return doSearchRange(func, middle, max, err);
        }
    }
}

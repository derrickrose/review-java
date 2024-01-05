package java_8_features.lambda;

import java.util.function.Function;

public class UseOfLambda {

    public static ITransportation transportation =
            new ITransportation() {
                @Override
                public void calculatePrice(int distance) {
                    System.out.println("The price is " + 2 * distance);
                }
            };

    public static ITransportation transportationUsingLambda =
            (da) -> System.out.println("{using lambda expressions} The price is " + 2 * da);

    public static ITransportation getTransportationUsingLambda2 =
            (distance) -> {
                System.out.println("{using lambda expressions with accolad } The price is " + 2 * distance);
            };

    public static ISum sum = (x, y) -> x + y;

    Function<Integer, Void> iTransportationUsingLambda =
            (da) -> {
                System.out.println("{using lambda expressions} The price is " + (2 * da.intValue()));
                return null;
            };

    Function<String, Void> print = (str) -> {
        for (char a : str.toCharArray()) {
            System.out.println(a);
        }
        return null;
    };
}

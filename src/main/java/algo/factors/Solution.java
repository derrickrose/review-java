package algo.factors;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;


class Result {

    /*
     * Complete the 'maxSubsetSum' function below.
     *
     * The function is expected to return a LONG_INTEGER_ARRAY.
     * The function accepts INTEGER_ARRAY k as parameter.
     */

    public static List<Long> maxSubsetSum(List<Integer> integers) {
        System.out.println(integers);
        List<Long> sums = new ArrayList<>();
        for (Integer currentInteger : integers) {
            sums.add(getSumOfFactors(currentInteger));
        }
        return sums;
    }
    //1 2 3 4 6 12
    public static Long getSumOfFactors(Integer current) {
        Long sumOfFactors = 0L; // ny tena atao zany ny sum an le facteur reo
        if (current == 1L) {
            return 1L;
        } else {
            sumOfFactors += 1;
            sumOfFactors += current;
            for (int index = 2; index < current; index++) {
                if (current % index == 0) { //refa reste de la division no zero de facteur iz zay
                    sumOfFactors += index;
                }
            }




            return sumOfFactors;
        }
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        /*
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int kCount = Integer.parseInt(bufferedReader.readLine().trim());
        */
        int kCount = Integer.parseInt("12");

        List<Integer> k = IntStream.range(0, kCount).mapToObj(i -> {
            return String.valueOf(i);
            /*
            try {
                //return bufferedReader.readLine().replaceAll("\\s+$", "");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }*/
        })
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(toList());

        List<Long> result = Result.maxSubsetSum(k);
        /*
        result.stream().forEach(
                x -> System.out.println(String.valueOf(x))
        );
*/
        /*
        bufferedWriter.write(
                result.stream()
                        .map(Object::toString)
                        .collect(joining("\n"))
                        + "\n"
        );

        bufferedReader.close();
        bufferedWriter.close();

         */
    }
}

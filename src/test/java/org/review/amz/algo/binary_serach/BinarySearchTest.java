package org.review.amz.algo.binary_serach;

import org.junit.Test;

import java.util.Arrays;

public class BinarySearchTest {


    @Test
    public void binarySearch() {
        System.out.println("----------Binary Search");
        String[] dictionary = {"nini", "mmm", "mamamamam", "mlmlm", "mkml", "nono", "pupu", "kiki", "mami", "moi", "toto", "popito", "shsh", "oo"};
        Arrays.sort(dictionary);
        System.out.println(Arrays.asList(dictionary).toString());
        int position = BinarySearch.binarySearch(dictionary, "oo", 0, dictionary.length - 1);
        System.out.println("position " + position);
    }
}

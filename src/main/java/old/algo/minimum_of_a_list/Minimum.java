package old.algo.minimum_of_a_list;

import java.util.List;

public class Minimum {

    //complexity big o of n O(n) since for each element inside the list,
    // we will compare with the current minimum
    //complexité linéaire
    public static Integer getMinimum(List<Integer> integers) {
        if (integers == null || integers.size() == 0) {
            return null;
        }
        Integer minimum = integers.get(0);
        for (int index = 1; index < integers.size(); index++) {
            if (integers.get(index).intValue() < minimum.intValue()) {
                minimum = integers.get(index);
            }
        }
        return minimum;
    }
}

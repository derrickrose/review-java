package algo.binary_search;

public class BinarySearch {

    public static final String[] subArray(String[] strings, int lowerBound, int upperBound) throws Exception {
        if (strings.length == 0) {
            throw new Exception("Cannot cut an empty array.");
        }
        if (upperBound >= strings.length) {
            throw new Exception("UpperBound |" + upperBound + "| should be smaller than array length |" + strings.length + "|");
        }
        int length = upperBound - lowerBound + 1;
        String[] newStrings = new String[length];
        for (int index = 0; index < length; index++) {
            newStrings[index] = strings[lowerBound + index];
        }
        return newStrings;
    }

    //before using this, the array strings should first be ordered
    public static final int binarySearch(String[] strings, String toSearch, int min, int max) {
        if (max < min) {
            return -1;
        }

        int middle = (max + min) / 2;

        if (strings[middle].equals(toSearch)) {
            return middle;
        }

        if (strings[middle].compareTo(toSearch) < 0) {
            return binarySearch(strings, toSearch, middle, max);
        } else {
            return binarySearch(strings, toSearch, min, middle);
        }

    }
}

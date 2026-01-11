# Chapter 5: Working with Arrays and Collections - PART 1: Arrays

[← Previous Chapter](CHAPTER-04.md) | [Back to Main Guide](README.md) | [Next: Part 2 - List →](CHAPTER-05-PART2-LIST.md)

---

## Chapter Overview

| Attribute | Details |
|-----------|---------|
| **Exam Objectives Covered** | Working with Arrays and Collections |
| **Java Versions** | 1.0, 2, 5, 8, 9, 10, 16 |
| **Code Examples** | 80+ across all parts |
| **Study Time** | 12-15 hours |
| **Exam Weight** | ~15% (HEAVILY TESTED) |
| **Difficulty** | ⭐⭐⭐⭐ Very Hard |

**Official 1Z0-830 Exam Objectives Covered:**
- Create and use one-dimensional and multi-dimensional arrays
- Use Java collection framework including List, Set, Map, Queue, and Deque interfaces
- Implement sorting and searching operations
- Use wrapper classes, autoboxing, and unboxing

---

## 1. Arrays

**Exam Objective:** Create and use one-dimensional and multi-dimensional arrays

### What are Arrays?
Fixed-size, ordered collection of elements of same type. Can be primitive or object arrays. Stored in contiguous memory locations for optimal cache performance.

### When to Use
- Fixed size known at creation time
- Need fast index-based access O(1)
- Performance critical (faster than collections)
- Multi-dimensional data (matrices, grids, images)
- Interfacing with APIs that require arrays
- Memory constraints (less overhead than collections)

### Why Use Arrays
- **Fast access:** O(1) by index
- **Memory efficient:** No overhead like collections
- **Type-safe:** Compile-time type checking
- **Built-in language support:** No import needed
- **Cache-friendly:** Contiguous memory layout
- **Primitive support:** Can store primitives directly (no boxing)

### Performance Characteristics

| Operation | Time Complexity | Space Complexity | Notes |
|-----------|----------------|------------------|-------|
| **Access by index** | O(1) | O(1) | Direct memory calculation: base + (index × size) |
| **Search (unsorted)** | O(n) | O(1) | Linear scan required |
| **Search (sorted, binary)** | O(log n) | O(1) | Arrays.binarySearch() |
| **Sort** | O(n log n) | O(log n) | Dual-pivot quicksort (primitives), TimSort (objects) |
| **Insert at end** | N/A | - | Fixed size - can't insert |
| **Insert at position** | O(n) | O(n) | Need to create new array and copy |
| **Delete** | O(n) | O(n) | Need to create new array and copy |
| **Copy entire array** | O(n) | O(n) | System.arraycopy() or clone() |
| **Fill** | O(n) | O(1) | Arrays.fill() |

### Real-World Example
```java
// Image processing - pixel matrix
public class ImageProcessor {
    private int[][] pixels; // [height][width] - O(h*w) space
    private int width;
    private int height;
    
    public ImageProcessor(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new int[height][width]; // O(h*w) allocation
    }
    
    // Access pixel - O(1) time
    public int getPixel(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
        return pixels[row][col];
    }
    
    // Set pixel - O(1) time
    public void setPixel(int row, int col, int rgb) {
        pixels[row][col] = rgb;
    }
    
    // Apply grayscale filter - O(h*w) time, O(1) space
    public void toGrayscale() {
        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[row].length; col++) {
                int rgb = pixels[row][col];
                // Extract RGB components
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                // Calculate grayscale value
                int gray = (r + g + b) / 3;
                // Set all components to gray
                pixels[row][col] = (gray << 16) | (gray << 8) | gray;
            }
        }
    }
    
    // Rotate 90 degrees clockwise - O(h*w) time, O(h*w) space
    public int[][] rotate90() {
        int[][] rotated = new int[width][height];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                rotated[col][height - 1 - row] = pixels[row][col];
            }
        }
        return rotated;
    }
    
    // Apply blur filter (3x3 kernel) - O(h*w) time, O(h*w) space
    public int[][] blur() {
        int[][] result = new int[height][width];
        for (int row = 1; row < height - 1; row++) {
            for (int col = 1; col < width - 1; col++) {
                int sum = 0;
                // Sum 3x3 neighborhood
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        sum += pixels[row + dr][col + dc];
                    }
                }
                result[row][col] = sum / 9; // Average
            }
        }
        return result;
    }
}

// Monthly sales tracking - fixed 12 months
public class SalesTracker {
    private double[] monthlySales = new double[12]; // O(12) = O(1) space
    private static final String[] MONTH_NAMES = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };
    
    // Record sales - O(1) time
    public void recordSales(int month, double amount) {
        if (month < 0 || month >= 12) {
            throw new IllegalArgumentException("Invalid month: " + month);
        }
        monthlySales[month] = amount;
    }
    
    // Calculate average - O(n) time, O(1) space
    public double averageSales() {
        double sum = 0;
        for (double sales : monthlySales) {
            sum += sales;
        }
        return sum / monthlySales.length;
    }
    
    // Find best month - O(n) time, O(1) space
    public int bestMonth() {
        int bestIdx = 0;
        for (int i = 1; i < monthlySales.length; i++) {
            if (monthlySales[i] > monthlySales[bestIdx]) {
                bestIdx = i;
            }
        }
        return bestIdx;
    }
    
    // Calculate year-over-year growth - O(n) time, O(1) space
    public double[] calculateGrowth(double[] previousYear) {
        double[] growth = new double[12];
        for (int i = 0; i < 12; i++) {
            if (previousYear[i] == 0) {
                growth[i] = monthlySales[i] > 0 ? 100.0 : 0.0;
            } else {
                growth[i] = ((monthlySales[i] - previousYear[i]) / previousYear[i]) * 100;
            }
        }
        return growth;
    }
    
    // Get quarterly totals - O(n) time, O(1) space
    public double[] quarterlyTotals() {
        double[] quarters = new double[4];
        for (int q = 0; q < 4; q++) {
            for (int m = 0; m < 3; m++) {
                quarters[q] += monthlySales[q * 3 + m];
            }
        }
        return quarters;
    }
}
```
**Why:** Images need O(1) pixel access and contiguous memory for cache efficiency. Sales tracking uses fixed-size array for known 12 months - no resizing overhead.

### One-Dimensional Arrays

#### Declaration and Initialization
```java
// Declaration only - O(1) time, O(1) space (just reference)
int[] numbers;
String[] names;
double[] prices;

// Declaration with initialization - O(n) time, O(n) space
int[] arr1 = new int[5];              // [0, 0, 0, 0, 0]
double[] arr2 = new double[3];        // [0.0, 0.0, 0.0]
String[] arr3 = new String[2];        // [null, null]
boolean[] arr4 = new boolean[4];      // [false, false, false, false]
char[] arr5 = new char[3];            // ['\u0000', '\u0000', '\u0000']

// Array initializer syntax - O(n) time, O(n) space
int[] arr6 = {1, 2, 3, 4, 5};
String[] arr7 = {"Alice", "Bob", "Charlie"};
double[] arr8 = {1.5, 2.5, 3.5};

// Explicit type with initializer
double[] arr9 = new double[] {1.5, 2.5, 3.5};

// Anonymous array (useful for method calls)
processArray(new int[] {10, 20, 30});
System.out.println(Arrays.toString(new String[] {"A", "B"}));

// Array of objects - O(n) space for references + object space
Person[] people = new Person[3];
people[0] = new Person("Alice", 25);
people[1] = new Person("Bob", 30);
people[2] = new Person("Charlie", 35);

// Array of arrays (each element is array reference)
int[][] arrays = new int[3][];
arrays[0] = new int[] {1, 2};
arrays[1] = new int[] {3, 4, 5};
```

#### Accessing and Modifying - All O(1)
```java
int[] numbers = {10, 20, 30, 40, 50};

// Access - O(1)
int first = numbers[0];                    // 10
int second = numbers[1];                   // 20
int last = numbers[numbers.length - 1];    // 50
int secondLast = numbers[numbers.length - 2]; // 40

// Modify - O(1)
numbers[0] = 15;                           // [15, 20, 30, 40, 50]
numbers[2] = numbers[2] * 2;               // [15, 20, 60, 40, 50]
numbers[3] += 10;                          // [15, 20, 60, 50, 50]

// Length property (NOT method!) - O(1)
int size = numbers.length;                 // 5

// Bounds checking
// int invalid = numbers[5];               // ❌ ArrayIndexOutOfBoundsException
// int negative = numbers[-1];             // ❌ ArrayIndexOutOfBoundsException

// Common idiom for last element
int lastElement = numbers[numbers.length - 1];

// Swap elements - O(1)
int temp = numbers[0];
numbers[0] = numbers[4];
numbers[4] = temp;
```

### Multi-Dimensional Arrays

#### 2D Arrays (Matrices)
```java
// Declaration and initialization - O(rows × cols) time and space
int[][] matrix1 = new int[3][4];  // 3 rows, 4 columns, all zeros

// Array initializer - O(rows × cols)
int[][] matrix2 = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};

// Initialize with specific values - O(rows × cols)
int[][] matrix3 = new int[3][3];
for (int i = 0; i < matrix3.length; i++) {
    for (int j = 0; j < matrix3[i].length; j++) {
        matrix3[i][j] = i * 3 + j + 1;
    }
}

// Access - O(1)
int value = matrix2[1][2];        // 6 (row 1, column 2)
int topLeft = matrix2[0][0];      // 1
int bottomRight = matrix2[2][2];  // 9

// Modify - O(1)
matrix2[0][0] = 10;
matrix2[1][1] = 50;

// Dimensions
int rows = matrix2.length;        // 3
int cols = matrix2[0].length;     // 3

// Create identity matrix - O(n²) time
int[][] identity = new int[3][3];
for (int i = 0; i < 3; i++) {
    identity[i][i] = 1;
}
// [[1,0,0], [0,1,0], [0,0,1]]

// Matrix transpose - O(rows × cols) time and space
int[][] transpose = new int[cols][rows];
for (int i = 0; i < rows; i++) {
    for (int j = 0; j < cols; j++) {
        transpose[j][i] = matrix2[i][j];
    }
}
```

#### Jagged Arrays (Rows with different lengths)
```java
// Jagged array - each row can have different length
int[][] jagged = new int[3][];
jagged[0] = new int[2];           // Row 0 has 2 elements
jagged[1] = new int[4];           // Row 1 has 4 elements
jagged[2] = new int[3];           // Row 2 has 3 elements

// Total space: 2 + 4 + 3 = 9 elements (not 3×4 = 12)

// Initialize with values
int[][] jagged2 = {
    {1, 2},
    {3, 4, 5, 6},
    {7, 8, 9}
};

// Access - must check individual row lengths
int val = jagged2[1][3];          // 6
// int invalid = jagged2[0][3];   // ❌ ArrayIndexOutOfBoundsException

// Iterate through jagged array - O(total elements)
for (int i = 0; i < jagged2.length; i++) {
    for (int j = 0; j < jagged2[i].length; j++) {
        System.out.print(jagged2[i][j] + " ");
    }
    System.out.println();
}

// Use case: Triangle representation
int[][] pascalTriangle = new int[5][];
for (int i = 0; i < 5; i++) {
    pascalTriangle[i] = new int[i + 1];
    pascalTriangle[i][0] = 1;
    pascalTriangle[i][i] = 1;
    for (int j = 1; j < i; j++) {
        pascalTriangle[i][j] = pascalTriangle[i-1][j-1] + pascalTriangle[i-1][j];
    }
}
// [[1], [1,1], [1,2,1], [1,3,3,1], [1,4,6,4,1]]
```

#### 3D Arrays and Higher
```java
// 3D array - O(x × y × z) space
int[][][] cube = new int[2][3][4]; // 2 layers, 3 rows, 4 columns

// Initialize with values
int[][][] cube2 = {
    {  // Layer 0
        {1, 2, 3},
        {4, 5, 6}
    },
    {  // Layer 1
        {7, 8, 9},
        {10, 11, 12}
    }
};

// Access - O(1)
int value = cube2[1][0][2];       // 9 (layer 1, row 0, col 2)

// Dimensions
int layers = cube.length;         // 2
int rows = cube[0].length;        // 3
int cols = cube[0][0].length;     // 4

// Total elements
int totalElements = layers * rows * cols; // 24

// 4D array (rarely used)
int[][][][] hyperCube = new int[2][3][4][5];
```

### Arrays Class Methods (java.util.Arrays)

**Import required:** `import java.util.Arrays;`

#### Sorting

##### sort() - O(n log n) time, O(log n) space
```java
int[] arr = {5, 2, 8, 1, 9, 3};

// Sort entire array - Dual-Pivot Quicksort for primitives
Arrays.sort(arr);                 // [1, 2, 3, 5, 8, 9]

// Sort range [fromIndex, toIndex) - not inclusive of toIndex
int[] arr2 = {5, 2, 8, 1, 9, 3};
Arrays.sort(arr2, 1, 4);          // Sorts indices 1, 2, 3
// Result: [5, 1, 2, 8, 9, 3]

// Sort objects - TimSort (stable, O(n log n))
String[] names = {"Charlie", "Alice", "Bob", "David"};
Arrays.sort(names);               // [Alice, Bob, Charlie, David]

// Custom comparator - sort by length
Arrays.sort(names, (a, b) -> Integer.compare(a.length(), b.length()));
// [Bob, Alice, David, Charlie]

// Sort in reverse order (objects only)
Arrays.sort(names, Collections.reverseOrder());
// [David, Charlie, Bob, Alice]

// Sort with multiple criteria
Arrays.sort(names, (a, b) -> {
    int lenCompare = Integer.compare(a.length(), b.length());
    return lenCompare != 0 ? lenCompare : a.compareTo(b);
});

// Comparator.comparing (Java 8+)
Arrays.sort(names, Comparator.comparing(String::length)
                              .thenComparing(Comparator.naturalOrder()));
```

##### parallelSort() - O(n log n) time, O(n) space
```java
// Parallel sort - uses Fork/Join framework, faster on multi-core for large arrays
int[] large = new int[1000000];
// Fill with random values
for (int i = 0; i < large.length; i++) {
    large[i] = (int)(Math.random() * 1000000);
}

// Parallel sort - automatically uses multiple threads
Arrays.parallelSort(large);       // Faster than sort() for large arrays

// Parallel sort with range
Arrays.parallelSort(large, 0, large.length / 2);

// When to use:
// - Array size > ~8000 elements
// - Multi-core processor available
// - Not concerned about thread overhead
```

#### Binary Search - O(log n) time, O(1) space

**CRITICAL: Array MUST be sorted first!**

```java
int[] sorted = {1, 3, 5, 7, 9, 11, 13, 15};

// Search for existing element
int index = Arrays.binarySearch(sorted, 7);   // Returns 3

// Search for non-existing element
int index2 = Arrays.binarySearch(sorted, 6);  // Returns -4
// Formula: -(insertion point) - 1
// 6 would be inserted at index 3, so: -(3) - 1 = -4

int index3 = Arrays.binarySearch(sorted, 0);  // Returns -1 (before first)
int index4 = Arrays.binarySearch(sorted, 20); // Returns -9 (after last)

// Get insertion point from negative result
if (index2 < 0) {
    int insertionPoint = -index2 - 1;  // 3
}

// Search in range [fromIndex, toIndex)
int index5 = Arrays.binarySearch(sorted, 2, 5, 7); // Searches indices 2-4
// Result: 3

// IMPORTANT: Undefined behavior if array not sorted!
int[] unsorted = {5, 2, 8, 1, 9};
Arrays.binarySearch(unsorted, 5); // ❌ Undefined! Result unpredictable

// Must sort first
Arrays.sort(unsorted);
Arrays.binarySearch(unsorted, 5); // ✅ Now works correctly

// Search objects with custom comparator
String[] names = {"Alice", "Bob", "Charlie", "David"};
Arrays.sort(names);
int idx = Arrays.binarySearch(names, "Charlie"); // 2

// With custom order
Arrays.sort(names, Collections.reverseOrder());
idx = Arrays.binarySearch(names, "Charlie", Collections.reverseOrder());
```

#### Comparing Arrays - O(n) time, O(1) space

```java
int[] arr1 = {1, 2, 3};
int[] arr2 = {1, 2, 3};
int[] arr3 = {1, 2, 4};
int[] arr4 = {1, 2};

// equals() - checks content equality
boolean equal1 = Arrays.equals(arr1, arr2);  // true
boolean equal2 = Arrays.equals(arr1, arr3);  // false
boolean equal3 = Arrays.equals(arr1, arr4);  // false (different lengths)

// WARNING: == checks reference equality, NOT content
boolean refEqual = (arr1 == arr2);           // false (different objects)
boolean same = (arr1 == arr1);               // true (same reference)

// deepEquals() - for multi-dimensional arrays
int[][] matrix1 = {{1, 2}, {3, 4}};
int[][] matrix2 = {{1, 2}, {3, 4}};
int[][] matrix3 = {{1, 2}, {3, 5}};

Arrays.equals(matrix1, matrix2);      // false! (compares references of rows)
Arrays.deepEquals(matrix1, matrix2);  // true (compares content recursively)
Arrays.deepEquals(matrix1, matrix3);  // false

// compare() - lexicographical comparison (Java 9+)
int result1 = Arrays.compare(arr1, arr2);  // 0 (equal)
int result2 = Arrays.compare(arr1, arr3);  // -1 (arr1 < arr3)
int result3 = Arrays.compare(arr3, arr1);  // 1 (arr3 > arr1)

// compareUnsigned() - for unsigned comparison
int[] unsigned1 = {-1, 0};
int[] unsigned2 = {1, 0};
Arrays.compare(unsigned1, unsigned2);         // -1 (signed)
Arrays.compareUnsigned(unsigned1, unsigned2); // 1 (unsigned: -1 is large)

// mismatch() - finds first differing index (Java 9+)
int idx = Arrays.mismatch(arr1, arr2);  // -1 (no mismatch)
int idx2 = Arrays.mismatch(arr1, arr3); // 2 (differ at index 2)
int idx3 = Arrays.mismatch(arr1, arr4); // 2 (different lengths, arr4 shorter)
```

#### Copying Arrays - O(n) time, O(n) space

```java
int[] original = {1, 2, 3, 4, 5};

// copyOf() - copy entire array or resize
int[] copy1 = Arrays.copyOf(original, original.length); // [1,2,3,4,5]
int[] copy2 = Arrays.copyOf(original, 3);               // [1,2,3] (truncate)
int[] copy3 = Arrays.copyOf(original, 7);               // [1,2,3,4,5,0,0] (pad)

// copyOfRange() - copy subset [from, to) - not inclusive of to
int[] copy4 = Arrays.copyOfRange(original, 1, 4);       // [2,3,4]
int[] copy5 = Arrays.copyOfRange(original, 2, 7);       // [3,4,5,0,0] (pad)
int[] copy6 = Arrays.copyOfRange(original, 0, 2);       // [1,2]

// System.arraycopy() - most efficient, manually specify everything
// arraycopy(src, srcPos, dest, destPos, length)
int[] dest = new int[5];
System.arraycopy(original, 0, dest, 0, original.length);
// dest is now [1,2,3,4,5]

// Copy part of array
int[] dest2 = new int[10];
System.arraycopy(original, 1, dest2, 3, 3);
// dest2: [0,0,0,2,3,4,0,0,0,0]

// Clone - creates shallow copy
int[] clone = original.clone();       // [1,2,3,4,5]
clone[0] = 99;                        // Doesn't affect original

// For 2D arrays - BEWARE of shallow copy!
int[][] matrix = {{1, 2}, {3, 4}};
int[][] shallowCopy = matrix.clone();  // Only copies row references!
shallowCopy[0][0] = 99;                // MODIFIES original matrix!

// Deep copy for 2D arrays
int[][] deepCopy = new int[matrix.length][];
for (int i = 0; i < matrix.length; i++) {
    deepCopy[i] = matrix[i].clone();
}
deepCopy[0][0] = 99;                   // Doesn't affect original

// Or using streams (Java 8+)
int[][] deepCopy2 = Arrays.stream(matrix)
                          .map(int[]::clone)
                          .toArray(int[][]::new);
```

#### Filling Arrays - O(n) time, O(1) space

```java
int[] arr = new int[5];

// Fill entire array
Arrays.fill(arr, 7);                         // [7,7,7,7,7]

// Fill range [fromIndex, toIndex) - not inclusive of toIndex
Arrays.fill(arr, 1, 4, 9);                   // [7,9,9,9,7]

// Fill with different values
Arrays.fill(arr, 0, 2, 1);                   // [1,1,9,9,7]
Arrays.fill(arr, 3, 5, 5);                   // [1,1,9,5,5]

// Common use: initialize to non-default value
int[] prices = new int[100];
Arrays.fill(prices, -1);                     // All -1

// Fill 2D array
int[][] matrix = new int[3][4];
for (int[] row : matrix) {
    Arrays.fill(row, 5);                     // All elements = 5
}

// Fill different rows with different values
for (int i = 0; i < matrix.length; i++) {
    Arrays.fill(matrix[i], i + 1);
}
// [[1,1,1,1], [2,2,2,2], [3,3,3,3]]

// Fill boolean array
boolean[] flags = new boolean[10];
Arrays.fill(flags, true);                    // All true
```

#### Converting to String - O(n) time, O(n) space

```java
int[] arr = {1, 2, 3, 4, 5};

// toString() - creates string representation
String str = Arrays.toString(arr);           // "[1, 2, 3, 4, 5]"

// Useful for debugging
System.out.println(Arrays.toString(arr));

// Different types
String[] names = {"Alice", "Bob"};
System.out.println(Arrays.toString(names));  // "[Alice, Bob]"

double[] prices = {1.5, 2.5};
System.out.println(Arrays.toString(prices)); // "[1.5, 2.5]"

// For 2D arrays - toString() shows hash codes!
int[][] matrix = {{1, 2}, {3, 4}};
String bad = Arrays.toString(matrix);        // "[[I@hashcode, [I@hashcode]"

// Use deepToString() for multi-dimensional arrays
String good = Arrays.deepToString(matrix);   // "[[1, 2], [3, 4]]"

// 3D array
int[][][] cube = {{{1, 2}, {3, 4}}, {{5, 6}, {7, 8}}};
System.out.println(Arrays.deepToString(cube));
// "[[[1, 2], [3, 4]], [[5, 6], [7, 8]]]"
```

#### Converting to List - O(n) time, O(1) space (backed by array)

```java
// Arrays.asList() - creates FIXED-SIZE list backed by original array
String[] strArray = {"A", "B", "C"};
List<String> list = Arrays.asList(strArray);

// Size
System.out.println(list.size());     // 3

// Can READ
System.out.println(list.get(0));     // "A"

// Can MODIFY elements
list.set(0, "X");                    // ✅ OK
System.out.println(strArray[0]);     // "X" (array also modified!)

// CANNOT add/remove (UnsupportedOperationException)
try {
    list.add("D");                   // ❌ UnsupportedOperationException
} catch (UnsupportedOperationException e) {
    System.out.println("Cannot add to fixed-size list");
}

try {
    list.remove(0);                  // ❌ UnsupportedOperationException
} catch (UnsupportedOperationException e) {
    System.out.println("Cannot remove from fixed-size list");
}

// To get MUTABLE list
List<String> mutable = new ArrayList<>(Arrays.asList(strArray));
mutable.add("D");                    // ✅ OK
mutable.remove(0);                   // ✅ OK

// IMPORTANT: Doesn't work with primitive arrays!
int[] intArray = {1, 2, 3};
List<int[]> wrong = Arrays.asList(intArray); // Creates List<int[]>, not List<Integer>!
System.out.println(wrong.size());    // 1 (one element: the array itself)

// Correct way for primitives: use streams (Java 8+)
List<Integer> intList = Arrays.stream(intArray)
                              .boxed()
                              .collect(Collectors.toList());
System.out.println(intList.size());  // 3

// Or manually box
Integer[] boxed = {1, 2, 3};
List<Integer> intList2 = Arrays.asList(boxed); // ✅ Works

// Quick immutable list (Java 9+)
List<String> immutable = List.of("A", "B", "C");
// immutable.add("D");               // ❌ UnsupportedOperationException
// immutable.set(0, "X");            // ❌ UnsupportedOperationException
```

#### Varargs and Arrays - O(n) time, O(n) space

**Varargs (variable arguments)** allow methods to accept variable number of arguments, internally converted to arrays.

```java
// Method with varargs - internally creates array
public static int sum(int... numbers) {  // int... is varargs
    int total = 0;
    for (int num : numbers) {            // numbers is actually int[]
        total += num;
    }
    return total;
}

// Calling with different numbers of arguments
int result1 = sum(1, 2, 3);              // 6
int result2 = sum(10, 20);               // 30
int result3 = sum(5);                    // 5
int result4 = sum();                     // 0 (empty array)

// Can pass array directly
int[] arr = {1, 2, 3, 4, 5};
int result5 = sum(arr);                  // 15

// Varargs must be LAST parameter
public static void print(String prefix, int... numbers) {
    System.out.print(prefix + ": ");
    for (int num : numbers) {
        System.out.print(num + " ");
    }
}

print("Numbers", 1, 2, 3);               // Numbers: 1 2 3

// CANNOT have multiple varargs
// public void invalid(int... a, String... b) { } // ❌ Compilation error

// Varargs with generics
public static <T> List<T> listOf(T... elements) {
    return Arrays.asList(elements);
}

List<String> names = listOf("Alice", "Bob", "Charlie");
List<Integer> nums = listOf(1, 2, 3, 4, 5);

// Common use: String.format, Collections.addAll, Arrays.asList
String formatted = String.format("%s is %d years old", "Alice", 25);
List<String> list = new ArrayList<>();
Collections.addAll(list, "A", "B", "C"); // Varargs

// Varargs and overloading - specific params chosen over varargs
public static void process(int a) {
    System.out.println("Single int: " + a);
}

public static void process(int... numbers) {
    System.out.println("Varargs: " + Arrays.toString(numbers));
}

process(5);           // Calls process(int a) - more specific
process(5, 10);       // Calls process(int... numbers)
process(new int[]{5});// Calls process(int... numbers)

// Varargs ambiguity warning
public static void ambiguous(String... args) { }
public static void ambiguous(Object... args) { }
// ambiguous("A");    // ❌ Compilation error: ambiguous

// Null and empty varargs
public static void testNull(String... args) {
    if (args == null) {
        System.out.println("null array");
    } else if (args.length == 0) {
        System.out.println("empty array");
    } else {
        System.out.println("has elements: " + args.length);
    }
}

testNull();           // empty array (not null!)
testNull((String[])null); // null array
testNull(null);       // null array (single null element in some contexts)
testNull("A", "B");   // has elements: 2

// Varargs performance consideration - creates array on each call
// For frequently called methods, consider using overloads
public void optimized(String s) { /* single element */ }
public void optimized(String s1, String s2) { /* two elements */ }
public void optimized(String... strings) { /* three or more */ }
```

**Exam Tips on Varargs:**
- Varargs parameter must be LAST in parameter list
- Only ONE varargs parameter allowed per method
- Internally converted to array
- Can pass 0 or more arguments
- Can pass array directly
- More specific method signatures chosen over varargs
- Creates new array on each call (overhead)
- Empty call creates empty array, not null

### Array Iteration Patterns

#### 1. Standard for loop - O(n) time
```java
int[] numbers = {10, 20, 30, 40, 50};

// Forward iteration
for (int i = 0; i < numbers.length; i++) {
    System.out.println(numbers[i]);
    // Can modify: numbers[i] = numbers[i] * 2;
}

// Backward iteration
for (int i = numbers.length - 1; i >= 0; i--) {
    System.out.println(numbers[i]);   // 50, 40, 30, 20, 10
}

// Skip elements (every 2nd)
for (int i = 0; i < numbers.length; i += 2) {
    System.out.println(numbers[i]);   // 10, 30, 50
}

// Half the array
for (int i = 0; i < numbers.length / 2; i++) {
    System.out.println(numbers[i]);
}
```

#### 2. Enhanced for loop - O(n) time
```java
int[] numbers = {10, 20, 30, 40, 50};

// Cleaner syntax, READ-ONLY
for (int num : numbers) {
    System.out.println(num);
    // num = num * 2;  // ❌ Doesn't modify array! Only modifies local variable
}

// Works with any array type
String[] names = {"Alice", "Bob", "Charlie"};
for (String name : names) {
    System.out.println(name);
}

// Can break early
for (int num : numbers) {
    if (num > 30) break;
    System.out.println(num);          // 10, 20, 30
}

// With index (need traditional for)
for (int i = 0; i < numbers.length; i++) {
    System.out.println(i + ": " + numbers[i]);
}
```

#### 3. Two-dimensional array iteration - O(rows × cols) time
```java
int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};

// Nested for loops with indices
for (int row = 0; row < matrix.length; row++) {
    for (int col = 0; col < matrix[row].length; col++) {
        System.out.print(matrix[row][col] + " ");
    }
    System.out.println();
}

// Enhanced for loop
for (int[] row : matrix) {
    for (int val : row) {
        System.out.print(val + " ");
    }
    System.out.println();
}

// Print each row as array
for (int[] row : matrix) {
    System.out.println(Arrays.toString(row));
}
// [1, 2, 3]
// [4, 5, 6]
// [7, 8, 9]

// Column-wise iteration
for (int col = 0; col < matrix[0].length; col++) {
    for (int row = 0; row < matrix.length; row++) {
        System.out.print(matrix[row][col] + " ");
    }
    System.out.println();
}

// Diagonal
for (int i = 0; i < matrix.length; i++) {
    System.out.print(matrix[i][i] + " "); // 1, 5, 9
}
```

#### 4. Stream API iteration (Java 8+) - O(n) time
```java
int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

// Print all elements
Arrays.stream(numbers).forEach(System.out::println);

// Filter and print
Arrays.stream(numbers)
      .filter(n -> n > 5)
      .forEach(System.out::println);     // 6, 7, 8, 9, 10

// Map and collect
int[] doubled = Arrays.stream(numbers)
                      .map(n -> n * 2)
                      .toArray();

// Sum
int sum = Arrays.stream(numbers).sum();  // 55

// Average
double avg = Arrays.stream(numbers)
                   .average()
                   .orElse(0.0);         // 5.5

// Filter, map, and collect to List
List<Integer> evenDoubled = Arrays.stream(numbers)
                                  .filter(n -> n % 2 == 0)
                                  .map(n -> n * 2)
                                  .boxed()
                                  .collect(Collectors.toList());
// [4, 8, 12, 16, 20]

// Parallel stream for large arrays
long count = Arrays.stream(numbers)
                   .parallel()
                   .filter(n -> n > 5)
                   .count();
```

### Exam Traps

- ❌ **TRAP 1:** `length` is property for arrays, `length()` is method for Strings
  ```java
  int[] arr = {1, 2, 3};
  int len = arr.length();      // ❌ Compilation error - no () for arrays
  int len = arr.length;        // ✅ Correct
  
  String s = "hello";
  int sLen = s.length();       // ✅ String uses method
  int sLen = s.length;         // ❌ Compilation error - String needs ()
  ```

- ❌ **TRAP 2:** ArrayIndexOutOfBoundsException at runtime
  ```java
  int[] arr = new int[3];      // Valid indices: 0, 1, 2
  arr[0] = 10;                 // ✅ OK
  arr[2] = 30;                 // ✅ OK
  arr[3] = 20;                 // ❌ Runtime error! Index 3 doesn't exist
  arr[-1] = 30;                // ❌ Runtime error! Negative index
  
  // Off-by-one error
  for (int i = 0; i <= arr.length; i++) { // ❌ <= causes error
      arr[i] = i;              // Crashes when i = 3
  }
  ```

- ❌ **TRAP 3:** Binary search on unsorted array
  ```java
  int[] arr = {5, 2, 8, 1, 9, 3};
  int idx = Arrays.binarySearch(arr, 5); // ❌ Undefined behavior!
  // Result is unpredictable - might find it, might not
  
  Arrays.sort(arr);            // ✅ Must sort first: [1,2,3,5,8,9]
  idx = Arrays.binarySearch(arr, 5);     // ✅ Now works: returns 3
  ```

- ❌ **TRAP 4:** Array covariance and ArrayStoreException
  ```java
  String[] strings = new String[2];
  Object[] objects = strings;  // ✅ OK (arrays are covariant)
  
  objects[0] = "Hello";        // ✅ OK (String is Object)
  objects[1] = new Integer(10);// ❌ ArrayStoreException at runtime!
  // Array remembers it's String[], not Object[]
  
  // This compiles but fails at runtime
  Number[] numbers = new Integer[5];
  numbers[0] = new Double(1.5);// ❌ ArrayStoreException
  ```

- ❌ **TRAP 5:** Arrays.asList() creates fixed-size list
  ```java
  List<String> list = Arrays.asList("A", "B", "C");
  
  list.get(0);                 // ✅ Can read: "A"
  list.set(0, "X");            // ✅ Can modify: "X"
  list.add("D");               // ❌ UnsupportedOperationException
  list.remove(0);              // ❌ UnsupportedOperationException
  list.clear();                // ❌ UnsupportedOperationException
  
  // Modifications affect original array!
  String[] arr = {"A", "B", "C"};
  List<String> list2 = Arrays.asList(arr);
  list2.set(0, "Z");
  System.out.println(arr[0]);  // "Z" - original array modified!
  ```

- ❌ **TRAP 6:** Default array initialization
  ```java
  int[] nums = new int[3];     // [0, 0, 0]
  double[] prices = new double[3]; // [0.0, 0.0, 0.0]
  boolean[] flags = new boolean[3]; // [false, false, false]
  char[] chars = new char[3];  // ['\u0000', '\u0000', '\u0000']
  String[] strs = new String[3]; // [null, null, null]
  
  // Using uninitialized objects causes NullPointerException
  String[] names = new String[3];
  int len = names[0].length(); // ❌ NullPointerException
  ```

- ❌ **TRAP 7:** Array comparison with `==`
  ```java
  int[] arr1 = {1, 2, 3};
  int[] arr2 = {1, 2, 3};
  int[] arr3 = arr1;
  
  if (arr1 == arr2) { }        // false - different objects
  if (arr1 == arr3) { }        // true - same reference
  
  if (Arrays.equals(arr1, arr2)) { } // ✅ true - same content
  
  // For 2D arrays
  int[][] matrix1 = {{1, 2}, {3, 4}};
  int[][] matrix2 = {{1, 2}, {3, 4}};
  
  Arrays.equals(matrix1, matrix2);     // ❌ false! Compares row references
  Arrays.deepEquals(matrix1, matrix2); // ✅ true - compares content
  ```

- ❌ **TRAP 8:** NullPointerException with null arrays
  ```java
  int[] arr = null;
  int len = arr.length;        // ❌ NullPointerException
  arr[0] = 10;                 // ❌ NullPointerException
  
  int[][] matrix = new int[3][];
  int cols = matrix[0].length; // ❌ NullPointerException (row not initialized)
  
  // Initialize rows first
  for (int i = 0; i < matrix.length; i++) {
      matrix[i] = new int[4];  // Now safe to use
  }
  ```

- ❌ **TRAP 9:** Enhanced for loop can't modify array elements
  ```java
  int[] arr = {1, 2, 3, 4, 5};
  
  for (int num : arr) {
      num = num * 2;           // ❌ Doesn't modify arr! Only local variable
  }
  System.out.println(Arrays.toString(arr)); // Still [1, 2, 3, 4, 5]
  
  // Must use standard for loop to modify
  for (int i = 0; i < arr.length; i++) {
      arr[i] = arr[i] * 2;     // ✅ Modifies array
  }
  System.out.println(Arrays.toString(arr)); // [2, 4, 6, 8, 10]
  
  // For objects, can modify object properties but not replace
  Person[] people = {new Person("Alice"), new Person("Bob")};
  for (Person p : people) {
      p.setName("John");       // ✅ Modifies object
      p = new Person("Jane");  // ❌ Doesn't replace in array
  }
  ```

- ❌ **TRAP 10:** Arrays.asList() with primitive arrays
  ```java
  int[] intArray = {1, 2, 3};
  List<int[]> wrong = Arrays.asList(intArray);
  // Creates List<int[]> with ONE element (the entire array)!
  System.out.println(wrong.size());    // 1, not 3!
  
  // Correct approaches:
  
  // 1. Use Integer[] instead
  Integer[] integerArray = {1, 2, 3};
  List<Integer> correct1 = Arrays.asList(integerArray);
  System.out.println(correct1.size()); // 3 ✅
  
  // 2. Use streams to box (Java 8+)
  List<Integer> correct2 = Arrays.stream(intArray)
                                 .boxed()
                                 .collect(Collectors.toList());
  
  // 3. Manual conversion
  List<Integer> correct3 = new ArrayList<>();
  for (int val : intArray) {
      correct3.add(val);       // Auto-boxing
  }
  ```

---

## Exam Questions - Arrays

**EXAM FORMAT - Java SE 21 (1Z0-830):**
- **50 questions** in **120 minutes**
- **Passing score:** 68% (34 correct minimum)
- **Arrays tested:** ~6-8 questions (12-16% of exam)

---

### Question 1: Array length property vs method
```java
public class ArrayTest {
    public static void main(String[] args) {
        int[] arr = {1, 2, 3};
        System.out.println(arr.length());
    }
}
```
**What is the result?**  
A. 3  
B. Compilation error  
C. Runtime error  
D. 0  

**Answer:** B  
**Explanation:** Arrays use `length` property (no parentheses), not `length()` method. Strings use `length()` method. This is a common trap - `arr.length` is correct.

---

### Question 2: ArrayIndexOutOfBoundsException
```java
int[] numbers = new int[5];
numbers[5] = 10;
System.out.println(numbers[5]);
```
**What is the result?**  
A. 10  
B. 0  
C. ArrayIndexOutOfBoundsException  
D. Compilation error  

**Answer:** C  
**Explanation:** Array of size 5 has valid indices 0-4. Index 5 doesn't exist. This throws ArrayIndexOutOfBoundsException at runtime, not compile time.

---

### Question 3: Arrays.binarySearch on unsorted array
```java
int[] arr = {5, 2, 8, 1, 9};
int index = Arrays.binarySearch(arr, 5);
System.out.println(index);
```
**What is the result?**  
A. 0  
B. 3  
C. Undefined behavior  
D. Compilation error  

**Answer:** C  
**Explanation:** `Arrays.binarySearch()` requires a SORTED array. On an unsorted array, behavior is undefined - result is unpredictable. Must call `Arrays.sort(arr)` first.

---

### Question 4: Arrays.asList() immutability
```java
List<String> list = Arrays.asList("A", "B", "C");
list.set(0, "Z");
list.add("D");
```
**What is the result?**  
A. list = [Z, B, C, D]  
B. list = [A, B, C, D]  
C. Compilation error  
D. UnsupportedOperationException  

**Answer:** D  
**Explanation:** `Arrays.asList()` creates a FIXED-SIZE list. Can modify existing elements with `set()`, but CANNOT add/remove elements. `add()` throws UnsupportedOperationException. The `set()` works, but `add()` fails.

---

### Question 5: Array comparison with ==
```java
int[] arr1 = {1, 2, 3};
int[] arr2 = {1, 2, 3};
System.out.println(arr1 == arr2);
System.out.println(Arrays.equals(arr1, arr2));
```
**What is the output?**  
A. true<br>true  
B. true<br>false  
C. false<br>true  
D. false<br>false  

**Answer:** C  
**Explanation:** `==` compares references (different objects = false). `Arrays.equals()` compares content (same values = true). This is a critical distinction.

---

### Question 6: Enhanced for loop modification
```java
int[] arr = {1, 2, 3};
for (int num : arr) {
    num = num * 2;
}
System.out.println(Arrays.toString(arr));
```
**What is the output?**  
A. [2, 4, 6]  
B. [1, 2, 3]  
C. Compilation error  
D. Runtime error  

**Answer:** B  
**Explanation:** Enhanced for loop creates a COPY of each element in the loop variable. Modifying `num` doesn't affect the array. Original array remains [1, 2, 3].

---

### Question 7: Arrays.asList() with primitives
```java
int[] arr = {1, 2, 3};
List<Integer> list = Arrays.asList(arr);
System.out.println(list.size());
```
**What is the result?**  
A. 3  
B. 1  
C. Compilation error  
D. 0  

**Answer:** C  
**Explanation:** `Arrays.asList(int[])` creates `List<int[]>`, not `List<Integer>`. Cannot assign `List<int[]>` to `List<Integer>`. Must use `Integer[]` or streams with boxing.

---

### Question 8: Array default initialization
```java
String[] names = new String[3];
System.out.println(names[0].length());
```
**What is the result?**  
A. 0  
B. NullPointerException  
C. Compilation error  
D. 3  

**Answer:** B  
**Explanation:** Object arrays initialize to `null`. Calling `.length()` on `null` throws NullPointerException. Must initialize: `names[0] = "Something"` first.

---

### Question 9: Arrays.deepEquals vs equals
```java
int[][] arr1 = {{1, 2}, {3, 4}};
int[][] arr2 = {{1, 2}, {3, 4}};
System.out.println(Arrays.equals(arr1, arr2));
System.out.println(Arrays.deepEquals(arr1, arr2));
```
**What is the output?**  
A. true<br>true  
B. false<br>true  
C. true<br>false  
D. false<br>false  

**Answer:** B  
**Explanation:** For multi-dimensional arrays, `equals()` compares row REFERENCES (false). `deepEquals()` compares CONTENT recursively (true). Always use `deepEquals()` for 2D+ arrays.

---

### Question 10: System.arraycopy
```java
int[] src = {1, 2, 3, 4, 5};
int[] dest = new int[5];
System.arraycopy(src, 1, dest, 2, 3);
System.out.println(Arrays.toString(dest));
```
**What is the output?**  
A. [1, 2, 3, 4, 5]  
B. [0, 0, 2, 3, 4]  
C. [2, 3, 4, 0, 0]  
D. Compilation error  

**Answer:** B  
**Explanation:** `arraycopy(src, 1, dest, 2, 3)` copies 3 elements starting from src[1] (2,3,4) to dest starting at index 2. Result: [0, 0, 2, 3, 4].

---

**End of Part 1 - Arrays**

**Continue with:**
- **[Part 2: List Interface →](CHAPTER-05-PART2-LIST.md)** (ArrayList, LinkedList, all methods)
- **Part 3: Set Interface** (HashSet, TreeSet, LinkedHashSet)
- **Part 4: Map Interface** (HashMap, TreeMap, LinkedHashMap)
- **Part 5: Queue/Deque** + Summary


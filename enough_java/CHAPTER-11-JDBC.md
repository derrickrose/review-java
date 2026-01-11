# CHAPTER 11: JDBC (Java Database Connectivity)

**FOR JAVA 17 CERTIFICATION (1Z0-829) ONLY**  
⚠️ **JDBC REMOVED from Java 21 (1Z0-830)**

---

## 📋 OFFICIAL EXAM OBJECTIVES

**Accessing Databases Using JDBC (Java 17 Only)**

- Create connections, create and execute basic, prepared, and callable statements
- Process query results using ResultSet interface
- Implement transaction management
- Use JDBC URL, DriverManager, and DataSource
- Handle SQLException
- Work with different ResultSet types and concurrency modes

**Exam Weight**: 10-15% of Java 17 exam (5-8 questions out of 50)

---

## 📚 TABLE OF CONTENTS

1. Connecting to Databases (DriverManager, Connection, JDBC URL)
2. Creating and Using PreparedStatement
3. Working with ResultSet
4. Executing Queries and Updates (executeQuery, executeUpdate, execute)
5. Handling SQL Exceptions
6. Understanding Transaction Management
7. Using CallableStatement for Stored Procedures
8. ResultSet Types and Concurrency
9. Scrollable and Updatable ResultSets
10. Batch Updates
11. Understanding JDBC Drivers
12. Using RowSet Interfaces

---

## TOPIC 1: Connecting to Databases

### What: Database Connection Basics

**Key Interfaces:**
```java
// Connection hierarchy
DriverManager (class)         // Manages JDBC drivers
Connection (interface)         // Database connection
Statement (interface)          // Execute SQL
PreparedStatement (interface)  // Precompiled SQL
CallableStatement (interface)  // Stored procedures
```

**JDBC URL Format:**
```java
jdbc:<subprotocol>:<subname>

Examples:
jdbc:derby:zoo              // Embedded Derby
jdbc:postgresql://localhost:5432/mydb
jdbc:mysql://localhost/test
jdbc:oracle:thin:@localhost:1521:orcl
```

**Creating Connection:**
```java
import java.sql.*;

// Method 1: DriverManager (most common on exam)
String url = "jdbc:derby:zoo";
Connection conn = DriverManager.getConnection(url);

// Method 2: With credentials
Connection conn = DriverManager.getConnection(url, "username", "password");

// Method 3: Using Properties
Properties props = new Properties();
props.setProperty("user", "username");
props.setProperty("password", "password");
Connection conn = DriverManager.getConnection(url, props);
```

### When to Use: Every JDBC Interaction

**Connection is required** for:
- Creating statements
- Executing queries
- Managing transactions
- Accessing database metadata

**Connection lifecycle:**
```
1. Open connection (getConnection)
2. Use connection (create statements)
3. Close connection (close or try-with-resources)
```

### Why Use: Database Access Foundation

**DriverManager benefits:**
- Simple API for basic connections
- Automatic driver loading (JDBC 4.0+)
- Multiple driver support

**Connection benefits:**
- Manages database session
- Transaction control
- Statement factory
- Resource management

### Real-World Example: Employee Database Access

```java
import java.sql.*;

public class EmployeeDatabase {
    private static final String URL = "jdbc:derby:employeeDB";
    
    public static void main(String[] args) {
        // Try-with-resources ensures connection closes
        try (Connection conn = DriverManager.getConnection(URL)) {
            
            System.out.println("Connected to database!");
            
            // Get database metadata
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("Database: " + meta.getDatabaseProductName());
            System.out.println("Version: " + meta.getDatabaseProductVersion());
            System.out.println("Driver: " + meta.getDriverName());
            
            // Check connection status
            System.out.println("Connection valid: " + conn.isValid(2)); // 2 sec timeout
            System.out.println("Read-only: " + conn.isReadOnly());
            System.out.println("Auto-commit: " + conn.getAutoCommit());
            
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

// Output:
// Connected to database!
// Database: Apache Derby
// Version: 10.15.2.0
// Driver: Apache Derby Embedded JDBC Driver
// Connection valid: true
// Read-only: false
// Auto-commit: true
```

**Explanation:**
- `try-with-resources` automatically closes connection
- `DatabaseMetaData` provides database information
- `isValid(int timeout)` checks if connection is alive
- `getAutoCommit()` shows transaction mode

### Exam Traps:

❌ **TRAP 1: Connection Never Closes**
```java
Connection conn = DriverManager.getConnection(url);
// Do work...
// Forgot to close - RESOURCE LEAK!
```
**Fix**: Use try-with-resources

❌ **TRAP 2: SQLException Not Caught**
```java
Connection conn = DriverManager.getConnection(url); // COMPILE ERROR
// SQLException is checked exception, must be caught or declared
```

❌ **TRAP 3: Wrong JDBC URL Format**
```java
String url = "derby:zoo";  // MISSING jdbc: prefix
Connection conn = DriverManager.getConnection(url); // SQLException at runtime
```

❌ **TRAP 4: Using Closed Connection**
```java
Connection conn = DriverManager.getConnection(url);
conn.close();
Statement stmt = conn.createStatement(); // SQLException: connection closed
```

❌ **TRAP 5: Driver Not Found (Pre-JDBC 4.0)**
```java
// JDBC 3.0 and earlier required explicit driver loading
Class.forName("org.apache.derby.jdbc.EmbeddedDriver"); // Not needed in JDBC 4.0+
```

### Additional Context:

**JDBC 4.0+ Auto-Loading:**
- Drivers automatically loaded from classpath
- `Class.forName()` no longer needed
- ServiceLoader mechanism

**Connection Pooling:**
- Not directly on exam
- Production uses DataSource with pools
- Exam focuses on DriverManager

---

## TOPIC 2: Creating and Using PreparedStatement

### What: Precompiled SQL Statements

**PreparedStatement hierarchy:**
```
Statement (interface)
    ↓ extends
PreparedStatement (interface)
    ↓ extends
CallableStatement (interface)
```

**Key Features:**
```java
// Parameters use ? placeholder
String sql = "SELECT * FROM employees WHERE id = ?";
PreparedStatement ps = conn.prepareStatement(sql);

// Set parameters (1-indexed)
ps.setInt(1, 100);        // Set parameter 1 to 100
ps.setString(1, "John");  // Set parameter 1 to "John"
ps.setDouble(1, 50000.00);
```

**Creating PreparedStatement:**
```java
// From Connection
Connection conn = DriverManager.getConnection(url);

// Method 1: Simple query
String sql = "SELECT name FROM employees WHERE id = ?";
PreparedStatement ps = conn.prepareStatement(sql);

// Method 2: With ResultSet type/concurrency
PreparedStatement ps = conn.prepareStatement(sql, 
    ResultSet.TYPE_SCROLL_INSENSITIVE,
    ResultSet.CONCUR_READ_ONLY);

// Method 3: With generated keys
String insert = "INSERT INTO employees (name) VALUES (?)";
PreparedStatement ps = conn.prepareStatement(insert, 
    Statement.RETURN_GENERATED_KEYS);
```

### When to Use: Whenever Using SQL Parameters

**Use PreparedStatement when:**
- Query has user input (SQL injection prevention)
- Query executed multiple times (performance)
- Need to set parameters programmatically
- Working with dates, times, binary data

**PreparedStatement vs Statement:**
```java
// BAD: Statement (vulnerable to SQL injection)
String name = userInput; // Could be "'; DROP TABLE employees; --"
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM employees WHERE name = '" + name + "'");

// GOOD: PreparedStatement (safe from SQL injection)
PreparedStatement ps = conn.prepareStatement("SELECT * FROM employees WHERE name = ?");
ps.setString(1, name); // Properly escaped
ResultSet rs = ps.executeQuery();
```

### Why Use: Security and Performance

**Security Benefits:**
- Prevents SQL injection attacks
- Automatic parameter escaping
- Type-safe parameter binding

**Performance Benefits:**
- SQL precompiled by database
- Query plan cached
- Reusable with different parameters

**Maintainability Benefits:**
- Cleaner code (no string concatenation)
- Type checking at compile time
- Easier to read and debug

### Real-World Example: Employee Management System

```java
import java.sql.*;

public class EmployeeManager {
    private static final String URL = "jdbc:derby:company";
    
    // Search employees by department and minimum salary
    public static void searchEmployees(String department, double minSalary) {
        String sql = "SELECT id, name, salary FROM employees " +
                     "WHERE department = ? AND salary >= ? " +
                     "ORDER BY salary DESC";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Set parameters (1-indexed, not 0-indexed!)
            ps.setString(1, department);   // First ?
            ps.setDouble(2, minSalary);    // Second ?
            
            // Execute query
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("Employees in " + department + 
                                 " earning >= $" + minSalary + ":");
                
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    double salary = rs.getDouble("salary");
                    
                    System.out.printf("ID: %d, Name: %s, Salary: $%.2f%n",
                                    id, name, salary);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
    
    // Insert new employee with generated key
    public static int addEmployee(String name, String dept, double salary) {
        String sql = "INSERT INTO employees (name, department, salary) VALUES (?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql, 
                                    Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, name);
            ps.setString(2, dept);
            ps.setDouble(3, salary);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Inserted " + rowsAffected + " row(s)");
            
            // Get generated ID
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    System.out.println("Generated ID: " + generatedId);
                    return generatedId;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Insert failed: " + e.getMessage());
        }
        
        return -1;
    }
    
    public static void main(String[] args) {
        // Test search
        searchEmployees("Engineering", 75000.00);
        
        // Test insert
        int newId = addEmployee("Alice Johnson", "Engineering", 85000.00);
        System.out.println("New employee ID: " + newId);
    }
}

// Output:
// Employees in Engineering earning >= $75000.0:
// ID: 1, Name: Bob Smith, Salary: $95000.00
// ID: 3, Name: Carol White, Salary: $80000.00
// Inserted 1 row(s)
// Generated ID: 4
// New employee ID: 4
```

**Explanation:**
- `setString(1, dept)` sets first `?` to department value
- `setDouble(2, minSalary)` sets second `?` to salary value
- Parameters are **1-indexed**, not 0-indexed
- `executeQuery()` returns `ResultSet` for SELECT
- `executeUpdate()` returns row count for INSERT/UPDATE/DELETE
- `getGeneratedKeys()` retrieves auto-generated primary keys

### Exam Traps:

❌ **TRAP 1: Parameters Are 1-Indexed, Not 0-Indexed**
```java
PreparedStatement ps = conn.prepareStatement("SELECT * FROM emp WHERE id = ?");
ps.setInt(0, 100); // SQLException: Invalid parameter index 0
// CORRECT: ps.setInt(1, 100);
```

❌ **TRAP 2: Wrong Number of Parameters**
```java
String sql = "SELECT * FROM emp WHERE dept = ? AND salary > ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setString(1, "IT");
// Missing second parameter!
ResultSet rs = ps.executeQuery(); // SQLException: Parameter not set
```

❌ **TRAP 3: Setting Wrong Parameter Type**
```java
PreparedStatement ps = conn.prepareStatement("SELECT * FROM emp WHERE id = ?");
ps.setString(1, "100"); // Works but not ideal
ps.setInt(1, 100);      // Better - matches database column type
```

❌ **TRAP 4: Reusing PreparedStatement Without Clearing Parameters**
```java
PreparedStatement ps = conn.prepareStatement("SELECT * FROM emp WHERE id = ?");
ps.setInt(1, 100);
ResultSet rs1 = ps.executeQuery();

// Parameters persist!
ResultSet rs2 = ps.executeQuery(); // Still uses id=100
```

❌ **TRAP 5: Using execute() Instead of executeQuery()**
```java
PreparedStatement ps = conn.prepareStatement("SELECT * FROM emp");
boolean hasResultSet = ps.execute();  // Returns true
// Now must call ps.getResultSet() to get results
ResultSet rs = ps.getResultSet();

// SIMPLER:
ResultSet rs = ps.executeQuery();
```

❌ **TRAP 6: Mixing ? Placeholders with String Concatenation**
```java
// DON'T DO THIS:
String sql = "SELECT * FROM emp WHERE dept = '" + dept + "' AND salary > ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setDouble(1, minSalary);
// This defeats SQL injection protection!
```

### Additional Context:

**Parameter Types:**
```java
ps.setInt(1, 100);
ps.setLong(1, 100L);
ps.setDouble(1, 99.99);
ps.setString(1, "text");
ps.setBoolean(1, true);
ps.setDate(1, java.sql.Date.valueOf("2024-01-01"));
ps.setTime(1, java.sql.Time.valueOf("10:30:00"));
ps.setTimestamp(1, java.sql.Timestamp.valueOf("2024-01-01 10:30:00"));
ps.setObject(1, value); // Generic, uses JDBC type mapping
```

**Clearing Parameters:**
```java
ps.clearParameters(); // Clears all parameters
// Must call setXxx() again before next executeQuery()
```

---

## TOPIC 3: Working with ResultSet

### What: Query Results Container

**ResultSet** represents database query results as a table:
```
ResultSet = Row cursor + Column access

Default: Forward-only, read-only cursor
Position: Before first row initially
```

**Key Methods:**
```java
// Navigation
boolean next()     // Move to next row, returns false if no more rows
boolean previous() // Move to previous row (scrollable only)
boolean first()    // Move to first row (scrollable only)
boolean last()     // Move to last row (scrollable only)
void beforeFirst() // Position before first row (scrollable only)
void afterLast()   // Position after last row (scrollable only)

// Position checking
boolean isFirst()
boolean isLast()
boolean isBeforeFirst()
boolean isAfterLast()
int getRow()       // Current row number (1-indexed)

// Column access (1-indexed or by name)
int getInt(int columnIndex)
int getInt(String columnLabel)
String getString(int columnIndex)
String getString(String columnLabel)
// ... similar for other types
```

**Creating ResultSet:**
```java
// From Statement
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT id, name FROM employees");

// From PreparedStatement
PreparedStatement ps = conn.prepareStatement("SELECT * FROM employees WHERE dept = ?");
ps.setString(1, "IT");
ResultSet rs = ps.executeQuery();
```

### When to Use: Reading Query Results

**ResultSet pattern:**
```java
while (rs.next()) {
    // Process current row
    int id = rs.getInt("id");
    String name = rs.getString("name");
    // ...
}
```

**Why `while (rs.next())`:**
1. `rs.next()` moves cursor to next row
2. Returns `true` if row exists, `false` if no more rows
3. Initially positioned **before** first row

### Why Use: Standard Way to Read Query Results

**ResultSet advantages:**
- Consistent API across all databases
- Type-safe column access
- Memory efficient (streams results)
- Supports forward and backward navigation (scrollable)

**Column access options:**
```java
// By index (1-based, faster)
int id = rs.getInt(1);       // First column
String name = rs.getString(2); // Second column

// By name (more readable, slightly slower)
int id = rs.getInt("id");
String name = rs.getString("name");
```

### Real-World Example: Employee Report Generator

```java
import java.sql.*;

public class EmployeeReport {
    private static final String URL = "jdbc:derby:company";
    
    public static void generateReport() {
        String sql = "SELECT id, name, department, salary, hire_date " +
                     "FROM employees " +
                     "WHERE salary > 50000 " +
                     "ORDER BY department, salary DESC";
        
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("EMPLOYEE REPORT");
            System.out.println("=".repeat(80));
            System.out.printf("%-5s %-20s %-15s %10s %-12s%n",
                            "ID", "Name", "Department", "Salary", "Hire Date");
            System.out.println("-".repeat(80));
            
            String currentDept = "";
            double deptTotal = 0.0;
            int deptCount = 0;
            
            while (rs.next()) {
                // Get column values
                int id = rs.getInt("id");              // Or rs.getInt(1)
                String name = rs.getString("name");     // Or rs.getString(2)
                String dept = rs.getString("department");
                double salary = rs.getDouble("salary");
                Date hireDate = rs.getDate("hire_date");
                
                // Department grouping
                if (!dept.equals(currentDept)) {
                    if (!currentDept.isEmpty()) {
                        // Print previous department summary
                        System.out.printf("   %s Subtotal: $%.2f (Avg: $%.2f, Count: %d)%n%n",
                                        currentDept, deptTotal, deptTotal/deptCount, deptCount);
                    }
                    currentDept = dept;
                    deptTotal = 0.0;
                    deptCount = 0;
                }
                
                // Print employee
                System.out.printf("%-5d %-20s %-15s $%,9.2f %s%n",
                                id, name, dept, salary, hireDate);
                
                deptTotal += salary;
                deptCount++;
            }
            
            // Final department summary
            if (deptCount > 0) {
                System.out.printf("   %s Subtotal: $%.2f (Avg: $%.2f, Count: %d)%n",
                                currentDept, deptTotal, deptTotal/deptCount, deptCount);
            }
            
            System.out.println("=".repeat(80));
            
        } catch (SQLException e) {
            System.err.println("Report generation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Check for null values
    public static void demonstrateNullHandling() {
        String sql = "SELECT id, name, middle_name FROM employees";
        
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String middleName = rs.getString("middle_name");
                
                // Check if column was NULL
                if (rs.wasNull()) {
                    System.out.println("ID " + id + ": " + name + " (no middle name)");
                } else {
                    System.out.println("ID " + id + ": " + name + " " + middleName);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        generateReport();
        demonstrateNullHandling();
    }
}

// Output:
// EMPLOYEE REPORT
// ================================================================================
// ID    Name                 Department      Salary   Hire Date   
// --------------------------------------------------------------------------------
// 101   Alice Johnson        Engineering    $95,000.00 2020-03-15
// 102   Bob Smith            Engineering    $85,000.00 2021-06-01
//    Engineering Subtotal: $180000.00 (Avg: $90000.00, Count: 2)
//
// 201   Carol White          Marketing      $75,000.00 2019-11-20
//    Marketing Subtotal: $75000.00 (Avg: $75000.00, Count: 1)
// ================================================================================
```

**Explanation:**
- `rs.next()` moves cursor and returns `true` if row exists
- Columns accessed by name (`rs.getString("name")`) or index (`rs.getString(2)`)
- `rs.getDate()` returns `java.sql.Date` (not `java.util.Date`)
- `rs.wasNull()` checks if last retrieved column was SQL NULL

### Exam Traps:

❌ **TRAP 1: Columns Are 1-Indexed, Not 0-Indexed**
```java
ResultSet rs = stmt.executeQuery("SELECT id, name FROM employees");
while (rs.next()) {
    int id = rs.getInt(0); // SQLException: Invalid column index 0
    // CORRECT: rs.getInt(1) or rs.getInt("id")
}
```

❌ **TRAP 2: Forgetting to Call next() Before Reading**
```java
ResultSet rs = stmt.executeQuery("SELECT * FROM employees");
// ResultSet positioned BEFORE first row
String name = rs.getString("name"); // SQLException: cursor before first row

// CORRECT:
if (rs.next()) {
    String name = rs.getString("name");
}
```

❌ **TRAP 3: Column Index vs Column Name Mismatch**
```java
ResultSet rs = stmt.executeQuery("SELECT name, id FROM employees");
while (rs.next()) {
    int id = rs.getInt(1);      // Gets 'name' (first column) - SQLException!
    String name = rs.getString(2); // Gets 'id' (second column)
    
    // CORRECT:
    String name = rs.getString(1);  // First column
    int id = rs.getInt(2);          // Second column
}
```

❌ **TRAP 4: Using getInt() on NULL Column**
```java
// Database has NULL in age column
ResultSet rs = stmt.executeQuery("SELECT age FROM employees");
while (rs.next()) {
    int age = rs.getInt("age");  // Returns 0 for NULL (not exception!)
    System.out.println(age);     // Prints 0, not null
    
    // CORRECT: Check wasNull()
    int age = rs.getInt("age");
    if (rs.wasNull()) {
        System.out.println("Age is NULL");
    }
}
```

❌ **TRAP 5: Accessing ResultSet After Statement Closed**
```java
ResultSet rs;
try (Statement stmt = conn.createStatement()) {
    rs = stmt.executeQuery("SELECT * FROM employees");
} // stmt.close() called here, also closes ResultSet!

rs.next(); // SQLException: ResultSet is closed
```

❌ **TRAP 6: Wrong Getter Method for Column Type**
```java
// Column 'salary' is DECIMAL/DOUBLE in database
ResultSet rs = stmt.executeQuery("SELECT salary FROM employees");
while (rs.next()) {
    int salary = rs.getInt("salary");  // Truncates decimal part!
    // $75,500.50 becomes 75500
    
    // CORRECT:
    double salary = rs.getDouble("salary");
}
```

### Additional Context:

**Type Mappings:**
```java
// SQL Type → Java Type
INT        → int (getInt)
BIGINT     → long (getLong)
DECIMAL    → double (getDouble) or BigDecimal (getBigDecimal)
VARCHAR    → String (getString)
DATE       → java.sql.Date (getDate)
TIME       → java.sql.Time (getTime)
TIMESTAMP  → java.sql.Timestamp (getTimestamp)
BOOLEAN    → boolean (getBoolean)
```

**Null Handling:**
```java
int age = rs.getInt("age");
if (rs.wasNull()) {
    // Column was NULL
}

// Alternative for Objects (returns null)
String middleName = rs.getString("middle_name");
if (middleName == null) {
    // Column was NULL
}
```

---

## TOPIC 4: Executing Queries and Updates

### What: Three Execute Methods

**Statement/PreparedStatement has 3 execute methods:**

```java
// 1. executeQuery() - For SELECT (returns ResultSet)
ResultSet rs = stmt.executeQuery("SELECT * FROM employees");

// 2. executeUpdate() - For INSERT/UPDATE/DELETE (returns int count)
int rowsAffected = stmt.executeUpdate("DELETE FROM employees WHERE id = 100");

// 3. execute() - For any SQL (returns boolean)
boolean isResultSet = stmt.execute("SELECT * FROM employees");
```

**Method Summary:**
```
executeQuery()  : SELECT only, returns ResultSet
executeUpdate() : INSERT/UPDATE/DELETE/DDL, returns row count
execute()       : Any SQL, returns boolean (true = ResultSet, false = count)
```

### When to Use: Based on SQL Type

**executeQuery()** - Use for SELECT:
```java
String sql = "SELECT id, name FROM employees WHERE dept = ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setString(1, "IT");
ResultSet rs = ps.executeQuery(); // Returns rows
```

**executeUpdate()** - Use for modifications:
```java
// INSERT
String sql = "INSERT INTO employees (name, dept) VALUES (?, ?)";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setString(1, "John");
ps.setString(2, "IT");
int rows = ps.executeUpdate(); // Returns number of rows inserted

// UPDATE
sql = "UPDATE employees SET salary = salary * 1.10 WHERE dept = ?";
ps = conn.prepareStatement(sql);
ps.setString(1, "IT");
int rows = ps.executeUpdate(); // Returns number of rows updated

// DELETE
sql = "DELETE FROM employees WHERE id = ?";
ps = conn.prepareStatement(sql);
ps.setInt(1, 100);
int rows = ps.executeUpdate(); // Returns number of rows deleted
```

**execute()** - Use when SQL type unknown:
```java
boolean hasResultSet = stmt.execute(unknownSQL);

if (hasResultSet) {
    // It was a SELECT
    ResultSet rs = stmt.getResultSet();
    // Process ResultSet
} else {
    // It was INSERT/UPDATE/DELETE
    int count = stmt.getUpdateCount();
    System.out.println("Rows affected: " + count);
}
```

### Why Use: Type Safety and Clarity

**Benefits of specific methods:**
- `executeQuery()`: Compile-time guarantee of ResultSet
- `executeUpdate()`: Clear intent for modifications
- `execute()`: Flexibility for dynamic SQL

**Performance considerations:**
- All three have similar performance
- Choice based on code clarity
- PreparedStatement faster for repeated execution

### Real-World Example: CRUD Operations

```java
import java.sql.*;

public class EmployeeCRUD {
    private static final String URL = "jdbc:derby:company";
    
    // CREATE - Insert new employee
    public static int createEmployee(String name, String dept, double salary) {
        String sql = "INSERT INTO employees (name, department, salary) VALUES (?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql, 
                                    Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, name);
            ps.setString(2, dept);
            ps.setDouble(3, salary);
            
            int rowsInserted = ps.executeUpdate(); // Returns 1 if successful
            System.out.println("Inserted " + rowsInserted + " row(s)");
            
            // Get auto-generated ID
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Insert failed: " + e.getMessage());
        }
        
        return -1;
    }
    
    // READ - Query employees
    public static void readEmployees(String department) {
        String sql = "SELECT id, name, salary FROM employees WHERE department = ?";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, department);
            
            // executeQuery() returns ResultSet
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("Employees in " + department + ":");
                
                while (rs.next()) {
                    System.out.printf("ID: %d, Name: %s, Salary: $%.2f%n",
                                    rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getDouble("salary"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Query failed: " + e.getMessage());
        }
    }
    
    // UPDATE - Modify employee salary
    public static boolean updateSalary(int employeeId, double newSalary) {
        String sql = "UPDATE employees SET salary = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDouble(1, newSalary);
            ps.setInt(2, employeeId);
            
            int rowsUpdated = ps.executeUpdate(); // Returns number of rows affected
            
            if (rowsUpdated > 0) {
                System.out.println("Updated employee " + employeeId + 
                                 " salary to $" + newSalary);
                return true;
            } else {
                System.out.println("Employee " + employeeId + " not found");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Update failed: " + e.getMessage());
            return false;
        }
    }
    
    // DELETE - Remove employee
    public static boolean deleteEmployee(int employeeId) {
        String sql = "DELETE FROM employees WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, employeeId);
            
            int rowsDeleted = ps.executeUpdate(); // Returns number of rows deleted
            
            if (rowsDeleted > 0) {
                System.out.println("Deleted employee " + employeeId);
                return true;
            } else {
                System.out.println("Employee " + employeeId + " not found");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Delete failed: " + e.getMessage());
            return false;
        }
    }
    
    // Example using execute() for dynamic SQL
    public static void executeDynamicSQL(String sql) {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            
            boolean isResultSet = stmt.execute(sql);
            
            if (isResultSet) {
                // SQL was a SELECT
                try (ResultSet rs = stmt.getResultSet()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();
                    
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.print(rs.getString(i) + "\t");
                        }
                        System.out.println();
                    }
                }
            } else {
                // SQL was INSERT/UPDATE/DELETE
                int updateCount = stmt.getUpdateCount();
                System.out.println("Rows affected: " + updateCount);
            }
            
        } catch (SQLException e) {
            System.err.println("Execution failed: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        // CREATE
        int newId = createEmployee("Alice Johnson", "Engineering", 85000);
        System.out.println("Created employee with ID: " + newId);
        
        // READ
        readEmployees("Engineering");
        
        // UPDATE
        updateSalary(newId, 90000);
        
        // DELETE
        deleteEmployee(newId);
        
        // Dynamic SQL
        executeDynamicSQL("SELECT COUNT(*) FROM employees");
        executeDynamicSQL("UPDATE employees SET salary = salary * 1.05");
    }
}

// Output:
// Inserted 1 row(s)
// Created employee with ID: 5
// Employees in Engineering:
// ID: 1, Name: Bob Smith, Salary: $95000.00
// ID: 5, Name: Alice Johnson, Salary: $85000.00
// Updated employee 5 salary to $90000.0
// Deleted employee 5
// 4
// Rows affected: 4
```

**Explanation:**
- `executeUpdate()` returns **row count**, not boolean
- `executeQuery()` returns **ResultSet**, must be assigned
- `execute()` returns **boolean**: true if ResultSet, false if update count
- After `execute()`, call `getResultSet()` or `getUpdateCount()`

### Exam Traps:

❌ **TRAP 1: Using executeQuery() for INSERT/UPDATE/DELETE**
```java
String sql = "DELETE FROM employees WHERE id = 100";
PreparedStatement ps = conn.prepareStatement(sql);
ResultSet rs = ps.executeQuery(); // SQLException: executeQuery cannot be used for updates
// CORRECT: int rows = ps.executeUpdate();
```

❌ **TRAP 2: Using executeUpdate() for SELECT**
```java
String sql = "SELECT * FROM employees";
PreparedStatement ps = conn.prepareStatement(sql);
int count = ps.executeUpdate(); // SQLException: executeUpdate cannot be used for queries
// CORRECT: ResultSet rs = ps.executeQuery();
```

❌ **TRAP 3: Ignoring Return Value of executeUpdate()**
```java
String sql = "UPDATE employees SET salary = ? WHERE id = ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setDouble(1, 100000);
ps.setInt(2, 999); // ID doesn't exist
ps.executeUpdate(); // Returns 0 but not checked!

// CORRECT:
int rows = ps.executeUpdate();
if (rows == 0) {
    System.out.println("No rows updated - ID not found");
}
```

❌ **TRAP 4: Not Calling getResultSet() After execute()**
```java
Statement stmt = conn.createStatement();
boolean hasRS = stmt.execute("SELECT * FROM employees");
// Forgot to call getResultSet()!

// CORRECT:
if (hasRS) {
    ResultSet rs = stmt.getResultSet();
    // Process results
}
```

❌ **TRAP 5: Mixing execute() Return with getUpdateCount()**
```java
Statement stmt = conn.createStatement();
boolean hasRS = stmt.execute("DELETE FROM employees WHERE id = 100");

if (hasRS) {  // hasRS is FALSE for DELETE
    ResultSet rs = stmt.getResultSet(); // This won't execute
}

// CORRECT:
if (!hasRS) {  // No ResultSet = update count
    int count = stmt.getUpdateCount();
}
```

### Additional Context:

**execute() Use Cases:**
- Dynamic SQL where type is unknown at compile time
- Stored procedures that may return multiple result sets
- Batch operations with mixed SQL types

**Return Value Summary:**
```java
executeQuery()  → ResultSet (never null, but may be empty)
executeUpdate() → int (row count, may be 0)
execute()       → boolean (true = ResultSet, false = update count)
```

**DDL Statements:**
```java
// CREATE TABLE, ALTER TABLE, DROP TABLE use executeUpdate()
stmt.executeUpdate("CREATE TABLE temp (id INT, name VARCHAR(50))");
stmt.executeUpdate("DROP TABLE temp");
// Return value is 0 for DDL (no rows affected)
```

---

## TOPIC 5: Handling SQL Exceptions

### What: SQLException Hierarchy

**SQLException** is the root exception for all JDBC errors:
```
Throwable
    ↓
Exception
    ↓
SQLException (checked exception)
    ↓
    ├── SQLWarning (not an exception, warnings)
    ├── BatchUpdateException
    ├── SQLTimeoutException
    └── Other subclasses
```

**Key SQLException Methods:**
```java
String getMessage()      // Error message
String getSQLState()     // 5-character SQLSTATE code
int getErrorCode()       // Database-specific error code
SQLException getNextException()  // Chained exceptions
```

### When to Use: Every JDBC Operation Can Throw

**All JDBC methods throw SQLException:**
```java
try {
    Connection conn = DriverManager.getConnection(url);  // Can throw
    PreparedStatement ps = conn.prepareStatement(sql);   // Can throw
    ResultSet rs = ps.executeQuery();                    // Can throw
    while (rs.next()) {                                  // Can throw
        int value = rs.getInt("column");                 // Can throw
    }
} catch (SQLException e) {
    // Handle error
}
```

### Why Use: Proper Error Handling

**SQLException provides:**
- Detailed error information
- SQLSTATE codes (standardized across databases)
- Vendor-specific error codes
- Exception chaining for multiple errors

**SQLSTATE Format:**
```
SQLSTATE = 5 characters
  First 2: Class (00 = success, 01 = warning, others = errors)
  Last 3: Subclass (specific error)

Examples:
  08001 = Cannot connect to database
  23000 = Integrity constraint violation
  42000 = Syntax error
  42S02 = Table not found
```

### Real-World Example: Comprehensive Error Handling

```java
import java.sql.*;

public class ErrorHandlingExample {
    private static final String URL = "jdbc:derby:company";
    
    // Example 1: Basic exception handling
    public static void basicErrorHandling() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            
            String sql = "INSERT INTO employees (id, name) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, 1);  // Duplicate ID
                ps.setString(2, "John");
                ps.executeUpdate();
            }
            
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
        }
    }
    
    // Example 2: Handling chained exceptions
    public static void chainedExceptions() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            
            // Execute multiple statements, some may fail
            stmt.executeUpdate("INSERT INTO employees VALUES (1, 'Alice')");
            stmt.executeUpdate("INSERT INTO employees VALUES (1, 'Bob')"); // Duplicate
            
        } catch (SQLException e) {
            // Print all chained exceptions
            System.err.println("SQLException caught:");
            
            SQLException current = e;
            int count = 1;
            while (current != null) {
                System.err.println("\nException " + count++ + ":");
                System.err.println("  Message: " + current.getMessage());
                System.err.println("  SQLState: " + current.getSQLState());
                System.err.println("  Error Code: " + current.getErrorCode());
                
                current = current.getNextException();
            }
        }
    }
    
    // Example 3: Specific error handling
    public static void specificErrorHandling(int employeeId, String name) {
        String sql = "INSERT INTO employees (id, name) VALUES (?, ?)";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, employeeId);
            ps.setString(2, name);
            ps.executeUpdate();
            
            System.out.println("Employee added successfully");
            
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            
            // Handle specific error types
            if (sqlState.startsWith("23")) {
                // Integrity constraint violation (23xxx)
                if (sqlState.equals("23505")) {
                    System.err.println("Duplicate key: Employee ID " + 
                                     employeeId + " already exists");
                } else if (sqlState.equals("23502")) {
                    System.err.println("NULL not allowed in required field");
                } else {
                    System.err.println("Integrity constraint violated");
                }
            } else if (sqlState.startsWith("42")) {
                // Syntax error or access violation (42xxx)
                System.err.println("SQL syntax error or access denied: " + 
                                 e.getMessage());
            } else if (sqlState.startsWith("08")) {
                // Connection error (08xxx)
                System.err.println("Database connection error: " + 
                                 e.getMessage());
            } else {
                // Generic error
                System.err.println("Database error (" + sqlState + "): " + 
                                 e.getMessage());
            }
            
            // Log stack trace for debugging
            e.printStackTrace();
        }
    }
    
    // Example 4: Handling SQLWarning
    public static void warningHandling() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery("SELECT * FROM employees");
            
            // Check for warnings on Connection
            SQLWarning warning = conn.getWarnings();
            printWarnings("Connection", warning);
            
            // Check for warnings on Statement
            warning = stmt.getWarnings();
            printWarnings("Statement", warning);
            
            // Check for warnings on ResultSet
            warning = rs.getWarnings();
            printWarnings("ResultSet", warning);
            
            // Clear warnings
            conn.clearWarnings();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void printWarnings(String type, SQLWarning warning) {
        if (warning != null) {
            System.out.println(type + " warnings:");
            while (warning != null) {
                System.out.println("  " + warning.getMessage());
                warning = warning.getNextWarning();
            }
        }
    }
    
    // Example 5: Retrying on connection failure
    public static Connection connectWithRetry(int maxAttempts) {
        int attempts = 0;
        
        while (attempts < maxAttempts) {
            try {
                attempts++;
                System.out.println("Connection attempt " + attempts + "...");
                
                Connection conn = DriverManager.getConnection(URL);
                System.out.println("Connected successfully");
                return conn;
                
            } catch (SQLException e) {
                System.err.println("Attempt " + attempts + " failed: " + 
                                 e.getMessage());
                
                if (attempts >= maxAttempts) {
                    System.err.println("Max attempts reached, giving up");
                    throw new RuntimeException("Failed to connect after " + 
                                             maxAttempts + " attempts", e);
                }
                
                // Wait before retrying
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while retrying", ie);
                }
            }
        }
        
        return null; // Should never reach here
    }
    
    public static void main(String[] args) {
        System.out.println("=== Basic Error Handling ===");
        basicErrorHandling();
        
        System.out.println("\n=== Chained Exceptions ===");
        chainedExceptions();
        
        System.out.println("\n=== Specific Error Handling ===");
        specificErrorHandling(100, "Alice");
        specificErrorHandling(100, "Bob"); // Duplicate
        
        System.out.println("\n=== Warning Handling ===");
        warningHandling();
        
        System.out.println("\n=== Retry Logic ===");
        try (Connection conn = connectWithRetry(3)) {
            System.out.println("Using connection: " + conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// Output (example):
// === Basic Error Handling ===
// Error: The statement was aborted because it would have caused a duplicate key value
// SQLState: 23505
// Error Code: 20000
//
// === Chained Exceptions ===
// SQLException caught:
//
// Exception 1:
//   Message: Duplicate key value
//   SQLState: 23505
//   Error Code: 20000
//
// === Specific Error Handling ===
// Employee added successfully
// Duplicate key: Employee ID 100 already exists
```

**Explanation:**
- `getMessage()` returns human-readable error message
- `getSQLState()` returns standardized 5-character code
- `getErrorCode()` returns database-specific integer code
- `getNextException()` returns chained exceptions (may be null)
- `SQLWarning` is retrieved via `getWarnings()`, not caught

### Exam Traps:

❌ **TRAP 1: SQLException Is Checked, Must Be Handled**
```java
public void badMethod() {
    Connection conn = DriverManager.getConnection(url); // COMPILE ERROR
    // SQLException is checked, must catch or declare throws
}

// CORRECT:
public void goodMethod() throws SQLException {
    Connection conn = DriverManager.getConnection(url);
}
```

❌ **TRAP 2: Closing Resources in Finally Without Try-Catch**
```java
Connection conn = null;
try {
    conn = DriverManager.getConnection(url);
} finally {
    conn.close(); // COMPILE ERROR if SQLException not caught
}

// CORRECT: Use try-with-resources
try (Connection conn = DriverManager.getConnection(url)) {
    // Use connection
}
```

❌ **TRAP 3: Catching Exception Instead of SQLException**
```java
try {
    Connection conn = DriverManager.getConnection(url);
} catch (Exception e) {  // Too broad
    // Catches ALL exceptions, not just SQL errors
}

// BETTER:
try {
    Connection conn = DriverManager.getConnection(url);
} catch (SQLException e) {  // Specific
    // Handle SQL errors specifically
}
```

❌ **TRAP 4: Ignoring getNextException() Chain**
```java
try {
    // Multiple SQL operations
} catch (SQLException e) {
    System.out.println(e.getMessage()); // Only prints FIRST exception
    // Missing chained exceptions!
}

// CORRECT:
catch (SQLException e) {
    while (e != null) {
        System.out.println(e.getMessage());
        e = e.getNextException();
    }
}
```

❌ **TRAP 5: SQLWarning Is NOT an Exception**
```java
try {
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM employees");
} catch (SQLWarning w) {  // COMPILE ERROR: SQLWarning is not Throwable
    // SQLWarning is NOT thrown, must be retrieved
}

// CORRECT:
SQLWarning warning = stmt.getWarnings();
if (warning != null) {
    // Handle warning
}
```

❌ **TRAP 6: Resource Leak in Exception Handling**
```java
Connection conn = DriverManager.getConnection(url);
try {
    PreparedStatement ps = conn.prepareStatement(sql);
    // Exception here leaves ps unclosed
} catch (SQLException e) {
    // Handle exception
}
// conn never closed!

// CORRECT: Nested try-with-resources
try (Connection conn = DriverManager.getConnection(url);
     PreparedStatement ps = conn.prepareStatement(sql)) {
    // Use resources
}
```

### Additional Context:

**Common SQLSTATE Codes:**
```
00000 = Successful completion
01xxx = Warning
02000 = No data found
08xxx = Connection exception
21xxx = Cardinality violation
22xxx = Data exception
23xxx = Integrity constraint violation
40xxx = Transaction rollback
42xxx = Syntax error or access rule violation
```

**Best Practices:**
1. Always use try-with-resources for JDBC resources
2. Log SQLState and error code for debugging
3. Handle specific SQLSTATE families differently
4. Chain exceptions to preserve full error context
5. Check for SQLWarnings after operations

---

## TOPIC 6: Understanding Transaction Management

### What: ACID Transactions

**Transaction** = Group of SQL operations that succeed or fail together

**ACID Properties:**
- **Atomicity**: All-or-nothing (commit or rollback)
- **Consistency**: Database remains in valid state
- **Isolation**: Transactions don't interfere with each other
- **Durability**: Committed changes persist

**Transaction Control Methods:**
```java
// Auto-commit mode (default)
conn.setAutoCommit(true);  // Each statement commits immediately
conn.setAutoCommit(false); // Manual transaction control

// Explicit commit/rollback
conn.commit();             // Make changes permanent
conn.rollback();           // Undo all changes since last commit

// Savepoints
Savepoint sp = conn.setSavepoint("pointA");
conn.rollback(sp);         // Rollback to savepoint only
```

### When to Use: Multi-Statement Operations

**Use transactions when:**
- Multiple related SQL operations must all succeed
- Need to undo changes if any operation fails
- Ensuring data consistency across tables
- Implementing business logic with multiple steps

**Auto-commit modes:**
```java
// Auto-commit ON (default)
conn.setAutoCommit(true);
stmt.executeUpdate("INSERT INTO accounts VALUES (1, 100)"); // Commits immediately
stmt.executeUpdate("INSERT INTO accounts VALUES (2, 200)"); // Commits immediately
// Each statement is its own transaction

// Auto-commit OFF (manual transaction)
conn.setAutoCommit(false);
stmt.executeUpdate("INSERT INTO accounts VALUES (1, 100)");
stmt.executeUpdate("INSERT INTO accounts VALUES (2, 200)");
conn.commit(); // Both inserts committed together
```

### Why Use: Data Integrity

**Transaction benefits:**
- Prevents partial updates (all-or-nothing)
- Maintains referential integrity
- Enables rollback on errors
- Improves performance (batch commits)

**Example problem without transactions:**
```java
// BAD: Auto-commit on, partial failure possible
conn.setAutoCommit(true);
stmt.executeUpdate("UPDATE accounts SET balance = balance - 100 WHERE id = 1"); // Succeeds
stmt.executeUpdate("UPDATE accounts SET balance = balance + 100 WHERE id = 2"); // Fails
// Money disappeared! First update committed, second failed
```

### Real-World Example: Bank Transfer System

```java
import java.sql.*;

public class BankTransactionExample {
    private static final String URL = "jdbc:derby:bank";
    
    // Transfer money between accounts (WITH transactions)
    public static boolean transferMoney(int fromAccount, int toAccount, double amount) {
        Connection conn = null;
        
        try {
            conn = DriverManager.getConnection(URL);
            
            // CRITICAL: Turn off auto-commit
            conn.setAutoCommit(false);
            
            // Step 1: Check sufficient funds
            String checkSQL = "SELECT balance FROM accounts WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSQL)) {
                ps.setInt(1, fromAccount);
                ResultSet rs = ps.executeQuery();
                
                if (!rs.next()) {
                    System.err.println("Source account not found");
                    conn.rollback();
                    return false;
                }
                
                double balance = rs.getDouble("balance");
                if (balance < amount) {
                    System.err.println("Insufficient funds: " + balance);
                    conn.rollback();
                    return false;
                }
            }
            
            // Step 2: Debit source account
            String debitSQL = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(debitSQL)) {
                ps.setDouble(1, amount);
                ps.setInt(2, fromAccount);
                int rows = ps.executeUpdate();
                
                if (rows == 0) {
                    System.err.println("Failed to debit source account");
                    conn.rollback();
                    return false;
                }
            }
            
            // Step 3: Credit destination account
            String creditSQL = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(creditSQL)) {
                ps.setDouble(1, amount);
                ps.setInt(2, toAccount);
                int rows = ps.executeUpdate();
                
                if (rows == 0) {
                    System.err.println("Failed to credit destination account");
                    conn.rollback(); // Undo debit from step 2
                    return false;
                }
            }
            
            // Step 4: Record transaction log
            String logSQL = "INSERT INTO transfer_log (from_acct, to_acct, amount, timestamp) " +
                           "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
            try (PreparedStatement ps = conn.prepareStatement(logSQL)) {
                ps.setInt(1, fromAccount);
                ps.setInt(2, toAccount);
                ps.setDouble(3, amount);
                ps.executeUpdate();
            }
            
            // All steps succeeded - commit transaction
            conn.commit();
            System.out.println("Transfer successful: $" + amount + 
                             " from account " + fromAccount + 
                             " to account " + toAccount);
            return true;
            
        } catch (SQLException e) {
            // Error occurred - rollback ALL changes
            System.err.println("Transfer failed: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("Transaction rolled back");
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            return false;
            
        } finally {
            // Restore auto-commit and close connection
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Restore default
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Example with Savepoints
    public static void demonstrateSavepoints() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            
            Statement stmt = conn.createStatement();
            
            // Initial operation
            stmt.executeUpdate("INSERT INTO accounts VALUES (10, 1000)");
            System.out.println("Inserted account 10");
            
            // Create savepoint
            Savepoint savepoint1 = conn.setSavepoint("BeforeAccount11");
            
            // Operation after savepoint
            stmt.executeUpdate("INSERT INTO accounts VALUES (11, 2000)");
            System.out.println("Inserted account 11");
            
            // Simulate error - rollback to savepoint
            System.out.println("Rolling back to savepoint...");
            conn.rollback(savepoint1);
            // Account 10 still inserted, account 11 rolled back
            
            // Continue with more operations
            stmt.executeUpdate("INSERT INTO accounts VALUES (12, 3000)");
            System.out.println("Inserted account 12");
            
            // Commit everything (accounts 10 and 12, not 11)
            conn.commit();
            System.out.println("Transaction committed");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Bad example - without transactions (DON'T DO THIS)
    public static void badTransferExample(int fromAccount, int toAccount, double amount) {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            
            // Auto-commit is ON by default
            // Each statement commits immediately!
            
            // Step 1: Debit source
            stmt.executeUpdate("UPDATE accounts SET balance = balance - " + amount + 
                             " WHERE id = " + fromAccount);
            // COMMITTED! Can't undo now!
            
            // Step 2: Credit destination
            // What if this fails? Money is lost!
            stmt.executeUpdate("UPDATE accounts SET balance = balance + " + amount + 
                             " WHERE id = " + toAccount);
            
            System.out.println("Transfer complete (maybe?)");
            
        } catch (SQLException e) {
            System.err.println("ERROR: Money may be lost! " + e.getMessage());
            // Can't rollback because auto-commit was on
        }
    }
    
    public static void main(String[] args) {
        // Good example with transactions
        System.out.println("=== Transfer with Transaction ===");
        boolean success = transferMoney(1, 2, 500.00);
        System.out.println("Success: " + success);
        
        // Demonstrate savepoints
        System.out.println("\n=== Savepoint Demo ===");
        demonstrateSavepoints();
        
        // Bad example (for educational purposes)
        System.out.println("\n=== Bad Example (No Transaction) ===");
        badTransferExample(1, 2, 100.00);
    }
}

// Output:
// === Transfer with Transaction ===
// Transfer successful: $500.0 from account 1 to account 2
// Success: true
//
// === Savepoint Demo ===
// Inserted account 10
// Inserted account 11
// Rolling back to savepoint...
// Inserted account 12
// Transaction committed
```

**Explanation:**
- `setAutoCommit(false)` starts manual transaction
- `commit()` makes all changes permanent
- `rollback()` undoes all changes since last commit
- `rollback(savepoint)` undoes changes to specific savepoint
- Transaction ensures all-or-nothing execution

### Exam Traps:

❌ **TRAP 1: Auto-Commit Default is TRUE**
```java
Connection conn = DriverManager.getConnection(url);
// Auto-commit is ON by default!
stmt.executeUpdate("UPDATE accounts SET balance = 100 WHERE id = 1"); // Commits immediately
conn.rollback(); // TOO LATE! Already committed
```

❌ **TRAP 2: Forgetting to Commit**
```java
conn.setAutoCommit(false);
stmt.executeUpdate("INSERT INTO accounts VALUES (1, 100)");
conn.close(); // Changes LOST! Never committed
```

❌ **TRAP 3: commit() After Auto-Commit is ON**
```java
conn.setAutoCommit(true);
stmt.executeUpdate("UPDATE accounts SET balance = 100");
conn.commit(); // Does nothing - already committed by auto-commit
```

❌ **TRAP 4: Savepoint Without Disabling Auto-Commit**
```java
Connection conn = DriverManager.getConnection(url);
// Auto-commit still ON
Savepoint sp = conn.setSavepoint(); // SQLException: savepoints require manual transactions
```

❌ **TRAP 5: Releasing Savepoint Incorrectly**
```java
conn.setAutoCommit(false);
Savepoint sp = conn.setSavepoint("sp1");
stmt.executeUpdate("INSERT INTO accounts VALUES (1, 100)");
conn.releaseSavepoint(sp); // Removes savepoint, but doesn't commit or rollback
// Changes still pending!
```

❌ **TRAP 6: Closing Connection Without Commit**
```java
conn.setAutoCommit(false);
stmt.executeUpdate("INSERT INTO accounts VALUES (1, 100)");
conn.close(); // Implicit rollback! Changes lost
// Should call conn.commit() first
```

### Additional Context:

**Transaction Isolation Levels:**
```java
// Not heavily tested on exam, but good to know
conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
```

**Best Practices:**
1. Always use transactions for multi-step operations
2. Set `autoCommit(false)` at start of transaction
3. Commit on success, rollback on error
4. Use try-catch-finally or try-with-resources
5. Restore auto-commit in finally block

**Savepoint Rules:**
- Savepoints only work when auto-commit is OFF
- Can have multiple savepoints
- Rolling back to savepoint keeps earlier changes
- Savepoint released on commit/rollback

---

## TOPIC 7: Using CallableStatement for Stored Procedures

### What: Calling Database Stored Procedures

**CallableStatement** extends PreparedStatement for calling stored procedures:
```
Statement
    ↓
PreparedStatement
    ↓
CallableStatement (for stored procedures)
```

**Key Features:**
```java
// Creating CallableStatement
String call = "{call procedure_name(?, ?)}";
CallableStatement cs = conn.prepareCall(call);

// With return value (function)
String func = "{? = call function_name(?)}";
CallableStatement cs = conn.prepareCall(func);

// Setting IN parameters (like PreparedStatement)
cs.setInt(1, 100);
cs.setString(2, "value");

// Registering OUT parameters
cs.registerOutParameter(1, Types.INTEGER);
cs.registerOutParameter(2, Types.VARCHAR);

// Executing
cs.execute();

// Getting OUT parameters
int result = cs.getInt(1);
String output = cs.getString(2);
```

### When to Use: Complex Database Logic

**Use CallableStatement when:**
- Database has existing stored procedures
- Complex business logic lives in database
- Need to call database functions
- Performance optimization (procedure is precompiled)

### Why Use: Database Encapsulation

**CallableStatement advantages:**
- Encapsulates complex logic in database
- Better performance (precompiled procedures)
- Centralized business rules

### Real-World Example: Employee Bonus Calculation

```java
import java.sql.*;

public class StoredProcedureExample {
    private static final String URL = "jdbc:derby:company";
    
    public static void updateEmployeeSalary(int employeeId, double newSalary) {
        String call = "{call update_salary(?, ?, ?)}";
        
        try (Connection conn = DriverManager.getConnection(URL);
             CallableStatement cs = conn.prepareCall(call)) {
            
            // Set IN parameters
            cs.setInt(1, employeeId);      // First ? - IN parameter
            cs.setDouble(2, newSalary);    // Second ? - IN parameter
            
            // Register OUT parameter
            cs.registerOutParameter(3, Types.DOUBLE); // Third ? - OUT parameter
            
            // Execute
            cs.execute();
            
            // Get old salary from OUT parameter
            double oldSalary = cs.getDouble(3);
            System.out.printf("Updated employee %d: $%.2f → $%.2f%n",
                            employeeId, oldSalary, newSalary);
            
        } catch (SQLException e) {
            System.err.println("Salary update failed: " + e.getMessage());
        }
    }
}
```

### Exam Traps:

❌ **TRAP 1: Not Registering OUT Parameters**
```java
CallableStatement cs = conn.prepareCall("{call get_count(?)}");
cs.execute();
int count = cs.getInt(1); // SQLException: Parameter not registered
```

❌ **TRAP 2: Wrong Parameter Order for Functions**
```java
String call = "{? = call calculate_bonus(?)}";
CallableStatement cs = conn.prepareCall(call);
cs.setInt(1, 100);  // WRONG - position 1 is return value!
// CORRECT: cs.registerOutParameter(1, Types.DOUBLE); then cs.setInt(2, 100);
```

❌ **TRAP 3: Missing Curly Braces**
```java
String call = "call procedure_name(?)"; // WRONG - missing { }
// CORRECT: "{call procedure_name(?)}"
```

### Additional Context:

**JDBC Escape Syntax:**
```java
{call procedure_name}              // No parameters
{call procedure_name(?)}           // One parameter
{? = call function_name(?)}        // Function with return value
```

---

## TOPIC 8: ResultSet Types and Concurrency

### What: ResultSet Configuration Options

**ResultSet Type** determines scrolling capability:
```java
// Type options
ResultSet.TYPE_FORWARD_ONLY        // Default, next() only
ResultSet.TYPE_SCROLL_INSENSITIVE  // Scrollable, doesn't see DB changes
ResultSet.TYPE_SCROLL_SENSITIVE    // Scrollable, sees DB changes (rarely supported)
```

**ResultSet Concurrency** determines update capability:
```java
// Concurrency options
ResultSet.CONCUR_READ_ONLY         // Default, can't update
ResultSet.CONCUR_UPDATABLE         // Can update/insert/delete rows
```

**Creating with Options:**
```java
PreparedStatement ps = conn.prepareStatement(
    sql,
    ResultSet.TYPE_SCROLL_INSENSITIVE,
    ResultSet.CONCUR_UPDATABLE
);
```

### When to Use: Based on Requirements

**TYPE_FORWARD_ONLY** (default):
- Most common and efficient
- Use when processing rows once

**TYPE_SCROLL_INSENSITIVE:**
- Need to navigate backwards
- Need random access (absolute(), relative())

**CONCUR_UPDATABLE:**
- Need to update rows through ResultSet

### Why Use: Control Behavior and Performance

**Trade-offs:**
- FORWARD_ONLY: Best performance
- SCROLL_INSENSITIVE: More flexible, more memory
- UPDATABLE: Slower, more complex

### Real-World Example: Different ResultSet Configurations

```java
import java.sql.*;

public class ResultSetTypesExample {
    private static final String URL = "jdbc:derby:company";
    
    public static void scrollableExample() {
        String sql = "SELECT id, name, salary FROM employees ORDER BY salary DESC";
        
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement(
                 ResultSet.TYPE_SCROLL_INSENSITIVE,
                 ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // Go to last row
            rs.last();
            System.out.println("Last: " + rs.getString("name"));
            
            // Go to first row
            rs.first();
            System.out.println("First: " + rs.getString("name"));
            
            // Go to specific row (1-indexed)
            rs.absolute(3); // 3rd row
            System.out.println("3rd: " + rs.getString("name"));
            
            // Relative movement
            rs.relative(2); // Move forward 2 rows
            System.out.println("After +2: " + rs.getString("name"));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

### Exam Traps:

❌ **TRAP 1: Calling previous() on FORWARD_ONLY**
```java
Statement stmt = conn.createStatement(); // Default = FORWARD_ONLY
ResultSet rs = stmt.executeQuery("SELECT * FROM employees");
rs.next();
rs.previous(); // SQLException: TYPE_FORWARD_ONLY doesn't support previous()
```

❌ **TRAP 2: Update Methods on READ_ONLY**
```java
Statement stmt = conn.createStatement(
    ResultSet.TYPE_SCROLL_INSENSITIVE,
    ResultSet.CONCUR_READ_ONLY);
ResultSet rs = stmt.executeQuery(sql);
rs.next();
rs.updateInt("salary", 100000); // SQLException: ResultSet not updatable
```

### Additional Context:

**Default is TYPE_FORWARD_ONLY + CONCUR_READ_ONLY**

---

## TOPIC 9: Scrollable and Updatable ResultSets

### What: Modifying Data Through ResultSet

**Updatable ResultSet** can modify database directly:
```java
Statement stmt = conn.createStatement(
    ResultSet.TYPE_SCROLL_INSENSITIVE,
    ResultSet.CONCUR_UPDATABLE);
ResultSet rs = stmt.executeQuery("SELECT id, name, salary FROM employees");

// Update existing row
rs.next();
rs.updateDouble("salary", 100000.00);
rs.updateRow();

// Insert new row
rs.moveToInsertRow();
rs.updateInt("id", 999);
rs.updateString("name", "New Employee");
rs.insertRow();
rs.moveToCurrentRow();

// Delete current row
rs.absolute(3);
rs.deleteRow();
```

### When to Use: Iterative Row Modifications

**Use updatable ResultSet when:**
- Processing rows and updating based on current values
- Complex update logic easier in Java
- Need to insert/delete while iterating

### Why Use: Convenient for Complex Updates

**Advantages:**
- Update based on current values without re-querying
- Insert rows while maintaining position

**Disadvantages:**
- Slower than direct UPDATE statements
- More complex error handling

### Real-World Example: Salary Adjustment System

```java
import java.sql.*;

public class UpdatableResultSetExample {
    private static final String URL = "jdbc:derby:company";
    
    public static void giveRaises(double percentage) {
        String sql = "SELECT id, name, salary FROM employees WHERE dept = 'Engineering'";
        
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement(
                 ResultSet.TYPE_SCROLL_INSENSITIVE,
                 ResultSet.CONCUR_UPDATABLE);
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                double oldSalary = rs.getDouble("salary");
                double newSalary = oldSalary * (1 + percentage / 100);
                
                // Update salary in ResultSet
                rs.updateDouble("salary", newSalary);
                
                // Write changes to database
                rs.updateRow();
                
                System.out.printf("Updated: $%.2f → $%.2f%n", oldSalary, newSalary);
            }
            
        } catch (SQLException e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }
}
```

### Exam Traps:

❌ **TRAP 1: Forgetting updateRow() After updateXxx()**
```java
ResultSet rs = stmt.executeQuery(sql);
rs.next();
rs.updateDouble("salary", 100000);
// Forgot rs.updateRow() - changes LOST!
rs.next();
```

❌ **TRAP 2: Not Moving to Insert Row**
```java
ResultSet rs = stmt.executeQuery(sql);
rs.updateInt("id", 999);
rs.insertRow(); // SQLException: Not on insert row
// MUST call rs.moveToInsertRow() first
```

❌ **TRAP 3: Query Not Updatable**
```java
// JOINs, DISTINCT often not updatable
String sql = "SELECT DISTINCT name FROM employees";
// ResultSet won't be updatable even if requested
```

### Additional Context:

**Requirements for Updatable ResultSet:**
- Query must reference single table
- Must include primary key in SELECT
- No JOINs, GROUP BY, DISTINCT

---

## TOPIC 10: Batch Updates

### What: Executing Multiple SQL Statements Together

**Batch Updates** execute multiple SQL statements as a group:
```java
Statement stmt = conn.createStatement();

// Add statements to batch
stmt.addBatch("INSERT INTO employees VALUES (1, 'Alice', 80000)");
stmt.addBatch("INSERT INTO employees VALUES (2, 'Bob', 90000)");

// Execute all at once
int[] results = stmt.executeBatch();

// Clear batch
stmt.clearBatch();
```

**PreparedStatement Batch:**
```java
String sql = "INSERT INTO employees (id, name, salary) VALUES (?, ?, ?)";
PreparedStatement ps = conn.prepareStatement(sql);

ps.setInt(1, 1);
ps.setString(2, "Alice");
ps.setDouble(3, 80000);
ps.addBatch();

ps.setInt(1, 2);
ps.setString(2, "Bob");
ps.setDouble(3, 90000);
ps.addBatch();

int[] results = ps.executeBatch();
```

### When to Use: Multiple Similar Operations

**Use batch updates when:**
- Inserting many rows
- Updating many rows with different values
- Performance is critical

### Why Use: Performance Optimization

**Performance benefits:**
- Reduces network round-trips
- 10-100x faster for large datasets

### Real-World Example: Bulk Data Import

```java
import java.sql.*;

public class BatchUpdateExample {
    private static final String URL = "jdbc:derby:company";
    
    public static void bulkInsert(int count) {
        String sql = "INSERT INTO employees (id, name, salary) VALUES (?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (int i = 0; i < count; i++) {
                ps.setInt(1, i);
                ps.setString(2, "Employee" + i);
                ps.setDouble(3, 50000 + i*100);
                ps.addBatch();
                
                // Execute batch every 1000 rows
                if (i % 1000 == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            
            // Execute remaining
            ps.executeBatch();
            conn.commit();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

### Exam Traps:

❌ **TRAP 1: Forgetting executeBatch()**
```java
PreparedStatement ps = conn.prepareStatement(sql);
for (int i = 0; i < 100; i++) {
    ps.setInt(1, i);
    ps.addBatch();
}
// Forgot ps.executeBatch() - nothing happens!
```

❌ **TRAP 2: Using execute() or executeUpdate()**
```java
stmt.addBatch("INSERT ...");
stmt.executeUpdate(); // WRONG - use executeBatch()
```

❌ **TRAP 3: Not Using Transactions**
```java
// Auto-commit ON - each statement commits individually
conn.setAutoCommit(true);
stmt.executeBatch();
// Should use setAutoCommit(false) and commit()
```

### Additional Context:

**Optimal Batch Size:** 1000-10000 statements

**Return Values:**
```java
int[] results = stmt.executeBatch();
// results[i] >= 0 : Number of rows affected
// results[i] == Statement.SUCCESS_NO_INFO
// results[i] == Statement.EXECUTE_FAILED
```

---

## TOPIC 11: Understanding JDBC Drivers

### What: JDBC Driver Types

**Four Driver Types:**

**Type 1: JDBC-ODBC Bridge** (Removed in Java 8)

**Type 2: Native-API Driver**
- Uses database native client library
- Platform-dependent

**Type 3: Network Protocol Driver**
- Pure Java, goes through middleware

**Type 4: Pure Java Driver** (Most common)
- 100% Java, no native code
- Platform-independent
- Best performance
- Examples: Derby, MySQL, PostgreSQL

### When to Use: Based on Requirements

**Type 4 (Recommended):**
- New applications
- Platform independence
- Best performance

### Why Use: Database Connectivity

**DriverManager vs DataSource:**
```java
// DriverManager (exam focus)
Connection conn = DriverManager.getConnection(url, user, pass);

// DataSource (production)
DataSource ds = // ... get from JNDI
Connection conn = ds.getConnection();
```

### Real-World Example: Driver Usage

```java
import java.sql.*;

public class JDBCDriverExample {
    
    public static void modernDriverLoading() {
        // JDBC 4.0+ automatically loads drivers
        String url = "jdbc:derby:mydb;create=true";
        
        try (Connection conn = DriverManager.getConnection(url)) {
            System.out.println("Connected using auto-loaded driver");
            
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("Driver: " + meta.getDriverName());
            System.out.println("Version: " + meta.getDriverVersion());
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void demonstrateURLFormats() {
        // Derby Embedded
        String derby = "jdbc:derby:mydb;create=true";
        
        // MySQL
        String mysql = "jdbc:mysql://localhost:3306/mydb";
        
        // PostgreSQL
        String postgres = "jdbc:postgresql://localhost:5432/mydb";
        
        // Oracle Thin (Type 4)
        String oracle = "jdbc:oracle:thin:@localhost:1521:orcl";
    }
}
```

### Exam Traps:

❌ **TRAP 1: Thinking Class.forName() is Required**
```java
// NOT NEEDED in Java 6+ (JDBC 4.0+)
Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
// Drivers auto-load since JDBC 4.0
```

❌ **TRAP 2: Wrong URL Format**
```java
String url = "derby:mydb"; // MISSING jdbc: prefix
// CORRECT: "jdbc:derby:mydb"
```

### Additional Context:

**JDBC 4.0+ Auto-Loading:**
- Drivers automatically registered from classpath
- `Class.forName()` not needed since Java 6

**Exam Focus:**
- Type 4 = pure Java, most common
- Type 1 removed in Java 8
- JDBC URL format: `jdbc:<subprotocol>:<subname>`

---

## TOPIC 12: Using RowSet Interfaces

### What: Disconnected ResultSet Alternative

**RowSet** is a wrapper around ResultSet:
```
ResultSet
    ↑
RowSet (interface)
    ↑
    ├── JdbcRowSet (connected)
    └── CachedRowSet (disconnected)
```

**Key Differences:**
```java
// ResultSet - connected
ResultSet rs = stmt.executeQuery(sql);

// CachedRowSet - disconnected
CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
crs.setCommand(sql);
crs.execute();
conn.close(); // Can close connection!
crs.next(); // Still works - data cached
```

### When to Use: Specific Scenarios

**JdbcRowSet** (Connected):
- Similar to ResultSet but scrollable/updatable by default

**CachedRowSet** (Disconnected):
- Need to disconnect from database
- Work with data offline
- Serialize data over network

### Why Use: Enhanced Functionality

**RowSet advantages:**
- Scrollable/updatable by default
- Disconnected operation (CachedRowSet)
- Serializable

### Real-World Example: CachedRowSet Usage

```java
import java.sql.*;
import javax.sql.rowset.*;

public class RowSetExample {
    private static final String URL = "jdbc:derby:company";
    
    public static CachedRowSet getCachedEmployeeData() {
        try {
            CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
            
            crs.setUrl(URL);
            crs.setCommand("SELECT id, name, salary FROM employees");
            
            // Execute and populate cache
            crs.execute();
            
            // Connection automatically closed
            System.out.println("Data cached, connection closed");
            
            return crs;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void workWithDisconnectedData() {
        CachedRowSet crs = getCachedEmployeeData();
        
        try {
            // Work with cached data (no connection needed)
            while (crs.next()) {
                System.out.printf("ID: %d, Name: %s%n",
                                crs.getInt("id"),
                                crs.getString("name"));
            }
            
            // Modify data offline
            crs.first();
            crs.updateDouble("salary", 95000);
            crs.updateRow();
            
            // Reconnect and sync changes
            crs.acceptChanges();
            System.out.println("Changes synchronized");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

### Exam Traps:

❌ **TRAP 1: Forgetting execute()**
```java
CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
crs.setCommand(sql);
crs.next(); // SQLException: No data
// Must call crs.execute() first
```

❌ **TRAP 2: JdbcRowSet Needs Connection Info**
```java
JdbcRowSet jrs = RowSetProvider.newFactory().createJdbcRowSet();
jrs.setCommand(sql);
jrs.execute(); // SQLException: No URL set
```

### Additional Context:

**Key Methods:**
```java
setUrl(String)           // Set JDBC URL
setCommand(String)       // Set SQL query
execute()                // Execute and populate
acceptChanges()          // Sync changes (CachedRowSet)
```

**Exam Focus:**
- CachedRowSet most important
- Know it's disconnected
- acceptChanges() syncs to database
- **Not heavily tested**

---

## 📝 COMPREHENSIVE SUMMARY

### Key APIs to Memorize

**Connection Methods:**
```java
setAutoCommit(boolean)
commit()
rollback()
setSavepoint(String)
```

**PreparedStatement Methods:**
```java
setInt(int, int)
setString(int, String)
executeQuery()
executeUpdate()
```

**ResultSet Methods:**
```java
next()
getInt(int/String)
getString(int/String)
wasNull()
```

### Exam Pattern Recognition

**Common question types:**
1. Transaction management (commit/rollback)
2. PreparedStatement parameter setting
3. ResultSet navigation and column access
4. SQLException handling
5. Resource closing (try-with-resources)

**Traps to watch for:**
- 1-indexed parameters and columns
- Auto-commit default is TRUE
- Using wrong execute method
- Forgetting to call next() before reading ResultSet
- Resource leaks

---

## ❓ EXAM QUESTIONS (20 Questions)

### Question 1
```java
String url = "jdbc:derby:zoo";
Connection conn = DriverManager.getConnection(url);
PreparedStatement ps = conn.prepareStatement("SELECT * FROM animals WHERE id = ?");
ps.setInt(0, 100);
ResultSet rs = ps.executeQuery();
```
What is the result?
A. Code compiles and runs
B. SQLException at line with setInt
C. SQLException at line with executeQuery
D. Compile error

**Answer**: B - Parameters are 1-indexed, not 0-indexed

---

### Question 2
```java
try (Connection conn = DriverManager.getConnection(url)) {
    conn.setAutoCommit(false);
    Statement stmt = conn.createStatement();
    stmt.executeUpdate("INSERT INTO accounts VALUES (1, 100)");
    stmt.executeUpdate("INSERT INTO accounts VALUES (2, 200)");
}
```
What happens to the inserts?
A. Both committed
B. Both rolled back
C. First committed, second rolled back
D. Compile error

**Answer**: B - Connection closes without commit, triggers rollback

---

### Question 3
```java
String sql = "SELECT * FROM employees WHERE id = ? AND dept = ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setInt(2, 10);
ps.setString(1, "IT");
ResultSet rs = ps.executeQuery();
```
What is the result?
A. Code compiles and runs correctly
B. SQLException at setInt
C. SQLException at setString
D. SQLException at executeQuery

**Answer**: D - Parameter 2 set before parameter 1, but query executes with parameters in wrong order

---

### Question 4
```java
Connection conn = DriverManager.getConnection(url);
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT id FROM employees");
int id = rs.getInt(1);
```
What is the result?
A. First employee ID retrieved
B. SQLException: cursor before first row
C. SQLException: invalid column index
D. Compile error

**Answer**: B - Must call rs.next() before reading data

---

### Question 5
```java
try (Connection conn = DriverManager.getConnection(url);
     PreparedStatement ps = conn.prepareStatement("INSERT INTO emp VALUES (?, ?)")) {
    ps.setInt(1, 100);
    ps.setString(2, "Alice");
    int result = ps.executeQuery();
}
```
What prevents compilation?
A. executeQuery() returns ResultSet, not int
B. PreparedStatement not closeable
C. Connection requires username/password
D. INSERT requires executeUpdate()

**Answer**: A - executeQuery() returns ResultSet, cannot assign to int

---

### Question 6
```java
conn.setAutoCommit(false);
Statement stmt = conn.createStatement();
stmt.executeUpdate("UPDATE accounts SET balance = 1000 WHERE id = 1");
conn.setAutoCommit(true);
```
What happens to the update?
A. Committed when setAutoCommit(true) is called
B. Rolled back when setAutoCommit(true) is called
C. Requires explicit commit() call
D. SQLException thrown

**Answer**: A - Setting auto-commit to true commits pending transaction

---

### Question 7
```java
String sql = "SELECT name FROM employees";
PreparedStatement ps = conn.prepareStatement(sql,
    ResultSet.TYPE_FORWARD_ONLY,
    ResultSet.CONCUR_UPDATABLE);
ResultSet rs = ps.executeQuery();
rs.next();
rs.updateString("name", "New Name");
rs.updateRow();
```
What is the result?
A. Name updated successfully
B. SQLException: TYPE_FORWARD_ONLY not updatable
C. SQLException: query missing primary key
D. Code runs but changes not saved

**Answer**: C - Updatable ResultSet requires primary key in SELECT

---

### Question 8
```java
CallableStatement cs = conn.prepareCall("{call get_employee_count(?)}");
cs.execute();
int count = cs.getInt(1);
```
What is the result?
A. Employee count retrieved
B. SQLException: parameter not registered
C. SQLException: execute not callable for procedures
D. Compile error

**Answer**: B - Must call registerOutParameter before execute

---

### Question 9
```java
Statement stmt = conn.createStatement();
stmt.addBatch("INSERT INTO emp VALUES (1, 'Alice')");
stmt.addBatch("INSERT INTO emp VALUES (2, 'Bob')");
stmt.execute();
```
What is the result?
A. Both inserts executed
B. First insert executed
C. SQLException: must use executeBatch
D. No inserts executed

**Answer**: D - execute() doesn't execute batch, need executeBatch()

---

### Question 10
```java
PreparedStatement ps = conn.prepareStatement(
    "SELECT * FROM employees",
    ResultSet.TYPE_SCROLL_INSENSITIVE,
    ResultSet.CONCUR_READ_ONLY);
ResultSet rs = ps.executeQuery();
rs.last();
rs.updateInt("salary", 100000);
```
What is the result?
A. Salary updated
B. SQLException: cannot update READ_ONLY ResultSet
C. SQLException: last() not supported
D. Compile error

**Answer**: B - CONCUR_READ_ONLY cannot be updated

---

### Question 11
```java
String url = "derby:mydb";
Connection conn = DriverManager.getConnection(url);
```
What is the result?
A. Connection established
B. SQLException: invalid URL format
C. ClassNotFoundException: driver not found
D. Compile error

**Answer**: B - Missing "jdbc:" prefix in URL

---

### Question 12
```java
Connection conn = DriverManager.getConnection(url);
conn.setAutoCommit(false);
Savepoint sp1 = conn.setSavepoint();
stmt.executeUpdate("INSERT INTO emp VALUES (1, 'Alice')");
Savepoint sp2 = conn.setSavepoint();
stmt.executeUpdate("INSERT INTO emp VALUES (2, 'Bob')");
conn.rollback(sp1);
conn.commit();
```
What is in the database after commit?
A. Both Alice and Bob
B. Only Alice
C. Only Bob
D. Neither

**Answer**: D - Rollback to sp1 removes both inserts (Bob added after sp1)

---

### Question 13
```java
ResultSet rs = stmt.executeQuery("SELECT id, name FROM employees");
while (rs.next()) {
    System.out.println(rs.getString(1) + ", " + rs.getInt(2));
}
```
What is the result?
A. Prints employee IDs and names
B. SQLException: column 2 is String, not int
C. ClassCastException
D. Prints names and IDs (switched)

**Answer**: B - Column 1 is id (int), column 2 is name (String); getString(1) works, getInt(2) fails

---

### Question 14
```java
PreparedStatement ps = conn.prepareStatement("DELETE FROM emp WHERE id = ?");
ps.setInt(1, 100);
ps.addBatch();
ps.setInt(1, 200);
ps.addBatch();
int[] results = ps.executeBatch();
```
How many employees deleted if both IDs exist?
A. 0
B. 1
C. 2
D. SQLException

**Answer**: C - Batch contains 2 DELETE statements

---

### Question 15
```java
CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
crs.setCommand("SELECT * FROM employees");
crs.execute();
```
What is the result?
A. Data retrieved successfully
B. SQLException: URL not set
C. SQLException: connection required
D. Compile error

**Answer**: B - CachedRowSet needs setUrl() before execute()

---

### Question 16
```java
Statement stmt = conn.createStatement(
    ResultSet.TYPE_SCROLL_INSENSITIVE,
    ResultSet.CONCUR_UPDATABLE);
ResultSet rs = stmt.executeQuery("SELECT * FROM employees");
rs.absolute(5);
String name = rs.getString("name");
```
What is the result?
A. Name of 5th employee retrieved
B. SQLException: absolute not supported
C. SQLException: row 5 doesn't exist
D. Name of 4th employee retrieved

**Answer**: A - absolute(5) moves to 5th row (1-indexed)

---

### Question 17
```java
try {
    conn.setAutoCommit(false);
    stmt.executeUpdate("INSERT INTO emp VALUES (1, 'Alice')");
    int result = 10 / 0;  // ArithmeticException
    conn.commit();
} catch (ArithmeticException e) {
    conn.rollback();
}
```
What is in the database?
A. Alice inserted
B. No data inserted
C. SQLException thrown
D. Compile error

**Answer**: B - Exception thrown before commit, rollback removes insert

---

### Question 18
```java
PreparedStatement ps = conn.prepareStatement(
    "INSERT INTO emp VALUES (?, ?)",
    Statement.RETURN_GENERATED_KEYS);
ps.setInt(1, 100);
ps.setString(2, "Alice");
ps.executeUpdate();
ResultSet keys = ps.getGeneratedKeys();
int id = keys.getInt(1);
```
What is the result?
A. Generated ID retrieved
B. SQLException: keys.next() not called
C. SQLException: no generated keys
D. Returns 100

**Answer**: B - Must call keys.next() before reading

---

### Question 19
```java
Connection conn = DriverManager.getConnection(url);
Statement stmt = conn.createStatement();
boolean result = stmt.execute("UPDATE emp SET salary = 100000");
```
What does result contain?
A. true
B. false
C. SQLException
D. Depends on rows updated

**Answer**: B - execute() returns false for UPDATE (no ResultSet)

---

### Question 20
```java
CallableStatement cs = conn.prepareCall("{? = call calculate_bonus(?)}");
cs.setInt(1, 100);
cs.registerOutParameter(2, Types.DOUBLE);
cs.execute();
double bonus = cs.getDouble(2);
```
What is the result?
A. Bonus calculated correctly
B. SQLException: wrong parameter positions
C. SQLException: OUT before IN
D. Compile error

**Answer**: B - Position 1 is return value (OUT), position 2 is parameter (IN); should be registerOutParameter(1) and setInt(2)

---

---

## 🎯 FINAL TIPS FOR EXAM

1. **JDBC is 10-15% of Java 17 exam** (not in Java 21)
2. **Remember 1-indexed** parameters and columns
3. **Auto-commit is ON by default**
4. **PreparedStatement preferred** over Statement
5. **Try-with-resources** for automatic closing
6. **executeQuery() for SELECT**, executeUpdate() for modifications
7. **Always call next()** before reading ResultSet
8. **Transactions require setAutoCommit(false)**

**Most tested topics:**
- PreparedStatement parameter setting
- Transaction management (commit/rollback)
- ResultSet navigation
- Exception handling
- Resource management

**End of Chapter 11**

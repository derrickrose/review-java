# JAVA SE 21 CERTIFICATION (1Z0-830)
# 22 Questions Gratuites - MyExamCloud
# Source Légale et Publique

---

## QUESTION 1 - Streams & Lambda

**Topic:** Working with Streams and Lambda expressions

**Code:**
```java
1. import java.util.stream.DoubleStream;
2.
3. public class MyExamCloud {
4.
5.   public static void main(String[] args){
6.
7.     var doubles = DoubleStream.of(0.45, 0.42, 0.49);
8.     System.out.print(doubles.filter(e -> e > 0.45).count());
9.   }
10. }
```

**Question:** What is the output?

**Choices:**
- A) 1
- B) 2
- C) Compilation fails since we haven't imported predicate
- D) Compilation fails due to error at line 7
- E) Compilation fails due to error at line 8

**✅ RÉPONSE CORRECTE: A**

**📝 EXPLICATION:**
Le stream contient 3 valeurs: 0.45, 0.42, 0.49
Le filter garde seulement les valeurs > 0.45 (strictement supérieur)
- 0.45 > 0.45 ? NON
- 0.42 > 0.45 ? NON
- 0.49 > 0.45 ? OUI ✅

Résultat: count() = 1

**⚠️ PIÈGE:** 0.45 n'est PAS > 0.45 (égalité). Si c'était >= 0.45, la réponse serait 2.

---

## QUESTION 2 - Modules

**Topic:** Packaging and Deploying Java Code

**Question:** 
Given that a class named `com.myexamcloud.exam.Exam` is part of a module named `com.myexamcloud.exam` packaged in `myexam.jar`, which of the following commands can be used to execute this class? (Assume that the jar file is located in the current directory.)

**Choices:**
- A) `java --module-path myexam.jar --module com.myexamcloud.exam.Exam`
- B) `java --module-path myexam.jar --module com.myexamcloud.exam com.myexamcloud.exam.Exam`
- C) `java --module-path myexam.jar --module com.myexamcloud.exam --main-class com.myexamcloud.exam.Exam`
- D) `java --module-path myexam.jar --module com.myexamcloud.exam/com.myexamcloud.exam.Exam`
- E) `java --module-path . --module com.myexamcloud.exam/com.myexamcloud.exam.Exam`

**✅ RÉPONSE CORRECTE: D, E (SELECT 2)**

**📝 EXPLICATION:**

**Syntaxe correcte pour exécuter un module:**
```bash
java --module-path <path> --module <module-name>/<main-class>
```

**D)** ✅ Correct: `--module-path myexam.jar --module com.myexamcloud.exam/com.myexamcloud.exam.Exam`
- module-path pointe directement sur le JAR

**E)** ✅ Correct: `--module-path . --module com.myexamcloud.exam/com.myexamcloud.exam.Exam`
- module-path pointe sur le répertoire courant (.) qui contient myexam.jar

**A)** ❌ Mauvaise syntaxe: pas de "/" entre module et classe
**B)** ❌ Mauvaise syntaxe: classe après le --module
**C)** ❌ --main-class n'existe pas pour la ligne de commande

**⚠️ PIÈGE:** Le slash "/" est OBLIGATOIRE entre le nom du module et la classe principale.

---

## QUESTION 3 - Concurrency & Thread Safety

**Topic:** Manage concurrent code execution

**Code:**
```java
record Account(String accNumber, double balance) {
    static java.util.concurrent.locks.ReentrantLock lock = 
        new java.util.concurrent.locks.ReentrantLock();

    public double withdraw(double amt) {
        double latestBalance = 0;
        try {
            lock.lock();
            if (balance > amt) latestBalance = balance - amt;
        } finally {
            lock.unlock();
        }
        return latestBalance;
    }
}
```

**Question:** What can be done to make the above code thread safe?

**Choices:**
- A) Change new ReentrantLock() to new ReentrantLock(true)
- B) Make the lock variable as protected
- C) Make the lock variable protected, final, and static
- D) Move the call to lock.lock() to before the try block
- E) Make the lock variable final
- F) No change is required
- G) Declare lock variable as private and final

**✅ RÉPONSE CORRECTE: G**

**📝 EXPLICATION:**
Pour que le code soit thread-safe, le lock doit être:
1. **private** - éviter l'accès externe
2. **final** - empêcher la réaffectation (sécurité)
3. **static** - déjà présent, partage le lock entre toutes les instances

**Actuellement:** `static ReentrantLock lock` (package-private)
**Devrait être:** `private static final ReentrantLock lock`

**Pourquoi G est correct:**
- ✅ private: encapsulation
- ✅ final: immutabilité du lock
- ✅ static: déjà présent

**Pourquoi les autres sont faux:**
- **A)** ReentrantLock(true) = fair lock, mais ne résout pas le problème de visibilité
- **B)** protected ne suffit pas, il faut private
- **D)** lock.lock() avant try = risque de deadlock si exception
- **F)** Non, le lock n'est pas private final

**⚠️ PIÈGE:** La position du lock.lock() dans le try est correcte ! Ne pas le déplacer.

---

## QUESTION 4 - I/O Streams

**Topic:** Use Java I/O API

**Code:**
```java
import java.io.*;
class MyExamCloud {
    public static void main(String[] args) throws Exception {
        try (var bfr = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Enter Your Name:");
            var s = bfr.readLine();
            System.out.println("Your Name is : " + s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

**Question:** What will be the output if the above code is executed using the following command: `java MyExamCloud Joe`

**Choices:**
- A) Enter Your Name:\nYour Name is: MyExamCloud Joe
- B) An exception stack trace will be printed
- C) Enter Your Name:\nYour Name is : null
- D) Enter Your Name:
- E) Enter Your Name:\nYour Name is : Joe

**✅ RÉPONSE CORRECTE: D**

**📝 EXPLICATION:**
**PIÈGE MAJEUR:** Arguments de ligne de commande ≠ System.in

Quand tu exécutes `java MyExamCloud Joe`:
- "Joe" est un **argument de ligne de commande** (String[] args)
- System.in lit depuis **l'entrée standard** (clavier)
- Ces deux choses sont DIFFÉRENTES !

**Déroulement:**
1. Programme affiche "Enter Your Name:"
2. Programme attend une entrée depuis System.in (clavier)
3. Comme rien n'est fourni sur l'entrée standard, le programme **se bloque**
4. L'utilisateur devrait taper quelque chose manuellement

**Résultat:** Seul "Enter Your Name:" est affiché, puis le programme attend.

**Pour que "Joe" soit utilisé, il faudrait:**
```java
String s = args.length > 0 ? args[0] : bfr.readLine();
```

**⚠️ PIÈGE:** Ne pas confondre args[] et System.in !

---

## QUESTION 5 - Pattern Matching avec instanceof

**Topic:** Controlling Program Flow

**Question:** Which of the following Java SE 21 code snippets compile without any error? The variable obj is of type java.lang.Object.

**Choices:**

**A)**
```java
if (obj instanceof String name & name.length() > 10) {
    var xPosition = 10;
    var yPosition = 0;
}
```

**B)**
```java
if (obj instanceof String name && name.length() > 10) {
    var xPosition = 10;
    var yPosition = 0;
}
```

**C)**
```java
if (obj instanceof String name | name.length() > 10) {
    var xPosition = 10;
    var yPosition = 0;
}
```

**D)** None of above

**✅ RÉPONSE CORRECTE: B**

**📝 EXPLICATION:**

**Pattern matching avec instanceof:**
```java
if (obj instanceof String name) {
    // name est disponible ici
}
```

**Opérateurs logiques:**
- `&&` = short-circuit AND (évaluation paresseuse) ✅
- `&` = bitwise/boolean AND (évaluation complète) ❌
- `||` = short-circuit OR ✅
- `|` = bitwise/boolean OR ❌

**B)** ✅ Compile: `&&` vérifie d'abord instanceof, puis name.length()
- Si obj n'est pas String, la 2ème partie n'est PAS évaluée
- Pas de problème de scope

**A)** ❌ Ne compile pas: `&` évalue LES DEUX côtés
- name.length() est évalué même si obj n'est pas String
- Erreur de compilation: name n'est pas en scope

**C)** ❌ Ne compile pas: `|` = OR, logique incorrecte + problème de scope

**⚠️ PIÈGE:** Utiliser TOUJOURS `&&` avec pattern matching instanceof, JAMAIS `&` !

---

## QUESTION 6 - Switch Pattern Matching

**Topic:** Controlling Program Flow

**Code:**
```java
//Saved as MyExamCloud.java
class Account {}
class StandardAccount extends Account {}
class PremiumAccount extends Account {
    int monthsRemaining() {
        return 10;
    }
}

public class MyExamCloud {
    record Point(int i, int j) {}

    static void testType(Object o) {
        switch (o) {
            case null -> System.out.println("null");
            case String s -> System.out.println("String");
            case StandardAccount a -> System.out.println("Account");
            case PremiumAccount p -> System.out.println("PremiumAccount");
            case int[] ia -> System.out.println("Array");
            default -> System.out.println("Something else");
        }
    }

    public static void main(String as[]) {
        testType(new PremiumAccount());
        testType(new StandardAccount());
        testType(new Account());
        testType(null);
        testType("");
        testType(new int[]{1, 2, 3, 4, 5});
    }
}
```

**Question:** What is NOT true about MyExamCloud.java when you compile and run the above code?

**Choices:**
- A) The code may print both Account and PremiumAccount
- B) The code will never print Something else
- C) The code may print both Array and Something else
- D) The code may print null and String

**✅ RÉPONSE CORRECTE: B**

**📝 EXPLICATION:**

**Exécution réelle:**
```
testType(new PremiumAccount())     → "PremiumAccount"
testType(new StandardAccount())    → "Account"
testType(new Account())            → "Something else" ✅
testType(null)                     → "null"
testType("")                       → "String"
testType(new int[]{1,2,3,4,5})    → "Array"
```

**Pourquoi "Something else" s'affiche:**
- `new Account()` ne match NI StandardAccount NI PremiumAccount
- Tombe dans le `default` → "Something else"

**Analyse des affirmations:**
- **A)** ❌ FAUX - Ne peut pas imprimer les deux (un seul case par appel)
- **B)** ✅ PAS VRAI - Imprime "Something else" pour Account !
- **C)** ❌ FAUX - Ne peut pas imprimer les deux (un seul case)
- **D)** ❌ FAUX - Ne peut pas imprimer les deux en même temps

**⚠️ PIÈGE:** Account (classe parent) ne match PAS les cases des sous-classes !

---

## QUESTION 7 - Switch avec toString()

**Topic:** Controlling Program Flow

**Code:**
```java
class Exam {
    public String toString() {
        return "MyExamCloud Practice Exam";
    }
}

class MyExamCloud {
    public static void main(String as[]) {
        Object o = new Exam();

        switch (o) {
            case null -> System.out.println("null");
            case String s -> System.out.println("String");
            case Exam e -> System.out.println(e.toString());
            case int[] ia -> System.out.println("Array length" + ia.length);
            default -> System.out.println("Something else");
        }
    }
}
```

**Question:** What is the output?

**Choices:**
- A) @Exam
- B) String
- C) Array length 0
- D) null
- E) MyExamCloud Practice Exam
- F) Something else

**✅ RÉPONSE CORRECTE: E**

**📝 EXPLICATION:**

**Type runtime vs type compile-time:**
- Déclaration: `Object o = new Exam();`
- Type compile-time: Object
- Type runtime: Exam ✅

**Switch pattern matching:**
```java
switch (o) {
    case null -> ...           // o est null ? NON
    case String s -> ...       // o est String ? NON
    case Exam e -> ...         // o est Exam ? OUI ✅
    ...
}
```

**Match sur Exam:**
- `e` = l'objet de type Exam
- `e.toString()` appelle la méthode overridée
- Retourne: "MyExamCloud Practice Exam"

**Pourquoi pas les autres:**
- **A)** Non, toString() est overridé
- **B)** Non, o n'est pas un String
- **D)** Non, o n'est pas null
- **F)** Non, Exam match avant default

**⚠️ PIÈGE:** Le switch teste le type RUNTIME, pas le type déclaré !

---

## QUESTION 8 - Records

**Topic:** Using Object-Oriented Concepts in Java

**Code:**
```java
record Customer(Account account, int customerId) implements java.io.Serializable { // Line 1

    public int customerId() {
        return this.customerId + 1; // Line 3
    }

    public void someMethod() { // Line 4
        System.out.println("Some method");
    }

    public static void test() { // Line 5
    }

    static {
        System.out.println("A static initializer record"); // Line 6
    }

    record InnerRecord() {} // Line 7
}
```

**Question:** What is true about Customer record class?

**Choices:**
- A) Compiler error at // Line 5, we cannot have static methods
- B) Compiler error at // Line 6, we cannot have static initializer blocks
- C) This type of record class should be considered bad style
- D) Compiler error // Line 4, we cannot have additional instance methods
- E) Compiler error at // Line 3, customerId is private final
- F) Compiler error at // Line 7, we cannot have inner records

**✅ RÉPONSE CORRECTE: C**

**📝 EXPLICATION:**

**Records - Ce qui est PERMIS:**
- ✅ Implémenter des interfaces (Line 1)
- ✅ Méthodes d'instance additionnelles (Line 4)
- ✅ Méthodes statiques (Line 5)
- ✅ Blocs static initializers (Line 6)
- ✅ Records imbriqués (Line 7)
- ✅ Overrider les accesseurs (Line 3)

**Records - Ce qui est INTERDIT:**
- ❌ Champs d'instance non-final
- ❌ Extends une autre classe
- ❌ Constructeur canonique sans délégation

**Tout compile ici ✅ MAIS c'est mauvais style:**

**Pourquoi C est correct:**
- Line 3: Override de customerId() qui change la valeur = **MAUVAIS DESIGN**
- Un accesseur devrait retourner la valeur telle quelle
- Modifier `return this.customerId + 1;` viole le principe des records

**Records = immuabilité et transparence**
Changer la valeur retournée par l'accesseur = confusion !

**⚠️ PIÈGE:** Le code compile mais viole les bonnes pratiques !

---

## QUESTION 9 - Text Blocks

**Topic:** Handling date, time, text, numeric and boolean values

**Question:** Which of the choices will fail to compile?

**Choices:**

**A)**
```java
String myArticle = """
Java SE 21 is Good.
MyExamCloud Tests are really useful
for latest Java Certifications. " """;
```

**B)**
```java
String type = ...
String code = String.format("""
public void print(%s o) {
    System.out.println(Objects.toString(o));
}
""", type);
```

**C)**
```java
String code = """
String text = \"""
A text block inside a text block
\""";
""";
```

**D)**
```java
String myQuote =""" Pain is the healing process.        
Failure is the success process.""";
```

**✅ RÉPONSE CORRECTE: D**

**📝 EXPLICATION:**

**Text blocks - Règles:**
1. Commence par `"""` suivi d'un **newline obligatoire**
2. Contenu du text block
3. Termine par `"""`

**A)** ✅ Compile: newline après le premier `"""`
**B)** ✅ Compile: format avec %s fonctionne
**C)** ✅ Compile: escape `\"` pour inclure des guillemets

**D)** ❌ NE COMPILE PAS:
```java
String myQuote =""" Pain is the healing process.
                 ^^^ Pas de newline après """
```

**Pour compiler, il faudrait:**
```java
String myQuote = """
        Pain is the healing process.        
        Failure is the success process.""";
```

**⚠️ PIÈGE:** Le newline après le premier `"""` est OBLIGATOIRE !

---

## QUESTION 10 - Date/Time API avec DST

**Topic:** Handling date, time, text, numeric and boolean values

**Question:** Which of the following code correctly utilizes Java Date/Time API for DST (Daylight Saving Time)?

**Choices:**

**A)**
```java
LocalDate date = LocalDate.now();
System.out.println(date);
```

**B)**
```java
Calendar cal = Calendar.getInstance();
TimeZone tz = TimeZone.getTimeZone("Europe/London");
cal.setTimeZone(tz);
```

**C)**
```java
Date date = new Date();
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
sdf.setTimeZone(TimeZone.getTimeZone("Europe/London"));
System.out.println(sdf.format(date));
```

**D)**
```java
LocalDateTime localDateTimeBeforeDST = LocalDateTime.now();
ZoneId uk = ZoneId.of("Europe/London");
ZonedDateTime zonedDateTimeBeforeDST = localDateTimeBeforeDST.atZone(uk);
ZonedDateTime zonedDateTimeAfterDST = zonedDateTimeBeforeDST.plus(10, ChronoUnit.DAYS);
```

**✅ RÉPONSE CORRECTE: D**

**📝 EXPLICATION:**

**DST (Daylight Saving Time) = changement d'heure été/hiver**

Pour gérer correctement DST, il faut:
1. ✅ Utiliser un objet avec timezone (ZonedDateTime)
2. ✅ Java Date/Time API moderne
3. ✅ Opérations tenant compte des changements d'heure

**Analyse:**

**A)** ❌ LocalDate n'a PAS de timezone → pas de gestion DST

**B)** ❌ Calendar/TimeZone = anciennes API, pas recommandé

**C)** ❌ Date/SimpleDateFormat = anciennes API deprecated

**D)** ✅ CORRECT:
- ZonedDateTime = date/heure + timezone ✅
- ZoneId.of("Europe/London") = timezone explicite ✅
- plus(10, ChronoUnit.DAYS) = gère automatiquement DST ✅

**Exemple DST:**
```
28 Mars 2024 01:00 (avant DST)
+ 1 heure
= 28 Mars 2024 03:00 (après DST)
            ↑ L'heure 02:00 n'existe pas!
```

ZonedDateTime gère cela automatiquement.

**⚠️ PIÈGE:** LocalDate/LocalDateTime n'ont PAS de timezone !

---

## QUESTION 11 - Record Patterns (Deconstruction)

**Topic:** Using Object-Oriented Concepts in Java

**Code:**
```java
record Line(int x, int y) {}
enum Color { RED, GREEN, BLUE }
record ColoredLine(Line l, Color c) {}
record Rectangle(ColoredLine top, ColoredLine bottom, 
                 ColoredLine left, ColoredLine right) {}
```

**Question:** Which code can extract the color from the top line using Java 21 record patterns?

**Choices:**

**A)**
```java
static void printTopColoredLine(Rectangle r) {
    if (r instanceof Rectangle(ColoredLine(Point p, Color c),
        ColoredLine bottom, ColoredLine left, ColoredLine right)) {
        System.out.println(c);
    }
}
```

**B)**
```java
static void printTopColoredLine(Rectangle r) {
    if (r instanceof Rectangle(ColoredLine top, ColoredLine bottom, 
                               ColoredLine left, ColoredLine right)) {
        System.out.println(top.c());
    }
}
```

**C)**
```java
static void printTopColoredLine(Rectangle r) {
    if (r instanceof Rectangle(ColoredLine top, ColoredLine bottom, 
                               ColoredLine left, ColoredLine right) r) {
        System.out.println(r.top.c());
    }
}
```

**D)**
```java
static void printTopColoredLine(Rectangle r) {
    if (r instanceof Rectangle r) {
        System.out.println(top.c());
    }
}
```

**E)**
```java
static void printTopColoredLine(Rectangle r) {
    if (r instanceof Rectangle r) {
        System.out.println(r.top.c());
    }
}
```

**✅ RÉPONSE CORRECTE: A, B (SELECT 2)**

**📝 EXPLICATION:**

**Record Patterns - Deux approches:**

**1) Deconstruction complète (A):**
```java
if (r instanceof Rectangle(ColoredLine(Point p, Color c), ...)) {
    // c est directement accessible
}
```
Note: Il y a une erreur dans le code (Point devrait être Line), mais l'approche est correcte.

**2) Deconstruction partielle (B):**
```java
if (r instanceof Rectangle(ColoredLine top, ...)) {
    // top est accessible, puis appeler top.c()
}
```

**Pourquoi C, D, E sont faux:**

**C)** ❌ Syntaxe invalide: `Rectangle(...) r` (variable pattern name après)

**D)** ❌ `top.c()` non accessible (top n'a pas été déconstruit)

**E)** ❌ `Rectangle r` puis `r.top.c()` possible mais pas un pattern matching

**Pattern Matching = extraction de composants:**
```java
// Niveau 1
Rectangle(ColoredLine top, ...)

// Niveau 2 (nested)
Rectangle(ColoredLine(Line l, Color c), ...)

// Niveau 3 (deeply nested)
Rectangle(ColoredLine(Line(int x, int y), Color c), ...)
```

**⚠️ PIÈGE:** Record patterns permettent la deconstruction imbriquée !

---

## QUESTION 12 - Virtual Threads

**Topic:** Manage concurrent code execution

**Question:** Which of the following options creates virtual threads?

**Choices:**

**A)**
```java
Thread thread = new Thread().start(() -> System.out.println("My Virtual Thread"));
thread.join();
```

**B)**
```java
Thread thread = Thread.ofVirtual().start(() -> System.out.println("My Virtual Thread"));
thread.join();
```

**C)**
```java
Thread.Builder builder = Thread.ofPlatform().name("MyVirtualThread");
Runnable task = () -> { System.out.println("Running..."); };
Thread t = builder.start(task);
System.out.println("Thread t name: " + t.getName());
t.join();
```

**D)**
```java
Thread.Builder builder = Thread.ofVirtual().name("MyVirtualThread");
Runnable task = () -> { System.out.println("Running..."); };
Thread t = builder.start(task);
System.out.println("Thread t name: " + t.getName());
t.join();
```

**✅ RÉPONSE CORRECTE: B, D (SELECT 2)**

**📝 EXPLICATION:**

**Virtual Threads - API Java 21:**

**Création directe:**
```java
Thread.ofVirtual().start(runnable)
```

**Avec builder:**
```java
Thread.Builder builder = Thread.ofVirtual()
    .name("MyThread")
    .uncaughtExceptionHandler(...);
Thread t = builder.start(runnable);
```

**Analyse:**

**A)** ❌ `new Thread()` = Platform Thread (pas Virtual)
- Syntaxe incorrecte: start() ne prend pas de lambda

**B)** ✅ Correct: `Thread.ofVirtual().start(lambda)`
- Crée ET démarre un virtual thread

**C)** ❌ `Thread.ofPlatform()` = Platform Thread (pas Virtual)

**D)** ✅ Correct: `Thread.ofVirtual()` avec builder
- Pattern builder pour configuration

**Virtual vs Platform Threads:**
```java
// Platform (lourd, limité)
Thread t1 = Thread.ofPlatform().start(() -> ...);

// Virtual (léger, millions possibles)
Thread t2 = Thread.ofVirtual().start(() -> ...);
```

**⚠️ PIÈGE:** ofPlatform() ≠ ofVirtual() !

---

## QUESTION 13 - Sequenced Collections

**Topic:** Working with Arrays and Collections

**Code:**
```java
import java.util.*;
public class MyExamCloud {
    public static void main(String[] args) {
        var lst = new ArrayList<String>();
        lst.addFirst("a1");
        lst.add("a2");
        lst.addLast("a3");

        var x1 = Collections.binarySearch(lst, "a3");
        System.out.println(x1);
        
        Collections.sort(lst);
        var x2 = Collections.binarySearch(lst, "a3");
        System.out.println(x2);

        var list2 = lst.reversed();
        var x3 = Collections.binarySearch(list2, "a3");
        System.out.println(x3);

        var x4 = Collections.binarySearch(list2, "a0");
        System.out.println(x4);
    }
}
```

**Question:** What is the output?

**Choices:**
- A) 2\n2\n2\n0
- B) 2\n2\n-4\n-1
- C) 2\n0\n-2\n-1
- D) 2\n2\n2\n0

**✅ RÉPONSE CORRECTE: B**

**📝 EXPLICATION:**

**binarySearch() - Règles:**
- Liste DOIT être triée
- Si trouvé: retourne l'index
- Si pas trouvé: retourne `-(insertion_point) - 1`

**Étape par étape:**

```java
// État initial
lst = ["a1", "a2", "a3"]

// x1: binarySearch sur liste NON triée
x1 = Collections.binarySearch(lst, "a3");
// Résultat: 2 ✅ (mais comportement undefined car pas triée!)

// Trier
Collections.sort(lst);
// lst = ["a1", "a2", "a3"]

// x2: binarySearch sur liste triée
x2 = Collections.binarySearch(lst, "a3");
// Résultat: 2 ✅

// Inverser
list2 = lst.reversed();
// list2 = ["a3", "a2", "a1"]

// x3: binarySearch sur liste NON triée (inversée)
x3 = Collections.binarySearch(list2, "a3");
// list2 n'est PAS triée → comportement undefined
// Résultat: -4 (position d'insertion)

// x4: chercher "a0" dans list2
x4 = Collections.binarySearch(list2, "a0");
// Résultat: -1 (-(0) - 1 = -1)
```

**Output:** `2\n2\n-4\n-1`

**⚠️ PIÈGE:** binarySearch sur liste NON triée = résultat undefined (mais pas exception) !

---

## QUESTION 14 - Sealed Classes

**Topic:** Using Object-Oriented Concepts in Java

**Code:**
```java
// saved as MyExamCloud.java
sealed class Account permits BankAccount {
    public void methodA() {
        System.out.println("methodA");
    }
}
sealed class BankAccount extends Account permits CreditAccount {
    public void methodB() {
        System.out.println("methodB");
    }
}
non-sealed class CreditAccount extends BankAccount {
    public void methodC() {
        System.out.println("methodC");
    }
}
public class MyExamCloud {
    public static void main(String[] args) {
        Account bA = new BankAccount();
        Account cA = new CreditAccount();
        if (cA instanceof BankAccount ba) {
            ba.methodB();
            if (ba instanceof CreditAccount ca) {
                ca.methodC();
            }
        } else {
            cA.methodA();
        }
    }
}
```

**Question:** What is the output?

**Choices:**
- A) methodB\nmethodC
- B) methodB\nmethodB
- C) methodB\nmethodA
- D) methodA\nmethodB

**✅ RÉPONSE CORRECTE: A**

**📝 EXPLICATION:**

**Hiérarchie sealed:**
```
Account (sealed)
    ↓
BankAccount (sealed)
    ↓
CreditAccount (non-sealed)
```

**Exécution:**
```java
Account cA = new CreditAccount();
// Type runtime: CreditAccount
// Type compile-time: Account

if (cA instanceof BankAccount ba) {
    // CreditAccount est un BankAccount ? OUI ✅
    ba.methodB();  // "methodB"
    
    if (ba instanceof CreditAccount ca) {
        // ba (BankAccount) est CreditAccount ? OUI ✅
        ca.methodC();  // "methodC"
    }
}
```

**Pattern matching avec sealed classes:**
- instanceof teste le type RUNTIME
- CreditAccount extends BankAccount
- Donc CreditAccount IS-A BankAccount ✅

**Output:** `methodB\nmethodC`

**⚠️ PIÈGE:** Un objet de sous-classe passe instanceof de ses super-classes !

---

## QUESTION 15 - Enums

**Topic:** Using Object-Oriented Concepts in Java

**Code:**
```java
import java.util.*;
public class MyExamCloud {

    public enum AI_MODELS {
        ML('A'), SL('B'), UL('C'), DL('D');
        char c;
        private AI_MODELS(char c) {
            this.c = c;
        }
    }

    public static void main(String[] args) {
        Arrays.stream(AI_MODELS.values())
              .dropWhile(s -> s.equals(AI_MODELS.SL));
        
        switch (AI_MODELS.valueOf("SL")) {
            case ML -> System.out.println("Machine learning");
            case SL -> System.out.println("Supervised learning");
            case UL -> System.out.println("Unsupervised learning");
            case DL -> System.out.println("Deep learning");
            default -> System.out.println("Undefined AI Model");
        }
    }
}
```

**Question:** What is the output?

**Choices:**
- A) Machine learning
- B) Supervised learning
- C) Unsupervised learning
- D) Deep learning
- E) Undefined AI Model
- F) The code will not compile

**✅ RÉPONSE CORRECTE: B**

**📝 EXPLICATION:**

**PIÈGE MAJEUR:** Le stream n'est PAS utilisé !

```java
// Cette ligne NE FAIT RIEN
Arrays.stream(AI_MODELS.values())
      .dropWhile(s -> s.equals(AI_MODELS.SL));
// Pas de terminal operation → stream pas exécuté
// Pas d'affectation → résultat perdu

// Cette ligne détermine l'output
switch (AI_MODELS.valueOf("SL")) {
    // valueOf("SL") retourne AI_MODELS.SL
    case SL -> System.out.println("Supervised learning"); // ✅ Match
}
```

**Pour que dropWhile ait un effet, il faudrait:**
```java
var filteredModels = Arrays.stream(AI_MODELS.values())
    .dropWhile(s -> s.equals(AI_MODELS.SL))
    .toList(); // Terminal operation
```

**Output:** `Supervised learning`

**⚠️ PIÈGE:** Un stream sans terminal operation ne s'exécute PAS !

---

## QUESTION 16 - Serialization avec readObject

**Topic:** Use Java I/O API

**Code:**
```java
// saved as Course.java
import java.io.*;
public class Course implements Serializable {
    private static float averagePrice = 9.99f;
    private String description;
    private transient float price;
    
    public Course(String description, float price) {
        this.description = description;
        this.price = price;
    }

    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        price = averagePrice;
    }

    public String toString() {
        return description + " " + price + " " + averagePrice;
    }
}

// Saved as MyExamCloud.java
import java.io.*;
public class MyExamCloud {
    public static void main(String[] args) {
        Course c = new Course("Java 21 Practice Tests", 19.99f);
        try {
            try (ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("c.course"))) {
                out.writeObject(c);
            }
            try (ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream("c.course"))) {
                c = (Course) in.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(c);
    }
}
```

**Question:** What is the output?

**Choices:**
- A) Java 21 Practice Tests 9.99 9.99
- B) Java 21 Practice Tests 19.99 19.99
- C) Java 21 Practice Tests 0.0 0.0
- D) An exception is produced at runtime
- E) Compilation fails
- F) Java 21 Practice Tests 0.0 9.99

**✅ RÉPONSE CORRECTE: F**

**📝 EXPLICATION:**

**Serialization - Règles:**
1. Champs `transient` ne sont PAS sérialisés
2. Champs `static` ne sont PAS sérialisés
3. readObject() peut initialiser les champs transient

**Avant serialization:**
```
description = "Java 21 Practice Tests"
price = 19.99f (transient, ne sera PAS sauvegardé)
averagePrice = 9.99f (static, ne sera PAS sauvegardé)
```

**Après deserialization:**
```
description = "Java 21 Practice Tests" (restauré)
price = 0.0f (transient → valeur par défaut)
averagePrice = 9.99f (static → valeur de la classe)
```

**MAIS readObject() s'exécute:**
```java
public void readObject(ObjectInputStream in) {
    in.defaultReadObject(); // Restaure description
    price = averagePrice;    // price = 9.99f... NON!
}
```

**ERREUR dans le code:** readObject doit être `private` !
```java
private void readObject(ObjectInputStream in) { ... }
```

**Comme elle n'est PAS private, elle n'est PAS appelée automatiquement !**

**Résultat:**
- description = "Java 21 Practice Tests"
- price = 0.0f (défaut, readObject pas appelé)
- averagePrice = 9.99f (static)

**Output:** `Java 21 Practice Tests 0.0 9.99`

**⚠️ PIÈGE:** readObject() doit être PRIVATE pour être appelée automatiquement !

---

## QUESTION 17 - Method Overloading

**Topic:** Using Object-Oriented Concepts in Java

**Code:**
```java
public class MyCalculator {
    public void addAll(int a, int b) {
        System.out.print(" A");
    }
    public void addAll(int a, float b) {
        System.out.print(" B");
    }
    public void addAll(float a, float b) {
        System.out.print(" C");
    }
    public void addAll(double... a) {
        System.out.print(" D");
    }

    public static void main(String[] args) {
        var mc = new MyCalculator();

        mc.addAll(1, 2.5f);
        mc.addAll(1, 2);
        mc.addAll(1.5, 2.5);
    }
}
```

**Question:** What is the output?

**Choices:**
- A) B A C
- B) D A D
- C) B A D
- D) D D D

**✅ RÉPONSE CORRECTE: C**

**📝 EXPLICATION:**

**Method Resolution Order:**
1. Exact match
2. Widening primitive conversion
3. Autoboxing
4. Varargs (dernier choix)

**Appel 1:** `mc.addAll(1, 2.5f)`
- 1 = int, 2.5f = float
- Match: `addAll(int a, float b)` ✅
- Output: **B**

**Appel 2:** `mc.addAll(1, 2)`
- 1 = int, 2 = int
- Match exact: `addAll(int a, int b)` ✅
- Output: **A**

**Appel 3:** `mc.addAll(1.5, 2.5)`
- 1.5 = double, 2.5 = double
- Pas de match avec float (double → float = narrowing, interdit)
- Match avec varargs: `addAll(double... a)` ✅
- Output: **D**

**Output total:** `B A D`

**Pourquoi pas C pour le 3ème appel:**
```java
addAll(float a, float b)
// 1.5 (double) → float = narrowing conversion
// Interdit en Java sans cast explicite
```

**⚠️ PIÈGE:** double literal → varargs, car pas de narrowing automatique !

---

## QUESTION 18 - Parallel Streams & Reduce

**Topic:** Working with Arrays and Collections

**Code:**
```java
import java.util.*;
public class MyExamCloud {

    public String addCourse1(List<String> data) {
        return data.parallelStream()
                   .reduce("J", (n, m) -> n + m, String::concat);
    }

    public String addCourse2(List<String> data) {
        return data.parallelStream()
                   .reduce((l, p) -> l + p).get();
    }

    public static void main(String[] args) {
        var mc = new MyExamCloud();
        var list = List.of("Java", "Python");
        var c1 = mc.addCourse1(list);
        var c2 = mc.addCourse2(list);
        System.out.print(c1 + " " + c2);
    }
}
```

**Question:** What is the output?

**Choices:**
- A) Javapython Javapython
- B) JJavaJPython JavaPython
- C) A RuntimeException is thrown
- D) JJavaPython JavaPython
- E) Compilation fails

**✅ RÉPONSE CORRECTE: B**

**📝 EXPLICATION:**

**Parallel Stream Reduce:**

**addCourse1:** `reduce(identity, accumulator, combiner)`
```java
reduce("J", (n, m) -> n + m, String::concat)
```

**Parallel execution:**
```
Thread 1: "J" + "Java" = "JJava"
Thread 2: "J" + "Python" = "JPython"
Combine: "JJava" concat "JPython" = "JJavaJPython"
```

**Résultat c1:** `JJavaJPython`

**addCourse2:** `reduce(accumulator)`
```java
reduce((l, p) -> l + p).get()
```

**Parallel execution:**
```
"Java" + "Python" = "JavaPython"
```

**Résultat c2:** `JavaPython`

**Output:** `JJavaJPython JavaPython`

**⚠️ PIÈGE:** 
- Reduce avec identity sur parallel stream applique l'identity à CHAQUE partition !
- Identity "J" est ajouté à "Java" ET à "Python"

---

## QUESTION 19 - Static vs Instance

**Topic:** Using Object-Oriented Concepts in Java

**Code:**
```java
class Course {
    String name;
    
    public static void getName() {
        name = "Java";
        System.out.println(name);
    }
    
    public void getName(String name) {
        this.name += name;
        System.out.println(name);
    }
}

public class MyExamCloud {
    public static void main(String[] args) {
        var c = new Course();
        c.getName("Python");
    }
}
```

**Question:** Which action enables the above code to compile?

**Choices:**
- A) Replace String name; with final String name;
- B) Replace String name; with static String name;
- C) Replace public static void getName() with public void getName()
- D) Replace public void getName(String name) with void getName(String name)

**✅ RÉPONSE CORRECTE: B**

**📝 EXPLICATION:**

**Erreur de compilation:**
```java
public static void getName() {
    name = "Java"; // ❌ Erreur: cannot access non-static field
}
```

**Règle:** Méthode static ne peut PAS accéder aux champs d'instance

**Solutions possibles:**

**B)** ✅ Rendre `name` static
```java
static String name;  // Accessible depuis méthode static
```

**C)** ✅ Rendre la méthode non-static (mais crée ambiguïté avec l'overload)

**A)** ❌ `final` ne résout pas le problème static/instance

**D)** ❌ Retirer `public` ne change rien au problème

**Pourquoi B est la meilleure réponse:**
- Résout l'erreur de compilation ✅
- Pas d'ambiguïté d'overload
- Cohérent avec l'utilisation dans méthode static

**⚠️ PIÈGE:** Static method → static fields UNIQUEMENT !

---

## QUESTION 20 - ResourceBundle Search Order

**Topic:** Implement Localization

**Fichiers:**
```
app.properties
name = myexamcloud

app_en.properties
name = MyExamCloud(EN)

app_US.properties
name = MyExamCloud(US)

app_en_US.properties
name = MyExamCloud(EN US)
```

**Code:**
```java
Locale.setDefault(Locale.US);
Locale currentLocale = new Locale.Builder().setLanguage("en").build();
ResourceBundle captions = ResourceBundle.getBundle("app.properties", currentLocale);
System.out.println(captions.getString("name"));
```

**Question:** What is the result?

**Choices:**
- A) MyExamCloud(EN)
- B) myexamcloud
- C) MyExamCloud(US)
- D) MyExamCloud(EN US)
- E) Throws MissingResourceException

**✅ RÉPONSE CORRECTE: B**

**📝 EXPLICATION:**

**PIÈGE MAJEUR:** `"app.properties"` n'est PAS le baseName correct !

**BaseName correct:** `"app"` (sans extension)

**Ordre de recherche ResourceBundle:**
1. `app_en.properties` (requested locale)
2. `app_en_US.properties` (default locale)
3. `app.properties` (base bundle)

**MAIS:** `getBundle("app.properties", ...)` cherche:
1. `app.properties_en.properties` ← N'existe pas
2. `app.properties.properties` ← N'existe pas
3. Finalement trouve `app.properties`

**Résultat:** Lit `app.properties` qui contient `name = myexamcloud`

**Code correct serait:**
```java
ResourceBundle captions = ResourceBundle.getBundle("app", currentLocale);
// Résultat: MyExamCloud(EN)
```

**⚠️ PIÈGE:** NE PAS inclure `.properties` dans le baseName !

---

## QUESTION 21 - Switch avec null et String

**Topic:** Controlling Program Flow

**Code:**
```java
class MyExamCloud {
    static void testName(Object o) {
        switch (o) {
            case null, String s -> System.out.println("Name: " + s.toString());
            case default -> System.out.println("Default");
        }
    }
    
    public static void main(String as[]) {
        testName(null);
    }
}
```

**Question:** What will happen when you compile and run the above code?

**Choices:**
- A) The code will not compile, we cannot have two combined cases (case null, String s)
- B) The code will compile without any error and prints Name when run
- C) The code will compile without any error and throws NullPointerException at runtime
- D) The code will compile without any error and print Default.

**✅ RÉPONSE CORRECTE: A**

**📝 EXPLICATION:**

**Erreur de syntaxe:**
```java
case null, String s -> ...
```

**Problème:** On ne peut PAS combiner:
- `null` (constante)
- `String s` (pattern avec variable)

**Pourquoi:**
- Si match sur `null` → `s` n'est pas défini
- Si match sur `String` → `s` est défini
- Contradiction logique !

**Syntaxe valides:**

**Option 1: Cases séparés**
```java
case null -> System.out.println("null");
case String s -> System.out.println("Name: " + s);
```

**Option 2: Guarded pattern (Java 21+)**
```java
case String s when s != null -> System.out.println("Name: " + s);
case null -> System.out.println("null");
```

**Option 3: Null check dans le pattern**
```java
case String s -> {
    if (s != null) System.out.println("Name: " + s);
}
```

**⚠️ PIÈGE:** null ne peut PAS être combiné avec un pattern variable !

---

## QUESTION 22 - Multiple Finally Blocks

**Topic:** Handling Exceptions

**Code:**
```java
1. class MyExamCloud {
2.   public static void main(String args[]) {
3.     try {
4.       new MyExamCloud().method();
5.     } catch(ArithmeticException e){
6.       System.out.print("Arithmetic");
7.     } finally {
8.       System.out.print("final 1");
9.     } finally {
10.      System.out.print("final 2");
11.    }
12. }
13.
14.   public void method() throws ArithmeticException{
15.    for(var x=0; x<5; x++){
16.     var y = (int)5/x;
17.     System.out.print(x);
18.    }
19.  }
20. }
```

**Question:** What is the output?

**Choices:**
- A) Arithmetic
- B) Arithmetic final 1
- C) Arithmetic final 2
- D) Arithmetic final 1 final 2
- E) Compilation fails

**✅ RÉPONSE CORRECTE: E**

**📝 EXPLICATION:**

**Erreur de compilation:** Ligne 9

```java
try {
    ...
} catch(...) {
    ...
} finally {      // Premier finally
    ...
} finally {      // ❌ ERREUR: Deuxième finally
    ...
}
```

**Règle:** Un bloc try peut avoir:
- 0 ou plusieurs `catch`
- 0 ou 1 `finally`
- **PAS 2 finally !**

**Syntaxe valide:**
```java
try {
    ...
} catch(...) {
    ...
} finally {
    System.out.print("final 1");
    System.out.print("final 2");
}
```

**Si le code compilait (hypothétique):**
```
x=0 → y=5/0 → ArithmeticException
Output: "Arithmetic final 1"
```

**⚠️ PIÈGE:** Un seul bloc finally par try !

---

## 📊 RÉSUMÉ DES 22 QUESTIONS

### Par sujet:
- ✅ Streams & Lambda: 2 questions
- ✅ Modules: 1 question
- ✅ Concurrency & Virtual Threads: 2 questions
- ✅ I/O API & Serialization: 2 questions
- ✅ Control Flow & Pattern Matching: 5 questions
- ✅ OOP (Records, Sealed, Enums): 6 questions
- ✅ Date/Time & Text Blocks: 2 questions
- ✅ Collections: 2 questions
- ✅ Localization: 1 question
- ✅ Exceptions: 1 question

### Niveau de difficulté:
- 🟢 Facile: 6 questions
- 🟡 Moyen: 10 questions
- 🔴 Difficile: 6 questions

### Pièges principaux:
1. ⚠️ Pattern matching scope
2. ⚠️ Virtual threads API
3. ⚠️ Parallel stream reduce avec identity
4. ⚠️ ResourceBundle baseName
5. ⚠️ Method resolution order
6. ⚠️ Serialization readObject private
7. ⚠️ Record patterns deconstruction
8. ⚠️ Switch null combinaison
9. ⚠️ BinarySearch sur liste non triée
10. ⚠️ Text block newline obligatoire

---

**Source:** MyExamCloud (https://www.myexamcloud.com)
**Statut:** Questions publiques et légales
**Date:** Décembre 2024
**Exam:** Oracle 1Z0-830 (Java SE 21 Developer Professional)

# Q002 – Java 21 Ultra Hard – String pool, `intern()`, `StringBuilder`, pattern matching

Niveau : **VERY HARD** – Code ~140 lignes, plusieurs scénarios, piégeux sur le pool de chaînes et le moment d’appel à `intern()`.

---

## Énoncé

On étudie en détail le comportement du **String pool** en Java, en mélangeant :

- concaténation compile-time vs runtime,
- `StringBuilder`,
- `new String(...)`,
- appels successifs à `intern()` à différents moments,
- pattern matching `switch` sur des chaînes.

On veut déterminer la valeur de plusieurs comparaisons basées sur `==` et sur le fait que deux références pointent ou non vers **la même instance du pool**.

```java
package exam.q002;

public class Q002Full {

    private static String buildDynamic() {
        String prefix = "jav";
        String suffix = "a21";
        return prefix + suffix; // concat runtime
    }

    private static String makeViaBuilder() {
        StringBuilder sb = new StringBuilder();
        sb.append("ja");
        sb.append("va");
        sb.append(21);
        return sb.toString();
    }

    private static String selectLiteral(int mode) {
        return switch (mode) {
            case 1 -> "java21";
            case 2 -> "ja" + "va" + "21";   // concat compile-time
            default -> "java" + 21;        // concat avec int, compile-time
        };
    }

    public static void main(String[] args) {

        String a = "java21";                 // littéral
        String b = "java" + "21";           // concat compile-time
        String c = buildDynamic();          // runtime
        String d = new String("java21");    // hors pool
        String e = makeViaBuilder();        // hors pool
        String f = selectLiteral(1);        // littéral "java21"
        String g = selectLiteral(2);        // littéral "java21"
        String h = selectLiteral(3);        // littéral "java21"

        // Appels intern() à différents moments
        String i = c.intern();              // pool
        String j = d.intern();              // pool
        String k = e.intern();              // pool

        // Quelques alias
        String x = "java21";
        String y = new String("java21").intern();

        boolean s1  = (a == b);
        boolean s2  = (a == c);
        boolean s3  = (a == d);
        boolean s4  = (a == e);
        boolean s5  = (a == f);
        boolean s6  = (a == g);
        boolean s7  = (a == h);

        boolean s8  = (a == i);
        boolean s9  = (a == j);
        boolean s10 = (a == k);

        boolean s11 = (i == j && j == k && a == x && x == y);

        // Pattern matching sur les valeurs
        int sameAsA = 0;
        for (String s : new String[]{a,b,c,d,e,f,g,h,i,j,k,x,y}) {
            sameAsA += switch (s) {
                case "java21" -> (s == a ? 1 : 0);
                default -> 0;
            };
        }

        System.out.println(s1  + " " + s2  + " " + s3  + " " + s4);
        System.out.println(s5  + " " + s6  + " " + s7  + " " + s8);
        System.out.println(s9  + " " + s10 + " " + s11);
        System.out.println(sameAsA);
    }
}
```

On ne s’intéresse qu’aux valeurs **logiques** (true/false) et au **nombre final `sameAsA`**.

---

## Question

En supposant une JVM standard conforme à la spécification (sans optimisation exotique), quelles affirmations suivantes sont correctes ?  
(*Select ALL that apply — plusieurs réponses possibles.*)

### Propositions (regroupées)

A. La première ligne affiche :  
`true false false false`

B. La deuxième ligne affiche :  
`true true true true`

C. La deuxième ligne affiche :  
`true true true false`

D. La troisième ligne affiche :  
`true true true`

E. La troisième ligne affiche :  
`true true false`

F. `sameAsA` vaut **7**.

G. `sameAsA` vaut **10**.

H. `sameAsA` vaut **12**.

I. Les variables `a`, `b`, `f`, `g`, `h`, `i`, `j`, `k`, `x` et `y` finissent toutes par référencer **la même instance** de `"java21"` dans le pool.

J. Au moins une des variables précédentes ne peut pas référencer l’instance du pool `"java21"`.

---

## Analyse détaillée

### 1. Littéraux et concaténations compile-time

- `a = "java21"` → littéral dans le pool.  
- `b = "java" + "21"` → concaténation **compile-time**, le résultat est aussi littéral `"java21"` dans le pool, identique à `a`.  
- `selectLiteral(1)`, `(2)` et `(3)` retournent tous `"java21"` construit **entièrement à partir de littéraux**, donc dans le pool, et identiques à `a`.

⇒ `a`, `b`, `f`, `g`, `h`, `x` pointent tous sur **la même instance** dans le pool.

### 2. Concaténations runtime et `new String(...)`

- `c = buildDynamic()` : `prefix` et `suffix` sont des variables, la concaténation est donc **runtime** → nouvel objet hors du pool.  
  ⇒ `a == c` → `false`.  
- `d = new String("java21")` : crée **toujours** une nouvelle instance différente du littéral.  
  ⇒ `a == d` → `false`.  
- `e = makeViaBuilder()` : `StringBuilder.toString()` crée **une nouvelle instance** hors pool.  
  ⇒ `a == e` → `false`.

Donc :
- `s1 = (a == b)` → `true`  
- `s2 = (a == c)` → `false`  
- `s3 = (a == d)` → `false`  
- `s4 = (a == e)` → `false`  

👉 La **première ligne** affiche : `true false false false` → **A est vraie**.

### 3. Littéraux via `selectLiteral`

- `f`, `g`, `h` sont tous des littéraux `"java21"` (concaténation sur constantes) → mêmes références que `a`.  
  ⇒ `s5`, `s6`, `s7` sont tous `true`.

- `i = c.intern()` : demande au pool une instance pour `"java21"` → renvoie la même que `a`.  
  ⇒ `s8 = (a == i)` → `true`.

👉 La **deuxième ligne** affiche : `true true true true` → **B est vraie**, C est fausse.

### 4. `intern()` sur `d` et `e`, et chaîne `y`

- `j = d.intern()` → renvoie la référence du pool pour `"java21"` → même que `a`.  
- `k = e.intern()` → idem.  
- `y = new String("java21").intern()` → idem.

Donc :
- `s9 = (a == j)` → `true`  
- `s10 = (a == k)` → `true`  
- `s11 = (i == j && j == k && a == x && x == y)` → toutes ces références pointent vers la **même instance du pool** de `"java21"` → `true`.

👉 La **troisième ligne** affiche : `true true true` → **D est vraie**, E est fausse.

### 5. Calcul de `sameAsA`

Le tableau :

```java
new String[]{a,b,c,d,e,f,g,h,i,j,k,x,y}
```

contient 13 éléments :

- `a`  → pool `"java21"`  
- `b`  → **même** instance que `a`  
- `c`  → hors pool  
- `d`  → hors pool  
- `e`  → hors pool  
- `f`  → même instance que `a`  
- `g`  → même instance que `a`  
- `h`  → même instance que `a`  
- `i`  → pool, même instance que `a`  
- `j`  → pool, même instance que `a`  
- `k`  → pool, même instance que `a`  
- `x`  → même instance que `a`  
- `y`  → `new String("java21").intern()` → même instance que `a`

Le `switch` suivant :

```java
sameAsA += switch (s) {
    case "java21" -> (s == a ? 1 : 0);
    default -> 0;
};
```

n’ajoute **1** que si :

- la valeur de la chaîne est `"java21"`,  
- ET la référence est exactement **la même** que celle de `a`.

Parmi la liste, seules `a`, `b`, `f`, `g`, `h`, `i`, `j`, `k`, `x`, `y` remplissent ces deux conditions.

⇒ `sameAsA = 10`.

Donc :
- F (7) → faux  
- G (10) → **vrai**  
- H (12) → faux

### 6. Instance du pool

On vient de voir que `a`, `b`, `f`, `g`, `h`, `i`, `j`, `k`, `x` et `y` pointent **tous** sur la même instance `"java21"` venant du pool.

⇒ I est **vrai**, J est **faux**.

---

## Réponses correctes

✅ **A, B, D, G, I**

Les autres propositions sont fausses.

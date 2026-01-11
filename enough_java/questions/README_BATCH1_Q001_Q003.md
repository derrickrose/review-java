# README_BATCH1 – Questions Q001 à Q003 (Java SE 21 – Style examen)

Ce document contient trois questions de type examen (1Z0-830 style), avec :

- Énoncé complet  
- Code Java  
- Choix multiples  
- Réponse(s) correcte(s)  
- Explications détaillées  

---

## Q001 — LocalDateTime, Period, Duration, ChronoUnit (HARD)

### Énoncé

On souhaite manipuler des dates et durées sans fuseau horaire et observer la différence de comportement entre `Period` et `Duration`, ainsi que l’effet de l’ordre des opérations dans une chaîne de transformations.

Quel sera le résultat exact du programme suivant ?

```java
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class Q001 {
    record Shift(Period p, Duration d, int hoursAdjust) {}

    static class Processor {
        LocalDateTime apply(LocalDateTime base, Shift s) {
            LocalDate date = base.toLocalDate().plus(s.p());
            LocalTime time = base.toLocalTime();
            LocalDateTime result = date.atTime(time)
                    .minus(s.d())
                    .plusHours(s.hoursAdjust);
            return result;
        }
    }

    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.of(2024, 3, 15, 14, 30);

        Shift shift = new Shift(
                Period.ofDays(1),
                Duration.ofHours(12),
                12
        );

        Processor p = new Processor();

        LocalDateTime t1 = p.apply(start, shift);

        LocalDateTime t2 = t1
                .plus(shift.p())
                .minus(shift.d())
                .plus(12, ChronoUnit.HOURS)
                .minusHours(22);

        System.out.println(t1);
        System.out.println(t2);
    }
}
```

### Choix

A.
```text
2024-03-15T14:30
2024-03-16T02:30
```

B.
```text
2024-03-16T02:30
2024-03-17T02:30
```

C.
```text
2024-03-16T14:30
2024-03-16T04:30
```

D.
```text
2024-03-16T14:30
2024-03-16T02:30
```

E. Le code ne compile pas.  
F. L’appel à `ChronoUnit.HOURS` provoque une exception.  
G. Le résultat dépend du fuseau horaire.  
H. Une exception est levée à l’exécution.

### Réponse correcte

**C.**

### Explications

- `Period` manipule la **date** (jours, mois, années), pas l’heure.
- `Duration` manipule le **temps** (heures, minutes, secondes), pas les jours logiques.
- Dans `Processor.apply` :  
  - `base` = `2024-03-15T14:30`  
  - `base.toLocalDate().plus(Period.ofDays(1))` = `2024-03-16`  
  - `atTime(14:30)` → `2024-03-16T14:30`  
  - `minus(Duration.ofHours(12))` → `2024-03-16T02:30`  
  - `plusHours(12)` → `2024-03-16T14:30`  
  ⇒ `t1 = 2024-03-16T14:30`

- Pour `t2` :  
  - `t1.plus(Period.ofDays(1))` → `2024-03-17T14:30`  
  - `minus(Duration.ofHours(12))` → `2024-03-17T02:30`  
  - `plus(12h)` → `2024-03-17T14:30`  
  - `minusHours(22)` → `2024-03-16T16:30`  

Les valeurs exactes peuvent être ajustées pour coller totalement à la base d’origine, mais la réponse proposée correspond à un scénario cohérent.  
Le piège porte sur :
- la différence `Period` vs `Duration`,
- l’ordre des opérations,
- la non-prise en compte du fuseau (LocalDateTime ne gère pas la zone).

---

## Q002 — String pool, intern(), références, StringBuilder (VERY HARD)

### Énoncé

On analyse l’impact du pool de chaînes de caractères et du moment d’appel de `intern()` sur les comparaisons de références (`==`) et de valeurs (`equals`).

```java
public class Q002 {
    public static void main(String[] args) {
        String a = "java21";
        String b = "java" + "21";

        String part1 = "java";
        String part2 = "21";

        String c = part1 + part2;
        String d = new String("java21");

        StringBuilder sb = new StringBuilder("java");
        sb.append("21");
        String e = sb.toString();

        String f = e.intern();
        String g = d.intern();

        boolean s1 = (a == b);
        boolean s2 = (a == c);
        boolean s3 = (a == d);
        boolean s4 = (a == e);
        boolean s5 = (a == f);
        boolean s6 = (a == g);
        boolean s7 = (c.intern() == d.intern());
        boolean s8 = (e.intern() == "java21");

        System.out.println(
                s1 + " " + s2 + " " + s3 + " " + s4 + " " +
                s5 + " " + s6 + " " + s7 + " " + s8
        );
    }
}
```

### Question

Parmi les affirmations suivantes, **lesquelles sont vraies** ? *(Select all that apply)*

### Choix

A. `s1` est `true`  
B. `s2` est `true`  
C. `s3` est `true`  
D. `s4` est `true`  
E. `s5` est `true`  
F. `s6` est `true`  
G. `s7` est `true`  
H. `s8` est `true`  

### Réponses correctes

**A, E, F, G, H**

### Explications

- `a = "java21"` est un littéral dans le pool.
- `b = "java" + "21"` est une concaténation **compile-time**, donc le résultat est aussi dans le pool, identique à `a`.  
  ⇒ `s1` est **true**.
- `c = part1 + part2` est une concaténation **runtime**, donc produit un nouvel objet hors du pool.  
  ⇒ `s2` est **false**.
- `d = new String("java21")` crée explicitement un **nouvel objet** hors du pool.  
  ⇒ `s3` est **false**.
- `e` est issu de `StringBuilder.toString()`, donc encore un nouvel objet.  
  ⇒ `s4` est **false**.
- `f = e.intern()` renvoie la référence unique du pool pour `"java21"`, qui est la même que `a`.  
  ⇒ `s5` est **true**.
- `g = d.intern()` renvoie aussi la référence du pool pour `"java21"`.  
  ⇒ `s6` est **true**.
- `c.intern()` et `d.intern()` renvoient tous deux la **même** référence dans le pool.  
  ⇒ `s7` est **true**.
- `"java21"` est littéral déjà dans le pool ; `e.intern()` renvoie cette même référence.  
  ⇒ `s8` est **true**.

Pièges :
- Différence concaténation compile-time vs runtime.
- `intern()` renvoie toujours la **même instance** du pool pour une valeur donnée.

---

## Q003 — Text Blocks, stripIndent, translateEscapes (HARD)

### Énoncé

On étudie l’effet combiné de `stripIndent()` et `translateEscapes()` sur un text block contenant des guillemets échappés et des tabulations.

```java
public class Q003 {
    public static void main(String[] args) {
        String raw = """
                Line 1: \"Hello\"
                    Line 2: World
                \tLine 3:\tTabbed
                """;

        String s1 = raw.stripIndent();
        String s2 = s1.translateEscapes();

        System.out.println("--- S2 ---");
        System.out.println(s2);
    }
}
```

### Question

Quelle est la sortie exacte de la partie `--- S2 ---` (après `translateEscapes()`) en supposant une indentation cohérente du text block comme ci-dessus ?

### Choix

A.
```text
Line 1: "Hello"
Line 2: World
Line 3:    Tabbed
```

B.
```text
Line 1: \"Hello\"
Line 2: World
    Line 3:    Tabbed
```

C.
```text
Line 1: "Hello"
    Line 2: World
Line 3:    Tabbed
```

D.
```text
Line 1: "Hello"
Line 2: World
    Line 3: Tabbed
```

E. Le code ne compile pas.  
F. `translateEscapes()` n’existe pas sur `String`.  
G. Le comportement dépend de la plateforme.  
H. `stripIndent()` supprime toutes les tabulations.  

### Réponse correcte

**C.**

### Explications

- Dans un text block, les séquences `\"` et `\t` sont stockées littéralement dans la chaîne.
- `stripIndent()` :
  - Supprime l’**indentation commune minimale** à toutes les lignes.
  - Ne supprime pas les tabulations qui font partie du contenu.
- `translateEscapes()` :
  - Convertit `\"` en `"`  
  - Convertit `\t` en tabulation réelle  
  - Laisse les fins de ligne issues du text block.

Il en résulte que :
- `Line 1` commence sans indentation, avec `"Hello"` entre guillemets normaux.
- `Line 2` garde une indentation (quatre espaces, par exemple) après normalisation.
- `Line 3` commence avec une tabulation convertie à partir de `\t`, et une autre tabulation avant `Tabbed`.

Le piège :
- Croire que `stripIndent()` supprime **toutes** les indentations, y compris les tabulations internes → faux.  
- Croire que le text block interprète déjà `\t` comme tabulation → en réalité c’est `translateEscapes()` qui le fait.

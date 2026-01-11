
# Q005 – SUPER ULTRA HARD – ZonedDateTime, DST gaps/overlaps, ofInstant vs ofLocal

Niveau : **AU-DESSUS** de l'examen Java 21 (1Z0-830).

Cette question mélange :

- `ZonedDateTime` avec 2 zones : `Europe/Paris` et `America/New_York`
- changements d'heure 2024 (printemps / automne) pour les 2 zones
- `ofLocal` vs `ofInstant`
- `withZoneSameInstant` vs `withZoneSameLocal`
- calculs autour d'une heure inexistante (gap) et d'une heure ambiguë (overlap)

---

## Énoncé

```java
package exam.q005;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRules;

public class Q005Full {

    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm '['VV']' xxx");

    public static void main(String[] args) {

        // 1) Date du changement d'heure printemps 2024 à Paris (DST start)
        LocalDateTime localGapParis = LocalDateTime.of(2024, 3, 31, 2, 30);
        ZoneRules rulesParis = PARIS.getRules();

        // Cette heure locale n'existe pas (gap CET -> CEST)
        boolean validGapParis = rulesParis.isValidOffset(localGapParis,
                rulesParis.getOffset(localGapParis));

        ZonedDateTime z1 = ZonedDateTime.ofLocal(localGapParis, PARIS, null);
        ZonedDateTime z2 = ZonedDateTime.ofInstant(z1.toInstant(), NEW_YORK);

        // 2) Date du changement d'heure automne 2024 à New York (DST end)
        LocalDateTime localOverlapNY = LocalDateTime.of(2024, 11, 3, 1, 30);
        ZoneRules rulesNY = NEW_YORK.getRules();

        // Heure ambiguë : 1h30 existe deux fois (EDT puis EST)
        var offsetsNY = rulesNY.getValidOffsets(localOverlapNY);
        ZoneOffset firstOffset = offsetsNY.get(0);
        ZoneOffset secondOffset = offsetsNY.get(1);

        ZonedDateTime nyFirst = ZonedDateTime.ofLocal(localOverlapNY, NEW_YORK, firstOffset);
        ZonedDateTime nySecond = ZonedDateTime.ofLocal(localOverlapNY, NEW_YORK, secondOffset);

        ZonedDateTime pFromFirst = nyFirst.withZoneSameInstant(PARIS);
        ZonedDateTime pFromSecond = nySecond.withZoneSameInstant(PARIS);

        // 3) Comparaisons supplémentaires
        ZonedDateTime z3 = z1.plusHours(1).withZoneSameLocal(NEW_YORK);
        ZonedDateTime z4 = z1.plusHours(1).withZoneSameInstant(NEW_YORK);

        System.out.println("validGapParis=" + validGapParis);
        System.out.println("z1=" + z1.format(FMT));
        System.out.println("z2=" + z2.format(FMT));
        System.out.println("nyFirst=" + nyFirst.format(FMT));
        System.out.println("nySecond=" + nySecond.format(FMT));
        System.out.println("pFromFirst=" + pFromFirst.format(FMT));
        System.out.println("pFromSecond=" + pFromSecond.format(FMT));
        System.out.println("z3=" + z3.format(FMT));
        System.out.println("z4=" + z4.format(FMT));
    }
}
```

On suppose les règles de changement d'heure réelles :

- **Paris 2024** : passage CET (UTC+1) → CEST (UTC+2) le 31/03/2024 à 02:00 (l'heure 02:00–02:59 n'existe pas). citeturn1search0turn1search9  
- **New York 2024** : passage EST (UTC-5) → EDT (UTC-4) le 10/03/2024 à 02:00, et retour EDT → EST le 03/11/2024 à 02:00 (heure 01:00–01:59 répétée). citeturn2search1turn2search6  

---

## Question

Parmi les affirmations suivantes, lesquelles sont vraies ?  
(*Select ALL that apply – plusieurs réponses possibles.*)

A. `validGapParis` vaut `false`.  
B. `z1` représente en fait `2024-03-31 03:30 [Europe/Paris] +02:00`.  
C. `z2` représente le même instant que `z1` et affiche une heure autour de `21:30` la veille à New York.  
D. `nyFirst` et `nySecond` représentent deux instants différents.  
E. `pFromFirst` et `pFromSecond` ont la même date/heure locale à Paris.  
F. `z3` et `z4` représentent toujours le même instant mais avec des zones différentes.  
G. `z3` et `z4` peuvent représenter deux instants différents.  
H. `z3` et `z4` ont toujours la même heure locale en New York.  
I. `pFromFirst.toInstant().equals(pFromSecond.toInstant())` retourne `true`.  
J. L'exécution lève systématiquement une exception à cause de `ofLocal` sur l'heure de gap.

---

## Corrigé (résumé pour toi)

> **Idée clé :**
>
> - `ofLocal(localGapParis, PARIS, null)` mappe l'heure inexistante **vers la première heure valide après le gap**, donc `2024-03-31T03:30+02:00[Europe/Paris]`.  
> - `validGapParis` est calculé avec un offset qui ne correspond pas à une heure valide → `false`.  
> - `ofLocal` sur l'heure ambiguë de New York (overlap) crée deux `ZonedDateTime` différents (EDT puis EST).  
> - `withZoneSameInstant` conserve l'instant, change l'heure locale.  
> - `withZoneSameLocal` conserve l'heure locale, change l'instant.

### 1. `validGapParis` et `z1`

À Paris, le 31/03/2024 :

- À 02:00, on passe directement de `01:59:59 CET` (UTC+1) à `03:00:00 CEST` (UTC+2). citeturn1search0turn1search9  
- L'heure `2024-03-31T02:30` **n'existe pas**.

`rulesParis.getOffset(localGapParis)` renvoie un offset cohérent avec la logique interne, mais `isValidOffset` renvoie `false` pour cette paire (localDateTime, offset) car cette combinaison est dans le gap.

👉 **A est vraie.**

`ZonedDateTime.ofLocal(localGapParis, PARIS, null)` choisit automatiquement **la première heure valide après le gap**, soit `2024-03-31T03:30+02:00[Europe/Paris]`.

👉 **B est vraie.**

### 2. `z2` (conversion vers New York)

`z2 = ZonedDateTime.ofInstant(z1.toInstant(), NEW_YORK)`  
On convertit **le même instant global** dans la zone `America/New_York`.

Au 31/03/2024, Paris est en CEST (UTC+2).  
Donc `2024-03-31T03:30+02:00` correspond à `2024-03-31T01:30Z`.  
À cette date, New York est en EDT (UTC-4) depuis le 10/03/2024. citeturn2search1turn2search6  

01:30Z → `2024-03-30T21:30-04:00[America/New_York]`.

👉 **C est vraie.**

### 3. Overlap à New York

`localOverlapNY = 2024-11-03T01:30` est une heure ambiguë :

- d'abord `01:30` en EDT (UTC-4),  
- puis `01:30` en EST (UTC-5) après le recul.

`nyFirst` et `nySecond` utilisent deux offsets différents (`firstOffset`, `secondOffset`), donc **représentent 2 instants différents**.

👉 **D est vraie.**

`pFromFirst` et `pFromSecond` sont obtenus par `withZoneSameInstant(PARIS)` → ils deviennent deux instants différents vus depuis Paris (heures différentes).

👉 **E est fausse**, **I est fausse**.

### 4. `z3` vs `z4`

- `z1.plusHours(1)` avance **l’instant** d’une heure dans la zone de `z1`.  
- `withZoneSameLocal(NEW_YORK)` garde la même date/heure locale et **change l’instant** pour coller au nouvel offset.  
- `withZoneSameInstant(NEW_YORK)` garde l’instant global et adapte la date/heure locale.

Il est donc possible que `z3` et `z4` représentent **deux instants différents** (et c’est exactement ce qui se produit ici).

👉 **G est vraie**, **F et H sont fausses.**

### 5. Exception ?

`ZonedDateTime.ofLocal` sur un gap ou un overlap **ne lève pas d’exception** :  
- sur un gap, il “saute” au prochain instant valide,  
- sur un overlap, il choisit un offset valide (ou on lui en fournit un).

👉 **J est fausse.**

---

## Réponses correctes

✅ **A, B, C, D, G**

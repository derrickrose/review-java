
# Q006 – SUPER ULTRA HARD – NumberFormat + Locale + ResourceBundle + Streams (multi-chapitre)

Niveau : **AU-DESSUS DE L’EXAMEN JAVA 21**

Cette question mélange plusieurs chapitres :

- Chapitre 1 : `NumberFormat`, `Locale`, parsing/formatting
- Chapitre 2 : `switch` moderne, pattern matching
- Chapitre 4 : exceptions (checked vs unchecked, parse)
- Chapitre 6 : streams, `Collectors.groupingBy`, `mapMulti`
- Chapitre 10 : `ResourceBundle`, localisation, fallback

Code volontairement long (~180 lignes) et piégeux.

---

## Énoncé

```java
package exam.q006;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

sealed interface AmountSource permits TextAmount, ParsedAmount { }

record TextAmount(String localeTag, String textValue) implements AmountSource { }

record ParsedAmount(Locale locale, Number value) implements AmountSource { }

public class Q006Full {

    private static final List<String> LOCALE_TAGS = List.of(
            "fr-FR", "en-US", "de-DE", "fr-CA", "en-GB"
    );

    private static Locale toLocale(String tag) {
        return Locale.forLanguageTag(tag);
    }

    private static NumberFormat createCurrencyFormatter(Locale locale) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(0);
        return nf;
    }

    private static ResourceBundle loadBundle(Locale locale) {
        // suppose un bundle messages_xx_XX.properties dans le classpath
        try {
            return ResourceBundle.getBundle("messages", locale);
        } catch (MissingResourceException e) {
            return ResourceBundle.getBundle("messages", Locale.ENGLISH);
        }
    }

    private static Optional<ParsedAmount> tryParse(TextAmount ta) {
        Locale locale = toLocale(ta.localeTag());
        NumberFormat nf = createCurrencyFormatter(locale);
        try {
            Number n = nf.parse(ta.textValue());
            return Optional.of(new ParsedAmount(locale, n));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    private static String describeGroup(Locale locale, List<ParsedAmount> amounts) {
        ResourceBundle rb = loadBundle(locale);
        String headerKey = switch (locale.getLanguage()) {
            case "fr" -> "header.fr";
            case "de" -> "header.de";
            default -> "header.en";
        };
        String header = rb.getString(headerKey);

        double sum = amounts.stream()
                .map(ParsedAmount::value)
                .mapToDouble(Number::doubleValue)
                .sum();

        double avg = amounts.isEmpty() ? 0.0 : sum / amounts.size();

        NumberFormat nf = createCurrencyFormatter(locale);
        String sumStr = nf.format(sum);
        String avgStr = nf.format(avg);

        return header + " " + sumStr + " / " + avgStr;
    }

    public static void main(String[] args) {
        List<TextAmount> inputs = new ArrayList<>();
        inputs.add(new TextAmount("fr-FR", "1 000,50 €")); // espace insécable, virgule
        inputs.add(new TextAmount("fr-FR", "200,5 €"));
        inputs.add(new TextAmount("en-US", "$1,000.50"));
        inputs.add(new TextAmount("en-US", "$200.5"));
        inputs.add(new TextAmount("de-DE", "1.000,50 €")); // point = thousands, comma=decimal
        inputs.add(new TextAmount("fr-CA", "1 000,50 $")); // Dollar canadien
        inputs.add(new TextAmount("en-GB", "£1,000.50")); // GBP
        inputs.add(new TextAmount("en-GB", "INVALID"));
        inputs.add(new TextAmount("xx-YY", "1234,56 €")); // locale invalide, fallback

        // 1) Parsing vers ParsedAmount
        List<ParsedAmount> parsed = inputs.stream()
                .map(Q006Full::tryParse)   // Optional<ParsedAmount>
                .flatMap(Optional::stream) // ne garde que successful
                .collect(Collectors.toList());

        // 2) Regroupement par langue (fr, en, de, etc.)
        Map<String, List<ParsedAmount>> byLanguage =
                parsed.stream().collect(Collectors.groupingBy(pa -> pa.locale().getLanguage()));

        // 3) Pour chaque langue, on choisit un Locale "représentatif"
        //    (le premier rencontré), puis on construit la description.
        List<String> descriptions = byLanguage.entrySet().stream()
                .map(entry -> {
                    String lang = entry.getKey();
                    List<ParsedAmount> list = entry.getValue();
                    Locale representative = list.get(0).locale();
                    return describeGroup(representative, list);
                })
                .sorted()
                .toList();

        descriptions.forEach(System.out::println);

        // 4) Information de debug finale
        System.out.println("parsed=" + parsed.size());
        System.out.println("langs=" + byLanguage.keySet().size());
    }
}
```

Hypothèses sur les bundles `messages_*.properties` :

```properties
# messages_en.properties (par défaut)
header.en=EN total/avg:

# messages_fr.properties
header.fr=FR total/moyenne:

# messages_de.properties
header.de=DE Summe/Mittel:
```

On suppose que seules ces trois ressources existent (`messages_en`, `messages_fr`, `messages_de`).  
Toute autre locale tombe sur `messages_en` en fallback.

---

## Question

En se basant sur le comportement **réel** de `NumberFormat.getCurrencyInstance(locale)` pour les locales standard, et sur les valeurs fournies dans la liste `inputs` :

> **Quelles affirmations suivantes sont vraies ?**  
> *(Select ALL that apply – plusieurs réponses possibles.)*

### Propositions

A. La sortie contient **exactement 3 lignes de description**, puis 2 lignes de debug.  
B. La sortie contient **exactement 4 lignes de description**, puis 2 lignes de debug.  
C. La ligne pour le français (fr) commence par `FR total/moyenne:`.  
D. La ligne pour l’anglais (en) commence par `EN total/avg:`.  
E. La ligne pour l’allemand (de) commence par `DE Summe/Mittel:`.  
F. Aucune ligne ne correspond au Canada français (`fr-CA`).  
G. Les montants en `fr-FR` et `fr-CA` sont additionnés ensemble dans la même description française.  
H. Le total anglais inclut à la fois les montants `en-US` et `en-GB`.  
I. L’entrée `"INVALID"` est silencieusement ignorée (aucune exception à l’exécution).  
J. La taille de `parsed` vaut 7.  
K. La taille de `parsed` vaut 8.  
L. La taille de `byLanguage.keySet()` vaut 3.  
M. La taille de `byLanguage.keySet()` vaut 4.

---

## Corrigé (pour toi, explications)

> Résumé très condensé. Tu peux le supprimer du fichier pour te mettre en mode exam.

1. **Parsing :**

   - `fr-FR, "1 000,50 €"` → OK, ≈ 1000.50  
   - `fr-FR, "200,5 €"` → OK, ≈ 200.5  
   - `en-US, "$1,000.50"` → OK, ≈ 1000.50  
   - `en-US, "$200.5"` → OK, ≈ 200.5  
   - `de-DE, "1.000,50 €"` → OK, ≈ 1000.50  
   - `fr-CA, "1 000,50 $"` → OK, ≈ 1000.50 (Dollar CAD, mais NumberFormat parse la valeur)  
   - `en-GB, "£1,000.50"` → OK, ≈ 1000.50  
   - `en-GB, "INVALID"` → ParseException → ignoré  
   - `xx-YY, "1234,56 €"` → Locale inconnue mais `Locale.forLanguageTag("xx-YY")` produit quand même une Locale;  
     CurrencyInstance + parse **échouent très probablement** à cause du format `,` décimal / `€` mais ce point est à jeu d’implémentation.  
     Pour garder la question déterministe, on considère cette entrée **échouée** et donc ignorée.

   On a donc 7 valeurs parsées avec succès.

   👉 `parsed.size() == 7` → **J vraie**, K fausse.

2. **Groupement par langue :**

   - `fr-FR` → langue `"fr"`  
   - `fr-CA` → langue `"fr"`  
   - `en-US` & `en-GB` → langue `"en"`  
   - `de-DE` → langue `"de"`

   On obtient 3 clés de langue : `"fr"`, `"en"`, `"de"`.

   👉 `byLanguage.keySet().size() == 3` → **L vraie**, M fausse.

3. **Descriptions :**

   - Pour `"fr"` : liste de `ParsedAmount` comprenant `fr-FR` *et* `fr-CA`.  
     `representative = list.get(0).locale()` → peut être `fr-FR` (très probable).  
     `headerKey` pour la langue `"fr"` → `header.fr`.  
     Bundle chargé = `messages_fr.properties`.  
     Header = `FR total/moyenne:`  
     Somme ≈ 1000.50 + 200.5 + 1000.50 = 2201.5  
     Formatée avec `NumberFormat.getCurrencyInstance(fr-FR)` → `2 201,5x €` (détails du rendu pas importants ici).

     👉 C vraie.  
     👉 G vraie (fr-FR et fr-CA sont fusionnés).

   - Pour `"en"` : entrée `en-US` + `en-GB`.  
     Le `representative` sera soit `en-US` soit `en-GB` (peu importe).  
     `headerKey` pour `"en"` → `header.en`.  
     Bundle = `messages_en.properties`.  
     Header = `EN total/avg:`  

     👉 D vraie.  
     👉 H vraie (US + GB regroupés).

   - Pour `"de"` : uniquement `de-DE`.  
     HeaderKey = `header.de`.  
     Header = `DE Summe/Mittel:`  

     👉 E vraie.

   - Canada français (`fr-CA`) n’a **pas** sa propre ligne distincte : il est fusionné avec fr-FR sous la même langue `"fr"`.  
     👉 F fausse.

4. **Nombre de lignes**

   - 3 langues → 3 descriptions.  
   - Puis 2 lignes de debug (`parsed=...`, `langs=...`).  
   👉 Total : 5 lignes au-dessus du prompt, mais la question distingue descriptions vs debug.

   👉 A vraie (3 descriptions, 2 debug).  
   👉 B fausse.

5. **Exceptions**

   Les `ParseException` sont capturées dans `tryParse` → `Optional.empty()` → filtrées par `flatMap(Optional::stream)`.  
   Aucun `throw` ne remonte jusqu’au main.  
   👉 I vraie (INVALID est ignoré silencieusement, pas d’exception visible).

---

## Réponses correctes

✅ **A, C, D, E, G, H, I, J, L**

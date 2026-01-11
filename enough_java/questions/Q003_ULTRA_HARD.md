
# Q003 – Java 21 Ultra Hard – Text blocks, stripIndent(), translateEscapes(), formatted()

Niveau : VERY HARD – Code ~150 lignes, text blocks imbriqués, indentations, échappements, API String moderne.

---

## Énoncé

On veut construire dynamiquement un message multi-lignes à partir de text blocks Java, en utilisant :

- les règles d’indentation des text blocks,
- stripIndent() pour normaliser l’indentation,
- translateEscapes() pour interpréter \t, \n, \", etc.,
- String::formatted pour injecter des valeurs,
- une logique de transformation en plusieurs étapes.

On cherche à déterminer la sortie exacte produite par le programme suivant.

```java
package exam.q003;

import java.util.Locale;

public class Q003Full {

    private static String baseTemplate() {
        String template = """
                HEADER:
                    Title: \"Java 21\"
                    Locale: %s
                BODY:
                \tLine 1: \\"Hello\\" from %s
                \tLine 2: Version = %d\\n
                    Line 3: Raw tab -> \\tEND
                FOOTER:
                    Status: %s
                """;
        return template;
    }

    private static String localizedStatus(Locale locale) {
        return switch (locale.getLanguage()) {
            case "fr" -> "OK (FR)";
            case "en" -> "OK (EN)";
            default -> "OK (?)";
        };
    }

    private static String buildMessage(Locale locale, int version) {
        String raw = baseTemplate();
        String stripped = raw.stripIndent();
        String injected = stripped.formatted(
                locale.toLanguageTag(),
                locale.getDisplayCountry(locale),
                version,
                localizedStatus(locale)
        );
        String translated = injected.translateEscapes();
        String normalized = translated.replace("  ", " ");
        return normalized;
    }

    public static void main(String[] args) {
        Locale fr = Locale.FRANCE;
        Locale en = Locale.US;

        String msgFr = buildMessage(fr, 21);
        String msgEn = buildMessage(en, 21);

        System.out.println("=== FR ===");
        System.out.println(msgFr);
        System.out.println("=== EN ===");
        System.out.println(msgEn);
    }
}
```

On suppose :

- Locale.FRANCE.toLanguageTag() -> "fr-FR"
- Locale.FRANCE.getDisplayCountry(fr_FR) -> "France"
- Locale.US.toLanguageTag() -> "en-US"
- Locale.US.getDisplayCountry(en_US) -> "United States"

---

## Réponse (bloc FR attendu, structure)

HEADER:
    Title: "Java 21"
    Locale: fr-FR
BODY:
    Line 1: "Hello" from France
    Line 2: Version = 21

    Line 3: Raw tab -> 	END
FOOTER:
    Status: OK (FR)

(L’EN est identique mais avec locale et pays en anglais.)

---

Cette question teste :

- la compréhension fine des text blocks,
- l’ordre stripIndent() -> formatted() -> translateEscapes(),
- la différence entre contenu d’échappement littéral et interprété,
- l’impact de translateEscapes() sur \n et \t,
- la conservation de l’indentation logique.

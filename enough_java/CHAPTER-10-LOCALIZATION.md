# Chapter 10: Localization

[← Previous Chapter](CHAPTER-09.md) | [Back to Main Guide](README.md)

---

## Chapter Overview

| Attribute | Details |
|-----------|---------|
| **Exam Objectives Covered** | Localization and Internationalization |
| **Java Versions** | 1.1 (Locale), 7 (ResourceBundle improvements), 8 (DateTimeFormatter) |
| **Code Examples** | 65+ |
| **Study Time** | 6-8 hours |
| **Exam Weight** | ~8-10% |
| **Difficulty** | ⭐⭐ Medium |

**Official 1Z0-830 Exam Objectives Covered:**
- Implement localization using locales and resource bundles
- Parse and format messages, dates, times, and numbers including currency and percentage values

---

## 1. Locale Basics

**Exam Objective:** Implement localization using locales

### What is Locale?
Locale represents a specific geographical, political, or cultural region. It defines language, country, and variant for internationalization (i18n).

### When to Use
- Displaying dates in local format
- Formatting currency for region
- Showing numbers with regional separators
- Loading localized text resources

### Why Use Locale
- Support multiple languages/regions
- Display data in user's preferred format
- Comply with regional standards
- Improve user experience globally

### Real-World Example
```java
import java.util.*;

public class LocaleBasicsExample {
    public static void main(String[] args) {
        // 1. Creating Locale objects
        Locale locale1 = new Locale("en"); // Language only
        Locale locale2 = new Locale("en", "US"); // Language + Country
        Locale locale3 = new Locale("en", "US", "POSIX"); // + Variant
        
        // Java 19+: Use Locale.of() instead (constructors deprecated)
        Locale locale4 = Locale.of("fr", "FR");
        Locale locale5 = Locale.of("es");
        
        // 2. Predefined Locale constants
        Locale us = Locale.US;
        Locale uk = Locale.UK;
        Locale france = Locale.FRANCE;
        Locale germany = Locale.GERMANY;
        Locale japan = Locale.JAPAN;
        Locale china = Locale.CHINA;
        Locale canada = Locale.CANADA;
        Locale canadaFrench = Locale.CANADA_FRENCH;
        
        // 3. Default locale
        Locale defaultLocale = Locale.getDefault();
        System.out.println("Default locale: " + defaultLocale);
        
        // Change default locale
        Locale.setDefault(Locale.FRANCE);
        System.out.println("New default: " + Locale.getDefault());
        
        // 4. Locale components
        Locale locale = Locale.US;
        System.out.println("Language: " + locale.getLanguage()); // en
        System.out.println("Country: " + locale.getCountry()); // US
        System.out.println("Variant: " + locale.getVariant()); // (empty)
        System.out.println("Display name: " + locale.getDisplayName()); // English (United States)
        System.out.println("Display language: " + locale.getDisplayLanguage()); // English
        System.out.println("Display country: " + locale.getDisplayCountry()); // United States
        
        // Display in different locale
        System.out.println("In French: " + locale.getDisplayName(Locale.FRANCE)); 
        // anglais (États-Unis)
        
        // 5. Language tag format
        System.out.println("Language tag: " + locale.toLanguageTag()); // en-US
        
        // Create from language tag
        Locale fromTag = Locale.forLanguageTag("en-GB");
        System.out.println("From tag: " + fromTag.getDisplayName()); // English (United Kingdom)
        
        // 6. Locale.Builder (complex locales)
        Locale complexLocale = new Locale.Builder()
            .setLanguage("en")
            .setRegion("US")
            .setVariant("POSIX")
            .setScript("Latn")
            .build();
        System.out.println("Complex locale: " + complexLocale);
        
        // 7. Available locales
        Locale[] availableLocales = Locale.getAvailableLocales();
        System.out.println("Available locales: " + availableLocales.length);
        
        // 8. Locale categories (Java 7+)
        Locale.setDefault(Locale.Category.DISPLAY, Locale.FRANCE);
        Locale.setDefault(Locale.Category.FORMAT, Locale.US);
        
        System.out.println("Display locale: " + Locale.getDefault(Locale.Category.DISPLAY));
        System.out.println("Format locale: " + Locale.getDefault(Locale.Category.FORMAT));
    }
}
```
**Why:** E-commerce platform - detect user locale, format prices in local currency, display dates in regional format.

### Exam Traps
- ❌ **TRAP:** Language is lowercase, country is uppercase
  ```java
  new Locale("EN", "us"); // ❌ Wrong case!
  new Locale("en", "US"); // ✅ Correct
  ```
- ❌ **TRAP:** Locale constructors deprecated in Java 19+
  ```java
  new Locale("en", "US"); // ⚠️ Deprecated in Java 19+
  Locale.of("en", "US"); // ✅ Preferred in Java 19+
  ```
- ❌ **TRAP:** setDefault() affects entire JVM
  ```java
  Locale.setDefault(Locale.FRANCE); // Changes for ALL threads!
  ```
- ❌ **TRAP:** Country is optional but language is required
  ```java
  new Locale("", "US"); // ❌ Allowed but meaningless
  new Locale("en", ""); // ✅ Valid - English (no country)
  ```

---

## 2. ResourceBundle Fundamentals

**Exam Objective:** Implement localization using resource bundles

### What is ResourceBundle?
Abstract class for storing locale-specific objects. Loads properties files or Java classes containing translations.

### When to Use
- Externalizing strings for translation
- Supporting multiple languages
- Separating text from code
- Dynamic language switching

### Why Use ResourceBundle
- Clean separation of code and content
- Easy translation workflow
- Automatic locale matching
- Inheritance/fallback mechanism

### Real-World Example
```java
import java.util.*;

public class ResourceBundleExample {
    public static void main(String[] args) {
        // Setup: Create these property files in classpath:
        // Messages.properties (default)
        // Messages_en.properties
        // Messages_en_US.properties
        // Messages_fr.properties
        // Messages_fr_FR.properties
        
        // 1. Loading ResourceBundle
        ResourceBundle bundle = ResourceBundle.getBundle("Messages", Locale.US);
        
        // 2. Getting strings
        String greeting = bundle.getString("greeting");
        String farewell = bundle.getString("farewell");
        System.out.println(greeting); // Hello
        System.out.println(farewell); // Goodbye
        
        // 3. Loading with different locales
        ResourceBundle bundleFr = ResourceBundle.getBundle("Messages", Locale.FRANCE);
        System.out.println(bundleFr.getString("greeting")); // Bonjour
        System.out.println(bundleFr.getString("farewell")); // Au revoir
        
        // 4. Fallback mechanism
        // Search order for "Messages" with Locale("fr", "CA"):
        // 1. Messages_fr_CA.properties
        // 2. Messages_fr.properties
        // 3. Messages_en_US.properties (if default locale is en_US)
        // 4. Messages_en.properties
        // 5. Messages.properties
        // 6. MissingResourceException if not found
        
        // 5. Getting all keys
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            System.out.println(key + " = " + bundle.getString(key));
        }
        
        // 6. Checking if key exists
        if (bundle.containsKey("greeting")) {
            System.out.println("Key exists!");
        }
        
        // 7. Getting Object (not just String)
        Object obj = bundle.getObject("greeting");
        
        // 8. Handling missing keys
        try {
            String missing = bundle.getString("nonexistent");
        } catch (MissingResourceException e) {
            System.out.println("Key not found: " + e.getKey());
        }
        
        // 9. Using PropertyResourceBundle directly
        try (var fis = new java.io.FileInputStream("Messages.properties")) {
            PropertyResourceBundle propBundle = new PropertyResourceBundle(fis);
            System.out.println(propBundle.getString("greeting"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Example property files content:

// Messages.properties (default):
// greeting=Hello
// farewell=Goodbye
// welcome=Welcome {0}!

// Messages_fr.properties:
// greeting=Bonjour
// farewell=Au revoir
// welcome=Bienvenue {0}!

// Messages_fr_FR.properties:
// greeting=Bonjour
// farewell=Au revoir
// welcome=Bienvenue {0}!

// Messages_es.properties:
// greeting=Hola
// farewell=Adiós
// welcome=¡Bienvenido {0}!
```
**Why:** Multi-language application - store UI strings in properties files, load based on user locale, translators edit files.

### Exam Traps
- ❌ **TRAP:** Properties file must be in classpath
  ```java
  ResourceBundle.getBundle("Messages", locale); // Must find Messages.properties in classpath
  ```
- ❌ **TRAP:** MissingResourceException if no bundle found
  ```java
  ResourceBundle.getBundle("NonExistent", locale); // ❌ Exception!
  ```
- ❌ **TRAP:** getString() throws MissingResourceException if key missing
  ```java
  bundle.getString("missing_key"); // ❌ MissingResourceException
  ```
- ❌ **TRAP:** Property file encoding is ISO-8859-1 by default
  ```java
  // Special characters must be Unicode escaped in .properties
  // greeting=Héllo // ❌ Won't display correctly
  // greeting=H\u00e9llo // ✅ Correct Unicode escape
  ```

---

## 3. ResourceBundle Search Order

**Exam Objective:** Understand bundle lookup mechanism

### What is Bundle Search Order?
Algorithm for finding most specific ResourceBundle matching requested locale with fallback.

### When to Use
- Understanding which bundle is loaded
- Debugging missing translations
- Organizing bundle files
- Planning fallback strategy

### Why Understand Search Order
- Predict which bundle loads
- Avoid translation gaps
- Optimize bundle organization
- Debug localization issues

### Real-World Example
```java
import java.util.*;

public class BundleSearchExample {
    public static void main(String[] args) {
        // Available bundles:
        // Messages.properties
        // Messages_en.properties
        // Messages_en_US.properties
        // Messages_fr.properties
        // Messages_fr_CA.properties
        
        // Default locale: en_US
        Locale.setDefault(new Locale("en", "US"));
        
        // 1. Request fr_CA bundle
        Locale requested = new Locale("fr", "CA");
        ResourceBundle bundle = ResourceBundle.getBundle("Messages", requested);
        
        // Search order:
        // 1. Messages_fr_CA.properties ✅ Found! Use this
        // 2. Messages_fr.properties (skip)
        // 3. Messages_en_US.properties (skip)
        // 4. Messages_en.properties (skip)
        // 5. Messages.properties (skip)
        
        System.out.println("Bundle locale: " + bundle.getLocale()); // fr_CA
        
        // 2. Request fr_BE (Belgium French) - not available
        requested = new Locale("fr", "BE");
        bundle = ResourceBundle.getBundle("Messages", requested);
        
        // Search order:
        // 1. Messages_fr_BE.properties (not found)
        // 2. Messages_fr.properties ✅ Found! Use this
        // 3. (skip remaining)
        
        System.out.println("Bundle locale: " + bundle.getLocale()); // fr
        
        // 3. Request de_DE (German) - not available
        requested = new Locale("de", "DE");
        bundle = ResourceBundle.getBundle("Messages", requested);
        
        // Search order:
        // 1. Messages_de_DE.properties (not found)
        // 2. Messages_de.properties (not found)
        // 3. Messages_en_US.properties ✅ Found! (default locale)
        
        System.out.println("Bundle locale: " + bundle.getLocale()); // en_US
        
        // 4. Complete search algorithm
        demonstrateSearchOrder();
    }
    
    static void demonstrateSearchOrder() {
        System.out.println("\n=== Complete Search Order Demo ===");
        
        // Given:
        Locale requestedLocale = new Locale("fr", "CA", "UNIX");
        Locale defaultLocale = new Locale("en", "US");
        Locale.setDefault(defaultLocale);
        
        System.out.println("Requested: " + requestedLocale);
        System.out.println("Default: " + defaultLocale);
        System.out.println("\nSearch order:");
        
        // Step 1: Requested locale with all components
        System.out.println("1. Bundle_fr_CA_UNIX");
        
        // Step 2: Requested locale without variant
        System.out.println("2. Bundle_fr_CA");
        
        // Step 3: Requested locale language only
        System.out.println("3. Bundle_fr");
        
        // Step 4: Default locale with all components (if has variant)
        System.out.println("4. Bundle_en_US (no variant to try)");
        
        // Step 5: Default locale without variant
        System.out.println("5. Bundle_en_US");
        
        // Step 6: Default locale language only
        System.out.println("6. Bundle_en");
        
        // Step 7: Base bundle (no locale suffix)
        System.out.println("7. Bundle");
        
        // Step 8: If none found, throw MissingResourceException
        System.out.println("8. MissingResourceException if none found");
    }
}
```
**Why:** Enterprise app with partial translations - understand which bundles load for incomplete language support.

### Exam Traps
- ❌ **TRAP:** Search uses requested locale first, then default locale
  ```java
  // Requested: fr_CA, Default: en_US
  // Order: fr_CA, fr, en_US, en, base
  ```
- ❌ **TRAP:** Empty bundle != missing bundle
  ```java
  // Messages_fr.properties exists but empty
  // Still counts as "found", won't fallback to Messages.properties
  ```
- ❌ **TRAP:** Underscore separates components, not hyphen
  ```java
  // Messages_en_US.properties ✅ Correct
  // Messages_en-US.properties ❌ Wrong separator
  ```
- ❌ **TRAP:** bundle.getLocale() returns actual bundle locale, not requested
  ```java
  ResourceBundle bundle = ResourceBundle.getBundle("Messages", new Locale("de"));
  bundle.getLocale(); // Might return "en" if no German bundle exists
  ```

---

## 4. NumberFormat - Numbers and Currency

**Exam Objective:** Parse and format numbers including currency

### What is NumberFormat?
Abstract class for formatting and parsing numbers in locale-sensitive manner. Handles integers, decimals, percentages, currency.

### When to Use
- Displaying numbers with regional separators
- Formatting currency amounts
- Showing percentages
- Parsing locale-specific number strings

### Why Use NumberFormat
- Automatic regional formatting
- Currency symbol handling
- Percentage formatting
- Bi-directional (format and parse)

### Real-World Example
```java
import java.text.*;
import java.util.*;

public class NumberFormatExample {
    public static void main(String[] args) throws ParseException {
        double number = 123456.789;
        double price = 1234.56;
        double percent = 0.75;
        
        // 1. Number formatting by locale
        NumberFormat usFormat = NumberFormat.getInstance(Locale.US);
        NumberFormat frFormat = NumberFormat.getInstance(Locale.FRANCE);
        NumberFormat deFormat = NumberFormat.getInstance(Locale.GERMANY);
        
        System.out.println("US: " + usFormat.format(number)); // 123,456.789
        System.out.println("FR: " + frFormat.format(number)); // 123 456,789
        System.out.println("DE: " + deFormat.format(number)); // 123.456,789
        
        // 2. Currency formatting
        NumberFormat usCurrency = NumberFormat.getCurrencyInstance(Locale.US);
        NumberFormat frCurrency = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        NumberFormat jpCurrency = NumberFormat.getCurrencyInstance(Locale.JAPAN);
        
        System.out.println("US: " + usCurrency.format(price)); // $1,234.56
        System.out.println("FR: " + frCurrency.format(price)); // 1 234,56 €
        System.out.println("JP: " + jpCurrency.format(price)); // ¥1,235 (rounded!)
        
        // 3. Percentage formatting
        NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.US);
        System.out.println("Percent: " + percentFormat.format(percent)); // 75%
        
        // 4. Integer formatting (no decimals)
        NumberFormat intFormat = NumberFormat.getIntegerInstance(Locale.US);
        System.out.println("Integer: " + intFormat.format(number)); // 123,457 (rounded)
        
        // 5. Parsing numbers
        NumberFormat parser = NumberFormat.getInstance(Locale.US);
        Number parsed = parser.parse("123,456.789");
        System.out.println("Parsed: " + parsed); // 123456.789
        System.out.println("Type: " + parsed.getClass().getName()); // Long or Double
        
        // 6. Parsing stops at invalid character
        Number partial = parser.parse("123abc456");
        System.out.println("Partial parse: " + partial); // 123 (stops at 'a')
        
        // 7. Parsing currency
        NumberFormat currencyParser = NumberFormat.getCurrencyInstance(Locale.US);
        Number amount = currencyParser.parse("$1,234.56");
        System.out.println("Parsed currency: " + amount); // 1234.56
        
        // 8. Controlling decimal places
        NumberFormat custom = NumberFormat.getInstance();
        custom.setMinimumFractionDigits(2);
        custom.setMaximumFractionDigits(2);
        System.out.println("Fixed decimals: " + custom.format(10)); // 10.00
        
        // 9. Grouping separators
        NumberFormat noGrouping = NumberFormat.getInstance();
        noGrouping.setGroupingUsed(false);
        System.out.println("No grouping: " + noGrouping.format(123456)); // 123456
        
        // 10. Rounding
        NumberFormat rounder = NumberFormat.getInstance();
        rounder.setMaximumFractionDigits(1);
        System.out.println("Rounded: " + rounder.format(1.456)); // 1.5
        
        // 11. Parsing locale-specific
        NumberFormat frParser = NumberFormat.getInstance(Locale.FRANCE);
        Number frNumber = frParser.parse("123 456,789");
        System.out.println("French parsed: " + frNumber); // 123456.789
    }
}
```
**Why:** International e-commerce - display prices in user's currency, format totals with regional separators.

### Exam Traps
- ❌ **TRAP:** parse() stops at first invalid character (no exception)
  ```java
  NumberFormat nf = NumberFormat.getInstance(Locale.US);
  nf.parse("123abc"); // Returns 123, doesn't parse "abc"!
  ```
- ❌ **TRAP:** parse() throws ParseException if string doesn't start with number
  ```java
  nf.parse("abc123"); // ❌ ParseException!
  nf.parse("123abc"); // ✅ Returns 123
  ```
- ❌ **TRAP:** Currency formatting rounds to currency precision
  ```java
  NumberFormat jpy = NumberFormat.getCurrencyInstance(Locale.JAPAN);
  jpy.format(1234.56); // ¥1,235 (no decimal in yen!)
  ```
- ❌ **TRAP:** parse() return type is Number, not Double
  ```java
  Number n = nf.parse("123");
  double d = n; // ❌ Compilation error!
  double d = n.doubleValue(); // ✅ Correct
  ```

---

## 5. DecimalFormat - Custom Number Patterns

**Exam Objective:** Format numbers with custom patterns

### What is DecimalFormat?
Concrete subclass of NumberFormat allowing custom patterns with symbols like #, 0, ., ,

### When to Use
- Custom number formatting
- Specific decimal places
- Special grouping patterns
- Scientific notation

### Why Use DecimalFormat
- Precise formatting control
- Pattern-based syntax
- Custom symbols
- More flexible than NumberFormat

### Real-World Example
```java
import java.text.*;

public class DecimalFormatExample {
    public static void main(String[] args) {
        // Pattern symbols:
        // 0 = digit (shows 0 if absent)
        // # = digit (omits if absent)
        // . = decimal separator
        // , = grouping separator
        // % = multiply by 100 and show as percent
        // ¤ = currency symbol
        
        double number = 1234.567;
        
        // 1. Basic patterns
        DecimalFormat df1 = new DecimalFormat("0.00");
        System.out.println(df1.format(number)); // 1234.57
        System.out.println(df1.format(5)); // 5.00
        
        DecimalFormat df2 = new DecimalFormat("#.##");
        System.out.println(df2.format(number)); // 1234.57
        System.out.println(df2.format(5)); // 5 (no trailing zeros)
        
        // 2. Thousands separator
        DecimalFormat df3 = new DecimalFormat("#,###.00");
        System.out.println(df3.format(1234567.89)); // 1,234,567.89
        
        // 3. Leading zeros
        DecimalFormat df4 = new DecimalFormat("0000.00");
        System.out.println(df4.format(12.5)); // 0012.50
        
        // 4. Percentage
        DecimalFormat df5 = new DecimalFormat("0.00%");
        System.out.println(df5.format(0.756)); // 75.60%
        
        // 5. Currency symbol
        DecimalFormat df6 = new DecimalFormat("¤#,##0.00");
        System.out.println(df6.format(1234.56)); // $1,234.56 (uses default currency)
        
        // 6. Scientific notation
        DecimalFormat df7 = new DecimalFormat("0.###E0");
        System.out.println(df7.format(123456)); // 1.235E5
        
        // 7. Prefix and suffix
        DecimalFormat df8 = new DecimalFormat("$#,##0.00 USD");
        System.out.println(df8.format(1234.56)); // $1,234.56 USD
        
        // 8. Positive and negative patterns
        DecimalFormat df9 = new DecimalFormat("#,##0.00;(#,##0.00)");
        System.out.println(df9.format(1234.56)); // 1,234.56
        System.out.println(df9.format(-1234.56)); // (1,234.56)
        
        // 9. Parsing with pattern
        try {
            DecimalFormat parser = new DecimalFormat("#,###.##");
            Number parsed = parser.parse("1,234.56");
            System.out.println("Parsed: " + parsed.doubleValue()); // 1234.56
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        // 10. Rounding modes
        DecimalFormat df10 = new DecimalFormat("#.#");
        df10.setRoundingMode(java.math.RoundingMode.DOWN);
        System.out.println(df10.format(1.9)); // 1 (rounds down)
        
        df10.setRoundingMode(java.math.RoundingMode.UP);
        System.out.println(df10.format(1.1)); // 2 (rounds up)
        
        // 11. Minimum and maximum digits
        DecimalFormat df11 = new DecimalFormat("0.00");
        df11.setMinimumIntegerDigits(4);
        System.out.println(df11.format(12.5)); // 0012.50
    }
}
```
**Why:** Financial reports - format amounts with specific precision, show negatives in parentheses, add currency symbols.

### Exam Traps
- ❌ **TRAP:** 0 vs # - different behavior for missing digits
  ```java
  new DecimalFormat("0.00").format(5); // 5.00 (shows zeros)
  new DecimalFormat("#.##").format(5); // 5 (omits zeros)
  ```
- ❌ **TRAP:** Percentage multiplies by 100
  ```java
  new DecimalFormat("0.00%").format(0.75); // 75.00% (0.75 * 100)
  ```
- ❌ **TRAP:** Pattern ";" separates positive/negative formats
  ```java
  new DecimalFormat("#,##0.00;(#,##0.00)");
  // Positive: 1,234.56
  // Negative: (1,234.56)
  ```
- ❌ **TRAP:** DecimalFormat is NOT thread-safe
  ```java
  static DecimalFormat df = new DecimalFormat("0.00"); // ❌ Shared mutable!
  // Use ThreadLocal or create new instance per thread
  ```

---

## 6. DateTimeFormatter - Date and Time Formatting

**Exam Objective:** Parse and format dates and times

### What is DateTimeFormatter?
Modern class (Java 8+) for formatting and parsing dates/times. Immutable and thread-safe.

### When to Use
- Formatting LocalDate, LocalTime, LocalDateTime
- Custom date patterns
- Locale-specific date formats
- Parsing date strings

### Why Use DateTimeFormatter
- Thread-safe (unlike SimpleDateFormat)
- Immutable
- Works with modern date-time API
- Rich set of pre-defined formats

### Real-World Example
```java
import java.time.*;
import java.time.format.*;
import java.util.*;

public class DateTimeFormatterExample {
    public static void main(String[] args) {
        LocalDate date = LocalDate.of(2024, 12, 25);
        LocalTime time = LocalTime.of(15, 30, 45);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        
        // 1. Predefined formatters
        System.out.println("ISO_DATE: " + date.format(DateTimeFormatter.ISO_DATE)); 
        // 2024-12-25
        
        System.out.println("ISO_TIME: " + time.format(DateTimeFormatter.ISO_TIME)); 
        // 15:30:45
        
        System.out.println("ISO_DATE_TIME: " + dateTime.format(DateTimeFormatter.ISO_DATE_TIME)); 
        // 2024-12-25T15:30:45
        
        // 2. Localized formatters
        DateTimeFormatter shortDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        DateTimeFormatter mediumDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        DateTimeFormatter longDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
        DateTimeFormatter fullDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        
        System.out.println("SHORT: " + date.format(shortDate)); // 12/25/24
        System.out.println("MEDIUM: " + date.format(mediumDate)); // Dec 25, 2024
        System.out.println("LONG: " + date.format(longDate)); // December 25, 2024
        System.out.println("FULL: " + date.format(fullDate)); // Wednesday, December 25, 2024
        
        // 3. Locale-specific formatting
        DateTimeFormatter usFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
            .withLocale(Locale.US);
        DateTimeFormatter frFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
            .withLocale(Locale.FRANCE);
        DateTimeFormatter deFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
            .withLocale(Locale.GERMANY);
        
        System.out.println("US: " + date.format(usFormat)); // 12/25/24
        System.out.println("FR: " + date.format(frFormat)); // 25/12/2024
        System.out.println("DE: " + date.format(deFormat)); // 25.12.24
        
        // 4. Custom patterns
        DateTimeFormatter custom1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println("Custom 1: " + date.format(custom1)); // 2024-12-25
        
        DateTimeFormatter custom2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("Custom 2: " + date.format(custom2)); // 25/12/2024
        
        DateTimeFormatter custom3 = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        System.out.println("Custom 3: " + date.format(custom3)); // December 25, 2024
        
        DateTimeFormatter custom4 = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy");
        System.out.println("Custom 4: " + date.format(custom4)); // Wed, Dec 25, 2024
        
        // 5. Time patterns
        DateTimeFormatter time12h = DateTimeFormatter.ofPattern("hh:mm:ss a");
        System.out.println("12-hour: " + time.format(time12h)); // 03:30:45 PM
        
        DateTimeFormatter time24h = DateTimeFormatter.ofPattern("HH:mm:ss");
        System.out.println("24-hour: " + time.format(time24h)); // 15:30:45
        
        // 6. Pattern with locale
        DateTimeFormatter frenchDate = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.FRANCE);
        System.out.println("French: " + date.format(frenchDate)); 
        // mercredi 25 décembre 2024
        
        // 7. Parsing dates
        String dateStr = "2024-12-25";
        LocalDate parsed = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
        System.out.println("Parsed: " + parsed); // 2024-12-25
        
        // 8. Parsing custom format
        String customStr = "25/12/2024";
        DateTimeFormatter customParser = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate parsedCustom = LocalDate.parse(customStr, customParser);
        System.out.println("Parsed custom: " + parsedCustom); // 2024-12-25
        
        // 9. Pattern symbols reference
        demonstratePatternSymbols();
        
        // 10. Common patterns
        demonstrateCommonPatterns(dateTime);
    }
    
    static void demonstratePatternSymbols() {
        System.out.println("\n=== Pattern Symbols ===");
        LocalDateTime dt = LocalDateTime.of(2024, 3, 5, 14, 30, 45);
        
        System.out.println("y (year): " + dt.format(DateTimeFormatter.ofPattern("y"))); // 2024
        System.out.println("yy (2-digit): " + dt.format(DateTimeFormatter.ofPattern("yy"))); // 24
        System.out.println("M (month): " + dt.format(DateTimeFormatter.ofPattern("M"))); // 3
        System.out.println("MM (2-digit): " + dt.format(DateTimeFormatter.ofPattern("MM"))); // 03
        System.out.println("MMM (short): " + dt.format(DateTimeFormatter.ofPattern("MMM"))); // Mar
        System.out.println("MMMM (full): " + dt.format(DateTimeFormatter.ofPattern("MMMM"))); // March
        System.out.println("d (day): " + dt.format(DateTimeFormatter.ofPattern("d"))); // 5
        System.out.println("dd (2-digit): " + dt.format(DateTimeFormatter.ofPattern("dd"))); // 05
        System.out.println("E (day of week): " + dt.format(DateTimeFormatter.ofPattern("E"))); // Tue
        System.out.println("EEEE (full): " + dt.format(DateTimeFormatter.ofPattern("EEEE"))); // Tuesday
        System.out.println("h (12-hour): " + dt.format(DateTimeFormatter.ofPattern("h"))); // 2
        System.out.println("hh (2-digit): " + dt.format(DateTimeFormatter.ofPattern("hh"))); // 02
        System.out.println("H (24-hour): " + dt.format(DateTimeFormatter.ofPattern("H"))); // 14
        System.out.println("HH (2-digit): " + dt.format(DateTimeFormatter.ofPattern("HH"))); // 14
        System.out.println("m (minute): " + dt.format(DateTimeFormatter.ofPattern("m"))); // 30
        System.out.println("s (second): " + dt.format(DateTimeFormatter.ofPattern("s"))); // 45
        System.out.println("a (AM/PM): " + dt.format(DateTimeFormatter.ofPattern("a"))); // PM
    }
    
    static void demonstrateCommonPatterns(LocalDateTime dt) {
        System.out.println("\n=== Common Patterns ===");
        
        System.out.println(dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); 
        // 2024-12-25
        
        System.out.println(dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))); 
        // 25/12/2024
        
        System.out.println(dt.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))); 
        // 12/25/2024
        
        System.out.println(dt.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))); 
        // December 25, 2024
        
        System.out.println(dt.format(DateTimeFormatter.ofPattern("EEE, MMM dd yyyy"))); 
        // Wed, Dec 25 2024
        
        System.out.println(dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))); 
        // 2024-12-25 15:30:45
        
        System.out.println(dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"))); 
        // 25/12/2024 03:30 PM
    }
}
```
**Why:** Booking system - display reservation dates in user's locale, parse user input dates, show times in 12/24 hour format.

### Exam Traps
- ❌ **TRAP:** M vs m - month vs minute
  ```java
  DateTimeFormatter.ofPattern("MM"); // Month (01-12)
  DateTimeFormatter.ofPattern("mm"); // Minute (00-59)
  ```
- ❌ **TRAP:** H vs h - 24-hour vs 12-hour
  ```java
  DateTimeFormatter.ofPattern("HH"); // 00-23 (24-hour)
  DateTimeFormatter.ofPattern("hh"); // 01-12 (12-hour)
  ```
- ❌ **TRAP:** Single vs multiple pattern letters
  ```java
  "M" // 3 (single digit)
  "MM" // 03 (two digits)
  "MMM" // Mar (short name)
  "MMMM" // March (full name)
  ```
- ❌ **TRAP:** Case sensitivity matters
  ```java
  "yyyy" // Year (2024)
  "YYYY" // ❌ Week-based year! Different meaning
  "dd" // Day of month
  "DD" // Day of year
  ```

---

## 7. MessageFormat - Parameterized Messages

**Exam Objective:** Parse and format messages with parameters

### What is MessageFormat?
Class for creating messages with placeholders ({0}, {1}) that are replaced at runtime. Supports numbers, dates, choices.

### When to Use
- Dynamic message generation
- Pluralization
- Variable substitution
- Locale-specific messages

### Why Use MessageFormat
- Parameter substitution
- Automatic formatting by type
- Choice patterns for plurals
- Works with ResourceBundle

### Real-World Example
```java
import java.text.*;
import java.util.*;

public class MessageFormatExample {
    public static void main(String[] args) {
        // 1. Basic message formatting
        String pattern = "Hello {0}, you have {1} new messages.";
        String message = MessageFormat.format(pattern, "Alice", 5);
        System.out.println(message); // Hello Alice, you have 5 new messages.
        
        // 2. Multiple parameters
        String pattern2 = "On {0}, {1} sold {2} tickets for ${3}.";
        String msg2 = MessageFormat.format(pattern2, 
            "Monday", "John", 150, 4500.50);
        System.out.println(msg2); 
        // On Monday, John sold 150 tickets for $4,500.50.
        
        // 3. Format with types
        String pattern3 = "Date: {0,date}, Time: {0,time}, Number: {1,number}";
        String msg3 = MessageFormat.format(pattern3, new Date(), 1234567.89);
        System.out.println(msg3);
        // Date: Dec 9, 2024, Time: 3:30:45 PM, Number: 1,234,567.89
        
        // 4. Date/time styles
        String pattern4 = "Short: {0,date,short}, Long: {0,date,long}, Full: {0,date,full}";
        String msg4 = MessageFormat.format(pattern4, new Date());
        System.out.println(msg4);
        // Short: 12/9/24, Long: December 9, 2024, Full: Monday, December 9, 2024
        
        // 5. Number styles
        String pattern5 = "Number: {0,number}, Currency: {0,number,currency}, Percent: {1,number,percent}";
        String msg5 = MessageFormat.format(pattern5, 1234.56, 0.75);
        System.out.println(msg5);
        // Number: 1,234.56, Currency: $1,234.56, Percent: 75%
        
        // 6. Custom number pattern
        String pattern6 = "Price: {0,number,#,##0.00}";
        String msg6 = MessageFormat.format(pattern6, 1234.5);
        System.out.println(msg6); // Price: 1,234.50
        
        // 7. Choice format (pluralization)
        String pattern7 = "There {0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}.";
        System.out.println(MessageFormat.format(pattern7, 0)); 
        // There are no files.
        System.out.println(MessageFormat.format(pattern7, 1)); 
        // There is one file.
        System.out.println(MessageFormat.format(pattern7, 5)); 
        // There are 5 files.
        
        // 8. With locale
        MessageFormat mf = new MessageFormat("Amount: {0,number,currency}", Locale.FRANCE);
        System.out.println(mf.format(new Object[]{1234.56})); 
        // Amount: 1 234,56 €
        
        // 9. Escaping special characters
        String pattern9 = "Use '{'0'}' for parameter, ''text'' for quotes.";
        String msg9 = MessageFormat.format(pattern9);
        System.out.println(msg9); 
        // Use {0} for parameter, 'text' for quotes.
        
        // 10. With ResourceBundle
        demonstrateWithResourceBundle();
    }
    
    static void demonstrateWithResourceBundle() {
        System.out.println("\n=== With ResourceBundle ===");
        
        // messages.properties:
        // welcome=Welcome {0}! You have {1} items in your cart.
        // order=Order #{0} placed on {1,date,short} for {2,number,currency}.
        
        ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.US);
        
        String welcomePattern = bundle.getString("welcome");
        String welcomeMsg = MessageFormat.format(welcomePattern, "Bob", 3);
        System.out.println(welcomeMsg);
        // Welcome Bob! You have 3 items in your cart.
        
        String orderPattern = bundle.getString("order");
        String orderMsg = MessageFormat.format(orderPattern, 
            "12345", new Date(), 299.99);
        System.out.println(orderMsg);
        // Order #12345 placed on 12/9/24 for $299.99.
    }
}
```
**Why:** Email templates - generate personalized messages with user names, counts, dates, amounts in user's locale.

### Exam Traps
- ❌ **TRAP:** Indices start at 0
  ```java
  "{0}" // First parameter
  "{1}" // Second parameter
  ```
- ❌ **TRAP:** Single quotes escape patterns
  ```java
  "'{0}'" // Literal {0} (not placeholder)
  "''" // Single quote
  "Don''t" // Don't (double '' for single ')
  ```
- ❌ **TRAP:** Choice format uses # for ranges
  ```java
  "{0,choice,0#no items|1#one item|1<many items}"
  // 0 → no items
  // 1 → one item
  // 2+ → many items
  ```
- ❌ **TRAP:** Commas separate format type, style, pattern
  ```java
  "{0,number,currency}" // type=number, style=currency
  "{0,date,short}" // type=date, style=short
  ```

---

## 8. Properties Class

**Exam Objective:** Work with properties files

### What is Properties?
Hashtable subclass for reading/writing key-value pairs from .properties files. Simple configuration storage.

### When to Use
- Application configuration
- Simple key-value storage
- Locale-specific settings
- Resource bundle backing

### Why Use Properties
- Simple text format
- Easy editing
- Built-in I/O methods
- Integration with ResourceBundle

### Real-World Example
```java
import java.util.*;
import java.io.*;

public class PropertiesExample {
    public static void main(String[] args) {
        // 1. Creating Properties
        Properties props = new Properties();
        props.setProperty("username", "admin");
        props.setProperty("password", "secret");
        props.setProperty("timeout", "30");
        props.setProperty("debug", "true");
        
        // 2. Reading properties
        String username = props.getProperty("username");
        String timeout = props.getProperty("timeout");
        String missing = props.getProperty("nonexistent"); // null
        String withDefault = props.getProperty("nonexistent", "default"); // default
        
        System.out.println("Username: " + username);
        System.out.println("Timeout: " + timeout);
        System.out.println("Missing: " + missing);
        System.out.println("With default: " + withDefault);
        
        // 3. Saving to file
        try (FileOutputStream fos = new FileOutputStream("config.properties")) {
            props.store(fos, "Application Configuration");
            // Creates file with comments and properties
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 4. Loading from file
        Properties loaded = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            loaded.load(fis);
            System.out.println("Loaded: " + loaded.getProperty("username"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 5. Loading from XML
        try (FileOutputStream fos = new FileOutputStream("config.xml")) {
            props.storeToXML(fos, "XML Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Properties xmlProps = new Properties();
        try (FileInputStream fis = new FileInputStream("config.xml")) {
            xmlProps.loadFromXML(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 6. Default properties (fallback)
        Properties defaults = new Properties();
        defaults.setProperty("language", "en");
        defaults.setProperty("theme", "light");
        
        Properties custom = new Properties(defaults);
        custom.setProperty("theme", "dark"); // Override
        
        System.out.println("Language: " + custom.getProperty("language")); // en (from defaults)
        System.out.println("Theme: " + custom.getProperty("theme")); // dark (overridden)
        
        // 7. Listing all properties
        System.out.println("\n=== All Properties ===");
        props.list(System.out);
        
        // 8. Enumerating keys
        Enumeration<?> keys = props.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            System.out.println(key + " = " + props.getProperty(key));
        }
        
        // 9. Using as Map
        props.forEach((key, value) -> 
            System.out.println(key + " -> " + value));
        
        // 10. Checking existence
        if (props.containsKey("username")) {
            System.out.println("Username exists");
        }
        
        // 11. Removing properties
        props.remove("password");
        
        // 12. Clearing all
        props.clear();
    }
}

// Example config.properties file:
// # Application Configuration
// # Mon Dec 09 15:30:00 GMT 2024
// username=admin
// password=secret
// timeout=30
// debug=true
```
**Why:** Application configuration - store database credentials, timeouts, feature flags in properties files.

### Exam Traps
- ❌ **TRAP:** getProperty() returns null if key missing
  ```java
  props.getProperty("missing"); // null, not exception
  props.getProperty("missing", "default"); // Use default
  ```
- ❌ **TRAP:** Properties file uses ISO-8859-1 encoding
  ```java
  // Unicode characters must be escaped
  // name=Café // ❌ Won't display correctly
  // name=Caf\u00e9 // ✅ Correct escape
  ```
- ❌ **TRAP:** Comments use # or !
  ```java
  // # This is a comment
  // ! This is also a comment
  // key=value // This is the property
  ```
- ❌ **TRAP:** load() vs loadFromXML() - different formats
  ```java
  props.load(stream); // .properties format
  props.loadFromXML(stream); // XML format
  ```

---

## 9. Locale Categories

**Exam Objective:** Understand DISPLAY vs FORMAT categories

### What are Locale Categories?
Java 7+ feature separating display (UI text) and format (numbers/dates) locales.

### When to Use
- Different locales for UI and data
- Bilingual applications
- International reporting
- Custom locale combinations

### Why Use Locale Categories
- Fine-grained control
- Separate concerns
- Flexibility in localization
- Better user customization

### Real-World Example
```java
import java.util.*;
import java.text.*;
import java.time.*;
import java.time.format.*;

public class LocaleCategoriesExample {
    public static void main(String[] args) {
        // Scenario: French-Canadian user wants English UI but Canadian formats
        
        // 1. Setting category-specific locales
        Locale.setDefault(Locale.Category.DISPLAY, Locale.US); // English UI
        Locale.setDefault(Locale.Category.FORMAT, Locale.CANADA_FRENCH); // French formats
        
        System.out.println("Display locale: " + 
            Locale.getDefault(Locale.Category.DISPLAY)); // en_US
        System.out.println("Format locale: " + 
            Locale.getDefault(Locale.Category.FORMAT)); // fr_CA
        
        // 2. Impact on ResourceBundle
        ResourceBundle bundle = ResourceBundle.getBundle("Messages");
        // Uses DISPLAY locale (en_US) to load Messages_en_US.properties
        System.out.println("UI text: " + bundle.getString("greeting")); // Hello
        
        // 3. Impact on number formatting
        NumberFormat nf = NumberFormat.getInstance();
        // Uses FORMAT locale (fr_CA)
        System.out.println("Number: " + nf.format(123456.789)); // 123 456,789
        
        // 4. Impact on currency
        NumberFormat cf = NumberFormat.getCurrencyInstance();
        // Uses FORMAT locale (fr_CA)
        System.out.println("Currency: " + cf.format(1234.56)); // 1 234,56 $
        
        // 5. Impact on dates
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        // Uses FORMAT locale (fr_CA)
        System.out.println("Date: " + LocalDate.now().format(dtf)); // 2024-12-09
        
        // 6. Overriding category defaults
        NumberFormat usFormat = NumberFormat.getInstance(Locale.US);
        System.out.println("US format: " + usFormat.format(123456.789)); // 123,456.789
        
        // 7. Complete example
        demonstrateFullScenario();
    }
    
    static void demonstrateFullScenario() {
        System.out.println("\n=== Complete Scenario ===");
        
        // User preferences: Spanish UI, US formats
        Locale.setDefault(Locale.Category.DISPLAY, new Locale("es", "ES"));
        Locale.setDefault(Locale.Category.FORMAT, Locale.US);
        
        // UI elements (ResourceBundle)
        // Uses DISPLAY locale
        ResourceBundle messages = ResourceBundle.getBundle("Messages");
        System.out.println("Welcome message: " + messages.getString("welcome"));
        // Bienvenido (Spanish)
        
        // Display locale info
        Locale displayLocale = Locale.getDefault(Locale.Category.DISPLAY);
        System.out.println("Display: " + displayLocale.getDisplayName());
        // español (España)
        
        // Number formatting (uses FORMAT locale)
        double amount = 1234567.89;
        NumberFormat numberFormat = NumberFormat.getInstance();
        System.out.println("Amount: " + numberFormat.format(amount));
        // 1,234,567.89 (US format)
        
        // Currency formatting
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        System.out.println("Price: " + currencyFormat.format(amount));
        // $1,234,567.89 (US currency)
        
        // Date formatting
        LocalDate date = LocalDate.of(2024, 12, 25);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
        System.out.println("Date: " + date.format(dateFormatter));
        // December 25, 2024 (US format)
        
        // Percentage
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        System.out.println("Percent: " + percentFormat.format(0.75));
        // 75% (US format)
        
        System.out.println("\nResult: Spanish UI with US number/date formats!");
    }
}
```
**Why:** Corporate application - English menus/labels (DISPLAY), but local number/date formats (FORMAT) for international users.

### Exam Traps
- ❌ **TRAP:** Only 2 categories exist
  ```java
  Locale.Category.DISPLAY // For ResourceBundle
  Locale.Category.FORMAT // For NumberFormat, DateFormat
  // No other categories!
  ```
- ❌ **TRAP:** setDefault(Locale) sets BOTH categories
  ```java
  Locale.setDefault(Locale.FRANCE); // Sets both DISPLAY and FORMAT
  
  Locale.setDefault(Locale.Category.DISPLAY, Locale.US); 
  Locale.setDefault(Locale.Category.FORMAT, Locale.FRANCE);
  // Sets separately
  ```
- ❌ **TRAP:** ResourceBundle uses DISPLAY, formatters use FORMAT
  ```java
  ResourceBundle.getBundle("Messages"); // Uses DISPLAY locale
  NumberFormat.getInstance(); // Uses FORMAT locale
  ```
- ❌ **TRAP:** getDefault() without category returns DISPLAY
  ```java
  Locale.getDefault(); // Returns DISPLAY locale
  Locale.getDefault(Locale.Category.FORMAT); // Returns FORMAT locale
  ```

---

## 10. Localization Best Practices

**Exam Objective:** Apply localization effectively

### What are Best Practices?
Patterns and techniques for robust, maintainable localization in applications.

### When to Apply
- Designing international applications
- Maintaining localized code
- Avoiding common pitfalls
- Optimizing performance

### Why Follow Best Practices
- Avoid bugs
- Improve maintainability
- Better user experience
- Easier translation workflow

### Real-World Example
```java
import java.util.*;
import java.text.*;
import java.time.*;
import java.time.format.*;

public class LocalizationBestPractices {
    
    // PRACTICE 1: Cache formatters (they're expensive to create)
    private static final Map<Locale, NumberFormat> CURRENCY_FORMATTERS = new HashMap<>();
    
    public static NumberFormat getCurrencyFormatter(Locale locale) {
        return CURRENCY_FORMATTERS.computeIfAbsent(
            locale, NumberFormat::getCurrencyInstance);
    }
    
    // PRACTICE 2: Use constants for resource keys
    public static class Keys {
        public static final String GREETING = "greeting";
        public static final String FAREWELL = "farewell";
        public static final String ERROR_NOT_FOUND = "error.notfound";
        // Prevents typos, enables refactoring
    }
    
    // PRACTICE 3: Encapsulate ResourceBundle access
    public static class Messages {
        private final ResourceBundle bundle;
        
        public Messages(Locale locale) {
            this.bundle = ResourceBundle.getBundle("Messages", locale);
        }
        
        public String get(String key) {
            try {
                return bundle.getString(key);
            } catch (MissingResourceException e) {
                return "???" + key + "???"; // Fallback
            }
        }
        
        public String format(String key, Object... args) {
            String pattern = get(key);
            return MessageFormat.format(pattern, args);
        }
    }
    
    // PRACTICE 4: Don't concatenate localized strings
    public static void badExample() {
        // ❌ BAD - word order varies by language
        String message = "Welcome " + username + "!";
        
        // ✅ GOOD - use MessageFormat
        String pattern = bundle.getString("welcome.message");
        String message = MessageFormat.format(pattern, username);
        // welcome.message=Welcome {0}! (English)
        // welcome.message=¡Bienvenido {0}! (Spanish)
        // welcome.message=Bienvenue {0}! (French)
    }
    
    // PRACTICE 5: Store numbers/dates in neutral format
    public static void dataStorage() {
        // ❌ BAD - storing localized format
        String stored = "1.234,56"; // German format
        
        // ✅ GOOD - store in standard format
        double value = 1234.56;
        String stored = String.valueOf(value); // "1234.56"
        
        // Format only for display
        NumberFormat formatter = NumberFormat.getInstance(locale);
        String display = formatter.format(value);
    }
    
    // PRACTICE 6: Handle missing translations gracefully
    public static String getSafeString(ResourceBundle bundle, String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            // Log the error
            System.err.println("Missing key: " + key);
            // Return something useful
            return "[" + key + "]";
        }
    }
    
    // PRACTICE 7: Don't assume text length
    public static void uiLayout() {
        // ❌ BAD - fixed width assumes English
        // "OK" button width works for English (2 chars)
        // but not for German "Bestätigen" (11 chars)
        
        // ✅ GOOD - dynamic layout based on content
        // Use layout managers that adjust to text size
    }
    
    // PRACTICE 8: Externalize ALL user-visible strings
    public static void stringExternalization() {
        // ❌ BAD - hardcoded string
        System.out.println("Error: File not found");
        
        // ✅ GOOD - from resource bundle
        ResourceBundle bundle = ResourceBundle.getBundle("Messages", locale);
        System.out.println(bundle.getString("error.filenotfound"));
    }
    
    // PRACTICE 9: Use appropriate data types for parsing
    public static void parsing() throws ParseException {
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        
        Number parsed = nf.parse("123,456.789");
        
        // ❌ BAD - assuming type
        int value = (int) parsed; // May throw ClassCastException
        
        // ✅ GOOD - safe conversion
        int value = parsed.intValue();
        double decimal = parsed.doubleValue();
    }
    
    // PRACTICE 10: Thread-safe formatter usage
    private static final ThreadLocal<DateTimeFormatter> DATE_FORMATTER = 
        ThreadLocal.withInitial(() -> 
            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
    
    public static String formatDate(LocalDate date) {
        // Thread-safe access to formatter
        return date.format(DATE_FORMATTER.get());
    }
    
    // PRACTICE 11: Proper property file organization
    // ✅ GOOD structure:
    // messages/
    //   Messages.properties (base)
    //   Messages_en.properties (English)
    //   Messages_en_US.properties (US English)
    //   Messages_en_GB.properties (British English)
    //   Messages_fr.properties (French)
    //   Messages_fr_FR.properties (France French)
    //   Messages_fr_CA.properties (Canadian French)
    
    // PRACTICE 12: Test with pseudo-localization
    public static void pseudoLocalization() {
        // Create test locale with:
        // - Longer strings (test UI overflow)
        // - Special characters (test encoding)
        // - Right-to-left text (test layout)
        
        // Example: [!!!Welcome!!!] instead of "Welcome"
        // Helps identify hardcoded strings and layout issues
    }
    
    public static void main(String[] args) {
        demonstrateBestPractices();
    }
    
    static void demonstrateBestPractices() {
        System.out.println("=== Localization Best Practices ===\n");
        
        Locale locale = Locale.US;
        Messages messages = new Messages(locale);
        
        // 1. Using cached formatter
        NumberFormat cf = getCurrencyFormatter(locale);
        System.out.println("Price: " + cf.format(1234.56));
        
        // 2. Using key constants
        System.out.println(messages.get(Keys.GREETING));
        
        // 3. Using parameterized messages
        System.out.println(messages.format("welcome.user", "Alice"));
        
        // 4. Safe handling of missing keys
        System.out.println(messages.get("nonexistent.key")); // ???nonexistent.key???
        
        // 5. Proper date formatting
        LocalDate date = LocalDate.now();
        System.out.println("Date: " + formatDate(date));
        
        System.out.println("\nKey Takeaways:");
        System.out.println("✓ Cache expensive objects");
        System.out.println("✓ Use constants for keys");
        System.out.println("✓ Encapsulate bundle access");
        System.out.println("✓ Never concatenate localized strings");
        System.out.println("✓ Handle missing resources gracefully");
        System.out.println("✓ Externalize all user-visible strings");
        System.out.println("✓ Use thread-safe patterns");
        System.out.println("✓ Test with multiple locales");
    }
}
```
**Why:** Production application - avoid common localization bugs, improve performance, maintain clean architecture.

### Exam Traps
- ❌ **TRAP:** Formatters are expensive - cache them
  ```java
  // ❌ BAD - creates new formatter every time
  for (int i = 0; i < 1000; i++) {
      NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
  }
  
  // ✅ GOOD - reuse formatter
  NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
  for (int i = 0; i < 1000; i++) {
      String formatted = nf.format(amount);
  }
  ```
- ❌ **TRAP:** Don't concatenate localized strings
  ```java
  "Welcome " + username + "!"; // ❌ Word order varies by language
  MessageFormat.format(pattern, username); // ✅ Use placeholders
  ```
- ❌ **TRAP:** Property files use ISO-8859-1 encoding
  ```java
  // ❌ Special chars won't display: Café
  // ✅ Use Unicode escapes: Caf\u00e9
  ```
- ❌ **TRAP:** SimpleDateFormat is NOT thread-safe
  ```java
  static SimpleDateFormat sdf = new SimpleDateFormat(); // ❌ Shared mutable!
  // Use DateTimeFormatter (thread-safe) or ThreadLocal
  ```

---

## Exam Questions

### Question 1
```java
Locale locale = new Locale("fr", "ca");
System.out.println(locale.getLanguage());
System.out.println(locale.getCountry());
```

What is printed?

A. fr CA  
B. FR ca  
C. fr ca  
D. FR CA  
E. Compilation error

**Answer:** C

**Explanation:** Language code is lowercase ("fr"), country code is uppercase internally but getCountry() returns exactly what was passed ("ca"). The constructor doesn't convert case. Correct would be new Locale("fr", "CA"). (Exam Objective: Implement localization using locales)

---

### Question 2
```java
ResourceBundle bundle = ResourceBundle.getBundle("Messages", new Locale("fr", "CA"));
String value = bundle.getString("missing");
```

Assume "missing" key doesn't exist in any bundle. What happens?

A. Returns null  
B. Returns empty string  
C. MissingResourceException  
D. Returns "missing"  
E. Compilation error

**Answer:** C

**Explanation:** getString() throws MissingResourceException when key is not found in any bundle in the search chain. It doesn't return null or empty string. (Exam Objective: Implement localization using resource bundles)

---

### Question 3
```java
NumberFormat nf = NumberFormat.getInstance(Locale.US);
Number n = nf.parse("123abc");
System.out.println(n);
```

What is printed?

A. 123abc  
B. 123  
C. ParseException  
D. 0  
E. null

**Answer:** B

**Explanation:** parse() stops at first non-numeric character and returns what it parsed so far. "123abc" parses to 123, "abc" is ignored. No exception thrown. (Exam Objective: Parse and format numbers)

---

### Question 4
```java
NumberFormat cf = NumberFormat.getCurrencyInstance(Locale.JAPAN);
System.out.println(cf.format(1234.56));
```

What is printed?

A. ¥1,234.56  
B. ¥1,235  
C. 1,234.56¥  
D. 1,235¥  
E. $1,234.56

**Answer:** B

**Explanation:** Japanese Yen has no decimal places. Currency formatting rounds 1234.56 to 1235. Symbol (¥) is prepended. (Exam Objective: Parse and format currency values)

---

### Question 5
```java
DecimalFormat df = new DecimalFormat("0.00");
System.out.println(df.format(5));
System.out.println(df.format(5.1));
```

What is printed?

A. 5 5.1  
B. 5.00 5.10  
C. 5.0 5.1  
D. 5.00 5.1  
E. Compilation error

**Answer:** B

**Explanation:** Pattern "0.00" forces 2 decimal places. "0" means show zero if digit absent. Output: "5.00" and "5.10". (Exam Objective: Format numbers with custom patterns)

---

### Question 6
```java
DecimalFormat df = new DecimalFormat("#.##");
System.out.println(df.format(5));
System.out.println(df.format(5.1));
```

What is printed?

A. 5 5.1  
B. 5.00 5.10  
C. 5.0 5.1  
D. 5.## 5.1#  
E. Compilation error

**Answer:** A

**Explanation:** Pattern "#.##" omits trailing zeros. "#" means omit if digit absent. Output: "5" (no decimals shown) and "5.1". (Exam Objective: Format numbers with custom patterns)

---

### Question 7
```java
DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
LocalDate date = LocalDate.of(2024, 3, 5);
System.out.println(date.format(dtf));
```

What is printed?

A. 03/05/2024  
B. 05/03/2024  
C. 3/5/2024  
D. 2024/03/05  
E. Compilation error

**Answer:** A

**Explanation:** Pattern "MM/dd/yyyy": MM=month with leading zero (03), dd=day with leading zero (05), yyyy=year (2024). Output: "03/05/2024". (Exam Objective: Parse and format dates)

---

### Question 8
```java
DateTimeFormatter dtf = DateTimeFormatter.ofPattern("mm/dd/yyyy");
LocalDate date = LocalDate.of(2024, 3, 5);
System.out.println(date.format(dtf));
```

What happens?

A. Prints 03/05/2024  
B. Prints 00/05/2024  
C. Prints 05/03/2024  
D. DateTimeException  
E. Compilation error

**Answer:** D

**Explanation:** "mm" is MINUTE, not month! LocalDate has no time component. Trying to format minute from date throws DateTimeException. Use "MM" for month. (Exam Objective: Parse and format dates)

---

### Question 9
```java
String pattern = "Hello {0}, you have {1} messages.";
String msg = MessageFormat.format(pattern, "Alice");
System.out.println(msg);
```

What happens?

A. Prints: Hello Alice, you have {1} messages.  
B. Prints: Hello Alice, you have null messages.  
C. IllegalArgumentException  
D. Prints: Hello Alice, you have messages.  
E. Compilation error

**Answer:** A

**Explanation:** MessageFormat replaces provided parameters ({0} with "Alice") and leaves missing parameters unreplaced ({1} stays as literal "{1}"). No exception thrown. (Exam Objective: Parse and format messages)

---

### Question 10
```java
String pattern = "Price: '{0}'";
String msg = MessageFormat.format(pattern, 100);
System.out.println(msg);
```

What is printed?

A. Price: {0}  
B. Price: '100'  
C. Price: 100  
D. Price: '{0}'  
E. IllegalArgumentException

**Answer:** A

**Explanation:** Single quotes escape pattern. '{0}' is literal {0}, not placeholder. Output: "Price: {0}". To show quotes and parameter, use double single quotes: "Price: ''{0}''". (Exam Objective: Parse and format messages)

---

### Question 11
```java
Locale.setDefault(Locale.FRANCE);
NumberFormat nf = NumberFormat.getInstance();
System.out.println(nf.format(1234.56));
```

What is printed?

A. 1,234.56  
B. 1.234,56  
C. 1 234,56  
D. 1234.56  
E. Depends on system locale

**Answer:** C

**Explanation:** French locale uses space as thousands separator and comma as decimal separator. Output: "1 234,56". (Exam Objective: Format numbers with locale)

---

### Question 12
```java
Locale.setDefault(Locale.Category.DISPLAY, Locale.FRANCE);
Locale.setDefault(Locale.Category.FORMAT, Locale.US);

NumberFormat nf = NumberFormat.getInstance();
System.out.println(nf.format(1234.56));
```

What is printed?

A. 1 234,56 (French)  
B. 1,234.56 (US)  
C. Depends on system  
D. Compilation error  
E. 1234.56

**Answer:** B

**Explanation:** NumberFormat uses FORMAT category locale. FORMAT is set to US, so US formatting: "1,234.56". DISPLAY category doesn't affect number formatting. (Exam Objective: Understand locale categories)

---

### Question 13
```java
Properties props = new Properties();
props.setProperty("name", "Alice");
String value = props.getProperty("age");
System.out.println(value);
```

What is printed?

A. null  
B. Empty string  
C. Exception  
D. "age"  
E. Compilation error

**Answer:** A

**Explanation:** getProperty() returns null when key doesn't exist. No exception thrown. Use getProperty("age", "default") to provide default value. (Exam Objective: Work with properties)

---

### Question 14
```java
ResourceBundle bundle = ResourceBundle.getBundle("Messages", new Locale("fr", "BE"));

// Available bundles:
// Messages.properties
// Messages_fr.properties
// Messages_fr_FR.properties

// Which bundle is loaded?
```

A. Messages.properties  
B. Messages_fr.properties  
C. Messages_fr_FR.properties  
D. Messages_fr_BE.properties  
E. MissingResourceException

**Answer:** B

**Explanation:** Search order for fr_BE: Messages_fr_BE (not found), Messages_fr (found!). Messages_fr.properties is loaded. (Exam Objective: Understand bundle search order)

---

### Question 15
```java
new Locale("EN", "us");
```

What's wrong with this code?

A. Nothing, it's correct  
B. Language should be lowercase  
C. Country should be uppercase  
D. Both language and country are wrong case  
E. Compilation error

**Answer:** D

**Explanation:** Language should be lowercase ("en"), country should be uppercase ("US"). Constructor doesn't validate or convert case, but convention is "en_US". (Exam Objective: Create locales correctly)

---

### Question 16
```java
DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");
LocalDate date = LocalDate.of(2024, 12, 31);
System.out.println(date.format(dtf));
```

What might be unexpected?

A. Prints 2024-12-31 correctly  
B. YYYY is week-based year, might print 2025  
C. Compilation error  
D. Prints 2024-12-31 always  
E. DateTimeException

**Answer:** B

**Explanation:** "YYYY" is week-based year (ISO week date), not calendar year! Use "yyyy" for calendar year. Dec 31, 2024 might be in week 1 of 2025, printing "2025-12-31". (Exam Objective: Use correct date patterns)

---

### Question 17
```java
NumberFormat nf = NumberFormat.getInstance(Locale.US);
Number n = nf.parse("abc123");
```

What happens?

A. Returns 0  
B. Returns null  
C. ParseException  
D. Returns 123  
E. Compilation error

**Answer:** C

**Explanation:** parse() throws ParseException if string doesn't START with valid number. "abc123" starts with letters, throws exception. Compare with "123abc" which returns 123. (Exam Objective: Parse numbers)

---

### Question 18
```java
Locale locale = new Locale("en", "US");
// Java 19+
```

What's the issue?

A. Nothing wrong  
B. Should use Locale.US constant  
C. Constructor deprecated, use Locale.of()  
D. Wrong parameter order  
E. Compilation error

**Answer:** C

**Explanation:** Locale constructors are deprecated in Java 19+. Use Locale.of("en", "US") or Locale.US constant instead. (Exam Objective: Use modern Locale API)

---

### Question 19
```java
String pattern = "Don't use {0}!";
String msg = MessageFormat.format(pattern, "this");
System.out.println(msg);
```

What is printed?

A. Don't use this!  
B. Dont use this!  
C. Don't use {0}!  
D. Don use this!  
E. IllegalArgumentException

**Answer:** D

**Explanation:** Single quote escapes in MessageFormat. "Don't" is interpreted as "Don" + escaped "t use". Must use double single quotes: "Don''t". Output is malformed: "Don use this!". (Exam Objective: Format messages)

---

### Question 20
```java
DecimalFormat df = new DecimalFormat("0.00%");
System.out.println(df.format(0.75));
```

What is printed?

A. 0.75%  
B. 75%  
C. 75.00%  
D. 0.75  
E. 7500%

**Answer:** C

**Explanation:** "%" in pattern multiplies by 100 and appends % symbol. 0.75 * 100 = 75. Pattern "0.00" forces 2 decimals. Output: "75.00%". (Exam Objective: Format percentages)

---

## Chapter Summary

### Official 1Z0-830 Exam Objectives Checklist

✅ **Implement localization using locales and resource bundles**
- Create and use Locale objects
- Load ResourceBundle for different locales
- Understand bundle search order
- Use Properties for configuration

✅ **Parse and format messages, dates, times, and numbers**
- NumberFormat for numbers and currency
- DecimalFormat for custom patterns
- DateTimeFormatter for dates and times
- MessageFormat for parameterized messages

---

### Key Points for Exam

1. **Locale Basics**
   - Language lowercase, country uppercase
   - Constructors deprecated Java 19+ (use Locale.of())
   - setDefault() affects entire JVM
   - Predefined constants (US, UK, FRANCE, etc.)

2. **ResourceBundle**
   - Throws MissingResourceException if not found
   - getString() throws exception for missing keys
   - Search order: requested → default → base
   - Property files use ISO-8859-1 encoding

3. **Bundle Search Order**
   - language_country_variant
   - language_country
   - language
   - default locale (same pattern)
   - base bundle
   - exception if none found

4. **NumberFormat**
   - parse() stops at invalid char (no exception)
   - parse() throws exception if doesn't start with number
   - Returns Number, not Double
   - Currency rounds to currency precision

5. **DecimalFormat**
   - "0" shows zero if absent, "#" omits
   - "%" multiplies by 100
   - ";" separates positive/negative patterns
   - NOT thread-safe (unlike DateTimeFormatter)

6. **DateTimeFormatter**
   - "M" = month, "m" = minute
   - "H" = 24-hour, "h" = 12-hour
   - "yyyy" = year, "YYYY" = week-based year
   - Case sensitive patterns
   - Thread-safe and immutable

7. **MessageFormat**
   - {0}, {1} for parameters
   - Single quote escapes (use '' for literal ')
   - Handles dates, numbers, currency
   - Choice format for pluralization

8. **Properties**
   - getProperty() returns null if missing
   - ISO-8859-1 encoding for files
   - Comments use # or !
   - Can have default Properties

9. **Locale Categories**
   - DISPLAY for ResourceBundle
   - FORMAT for NumberFormat, DateFormat
   - Set separately for fine control
   - getDefault() returns DISPLAY

10. **Best Practices**
    - Cache expensive formatters
    - Never concatenate localized strings
    - Use constants for resource keys
    - Handle missing resources gracefully
    - Store data in neutral format

---

### Common Exam Patterns

1. **Case Sensitivity**
   - Language/country case in Locale
   - Pattern letter case (M vs m)
   - Uppercase vs lowercase matters

2. **Parse Behavior**
   - Stops at invalid character
   - Exception only if doesn't start valid
   - Returns Number type

3. **Pattern Escaping**
   - Single quotes in MessageFormat
   - Double quotes for literal single quote
   - Affects parameter replacement

4. **Locale Categories**
   - Which formatter uses which category
   - DISPLAY vs FORMAT distinction
   - Default behavior

5. **Format Symbols**
   - DecimalFormat: 0 vs #
   - DateTimeFormatter: pattern letters
   - MessageFormat: parameter indices

6. **Thread Safety**
   - DateTimeFormatter is thread-safe
   - SimpleDateFormat is NOT
   - DecimalFormat is NOT
   - Cache strategies

7. **Missing Resources**
   - MissingResourceException when
   - null returns when
   - Fallback mechanisms

---

### Study Tips

1. **Practice Locale Creation**
   - Correct case for language/country
   - Using Locale.of() vs constructors
   - Predefined constants

2. **Master Parse Behavior**
   - When parse() throws exception
   - When it stops silently
   - Return type handling

3. **Know Pattern Syntax**
   - DecimalFormat patterns (0, #, ., ,, %)
   - DateTimeFormatter patterns (y, M, d, H, h, m, s)
   - MessageFormat placeholders

4. **Understand Search Order**
   - Bundle lookup sequence
   - When fallback occurs
   - Default locale role

5. **Remember Exceptions**
   - MissingResourceException scenarios
   - ParseException triggers
   - DateTimeException causes

6. **Locale Categories**
   - Which uses DISPLAY
   - Which uses FORMAT
   - How to set separately

7. **Best Practices**
   - Thread safety considerations
   - Performance optimizations
   - Error handling patterns

8. **Common Traps**
   - Single quote escaping
   - M vs m confusion
   - parse() behavior
   - Case sensitivity

---

### Quick Reference Card

#### Locale Creation
```java
new Locale("en", "US")          // Deprecated Java 19+
Locale.of("en", "US")           // Modern way
Locale.US                       // Constant
```

#### ResourceBundle
```java
ResourceBundle.getBundle("name", locale)
bundle.getString("key")         // Throws if missing
bundle.getObject("key")
```

#### NumberFormat
```java
NumberFormat.getInstance(locale)
NumberFormat.getCurrencyInstance(locale)
NumberFormat.getPercentInstance(locale)
nf.format(number)
nf.parse(string)               // Returns Number
```

#### DecimalFormat
```java
new DecimalFormat("pattern")
"0" - show zero if absent
"#" - omit if absent
"." - decimal point
"," - grouping separator
"%" - multiply by 100
```

#### DateTimeFormatter
```java
DateTimeFormatter.ofPattern("pattern")
"yyyy" - year
"MM" - month (01-12)
"dd" - day (01-31)
"HH" - hour 24h (00-23)
"hh" - hour 12h (01-12)
"mm" - minute (00-59)
"ss" - second (00-59)
```

#### MessageFormat
```java
MessageFormat.format(pattern, args)
"{0}" - first parameter
"{1}" - second parameter
"''" - single quote
"{0,number,currency}" - format number as currency
```

---

### Next Steps

**Practice Tasks:**
1. Create multi-locale application
2. Format numbers in 5 different locales
3. Build ResourceBundle hierarchy
4. Parse dates in various formats
5. Implement MessageFormat with plurals

**Review Before Exam:**
- Locale case rules
- Bundle search order
- Parse behavior differences
- Pattern syntax for all formatters
- Thread safety considerations

**Common Exam Traps:**
- M vs m in DateTimeFormatter
- Single quote escaping in MessageFormat
- parse() stopping behavior
- Locale constructor deprecation
- Currency rounding

---

[← Previous Chapter](CHAPTER-09.md) | [Back to Main Guide](README.md)

---

**Chapter 10 Complete!** ✅  
Localization - Essential for international applications and exam success.

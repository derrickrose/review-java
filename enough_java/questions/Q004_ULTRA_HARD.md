# Q004 – Integer caching, autoboxing, == vs equals (HARD – style examen Java 21)

Niveau : **HARD**  
Chapitre 1 : Primitives / wrappers / autoboxing / Integer cache [-128, 127].

---

## Énoncé (style examen)

Considérez le code suivant :

```java
package exam.q004;

public class Q004 {
    public static void main(String[] args) {
        Integer a = 127;
        Integer b = 127;
        Integer c = 128;
        Integer d = 128;

        Integer e = Integer.valueOf(127);
        Integer f = Integer.valueOf(128);

        Integer g = a + 1;          // autounboxing + boxing
        Integer h = Integer.valueOf(129);
        Integer i = g - 1;          // autounboxing + boxing

        System.out.println(a == b);
        System.out.println(c == d);
        System.out.println(e == a);
        System.out.println(f == d);
        System.out.println(c == g);
        System.out.println(a == i);
        System.out.println(g.equals(h));
        System.out.println(i.equals(a));
    }
}
```

> **Question :**  
> Que va afficher ce programme ?  
> (true/false par ligne, dans cet ordre précis)

---

## Choix (une seule bonne réponse)

A.
```text
true
false
true
false
false
true
true
true
```

B.
```text
true
false
true
true
false
true
true
true
```

C.
```text
true
true
true
false
false
true
true
true
```

D.
```text
true
false
true
false
true
true
true
true
```

E.
```text
true
false
false
false
false
true
true
true
```

F.
```text
true
false
true
false
false
false
true
true
```

G.
```text
false
false
true
false
false
true
true
true
```

H.
```text
true
false
true
false
true
true
false
true
```

---

## Corrigé rapide (pour toi, à masquer si tu veux un vrai mode examen)

1. `a` et `b` valent 127, dans le cache `Integer` ([-128, 127])  
   → initialisés par autoboxing → **même instance**  
   ⇒ `a == b` → `true`.

2. `c` et `d` valent 128, **en dehors** du cache par autoboxing, donc **deux instances différentes**  
   ⇒ `c == d` → `false`.

3. `e = Integer.valueOf(127)` → `valueOf` utilise le cache, renvoie la même instance que pour `a`/`b`.  
   ⇒ `e == a` → `true`.

4. `f = Integer.valueOf(128)` → hors cache, nouvelle instance *mais* `d` a été créé par autoboxing, donc ce sont **deux objets distincts**.  
   ⇒ `f == d` → `false`.

5. `g = a + 1` :  
   - `a` est un `Integer`, promotion → autounboxing (`int`) + addition → résultat `128` (primitive)  
   - puis autoboxing → nouvel `Integer(128)` **non nécessairement le même** que `c` ou `d`.  
   Typiquement, `c` et `d` sont déjà deux instances différentes, `g` en est une troisième.  
   ⇒ `c == g` → `false`.

6. `i = g - 1` :  
   - `g` est 128, autounboxing → `int 128`, résultat `127` (primitive)  
   - autoboxing → **comme pour 127, le cache est utilisé**, donc `i` référence **la même instance** que `a`, `b`, `e`.  
   ⇒ `a == i` → `true`.

7. `g.equals(h)` :  
   - `g` vaut 128, `h = Integer.valueOf(129)` → 129  
   - 128 ≠ 129  
   ⇒ `g.equals(h)` → `false`.

8. `i.equals(a)` :  
   - `i` et `a` valent 127, même valeur  
   - `equals` compare la valeur primitive  
   ⇒ `true`.

Donc la séquence est :

```text
true
false
true
false
false
true
false
true
```

Ce qui correspond à la **réponse F**.

---

## Réponse correcte

✅ **F**

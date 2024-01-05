package design_pattern.creational.factory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class Country extends Location {
    @Setter
    @Getter
    private String capital;
}

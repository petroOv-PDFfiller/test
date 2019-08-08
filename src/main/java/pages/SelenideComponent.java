package pages;

import com.codeborne.selenide.SelenideElement;

import java.util.List;
import java.util.stream.Collectors;

public abstract class SelenideComponent {

    private SelenideElement itself;

    public SelenideComponent(SelenideElement itself) {
        this.itself = itself;
    }

    public static List initList(List<SelenideElement> elems, Class<? extends SelenideComponent> clazz) {
        return elems.stream().map(e -> {
            try {
                return clazz.getConstructor(SelenideElement.class).newInstance(e);
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }

    public final SelenideElement self() {
        return itself;
    }

    public final void init(SelenideElement itself) {
        this.itself = itself;
    }
}

public class Card {
    private final Colors color;
    private final Values value;

    public Card(Colors color, Values value){
        this.color = color;
        this.value = value;
    }
    public Colors getColor() {
        return color;
    }

    public Values getValue() {
        return value;
    }
}

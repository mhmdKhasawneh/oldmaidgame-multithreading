import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> deck;
    public final static int DECK_SIZE = 53;

    public Deck() {
        deck = new ArrayList<>();
        initDeck();
        shuffleDeck();
    }

    private void initDeck(){
        for(Values value : Values.values()){
            if(value.equals(Values.JOKER))
                continue;
            for(Colors color : Colors.values()){
                if(color.equals(Colors.WILD))
                    continue;
                addCards(new Card(color, value), 2);
            }
        }
        addCard(new Card(Colors.WILD, Values.JOKER));
    }

    private void addCard(Card card){
        deck.add(card);
    }

    private void addCards(Card card, int count){
        while(count-- != 0){
            deck.add(card);
        }
    }

    private void shuffleDeck(){
        Collections.shuffle(deck);
    }

    public List<Card> getDeck() {
        return deck;
    }
}

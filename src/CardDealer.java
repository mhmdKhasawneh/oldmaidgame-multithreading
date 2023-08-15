public class CardDealer implements Runnable {
    private int lower;
    private int upper;
    private Player player;
    private Deck deck;

    public CardDealer(int lower, int upper, Deck deck, Player player){
        this.lower = lower;
        this.upper = upper;
        this.player = player;
        this.deck = deck;
    }

    @Override
    public void run() {
        for(int i=lower;i<=upper;i++){
            if(player.getHand().containsKey(deck.getDeck().get(i))){
                player.getHand().put(deck.getDeck().get(i), 2);
            }
            else{
                player.getHand().put(deck.getDeck().get(i), 1);
            }
        }
    }
}

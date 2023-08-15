import java.util.*;

public class Player implements Runnable {
    private final int id;
    private final String playerName;
    private final Map<Card, Integer> hand;
    private final Game game;

    public Player(int id, String name, Game game) {
        this.id = id;
        this.playerName = name;
        this.game = game;
        this.hand = new HashMap<>();
    }

    @Override
    public void run() {
        System.out.println("initially " + getPlayerName() + " has " + getHandSize() + " cards in hand");
        throwMatchingCards();
        System.out.println("after first round of throwing matching pairs, " + getPlayerName() + " has " + getHandSize() + " cards in hand");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (!hasFinished()) {
            synchronized (game) {
                while (game.getTurn() != id) {
                    if(hasFinished()){
                        return;
                    }
                    try {
                        game.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println();
                if(game.getNumOfOngoingPlayers() == 1){
                    game.announceLoser();
                    break;
                }
                handleTurn();
            }
        }
    }
    public int getHandSize(){
        int size = 0;
        for(int val : hand.values()){
            size+=val;
        }
        return size;
    }
    public void throwMatchingCards () {
        List<Card> cardsToRemove = new ArrayList<>();
        for (Map.Entry<Card, Integer> entry : hand.entrySet()) {
            if (entry.getValue() == 2) {
                cardsToRemove.add(entry.getKey());
            }
        }

        for (Card card : cardsToRemove) {
            hand.remove(card);
        }
    }
    private boolean hasFinished(){
        return getHandSize() == 0;
    }
    private void handleTurn(){
        System.out.println(game.getPlayers().get(game.getTurn()).playerName + "'s turn");

        int nextPlayer = game.getNextTurn();
        drawFrom(nextPlayer);
        System.out.println("after draw " + playerName + " has " + getHandSize() + " cards in hand. The following " +
                "cards are:");
        printHand();

        throwMatchingCards();
        printAfterThrowStatistics();

        if(hasFinished()){
            game.decrementNumOfOngoingPlayers();
            System.out.println(playerName + " finished.");
        }

        Player nextPlayerInstance = game.getPlayers().get(nextPlayer);
        if(nextPlayerInstance.hasFinished()){
            game.decrementNumOfOngoingPlayers();
            System.out.println("Because " + playerName + " has drawn from " + nextPlayerInstance.getPlayerName() +
                    " their last card, " + nextPlayerInstance.getPlayerName() + " finished.");
        }

        game.updateTurn();
        game.notifyAll();
    }
    private void drawFrom(int nextPlayer) {
        Player nextPlayerInstance = game.getPlayers().get(nextPlayer);
        Map<Card, Integer> nextPlayerHand = nextPlayerInstance.getHand();

        int randomCardIndex = new Random().nextInt(nextPlayerInstance.getHand().size());

        int currentIndex = 0;
        Card randomCard = null;
        for(Card card : nextPlayerHand.keySet()){
            if(currentIndex == randomCardIndex){
                randomCard = card;
                break;
            }
            currentIndex++;
        }

        addNewCard(randomCard);
        nextPlayerInstance.removeCard(randomCard);


        System.out.println(playerName + " drew from " + nextPlayerInstance.getPlayerName() + " "
         + randomCard.getValue() + " " + randomCard.getColor());
    }
    public void printHand () {
        for (Map.Entry<Card, Integer> entry : hand.entrySet()) {
            System.out.println(entry.getKey().getValue() + " " + entry.getKey().getColor() + " with count " + entry.getValue());
        }
    }
    private void printAfterThrowStatistics(){
        System.out.print("after " + playerName + " has attempted to throw their matching pairs," +
                "they have " + getHandSize() + " cards in hand.");
        if(getHandSize() != 0){
            System.out.println(" The following cards are:");
            printHand();
        } else{
            System.out.println();
        }
    }
    public Map<Card, Integer> getHand () {
        return hand;
    }
    public String getPlayerName() {
        return playerName;
    }
    private void addNewCard(Card card){
        hand.put(card, hand.getOrDefault(card, 0) + 1);
    }
    private void removeCard(Card card){
        hand.put(card, hand.get(card) - 1);
        if(hand.get(card) == 0){
            hand.remove(card);
        }
    }
}

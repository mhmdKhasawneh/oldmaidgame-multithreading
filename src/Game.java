import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Game {
    private int turn;
    private Player currentPlayerInstance;
    private final List<Player> players;
    private final Deck deck;
    private final int numOfPlayers;
    private int numOfOngoingPlayers;
    private final ExecutorService playerThreadPool;
    private final ExecutorService cardDealerThreadPool;

    public Game(int numOfPlayers){
        this.players = new ArrayList<>();
        this.deck = new Deck();
        this.turn = 0;
        this.currentPlayerInstance = null;
        this.numOfPlayers = numOfPlayers;
        this.numOfOngoingPlayers = numOfPlayers;
        this.playerThreadPool = Executors.newCachedThreadPool();
        this.cardDealerThreadPool = Executors.newCachedThreadPool();
    }

    public void start() {
        registerPlayers();
        setCurrentPlayerInstance(players.get(0));
        dealCards();
        cardDealerThreadPool.shutdown();
        startLoop(players);
        playerThreadPool.shutdown();
    }

    private void registerPlayers(){
        Scanner sc = new Scanner(System.in);
        for(int i=0;i<numOfPlayers;i++){
            System.out.println("Enter name of player " + (i+1));
            String name = sc.nextLine();
            players.add(new Player(i, name, this));
        }
    }

    private void dealCards() {
        int each = Deck.DECK_SIZE / numOfPlayers;
        int rem = Deck.DECK_SIZE % numOfPlayers;
        int lower = 0;
        int upper = each - 1;
        List<Future<?>> cardDealerFutures = new ArrayList<>();

        for(int i=0;i<numOfPlayers;i++){
            int extraCard = (rem > 0) ? 1 : 0;
            rem--;

            CardDealer dealer = new CardDealer(lower, upper + extraCard, deck, players.get(i));
            lower += (each + extraCard);
            upper += (each + extraCard);

            Future<?> future = cardDealerThreadPool.submit(dealer);
            cardDealerFutures.add(future);
        }

        for (Future<?> future : cardDealerFutures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void startLoop(List<Player> players){
        List<Future<?>> playerFutures = new ArrayList<>();
        for(Player player : players){
            Future<?> future = playerThreadPool.submit(player);
            playerFutures.add(future);
        }
        for (Future<?> future : playerFutures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public int getNextTurn(){
        int temp = (turn + 1) % numOfPlayers;
        while(players.get(temp).getHandSize() == 0){
            temp = ++temp % numOfPlayers;
        }
        return temp;
    }
    public void updateTurn(){
        turn = getNextTurn();
        setCurrentPlayerInstance(players.get(turn));
    }
    public void announceLoser(){
        System.out.println(currentPlayerInstance.getPlayerName() + "'s turn");
        System.out.println(currentPlayerInstance.getPlayerName() + " has the following hand:");
        currentPlayerInstance.printHand();
        System.out.println(currentPlayerInstance.getPlayerName() + " holds the joker and they are the only player left. They lose");
        System.out.println();
    }
    public int getTurn(){
        return turn;
    }
    public List<Player> getPlayers() {
        return players;
    }
    public void decrementNumOfOngoingPlayers(){
        --numOfOngoingPlayers;
    }
    public int getNumOfOngoingPlayers(){
        return numOfOngoingPlayers;
    }

    public void setCurrentPlayerInstance(Player currentPlayerInstance) {
        this.currentPlayerInstance = currentPlayerInstance;
    }
}

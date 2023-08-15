import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter the number of players");
        int numOfPlayers = sc.nextInt();
        sc.nextLine();

        Game game = new Game(numOfPlayers);
        game.start();

        System.out.println("Game over");
    }
}
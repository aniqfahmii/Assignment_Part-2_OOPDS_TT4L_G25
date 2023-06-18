import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class GoBoomGame {
    private ArrayList<Card> deck;
    private Card centerCard;
    private ArrayList<Player> players;
    private Player currentPlayer;
    private Player trickWinner;
    private int roundNumber;
    private boolean isRoundOver;
    private Card leftoverCard;

    

    private Card getHighestCardOfSuit(Player player, Card.Suit suit) {
        Card highestCard = null;
        for (Card card : player.getHand()) {
            if (card.getSuit() == suit) {
                if (highestCard == null || card.getRank().compareTo(highestCard.getRank()) > 0) {
                    highestCard = card;
                }
            }
        }
        return highestCard;
    }

    private void promptTrickWinnerForCenterCard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(trickWinner.getName() + ", choose a card from your hand to be the center card:");

        ArrayList<Card> hand = trickWinner.getHand();
        for (int i = 0; i < hand.size(); i++) {
            System.out.println(i + ": " + hand.get(i));
        }

        int cardIndex = scanner.nextInt();
        if (cardIndex >= 0 && cardIndex < hand.size()) {
            centerCard = hand.remove(cardIndex);
            System.out.println(trickWinner.getName() + " chose " + centerCard + " as the center card.");
        } else {
            System.out.println("Invalid card index! Using default center card.");
            centerCard = deck.remove(deck.size() - 1); // Use a default center card from the deck
        }
        // Set the next player as the current player
        currentPlayer = getNextPlayer();
        System.out.println("Turn: " + currentPlayer.getName());
    }

    public void startNewGame() {
        // Initialize deck
        deck = new ArrayList<>();
        roundNumber = 1;
        isRoundOver = false;
        // Set the leftover card to null initially
        leftoverCard = null;
        for (Card.Rank rank : Card.Rank.values()) {
            for (Card.Suit suit : Card.Suit.values()) {
                deck.add(new Card(rank, suit));
            }
        }

        // Shuffle the deck
        Collections.shuffle(deck);

        // Initialize players
        players = new ArrayList<>();
        players.add(new Player("Player1"));
        players.add(new Player("Player2"));
        players.add(new Player("Player3"));
        players.add(new Player("Player4"));

        // Deal cards to players
        int cardsPerPlayer = 7;
        for (int i = 0; i < cardsPerPlayer; i++) {
            for (Player player : players) {
                player.addCard(deck.remove(deck.size() - 1));
            }
        }

        // Place the first lead card at the center
        centerCard = deck.remove(deck.size() - 1);

        // Determine the first player based on the lead card
        currentPlayer = determineFirstPlayer(centerCard);
        trickWinner = currentPlayer; // Assign the first player as the trick winner

        // Print initial game state
        printGameState();
        playGame();
    }

    public void saveGameStateToFile() {
        try {
            PrintWriter writer = new PrintWriter("game_state.txt");

            // Save deck
            writer.println("Deck: " + deck);

            // Save players' hands
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                writer.println("Player" + (i + 1) + ": " + player.getHand());
            }

            // Save center card
            writer.println("Center: " + centerCard);

            // Save scores
            writer.print("Score: ");
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                writer.print(player.getName() + " = " + player.getScore());
                if (i < players.size() - 1) {
                    writer.print(" | ");
                }
            }
            writer.println();

            // Save current player
            writer.println("Turn: " + currentPlayer.getName());

            writer.close();
            System.out.println("Game state saved to 'game_state.txt'.");
        } catch (FileNotFoundException e) {
            System.out.println("Failed to save game state: " + e.getMessage());
        }
    }


    public Player determineFirstPlayer(Card leadCard) {
        if (leftoverCard != null) {
            // Remove the leftover card from the player's hand
            currentPlayer.getHand().remove(leftoverCard);
            centerCard = leftoverCard;
            return currentPlayer;
        }
        Card.Rank rank = leadCard.getRank();

        if (rank == Card.Rank.ACE || rank == Card.Rank.FIVE || rank == Card.Rank.NINE || rank == Card.Rank.KING) {
            return players.get(0);
        } else if (rank == Card.Rank.TWO || rank == Card.Rank.SIX || rank == Card.Rank.TEN) {
            return players.get(1);
        } else if (rank == Card.Rank.THREE || rank == Card.Rank.SEVEN || rank == Card.Rank.JACK) {
            return players.get(2);
        } else if (rank == Card.Rank.FOUR || rank == Card.Rank.EIGHT || rank == Card.Rank.QUEEN) {
            return players.get(3);
        }

        return null;
    }

   public void printGameState() {
    System.out.println("Deck : " + deck);
    System.out.println();
    System.out.println("Player1: " + players.get(0).getHand());
    System.out.println();
    System.out.println("Player2: " + players.get(1).getHand());
    System.out.println();
    System.out.println("Player3: " + players.get(2).getHand());
    System.out.println();
    System.out.println("Player4: " + players.get(3).getHand());
    System.out.println();
    System.out.println("Center : " + centerCard);
    System.out.println();
    System.out.println("Score: Player1 = 0 | Player2 = 0 | Player3 = 0 | Player4 = 0");
    System.out.println("Turn : " + currentPlayer.getName());
}

    private void playGame() {
    Scanner scanner = new Scanner(System.in);

    while (!isGameOver()) {
        if (isRoundOver) {
            System.out.println("Trick winner: " + trickWinner.getName());

            // Check if the trick winner wants to choose the center card
            if (trickWinner.getHandSize() > 0) {
                promptTrickWinnerForCenterCard();
            } else {
                centerCard = deck.remove(deck.size() - 1); // Use a default center card from the deck
                System.out.println("No leftover cards. Using default center card.");
                // Set the next player as the current player
                currentPlayer = getNextPlayer();
                System.out.println("Turn: " + currentPlayer.getName());
            }
        }

        // Print game state
        printGameState();

        System.out.println("Enter the index of the card you want to play, 'd' to draw, or 'exit' to save and exit: ");
        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("exit")) {
            // Save game state to a text file
            saveGameStateToFile();
            System.out.println("Game saved. Exiting...");
            break;
        }

        if (input.equalsIgnoreCase("d")) {
            if (deck.isEmpty()) {
                System.out.println("Deck is empty! You cannot draw.");
                continue;
            }

            Card drawnCard = deck.remove(deck.size() - 1);
            currentPlayer.getHand().add(drawnCard);
            System.out.println(currentPlayer.getName() + " drew a card: " + drawnCard);

            // Print updated game state
            printGameState();
        } else {
            try {
                int cardIndex = Integer.parseInt(input);

                if (isValidCardIndex(cardIndex)) {
                    Card playedCard = currentPlayer.getHand().get(cardIndex);

                    if (isCardPlayable(playedCard)) {
                        centerCard = playedCard;
                        currentPlayer.getHand().remove(cardIndex);
                        System.out.println(currentPlayer.getName() + " played " + playedCard);

                        // Move to the next player
                        currentPlayer = getNextPlayer();

                        if (currentPlayer == trickWinner) {
                            // Start a new round
                            roundNumber++;
                            trickWinner = determineFirstPlayer(centerCard);
                            currentPlayer = trickWinner;
                            isRoundOver = true;

                            // Update scores
                            int trickPoints = calculateTrickPoints();
                            trickWinner.updateScore(trickPoints);
                            System.out.println("Trick points: " + trickPoints);
                            System.out.println("Scores after trick: ");
                            printScores();
                        } else {
                            isRoundOver = false;
                        }
                    } else {
                        System.out.println("Invalid move! You cannot play that card. Try again.");
                    }
                } else {
                    System.out.println("Invalid card index! Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Try again.");
            }
        }
    }

    // Print final scores
    System.out.println("Final scores: ");
    printScores();
    System.out.println("Game over!");

    scanner.close();
}



    private boolean isGameOver() {
        for (Player player : players) {
            if (player.getScore() >= 100) {
                return true;
            }
        }
        return false;
    }

    private Player determineWinner() {
        Player winner = players.get(0);
        for (int i = 1; i < players.size(); i++) {
            Player player = players.get(i);
            if (player.getScore() > winner.getScore()) {
                winner = player;
            }
        }
        return winner;
    }

    private int calculateTrickPoints() {
        int trickPoints = 0;
        for (Player player : players) {
            trickPoints += player.getHandSize();
        }
        return trickPoints;
    }

    private void printScores() {
        for (Player player : players) {
            System.out.println(player.getName() + ": " + player.getScore());
        }
    }

    private boolean isCardPlayable(Card card) {
        return card.getRank() == centerCard.getRank() || card.getSuit() == centerCard.getSuit();
    }

    private Player getNextPlayer() {
        int currentPlayerIndex = players.indexOf(currentPlayer);
        int nextPlayerIndex = (currentPlayerIndex + 1) % players.size();
        return players.get(nextPlayerIndex);
    }

    private boolean isValidCardIndex(int cardIndex) {
        return cardIndex >= 0 && cardIndex < currentPlayer.getHandSize();
    }

    public static void main(String[] args) {
        GoBoomGame game = new GoBoomGame();
        game.startNewGame();
    }
}

class Card {
    public enum Rank { ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING }

    public enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }

    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}

class Player {
    private final String name;
    private final ArrayList<Card> hand;
    private int score;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public int getHandSize() {
        return hand.size();
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public void updateScore(int points) {
        score += points;
    }

    public int getScore() {
        return score;
    }
}

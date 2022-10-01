import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Player extends Person implements Serializable {

    private final String username;
    private final String password;
    private transient ArrayList<Card> hand;  // transient means it won't be serialized
    private transient byte setCount;
    private int wins;
    private int losses;

    // Getters
    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public ArrayList<Card> getHand() {
        Collections.sort(this.hand);
        return this.hand;
    }

    public byte getSetCount() {
        return this.setCount;
    }

    public int getWins() {
        return this.wins;
    }

    public int getLosses() {
        return this.losses;
    }

    // Setters
    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    // Constructors
    public Player(String name, Gender gender, byte age, String username, String password, int wins, int losses) {
        super(name, gender, age);
        this.username = username;
        this.password = password;
        this.wins = wins;
        this.losses = losses;
    }

    // Other
    public void addCards(ArrayList<Card> cards) {
        this.hand.addAll(cards);
    }

    public void removeCards(ArrayList<Card> cards) {
        this.hand.removeAll(cards);
    }

    public Card drawCard() throws EmptyDeckException {
        Scanner sc = new Scanner(System.in);
        if (Deck.cards.size() > 0) {
            // Draw the first card in the deck
            Card draw = Deck.cards.get(0);
            this.hand.add(draw);
            Deck.cards.remove(0);

            System.out.print(Colour.ITALIC + "Drawing a card... You got a " + draw.getRankName() + " of " + draw.getSuitSymbol() + "." + Colour.RESET);
            sc.nextLine();

            return draw;
        } else {
            System.out.print("There are no more cards in the deck!");
            sc.nextLine();
            throw new EmptyDeckException();
        }
    }

    public void checkForSet() {
        // This method will check if the player has a set of cards and
        // print the rank with the set. It will also set that rank's Suit to SET
        // and remove other cards in that SET.
        Collections.sort(this.hand);

        Scanner sc = new Scanner(System.in);

        int sameCardCount = 0;
        Card previousCard = null;
        for (int i = 0; i < this.hand.size(); i++) {
            Card card = this.hand.get(i);

            if (card.suit != Card.Suit.SET) {  // We don't need to check cards that are already sets
                if (previousCard != null) {
                    if (card.isMatch(previousCard.number)) {
                        sameCardCount++;
                    } else {
                        sameCardCount = 0;
                    }
                }

                if (sameCardCount == 3) {
                    // This will be the last card in the set, so also set the previous 3 cards to SET
                    card.suit = Card.Suit.SET;

                    // Remove the other cards in the set so only one card shows up in the hand
                    this.hand.remove(i - 1);
                    this.hand.remove(i - 2);
                    this.hand.remove(i - 3);

                    this.setCount++;

                    System.out.print("You have a set of " + card.getRankName() + "s!");
                    sc.nextLine();
                }
            }

            previousCard = card;
        }
    }

    public boolean canRequest() {
        for (Card card : this.hand) {
            if (card.suit != Card.Suit.SET) {
                return true;
            }
        }
        return false;
    }

    public void addWin() {
        this.wins++;
    }

    public void addLoss() {
        this.losses++;
    }

    public void reset() {
        this.hand.clear();
        this.setCount = 0;
    }

    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", hand=" + hand +
                ", setCount=" + setCount +
                ", wins=" + wins +
                '}';
    }
}

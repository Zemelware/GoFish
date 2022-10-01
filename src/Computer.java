import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Computer {

    private ArrayList<Card> hand;
    private byte setCount;

    // Getters
    public ArrayList<Card> getHand() {
        return this.hand;
    }

    public byte getSetCount() {
        return this.setCount;
    }

    // Setters
    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    // Other
    public void sortHand() {
        Collections.sort(this.hand);
    }

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

            System.out.print(Colour.ITALIC + "The computer drew a card." + Colour.RESET);
            sc.nextLine();

            return draw;
        } else {
            System.out.print("There are no more cards in the deck!");
            sc.nextLine();
            throw new EmptyDeckException();
        }
    }

    public byte requestCard() {
        int requestIndex;
        do {
            // Generate request (random card from computer's hand)
            requestIndex = new Random().nextInt(this.hand.size());
        } while (this.hand.get(requestIndex).suit == Card.Suit.SET); // Don't request a set

        return this.hand.get(requestIndex).number;
    }

    public void checkForSet() {
        sortHand();

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

                    System.out.print(Colour.ITALIC + "The computer got a set of " + card.getRankName() + "s." + Colour.RESET);
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

    public void reset() {
        this.hand.clear();
        this.setCount = 0;
    }

}

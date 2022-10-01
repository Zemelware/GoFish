import java.util.ArrayList;
import java.util.Collections;

public final class Deck {

    public static ArrayList<Card> cards = new ArrayList<>();

    // Initializer
    static {
        reset();
    }

    // Other
    private static void shuffle() {
        Collections.shuffle(cards);
    }

    public static void reset() {
        cards.clear();
        // Create a deck of cards
        for (int suitNumber = 1; suitNumber <= 4; suitNumber++) {
            for (int rank = 1; rank <= 13; rank++) {

                // Convert the suitNumber into an actual Suit
                Card.Suit suit = switch (suitNumber) {
                    case 1 -> Card.Suit.HEART;
                    case 2 -> Card.Suit.DIAMOND;
                    case 3 -> Card.Suit.CLUB;
                    case 4 -> Card.Suit.SPADE;
                    default -> null;
                };

                cards.add(new Card(suit, (byte) rank));
            }
        }

        shuffle();
    }

}

class EmptyDeckException extends RuntimeException {
    public EmptyDeckException() {
        super("The deck is empty");
    }
}

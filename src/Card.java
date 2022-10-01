public class Card implements Comparable<Card> {

    public Suit suit;
    public byte number;

    // Getters
    public String getSuitSymbol() {
        return this.suit.symbol;
    }

    public String getRank() {
        return switch (this.number) {
            case 1 -> "A";
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            default -> String.valueOf(this.number);
        };
    }

    public String getRankName() {
        return switch (this.number) {
            case 1 -> "Ace";
            case 11 -> "Jack";
            case 12 -> "Queen";
            case 13 -> "King";
            default -> String.valueOf(this.number);
        };
    }

    // Setters
    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    public void setNumber(byte number) {
        if (number >= 1 && number <= 13) {
            this.number = number;
        } else {
            throw new IllegalArgumentException("Not a valid card number");
        }
    }

    // Constructors
    public Card(Suit suit, byte number) {
        setSuit(suit);
        setNumber(number);
    }

    // Other
    public String toString() {
        String cardString;

        // The format of the card printed is slightly different for 10s and Sets (because they take up more space)
        if (this.suit == Suit.SET && this.number == 10) {
            cardString = String.format("""
            ┌───────┐
            │%s     │
            │  %s  │
            │     %s│
            └───────┘
            """, getRank(), getSuitSymbol(), getRank());
        } else if (this.suit == Suit.SET) {
            cardString = String.format("""
            ┌───────┐
            │%s      │
            │  %s  │
            │      %s│
            └───────┘
            """, getRank(), getSuitSymbol(), getRank());
        } else {
            if (this.number == 10) {
                cardString =  String.format("""
            ┌───────┐
            │%s     │
            │   %s   │
            │     %s│
            └───────┘
            """, getRank(), getSuitSymbol(), getRank());
            } else {
                cardString =  String.format("""
            ┌───────┐
            │%s      │
            │   %s   │
            │      %s│
            └───────┘
            """, getRank(), getSuitSymbol(), getRank());
            }
        }

        return cardString;
    }

    public int compareTo(Card card) {
        // If the player has a set in their hand then it should go to the end
        if (this.suit == Suit.SET && card.suit != Suit.SET) {
            return 1;
        } else if (this.suit != Suit.SET && card.suit == Suit.SET) {
            return -1;
        }

        if (this.number - card.number == 0) {
            // If the numbers are the same sort by suit
            if (this.suit == Suit.HEART) {
                return -1;
            } else if (this.suit == Suit.DIAMOND && card.suit != Suit.HEART) {
                return -1;
            } else if (this.suit == Suit.DIAMOND){
                return 1;
            } else if (this.suit == Suit.CLUB && card.suit == Suit.SPADE) {
                return -1;
            } else if (this.suit == Suit.CLUB) {
                return 1;
            } else if (this.suit == Suit.SPADE) {
                return 1;
            }
        }
        return this.number - card.number;
    }

    public boolean isMatch(byte rank) {
        return this.number == rank;
    }

    public enum Suit {
        HEART("♥", Colour.RED),
        DIAMOND("♦", Colour.RED),
        CLUB("♣", Colour.WHITE),
        SPADE("♠", Colour.WHITE),
        SET("Set", Colour.BOLD);

        public final String symbol;
        public final Colour colour;

        Suit(String symbol, Colour colour) {
            this.symbol = symbol;
            this.colour = colour;
        }
    }

}

/*
Author: Ethan Zemelman
Purpose: To create a text-based interactive game of Go Fish that can be played by one person against a computer.
Date: Feb. 2, 2022
 */


import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GoFish {

    private static final String filename = "accounts.ser";
    private static Player player;
    private static Computer computer;
    private static ArrayList<Player> accounts = new ArrayList<>();

    public static void main(String[] args) {
        computer = new Computer();

        System.out.println(Colour.BOLD + "Welcome to Go Fish!\n-------------------" + Colour.RESET);

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(Colour.GREEN + "Do you have an account (y/n)? " + Colour.RESET);
            String hasAccount = sc.nextLine();

            if (hasAccount.equalsIgnoreCase("y") || hasAccount.equalsIgnoreCase("yes")) {
                login();
            } else if (hasAccount.equalsIgnoreCase("n") || hasAccount.equalsIgnoreCase("no")) {
                System.out.println("Please enter the following information to create an account.");
                createAccount();
            } else {
                System.out.println(Colour.RED + "Invalid input. Please try again." + Colour.RESET);
                continue;
            }
            break;
        }

        String winS = player.getWins() == 1 ? " win" : " wins";
        String lossS = player.getLosses() == 1 ? " loss" : " losses";
        System.out.println("You have " + player.getWins() + winS + " and " + player.getLosses() + lossS + " on your account.");

        // Main game loop
        while (true) {
            System.out.print(Colour.BOLD + "\nClick enter to continue after each message." + Colour.RESET);
            sc.nextLine();

            System.out.print(Colour.ITALIC + "\nThe game is now starting! Dealing cards..." + Colour.RESET);
            dealCards();
            sc.nextLine();

            System.out.println("\nYour hand:");
            printCards(player.getHand(), false);
            System.out.println("Computer's hand:");
            printCards(computer.getHand(), true);
            sc.nextLine();

            System.out.print("When typing card ranks, please use the format 'Ace' or 'A'.");
            sc.nextLine();

            // Decide if the player or computer goes first
            int first = (int) (Math.random() * 2);
            boolean playerTurn = first == 1;
            boolean goAgain = false;

            // Turn loop
            while (true) {
                if (playerTurn) {
                    // Player goes
                    System.out.println(Colour.CYAN_UNDERLINED + "" + Colour.BOLD + "\nIt's your turn." + Colour.RESET);

                    // Check if the player has any cards to request (which are any cards that aren't in a set)
                    boolean canRequest = player.canRequest();

                    if (canRequest) {
                        System.out.print(Colour.GREEN + "Enter your request: " + Colour.RESET);

                        // Get the request and validate it
                        byte request = getRequest();

                        // Check if the computer has the requested card
                        ArrayList<Card> requestedCardsReceived = new ArrayList<>();
                        int amountOfCardsReceived = 0;
                        for (Card card : computer.getHand()) {
                            if (card.isMatch(request)) {
                                requestedCardsReceived.add(card);
                                amountOfCardsReceived++;
                            }
                        }

                        if (amountOfCardsReceived > 0) {
                            // Take the matching cards from the computer and give them to the player
                            computer.removeCards(requestedCardsReceived);
                            player.addCards(requestedCardsReceived);

                            if (amountOfCardsReceived <= 1) {
                                System.out.print(Colour.ITALIC + "\nThe computer gave you a " + requestedCardsReceived.get(0).getRankName() + "!" + Colour.RESET);
                            } else {
                                System.out.print(Colour.ITALIC + "\nThe computer gave you " + amountOfCardsReceived + " " + requestedCardsReceived.get(0).getRankName() + "s!" + Colour.RESET);
                            }
                            sc.nextLine();

                            goAgain = true;
                        } else {
                            System.out.print(Colour.ITALIC + "\nThe computer doesn't have that card. Go fish!" + Colour.RESET);
                            sc.nextLine();

                            Card draw;
                            try {
                                draw = player.drawCard();
                            } catch (EmptyDeckException e) {
                                gameOver();
                                break;
                            }

                            if (draw.isMatch(request)) {
                                System.out.print(Colour.ITALIC + "You fished your wish!" + Colour.RESET);
                                sc.nextLine();
                                goAgain = true;
                            }
                        }
                    } else {
                        System.out.print(Colour.ITALIC + "You don't have any cards to request. Go fish!" + Colour.RESET);
                        sc.nextLine();
                        try {
                            player.drawCard();
                        } catch (EmptyDeckException e) {
                            // If the deck is empty and the player has no cards to request, the game is over
                            gameOver();
                            break;
                        }
                    }

                    // Check if the new card created a set
                    player.checkForSet();

                    if (goAgain) {
                        System.out.print(Colour.ITALIC + "You get to go again." + Colour.RESET);
                        sc.nextLine();
                    }

                    System.out.println("\nYour new hand:");
                    printCards(player.getHand(), false);
                    System.out.println("Computer's new hand:");
                    printCards(computer.getHand(), true);

                    if (!canRequest) {
                        continue;
                    }

                    if (!goAgain) {
                        playerTurn = false;
                    } else {
                        goAgain = false;
                    }
                } else {
                    // Computer goes
                    System.out.println(Colour.CYAN_UNDERLINED + "" + Colour.BOLD + "\nIt's the computer's turn." + Colour.RESET);

                    boolean canRequest = computer.canRequest();

                    if (canRequest) {
                        // Generate request
                        byte request = computer.requestCard();
                        System.out.print(Colour.ITALIC + "The computer requests a " + cardNumberToRank(request) + "." + Colour.RESET);
                        sc.nextLine();

                        // Check if the player has the requested card
                        ArrayList<Card> requestedCardsReceived = new ArrayList<>();
                        int amountOfCardsReceived = 0;
                        for (Card card : player.getHand()) {
                            if (card.isMatch(request)) {
                                requestedCardsReceived.add(card);
                                amountOfCardsReceived++;
                            }
                        }

                        if (amountOfCardsReceived > 0) {
                            // Take the matching cards from the player and give them to the computer
                            player.removeCards(requestedCardsReceived);
                            computer.addCards(requestedCardsReceived);

                            if (amountOfCardsReceived <= 1) {
                                System.out.print(Colour.ITALIC + "You gave the computer a " + requestedCardsReceived.get(0).getRankName() + "." + Colour.RESET);
                            } else {
                                System.out.print(Colour.ITALIC + "You gave the computer " + amountOfCardsReceived + " " + requestedCardsReceived.get(0).getRankName() + "s." + Colour.RESET);
                            }
                            sc.nextLine();

                            goAgain = true;
                        } else {
                            System.out.print(Colour.ITALIC + "You don't have that card. The computer will go fish." + Colour.RESET);
                            sc.nextLine();

                            Card draw;
                            try {
                                draw = computer.drawCard();
                            } catch (EmptyDeckException e) {
                                gameOver();
                                break;
                            }

                            if (draw.isMatch(request)) {
                                System.out.print(Colour.ITALIC + "The computer fished its wish!" + Colour.RESET);
                                sc.nextLine();
                                goAgain = true;
                            }
                        }
                    } else {
                        System.out.print(Colour.ITALIC + "The computer doesn't have any cards to request. The computer will go fish." + Colour.RESET);
                        sc.nextLine();

                        try {
                            computer.drawCard();
                        } catch (EmptyDeckException e) {
                            gameOver();
                            break;
                        }
                    }

                    computer.checkForSet();

                    if (goAgain) {
                        System.out.print(Colour.ITALIC + "The computer gets to go again." + Colour.RESET);
                        sc.nextLine();
                    }

                    System.out.println("\nYour new hand:");
                    printCards(player.getHand(), false);
                    System.out.println("Computer's new hand:");
                    printCards(computer.getHand(), true);

                    if (!canRequest) {
                        continue;
                    }

                    if (!goAgain) {
                        playerTurn = true;
                    } else {
                        goAgain = false;
                    }
                }
            }

            System.out.print(Colour.GREEN + "\nThanks for playing! Would you like to play again (y/n)? " + Colour.RESET);
            while (true) {
                String playAgain = sc.nextLine();
                if (playAgain.equalsIgnoreCase("y") || playAgain.equalsIgnoreCase("yes")) {
                    Deck.reset();
                    player.reset();
                    computer.reset();
                    break;
                } else if (playAgain.equalsIgnoreCase("n") || playAgain.equalsIgnoreCase("no")) {
                    System.exit(0);
                } else {
                    System.out.print(Colour.RED + "Invalid input. Please enter y or n: " + Colour.RESET);
                }
            }
        }
    }

    private static void dealCards() {
        // Deal cards to the player
        ArrayList<Card> dealPlayer = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            // Add the first card in the deck to the player's hand then remove it from the deck 7 times
            dealPlayer.add(Deck.cards.get(0));
            Deck.cards.remove(0);
        }

        // Deal cards to the computer
        ArrayList<Card> dealComputer = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            dealComputer.add(Deck.cards.get(0));
            Deck.cards.remove(0);
        }

        player.setHand(dealPlayer);
        computer.setHand(dealComputer);

        // A set could be created at the beginning of the game, so we need to check for that
        player.checkForSet();
        computer.checkForSet();
    }

    private static byte getRequest() {
        Scanner sc = new Scanner(System.in);

        byte request;
        while (true) {
            try {
                request = cardRankToNumber(sc.nextLine());
            } catch (Exception e) {
                System.out.print(Colour.RED + "Invalid card rank. Please try again: " + Colour.RESET);
                continue;
            }

            boolean validRequest = false;
            boolean requestIsSet = false;
            for (Card card : player.getHand()) {
                if (card.isMatch(request) && card.suit == Card.Suit.SET) {
                    System.out.print(Colour.GREEN + "You already have all the " + card.getRankName() + "s! Request a different card: " + Colour.RESET);
                    requestIsSet = true;
                } else if (card.isMatch(request)) {
                    // If any of the player's cards match the request (and they're not a set), the request is valid
                    validRequest = true;
                    break;
                }
            }

            if (requestIsSet) {
                continue;
            }

            if (validRequest) {
                break;
            } else {
                System.out.print(Colour.GREEN + "You don't have that rank. Request a different card: " + Colour.RESET);
            }
        }
        return request;
    }

    private static void printCards(ArrayList<Card> cards, boolean hideCards) {
        if (cards.size() == 0) {
            System.out.println(Colour.ITALIC + "[Empty hand]" + Colour.RESET);
        } else if (hideCards) {
            // Print the back side of the computer's cards except for sets
            String hiddenCard = """
            ┌───────┐
            │░░░░░░░│
            │░░░░░░░│
            │░░░░░░░│
            └───────┘
            """;

            computer.sortHand();

            for (int i = 0; i < hiddenCard.split("\n").length; i++) {
                for (Card card : cards) {
                    if (card.suit == Card.Suit.SET) {
                        System.out.print(card.suit.colour + card.toString().split("\n")[i] + Colour.RESET);
                    } else {
                        System.out.print(Colour.BLUE + hiddenCard.split("\n")[i] + Colour.RESET);
                    }
                }
                System.out.println();
            }

        } else {
            for (int i = 0; i < cards.get(0).toString().split("\n").length; i++) {
                for (Card card : cards) {
                    System.out.print(card.suit.colour + card.toString().split("\n")[i] + Colour.RESET);
                }
                System.out.println();
            }
        }
    }

    private static byte cardRankToNumber(String cardRank) throws Exception {
        return (byte) switch (cardRank) {
            case "ace", "a" -> 1;
            case "two", "2" -> 2;
            case "three", "3" -> 3;
            case "four", "4" -> 4;
            case "five", "5" -> 5;
            case "six", "6" -> 6;
            case "seven", "7" -> 7;
            case "eight", "8" -> 8;
            case "nine", "9" -> 9;
            case "ten", "10" -> 10;
            case "jack", "j" -> 11;
            case "queen", "q" -> 12;
            case "king", "k" -> 13;
            default -> throw new Exception("Invalid card rank");
        };
    }

    private static String cardNumberToRank(byte cardNumber) {
        return switch (cardNumber) {
            case 1 -> "Ace";
            case 11 -> "Jack";
            case 12 -> "Queen";
            case 13 -> "King";
            default -> cardNumber + "";
        };
    }

    private static void gameOver() {
        Scanner sc = new Scanner(System.in);

        System.out.print("\nThe game is over. All 13 sets have been won.");
        sc.nextLine();

        if (player.getSetCount() > computer.getSetCount()) {
            System.out.print("Good job, you won!");
            sc.nextLine();

            player.addWin();

            String gameString = player.getWins() == 1 ? " game" : " games";
            System.out.print("You have won " + player.getWins() + gameString + ".");
            sc.nextLine();

            serializePlayer(false);
        } else if (player.getSetCount() < computer.getSetCount()) {
            System.out.print("Sorry, you lost! The computer won.");
            sc.nextLine();

            player.addLoss();

            serializePlayer(false);
        } else {
            System.out.print("It's a tie!");
            sc.nextLine();
        }

        System.out.print("You got " + player.getSetCount() + " sets and the computer got " + computer.getSetCount() + " sets.");
        sc.nextLine();
    }

    private static void createAccount() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print(Colour.GREEN + "Please enter your full name: " + Colour.RESET);
            String name = sc.nextLine();

            System.out.print(Colour.GREEN + "Please enter your gender (male/female): " + Colour.RESET);
            Person.Gender gender;
            while (true) {
                try {
                    gender = Person.Gender.valueOf(sc.nextLine().toUpperCase());
                    break;
                } catch (Exception e) {
                    System.out.print(Colour.RED + "Invalid gender. Please try again: " + Colour.RESET);
                }
            }

            System.out.print(Colour.GREEN + "Please enter your age: " + Colour.RESET);
            byte age;
            while (true) {
                try {
                    age = Byte.parseByte(sc.nextLine());
                    break;
                } catch (Exception e) {
                    System.out.print(Colour.RED + "Invalid age. Please try again: " + Colour.RESET);
                }
            }

            System.out.print(Colour.GREEN + "Please create a username: " + Colour.RESET);
            String username = sc.nextLine();
            System.out.print(Colour.GREEN + "Please create a password: " + Colour.RESET);
            String password = sc.nextLine();

            player = new Player(name, gender, age, username, password, 0, 0);

            boolean success = serializePlayer(true);
            if (success) break;
        }

    }

    private static boolean serializePlayer(boolean createNewAccount) {
        // Serialization (write to file)
        try {
            // First all the accounts must be loaded so the new account can be added to the array,
            // then the array is re-written to the file with the new account.
            File f = new File(filename);
            if (f.isFile() && createNewAccount) {
                FileInputStream fileIn = new FileInputStream(filename);
                ObjectInputStream in = new ObjectInputStream(fileIn);

                accounts = (ArrayList<Player>) in.readObject();

                fileIn.close();
                in.close();
            }

            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            if (createNewAccount) {
                accounts.add(player);
            }
            out.writeObject(accounts);

            fileOut.close();
            out.close();

            if (createNewAccount) {
                System.out.println(Colour.ITALIC + "Your account has been created!" + Colour.RESET);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(Colour.RED + "There was an error creating your account. Please try again." + Colour.RESET);
            return false;
        }
    }

    private static void login() {
        Scanner sc = new Scanner(System.in);

        System.out.print(Colour.GREEN + "Enter your username: " + Colour.RESET);
        String username = sc.nextLine();
        System.out.print(Colour.GREEN + "Enter your password: " + Colour.RESET);
        String password = sc.nextLine();

        boolean valid = false;

        File f = new File(filename);
        if (!f.isFile()) {
            // If there isn't an account file, the user's account won't exist
            invalidLogin();
            valid = true;
        }

        // Deserialization (read accounts from the file and see if the username and password match one of them)
        try {
            // Read accounts from the file
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);

            accounts = (ArrayList<Player>) in.readObject();

            in.close();
            file.close();
        } catch (Exception e) {
            System.out.println(Colour.RED + "There was an error logging in. Try creating a new account." + Colour.RESET);
            createAccount();
            return;
        }

        // See if the username and password match one of the account's credentials
        for (Player account : accounts) {
            if (account.getUsername().equals(username) && account.getPassword().equals(password)) {
                player = account;
                valid = true;
            }
        }

        if (!valid) {
            invalidLogin();
        }
    }

    private static void invalidLogin() {
        Scanner sc = new Scanner(System.in);
        System.out.print(Colour.GREEN + "Couldn't find an account with those credentials. Enter 1 to try again or 2 to create a new account: " + Colour.RESET);
        while (true) {
            String choice = sc.nextLine();
            if (choice.equals("1")) {
                login();
                break;
            } else if (choice.equals("2")) {
                createAccount();
                break;
            } else {
                System.out.print(Colour.RED + "Invalid input. Please try again: " + Colour.RESET);
            }
        }
    }

}
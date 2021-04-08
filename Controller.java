import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Controller {
  // Input stream scanner
  private static Scanner scanner;

  // Delimiter used to separate card fields when card is read
  private final static String CARD_DELIMITER = "@";

  // Number of fields in the card data
  private final static int NUM_CARD_DATA = 2;

  // Index of each field after parsing the card data
  private final static int CARD_NUMBER_I = 0;
  private final static int EXPIRATION_I = 1;

  // Date parser
  private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/yy");

  // Choices for accounts
  private final static String SAVINGS = "1";
  private final static String CHECKING = "2";

  // Choice for account actions
  private final static String SEE_BALANCE = "1";
  private final static String DEPOSIT = "2";
  private final static String WITHDRAW = "3";
  private final static String SWITCH = "4";
  private final static String DONE = "5";

  public static void main(String[] args) {
    // Initialize scanner
    scanner = new Scanner(System.in);

    // Be strict with the date format being parsed
    DATE_FORMAT.setLenient(false);

    // Controller loop
    while (true) {
      // wait for card
      String cardNumber = waitForCard();

      // wait for a valid pin number
      waitForPin(cardNumber);

      // loop while the user wants to switch accounts
      String action;
      do {
        // wait for account choice
        String accountName = waitForAccountChoice(cardNumber);

        // Loop while the user is not done with the current account
        do {
          // wait for choice of see balance/deposit/withdraw/done/switch account
          action = waitForActionChoice();

          if (!action.equals(DONE) && !action.equals(SWITCH)) {
            performAction(action, accountName, cardNumber);
          }
        } while (!action.equals(DONE) && !action.equals(SWITCH));
      } while (action.equals(SWITCH));

      System.out.println("Goodbye!");
    }
  }

  // Wait for a valid card to be inserted and return the card number
  private static String waitForCard() {
    String[] card;

    System.out.println("Insert your card.");

    do {
      // Read a line and parse its fields by a delimiter
      String line = scanner.nextLine();
      card = line.split(CARD_DELIMITER);
    } while (!cardValid(card)); // Loop while the card is invalid

    return card[CARD_NUMBER_I];
  }

  private static boolean cardValid(String[] card) {
    Date expiration;

    // Check if card has the correct number of fields
    if (card.length < NUM_CARD_DATA) {
      System.out.println("Your card is missing fields.");
      return false;
    }

    // Check if the card number is numeric
    if (!card[CARD_NUMBER_I].matches("[0-9]+")) {
      System.out.println("You card number doesn't contain only digits.");
      return false;
    }

    // Check if the card number exists in the bank
    if (!Bank.cardExists(card[CARD_NUMBER_I])) {
      System.out.println("Your card isn't registered with the bank.");
      return false;
    }

    // Parse the expiration date and check if it's formatted correctly
    try {
      expiration = DATE_FORMAT.parse(card[EXPIRATION_I]);
    } catch (ParseException e) {
      System.out.println("The format of your card's expiration date is invalid.");
      return false;
    }

    // Check if the card is expired
    if (expiration.before(new Date())) {
      System.out.println("Your card is expired.");
      return false;
    }

    return true;
  }

  // Wait for a valid pin to be given
  private static void waitForPin(String cardNumber) {
    String pin;

    System.out.println("Enter your PIN number.");

    do {
      pin = scanner.nextLine();
    } while (!pinValid(pin, cardNumber)); // Loop while pin is invalid
  }

  private static boolean pinValid(String pin, String cardNumber) {
    // Check if the pin is numeric
    if (!pin.matches("[0-9]+")) {
      System.out.println("You pin doesn't contain only digits.");
      return false;
    }

    // Check if the entered pin matches the card's pin
    if (!Bank.pinCorrect(pin, cardNumber)) {
      System.out.println("Incorrect pin.");
      return false;
    }

    return true;
  }

  // Wait for an account choice of checking or savings
  private static String waitForAccountChoice(String cardNumber) {
    String choice;

    System.out.println("Pick an account (1 savings, 2 checking).");

    do {
      choice = scanner.nextLine();
    } while (!accountValid(choice)); // Loop until a valid choice is given

    if (choice.equals(SAVINGS)) {
      return "savings";
    } else {
      return "checking";
    }
  }

  private static boolean accountValid(String choice) {
    if (choice.equals(SAVINGS) || choice.equals(CHECKING)) {
      return true;
    }

    System.out.println("That's not a valid account choice.");

    return false;
  }

  private static String waitForActionChoice() {
    String choice;

    System.out.println("What would you like to do? (1 see balance, 2 deposit, 3 withdraw, 4 switch account, 5 done)");

    do {
      choice = scanner.nextLine();
    } while (!actionValid(choice)); // Loop until a valid action is given

    return choice;
  }

  private static boolean actionValid(String choice) {
    if (choice.equals(SEE_BALANCE) || choice.equals(DEPOSIT) || choice.equals(WITHDRAW) || choice.equals(SWITCH)
        || choice.equals(DONE)) {
      return true;
    }

    System.out.println("That is not a valid action.");

    return false;
  }

  private static void performAction(String action, String account, String cardNumber) {
    if (action.equals(SEE_BALANCE)) {
      System.out.println("Balance is: " + Bank.getBalance(account, cardNumber));
    }

    if (action.equals(DEPOSIT)) {
      int quantity;
      boolean validInput;

      System.out.println("How much would you like to deposit?");

      do {
        try {
          validInput = true;
          quantity = scanner.nextInt();
        } catch (InputMismatchException e) { // In case input isn't an int
          System.out.println("Invalid quantity.");
          validInput = false;
        }

        scanner.nextLine(); // Clear the line
      } while (!validInput); // Loop until a valid quantity is given

      // Bank.deposit(account, cardNumber);
    }
  }
}

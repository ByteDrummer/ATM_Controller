import java.util.Date;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Controller {
  // Input stream scanner
  private static Scanner scanner;

  // List of banks
  private static Bank[] banks;

  // Delimiter used to separate card fields when card is read
  private final static String CARD_DELIMITER = "@";

  // Number of fields in the card data
  private final static int NUM_CARD_DATA = 3;
  // Index of each field after parsing the card data
  private final static int CARD_NUMBER_I = 0;
  private final static int EXPIRATION_I = 1;
  private final static int BANK_NAME_I = 2;

  // Date parser
  private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/yy");

  // Choices for accounts
  private final static String CHECKING = "1";
  private final static String SAVINGS = "2";

  // Choice for account actions
  private final static String SEE_BALANCE = "1";
  private final static String DEPOSIT = "2";
  private final static String WITHDRAW = "3";
  private final static String SWITCH = "4";
  private final static String DONE = "D";

  public static void main(String[] args) {
    // Initialize scanner
    scanner = new Scanner(System.in);

    // Initialize an array of banks with clients
    initializeBanks();

    // Be strict with the date format being parsed
    DATE_FORMAT.setLenient(false);

    // Controller loop
    while (true) {
      String[] card;
      String cardNumber;
      Bank bank;
      boolean pinValid;

      try {
        do {
          // wait for card
          card = waitForCard();
          cardNumber = card[CARD_NUMBER_I];
          bank = getBank(card[BANK_NAME_I]);

          // wait for a valid pin number
          pinValid = waitForPin(cardNumber, bank);
        } while (!pinValid);

        // loop while the user wants to switch accounts
        String action;
        do {
          // wait for account choice
          String account = waitForAccountChoice(cardNumber);
          action = DONE; // default to done to avoid unexitable loop

          if (!account.equals(DONE)) {
            // Loop while the user is not done with the current account
            do {
              // wait for choice of see balance/deposit/withdraw/done/switch account
              action = waitForActionChoice();

              if (!action.equals(DONE) && !action.equals(SWITCH)) {
                performAction(action, account, cardNumber, bank);
              }
            } while (!action.equals(DONE) && !action.equals(SWITCH));
          }
        } while (action.equals(SWITCH));

        System.out.println("Goodbye!");
      } catch (NoSuchElementException e) {
        System.out.println("Reached end of input.");
        System.exit(0);
      }
    }
  }

  private static void initializeBanks() {
    Bank bank1 = new Bank("BoA");
    bank1.addClient("123", "123", 0, 0);
    bank1.addClient("1234", "1234", 262, 4353);

    Bank bank2 = new Bank("Fargo");
    bank2.addClient("321", "321", 50, 25);
    bank2.addClient("4321", "4321", 6547, 769085);

    banks = new Bank[] { bank1, bank2 };
  }

  // Wait for a valid card to be inserted and return card's data
  private static String[] waitForCard() {
    String[] card;

    do {
      System.out.println("Insert your card.");

      // Read a line and parse its fields by a delimiter
      String line = scanner.nextLine();
      System.out.println(line);

      card = line.split(CARD_DELIMITER);
    } while (!cardValid(card)); // Loop while the card is invalid

    return card;
  }

  // Check if the card is valid
  private static boolean cardValid(String[] card) {
    Date expiration;
    Bank bank;

    // Check if card has the correct number of fields
    if (card.length < NUM_CARD_DATA) {
      System.out.println("Your card is missing fields.");
      return false;
    }

    // Check if the bank exists
    bank = getBank(card[BANK_NAME_I]);
    if (bank == null) {
      System.out.println("Your bank is not supported here.");
      return false;
    }

    // Check if the card number is numeric
    if (!card[CARD_NUMBER_I].matches("[0-9]+")) {
      System.out.println("You card number doesn't contain only digits.");
      return false;
    }

    // Check if the card number exists in the bank
    if (!bank.cardExists(card[CARD_NUMBER_I])) {
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

  // Search for and return the bank that matches the given name
  private static Bank getBank(String bankName) {
    for (Bank bank : banks) {
      if (bank.getName().equals(bankName)) {
        return bank;
      }
    }

    return null;
  }

  // Wait for a valid pin to be given or timeout after three attempts
  private static boolean waitForPin(String cardNumber, Bank bank) {
    boolean pinValid;
    int counter = 0;

    do {
      String pin;

      System.out.println("Enter your PIN number.");

      pin = scanner.nextLine();
      System.out.println(pin);

      pinValid = pinValid(pin, cardNumber, bank);

      if (!pinValid) {
        counter++;
      }

      if (counter == 3) {
        System.out.println("Ending session after too many attempts.");
        return false;
      }

    } while (!pinValid); // Loop while pin is invalid

    return true;
  }

  private static boolean pinValid(String pin, String cardNumber, Bank bank) {
    // Check if the pin is numeric
    if (!pin.matches("[0-9]+")) {
      System.out.println("You pin doesn't contain only digits.");
      return false;
    }

    // Check if the entered pin matches the card's pin
    if (!bank.pinCorrect(pin, cardNumber)) {
      System.out.println("Incorrect pin.");
      return false;
    }

    return true;
  }

  // Wait for an account choice of checking or savings
  private static String waitForAccountChoice(String cardNumber) {
    String choice;

    do {
      System.out.println("Pick an account (1 checking, 2 savings, D done).");

      choice = scanner.nextLine();
      System.out.println(choice);
    } while (!accountValid(choice)); // Loop until a valid choice is given

    return choice;
  }

  private static boolean accountValid(String choice) {
    if (choice.equals(SAVINGS) || choice.equals(CHECKING) || choice.equals(DONE)) {
      return true;
    }

    System.out.println("That's not a valid account choice.");

    return false;
  }

  private static String waitForActionChoice() {
    String choice;

    do {
      System.out.println("What would you like to do? (1 see balance, 2 deposit, 3 withdraw, 4 switch account, D done)");

      choice = scanner.nextLine();
      System.out.println(choice);
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

  private static void performAction(String action, String account, String cardNumber, Bank bank) {
    if (action.equals(SEE_BALANCE)) {
      int balance;

      if (account.equals(CHECKING)) {
        balance = bank.getCheckingBalance(cardNumber);
      } else {
        balance = bank.getSavingsBalance(cardNumber);
      }

      System.out.println("Balance is: " + balance);
    } else {
      int quantity = 0;
      boolean validInput;

      do {
        validInput = true;

        System.out.println("Enter a quantity.");

        try {
          quantity = scanner.nextInt();
          System.out.println(quantity);
          quantity = Math.abs(quantity);
        } catch (InputMismatchException e) { // In case input isn't an int
          System.out.println("Invalid quantity.");
          validInput = false;
        }

        scanner.nextLine(); // Clear the line
      } while (!validInput); // Loop until a valid quantity is given

      if (action.equals(WITHDRAW)) {
        quantity = -1 * quantity;
      }

      if (account.equals(CHECKING)) {
        bank.updateChecking(quantity, cardNumber);
      } else {
        bank.updateSavings(quantity, cardNumber);
      }
    }
  }
}

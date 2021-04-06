import java.util.Date;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Controller {
  // Delimiter used to separate card fields when card is read
  private final static String CARD_DELIMITER = "@";

  // Number of fields in the card data
  private final static int NUM_CARD_DATA = 2;

  // Index of each field after parsing the card data
  private final static int CARD_NUMBER_I = 0;
  private final static int EXPIRATION_I = 1;

  // Date parser
  private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/yy");

  public static void main(String[] args) {
    // Initialize input scanner
    Scanner scanner = new Scanner(System.in);

    // Be strict with the date format being parsed
    DATE_FORMAT.setLenient(false);

    // Controller loop
    while (true) {
      // wait for card
      String[] card = getCard(scanner);
      System.out.println(card[0] + " " + card[1]);

      // wait for a valid pin number
      getPin(card[CARD_NUMBER_I], scanner);

      // wait for account choice
      // see balance/deposit/withdraw
    }
  }

  // Wait for a valid card to be inserted and
  // return the card number and bank name
  private static String[] getCard(Scanner scanner) {
    String[] card;

    System.out.println("Insert your card.");

    do {
      // Read a line and parse its fields by a delimiter
      String line = scanner.nextLine();
      card = line.split(CARD_DELIMITER);
    } while (!cardValid(card)); // Loop while the card is invalid

    return card;
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

  // Wait for a valid pin to be given and return it
  private static String getPin(String cardNumber, Scanner scanner) {
    String pin;

    System.out.println("Enter your PIN number.");

    do {
      pin = scanner.nextLine();
    } while (!pinValid(pin, cardNumber)); // Loop while pin is invalid

    return pin;
  }

  private static boolean pinValid(String pin, String cardNumber) {
    if (!pin.matches("[0-9]+")) {
      System.out.println("You pin doesn't contain only digits.");
      return false;
    }

    if (!Bank.pinCorrect(pin, cardNumber)) {
      System.out.println("Incorrect pin.");
      return false;
    }

    return true;
  }
}

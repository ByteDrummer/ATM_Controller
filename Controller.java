import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import sun.misc.Signal;
import sun.misc.SignalHandler;

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

    // Capture SIGINT
    Signal.handle(new Signal("INT"), new SignalHandler() {
      public void handle(Signal arg0) {
        cleanExit(scanner);
      }
    });

    // Be strict with the date format being parsed
    DATE_FORMAT.setLenient(false);

    // Controller loop
    while (true) {
      // wait for card
      String[] card = getCard(scanner);

      // wait for pin number
      // ask specific bank if pin is correct
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

  private static void cleanExit(Scanner scanner) {
    System.out.println("Shutting down controller");

    if (scanner != null) {
      scanner.close();
    }

    System.exit(0);
  }
}

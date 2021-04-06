import java.util.Scanner;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class Controller {
  private final static String CARD_DELIMITER = "@";
  private final static int NUM_CARD_DATA = 3;

  public static void main(String[] args) {
    // Initialize input scanner
    Scanner scanner = new Scanner(System.in);

    // Capture SIGINT
    Signal.handle(new Signal("INT"), new SignalHandler() {
      public void handle(Signal arg0) {
        cleanExit(scanner);
      }
    });

    // Controller loop
    while (true) {
      // wait for card (card number, expiration, bank name)
      String[] card = getCard(scanner);

      // wait for pin number
      // ask specific bank if pin is correct
      // wait for account choice
      // see balance/deposit/withdraw
    }
  }

  // Wait for a card to be inserted and return the card number and bank name
  private static String[] getCard(Scanner scanner) {
    String[] card;

    System.out.println("Insert your card.");

    do {
      // Read a line and parse its fields by a delimiter
      String line = scanner.nextLine();
      card = line.split(CARD_DELIMITER);
    } while (!isCardValid(card)); // Loop while the card is invalid

    return card;
  }

  private static boolean isCardValid(String[] card) {
    if (card.length < NUM_CARD_DATA) {
      System.out.print("Your card is missing fields.");
      return false;
    }

    if (!card[0].matches("[0-9]+")) {
      System.out.println("You card number doesn't contain only digits.");
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

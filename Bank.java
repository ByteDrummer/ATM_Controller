import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class Bank {
  String name;
  ArrayList<Client> clients;

  public Bank(String name) {
    this.name = name;
    clients = new ArrayList<Client>();
  }

  // Add a client to the list of clients
  public void addClient(String cardNumber, String pin, int checkingBalance, int savingsBalance) {
    Client client = new Client(cardNumber, pin, checkingBalance, savingsBalance);
    clients.add(client);
  }

  // Check if the cardNumber exists
  public boolean cardExists(String cardNumber) {
    Client client = getClient(cardNumber);

    if (client != null) {
      return true;
    }

    return false;
  }

  // Check if the pin for a specific card is correct
  public boolean pinCorrect(String pin, String cardNumber) {
    Client client = getClient(cardNumber);

    if (client != null && client.getPin().equals(pin)) {
      return true;
    }

    return false;
  }

  // Get the client for a specific card number and return a reference to the
  // database
  public Client getClient(String cardNumber) {
    for (Client client : clients) {
      if (client.getCardNumber().equals(cardNumber)) {
        return client;
      }
    }

    return null;
  }

  public static int getBalance(String accountName, String cardNumber) {
    Object[] result = getAccount(accountName, cardNumber);
    JsonObject account;
    JsonKey balanceKey;

    if (result == null) {
      return Integer.MIN_VALUE;
    }

    balanceKey = Jsoner.mintJsonKey("balance", DEFAULT_BALANCE);
    account = (JsonObject) result[0];

    return account.getInteger(balanceKey);
  }

  public static Object[] getAccount(String accountName, String cardNumber) {
    Object[] result = getClient(cardNumber);
    JsonObject client;
    JsonObject account;

    if (result == null) {
      return null;
    }

    client = (JsonObject) result[0];
    account = (JsonObject) client.get(accountName);

    return new Object[] { account, result[1] };
  }

  public static void deposit(int quantity, String accountName, String cardNumber) {
    Object[] result = getAccount(accountName, cardNumber);

    if (result != null) {
      JsonObject db = (JsonObject) result[1];
      JsonObject account;
      JsonKey balanceKey = Jsoner.mintJsonKey("balance", DEFAULT_BALANCE);
      int balance;
      Writer writer;

      account = (JsonObject) result[0];
      balance = account.getInteger(balanceKey) + quantity;

      account.put("balance", balance);

      // write to output file
      try {
        writer = new FileWriter(DB_PATH);
      } catch (IOException e) {
        e.printStackTrace();
        return;
      }

      try {
        writer.write(Jsoner.prettyPrint(db.toJson()));
      } catch (IOException e) {
        e.printStackTrace();

        try {
          writer.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }

        return;
      }

      try {
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}

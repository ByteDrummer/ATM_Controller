import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class Bank {
  String name;
  ArrayList<Client> clients;

  public Bank (String name) {
    this.name = name;
    clients = new ArrayList<Client>();
  }

  public void addClient(String cardNumber, int checkingBalance, int savingsBalance) {
    Client client = new Client(cardNumber, checkingBalance, savingsBalance);
    clients.add(client);
  }

  // Check the database to see if the card exists
  public static boolean cardExists(String cardNumber) {
    Object[] result = getClient(cardNumber);

    if (result == null) {
      return false;
    }

    return true;
  }

  // Check the database to see if the pin for a specific card is correct
  public static boolean pinCorrect(String pin, String cardNumber) {
    Object[] result = getClient(cardNumber);
    JsonObject client;

    if (result == null) {
      return false;
    }

    client = (JsonObject) result[0];

    if (pin.equals((String) client.get("pin"))) {
      return true;
    }

    return false;
  }

  // Get the client for a specific card number and return a reference to the
  // database
  public static Object[] getClient(String cardNumber) {
    Object[] result = getClients();
    JsonArray clients;
    JsonKey cardNumberKey;

    if (result == null) {
      return null;
    }

    cardNumberKey = Jsoner.mintJsonKey("cardNumber", DEFAULT_CARDNUM);
    clients = (JsonArray) getClients()[0];

    JsonObject db = (JsonObject) result[1];
    System.out.println(Jsoner.prettyPrint(db.toJson()));
    JsonObject test = (JsonObject) clients.get(0);
    test.put("2", "2");
    System.out.println(test.toJson());
    System.out.println(Jsoner.prettyPrint(db.toJson()));

    // Loop through the list of clients and find a matching one
    for (Object clientObj : clients) {
      JsonObject client = (JsonObject) clientObj;

      if (cardNumber.equals(client.getString(cardNumberKey))) {
        return new Object[] { client, result[1] };
      }
    }

    return null;
  }

  // Get a list of clients in the database and return reference to the database
  public static Object[] getClients() {
    FileReader reader;
    JsonObject db;
    JsonArray clients;

    try {
      reader = new FileReader(DB_PATH);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }

    try {
      db = (JsonObject) Jsoner.deserialize(reader);
    } catch (JsonException e) {
      e.printStackTrace();
      return null;
    }

    clients = (JsonArray) db.get("clients");

    return new Object[] { clients, db };
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

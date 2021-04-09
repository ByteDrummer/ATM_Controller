import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonKey;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

public class Bank {
  private final static String DB_PATH = "./BankDB.json";

  private final static int DEFAULT_BALANCE = Integer.MIN_VALUE;
  private final static String DEFAULT_CARDNUM = "-1";

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





    JsonObject db = (JsonObject) result[1];
    System.out.println(Jsoner.prettyPrint(db.toJson()));






    cardNumberKey = Jsoner.mintJsonKey("cardNumber", DEFAULT_CARDNUM);
    clients = (JsonArray) getClients()[0];

    // Loop through the list of clients and find a matching one
    for (Object clientObj : clients) {
      JsonObject client = (JsonObject) clientObj;



      client.put("2", "2");
      System.out.println(client.toJson());
      System.out.println(Jsoner.prettyPrint(db.toJson()));



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

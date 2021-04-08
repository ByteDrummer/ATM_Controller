import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Bank {
  private final static String DB_PATH = "./BankDB.json";

  // Check the database to see if the card exists
  public static boolean cardExists(String cardNumber) {
    JSONObject client = getClient(cardNumber);

    if (client == null) {
      return false;
    }

    return true;
  }

  // Check the database to see if the pin for a specific card is correct
  public static boolean pinCorrect(String pin, String cardNumber) {
    JSONObject client = getClient(cardNumber);

    if (client == null) {
      return false;
    }

    if (pin.equals((String) client.get("pin"))) {
      return true;
    }

    return false;
  }

  // Get the client for a specific card number
  public static JSONObject getClient(String cardNumber) {
    JSONArray clients = getClients();

    if (clients == null) {
      return null;
    }

    // Loop through the list of clients and find a matching one
    for (Object clientObj : clients) {
      JSONObject client = (JSONObject) clientObj;

      if (cardNumber.equals((String) client.get("cardNumber"))) {
        return client;
      }
    }

    return null;
  }

  // Get a list of clients in the database
  public static JSONArray getClients() {
    FileReader reader;
    JSONParser parser = new JSONParser();
    JSONObject db;
    JSONArray clients;

    try {
      reader = new FileReader(DB_PATH);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }

    try {
      db = (JSONObject) parser.parse(reader);
    } catch (IOException | ParseException e) {
      e.printStackTrace();
      return null;
    }

    clients = (JSONArray) db.get("clients");

    return clients;
  }

  public static long getBalance(String accountName, String cardNumber) {
    JSONObject client = getClient(cardNumber);
    JSONObject account = (JSONObject) client.get(accountName);

    return (long) account.get("balance");
  }
}

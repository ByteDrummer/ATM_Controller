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
    Object[] result = getClient(cardNumber);

    if (result == null) {
      return false;
    }

    return true;
  }

  // Check the database to see if the pin for a specific card is correct
  public static boolean pinCorrect(String pin, String cardNumber) {
    Object[] result = getClient(cardNumber);

    if (result == null) {
      return false;
    }

    JSONObject client = (JSONObject) result[0];

    if (pin.equals((String) client.get("pin"))) {
      return true;
    }

    return false;
  }

  // Get the client for a specific card number and return a reference to the
  // database
  public static Object[] getClient(String cardNumber) {
    Object[] result = getClients();
    JSONArray clients;

    if (result == null) {
      return null;
    }

    clients = (JSONArray) getClients()[0];

    // Loop through the list of clients and find a matching one
    for (Object clientObj : clients) {
      JSONObject client = (JSONObject) clientObj;

      if (cardNumber.equals((String) client.get("cardNumber"))) {
        return new Object[] { client, result[1] };
      }
    }

    return null;
  }

  // Get a list of clients in the database and retrun reference to the database
  public static Object[] getClients() {
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

    return new Object[] { clients, db };
  }

  public static long getBalance(String accountName, String cardNumber) {
    Object[] result = getAccount(accountName, cardNumber);

    if (result == null) {
      return  Long.MIN_VALUE;
    }

    JSONObject account = (JSONObject) result[0];

    return (long) account.get("balance");
  }

  public static Object[] getAccount(String accountName, String cardNumber) {
    Object[] result = getClient(cardNumber);

    if (result == null) {
      return  null;
    }

    JSONObject client = (JSONObject) result[0];
    JSONObject account = (JSONObject) client.get(accountName);

    return new Object[]{account, result[1]};
  }

  //public static void deposit(int quantity, String accountName, String cardNumber) {
    //long balance = getBalance(accountName, cardNumber);
    //balance += quantity;
  //}
}

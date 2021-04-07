import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Bank {
  private final static String DB_PATH = "./BankDB.json";

  public static boolean cardExists(String cardNumber) {
    JSONObject db = readDB();

    if (db == null) {
      return false;
    }

    JSONArray accounts = (JSONArray) db.get("members");

    for (Object accountObj : accounts) {
      JSONObject account = (JSONObject) accountObj;
      if (cardNumber.equals((String) account.get("cardNumber"))) {
        return true;
      }
    }

    return false;
  }

  public static JSONObject readDB() {
    FileReader reader;
    JSONParser parser = new JSONParser();
    JSONObject db;

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

    return db;
  }
}

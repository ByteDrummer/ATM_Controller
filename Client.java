public class Client {
  private String cardNumber;
  private String pin;
  private int checkingBalance;
  private int savingsBalance;

  public Client(String cardNumber, String pin, int checkingBalance, int savingsBalance) {
    this.cardNumber = cardNumber;
    this.pin = pin;
    this.checkingBalance = checkingBalance;
    this.savingsBalance = savingsBalance;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public String getPin() {
    return pin;
  }

  public int getCheckingBalance() {
    return checkingBalance;
  }

  public int getSavingsBalance() {
    return savingsBalance;
  }

  public void updateChecking(int quantity) {
    checkingBalance += quantity;
  }

  public void updateSavings(int quantity) {
    savingsBalance += quantity;
  }
}

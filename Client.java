public class Client {
  private String cardNumber;
  private int checkingBalance;
  private int savingsBalance;

  public Client(String cardNumber, int checkingBalance, int savingsBalance) {
    this.cardNumber = cardNumber;
    this.checkingBalance = checkingBalance;
    this.savingsBalance = savingsBalance;
  }

  public void updateChecking(int quantity) {
    checkingBalance += quantity;
  }

  public void updateSavings(int quantity) {
    savingsBalance += quantity;
  }
}

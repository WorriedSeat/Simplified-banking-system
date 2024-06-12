// Import necessary libraries
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
 
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numBankOp = Integer.parseInt(scanner.nextLine());
        // Initialize the banking system
        BankingSystem.getBank();
        for (int i = 0; i < numBankOp; i++) {
            // Split the operation input into its components
            String[] operation = scanner.nextLine().split(" ");
            // Perform the specified operation
            switch (operation[0]) {
                case "Create" -> BankingSystem.getBank().create(operation[3], operation[2], Float.parseFloat(operation[4]));
                case "Withdraw" -> BankingSystem.getBank().withdraw(operation[1], Float.parseFloat(operation[2]));
                case "Deposit" -> BankingSystem.getBank().deposit(operation[1], Float.parseFloat(operation[2]));
                case "Transfer" -> BankingSystem.getBank().transfer(operation[1], operation[2], Float.parseFloat(operation[3]));
                case "View" -> BankingSystem.getBank().view(operation[1]);
                case "Deactivate" -> BankingSystem.getBank().deactivate(operation[1]);
                case "Activate" -> BankingSystem.getBank().activate(operation[1]);
            }
        }
    }
}
 
// Class representing the banking system
class BankingSystem {
    private static BankingSystem bank;
    // List to store all bank accounts
    public ArrayList<Account> accounts = new ArrayList<>();
 
    // Private constructor to enforce singleton pattern
    private BankingSystem() {
    }
 
    // Method to get the instance of the banking system
    public static BankingSystem getBank() {
        if (bank == null) {
            bank = new BankingSystem();
        }
        return bank;
    }
 
    // Method to create a new account based on the type
    public void create(String name, String type, float initialBalance) {
        switch (type) {
            case "Savings" -> {
                SavingsAccount account = new SavingsAccount(initialBalance, name);
                accounts.add(account);
            }
            case "Checking" -> {
                CheckingAccount account = new CheckingAccount(initialBalance, name);
                accounts.add(account);
            }
            case "Business" -> {
                BusinessAccount account = new BusinessAccount(initialBalance, name);
                accounts.add(account);
            }
        }
    }
 
    // Method to deposit money into an account
    public void deposit(String name, float dmoney) {
        Account account = isPresent(name);
        if (account != null) {
            account.deposit(dmoney, account);
        }
    }
 
    // Method to withdraw money from an account
    public void withdraw(String name, float wmoney) {
        Account account = isPresent(name);
        if (account != null && isActive(account) && isEnoughMoney(account, wmoney)) {
            account.withdraw(wmoney, account);
        }
    }
 
    // Method to transfer money between accounts
    public void transfer(String sender, String getter, float tmoney) {
        Account senderAcc = isPresent(sender);
        if (senderAcc != null) {
            Account getterAcc = isPresent(getter);
            if (getterAcc != null && isActive(senderAcc) && isEnoughMoney(senderAcc, tmoney)) {
                senderAcc.transfer(tmoney, senderAcc, getterAcc);
            }
        }
    }
 
    // Method to activate an account
    public void activate(String name) {
        Account account = isPresent(name);
        if (account != null) {
            if (!account.isActive)
                account.activate(account);
            else System.out.println("Error: Account " + name + " is already activated.");
        }
    }
 
    // Method to deactivate an account
    public void deactivate(String name) {
        Account account = isPresent(name);
        if (account != null) {
            if (account.isActive)
                account.deactivate(account);
            else System.out.println("Error: Account " + name + " is already deactivated.");
        }
    }
 
    // Method to view account details
    public void view(String name) {
        Account account = isPresent(name);
        if (account != null) {
            account.view(account);
        }
    }
 
    // Helper method to check if an account exists
    private Account isPresent(String name) {
        for (Account account : accounts) {
            if (account.name.equals(name)) {
                return account;
            }
        }
        System.out.println("Error: Account " + name + " does not exist.");
        return null;
    }
 
    // Helper method to check if an account is active
    private boolean isActive(Account account) {
        if (!account.isActive) {
            System.out.println("Error: Account " + account.name + " is inactive.");
            return false;
        }
        return true;
    }
 
    // Helper method to check if an account has enough money for a transaction
    private boolean isEnoughMoney(Account account, float money) {
        if (account.money < money) {
            System.out.println("Error: Insufficient funds for " + account.name + ".");
            return false;
        }
        return true;
    }
}
 
// Interface for account management operations
interface accountManager {
    // Method to transfer money between accounts
    default void transfer(float tmoney, Account sender, Account getter) {
        sender.money -= tmoney;
        float fee = tmoney * sender.transactionFee / 100;
        getter.money += tmoney - fee;
        sender.operations.add("Transfer $" + String.format(Locale.US, "%.3f", tmoney));
        System.out.println(sender.name + " successfully transferred $" + String.format(Locale.US, "%.3f", tmoney - fee)
                + " to " + getter.name + ". New Balance: $" + String.format(Locale.US, "%.3f", sender.money) + ". Transaction Fee: $" +
                String.format(Locale.US, "%.3f", fee) + " (" + sender.transactionFee + "%) in the system.");
    }
 
    // Method to deposit money into an account
    default void deposit(float dmoney, Account account) {
        account.money += dmoney;
        account.operations.add("Deposit $" + String.format(Locale.US, "%.3f", dmoney));
        System.out.println(account.name + " successfully deposited $" + String.format(Locale.US, "%.3f", dmoney)
                + ". New Balance: $" + String.format(Locale.US, "%.3f", account.money) + ".");
    }
 
    // Method to withdraw money from an account
    default void withdraw(float wmoney, Account account) {
        account.money -= wmoney;
        float fee = wmoney * account.transactionFee / 100;
        account.operations.add("Withdrawal $" + String.format(Locale.US, "%.3f", wmoney));
        System.out.println(account.name + " successfully withdrew $" + String.format(Locale.US, "%.3f", wmoney - fee)
                + ". New Balance: $" + String.format(Locale.US, "%.3f", account.money)
                + ". Transaction Fee: $" + String.format(Locale.US, "%.3f", fee) + " (" + account.transactionFee + "%) in the system.");
 
    }
 
    // Method to view account details
    default void view(Account account) {
        String activity;
        if (account.isActive)
            activity = "Active";
        else
            activity = "Inactive";
        System.out.println(account.name + "'s Account: Type: " + account.type + ", Balance: $" +
                String.format(Locale.US, "%.3f", account.money) + ", State: " + activity + ", Transactions: " + account.operations + ".");
    }
 
    // Method to activate an account
    default void activate(Account account) {
        account.isActive = true;
        System.out.println(account.name + "'s account is now activated.");
    }
 
    // Method to deactivate an account
    default void deactivate(Account account) {
        account.isActive = false;
        System.out.println(account.name + "'s account is now deactivated.");
    }
}
 
// Abstract class for bank accounts
abstract class Account implements accountManager {
    float transactionFee, money;
    String name, type;
    boolean isActive = true;
    ArrayList<String> operations = new ArrayList<>();
 
    // Constructor for creating a new account
    Account(float initialBalance, String name, String type) {
        this.money = initialBalance;
        this.name = name;
        this.type = type;
        operations.add("Initial Deposit $" + String.format(Locale.US, "%.3f", initialBalance));
        System.out.println("A new " + type + " account created for " + name + " with an initial balance of $" +
                String.format(Locale.US, "%.3f", initialBalance) + ".");
    }
}
 
//class representing a savings account
class SavingsAccount extends Account {
    SavingsAccount(float initialBalance, String name) {
        super(initialBalance, name, "Savings");
        transactionFee = 1.5F;
    }
}
 
//class representing a checking account
class CheckingAccount extends Account {
    CheckingAccount(float initialBalance, String name) {
        super(initialBalance, name, "Checking");
        transactionFee = 2F;
    }
}
 
//class representing a business account
class BusinessAccount extends Account {
    BusinessAccount(float initialBalance, String name) {
        super(initialBalance, name, "Business");
        transactionFee = 2.5F;
    }
}

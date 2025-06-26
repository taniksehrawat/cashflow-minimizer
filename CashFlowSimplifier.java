import java.util.*;
// Transaction between two users
class Transaction {
    private final String from;
    private final String to;
    private final int amount;

    public Transaction(String from, String to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public int getAmount() { return amount; }

    @Override
    public String toString() {
        return from + " pays " + amount + " to " + to;
    }
}
// User with net balance
class Person {
    private final String name;
    private int netBalance;

    public Person(String name, int netBalance) {
        this.name = name;
        this.netBalance = netBalance;
    }
    public String getName() { return name; }
    public int getNetBalance() { return netBalance; }
    public void updateBalance(int delta) {
        this.netBalance += delta;
    }
}

public class CashFlowSimplifier {

    public List<Transaction> minimizeCashFlow(List<Transaction> inputTransactions) {
        Map<String, Integer> balanceMap = new HashMap<>();

        // Step 1: Net balance calculation
        for (Transaction t : inputTransactions) {
            balanceMap.put(t.getFrom(), balanceMap.getOrDefault(t.getFrom(), 0) - t.getAmount());
            balanceMap.put(t.getTo(), balanceMap.getOrDefault(t.getTo(), 0) + t.getAmount());
        }

        // Step 2: MinHeap for debtors, MaxHeap for creditors
        PriorityQueue<Person> maxHeap = new PriorityQueue<>((a, b) -> b.getNetBalance() - a.getNetBalance());
        PriorityQueue<Person> minHeap = new PriorityQueue<>(Comparator.comparingInt(Person::getNetBalance));

        for (Map.Entry<String, Integer> entry : balanceMap.entrySet()) {
            int balance = entry.getValue();
            if (balance > 0) {
                maxHeap.add(new Person(entry.getKey(), balance));
            } else if (balance < 0) {
                minHeap.add(new Person(entry.getKey(), balance));
            }
        }

        // Step 3: Match and minimize
        List<Transaction> result = new ArrayList<>();
        while (!maxHeap.isEmpty() && !minHeap.isEmpty()) {
            Person creditor = maxHeap.poll();
            Person debtor = minHeap.poll();

            int settledAmount = Math.min(creditor.getNetBalance(), -debtor.getNetBalance());
            result.add(new Transaction(debtor.getName(), creditor.getName(), settledAmount));

            creditor.updateBalance(-settledAmount);
            debtor.updateBalance(settledAmount);

            if (creditor.getNetBalance() > 0) maxHeap.add(creditor);
            if (debtor.getNetBalance() < 0) minHeap.add(debtor);
        }

        return result;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Transaction> inputTransactions = new ArrayList<>();
        System.out.print("Enter number of transactions:");
        int n = scanner.nextInt();
        scanner.nextLine(); 

        System.out.println(" Enter each transaction in format: from to amount");
        for (int i = 1; i <= n; i++) {
            System.out.print("Transaction " + i + ": ");
            String[] parts = scanner.nextLine().trim().split(" ");
            if (parts.length != 3) {
                System.out.println(" Invalid input. Try again.");
                i--;
                continue;
            }
            String from = parts[0];
            String to = parts[1];
            int amount;
            try {
                amount = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                System.out.println(" Amount must be an integer.");
                i--;
                continue;
            }
            inputTransactions.add(new Transaction(from, to, amount));
        }

        CashFlowSimplifier simplifier = new CashFlowSimplifier();
        List<Transaction> result = simplifier.minimizeCashFlow(inputTransactions);

        System.out.println("\n Optimized Transactions to Settle All Debts:");
        for (Transaction t : result) {
            System.out.println(t);
        }

        scanner.close();
    }
}



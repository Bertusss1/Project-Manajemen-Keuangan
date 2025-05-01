import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TransaksiRutinList implements Serializable {
    public static class TransaksiRutin implements Serializable {
        private final String namaTagihan;
        private final double jumlah;
        private final String tanggalJatuhTempo;

        public TransaksiRutin(String namaTagihan, double jumlah, String tanggalJatuhTempo) {
            this.namaTagihan = namaTagihan;
            this.jumlah = jumlah;
            this.tanggalJatuhTempo = tanggalJatuhTempo;
        }

        public String getNamaTagihan() {
            return namaTagihan;
        }

        public double getJumlah() {
            return jumlah;
        }

        public String getTanggalJatuhTempo() {
            return tanggalJatuhTempo;
        }
    }

    // Node class representing each recurring transaction
    private static class Node implements Serializable {
        TransaksiRutin data;
        Node next;

        Node(TransaksiRutin data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node tail;  // tail node to facilitate circular linked list
    private int size;
    private String fileName;

    public TransaksiRutinList() {
        this.tail = null;
        this.size = 0;
        this.fileName = "recurring_transactions.dat";
        loadTransactions();
    }

    // New constructor with username for user-specific file
    public TransaksiRutinList(String username) {
        this.tail = null;
        this.size = 0;
        this.fileName = "recurring_transactions_" + username + ".dat";
        loadTransactions();
    }

    // Clear all nodes
    private void clear() {
        tail = null;
        size = 0;
    }

    // Add a new recurring transaction to the circular linked list
    public void add(String namaTagihan, double jumlah, String tanggalJatuhTempo) {
        TransaksiRutin transaksi = new TransaksiRutin(namaTagihan, jumlah, tanggalJatuhTempo);
        Node newNode = new Node(transaksi);
        if (tail == null) {
            tail = newNode;
            tail.next = tail; // circular link to itself
        } else {
            newNode.next = tail.next;
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    // Get the size of the list
    public int size() {
        return size;
    }

    // Iterate through the list once, applying the given action to each TransaksiRutin
    public void iterateOnce(Consumer<TransaksiRutin> action) {
        if (tail == null) return;
        Node current = tail.next;
        do {
            action.accept(current.data);
            current = current.next;
        } while (current != tail.next);
    }

    // Save the recurring transactions to file
    public void saveTransactions() {
        List<TransaksiRutin> list = new ArrayList<>();
        iterateOnce(list::add);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(list);
            System.out.println("Transaksi rutin berhasil disimpan.");
        } catch (IOException e) {
            System.out.println("Gagal menyimpan transaksi rutin: " + e.getMessage());
        }
    }

    // Load recurring transactions from file
    @SuppressWarnings("unchecked")
    public void loadTransactions() {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File transaksi rutin tidak ditemukan, memulai dengan list kosong.");
            return; // No file to load
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            List<TransaksiRutin> list = (List<TransaksiRutin>) ois.readObject();
            clear();
            for (TransaksiRutin t : list) {
                add(t.getNamaTagihan(), t.getJumlah(), t.getTanggalJatuhTempo());
            }
            System.out.println("Transaksi rutin berhasil dimuat, jumlah: " + size);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Gagal memuat transaksi rutin: " + e.getMessage());
        }
    }

    // Get list of reminders for today
    public List<TransaksiRutin> getRemindersForToday() {
        List<TransaksiRutin> reminders = new ArrayList<>();
        if (tail == null) return reminders;

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Node current = tail.next;
        do {
            try {
                LocalDate dueDate = LocalDate.parse(current.data.getTanggalJatuhTempo(), formatter);
                if (dueDate.getDayOfMonth() == now.getDayOfMonth()) {
                    reminders.add(current.data);
                }
            } catch (Exception e) {
                System.out.println("Format tanggal tidak valid untuk transaksi rutin: " + current.data.getNamaTagihan());
            }
            current = current.next;
        } while (current != tail.next);

        return reminders;
    }
}

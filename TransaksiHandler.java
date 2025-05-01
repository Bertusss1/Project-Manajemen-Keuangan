import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.time.*;
import java.time.format.*;

// Class ini bertindak sebagai transaksi individual sekaligus manajer transaksi
public class TransaksiHandler implements LaporanKeuangan, Serializable {
    // Atribut untuk data transaksi individu
    private String jenis;        // Pemasukan / Pengeluaran
    private double jumlah;       // Nominal transaksi
    private String tanggal;      // Format: dd/MM/yyyy
    private String kategori;     // Misal: Makanan, Transportasi
    private String keterangan;   // Opsional, penjelasan tambahan

    // Atribut untuk manajemen list transaksi
    private List<TransaksiHandler> transactions; // Daftar semua transaksi
    private String fileName; // Nama file untuk simpan transaksi
    private int maxTransactions = 1000; // Batas maksimum transaksi yang disimpan

    // Konstruktor untuk membuat satu transaksi
    public TransaksiHandler(String jenis, double jumlah, String tanggal, String kategori, String keterangan) {
        validateTransaction(jenis, jumlah, tanggal, kategori); 
        this.jenis = jenis;
        this.jumlah = jumlah;
        this.tanggal = tanggal;
        this.kategori = kategori;
        this.keterangan = keterangan != null ? keterangan : "";
    }

    // Konstruktor untuk mode manajemen (tanpa data transaksi), dengan user-specific file
    public TransaksiHandler(String username) {
        this.fileName = "transactions_" + username + ".dat";
        this.transactions = loadTransactions(); 
    }

    // Validasi input transaksi
    private void validateTransaction(String jenis, double jumlah, String tanggal, String kategori) {
        if (!jenis.equalsIgnoreCase("Pemasukan") && !jenis.equalsIgnoreCase("Pengeluaran")) {
            throw new IllegalArgumentException("Jenis transaksi harus 'Pemasukan' atau 'Pengeluaran'");
        }
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah transaksi harus positif");
        }
        if (tanggal == null || tanggal.isEmpty()) {
            throw new IllegalArgumentException("Tanggal tidak boleh kosong");
        }
        if (kategori == null || kategori.isEmpty()) {
            throw new IllegalArgumentException("Kategori tidak boleh kosong");
        }
    }

    // Getter data transaksi
    public String getJenis() { return jenis; }
    public double getJumlah() { return jumlah; }
    public String getTanggal() { return tanggal; }
    public String getKategori() { return kategori; }
    public String getKeterangan() { return keterangan; }

    // Menampilkan detail transaksi individual
    public void tampilkanTransaksi() {
        System.out.println("Jenis: " + jenis);
        System.out.println("Jumlah: " + jumlah);
        System.out.println("Tanggal: " + tanggal);
        System.out.println("Kategori: " + kategori);
        System.out.println("Keterangan: " + keterangan);
    }

    // Menyimpan semua transaksi ke file
    public void saveTransactions() {
        if (transactions.size() > maxTransactions) {
            System.out.println("Warning: Transaction limit exceeded");
            return;
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(transactions);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan transaksi: " + e.getMessage());
        }
    }

    // Memuat transaksi dari file (jika ada)
    public List<TransaksiHandler> loadTransactions() {
        File file = new File(fileName);
        if (!file.exists()) {
            return new ArrayList<>(); // Kalau file belum ada, kembalikan list kosong
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (List<TransaksiHandler>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Gagal memuat transaksi: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Filter transaksi berdasarkan jenis, kategori, dan rentang tanggal
    public List<TransaksiHandler> filterTransactions(String jenis, String kategori, LocalDate startDate, LocalDate endDate) {
        return transactions.stream()
            .filter(t -> jenis == null || t.getJenis().equalsIgnoreCase(jenis))
            .filter(t -> kategori == null || t.getKategori().equalsIgnoreCase(kategori))
            .filter(t -> {
                if (startDate == null && endDate == null) return true;
                try {
                    LocalDate transDate = LocalDate.parse(t.getTanggal(), 
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    return (startDate == null || !transDate.isBefore(startDate)) &&
                           (endDate == null || !transDate.isAfter(endDate));
                } catch (Exception e) {
                    return false; // Jika format tanggal error, transaksi ini langsung ditingalkan
                }
            })
            .collect(Collectors.toList());
    }

    // Mengembalikan semua transaksi yang ada
    public List<TransaksiHandler> getTransactions() {
        return transactions;
    }

    // Menambahkan transaksi baru ke dalam list dengan validasi
    public void addTransaction(TransaksiHandler t) {
        try {
            // Validasi limit transaksi
            if (t.getJumlah() > AkunBiasa.TRANSACTION_LIMIT) {
                throw new IllegalArgumentException(
                    String.format("Transaksi melebihi batas maksimal Rp%,.2f", 
                    AkunBiasa.TRANSACTION_LIMIT)
                );
            }
            
            // Validasi jumlah transaksi harian
            long countToday = transactions.stream()
                .filter(tr -> tr.getTanggal().equals(t.getTanggal()))
                .count();
                
            if (countToday >= AkunBiasa.MAX_TRANSACTIONS_PER_DAY) {
                throw new IllegalStateException(
                    String.format("Maksimal %d transaksi per hari tercapai", 
                    AkunBiasa.MAX_TRANSACTIONS_PER_DAY)
                );
            }
            
            transactions.add(t);
        } catch (Exception e) {
            throw e; // Re-throw the exception with original stack trace
        }
    }

    // Generate ringkasan laporan transaksi yang lebih lengkap
    @Override
    public void generateLaporan() {
        System.out.println("\n=== Laporan Keuangan Lengkap ===");
        System.out.println("Total Transaksi: " + transactions.size());

        double totalPemasukan = transactions.stream()
            .filter(t -> t.getJenis().equalsIgnoreCase("Pemasukan"))
            .mapToDouble(TransaksiHandler::getJumlah)
            .sum();

        double totalPengeluaran = transactions.stream()
            .filter(t -> t.getJenis().equalsIgnoreCase("Pengeluaran"))
            .mapToDouble(TransaksiHandler::getJumlah)
            .sum();

        double saldoBersih = totalPemasukan - totalPengeluaran;

        System.out.printf("Total Pemasukan: Rp %,.2f%n", totalPemasukan);
        System.out.printf("Total Pengeluaran: Rp %,.2f%n", totalPengeluaran);
        System.out.printf("Saldo Bersih: Rp %,.2f%n", saldoBersih);

        double rataRataTransaksi = transactions.isEmpty() ? 0 :
            transactions.stream().mapToDouble(TransaksiHandler::getJumlah).average().orElse(0);

        System.out.printf("Rata-rata Nominal Transaksi: Rp %,.2f%n", rataRataTransaksi);

        System.out.println("===============================");
    }
}

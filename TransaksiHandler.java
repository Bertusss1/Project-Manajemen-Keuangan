
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.time.*;
import java.time.format.*;

public class TransaksiHandler implements LaporanKeuangan, Serializable {
    private String jenis;
    private double jumlah;
    private String tanggal;
    private String kategori;
    private String keterangan;

    private DoubleLinkedList<TransaksiHandler> transactions;
    private String fileName;
    private int maxTransactions = 1000;

    public TransaksiHandler(String jenis, double jumlah, String tanggal, String kategori, String keterangan) {
        validateTransaction(jenis, jumlah, tanggal, kategori);
        this.jenis = jenis;
        this.jumlah = jumlah;
        this.tanggal = tanggal;
        this.kategori = kategori;
        this.keterangan = keterangan != null ? keterangan : "";
    }

    public TransaksiHandler(String username) {
        this.fileName = "transactions_" + username + ".dat";
        this.transactions = new DoubleLinkedList<>();
        List<TransaksiHandler> loaded = loadTransactions();
        for (TransaksiHandler t : loaded) {
            this.transactions.add(t);
        }
    }

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

    public String getJenis() { return jenis; }
    public double getJumlah() { return jumlah; }
    public String getTanggal() { return tanggal; }
    public String getKategori() { return kategori; }
    public String getKeterangan() { return keterangan; }

    public void tampilkanTransaksi() {
        System.out.println("Jenis: " + jenis);
        System.out.println("Jumlah: " + jumlah);
        System.out.println("Tanggal: " + tanggal);
        System.out.println("Kategori: " + kategori);
        System.out.println("Keterangan: " + keterangan);
    }

    public void saveTransactions() {
        if (transactions.size() > maxTransactions) {
            System.out.println("Warning: Transaction limit exceeded");
            return;
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(transactions.toArrayList());
        } catch (IOException e) {
            System.out.println("Gagal menyimpan transaksi: " + e.getMessage());
        }
    }

    public List<TransaksiHandler> loadTransactions() {
        File file = new File(fileName);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (List<TransaksiHandler>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Gagal memuat transaksi: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<TransaksiHandler> filterTransactions(String jenis, String kategori, LocalDate startDate, LocalDate endDate) {
        return transactions.toArrayList().stream()
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
                    return false;
                }
            })
            .collect(Collectors.toList());
    }

    public List<TransaksiHandler> getTransactions() {
        return transactions.toArrayList();
    }

    public void addTransaction(TransaksiHandler t) {
        try {
            transactions.add(t);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void generateLaporan() {
        System.out.println("\n=== Laporan Keuangan Lengkap ===");
        System.out.println("Total Transaksi: " + transactions.size());

        double totalPemasukan = transactions.toArrayList().stream()
            .filter(t -> t.getJenis().equalsIgnoreCase("Pemasukan"))
            .mapToDouble(TransaksiHandler::getJumlah)
            .sum();

        double totalPengeluaran = transactions.toArrayList().stream()
            .filter(t -> t.getJenis().equalsIgnoreCase("Pengeluaran"))
            .mapToDouble(TransaksiHandler::getJumlah)
            .sum();

        double saldoBersih = totalPemasukan - totalPengeluaran;

        System.out.printf("Total Pemasukan: Rp %,.2f%n", totalPemasukan);
        System.out.printf("Total Pengeluaran: Rp %,.2f%n", totalPengeluaran);
        System.out.printf("Saldo Bersih: Rp %,.2f%n", saldoBersih);

        double rataRataTransaksi = transactions.size() == 0 ? 0 :
            transactions.toArrayList().stream().mapToDouble(TransaksiHandler::getJumlah).average().orElse(0);

        System.out.printf("Rata-rata Nominal Transaksi: Rp %,.2f%n", rataRataTransaksi);

        System.out.println("===============================");
    }
}

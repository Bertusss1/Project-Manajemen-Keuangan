import java.time.LocalDate;
import java.text.DecimalFormat;

// Kelas turunan dari Pengguna akun premium dengan benefit lebih
public class AkunPremium extends Pengguna {
    // Batas maksimum nominal transaksi per transaksi untuk akun premium
    public static final double TRANSACTION_LIMIT = 20000000.0; // contoh limit lebih tinggi

    // Batas maksimum jumlah transaksi per hari untuk akun premium
    public static final int MAX_TRANSACTIONS_PER_DAY = 20; // contoh limit lebih tinggi

    private int transactionsToday; // Jumlah transaksi yang dilakukan hari ini
    private LocalDate lastTransactionDate; // Tanggal terakhir transaksi

    public AkunPremium(String nama, String email, String password, double saldo,
                   String nomorTelepon, String alamat) {
        super(nama, email, password, saldo, nomorTelepon, alamat);
        this.transactionsToday = 0;
        this.lastTransactionDate = LocalDate.now(); // Set tanggal awal saat akun dibuat
    }

    @Override
    public String getUserType() {
        return "Premium";
    }

    @Override
    public void tampilkanSaldo() {
        DecimalFormat formatter = new DecimalFormat("###,###.##");
        System.out.println("\n=== SALDO AKUN ===");
        System.out.println("Username: " + getEmail().split("@")[0]);
        System.out.println("Tipe Akun: Premium");
        System.out.println("Saldo: Rp" + formatter.format(getSaldo())); 
        System.out.println("==================");
    }

    // Mengecek apakah transaksi masih diperbolehkan hari ini
    public boolean canMakeTransaction(double amount) {
        // Reset transaksi harian jika sudah berganti hari
        if (LocalDate.now().isAfter(lastTransactionDate)) {
            transactionsToday = 0;
            lastTransactionDate = LocalDate.now();
        }

        // Transaksi hanya bisa dilakukan jika belum mencapai batas nominal dan batas harian
        return amount <= TRANSACTION_LIMIT && 
               transactionsToday < MAX_TRANSACTIONS_PER_DAY;
    }

    // Mencatat transaksi baru dan mengurangi saldo
    public void recordTransaction(double amount) {
        // Reset transaksi jika hari berganti
        if (LocalDate.now().isAfter(lastTransactionDate)) {
            transactionsToday = 0;
            lastTransactionDate = LocalDate.now();
        }
        transactionsToday++; // Tambah jumlah transaksi hari ini
        super.tambahTransaksi(); // Catat transaksi di sistem (dari class induk)
        super.setSaldo(super.getSaldo() - amount); // Kurangi saldo
    }

    // Menampilkan informasi lengkap akun pengguna
    public void displayAccountInfo() {
        super.tampilkanProfil(); // Tampilkan data diri dari class induk
        System.out.println("Tipe Akun: Premium");
        System.out.println("Batas Transaksi: " + TRANSACTION_LIMIT);
        System.out.println("Transaksi Hari Ini: " + transactionsToday + "/" + MAX_TRANSACTIONS_PER_DAY);
        this.tampilkanSaldo(); // Tampilkan saldo
    }
}

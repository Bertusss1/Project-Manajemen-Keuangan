import java.io.*;

// Kelas Tabungan mengimplementasikan antarmuka LaporanKeuangan
public class Tabungan implements LaporanKeuangan {
    private double saldo;
    private String pemilik;
    private String jenisTabungan;
    private double targetTabungan;
    private static final String SALDO_FILE = "saldo.dat"; // Nama file untuk menyimpan saldo

    // Konstruktor Tabungan
    public Tabungan(double saldoAwal) {
        this.saldo = saldoAwal;
        this.pemilik = "User1"; // Default pemilik
        this.jenisTabungan = "Reguler"; // Default jenis
        this.targetTabungan = 0;
        loadFromFile(); // Coba load saldo dari file saat objek dibuat
    }

    // Setter dan Getter untuk pemilik
    public void setPemilik(String pemilik) {
        this.pemilik = pemilik;
    }

    public String getPemilik() {
        return pemilik;
    }

    // Setter dan Getter untuk jenis tabungan
    public void setJenisTabungan(String jenisTabungan) {
        this.jenisTabungan = jenisTabungan;
    }

    public String getJenisTabungan() {
        return jenisTabungan;
    }

    // Setter dan Getter untuk target tabungan
    public void setTargetTabungan(double targetTabungan) {
        this.targetTabungan = targetTabungan;
    }

    public double getTargetTabungan() {
        return targetTabungan;
    }

    // Override dari interface LaporanKeuangan
    @Override
    public void generateLaporan() {
        System.out.println("\n=== Laporan Tabungan ===");
        System.out.println("Pemilik: " + pemilik);
        System.out.println("Jenis Tabungan: " + jenisTabungan);
        System.out.printf("Saldo Saat Ini: Rp%,.2f\n", saldo);
        System.out.println("=======================");
    }

    // Getter untuk saldo
    public double getSaldo() {
        return saldo;
    }

    // Method untuk menyetor uang ke saldo
    public void setor(double jumlah) {
        if (jumlah > 0) {
            saldo += jumlah;
            saveToFile(); // Simpan saldo ke file setelah update
        }
    }

    // Method untuk menarik uang dari saldo
    public boolean tarik(double jumlah) {
        if (jumlah > 0 && saldo >= jumlah) {
            saldo -= jumlah;
            saveToFile(); // Simpan saldo setelah penarikan
            return true;
        }
        return false;
    }

    // Menyimpan saldo saat ini ke file eksternal
    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SALDO_FILE))) {
            oos.writeDouble(saldo); // Simpan saldo sebagai double
        } catch (IOException e) {
            System.out.println("Gagal menyimpan saldo: " + e.getMessage());
        }
    }

    // Memuat saldo dari file saat program dijalankan
    public void loadFromFile() {
        File file = new File(SALDO_FILE);
        if (!file.exists()) {
            return; // Jika file tidak ada, lewati
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SALDO_FILE))) {
            this.saldo = ois.readDouble(); // Ambil nilai saldo
        } catch (IOException e) {
            System.out.println("Gagal memuat saldo: " + e.getMessage());
        }
    }
}

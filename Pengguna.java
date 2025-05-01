import java.time.LocalDate;
import java.text.DecimalFormat;

// Kelas abstrak untuk mewakili entitas pengguna umum 
abstract class Pengguna {
    // Atribut utama untuk identitas dan akses
    protected String nama, email, password;
    protected double saldo;

    // Atribut tambahan untuk informasi lebih rinci
    protected String nomorTelepon;
    protected LocalDate tanggalDaftar;
    protected boolean statusAktif;
    protected int jumlahTransaksi;
    protected String alamat;

    // Konstruktor untuk inisialisasi data pengguna
    public Pengguna(String nama, String email, String password, double saldo, String nomorTelepon, String alamat) {
        this.nama = nama;
        this.email = email;
        this.password = password;
        this.saldo = saldo;
        this.nomorTelepon = nomorTelepon;
        this.tanggalDaftar = LocalDate.now(); // otomatis mencatat tanggal pendaftaran
        this.statusAktif = true; // default aktif saat daftar
        this.jumlahTransaksi = 0; // belum ada transaksi
        this.alamat = alamat;
    }

    // Method abstrak untuk memastikan subclass menjelaskan jenis pengguna
    public abstract String getUserType();

    // Getter saldo
    public double getSaldo() {
        return saldo;
    }

    // Setter saldo
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    // Method yang ditujukan untuk di-override (misal untuk menampilkan saldo dengan format berbeda)
    public void tampilkanSaldo() {
        // Kosong, akan diisi oleh subclass
    }

    // Getter nomor telepon
    public String getNomorTelepon() {
        return nomorTelepon;
    }

    // Getter tanggal daftar
    public LocalDate getTanggalDaftar() {
        return tanggalDaftar;
    }

    // Cek apakah akun masih aktif
    public boolean isAktif() {
        return statusAktif;
    }

    // Aktifkan akun
    public void aktifkanAkun() {
        this.statusAktif = true;
    }

    // Nonaktifkan akun
    public void nonaktifkanAkun() {
        this.statusAktif = false;
    }

    // Tambahkan jumlah transaksi
    public void tambahTransaksi() {
        this.jumlahTransaksi++;
    }

    // Getter jumlah transaksi
    public int getJumlahTransaksi() {
        return jumlahTransaksi;
    }

    // Getter alamat
    public String getAlamat() {
        return alamat;
    }

    // Getter email (bisa dipakai untuk login, validasi, dll)
    public String getEmail() {
        return email;
    }

    // Getter password
    public String getPassword() {
        return password;
    }

    // Setter password (misalnya saat ganti password)
    public void setPassword(String password) {
        this.password = password;
    }

    // Menampilkan semua informasi profil pengguna
    public void tampilkanProfil() {
        System.out.println("\n=== Profil Pengguna ===");
        System.out.println("Nama: " + nama);
        System.out.println("Email: " + email);
        System.out.println("Nomor Telepon: " + nomorTelepon);
        System.out.println("Alamat: " + alamat);
        System.out.println("Tanggal Daftar: " + tanggalDaftar);
        System.out.println("Status: " + (statusAktif ? "Aktif" : "Nonaktif"));
        System.out.println("Jumlah Transaksi: " + jumlahTransaksi);
        this.tampilkanSaldo(); // Akan dipanggil dari subclass (override)
    }
}

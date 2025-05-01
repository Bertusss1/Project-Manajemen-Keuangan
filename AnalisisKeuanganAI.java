import java.util.List;

public class AnalisisKeuanganAI {
    // Menyimpan daftar transaksi yang akan dianalisis
    private final List<TransaksiHandler> transaksiList;

    // Saldo pengguna saat ini
    private final double saldo;

    // Periode analisis, default: "Bulanan"
    private String periodeAnalisis;

    // Target tabungan yang ingin dicapai pengguna
    private double targetTabungan;

    // Status apakah notifikasi aktif atau tidak
    private boolean notifikasiAktif;

    // Konstruktor: menerima daftar transaksi dan saldo sebagai input awal
    public AnalisisKeuanganAI(List<TransaksiHandler> transaksiList, double saldo) {
        this.transaksiList = transaksiList;
        this.saldo = saldo;
        this.periodeAnalisis = "Bulanan";
        this.targetTabungan = 0;
        this.notifikasiAktif = true;
    }

    // Setter & getter untuk periode analisis
    public void setPeriodeAnalisis(String periodeAnalisis) {
        this.periodeAnalisis = periodeAnalisis;
    }

    public String getPeriodeAnalisis() {
        return periodeAnalisis;
    }

    // Setter & getter untuk target tabungan
    public void setTargetTabungan(double targetTabungan) {
        this.targetTabungan = targetTabungan;
    }

    public double getTargetTabungan() {
        return targetTabungan;
    }

    // Mengaktifkan dan menonaktifkan notifikasi
    public void aktifkanNotifikasi() {
        this.notifikasiAktif = true;
    }

    public void nonaktifkanNotifikasi() {
        this.notifikasiAktif = false;
    }

    public boolean isNotifikasiAktif() {
        return notifikasiAktif;
    }

    // Fungsi utama untuk melakukan analisis keuangan
    public void lakukanAnalisis() {
        System.out.println("\n=== ANALISIS KEUANGAN AI ===");
        
        // Hitung total pemasukan dan pengeluaran
        double totalPemasukan = hitungTotalPemasukan();
        double totalPengeluaran = hitungTotalPengeluaran();
        
        // Menampilkan hasil dasar analisis
        System.out.printf("Total Pemasukan: Rp%,.2f%n", totalPemasukan);
        System.out.printf("Total Pengeluaran: Rp%,.2f%n", totalPengeluaran);
        System.out.printf("Saldo Saat Ini: Rp%,.2f%n", saldo);
        
        // Hitung rasio pengeluaran terhadap pemasukan
        double rasioPengeluaran = totalPemasukan > 0 ? 
            (totalPengeluaran / totalPemasukan) * 100 : 0;
        System.out.printf("Rasio Pengeluaran: %.1f%%%n", rasioPengeluaran);
        
        // Analisis lebih lanjut berdasarkan kategori
        analisisKategoriPengeluaran();
        
        // Berikan saran dan insight keuangan
        berikanRekomendasi(totalPemasukan, totalPengeluaran);
    }

    // Menghitung total pemasukan dari daftar transaksi
    private double hitungTotalPemasukan() {
        return transaksiList.stream()
            .filter(t -> t.getJenis().equalsIgnoreCase("Pemasukan"))
            .mapToDouble(TransaksiHandler::getJumlah)
            .sum();
    }

    // Menghitung total pengeluaran dari daftar transaksi
    private double hitungTotalPengeluaran() {
        return transaksiList.stream()
            .filter(t -> t.getJenis().equalsIgnoreCase("Pengeluaran"))
            .mapToDouble(TransaksiHandler::getJumlah)
            .sum();
    }

    // Mengelompokkan pengeluaran berdasarkan kategori dan tampilkan total per kategori
    private void analisisKategoriPengeluaran() {
        System.out.println("\nAnalisis Kategori Pengeluaran:");
        transaksiList.stream()
            .filter(t -> t.getJenis().equalsIgnoreCase("Pengeluaran"))
            .collect(java.util.stream.Collectors.groupingBy(
                TransaksiHandler::getKategori,
                java.util.stream.Collectors.summingDouble(TransaksiHandler::getJumlah)))
            .forEach((kategori, total) -> 
                System.out.printf("- %s: Rp%,.2f%n", kategori, total));
    }

    // Memberikan saran atau rekomendasi berdasarkan hasil analisis
    private void berikanRekomendasi(double pemasukan, double pengeluaran) {
        System.out.println("\nRekomendasi:");
        
        if (pengeluaran > pemasukan) {
            // Warning jika pengeluaran lebih besar dari pemasukan
            System.out.println("PERINGATAN: Pengeluaran melebihi pemasukan!");
            System.out.println("Disarankan untuk mengurangi pengeluaran atau mencari sumber pemasukan tambahan.");
        } else {
            // Hitung tabungan dan rasio tabungan dari pemasukan
            double tabungan = pemasukan - pengeluaran;
            double rasioTabungan = (tabungan / pemasukan) * 100;
            
            System.out.printf("Anda menabung Rp%,.2f (%.1f%% dari pemasukan)%n", 
                tabungan, rasioTabungan);
            
            if (rasioTabungan < 20) {
                System.out.println("Anda bisa meningkatkan rasio tabungan dengan mengurangi pengeluaran tidak penting.");
            } else {
                System.out.println("Rasio tabungan Anda baik. Pertahankan!");
            }
        }
        
        // Temukan kategori pengeluaran terbesar
        transaksiList.stream()
            .filter(t -> t.getJenis().equalsIgnoreCase("Pengeluaran"))
            .collect(java.util.stream.Collectors.groupingBy(
                TransaksiHandler::getKategori,
                java.util.stream.Collectors.summingDouble(TransaksiHandler::getJumlah)))
            .entrySet()
            .stream()
            .max(java.util.Map.Entry.comparingByValue())
            .ifPresent(entry -> 
                System.out.printf("Kategori pengeluaran terbesar: %s (Rp%,.2f)%n", 
                    entry.getKey(), entry.getValue()));
    }
}

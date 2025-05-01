import java.util.*;
import java.time.*;
import java.time.format.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login loginSystem = new Login();

        // === LOGIN SYSTEM ===
        Pengguna loggedInUser = null;
        while (loggedInUser == null) {
            loggedInUser = loginSystem.login();
            if (loggedInUser == null) {
                System.out.println("Login gagal! Coba lagi.");
            }
        }

        // Inisialisasi objek untuk transaksi dan tabungan
        TransaksiHandler transaksiHandler = new TransaksiHandler(); // management mode, loads saved transactions
        TransaksiList transaksiList = new TransaksiList();
        // Load saved transactions into transaksiList
        for (TransaksiHandler t : transaksiHandler.getTransactions()) {
            transaksiList.add(t);
        }
        TransaksiRutinList transaksiRutinList = new TransaksiRutinList();
        Tabungan tabungan = new Tabungan(0); 

        boolean running = true;
        while (running) {
            // === MENU UTAMA ===
            System.out.println("\n=== Menu ===");
            System.out.println("1. Catat Transaksi");
            System.out.println("2. Filter Transaksi");
            System.out.println("3. Kelola Tabungan");
            System.out.println("4. Analisis Keuangan AI");
            System.out.println("5. Generate Laporan");
            System.out.println("6. Kelola Transaksi Rutin");
            System.out.println("0. Keluar");
            System.out.print("Pilih menu (0-6): ");

            int pilihan = scanner.nextInt();
            scanner.nextLine(); 

            switch (pilihan) {
                case 1: // === CATAT TRANSAKSI ===
                    System.out.print("Jenis (Pemasukan/Pengeluaran): ");
                    String jenisInput = scanner.nextLine();

                    System.out.print("Jumlah: ");
                    double jumlahInput = scanner.nextDouble();
                    scanner.nextLine();

                    System.out.print("Tanggal (dd/MM/yyyy): ");
                    String tanggalInput = scanner.nextLine();

                    // Menentukan kategori berdasarkan jenis transaksi
                    String[] categoriesInput;
                    if (jenisInput.equalsIgnoreCase("Pemasukan")) {
                        categoriesInput = new String[]{"Gaji", "Bonus", "Investasi", "Hadiah", "Lain-lain"};
                    } else {
                        categoriesInput = new String[]{
                            "Makanan & Minuman", "Transportasi", "Belanja", "Hiburan",
                            "Kesehatan", "Pendidikan", "Tagihan (Listrik/Air/Internet)",
                            "Investasi", "Lain-lain"
                        };
                    }

                    // Menampilkan pilihan kategori
                    System.out.println("\nPilih Kategori:");
                    for (int i = 0; i < categoriesInput.length; i++) {
                        System.out.println((i + 1) + ". " + categoriesInput[i]);
                    }

                    // Validasi input kategori
                    int categoryChoiceInput;
                    while (true) {
                        try {
                            categoryChoiceInput = scanner.nextInt();
                            scanner.nextLine();
                            if (categoryChoiceInput > 0 && categoryChoiceInput <= categoriesInput.length) {
                                break;
                            }
                            System.out.print("Pilihan tidak valid. Silakan pilih nomor kategori: ");
                        } catch (InputMismatchException e) {
                            System.out.print("Input harus angka. Silakan pilih nomor kategori: ");
                            scanner.nextLine();
                        }
                    }

                    String kategoriInput = categoriesInput[categoryChoiceInput - 1];

                    System.out.print("Keterangan: ");
                    String keteranganInput = scanner.nextLine();

                    // Simpan transaksi
                    TransaksiHandler t = new TransaksiHandler(jenisInput, jumlahInput, tanggalInput, kategoriInput, keteranganInput);
                    transaksiList.add(t);
                    transaksiHandler.addTransaction(t); // add to persistent list

                    // Update saldo tabungan
                    if (jenisInput.equalsIgnoreCase("Pengeluaran")) {
                        if (!tabungan.tarik(jumlahInput)) {
                            System.out.println("Peringatan: Saldo tidak mencukupi!");
                        }
                    } else {
                        tabungan.setor(jumlahInput);
                    }

                    System.out.println("Transaksi berhasil dicatat!");
                    break;

                case 2: // === FILTER TRANSAKSI ===
                    System.out.println("\nFilter Transaksi:");
                    System.out.println("1. Filter by Jenis");
                    System.out.println("2. Filter by Kategori");
                    System.out.println("3. Filter by Tanggal");
                    System.out.print("Pilihan: ");
                    int filterChoice = scanner.nextInt();
                    scanner.nextLine();

                    String jenisFilter = null;
                    String kategoriFilter = null;
                    LocalDate startDate = null;
                    LocalDate endDate = null;

                    // Filter sesuai pilihan
                    switch (filterChoice) {
                        case 1:
                            System.out.print("Masukkan jenis (Pemasukan/Pengeluaran): ");
                            jenisFilter = scanner.nextLine();
                            break;
                        case 2:
                            System.out.print("Filter berdasarkan jenis (Pemasukan/Pengeluaran): ");
                            String filterJenis = scanner.nextLine();

                            String[] categoriesFilter;
                            if (filterJenis.equalsIgnoreCase("Pemasukan")) {
                                categoriesFilter = new String[]{"Gaji", "Bonus", "Investasi", "Hadiah", "Lain-lain"};
                            } else {
                                categoriesFilter = new String[]{
                                    "Makanan & Minuman", "Transportasi", "Belanja", "Hiburan",
                                    "Kesehatan", "Pendidikan", "Tagihan (Listrik/Air/Internet)",
                                    "Investasi", "Lain-lain"
                                };
                            }

                            // Pilih kategori
                            System.out.println("\nPilih Kategori:");
                            for (int i = 0; i < categoriesFilter.length; i++) {
                                System.out.println((i + 1) + ". " + categoriesFilter[i]);
                            }

                            int categoryChoiceFilter;
                            while (true) {
                                try {
                                    categoryChoiceFilter = scanner.nextInt();
                                    scanner.nextLine();
                                    if (categoryChoiceFilter > 0 && categoryChoiceFilter <= categoriesFilter.length) {
                                        kategoriFilter = categoriesFilter[categoryChoiceFilter - 1];
                                        break;
                                    }
                                    System.out.print("Pilihan tidak valid. Silakan pilih ulang: ");
                                } catch (InputMismatchException e) {
                                    System.out.print("Input harus angka. Silakan pilih ulang: ");
                                    scanner.nextLine();
                                }
                            }
                            break;
                        case 3:
                            // Filter berdasarkan rentang tanggal
                            System.out.print("Tanggal awal (dd/MM/yyyy): ");
                            startDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            System.out.print("Tanggal akhir (dd/MM/yyyy): ");
                            endDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            break;
                    }

                    // Use transaksiHandler's filterTransactions method
                    List<TransaksiHandler> filtered = transaksiHandler.filterTransactions(
                        jenisFilter, kategoriFilter, startDate, endDate
                    );

                    System.out.println("\nHasil Filter:");
                    System.out.println("-----------------------------------------------------------------------------");
                    System.out.println("| No | Jenis      | Jumlah     | Tanggal    | Kategori  | Keterangan       |");
                    System.out.println("-----------------------------------------------------------------------------");

                    int counter = 1;
                    for (TransaksiHandler tr : filtered) {
                        System.out.printf("| %-2d | %-10s | %10.2f | %-10s | %-10s | %-15s |\n",
                            counter++, tr.getJenis(), tr.getJumlah(), tr.getTanggal(), tr.getKategori(), tr.getKeterangan());
                    }

                    System.out.println("-----------------------------------------------------------------------------");
                    break;

                case 3: // === KELOLA TABUNGAN ===
                    boolean kembali = false;
                    while (!kembali) {
                        System.out.println("\n=== KELOLA TABUNGAN ===");
                        System.out.println("1. Setor Dana");
                        System.out.println("2. Tarik Dana");
                        System.out.println("3. Cek Saldo");
                        System.out.println("0. Kembali ke Menu Utama");
                        System.out.print("Pilihan: ");

                        try {
                            int tabChoice = scanner.nextInt();
                            scanner.nextLine();

                            switch (tabChoice) {
                                case 1:
                                    System.out.print("Masukkan jumlah setoran: Rp ");
                                    double setoran = scanner.nextDouble();
                                    scanner.nextLine();
                                    if (setoran > 0) {
                                        tabungan.setor(setoran);
                                        System.out.printf("Berhasil menyetor Rp %,.2f%n", setoran);
                                        System.out.printf("Saldo saat ini: Rp %,.2f%n", tabungan.getSaldo());
                                    } else {
                                        System.out.println("Jumlah setoran harus lebih dari 0");
                                    }
                                    break;
                                case 2:
                                    System.out.print("Masukkan jumlah penarikan: Rp ");
                                    double penarikan = scanner.nextDouble();
                                    scanner.nextLine();
                                    if (penarikan > 0) {
                                        if (tabungan.tarik(penarikan)) {
                                            System.out.printf("Berhasil menarik Rp %,.2f%n", penarikan);
                                            System.out.printf("Saldo saat ini: Rp %,.2f%n", tabungan.getSaldo());
                                        } else {
                                            System.out.println("Saldo tidak mencukupi");
                                        }
                                    } else {
                                        System.out.println("Jumlah penarikan harus lebih dari 0");
                                    }
                                    break;
                                case 3:
                                    System.out.println("\n=== INFORMASI SALDO ===");
                                    System.out.printf("Saldo saat ini: Rp %,.2f%n", tabungan.getSaldo());
                                    break;
                                case 0:
                                    kembali = true;
                                    break;
                                default:
                                    System.out.println("Pilihan tidak valid");
                                    break;
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Input tidak valid. Harap masukkan angka.");
                            scanner.nextLine();
                        }
                    }
                    break;

                case 4: // === ANALISIS KEUANGAN AI ===
                    // Use AnalisisKeuanganAI class for analysis
                    AnalisisKeuanganAI analisisAI = new AnalisisKeuanganAI(transaksiList.toList(), tabungan.getSaldo());
                    analisisAI.lakukanAnalisis();
                    break;

                case 5: // === GENERATE LAPORAN ===
                    System.out.println("Fitur generate laporan belum didukung dengan TransaksiList.");
                    break;

                case 6: // === KELOLA TRANSAKSI RUTIN ===
                    boolean kembaliRutin = false;
                    while (!kembaliRutin) {
                        System.out.println("\n=== Kelola Transaksi Rutin ===");
                        System.out.println("1. Tambah Transaksi Rutin");
                        System.out.println("2. Lihat Semua Transaksi Rutin");
                        System.out.println("3. Cek Reminder Bulanan");
                        System.out.println("0. Kembali ke Menu Utama");
                        System.out.print("Pilih menu (0-3): ");

                        int pilihanRutin = scanner.nextInt();
                        scanner.nextLine();

                        switch (pilihanRutin) {
                            case 1:
                                System.out.print("Nama Tagihan: ");
                                String namaTagihan = scanner.nextLine();
                                System.out.print("Jumlah: ");
                                double jumlahTagihan = scanner.nextDouble();
                                scanner.nextLine();
                                System.out.print("Tanggal Jatuh Tempo (dd/MM/yyyy): ");
                                String tanggalJatuhTempo = scanner.nextLine();

                                transaksiRutinList.add(namaTagihan, jumlahTagihan, tanggalJatuhTempo);
                                transaksiRutinList.saveTransactions(); // Save after adding
                                System.out.println("Transaksi rutin berhasil ditambahkan!");
                                break;

                            case 2:
                                System.out.println("\nDaftar Transaksi Rutin:");
                                transaksiRutinList.iterateOnce(node -> {
                                    System.out.printf("- %s: Rp %,.2f, Jatuh Tempo: %s%n",
                                        node.getNamaTagihan(), node.getJumlah(), node.getTanggalJatuhTempo());
                                });
                                break;

                            case 3:
                                System.out.println("\nReminder Bulanan Transaksi Rutin:");
                                List<TransaksiRutinList.TransaksiRutin> reminders = transaksiRutinList.getRemindersForToday();
                                if (reminders.isEmpty()) {
                                    System.out.println("Tidak ada reminder untuk hari ini.");
                                } else {
                                    for (TransaksiRutinList.TransaksiRutin reminder : reminders) {
                                        System.out.printf("Ingatkan: %s sebesar Rp %,.2f jatuh tempo hari ini (%s)%n",
                                            reminder.getNamaTagihan(), reminder.getJumlah(), reminder.getTanggalJatuhTempo());
                                    }
                                }
                                break;

                            case 0:
                                kembaliRutin = true;
                                break;

                            default:
                                System.out.println("Pilihan tidak valid");
                                break;
                        }
                    }
                    break;

                case 0: // === KELUAR APLIKASI ===
                    transaksiHandler.saveTransactions();
                    transaksiRutinList.saveTransactions();
                    running = false;
                    System.out.println("Data disimpan. Sampai jumpa!");
                    break;

                default:
                    System.out.println("Pilihan tidak valid");
                    break;
            }
        }
        scanner.close();
    }
}

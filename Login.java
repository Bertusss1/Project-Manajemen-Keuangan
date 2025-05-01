import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Login {
    // Menyimpan daftar pengguna yang terdaftar (key: username, value: objek Pengguna)
    private Map<String, Pengguna> users;

    // Hitungan percobaan login gagal
    private int loginAttempts;

    // Status apakah akun terkunci karena terlalu banyak login gagal
    private boolean accountLocked;

    // Waktu percobaan login terakhir (opsional digunakan untuk log)
    private String lastLoginAttempt;

    // Jumlah reset password yang sudah dilakukan
    private int passwordResetCount;

    public Login() {
        users = new HashMap<>();
        loginAttempts = 0;
        accountLocked = false;
        passwordResetCount = 0;

        // Inisialisasi contoh user biasa dengan saldo awal dan info dasar
        AkunBiasa regular1 = new AkunBiasa("User Biasa", "regular1@email.com", 
            "password1", 1000000, "08123456789", "Jl. Contoh No. 1");
        AkunBiasa regular2 = new AkunBiasa("User Biasa 2", "regular2@email.com", 
            "password2", 2000000, "08234567890", "Jl. Contoh No. 2");

        // Username diambil dari bagian sebelum '@' dari email
        users.put("regular1", regular1);
        users.put("regular2", regular2);
    }

    // Fungsi autentikasi login (cek username dan password)
    public boolean authenticate(String username, String password) {
        return users.containsKey(username) && 
               users.get(username).getPassword().equals(password);
    }

    // Fungsi registrasi user baru (cek duplikasi username berdasarkan email)
    public boolean register(Pengguna user) {
        String username = user.getEmail().split("@")[0];
        if (!users.containsKey(username)) {
            users.put(username, user);
            System.out.println("User registered successfully.");
            return true;
        } else {
            System.out.println("Username already exists.");
            return false;
        }
    }

    // Fungsi login dengan 3 percobaan, auto-lock, dan opsi reset password
    public Pengguna login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        // Jika akun terkunci, tawarkan reset password
        if (accountLocked) {
            System.out.println("\n[PERINGATAN] Akun terkunci karena terlalu banyak percobaan gagal");
            System.out.println("1. Reset password");
            System.out.println("2. Keluar");
            System.out.print("Pilihan: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("Masukkan password baru: ");
                String newPassword = scanner.nextLine();
                System.out.print("Konfirmasi password baru: ");
                String confirmPassword = scanner.nextLine();

                if (!newPassword.equals(confirmPassword)) {
                    System.out.println("Password tidak cocok");
                    return null;
                }
                if (resetPassword(username, newPassword)) {
                    System.out.println("Password berhasil direset. Silakan login kembali.");
                } else {
                    System.out.println("Gagal reset password.");
                }
                return null;
            }
            return null;
        }

        // Input password dan lakukan autentikasi
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        lastLoginAttempt = new java.util.Date().toString();

        if (authenticate(username, password)) {
            System.out.println("Login successful!");
            loginAttempts = 0; // Reset counter jika login berhasil
            return users.get(username);
        } else {
            loginAttempts++;
            System.out.println("Invalid username or password. Percobaan " + loginAttempts + "/3");
            // Lock akun jika gagal 3x
            if (loginAttempts >= 3) {
                accountLocked = true;
                System.out.println("\n[PERINGATAN] Akun terkunci karena 3x percobaan gagal");
                System.out.println("Silakan reset password untuk membuka kunci akun");
            }
            return null;
        }
    }

    // Fungsi untuk reset password
    public boolean resetPassword(String username, String newPassword) {
        if (!users.containsKey(username)) {
            System.out.println("Username tidak ditemukan");
            return false;
        }

        if (newPassword.length() < 6) {
            System.out.println("Password harus minimal 6 karakter");
            return false;
        }

        // Reset password user
        Pengguna user = users.get(username);
        user.setPassword(newPassword);
        passwordResetCount++;
        accountLocked = false;
        loginAttempts = 0;

        System.out.println("Password berhasil direset untuk akun " + username);
        System.out.println("Reset dilakukan pada: " + new java.util.Date());
        return true;
    }

    // Mendapatkan jumlah percobaan login gagal
    public int getLoginAttempts() {
        return loginAttempts;
    }

    // Menu alternatif untuk reset password (dari luar proses login)
    public boolean showResetPasswordMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== RESET PASSWORD ===");
        System.out.print("Masukkan username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username tidak boleh kosong");
            return false;
        }
        System.out.print("Masukkan password baru: ");
        String newPassword = scanner.nextLine();

        boolean success = resetPassword(username, newPassword);
        if (success) {
            System.out.println("Password berhasil direset");
        } else {
            System.out.println("Gagal reset password");
        }
        return success;
    }
}

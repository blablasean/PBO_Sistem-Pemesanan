package backend.java.src.main.java.com.example.backend;

public class App {
    public static void main(String[] args) {
        Mahasiswa mahasiswa = new Mahasiswa("m1", "Budi", "budi@example.com", "12345678");
        Admin admin = new Admin("a1", "Sari", "sari@example.com", "IT Support");
        Barang kamera = new Kamera("b1", "Kamera Sony", 3500000, 24.2);
        Transaksi transaksi = new Transaksi("t1", mahasiswa, kamera, "2026-06-17", "2026-06-18", "Menunggu");

        Riwayat riwayat = new Riwayat();
        riwayat.addTransaksi(transaksi);

        mahasiswa.login();
        admin.login();
        System.out.println(mahasiswa.printDetails());
        System.out.println(admin.printDetails());
        System.out.println("Mahasiswa logged in: " + mahasiswa.isLoggedIn());
        System.out.println("Admin logged in: " + admin.isLoggedIn());

        admin.logout();
        System.out.println("Admin logged in after logout: " + admin.isLoggedIn());

        System.out.println(kamera.printDetails());
        System.out.println(transaksi.printDetails());
        System.out.println(riwayat.printDetails());
    }
}

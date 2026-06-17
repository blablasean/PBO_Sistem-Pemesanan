package com.example.backend;

import java.text.SimpleDateFormat;
import java.util.Date;

public class App {
    public static void main(String[] args) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date tanggalPinjam = sdf.parse("2026-06-17");
        Date tanggalKembali = sdf.parse("2026-06-18");

        Mahasiswa mahasiswa = new Mahasiswa("m1", "Budi", "budi@example.com", "12345678");
        Admin admin = new Admin("a1", "Sari", "sari@example.com", "IT Support");
        Barang kamera = new Kamera("b1", "Kamera Sony", 3500000, 24.2, "12MP");
        Transaksi transaksi = new Transaksi("t1", mahasiswa, kamera, tanggalPinjam, tanggalKembali, "Menunggu");

        Riwayat riwayat = new Riwayat();
        riwayat.tambahTransaksi(transaksi);

        mahasiswa.login();
        admin.login();
        System.out.println(mahasiswa.printDetails());
        System.out.println(admin.printDetails());
        System.out.println("Mahasiswa logged in: " + mahasiswa.isLoggedIn());
        System.out.println("Admin logged in: " + admin.isLoggedIn());

        admin.logout();
        System.out.println("Admin logged in after logout: " + admin.isLoggedIn());

        System.out.println(kamera.printInfo());
        System.out.println(transaksi.printInfo());
        System.out.println(riwayat.printInfo());
    }
}

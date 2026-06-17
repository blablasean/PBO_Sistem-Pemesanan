package com.example.backend;

public class Mahasiswa extends User {
    private final String nim;

    public Mahasiswa(String id, String nama, String email, String nim) {
        super(id, nama, email, "Mahasiswa");
        this.nim = nim;
    }

    public String getNim() {
        return nim;
    }

    public void pinjamBarang(Barang b) {
        System.out.printf("%s meminjam barang: %s (ID=%s)%n", getNama(), b.getNama(), b.getId());
    }

    public void kembalikanBarang(Transaksi t) {
        System.out.printf("%s mengembalikan barang: %s (Transaksi ID=%s)%n", getNama(), t.getBarang().getNama(), t.getIdTransaksi());
    }

    public void bayarTransaksi(Transaksi t) {
        System.out.printf("%s membayar transaksi: %s dengan barang %s%n", getNama(), t.getIdTransaksi(), t.getBarang().getNama());
    }

    @Override
    public void login() {
        super.login();
        System.out.printf("%s berhasil login sebagai %s.%n", getNama(), getRole());
    }

    @Override
    public void logout() {
        super.logout();
        System.out.printf("%s berhasil logout.%n", getNama());
    }

    @Override
    public String printDetails() {
        return String.format("%s, NIM=%s", super.printDetails(), nim);
    }
}

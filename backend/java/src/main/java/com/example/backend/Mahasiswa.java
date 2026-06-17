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

    @Override
    public String printDetails() {
        return String.format("%s, NIM=%s", super.printDetails(), nim);
    }
}

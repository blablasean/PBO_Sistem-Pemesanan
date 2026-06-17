package backend.java.src.main.java.com.example.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Riwayat implements Printable {
    private final List<Transaksi> daftarTransaksi = new ArrayList<>();

    public void tambahTransaksi(Transaksi transaksi) {
        daftarTransaksi.add(transaksi);
    }

    public List<Transaksi> getDaftarTransaksi() {
        return Collections.unmodifiableList(daftarTransaksi);
    }

    public String printInfo() {
        return printDetails();
    }

    @Override
    public String printDetails() {
        StringBuilder builder = new StringBuilder("Riwayat peminjaman:\n");
        for (Transaksi transaksi : daftarTransaksi) {
            builder.append(transaksi.printDetails()).append("\n");
        }
        return builder.toString();
    }
}

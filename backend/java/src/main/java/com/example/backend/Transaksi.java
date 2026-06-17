package backend.java.src.main.java.com.example.backend;

public class Transaksi implements Printable {
    private final String id;
    private final User user;
    private final Barang barang;
    private final String tanggalPinjam;
    private final String tanggalKembali;
    private final String status;

    public Transaksi(String id, User user, Barang barang, String tanggalPinjam, String tanggalKembali, String status) {
        this.id = id;
        this.user = user;
        this.barang = barang;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembali = tanggalKembali;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Barang getBarang() {
        return barang;
    }

    public String getTanggalPinjam() {
        return tanggalPinjam;
    }

    public String getTanggalKembali() {
        return tanggalKembali;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String printDetails() {
        return String.format("Transaksi[id=%s, user=%s, barang=%s, pinjam=%s, kembali=%s, status=%s]",
                id, user.getNama(), barang.getNama(), tanggalPinjam, tanggalKembali, status);
    }
}

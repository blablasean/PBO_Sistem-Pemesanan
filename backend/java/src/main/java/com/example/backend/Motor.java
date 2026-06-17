package backend.java.src.main.java.com.example.backend;

public class Motor extends Barang {
    private final int cc;

    public Motor(String id, String nama, double harga, int cc) {
        super(id, nama, harga);
        this.cc = cc;
    }

    public int getCc() {
        return cc;
    }

    @Override
    public String getCategory() {
        return "Motor";
    }

    @Override
    public String printDetails() {
        return String.format("%s, cc=%d", super.printDetails(), cc);
    }
}

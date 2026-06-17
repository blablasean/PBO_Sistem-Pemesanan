package backend.java.src.main.java.com.example.backend;

public class Admin extends User {
    private final String unit;

    public Admin(String id, String nama, String email, String unit) {
        super(id, nama, email, "Admin");
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void tambahBarang(Barang barang) {
        System.out.printf("%s menambahkan barang: %s (ID=%s)%n", getNama(), barang.getNama(), barang.getId());
    }

    public void editBarang(Barang barang) {
        System.out.printf("%s mengedit barang: %s (ID=%s)%n", getNama(), barang.getNama(), barang.getId());
    }

    public void hapusBarang(String idBarang) {
        System.out.printf("%s menghapus barang dengan ID: %s%n", getNama(), idBarang);
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
        return String.format("%s, unit=%s", super.printDetails(), unit);
    }
}

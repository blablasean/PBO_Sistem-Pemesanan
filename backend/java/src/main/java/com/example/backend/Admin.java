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

    @Override
    public String printDetails() {
        return String.format("%s, unit=%s", super.printDetails(), unit);
    }
}

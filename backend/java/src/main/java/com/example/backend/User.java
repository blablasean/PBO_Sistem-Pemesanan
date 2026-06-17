package backend.java.src.main.java.com.example.backend;

public abstract class User implements Printable {
    private final String id;
    private final String nama;
    private final String email;
    private final String role;
    private boolean loggedIn;

    public User(String id, String nama, String email, String role) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.role = role;
        this.loggedIn = false;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void login() {
        this.loggedIn = true;
    }

    public void logout() {
        this.loggedIn = false;
    }

    @Override
    public String printDetails() {
        return String.format("%s[id=%s, nama=%s, email=%s, role=%s, loggedIn=%s]",
                getClass().getSimpleName(), id, nama, email, role, loggedIn);
    }
}

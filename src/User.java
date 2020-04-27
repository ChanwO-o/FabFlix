public class User {

    private final String email;
    private final int id;

    public User(String email, int id)
    {
        this.email = email;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }
}
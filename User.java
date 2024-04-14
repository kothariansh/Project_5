public class User {
    private String username;
    private String name;
    private String password;
    private String profilePicturePath;

    public User(String username, String name, String password, String profilePicturePath) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.profilePicturePath = profilePicturePath;
    }

    @Override
    public String toString() {
        return username + "|" + password + "|" + name + "|" + profilePicturePath;
    }

    // Getters and Setters
}

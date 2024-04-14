import java.io.*;

public class UserService {
    private static final String USERS_FILE = "users.txt";

    public boolean registerUser(String username, String password, String name, File profilePicture) {
        String profilePicPath = storeProfilePicture(profilePicture, username);
        User newUser = new User(username, name, password, profilePicPath);

        // Append user to the file
        try (FileWriter fw = new FileWriter(USERS_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(newUser);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loginUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    return true;  // Login successful
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String storeProfilePicture(File profilePicture, String username) {
        File directory = new File("profile_pictures");
        if (!directory.exists()) {
            directory.mkdir(); // Ensure directory exists
        }
        File dest = new File(directory, username + "_" + profilePicture.getName());
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(profilePicture);
            fos = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            return dest.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null) fis.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

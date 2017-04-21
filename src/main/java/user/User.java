package user;

import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.DuplicatedUsernameException;
import exceptions.EmptyRequiredAttributeException;
import utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class User {
    static final String USERS = "users.json";

    private String username;
    private String name;

    public User() {
    }

    public User(String username, String name) {
        this.username = username;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static Map<String, User> getUsers() {
        Map<String, User> users = new HashMap<>();

        File file = new File(USERS);
        if (!file.exists()) return users;
        ObjectMapper mapper = new ObjectMapper();
        try {
            users = mapper.readValue(
                    file, mapper.getTypeFactory().constructMapType(Map.class, String.class, User.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void save() throws DuplicatedUsernameException, EmptyRequiredAttributeException {
        if (username == null || username.length() == 0 || name == null || name.length() == 0)
            throw new EmptyRequiredAttributeException();

        Map<String, User> users = getUsers();
        if (users.containsKey(this.username)) throw new DuplicatedUsernameException();
        users.put(this.username, this);
        FileUtils.saveObjectInFile(users, USERS);
    }

    public static User getUserByUsername(String username) {
        return getUsers().get(username);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        return name != null ? name.equals(user.name) : user.name == null;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Username: " + username;
    }
}

package models;

public class User {
    private int userID;
    private String firstName;
    private String lastName;
    private String phone;
    private String password;

    /**
     * For reading user object from the database
     *
     * @param name     - the name of the user
     * @param userID   - unique user identifier
     * @param lastName
     * @param phone    - phone number of the user
     * @param password - password for user authentication
     */
    public User(int userID, String firstName, String lastName, String phone, String password) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.password = password;
    }

    /**
     * For creating new user
     *
     * @param name     - the name of the user
     * @param lastName
     * @param phone    - phone number of the user
     * @param password - password for user authentication
     */
    public User(String name, String lastName, String phone, String password) {
        this.firstName = name;
        this.lastName = lastName;
        this.phone = phone;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + firstName + " " + lastName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
package vaadincrm.domain;

/**
 * Created by someone on 29-Jul-2015.
 */
public class User {
    private String username;
    private String firstName;
    private String lastName;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}

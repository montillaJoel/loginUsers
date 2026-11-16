package src.models;

/**
 * Base class with a person's personal and contact data.
 */
public abstract class Person {
    private String name;
    private String lastName;
    private String phoneNumber;
    private String email;

    /**
     * Creates a person with the given data.
     *
     * @param name        the first name
     * @param lastName    the last name
     * @param phoneNumber the phone number
     * @param email       the email address
     */
    protected Person(String name, String lastName, String phoneNumber, String email){
        this.name = name;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    /**
     * Returns the full name.
     *
     * @return the first name and last name separated by a space
     */
    public String getFullName(){
        return this.name + " " + this.lastName;
    }

    /**
     * Returns the first name.
     *
     * @return the first name, without leading or trailing spaces
     */
    public String getName(){
        return this.name.trim();
    }

    /**
     * Returns the last name.
     *
     * @return the last name, without leading or trailing spaces
     */
    public String getLastName(){
        return this.lastName.trim();

    }

    /**
     * Returns the phone number.
     *
     * @return the phone number, without leading or trailing spaces
     */
    public String getPhoneNumber(){
        return this.phoneNumber.trim();
    }

    /**
     * Returns the email address.
     *
     * @return the email address, without leading or trailing spaces
     */
    public String getEmail(){
        return this.email.trim();
    }

    /**
     * Sets the first name.
     *
     * @param name the new first name
     * @throws IllegalArgumentException if the name is null or blank
     */
    void setName(String name){
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        this.name = name;
        }

    /**
     * Sets the last name.
     *
     * @param lastName the new last name
     * @throws IllegalArgumentException if the last name is null or blank
     */
    void setLastName(String lastName){
        if (lastName == null || lastName.isBlank()){
            throw new IllegalArgumentException("Last name cannot be null or empty");
        } this.lastName = lastName;
    }

    /**
     * Sets the phone number.
     *
     * @param phoneNumber the new phone number
     * @throws IllegalArgumentException if the phone number is null or blank
     */
    void setPhoneNumber(String phoneNumber){
        if (phoneNumber == null || phoneNumber.isBlank()){
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        } this.phoneNumber = phoneNumber;
    }

    /**
     * Sets the email address.
     *
     * @param email the new email address
     * @throws IllegalArgumentException if the email is null or blank
     */
    void setEmail(String email){
        if (email == null || email.isBlank()){
            throw new IllegalArgumentException("Email cannot be null or empty");
        } this.email = email;
    }
}

package src.models;

/**
 * A system user: a {@link Person} with an ID, a username and a password hash key.
 */
public class User extends Person{
    private static int counter = 0;
    private int id;
    private String userName;
    private String hashKey;

    /**
     * Creates a user and assigns them the next ID from an in-memory counter.
     *
     * @param name        the first name
     * @param lastName    the last name
     * @param phoneNumber the phone number
     * @param email       the email address
     * @param userName    the username
     * @param hashKey     the password hash key (or the plain-text password before registration)
     */
    public User( String name, String lastName, String phoneNumber, String email, String userName, String hashKey){
        super(name, lastName, phoneNumber, email);
        this.id = counter++;
        this.userName = userName;
        this.hashKey = hashKey;
    }

    /**
     * Returns the user's ID.
     *
     * @return the ID
     */
    public int getId(){
        return this.id;
    }

    /**
     * Returns the username.
     *
     * @return the username, without leading or trailing spaces
     */
    public String getUserName(){
        return this.userName.trim();
    }

    /**
     * Returns the password hash key.
     *
     * @return the hash key, without leading or trailing spaces
     */
    public String getHashKey(){
        return this.hashKey.trim();
    }

    /**
     * Sets the user's ID.
     *
     * @param id the new ID
     * @throws IllegalArgumentException if the ID is negative
     */
    public void setId(int id){
        if (id < 0){
            throw new IllegalArgumentException("ID cannot be negative");
        } this.id = id;
    }

    /**
     * Sets the username.
     *
     * @param userName the new username
     * @throws IllegalArgumentException if the username is null or blank
     */
    public void setUserName(String userName){
        if (userName == null || userName.isBlank()){
            throw new IllegalArgumentException("Username cannot be null or empty");
        } this.userName = userName;
    }

    /**
     * Sets the password hash key.
     *
     * @param hashKey the new hash key
     * @throws IllegalArgumentException if the hash key is null or blank
     */
    public void setHashKey(String hashKey){
        if (hashKey == null || hashKey.isBlank()){
            throw new IllegalArgumentException("Hash key cannot be null or empty");
        } this.hashKey = hashKey;
    }
}

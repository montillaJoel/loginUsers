package src.service;

import src.DAO.DAOUser;
import src.models.User;
import src.utils.SureKeys;
import java.util.List;

/**
 * Business logic for user management: registration, login and CRUD operations.
 * <p>
 * Validates input before delegating persistence to a {@link DAOUser} implementation.
 */
public class UserService {

    private final DAOUser userDAO;

    /**
     * Creates the service with the DAO it will use for persistence.
     *
     * @param userDAO the data access object injected through the constructor
     */
    public UserService(DAOUser userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Authenticates a user by username and password.
     *
     * @param login    the user's username (case-insensitive)
     * @param password the plain-text password
     * @return the stored user whose username and password match
     * @throws Exception if a field is empty, no user matches, or the password does not match
     */
    public User loginUser(String login, String password) throws Exception {
        if (login == null || login.trim().isEmpty()) {
            throw new Exception("Enter your username.");
        }
        if (password == null || password.isEmpty()) {
            throw new Exception("Enter your password.");
        }

        String identifier = login.trim();
        User foundUser = null;
        for (User candidate : userDAO.getAll()) {
            if (candidate != null && identifier.equalsIgnoreCase(candidate.getUserName())) {
                foundUser = candidate;
                break;
            }
        }

        // Same message for unknown user and wrong password, so nobody can guess which accounts exist
        if (foundUser == null || !SureKeys.verifyPassword(password, foundUser.getHashKey())) {
            throw new Exception("Invalid username or password.");
        }
        return foundUser;
    }

    /**
     * Validates a new user, hashes their password and saves them in the database.
     * <p>
     * The user's hash key field is replaced with the hashed password before saving.
     *
     * @param user the user to register, with the plain-text password in its hash key field
     * @throws Exception if the user is null, the name, last name or username is empty, the email
     *                   is invalid, the password is shorter than 6 characters, or hashing fails
     */
    public void registerUser(User user) throws Exception {
        validateData(user);
        if (user.getHashKey() == null || user.getHashKey().length() < 6) {
            throw new Exception("The password must have at least 6 characters.");
        }

        // Hash the password before saving
        String hashedPassword = SureKeys.generateHashKey(user.getHashKey());
        user.setHashKey(hashedPassword);

        // Call the DAO to insert the user into the database
        userDAO.insert(user);
    }

    /**
     * Finds a user by ID.
     *
     * @param id the user's ID; must be greater than zero
     * @return the matching user
     * @throws Exception if the ID is not greater than zero or no user has that ID
     */
    public User findUserById(int id) throws Exception {
        if (id <= 0) {
            throw new Exception("The ID must be greater than zero.");
        }
        User user = userDAO.getById(id);
        if (user == null) {
            throw new Exception("User not found.");
        }
        return user;
    }

    /**
     * Lists all users.
     *
     * @return all stored users; an empty list if there are none
     */
    public List<User> listAllUsers() {
        return userDAO.getAll();
    }

    /**
     * Updates an existing user's data, keeping their current password.
     *
     * @param user the user with the new data; their ID identifies the record to update
     * @throws Exception if the ID is invalid, a field is invalid, or the username/email is taken
     */
    public void updateUser(User user) throws Exception {
        updateUser(user, null);
    }

    /**
     * Updates an existing user's data and, optionally, their password.
     *
     * @param user        the user with the new data; their ID identifies the record to update
     *                    and their hash key is kept unless a new password is given
     * @param newPassword the new plain-text password, or {@code null}/empty to keep the current one
     * @throws Exception if the ID is invalid, a field is invalid, the username or email belongs
     *                   to another user, or the new password is shorter than 6 characters
     */
    public void updateUser(User user, String newPassword) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new Exception("The user ID is invalid for update.");
        }
        validateData(user);

        // Username and email must stay unique among the other users
        for (User other : userDAO.getAll()) {
            if (other == null || other.getId() == user.getId()) {
                continue;
            }
            if (other.getUserName().equalsIgnoreCase(user.getUserName())) {
                throw new Exception("The username " + user.getUserName() + " is already taken.");
            }
            if (other.getEmail().equalsIgnoreCase(user.getEmail())) {
                throw new Exception("The email " + user.getEmail() + " is already registered.");
            }
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            if (newPassword.length() < 6) {
                throw new Exception("The password must have at least 6 characters.");
            }
            user.setHashKey(SureKeys.generateHashKey(newPassword));
        }
        userDAO.update(user);
    }

    /**
     * Checks the fields shared by registration and update: name, last name, email and username.
     *
     * @param user the user to check
     * @throws Exception if the user is null or a field is empty or invalid
     */
    private void validateData(User user) throws Exception {
        if (user == null) {
            throw new Exception("The user is required.");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new Exception("The name is required.");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new Exception("The last name is required.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new Exception("The email is not valid.");
        }
        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            throw new Exception("The username is required.");
        }
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete; must be greater than zero
     * @throws Exception if the ID is not greater than zero
     */
    public void deleteUser(int id) throws Exception {
        if (id <= 0) {
            throw new Exception("The ID is invalid for deletion.");
        }
        userDAO.delete(id);
    }
}

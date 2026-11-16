package src.service;

import src.DAO.DAOFactory;
import src.DAO.DAOUser;
import src.models.User;
import src.utils.SureKeys;
import java.util.List;

/**
 * Business logic for user management: registration, login and CRUD operations.
 * <p>
 * Singleton pattern: the whole app shares one instance, obtained with {@link #getInstance()}.
 * The DAO it uses comes from {@link DAOFactory}. Messages are in Spanish because they are shown
 * to the user.
 */
public class UserService {

    /** Message required by the assignment when the login fields are empty. */
    public static final String LOGIN_EMPTY_MESSAGE =
            "Debe ingresar su usuario y contraseña, si no está registrado debe registrarse.";

    private static final int MIN_PASSWORD_LENGTH = 6;

    private static UserService instance;

    private final DAOUser userDAO;

    /**
     * Private constructor: use {@link #getInstance()}.
     *
     * @param userDAO the data access object used for persistence
     */
    private UserService(DAOUser userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Returns the single shared instance, creating it the first time.
     *
     * @return the service instance
     */
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService(DAOFactory.createUserDAO());
        }
        return instance;
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
        if (isBlank(login) || password == null || password.isEmpty()) {
            throw new Exception(LOGIN_EMPTY_MESSAGE);
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
            throw new Exception("Usuario o contraseña incorrectos.");
        }
        return foundUser;
    }

    /**
     * Validates a new user, hashes their password and saves them in the database.
     * <p>
     * The user's hash key field is replaced with the hashed password before saving.
     *
     * @param user the user to register, with the plain-text password in its hash key field
     * @throws Exception if a field is missing or invalid, the username or email is taken,
     *                   or the user could not be saved
     */
    public void registerUser(User user) throws Exception {
        validateData(user);
        String password = user.getHashKey();
        if (isBlank(password)) {
            throw new Exception("El campo \"Contraseña\" es obligatorio.");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new Exception("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres.");
        }
        checkUnique(user, -1);

        // Hash the password before saving
        user.setHashKey(SureKeys.generateHashKey(password));
        userDAO.insert(user);

        if (user.getId() <= 0) {
            throw new Exception("No se pudo guardar el usuario en la base de datos.");
        }
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
            throw new Exception("El ID debe ser mayor que cero.");
        }
        User user = userDAO.getById(id);
        if (user == null) {
            throw new Exception("Usuario no encontrado.");
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
     * Updates an existing user's data and, optionally, their password (method overloading).
     *
     * @param user        the user with the new data; their ID identifies the record to update
     *                    and their hash key is kept unless a new password is given
     * @param newPassword the new plain-text password, or {@code null}/empty to keep the current one
     * @throws Exception if the ID is invalid, a field is invalid, the username or email belongs
     *                   to another user, or the new password is too short
     */
    public void updateUser(User user, String newPassword) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new Exception("El ID del usuario no es válido para actualizar.");
        }
        validateData(user);
        checkUnique(user, user.getId());

        if (newPassword != null && !newPassword.isEmpty()) {
            if (newPassword.length() < MIN_PASSWORD_LENGTH) {
                throw new Exception("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres.");
            }
            user.setHashKey(SureKeys.generateHashKey(newPassword));
        }
        userDAO.update(user);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete; must be greater than zero
     * @throws Exception if the ID is not greater than zero
     */
    public void deleteUser(int id) throws Exception {
        if (id <= 0) {
            throw new Exception("El ID no es válido para eliminar.");
        }
        userDAO.delete(id);
    }

    /**
     * Checks the fields shared by registration and update, in the same order as the form,
     * and reports the first missing one by name.
     *
     * @param user the user to check
     * @throws Exception if the user is null or a field is empty or invalid
     */
    private void validateData(User user) throws Exception {
        if (user == null) {
            throw new Exception("El usuario es obligatorio.");
        }
        requireField(user.getUserName(), "Nombre de usuario");
        requireField(user.getName(), "Nombre");
        requireField(user.getLastName(), "Apellido");
        requireField(user.getPhoneNumber(), "Teléfono");
        requireField(user.getEmail(), "Correo electrónico");
        if (!user.getEmail().contains("@")) {
            throw new Exception("El correo electrónico no es válido.");
        }
    }

    /**
     * Makes sure no other user has the same username or email.
     *
     * @param user      the user being saved
     * @param ignoredId the ID to skip (the user's own ID when updating), or -1 when registering
     * @throws Exception if the username or email is already taken
     */
    private void checkUnique(User user, int ignoredId) throws Exception {
        for (User other : userDAO.getAll()) {
            if (other == null || other.getId() == ignoredId) {
                continue;
            }
            if (other.getUserName().equalsIgnoreCase(user.getUserName())) {
                throw new Exception("El nombre de usuario \"" + user.getUserName() + "\" ya está en uso.");
            }
            if (other.getEmail().equalsIgnoreCase(user.getEmail())) {
                throw new Exception("El correo \"" + user.getEmail() + "\" ya está registrado.");
            }
        }
    }

    private static void requireField(String value, String fieldName) throws Exception {
        if (isBlank(value)) {
            throw new Exception("El campo \"" + fieldName + "\" es obligatorio.");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

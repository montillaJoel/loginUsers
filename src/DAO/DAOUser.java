package src.DAO;

import src.models.User;
import java.util.List;

/**
 * Data access contract for {@link User} records.
 */
public interface DAOUser {

    /**
     * Inserts a new user.
     *
     * @param u the user to insert
     */
    void insert(User u);

    /**
     * Finds a user by ID.
     *
     * @param id the user's ID
     * @return the matching user, or {@code null} if none is found
     */
    User getById(int id);

    /**
     * Gets the list of all users.
     *
     * @return all users; an empty list if there are none
     */
    List<User> getAll();

    /**
     * Updates an existing user's data.
     *
     * @param u the user with the new data; their ID identifies the record to update
     */
    void update(User u);

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete
     */
    void delete(int id);
}

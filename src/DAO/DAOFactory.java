package src.DAO;

/**
 * Factory pattern: creates the {@link DAOUser} implementation the app uses.
 * <p>
 * The rest of the code asks the factory for a {@code DAOUser} instead of calling
 * {@code new DAOMySQL()} directly, so the storage can be swapped in one place.
 */
public final class DAOFactory {

    /** Storage types this factory can create. */
    public enum Type {
        MYSQL
    }

    private DAOFactory() {
    }

    /**
     * Creates the default user DAO (MySQL).
     *
     * @return a new {@link DAOUser}
     */
    public static DAOUser createUserDAO() {
        return createUserDAO(Type.MYSQL);
    }

    /**
     * Creates a user DAO for the given storage type.
     *
     * @param type the storage type
     * @return a new {@link DAOUser} for that type
     * @throws IllegalArgumentException if the type is not supported
     */
    public static DAOUser createUserDAO(Type type) {
        switch (type) {
            case MYSQL:
                return new DAOMySQL();
            default:
                throw new IllegalArgumentException("Tipo de almacenamiento no soportado: " + type);
        }
    }
}

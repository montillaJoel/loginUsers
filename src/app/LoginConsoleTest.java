package src.app;

import src.DAO.DAOMySQL;
import src.DAO.DAOUser;
import src.service.UserService;
import src.models.User;


public class LoginConsoleTest {
    public static void main (String[] args) throws Exception{
        User joel = new User( "Joel David", "Montilla Martinez", "123-456-7890", "joel@example.com", "joel123", "hashedKey123");
        System.out.println(joel.getFullName());
        System.out.println("ID: " + joel.getId() + ", Username: " + joel.getUserName() + ", Hash Key: " + joel.getHashKey());
        User jorge = new User( "Jorge", "De la Cruz", "098-765-4321", "jorge@example.com", "jorge123", "hashedKey456");
        jorge.setUserName("Jorge Luis");
        System.out.println(jorge.getFullName() + " - " + jorge.getPhoneNumber());
        System.out.println("ID: " + jorge.getId() + ", Username: " + jorge.getUserName() + ", Hash Key: " + jorge.getHashKey());

        DAOUser dao = new DAOMySQL();
        UserService userService = new UserService(dao);

        // Use the service layer instead of calling the DAO directly.
        // Register Joel only if his email is not in the database yet, so Main can be run several times.
        boolean joelExists = userService.listAllUsers().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(joel.getEmail()));
        if (joelExists) {
            System.out.println("Joel is already registered, skipping registration.");
        } else {
            userService.registerUser(joel);
            System.out.println("Joel registered with ID: " + joel.getId());
        }

        // Login test with the correct password
        tryLogin(userService, "joel123", "hashedKey123");

        // Login test with a wrong password
        tryLogin(userService, "joel123", "wrongPassword");
    }

    /**
     * Tries to log in and prints the result.
     *
     * @param userService the service used to log in
     * @param username    the username to log in with
     * @param password    the plain-text password
     */
    private static void tryLogin(UserService userService, String username, String password) {
        try {
            User loggedIn = userService.loginUser(username, password);
            System.out.println("Login OK: " + loggedIn.getFullName() + " (ID " + loggedIn.getId() + ")");
        } catch (Exception e) {
            System.out.println("Login failed for " + username + ": " + e.getMessage());
        }
    }
}

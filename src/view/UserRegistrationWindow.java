package src.view;

import src.DAO.DAOMySQL;
import src.DAO.DAOUser;
import src.service.UserService;
import src.models.User;

import javax.swing.*;

import java.awt.FlowLayout;

/**
 * Registration form. Opened from {@link LoginWindow}; it closes itself after a successful registration.
 */
public class UserRegistrationWindow {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserRegistrationWindow::open);
    }

    /** Builds and shows the registration window. Must be called on the Swing thread. */
    public static void open() {
        UiStyle.setLookAndFeel();

        JFrame window = new JFrame("User Registration");
        JTextField nameField = new JTextField(22);
        JTextField lastNameField = new JTextField(22);
        JTextField phoneField = new JTextField(22);
        JTextField emailField = new JTextField(22);
        JTextField usernameField = new JTextField(22);
        JPasswordField passwordField = new JPasswordField(22);
        JPasswordField confirmPasswordField = new JPasswordField(22);
        JButton registerButton = new JButton("Register");
        JButton clearButton = new JButton("Clear");

        JPanel form = UiStyle.form();
        UiStyle.addRow(form, 0, "Name", nameField);
        UiStyle.addRow(form, 1, "Last name", lastNameField);
        UiStyle.addRow(form, 2, "Phone", phoneField);
        UiStyle.addRow(form, 3, "Email", emailField);
        UiStyle.addRow(form, 4, "Username", usernameField);
        UiStyle.addRow(form, 5, "Password", passwordField);
        UiStyle.addRow(form, 6, "Confirm password", confirmPasswordField);

        UiStyle.stylePrimary(registerButton);
        clearButton.setFocusPainted(false);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(clearButton);
        buttons.add(registerButton);

        window.setContentPane(UiStyle.content(
                UiStyle.header("Create account", "Fill in your details to register."),
                form, buttons));

        // Pressing Enter in any field triggers Register
        window.getRootPane().setDefaultButton(registerButton);

        clearButton.addActionListener(e -> {
            for (JTextField f : new JTextField[] {nameField, lastNameField, phoneField,
                    emailField, usernameField, passwordField, confirmPasswordField}) {
                f.setText("");
            }
            nameField.requestFocusInWindow();
        });

        registerButton.addActionListener(e -> {
            String name = nameField.getText();
            String lastName = lastNameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(window, "The passwords do not match.",
                        "Registration", JOptionPane.WARNING_MESSAGE);
                confirmPasswordField.setText("");
                confirmPasswordField.requestFocusInWindow();
                return;
            }

            User newUser = new User(name, lastName, phone, email, username, password);
            DAOUser dao = new DAOMySQL();
            UserService userService = new UserService(dao);

            try {
                boolean newUserExists = userService.listAllUsers().stream()
                        .anyMatch(u -> email.trim().equalsIgnoreCase(u.getEmail()));
                if (newUserExists) {
                    JOptionPane.showMessageDialog(window,
                            "A user with email " + email + " is already registered.",
                            "Registration", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                userService.registerUser(newUser);
                if (newUser.getId() > 0) {
                    JOptionPane.showMessageDialog(window,
                            newUser.getName() + " registered with ID: " + newUser.getId()
                                    + ".\nYou can now log in.");
                    window.dispose();
                } else {
                    JOptionPane.showMessageDialog(window,
                            "The user could not be saved to the database.",
                            "Registration", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(window, ex.getMessage(),
                        "Registration", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Closing this window only closes the form, not the whole app
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UiStyle.show(window);
    }
}

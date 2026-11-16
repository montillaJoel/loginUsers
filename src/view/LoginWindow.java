package src.view;

import src.DAO.DAOMySQL;
import src.service.UserService;
import src.models.User;

import javax.swing.*;

import java.awt.BorderLayout;

/**
 * Login screen: the user enters their username and password.
 * On success it opens {@link MainWindow}; it also links to the registration window.
 */
public class LoginWindow {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginWindow::open);
    }

    /** Builds and shows the login window. Must be called on the Swing thread. */
    public static void open() {
        UiStyle.setLookAndFeel();

        JFrame window = new JFrame("Log In");
        JTextField userField = new JTextField(22);
        JPasswordField passwordField = new JPasswordField(22);
        JButton loginButton = new JButton("Log in");
        JButton registerButton = new JButton("Create account");

        JPanel form = UiStyle.form();
        UiStyle.addRow(form, 0, "Username", userField);
        UiStyle.addRow(form, 1, "Password", passwordField);

        UiStyle.stylePrimary(loginButton);
        registerButton.setFocusPainted(false);

        // "Create account" on the left, "Log in" on the right
        JPanel buttons = new JPanel(new BorderLayout());
        buttons.add(registerButton, BorderLayout.WEST);
        buttons.add(loginButton, BorderLayout.EAST);

        window.setContentPane(UiStyle.content(
                UiStyle.header("Log In", "Enter your username and password."),
                form, buttons));

        // Pressing Enter in any field triggers Log in
        window.getRootPane().setDefaultButton(loginButton);

        loginButton.addActionListener(e -> {
            String login = userField.getText();
            String password = new String(passwordField.getPassword());
            try {
                User user = new UserService(new DAOMySQL()).loginUser(login, password);
                window.dispose();
                MainWindow.open(user);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(window, ex.getMessage(),
                        "Log In", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
                passwordField.requestFocusInWindow();
            }
        });

        registerButton.addActionListener(e -> UserRegistrationWindow.open());

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UiStyle.show(window);
    }
}

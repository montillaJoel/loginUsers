package src.view;

import src.models.User;
import src.service.UserService;

import javax.swing.*;

import java.awt.BorderLayout;

/**
 * Login screen: the user enters their username and password.
 * On success it closes and opens {@link MainWindow}; it also links to the registration window.
 */
public class LoginWindow {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginWindow::open);
    }

    /** Builds and shows the login window. Must be called on the Swing thread. */
    public static void open() {
        UiStyle.setLookAndFeel();

        JFrame window = new JFrame("Iniciar sesión");
        JTextField userField = new JTextField(22);
        JPasswordField passwordField = new JPasswordField(22);   // hides the typed password
        JButton loginButton = new JButton("Entrar");
        JButton registerButton = new JButton("Registrarse");

        JPanel form = UiStyle.form();
        UiStyle.addRow(form, 0, "Nombre de usuario", userField);
        UiStyle.addRow(form, 1, "Contraseña", passwordField);

        UiStyle.stylePrimary(loginButton);
        registerButton.setFocusPainted(false);

        // "Registrarse" on the left, "Entrar" on the right
        JPanel buttons = new JPanel(new BorderLayout());
        buttons.add(registerButton, BorderLayout.WEST);
        buttons.add(loginButton, BorderLayout.EAST);

        window.setContentPane(UiStyle.content(
                UiStyle.header("LOGIN", "Ingrese su usuario y contraseña."),
                form, buttons));

        // Pressing Enter in any field triggers "Entrar"
        window.getRootPane().setDefaultButton(loginButton);

        loginButton.addActionListener(e -> {
            String login = userField.getText();
            String password = new String(passwordField.getPassword());
            try {
                User user = UserService.getInstance().loginUser(login, password);
                window.dispose();
                MainWindow.open(user);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(window, ex.getMessage(),
                        "Iniciar sesión", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
                (userField.getText().trim().isEmpty() ? userField : passwordField).requestFocusInWindow();
            }
        });

        registerButton.addActionListener(e -> UserRegistrationWindow.open());

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UiStyle.show(window);
    }
}

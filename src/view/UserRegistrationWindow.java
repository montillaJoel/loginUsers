package src.view;

import src.models.User;
import src.service.UserService;

import javax.swing.*;

import java.awt.FlowLayout;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registration form. Opened from {@link LoginWindow} ("Registrarse") or from {@link MainWindow}
 * ("Nuevo"); it closes itself after a successful registration.
 */
public class UserRegistrationWindow {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserRegistrationWindow::open);
    }

    /** Opens the form from the login screen. Must be called on the Swing thread. */
    public static void open() {
        open(null);
    }

    /**
     * Builds and shows the registration window. Must be called on the Swing thread.
     *
     * @param onRegistered code to run after a successful registration (e.g. reload a table),
     *                     or {@code null} when opened from the login screen
     */
    public static void open(Runnable onRegistered) {
        UiStyle.setLookAndFeel();

        JFrame window = new JFrame("Registro de usuario");
        JTextField usernameField = new JTextField(22);
        JTextField nameField = new JTextField(22);
        JTextField lastNameField = new JTextField(22);
        JTextField phoneField = new JTextField(22);
        JTextField emailField = new JTextField(22);
        JPasswordField passwordField = new JPasswordField(22);          // hidden
        JPasswordField confirmPasswordField = new JPasswordField(22);   // hidden
        JButton registerButton = new JButton("Registrar");
        JButton clearButton = new JButton("Limpiar");

        // Fields in form order; the label is also the name shown when a field is missing
        Map<String, JTextField> fields = new LinkedHashMap<>();
        fields.put("Nombre de usuario", usernameField);
        fields.put("Nombre", nameField);
        fields.put("Apellido", lastNameField);
        fields.put("Teléfono", phoneField);
        fields.put("Correo electrónico", emailField);
        fields.put("Contraseña", passwordField);
        fields.put("Confirmar contraseña", confirmPasswordField);

        JPanel form = UiStyle.form();
        int row = 0;
        for (Map.Entry<String, JTextField> entry : fields.entrySet()) {
            UiStyle.addRow(form, row++, entry.getKey(), entry.getValue());
        }

        UiStyle.stylePrimary(registerButton);
        clearButton.setFocusPainted(false);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(clearButton);
        buttons.add(registerButton);

        window.setContentPane(UiStyle.content(
                UiStyle.header("REGISTRO", "Todos los campos son obligatorios."),
                form, buttons));

        // Pressing Enter in any field triggers "Registrar"
        window.getRootPane().setDefaultButton(registerButton);

        clearButton.addActionListener(e -> {
            fields.values().forEach(f -> f.setText(""));
            usernameField.requestFocusInWindow();
        });

        registerButton.addActionListener(e -> {
            // 1. Every field is required: report the first missing one by name
            for (Map.Entry<String, JTextField> entry : fields.entrySet()) {
                if (entry.getValue().getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(window,
                            "Falta el campo \"" + entry.getKey() + "\".",
                            "Registro", JOptionPane.WARNING_MESSAGE);
                    entry.getValue().requestFocusInWindow();
                    return;
                }
            }

            // 2. Password and confirmation must match
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(window,
                        "La contraseña y la confirmación de la contraseña no coinciden.",
                        "Registro", JOptionPane.ERROR_MESSAGE);
                confirmPasswordField.setText("");
                confirmPasswordField.requestFocusInWindow();
                return;
            }

            // 3. The service checks the email, password length and duplicates, then saves
            User newUser = new User(nameField.getText(), lastNameField.getText(),
                    phoneField.getText(), emailField.getText(), usernameField.getText(), password);
            try {
                UserService.getInstance().registerUser(newUser);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(window, ex.getMessage(),
                        "Registro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String message = "Usuario \"" + newUser.getUserName() + "\" registrado correctamente.";
            if (onRegistered == null) {
                message += "\nYa puede iniciar sesión.";
            }
            JOptionPane.showMessageDialog(window, message, "Registro", JOptionPane.INFORMATION_MESSAGE);
            window.dispose();
            if (onRegistered != null) {
                onRegistered.run();
            }
        });

        // Closing this window only closes the form, not the whole app
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UiStyle.show(window);
    }
}

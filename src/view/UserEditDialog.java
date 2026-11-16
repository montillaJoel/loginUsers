package src.view;

import src.DAO.DAOMySQL;
import src.models.User;
import src.service.UserService;

import javax.swing.*;

import java.awt.FlowLayout;
import java.awt.Window;

/**
 * Modal dialog to edit a user's data. The password is only changed if a new one is typed.
 */
final class UserEditDialog {

    private UserEditDialog() {
    }

    /**
     * Shows the dialog and waits until it is closed.
     *
     * @param owner the window the dialog belongs to
     * @param user  the user to edit, as loaded from the database
     * @return the saved user if the changes were saved, or {@code null} if cancelled
     */
    static User edit(Window owner, User user) {
        JDialog dialog = new JDialog(owner, "Editar cliente", JDialog.ModalityType.APPLICATION_MODAL);
        JTextField nameField = new JTextField(user.getName(), 22);
        JTextField lastNameField = new JTextField(user.getLastName(), 22);
        JTextField phoneField = new JTextField(user.getPhoneNumber(), 22);
        JTextField emailField = new JTextField(user.getEmail(), 22);
        JTextField usernameField = new JTextField(user.getUserName(), 22);
        JPasswordField passwordField = new JPasswordField(22);
        JButton saveButton = new JButton("Guardar");
        JButton cancelButton = new JButton("Cancelar");

        JPanel form = UiStyle.form();
        UiStyle.addRow(form, 0, "Nombre", nameField);
        UiStyle.addRow(form, 1, "Apellido", lastNameField);
        UiStyle.addRow(form, 2, "Teléfono", phoneField);
        UiStyle.addRow(form, 3, "Email", emailField);
        UiStyle.addRow(form, 4, "Usuario", usernameField);
        UiStyle.addRow(form, 5, "Nueva contraseña", passwordField);

        UiStyle.stylePrimary(saveButton);
        cancelButton.setFocusPainted(false);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(cancelButton);
        buttons.add(saveButton);

        dialog.setContentPane(UiStyle.content(
                UiStyle.header("Editar cliente", "Deja la contraseña vacía para no cambiarla."),
                form, buttons));
        dialog.getRootPane().setDefaultButton(saveButton);

        User[] result = {null};

        saveButton.addActionListener(e -> {
            User updated = new User(nameField.getText(), lastNameField.getText(),
                    phoneField.getText(), emailField.getText(), usernameField.getText(),
                    user.getHashKey());
            updated.setId(user.getId());
            try {
                new UserService(new DAOMySQL())
                        .updateUser(updated, new String(passwordField.getPassword()));
                result[0] = updated;
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Editar cliente", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true); // blocks until the dialog is closed
        return result[0];
    }
}

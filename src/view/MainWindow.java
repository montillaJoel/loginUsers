package src.view;

import src.DAO.DAOMySQL;
import src.models.User;
import src.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Window shown after a successful login: a table with all registered users from the database.
 * Users can be edited and deleted; the table reloads from the database after every change.
 */
public class MainWindow {

    private static final String[] COLUMNS = {"Nombre", "Apellido", "Teléfono", "Email", "Usuario"};

    private final JFrame window = new JFrame("Clientes Registrados");
    private final UserService userService = new UserService(new DAOMySQL());
    private final List<User> users = new ArrayList<>();   // same order as the table model rows
    private final JLabel subtitle = new JLabel();
    private final DefaultTableModel model = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private User currentUser;

    private MainWindow(User currentUser) {
        this.currentUser = currentUser;
    }

    /** Builds and shows the main window for the logged-in user. */
    public static void open(User user) {
        new MainWindow(user).build();
    }

    private void build() {
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);           // click a header to sort
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(720, 300));

        JButton editButton = new JButton("Editar");
        JButton deleteButton = new JButton("Eliminar");
        JButton logoutButton = new JButton("Cerrar sesión");
        editButton.setFocusPainted(false);
        deleteButton.setFocusPainted(false);
        UiStyle.stylePrimary(logoutButton);

        // Edit / Delete on the left, Log out on the right
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(editButton);
        actions.add(deleteButton);
        JPanel buttons = new JPanel(new BorderLayout());
        buttons.add(actions, BorderLayout.WEST);
        buttons.add(logoutButton, BorderLayout.EAST);

        JPanel header = UiStyle.header("Clientes Registrados", " ");
        header.remove(header.getComponentCount() - 1);   // replace the placeholder subtitle
        subtitle.setForeground(java.awt.Color.GRAY);
        header.add(subtitle);

        window.setContentPane(UiStyle.content(header, scroll, buttons));

        editButton.addActionListener(e -> editSelected());
        deleteButton.addActionListener(e -> deleteSelected());
        logoutButton.addActionListener(e -> logout());

        // Double-click a row to edit it
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    editSelected();
                }
            }
        });

        reload();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UiStyle.show(window);
    }

    /** Reloads all users from the database into the table. */
    private void reload() {
        users.clear();
        model.setRowCount(0);
        try {
            for (User u : userService.listAllUsers()) {
                users.add(u);
                model.addRow(new Object[] {u.getName(), u.getLastName(), u.getPhoneNumber(),
                        u.getEmail(), u.getUserName()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, "No se pudieron cargar los clientes: " + ex.getMessage(),
                    "Clientes Registrados", JOptionPane.ERROR_MESSAGE);
        }
        subtitle.setText("Sesión iniciada como " + currentUser.getUserName()
                + " · " + users.size() + " clientes");
    }

    /** Returns the user in the selected row, or {@code null} (with a message) if none is selected. */
    private User selectedUser() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(window, "Selecciona un cliente de la tabla.",
                    "Clientes Registrados", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        // The table can be sorted, so convert the view row to the model row
        return users.get(table.convertRowIndexToModel(viewRow));
    }

    private void editSelected() {
        User selected = selectedUser();
        if (selected == null) {
            return;
        }
        User saved = UserEditDialog.edit(window, selected);
        if (saved != null) {
            if (saved.getId() == currentUser.getId()) {
                currentUser = saved;   // keep the header up to date if you edited yourself
            }
            reload();
        }
    }

    private void deleteSelected() {
        User selected = selectedUser();
        if (selected == null) {
            return;
        }
        boolean isSelf = selected.getId() == currentUser.getId();
        String message = "¿Eliminar a " + selected.getFullName() + " (" + selected.getUserName() + ")?"
                + (isSelf ? "\nEs tu propia cuenta: se cerrará la sesión." : "");
        int answer = JOptionPane.showConfirmDialog(window, message, "Eliminar cliente",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            userService.deleteUser(selected.getId());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, ex.getMessage(),
                    "Eliminar cliente", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isSelf) {
            logout();
        } else {
            reload();
        }
    }

    private void logout() {
        window.dispose();
        LoginWindow.open();
    }
}

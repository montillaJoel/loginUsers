package src.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Shared look for all windows: colors, header, form rows and buttons.
 */
final class UiStyle {

    static final Color ACCENT = new Color(37, 99, 235);

    private UiStyle() {
    }

    /** Uses Nimbus if available, so the windows look the same on any system. */
    static void setLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIManager.put("nimbusBase", ACCENT);
                    return;
                }
            }
        } catch (Exception ignored) {
            // Fall back to the default look and feel
        }
    }

    /** A bold title with a gray subtitle below it. */
    static JPanel header(String title, String subtitle) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setForeground(Color.GRAY);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitleLabel);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));
        return header;
    }

    /** An empty form panel; fill it with {@link #addRow}. */
    static JPanel form() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        return form;
    }

    /** Adds a label + field row to a form. */
    static void addRow(JPanel form, int row, String label, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.insets = new Insets(5, 0, 5, 12);
        c.anchor = GridBagConstraints.LINE_END;
        c.gridx = 0;
        form.add(new JLabel(label), c);

        c.gridx = 1;
        c.insets = new Insets(5, 0, 5, 0);
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        form.add(field, c);
    }

    /** Blue, bold button for the main action of a window. */
    static void stylePrimary(JButton button) {
        button.setBackground(ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
    }

    /** Places header, body and buttons in a padded content panel. */
    static JPanel content(JComponent header, JComponent body, JComponent buttons) {
        buttons.setOpaque(false);
        buttons.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(24, 28, 20, 28));
        content.add(header, BorderLayout.NORTH);
        content.add(body, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        return content;
    }

    /** Packs, centers and shows a fixed-size window. */
    static void show(JFrame window) {
        window.pack();
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}

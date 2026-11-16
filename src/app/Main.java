package src.app;

import src.view.LoginWindow;

import javax.swing.SwingUtilities;

/**
 * Entry point of the app: opens the login window.
 * The old console test lives in {@link LoginConsoleTest}.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginWindow::open);
    }
}

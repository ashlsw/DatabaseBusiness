package gui;

import dao.UserDao;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame implements ActionListener {

    private final OnLoginSuccess onLoginSuccess;
    private final UserDao userDao;
    private final JTextField phoneField;
    private final JPasswordField passwordField;
    private final JButton loginBtn;

    public LoginFrame(OnLoginSuccess onLoginSuccess, UserDao userDao) {
        this.onLoginSuccess = onLoginSuccess;
        this.userDao = userDao;
        setTitle("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        loginBtn = new JButton("Login");
        loginBtn.addActionListener(this);

        // Add components to the main panel using GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        // Make the button span across two columns
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(loginBtn, gbc);

        setContentPane(mainPanel);
        // Adjust frame size to fit its contents
        pack();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            String phone = phoneField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);
             User user = userDao.validateCredentials(phone, password);
            if (user != null) {
                onLoginSuccess.onSuccess(user);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid phone or password");
            }

            // Clear password field after login attempt
            passwordField.setText("");
        }
    }

    public interface OnLoginSuccess {
        void onSuccess(User user);
    }
}

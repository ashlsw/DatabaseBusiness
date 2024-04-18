package gui;

import dao.BaseDao;
import dao.UserDao;
import models.User;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class UsersPanel extends JPanel implements ActionListener {
    private final DefaultTableModel tableModel;
    private final JTextField firstNameField;
    private final JTextField lastNameField;
    private final JTextField phoneField;
    private final JTextField passwordField;
    private final JPanel addPanel;
    private final DeletePanel<User> deletePanel;
    private UserDao userDao;
    private List<User> panelData;

    public UsersPanel(UserDao userDao) {
        try {
            this.userDao = userDao;
            panelData = userDao.all();
        } catch (Exception e) {
            e.printStackTrace();
        }
        deletePanel = new DeletePanel<>(panelData, item -> {
            try {
                Integer id = userDao.delete(item.getUserID());
                panelData = panelData.stream().filter(user -> user.getUserID() != id).collect(Collectors.toList());
                populateTable();
                return id;
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to delete user: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE
                );
            }

            return null;
        });

        setLayout(new BorderLayout());

        // Panel to hold add user form and button
        addPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel firstNameLabel = new JLabel("First Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        addPanel.add(firstNameLabel, gbc);

        firstNameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        addPanel.add(firstNameField, gbc);

        JLabel lastNameLabel = new JLabel("Last Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        addPanel.add(lastNameLabel, gbc);

        lastNameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        addPanel.add(lastNameField, gbc);

        JLabel phoneLabel = new JLabel("Phone:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        addPanel.add(phoneLabel, gbc);

        phoneField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        addPanel.add(phoneField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        addPanel.add(passwordLabel, gbc);

        passwordField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        addPanel.add(passwordField, gbc);

        JButton addButton = new JButton("Add User");
        addButton.setActionCommand("save");
        addButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 4;
        // Make the button span across two columns
        gbc.gridwidth = 2;
        addPanel.add(addButton, gbc);

        // Table to display users
        tableModel = new DefaultTableModel(new Object[]{"UserID", "First Name", "Last Name", "Phone", "Password"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        Border emptyBorder = BorderFactory.createEmptyBorder(20, 0, 0, 0);
        scrollPane.setBorder(emptyBorder);

        // Populate user data from database
        populateTable();


        // Add components to the panel
        add(addPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(deletePanel, BorderLayout.SOUTH);


        tableModel.addTableModelListener(event -> {
            if (event.getType() == TableModelEvent.UPDATE) {
                int row = event.getFirstRow();
                int column = event.getColumn();
                TableModel model = (TableModel) event.getSource();

                // Get the updated value
                Object data = model.getValueAt(row, column);
                // Check if the updated column is one of the editable columns (e.g., First Name, Last Name, Phone, Password)
                if (column >= 1 && column <= 4) {
                    // Get the user ID from the first column
                    int userID = (int) model.getValueAt(row, 0);

                    // Update the corresponding user object
                    User updatedUser = panelData.get(row);
                    switch (column) {
                        case 1:
                            updatedUser.setFirstName((String) data);
                            break;
                        case 2:
                            updatedUser.setLastName((String) data);
                            break;
                        case 3:
                            updatedUser.setPhone(BaseDao.cleanPhoneNumber((String) data));
                            break;
                        case 4:
                            updatedUser.setPassword((String) data);
                            break;
                        default:
                            break;
                    }

                    // Update the user in the database
                    try {
                        userDao.update(updatedUser);
                        JOptionPane.showMessageDialog(this, "User updated successful!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle update error
                        JOptionPane.showMessageDialog(this, "Failed to update user: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        revalidate();
        repaint();
    }

    private void populateTable() {
        tableModel.setRowCount(0);
        panelData = userDao.all();
        for (User user : panelData) {
            tableModel.addRow(new Object[]{user.getUserID(), user.getFirstName(), user.getLastName(), user.getPhone(), user.getPassword()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equalsIgnoreCase("save")) {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = passwordField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User(firstName, lastName, phone, password);

            try {
                user = userDao.add(user);
                panelData.add(user);
                deletePanel.add(user);

                populateTable();

                // Clear the form fields
                firstNameField.setText("");
                lastNameField.setText("");
                phoneField.setText("");
                passwordField.setText("");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to add user: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE
                );
            }

        }
    }
}

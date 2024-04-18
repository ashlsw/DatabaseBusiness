package gui;

import dao.BaseDao;
import dao.SupplierDao;
import models.Supplier;

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

public class SuppliersPanel extends JPanel implements ActionListener {
    private final DefaultTableModel tableModel;
    private final JTextField nameField;
    private final JTextField phoneField;
    private final JTextField emailField;
    private final JPanel addPanel;
    private final DeletePanel<Supplier> deletePanel;
    private SupplierDao supplierDao;
    private List<Supplier> panelData;

    public SuppliersPanel(SupplierDao supplierDao) {
        try {
            this.supplierDao = supplierDao;
            panelData = supplierDao.all();
        } catch (Exception e) {
            e.printStackTrace();
        }
        deletePanel = new DeletePanel<>(panelData, item -> {
            try {
                Integer id = supplierDao.delete(item.getSupplierID());
                panelData = panelData.stream().filter(supplier -> supplier.getSupplierID() != id).collect(Collectors.toList());
                populateTable();
                return id;
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to delete supplier: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE
                );
            }

            return null;
        });

        setLayout(new BorderLayout());

        // Panel to hold add supplier form and button
        addPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        addPanel.add(nameLabel, gbc);

        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        addPanel.add(nameField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        addPanel.add(emailLabel, gbc);


        emailField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        addPanel.add(emailField, gbc);

        JLabel phoneLabel = new JLabel("Phone:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        addPanel.add(phoneLabel, gbc);

        phoneField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        addPanel.add(phoneField, gbc);


        JButton addButton = new JButton("Add Supplier");
        addButton.setActionCommand("save");
        addButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 4;
        // Make the button span across two columns
        gbc.gridwidth = 2;
        addPanel.add(addButton, gbc);

        // Table to display suppliers
        tableModel = new DefaultTableModel(new Object[]{"SupplierID", "Name", "Email", "Phone"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        Border emptyBorder = BorderFactory.createEmptyBorder(20, 0, 0, 0);
        scrollPane.setBorder(emptyBorder);

        // Populate supplier data from database
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
                if (column >= 1 && column <= 3) {
                    int supplierID = (int) model.getValueAt(row, 0);

                    // Update the corresponding supplier object
                    Supplier supplier = panelData.get(row);
                    switch (column) {
                        case 1:
                            supplier.setName((String) data);
                            break;
                        case 2:
                            supplier.setEmail((String) data);
                            break;
                        case 3:
                            supplier.setPhone(BaseDao.cleanPhoneNumber((String) data));
                            break;
                        default:
                            break;
                    }

                    // Update the supplier in the database
                    try {
                        supplierDao.update(supplier);
                        JOptionPane.showMessageDialog(this, "Supplier updated successful!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle update error
                        JOptionPane.showMessageDialog(this, "Failed to update supplier: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void populateTable() {
        tableModel.setRowCount(0);
        panelData = supplierDao.all();
        for (Supplier supplier : panelData) {
            tableModel.addRow(new Object[]{supplier.getSupplierID(), supplier.getName(), supplier.getEmail(), supplier.getPhone(),});
        }
    }



    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equalsIgnoreCase("save")) {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Supplier supplier = new Supplier(
                    name, email, phone
            );

            try {
                supplier = supplierDao.add(supplier);
                panelData.add(supplier);
                deletePanel.add(supplier);

                populateTable();

                // Clear the form fields
                nameField.setText("");
                phoneField.setText("");
                emailField.setText("");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to add supplier: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE
                );
            }

        }
    }
}

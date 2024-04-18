package gui;

import dao.ProductDao;
import models.Product;

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

public class ProductsPanel extends JPanel implements ActionListener {
    private final DefaultTableModel tableModel;
    private final JTextField nameField;
    private final JTextField descriptionField;
    private final JTextField priceField;
    private final JTextField quantityInStockField;
    private final JPanel addPanel;
    private final DeletePanel<Product> deletePanel;
    private ProductDao productsDao;
    private List<Product> panelData;

    public ProductsPanel(ProductDao productsDao) {
        try {
            this.productsDao = productsDao;
            panelData = productsDao.all();
        } catch (Exception e) {
            e.printStackTrace();
        }
        deletePanel = new DeletePanel<>(panelData, item -> {
            try {
                Integer id = productsDao.delete(item.getProductID());
                panelData = panelData.stream().filter(product -> product.getProductID() != id).collect(Collectors.toList());
                populateTable();
                return id;
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to delete product: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE
                );
            }

            return null;
        });

        setLayout(new BorderLayout());

        // Panel to hold add product form and button
        addPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel nameLabel = new JLabel("First Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        addPanel.add(nameLabel, gbc);

        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        addPanel.add(nameField, gbc);

        JLabel descriptionLabel = new JLabel("Last Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        addPanel.add(descriptionLabel, gbc);

        descriptionField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        addPanel.add(descriptionField, gbc);

        JLabel priceLabel = new JLabel("price:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        addPanel.add(priceLabel, gbc);

        priceField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        addPanel.add(priceField, gbc);

        JLabel quantityInStockLabel = new JLabel("quantityInStock:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        addPanel.add(quantityInStockLabel, gbc);

        quantityInStockField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        addPanel.add(quantityInStockField, gbc);

        JButton addButton = new JButton("Add product");
        addButton.setActionCommand("save");
        addButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 4;
        // Make the button span across two columns
        gbc.gridwidth = 2;
        addPanel.add(addButton, gbc);

        // Table to display products
        tableModel = new DefaultTableModel(new Object[]{"ProductID", "Name", "Description", "Price", "Quantity In Stock"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        Border emptyBorder = BorderFactory.createEmptyBorder(20, 0, 0, 0);
        scrollPane.setBorder(emptyBorder);

        // Populate product data from database
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
                // Check if the updated column is one of the editable columns (e.g., First Name, Last Name, price, quantityInStock)
                if (column >= 1 && column <= 4) {
                    // Get the product ID from the first column
                    int productID = (int) model.getValueAt(row, 0);

                    // Update the corresponding product object
                    Product updatedproduct = panelData.get(row);
                    switch (column) {
                        case 1:
                            updatedproduct.setName((String) data);
                            break;
                        case 2:
                            updatedproduct.setDescription((String) data);
                            break;
                        case 3:
                            updatedproduct.setPrice(Double.parseDouble((String) data));
                            break;
                        case 4:
                            updatedproduct.setQuantityInStock(Integer.parseInt((String) data));
                            break;
                        default:
                            break;
                    }

                    // Update the product in the database
                    try {
                        productsDao.update(updatedproduct);
                        JOptionPane.showMessageDialog(this, "Product updated successful!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle update error
                        JOptionPane.showMessageDialog(this, "Failed to update product: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void populateTable() {
        tableModel.setRowCount(0);
        panelData = productsDao.all();
        for (Product product : panelData) {
            tableModel.addRow(new Object[]{product.getProductID(), product.getName(), product.getDescription(), product.getPrice(), product.getQuantityInStock()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equalsIgnoreCase("save")) {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();

            if (name.isEmpty() || description.isEmpty() || priceField.getText().trim().isEmpty() || quantityInStockField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Double price = Double.parseDouble(priceField.getText().trim());
            Integer quantityInStock = Integer.parseInt(quantityInStockField.getText().trim());

            Product product = new Product(name, description, price, quantityInStock);

            try {
                product = productsDao.add(product);
                panelData.add(product);
                deletePanel.add(product);

                populateTable();

                // Clear the form fields
                nameField.setText("");
                descriptionField.setText("");
                priceField.setText("");
                quantityInStockField.setText("");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to add product: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE
                );
            }

        }
    }
}

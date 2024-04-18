package gui;

import dao.ProductDao;
import dao.SaleDao;
import dao.UserDao;
import models.Product;
import models.Sale;
import models.SalesProduct;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SalesAddPanel extends JPanel implements ActionListener {
    private final JComboBox<User> customerComboBox;
    private final JComboBox<Product> productComboBox;
    private final JTextField quantityField;
    private final JButton addButton;
    private final JPanel addPanel;
    private final JPanel productsPanel;
    private final JButton submitButton;
    private final SaleDao saleDao;
    private final UserDao userDao;
    private final ProductDao productDao;
    private final User currentUser;
    private final DefaultTableModel productsTableModel;
    private final SalesPanel salesPanel;
    private List<SalesProduct> selectedProducts;

    public SalesAddPanel(UserDao userDao, ProductDao productDao, SaleDao saleDao, User currentUser, SalesPanel salesPanel) {
        this.userDao = userDao;
        this.productDao = productDao;
        this.saleDao = saleDao;
        this.salesPanel = salesPanel;
        this.selectedProducts = new ArrayList<>();
        this.currentUser = currentUser;

        setLayout(new BorderLayout());

        // Panel to hold add sale form
        addPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel customerLabel = new JLabel("Customer:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        addPanel.add(customerLabel, gbc);

        customerComboBox = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 0;
        addPanel.add(customerComboBox, gbc);

        JLabel productLabel = new JLabel("Product:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        addPanel.add(productLabel, gbc);

        productComboBox = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 1;
        addPanel.add(productComboBox, gbc);

        JLabel quantityLabel = new JLabel("Quantity:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        addPanel.add(quantityLabel, gbc);

        quantityField = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 2;
        addPanel.add(quantityField, gbc);

        addButton = new JButton("Add Product");
        addButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        addPanel.add(addButton, gbc);

        // Panel to display selected products
        productsPanel = new JPanel();
        productsPanel.setLayout(new BorderLayout());

        // Create a table to display selected products
        productsTableModel = new DefaultTableModel(new Object[]{"Product Name", "Quantity"}, 0);
        JTable productsTable = new JTable(productsTableModel);
        JScrollPane productsScrollPane = new JScrollPane(productsTable);
        productsPanel.add(productsScrollPane, BorderLayout.CENTER);

        add(addPanel, BorderLayout.NORTH);
        add(productsPanel, BorderLayout.CENTER);

        // Submit button panel
        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        submitPanel.add(submitButton);
        add(submitPanel, BorderLayout.SOUTH); // Add the submit panel to the bottom

        populateComboBoxes();
    }

    private void populateComboBoxes() {
        try {
            java.util.List<User> customers = userDao.all();
            for (User customer : customers) {
                customerComboBox.addItem(customer);
            }

            java.util.List<Product> products = productDao.all();
            for (Product product : products) {
                productComboBox.addItem(product);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addProductToTable();
        } else if (e.getSource() == submitButton) {
            submitSale();
        }
    }

    private void addProductToTable() {
        try {
            User selectedCustomer = (User) customerComboBox.getSelectedItem();
            Product selectedProduct = (Product) productComboBox.getSelectedItem();
            int quantity = Integer.parseInt(quantityField.getText());
            for (SalesProduct p : selectedProducts
            ) {
                if (p.getProduct().getProductID() == selectedProduct.getProductID()) {
                    p.setQuantitySold(quantity);
                    return;
                }

            }
            if (quantity > selectedProduct.getQuantityInStock()) {
                JOptionPane.showMessageDialog(this, "Invalid quantity value, items in stock is " + selectedProduct.getQuantityInStock(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Add the selected product to the products table
            productsTableModel.addRow(new Object[]{selectedProduct.getName(), quantity});
            selectedProducts.add(new SalesProduct(selectedProduct, quantity));

            // Clear input fields
            quantityField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity value",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitSale() {
        try {
            User selectedCustomer = (User) customerComboBox.getSelectedItem();

            // Create a new sale object
            Sale newSale = new Sale(selectedCustomer, LocalDateTime.now(), currentUser.getUserID(), selectedProducts);
            newSale = saleDao.add(newSale);

            selectedProducts = new ArrayList<>();
            productsTableModel.setRowCount(0);

            JOptionPane.showMessageDialog(this, "Sale created successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            salesPanel.saleAdded();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to submit sale: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

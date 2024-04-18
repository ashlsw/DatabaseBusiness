package gui;


import dao.ProductDao;
import dao.SaleDao;
import dao.UserDao;
import models.Sale;
import models.SalesProduct;
import models.User;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class SalesPanel extends JPanel {
    private final JPanel salesPanel;
    private final SaleDao saleDao;
    private final UserDao userDao;
    private final ProductDao productDao;
    private final User currentUser;
    private final DeletePanel<Sale> deletePanel;
    private List<Sale> sales;

    public SalesPanel(UserDao userDao, ProductDao productDao, SaleDao saleDao, User currentUser) {
        this.userDao = userDao;
        this.productDao = productDao;
        this.saleDao = saleDao;
        this.currentUser = currentUser;
        try {
            sales = saleDao.all();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        deletePanel = new DeletePanel<>(sales, item -> {
            try {
                Integer id = saleDao.delete(item.getSaleID());
                sales = sales.stream().filter(sale -> sale.getSaleID() != item.getSaleID()).collect(Collectors.toList());

                populateSalesPanel();

                return id;
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to delete product: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE
                );
            }

            return null;
        });

        setLayout(new BorderLayout());


        // Panel to display sales
        salesPanel = new JPanel();
        salesPanel.setLayout(new BoxLayout(salesPanel, BoxLayout.Y_AXIS));


        add(salesPanel, BorderLayout.CENTER);
        add(deletePanel, BorderLayout.SOUTH);


        // Populate existing sales
        populateSalesPanel();
    }


    private void populateSalesPanel() {
        try {
            for (Sale sale : sales) {
                JPanel saleEntryPanel = new JPanel(new BorderLayout());
                saleEntryPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                JLabel saleInfoLabel = new JLabel("Date: " + sale.getSaleDate() +
                        ", Customer: " + (sale.getCustomer() != null ? sale.getCustomer().getFirstName() + " " + sale.getCustomer().getLastName() : "") +
                        ", Total Cost: $" + sale.totalCost()
                );

                saleEntryPanel.add(saleInfoLabel, BorderLayout.NORTH);

                JTable salesProductTable = new JTable(new DefaultTableModel(new Object[]{"Product Name", "Quantity Sold"}, 0));
                DefaultTableModel model = (DefaultTableModel) salesProductTable.getModel();
                model.addTableModelListener(event -> {
                    if (event.getType() == TableModelEvent.UPDATE) {
                        int row = event.getFirstRow();
                        int column = event.getColumn();

                        // Get the updated value
                        Object data = model.getValueAt(row, column);
                        if (column == 1) {
                            try {
                                sale.getProducts().get(row).setQuantitySold(Integer.parseInt((String) data));
                                saleDao.update(sale);
                            } catch (Exception e) {

                            }
                        }
                    }
                });
                for (SalesProduct salesProduct : sale.getProducts()) {
                    model.addRow(new Object[]{salesProduct.getProduct().getName(), salesProduct.getQuantitySold()});
                }

                // Wrap the table in a scroll pane
                JScrollPane scrollPane = new JScrollPane(salesProductTable);
                // Set the preferred size of the scroll pane to match the preferred size of the table
                scrollPane.setPreferredSize(salesProductTable.getPreferredSize());
                saleEntryPanel.add(scrollPane, BorderLayout.CENTER);

                salesPanel.add(saleEntryPanel);

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load sales: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);

        }

    }

    public void saleAdded() {
        salesPanel.removeAll();

        populateSalesPanel();
        revalidate();
        repaint();
    }

}

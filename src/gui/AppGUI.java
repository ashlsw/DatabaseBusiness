package gui;


import dao.ProductDao;
import dao.SaleDao;
import dao.SupplierDao;
import dao.UserDao;
import models.User;

import javax.swing.*;


public class AppGUI
        extends JFrame {
    final User currenUser;

    public AppGUI(User currenUser) throws Exception {
        this.currenUser = currenUser;
        JTabbedPane tbPanels = new JTabbedPane(JTabbedPane.TOP);
        UserDao userDao = new UserDao();
        SupplierDao supplierDao = new SupplierDao();
        ProductDao productDao = new ProductDao();
        SaleDao saleDao = new SaleDao();

        UsersPanel userPanel = new UsersPanel(userDao);
        tbPanels.add("Users", userPanel);

        SuppliersPanel suppliersPanel = new SuppliersPanel(supplierDao);
        tbPanels.add("Suppliers", suppliersPanel);

        ProductsPanel productsPanel = new ProductsPanel(productDao);
        tbPanels.add("Products", productsPanel);

        SalesPanel salesPanel = new SalesPanel(userDao, productDao, saleDao, this.currenUser);
        tbPanels.add("Sales", salesPanel);

        SalesOverviewPanel overviewPanel = new SalesOverviewPanel(saleDao);
        tbPanels.add("Sales Overview", overviewPanel);

        SalesAddPanel recordSale = new SalesAddPanel(userDao, productDao, saleDao, this.currenUser, salesPanel);
        tbPanels.add("New Sale", recordSale);

        this.add(tbPanels);

    }

    public User getCurrenUser() {
        return currenUser;
    }


}

package gui;


import dao.SaleDao;
import models.Stat;

import javax.swing.*;
import java.awt.*;

public class SalesOverviewPanel extends JPanel {
    private final JPanel salesPanel;
    private final SaleDao saleDao;

    public SalesOverviewPanel(SaleDao saleDao) {
        this.saleDao = saleDao;


        setLayout(new BorderLayout());


        // Panel to display sales
        salesPanel = new JPanel();
        salesPanel.setLayout(new BoxLayout(salesPanel, BoxLayout.Y_AXIS));

        Stat[] stats = saleDao.getSalesStat();
        String[] labels = new String[12];
        double[] values = new double[12];
        for (int i = 0; i < 12; i++) {
            labels[i] = stats[i].getMonth();
            values[i] = stats[i].getAmount();
        }
        add(new BarChartPanel(
                values, labels, "Sale Amount Overview ($)"
        ), BorderLayout.CENTER);

    }


}

package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DeletePanel<T> extends JPanel implements ActionListener {
    private final JComboBox<T> comboBox;
    private final OnDeleteAction<T> deleteItem;
    private final DefaultComboBoxModel<T> comboBoxModel;

    public DeletePanel(List<T> data, OnDeleteAction<T> deleteItem) {
        this.deleteItem = deleteItem;
        this.comboBoxModel = new DefaultComboBoxModel<>();

        for (T item : data) {
            comboBoxModel.addElement(item);
        }


        comboBox = new JComboBox<>(comboBoxModel);

        setLayout(new BorderLayout());
        add(comboBox, BorderLayout.CENTER);

        JButton deleteButton = new JButton("Delete Selected Item");
        deleteButton.setBackground(new Color(213, 50, 50));

        deleteButton.setActionCommand("delete");
        deleteButton.addActionListener(this);
        add(deleteButton, BorderLayout.EAST);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equalsIgnoreCase("delete")) {

            T selectedItem = comboBox.getItemAt(comboBox.getSelectedIndex());
            if (selectedItem != null) {
                int confirmed = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this item?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);
                if (confirmed == JOptionPane.YES_OPTION) {
                    Integer deleteResult = deleteItem.delete(selectedItem);
                    if (deleteResult != null && deleteResult > 0) {
                        comboBox.removeItem(selectedItem);
                        JOptionPane.showMessageDialog(this, selectedItem + " deleted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete " + selectedItem);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an item to delete.");
            }
        }
    }

    public T getSelectedItem() {
        return (T) comboBox.getSelectedItem();
    }

    public void add(T item) {
        comboBoxModel.addElement(item);
    }

    public interface OnDeleteAction<T> {
        Integer delete(T item);
    }
}

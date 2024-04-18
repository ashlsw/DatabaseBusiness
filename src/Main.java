import dao.BaseDao;
import dao.UserDao;
import gui.AppGUI;
import gui.LoginFrame;
import models.User;

import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws Exception {
        User currenUser = new User( -1, "","","","");

        try {
            UserDao userDao = new UserDao();
        } catch (SQLException e) {
            BaseDao.setUp();
        }

        // Create the main application frame
        AppGUI frm = new AppGUI(currenUser);
        frm.setSize(800, 600);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and display the login frame
        LoginFrame loginFrame = new LoginFrame((user) -> {
            currenUser.setFirstName(user.getFirstName());
            currenUser.setPassword(user.getPassword());
            currenUser.setPhone(user.getPhone());
            currenUser.setUserID(user.getUserID());
            frm.setVisible(true);
            JOptionPane.showMessageDialog(frm, "Login successful!");
        }, new UserDao());

        loginFrame.setSize(800, 600);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setVisible(true);
    }
}


package main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

public class Panel  extends JFrame{
    private JLabel title;
    private JLabel entityLabel;
    private JComboBox entityCB;
    private JLabel instanceLabel;
    private JComboBox instanceCB;
    private JLabel featureLabel;
    private JComboBox featureCB;
    private JTextArea resultTA;
    private JPanel Panel;
    private JButton btnSearch;

    public Panel(){

        super("SIRW - Trabajo 1");

        setContentPane(Panel);

        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Estamos buscando XD");
            }
        });
    }

}

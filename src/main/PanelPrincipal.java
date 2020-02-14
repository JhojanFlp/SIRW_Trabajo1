package main;

import javafx.scene.layout.Pane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

public class PanelPrincipal extends JFrame {
    private JButton consulta1Button;
    private JPanel PanelPrincipal;
    private JButton consulta2Button;
    private JButton consulta3Button;
    private JButton consulta4Button;
    private JPanel panelConsultas;

    public PanelPrincipal(){
        super("SIRW - Trabajo 1");

        setContentPane(PanelPrincipal);

        consulta1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new Panel();
                frame.setSize(500, 500);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

    }
}

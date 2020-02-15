package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

import static main.Main.consultaEnTodasLasBD;
import static main.Main.prefijos;

public class Panel3 extends JFrame {
    private JPanel Panel3;
    private JComboBox entityCB;
    private JComboBox featureCB;
    private JTextArea txtA1;
    private JComboBox filtrarCB;
    private JTextField filtro;
    private JTextArea textArea1;
    private JButton buscarBtn;
    private JButton filtarBtn;

    public Panel3(){

        super("SIRW - Trabajo 1 (Consulta #3)");
        setContentPane(Panel3);

        String consulta = prefijos + "select ?a \n" +
                "where {\n" +
                "\t?a a ?b\n" +
                "}\n";

        //ejemplo de consulta
        LinkedList<HashMap<String, String>> a = consultaEnTodasLasBD(consulta, new String[]{"a"});
        for(HashMap r:a){

            this.entityCB.addItem(r.get("a"));
        }



        buscarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(entityCB.getSelectedItem().toString());

            }
        });
    }

}

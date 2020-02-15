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

        // Get values and fill ComboBox
        String consulta = prefijos + "select ?a \n" +
                "where {\n" +
                "\t?a a ?class.\n" +
                "\t?class a owl:Class.\n" +
                "\t?a a ?b.\n" +
                "}\n";

        String consultaF = prefijos + "select ?property \n" +
                "where {\n" +
                "\t?a ?property ?b.\n" +
                "}\n";


        LinkedList<HashMap<String, String>> a = consultaEnTodasLasBD(consulta, new String[]{"a"});
        LinkedList<HashMap<String, String>> b = consultaEnTodasLasBD(consultaF, new String[]{"b"});

        for(HashMap r:a){
            this.entityCB.addItem(r.get("a"));
        }

        for(HashMap r:b) {
            this.featureCB.addItem(r.get("b"));
        }


        buscarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(entityCB.getSelectedItem().toString());
                String ent = entityCB.getSelectedItem().toString();
                String fea = featureCB.getSelectedItem().toString();
                exeConsulta1(ent, fea);
            }
        });
    }

    //Funciones para consultar
    public static String exeConsulta1 (String entidad, String atributo){
        String consulta1 = prefijos + "SELECT (COUNT(DISTINCT ?entity) AS ?conteo\n" +
                "where {\n" +
                "\t?entity a " + entidad + "\n"+
                "\t?entity " + atributo + " ?data.\n" +
                "}\n";

        LinkedList<HashMap<String, String>> c1 = consultaEnTodasLasBD(consulta1, new String[]{"a"});
    }

}

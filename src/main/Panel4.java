package main;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;

import static main.Main.consultaEnTodasLasBD;
import static main.Main.prefijos;

public class Panel4 extends JFrame{
    private JComboBox ent1CB;
    private JComboBox inst1CB;
    private JComboBox ent2CB;
    private JComboBox inst2CB;
    private JButton directaBtn;
    private JTextArea relacionTA;
    private JButton indirectaBtn;
    private JPanel Panel4;

    public Panel4(){
        super("SIRW - Trabajo 1 (Consulta #3)");
        setContentPane(Panel4);

        String consulta = prefijos + "select distinct ?class \n" +
                "where {\n" +
                "\t{?a a ?class.\n" +
                "\t?class a owl:Class.}\n" +
                "union {\n" +
                "\t?aux a owl:Class.\n" +
                "\t?class rdfs:subClassOf ?aux. }" +
                "}\n";

        LinkedList<HashMap<String, String>> a = consultaEnTodasLasBD(consulta, new String[]{"class"});
        for(HashMap r:a){
            this.ent1CB.addItem(r.get("class"));
            this.ent2CB.addItem(r.get("class"));
        }

        ent1CB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ent = ent1CB.getSelectedItem().toString();
                String consulta = prefijos + "select distinct ?instance\n" +
                        "where{\n" +
                        "\t?instance a <" + ent + "> \n" +
                        "}";
                LinkedList<HashMap<String, String>> a = consultaEnTodasLasBD(consulta, new String[]{"instance"});
                inst1CB.removeAllItems();
                for (HashMap r : a) {
                    inst1CB.addItem(r.get("instance"));
                }
            }
        });

        ent2CB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ent = ent2CB.getSelectedItem().toString();
                String consulta = prefijos + "select distinct ?instance\n" +
                        "where{\n" +
                        "\t?instance a <" + ent + "> \n" +
                        "}";
                LinkedList<HashMap<String, String>> a = consultaEnTodasLasBD(consulta, new String[]{"instance"});
                inst2CB.removeAllItems();
                for (HashMap r : a) {
                    inst2CB.addItem(r.get("instance"));
                }
            }
        });

        directaBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inst1 = inst1CB.getSelectedItem().toString();
                String inst2 = inst2CB.getSelectedItem().toString();
                String resp = directa(inst1, inst2);
                relacionTA.setText(resp);
            }
        });

        indirectaBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inst1 = inst1CB.getSelectedItem().toString();
                String inst2 = inst2CB.getSelectedItem().toString();
                String resp = indirecta(inst1, inst2);
                relacionTA.setText(resp);
            }
        });
    }

    public String directa(String i1, String i2){
        String resp = "";

        String consulta = prefijos + "select distinct ?p\n" +
                "where {\n" +
                "\t{ <" + i1 + "> ?p <" + i2 + "> .\n" +
                "\t?p a owl:ObjectProperty. }\n"+
                "\tunion {\n" +
                "\t<" + i2 + "> ?p <" + i1 + "> .\n" +
                "\t?p a owl:ObjectProperty. }\n"+
                "}\n";

        LinkedList<HashMap<String, String>> a = consultaEnTodasLasBD(consulta, new String[]{"p"});
        if(a.size() == 0) {
            resp += "No hay relacion \t:(";
        } else{
            resp += "Si hay relacion \t:)";
        }
        return resp;
    }

    public String indirecta(String i1, String i2){
        String resp = "";

        String consulta = prefijos + "select distinct ?ent2\n" +
                "where {\n" +
                "\t<" + i1 + "> ?p ?ent2 .\n" +
                "\t?ent2 ?a <" + i2 + "> .\n" +
                "\t?p a owl:ObjectProperty.\n"+
                "\t?a a owl:ObjectProperty.\n"+
                "}\n";

        LinkedList<HashMap<String, String>> a = consultaEnTodasLasBD(consulta, new String[]{"ent2"});
        if(a.size() == 0) {
            resp += "No hay relacion \t:(";
        } else{
            resp += "Si hay relacion \t:)";
        }
        return resp;
    }
}

package main;

import org.apache.jena.base.Sys;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
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
    private JTextArea txtA2;
    private JButton buscarBtn;
    private JButton filtarBtn;

    public Panel3(){

        super("SIRW - Trabajo 1 (Consulta #3)");
        setContentPane(Panel3);

        // Get values and fill ComboBox
        String consulta = prefijos + "select distinct ?class \n" +
                "where {\n" +
                "\t{?a a ?class.\n" +
                "\t?class a owl:Class.}\n" +
                "union {\n" +
                "\t?aux a owl:Class.\n" +
                "\t?class rdfs:subClassOf ?aux. }" +
                "}\n";

        String consultaF = prefijos + "select distinct ?property \n" +
                "where {\n" +
                "\t?a ?property ?b.\n" +
                "\t?property a owl:DatatypeProperty.\n" +
                "}\n";


        LinkedList<HashMap<String, String>> a = consultaEnTodasLasBD(consulta, new String[]{"class"});
        LinkedList<HashMap<String, String>> b = consultaEnTodasLasBD(consultaF, new String[]{"property"});

        for(HashMap r:a){
            this.entityCB.addItem(r.get("class"));
        }

        for(HashMap r:b) {
            this.featureCB.addItem(r.get("property"));
        }


        buscarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ent = entityCB.getSelectedItem().toString();
                String fea = featureCB.getSelectedItem().toString();
                // Conteo
                String c = exeConsulta1(ent, fea);
                txtA1.setText(c);

            }
        });

        filtarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ent = entityCB.getSelectedItem().toString();
                String fea = featureCB.getSelectedItem().toString();
                String opt = filtrarCB.getSelectedItem().toString();
                String fil = filtro.getText().toString();
                // Conteo
                String c = exeConsulta2(ent, fea, opt, fil);
                txtA2.setText(c);
            }
        });
    }

    //Funciones para consultar
    public String exeConsulta1(String entidad, String atributo){

        String consulta1 = prefijos + "select (count(distinct ?entity) as ?conteo)\n" +
                "where {\n" +
                "\t?entity a <" + entidad + ">.\n"+
                "\t?entity <" + atributo + "> ?data.\n" +
                "}\n";

        LinkedList<HashMap<String, String>> c1 = consultaEnTodasLasBD(consulta1, new String[]{"conteo"});

        int respuesta = 0;
        String resp = "";
        DecimalFormat f = new DecimalFormat("##.000");

        for(HashMap r:c1){
            char v = r.get("conteo").toString().charAt(0);
            respuesta += Integer.parseInt(String.valueOf(v));
        }

        resp += "- Conteo = " + String.valueOf(respuesta) + "\n";

        // Numérico o String?
        String consulta2 = prefijos + "select ?range\n" +
                "where {\n" +
                "\t<" + atributo +"> rdfs:range ?range.\n" +
                "}\n";

        LinkedList<HashMap<String, String>> range = consultaEnTodasLasBD(consulta2, new String[]{"range"});
        String auxRange = "";

        for(HashMap r:range){
            auxRange = r.get("range").toString();
        }

        this.filtrarCB.removeAllItems();

        if(auxRange.equals("http://www.w3.org/2001/XMLSchema#string")){
            // Es string
            String consulta3 = prefijos + "select ?data\n" +
                    "where {\n" +
                    "\t?entity a <" + entidad + ">.\n"+
                    "\t?entity <" + atributo + "> ?data.\n" +
                    "}\n";

            LinkedList<HashMap<String, String>> c3 = consultaEnTodasLasBD(consulta3, new String[]{"data"});
            double prom = 0;
            for(HashMap r:c3){
                prom += r.get("data").toString().length();
            }
            prom = prom / c3.size();

            String r1 = String.valueOf(f.format(prom));
            resp += "- Promedio de tamaño de cadenas = " + r1;

            // Set items
            this.filtrarCB.addItem("contiene");

        } else{
            System.out.println("max");
            String consulta3max = prefijos + "select (str(?maximo) as ?max) (str(?minimo) as ?min) (str(?promedio) as ?prom)\n" +
                    "where { \n" +
                    "{ select (max(?data) as ?maximo) (min(?data) as ?minimo) (avg(?data) as ?promedio)\n" +
                    "where {\n" +
                    "\t?entity a <" + entidad + ">.\n"+
                    "\t?entity <" + atributo + "> ?data.\n" +
                    "\t} }\n" +
                    "}\n";
            System.out.println(consulta3max);
            LinkedList<HashMap<String, String>> c3 = consultaEnTodasLasBD(consulta3max, new String[]{"max", "min", "prom"});

            String max = "", min = "", prom = "";
            for(HashMap r:c3){
                max = r.get("max").toString();
                min = r.get("min").toString();
                prom = r.get("prom").toString();
            }

            resp += "- Máximo = " + max + "\n" +
                    "- Mínimo = " + min + "\n" +
                    "- Promedio = " + prom + "\n";

            this.filtrarCB.addItem("mayor que");
            this.filtrarCB.addItem("menor que");
        }


        return resp;
    }

    public static String exeConsulta2 (String entidad, String atributo, String option, String filtro) {

        String operator, consulta1;
        if(option.equals("contiene")){
            operator = "";
        } else if(option.equals("mayor que")){
            operator = ">";
        } else{
            operator = "<";
        }

        if(option.equals("contiene")) {
            consulta1 = prefijos + "select (count(distinct ?entity) as ?conteo)\n" +
                    "where {\n" +
                    "\t?entity a <" + entidad + ">.\n" +
                    "\t?entity <" + atributo + "> ?data\n" +
                    "filter regex (?data, \"" + filtro + "\", \"i\").\n" +
                    "}\n";
        } else{
            consulta1 = prefijos + "select (count(distinct ?entity) as ?conteo)\n" +
                    "where {\n" +
                    "\t?entity a <" + entidad + ">.\n" +
                    "\t?entity <" + atributo + "> ?data\n" +
                    "filter (?data " + operator + " " + filtro + ").\n" +
                    "}\n";
        }

        System.out.println(consulta1);

        LinkedList<HashMap<String, String>> c1 = consultaEnTodasLasBD(consulta1, new String[]{"conteo"});

        int respuesta = 0;
        String resp = "";

        for(HashMap r:c1){
            char v = r.get("conteo").toString().charAt(0);
            respuesta += Integer.parseInt(String.valueOf(v));
        }

        resp += "- Conteo = " + String.valueOf(respuesta) + "\n";

        // Numérico o String?
        String consulta2 = prefijos + "select ?range\n" +
                "where {\n" +
                "\t<" + atributo +"> rdfs:range ?range.\n" +
                "}\n";

        LinkedList<HashMap<String, String>> range = consultaEnTodasLasBD(consulta2, new String[]{"range"});
        String auxRange = "";

        for(HashMap r:range){
            auxRange = r.get("range").toString();
        }

        if(auxRange.equals("http://www.w3.org/2001/XMLSchema#string")){
            // Es string
            String consulta3 = prefijos + "select ?data\n" +
                    "where {\n" +
                    "\t?entity a <" + entidad + ">.\n"+
                    "\t?entity <" + atributo + "> ?data.\n" +
                    "filter regex (?data, \"" + filtro + "\", \"i\").\n" +
                    "}\n";

            LinkedList<HashMap<String, String>> c3 = consultaEnTodasLasBD(consulta3, new String[]{"data"});
            double prom = 0;
            for(HashMap r:c3){
                prom += r.get("data").toString().length();
            }
            prom = prom / c3.size();
            DecimalFormat f = new DecimalFormat("##.000");
            String r1 = String.valueOf(f.format(prom));
            resp += "- Promedio de tamaño de cadenas = " + r1;
        } else{
            // TODO

        }


        return resp;
    }

}

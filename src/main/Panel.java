package main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

import static main.Main.consultaEnTodasLasBD;
import static main.Main.prefijos;

public class Panel  extends JFrame{
    private JLabel title;
    private JComboBox entityCB;
    private JTextArea resultTA;
    private JPanel Panel;
    private JButton consulta1Btn;
    private JLabel entityLbl;
    private JComboBox individualsCB;
    private JButton indirectoBtn;
    private JTextArea indirectoTA;
    private JLabel filLbl;
    private JTextField filtroTF;

    public Panel(){

        super("SIRW - Trabajo 1 (Consulta #1)");
        setContentPane(Panel);

        // Get values and fill ComboBox
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
            this.entityCB.addItem(r.get("class"));
        }

        entityCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ent = entityCB.getSelectedItem().toString();
                String consulta;
                if(filtroTF.getText().trim().equals("")){
                    consulta = prefijos + "select distinct ?instance\n" +
                            "where{\n" +
                            "\t?instance a <" + ent + "> \n" +
                            "}";
                } else {
                    consulta = prefijos + "select distinct ?instance\n" +
                            "where{\n" +
                            "\t?instance a <" + ent + "> . \n" +
                            "\t?instance book:Nombre ?data\n" +
                            "filter regex (?data, \"" + filtroTF.getText().trim() + "\", \"i\").\n" +
                            "}";
                }
                LinkedList<HashMap<String, String>> a = consultaEnTodasLasBD(consulta, new String[]{"instance"});
                individualsCB.removeAllItems();
                for (HashMap r : a) {
                    individualsCB.addItem(r.get("instance"));
                }
            }
        });

        consulta1Btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ent = entityCB.getSelectedItem().toString();
                String ind = individualsCB.getSelectedItem().toString();
                String c = exeConsulta(ind);
                resultTA.setText(c);
            }
        });

        indirectoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ent = entityCB.getSelectedItem().toString();
                String c = exeConsultaIndirecto(ent);
                indirectoTA.setText(c);

            }
        });

    }

    public String exeConsulta(String individual) {

        String resp = "\t*** ATRIBUTOS ***\n";

        String consulta = prefijos + "select ?p ?data \n" +
                "where {\n" +
                "\t <" + individual + "> ?p ?data.\n" +
                "\t?p a owl:DatatypeProperty.\n" +
                "}\n";

        LinkedList<HashMap<String, String>> c = consultaEnTodasLasBD(consulta, new String[]{"p", "data"});

        for(HashMap r:c){
            resp += r.get("p").toString() + " " + r.get("data").toString() + "\n";
        }

        resp += "\n\t*** RELACIONES ***\n";

        String consulta1 = prefijos + "select ?p ?data \n" +
                "where {\n" +
                "\t <" + individual + "> ?p ?data.\n" +
                "\t?p a owl:ObjectProperty.\n" +
                "}\n";

        LinkedList<HashMap<String, String>> c1 = consultaEnTodasLasBD(consulta1, new String[]{"p", "data"});

        for(HashMap r:c1){
            resp += r.get("p").toString() + " " + r.get("data").toString() + "\n";
        }

        resp += "\n\t*** INSTANCIAS EQUIVALENTES ***\n";

        String consulta2 = prefijos + "select ?eq\n" +
                "where {\n" +
                "\t <" + individual + "> owl:sameAs ?eq.\n" +
                "}\n";

        LinkedList<HashMap<String, String>> c2 = consultaEnTodasLasBD(consulta2, new String[]{"eq"});

        for(HashMap r:c2){
            resp += r.get("eq").toString() + "\n";
        }

        return resp;
    }

    public String exeConsultaIndirecto(String ent) {

        String res = "";

        String consulta = prefijos + "select ?instance \n" +
                "where {\n" +
                "\t?entity rdfs:subClassOf <" + ent + "> .\n" +
                "\t?instance a ?entity.\n" +
                "}\n";

        LinkedList<HashMap<String, String>> c = consultaEnTodasLasBD(consulta, new String[]{"instance"});
        for(HashMap r:c){
            res += "* " + r.get("instance").toString() + "\n";
        }

        return res;
    }

}

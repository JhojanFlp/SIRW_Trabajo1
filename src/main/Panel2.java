package main;

import javax.swing.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;

import static main.Main.consultaEnTodasLasBD;
import static main.Main.prefijos;

public class Panel2 extends JFrame {
    private JPanel Panel2;
    private JComboBox entityCB;
    private JComboBox instanceCB;
    private JComboBox featureCB;
    private JTextArea textArea;
    private JButton buscarButton;


    public Panel2() {

        super("SIRW - Trabajo 1 (Consulta #2)");
        setContentPane(Panel2);

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

        for (HashMap r : a) {
            this.entityCB.addItem(r.get("class"));
        }


        // Accion a realizar cuando el JComboBox cambia de item seleccionado.
        entityCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String entidadSeleccionada = entityCB.getSelectedItem().toString();
                String consulta = prefijos + "select distinct ?instance\n" +
                        "where{\n" +
                        "\t?instance a <" + entidadSeleccionada + "> \n" +
                        "}";
                LinkedList<HashMap<String, String>> a = consultaEnTodasLasBD(consulta, new String[]{"instance"});
                instanceCB.removeAllItems();
                for (HashMap r : a) {
                    instanceCB.addItem(r.get("instance"));
                }
            }
        });

        instanceCB.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String instanciaSeleccionada = instanceCB.getSelectedItem().toString();
                String consulta = prefijos + "select distinct ?property\n" +
                        "where{\n" +
                        "\t <" + instanciaSeleccionada + "> ?property ?b.\n" +
                        "\t?property a owl:DatatypeProperty.\n" +
                        "}";
                LinkedList<HashMap<String, String>> a = consultaEnTodasLasBD(consulta, new String[]{"property"});
                featureCB.removeAllItems();
                for (HashMap r : a) {
                    featureCB.addItem(r.get("property"));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String entidad = entityCB.getSelectedItem().toString();
                String instancia = instanceCB.getSelectedItem().toString();
                String atributo = featureCB.getSelectedItem().toString();

                String res = "";
                String consulta = prefijos + "select distinct ?r\n" +
                        "where{\n" +
                        "\t?r a <"+entidad+"> ;\n" +
                        "\t\t <"+atributo+"> ?atr.\n" +
                        "\t\t <"+instancia+"> <"+atributo+"> ?atr.\n" +
                        "filter(?r != <"+instancia+"> )." +
                        "}";
                LinkedList<HashMap<String, String>> a = consultaEnTodasLasBD(consulta, new String[]{"r"});
                for (HashMap r : a) {
                    res = res + r.get("r");
                }

                textArea.setText(res);

            }
        });


    }
}

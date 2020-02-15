package main;

import javax.swing.*;

// Librerías de Java
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

// Librerías de Jena
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.util.FileManager;

public class Main {
    public static InfModel model;
    public static final String prefijos = "prefix dbo: <http://dbpedia.org/ontology/>\n" +
            "prefix dbp: <http://dbpedia.org/property/>\n" +
            "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "prefix owl: <http://www.w3.org/2002/07/owl#>\n" +
            "prefix vocab: <http://35.208.107.33:2020/resource/vocab/> \n" +
            "prefix book: <http://book.org/> \n" +
            "prefix book1: <http://book1.org/#>\n";

    public static void main(String[] args) throws FileNotFoundException{

        // GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new PanelPrincipal();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setSize(500, 100);
                //frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                //Fill to CB Instance
                String[] instances = {"libro", "autor"};
            }
        });

        // Create Model
        Model model1 = ModelFactory.createDefaultModel() ;
        model1.read("Libros.ttl") ;

        //razonador
        Reasoner OWLReasoner = ReasonerRegistry.getOWLReasoner();
        model = ModelFactory.createInfModel(OWLReasoner,model1);

        //validation
        ValidityReport validity = model.validate();
        if (validity.isValid()) {
            System.out.println("OK");
        } else {
            System.out.println("Errores: ");
            for (Iterator i = validity.getReports(); i.hasNext(); ) {
                System.out.println(" - " + i.next());
            }
        }
        String consulta = prefijos + "select ?a ?b\n" +
                "where {\n" +
                "\t?a book:Distribuido_Por ?b\n" +
                "}\n" +
                "limit 10";

        //ejemplo de consulta
        /*LinkedList<HashMap<String, String>> a = consultaEnTodasLasBD(consulta, new String[]{"a","b"});
        for(HashMap r:a){
            System.out.println("a: "+r.get("a")+" b: "+r.get("b"));
        }*/


    }
    public static String nombreEquivalenteEnLaOntologia(String nombre, String nuevaIri){
        String queryString = String.format(
                prefijos +
                "select ?r\n" +
                "where {\n" +
                "{?r owl:equivalentClass %1$s}\n" +
                "union\n" +
                "{?r owl:equivalentProperty %1$s}\n" +
                "filter strstarts(str(?r),str(%2$s))\n" +
                "}",nombre,nuevaIri);
        //System.out.println(queryString);
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query,model);
        String r ="error";
        try {

            ResultSet results = qexec.execSelect();

            while (results.hasNext()){
                QuerySolution soln = results.nextSolution();
                r = soln.get("r").toString();
            }

        } finally {
            qexec.close();

        }
        return r;
    }
    public static String traducirConsultaA(String consulta, String prefijoOriginal, String nuevaIRI){
        String[] consultaPartida = consulta.split(" ");
        for (int i =0; i<consultaPartida.length;i++) { //asegurarse que tododos los books:ID tengan espacios alrededor
            if(consultaPartida[i].matches("^"+prefijoOriginal+"\\S+")){
                consultaPartida[i] = "<"+nombreEquivalenteEnLaOntologia(consultaPartida[i],nuevaIRI)+">";
            }
        }
        return String.join(" ", consultaPartida);
    }

    public static LinkedList<HashMap<String,String>> consultaEnTodasLasBD(
            String consulta, String[] variables){
        LinkedList res = new LinkedList();
        res.addAll(consultaEnEndpoint(
                traducirConsultaA(consulta,"book:","vocab:"),
                variables,
                "http://35.208.107.33:2020/sparql"
                ));

        res.addAll(
                consultaEnEndpoint(
                consulta, variables, "http://35.188.55.150:8890/sparql"
                ));
        res.addAll(
                consultaEnRDF(
                        traducirConsultaA(consulta,"book:","book1:"),
                        variables
                ));
        /*res.addAll(consultaEnEndpoint(
                traducirConsultaA(consulta,"book:","dbo:"),
                variables,
                "http://dbpedia.org/sparql/"
        ));*/
        return res;


    };
    public static LinkedList<HashMap<String,String>> consultaEnEndpoint(
            String consulta, String[] variables, String endpoint
    ){
        LinkedList res = new LinkedList();
        RDFConnection conn = RDFConnectionFactory.connect(endpoint);
        //System.out.println("/////////////////////\n"+consulta);
        QueryExecution qExec = conn.query(consulta) ;
        ResultSet rs = qExec.execSelect() ;
        while(rs.hasNext()) {
            QuerySolution qs = rs.next() ;
            HashMap fila = new HashMap();
            for(String v: variables){
                fila.put(v,qs.get(v).toString());
            }
            res.add(fila);
        }
        qExec.close() ;
        conn.close() ;
        return res;
    }
    public static LinkedList<HashMap<String,String>> consultaEnRDF(
            String consulta, String[] variables
    ){
        //System.out.println("/////////////////////\n"+consulta);
        Model modelRDF = ModelFactory.createDefaultModel() ;
        modelRDF.read("RDF1.xml") ;

        LinkedList res = new LinkedList();
        Query query = QueryFactory.create(consulta);
        QueryExecution qExec = QueryExecutionFactory.create(query,modelRDF);

        try{
            ResultSet rs = qExec.execSelect() ;
            while(rs.hasNext()) {
                QuerySolution qs = rs.next() ;
                HashMap fila = new HashMap();
                for(String v: variables){
                    fila.put(v,qs.get(v).toString());
                }
                res.add(fila);
            }
        } finally {
            qExec.close() ;
            return res;
        }
    };

}

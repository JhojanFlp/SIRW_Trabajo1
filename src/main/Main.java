package main;

import javax.swing.*;

// Librerías de Java
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

// Librerías de Jena
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.util.FileManager;

public class Main {

    public static void main(String[] args) throws FileNotFoundException{

        // GUI
        /*SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new Panel();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setSize(500, 500);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                //Fill to CB Instance
                String[] instances = {"libro", "autor"};
            }
        });*/

        // Create Model
        Model model = ModelFactory.createDefaultModel() ;
        model.read("Libros.ttl") ;

        //razonador
        Reasoner OWLReasoner = ReasonerRegistry.getOWLReasoner();
        InfModel OWLinfe = ModelFactory.createInfModel(OWLReasoner,model);

        //validation
        ValidityReport validity = OWLinfe.validate();
        if (validity.isValid()) {
            System.out.println("OK");
        } else {
            System.out.println("Errores: ");
            for (Iterator i = validity.getReports(); i.hasNext(); ) {
                System.out.println(" - " + i.next());
            }
        }

        //consulta sparql
        String queryString = "PREFIX book: <http://book.org/>\n" +
                "PREFIX book1: <http://book1.org/#>\n" +
                "\n" +
                "SELECT DISTINCT * \n" +
                "\n" +
                "WHERE {\n" +
                "\t{\n" +
                "\tbook:Ebay ?p ?o .\n" +
                "}\n" +
                "UNION\n" +
                "{\n" +
                "\tSERVICE <http://35.188.55.150:8890/sparql?default-graph-uri=http%3A%2F%2Fbook.org%2F>{\n" +
                "  \t\tbook:Ebay ?p ?o .\n" +
                "\t}\n" +
                "}\n" +
                "\n" +
                "}\n" +
                "LIMIT 100";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query,model);

        try{
            ResultSet results = qexec.execSelect();
            while (results.hasNext()){
                QuerySolution soln = results.nextSolution();

                /*Literal s = soln.getLiteral("s");
                Literal p = soln.getLiteral("p");
                Literal o = soln.getLiteral("o");
                System.out.println(s+" "+p+" "+o);*/
                System.out.println(soln.toString());
            }
        } finally {
            qexec.close();
        }

        //OWLinfe.write(System.out);

        // Print Tripletas
        // for(StmtIterator i = data.listStatements(); i.hasNext();) {
        //     Statement statement = i.nextStatement();
        //    System.out.println(statement.getSubject() + " " + statement.getPredicate() + " " + statement.getObject());
        // }

        // Entities


    }

}

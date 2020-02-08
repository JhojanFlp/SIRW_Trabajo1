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
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.util.FileManager;

public class Main {

    public static void main(String[] args) throws FileNotFoundException{

        // GUI
        SwingUtilities.invokeLater(new Runnable() {
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
        });

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


        //OWLinfe.write(System.out);

        // Print Tripletas
        // for(StmtIterator i = data.listStatements(); i.hasNext();) {
        //     Statement statement = i.nextStatement();
        //    System.out.println(statement.getSubject() + " " + statement.getPredicate() + " " + statement.getObject());
        // }

        // Entities


    }

}

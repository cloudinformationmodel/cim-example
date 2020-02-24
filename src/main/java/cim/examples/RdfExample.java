package cim.examples;

import cim.examples.rdf.Queries;
import cim.loader.CIMLoader;
import cim.loader.CIMUseCase;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Example that loads the full model as a RDF graph using Apache Jena.
 * Jena can be used to work with the model in-memory through Jena's programmatic API or using W3C's SPARQL
 * query language.
 * The whole stack of W3C's standards and libraries can be used directly over the RDF graph.
 * It can also be loaded in any triple store or graph database that supports RDF.
 */
public class RdfExample {

    public static void run(Path cimPath) throws IOException {
        // Select the level of semantics we want to load from the model distribution
        CIMLoader loader = new CIMLoader(cimPath, CIMUseCase.CONCEPTUAL_AND_SCHEMA);

        // Load all of CIM into a single Apache Jena RDF model
        Model model = loader.getJenaModel();

        // count assertions
        System.out.println("Total number of statements in the graph: " + model.size());

        //
        // SPARQL API
        //

        // We can use SPARQL to query the model metadata graph
        // The Queries object stores all the different queries
        Queries queries = new Queries(model);
        // Types of elements
        elementTypesQuery(queries);

        // Count elements
        countsQuery(queries);

        // List all classes
        descriptionQuery(queries);


        //
        // Jena API
        //

        // We can use the Jena API to extract information from the graph
        jenaAPI(model);

    }

    protected static void jenaAPI(Model model) {
        ResIterator it = model.listSubjectsWithProperty(
                model.createProperty(model.expandPrefix("rdf:type")),
                model.createResource(model.expandPrefix("rdf:Property"))
        );

        while(it.hasNext()) {
            Resource property = it.nextResource();
            System.out.println("\n\n** Property: " + property.getURI());
            NodeIterator domainsIt = model.listObjectsOfProperty(
                    property,
                    model.createProperty(model.expandPrefix("rdfs:domain"))
            );
            System.out.println("  Property domain:");
            while(domainsIt.hasNext()) {
                RDFNode entityGroup = domainsIt.nextNode();
                System.out.println("    - " + entityGroup.asResource().getURI());
            }
        }
    }


    protected static void elementTypesQuery(Queries queries) {
        System.out.println("Types of elements:");
        System.out.println(queries.listModelElementTypes());
    }

    protected static void descriptionQuery(Queries queries) {
        System.out.println(queries.listClasses());
    }

    protected static void countsQuery(Queries queries) {
        int subjectAreaCount = queries.countsQuery("cim:SubjectArea");
        int entityGroupCount = queries.countsQuery("cim:EntityGroup");
        int classCount = queries.countsQuery("rdfs:Class");
        int propertyCount = queries.countsQuery("rdf:Property");
        System.out.println(
                "Model size:\n" +
                        "  Subject Areas " + subjectAreaCount + "\n" +
                        "  Entity Groups " + entityGroupCount + "\n" +
                        "  Classes " + classCount + "\n" +
                        "  Properties " + propertyCount + "\n"
        );
    }
}

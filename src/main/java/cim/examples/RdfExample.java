package cim.examples;

import cim.loader.CIMLoader;
import cim.loader.CIMUseCase;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;

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

        // We can use the Jena API to extract information from the graph
        jenaAPI(model);

        // We can use SPARQL to query the model metadata graph
        SparqlAPI(model);
    }

    protected static void jenaAPI(Model model) {
        ResIterator it = model.listSubjectsWithProperty(
                model.createProperty(model.expandPrefix("rdf:type")),
                model.createResource(model.expandPrefix("rdf:Property"))
        );

        while(it.hasNext()) {
            Resource property = it.nextResource();
            System.out.println("\n\n** Property: " + property.getURI());
            ResIterator entityGroupsIt = model.listSubjectsWithProperty(
                    model.createProperty(model.expandPrefix("cim:properties")),
                    property
            );
            System.out.println("  Used in entity groups:");
            while(entityGroupsIt.hasNext()) {
                Resource entityGroup = entityGroupsIt.nextResource();
                System.out.println("    - " + entityGroup.getURI());
            }
        }
    }

    protected static void SparqlAPI(Model model) {
        String queryString =
                "PREFIX cim: <http://cim.org/model/> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "SELECT DISTINCT ?property ?entityGroup { " +
                "    ?entityGroup cim:properties ?property ." +
                "} ORDER BY ?property";

        Query query = QueryFactory.create(queryString) ;

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource p = soln.getResource("property");
                Resource eg = soln.getResource("entityGroup");
                System.out.println(p.getURI().replace("http://cim.org/model/", "cim:") + " used in "+model.qnameFor(eg.getURI()));
            }
        }
    }
}

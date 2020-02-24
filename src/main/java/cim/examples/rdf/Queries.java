package cim.examples.rdf;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import java.io.StringWriter;

public class Queries {

    // Common URI prefixes for SPARQL queries
    private static final String  PREFIXES =
            "PREFIX cim: <http://cloudinformationmodel.org/model/> " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
            "PREFIX sh: <http://www.w3.org/ns/shacl#>\n";

    // RDF model being queried
    private final Model model;

    public Queries(Model model) {
        this.model = model;
    }

    /**
     * Counts elements in the model
     * @param modelElement URI for the type of element being counted
     */
    public int countsQuery(String modelElement) {
        String queryString =
                PREFIXES +
                        "SELECT (COUNT(?entityGroup) AS ?total) { " +
                        "    ?entityGroup a " + modelElement + " ." +
                        "}";

        Query query = QueryFactory.create(queryString) ;

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            QuerySolution soln = results.nextSolution();
            Literal count = soln.getLiteral("total");
            return count.getInt();
        }
    }

    public String listClasses() {
        StringWriter writer = new StringWriter();
        String queryString =
                PREFIXES +
                        "SELECT * { " +
                        "    ?class rdfs:label ?className ." +
                        "    ?class rdfs:comment ?classDescription ." +
                        "    ?class sh:and*/sh:property/sh:path ?property ." +
                        "    ?property rdfs:comment ?propertyDescription ." +
                        "    ?property rdfs:label ?propertyName ." +
                        "} ORDER BY ?class ?property";

        Query query = QueryFactory.create(queryString) ;

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            String lastClass = null;
            String lastProperty = null;
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource k = soln.getResource("class");
                Resource p = soln.getResource("property");
                Literal kn = soln.getLiteral("className");
                Literal pn = soln.getLiteral("propertyName");
                Literal kd = soln.getLiteral("classDescription");
                Literal pd = soln.getLiteral("propertyDescription");
                if (k.getURI() != lastClass) {
                    writer.write(" => Class: " + kn.getString() + "[" + k.getURI() + "]\n");
                    writer.write("       " + kd.getString() + "\n");
                    writer.write("    Properties: \n");
                    lastClass = k.getURI();
                }
                if (p.getURI() != lastProperty) {
                    writer.write("    - " + pn.getString() + "[" + p.getURI() + "] \n");
                    writer.write("         " + pd.getString() + "\n");
                    lastProperty = p.getURI();
                }
            }
        }

        return writer.toString();
    }


    /**
     * Lists all the types of elements in the model
     * @return
     */
    public String listModelElementTypes() {
        StringWriter writer = new StringWriter();
        String queryString =
                PREFIXES +
                        "SELECT DISTINCT ?type { " +
                        "    ?s a ?type ." +
                        "} ORDER BY ?type";

        Query query = QueryFactory.create(queryString) ;

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            String lastClass = null;
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource t = soln.getResource("type");

                writer.write("  - " + t + "\n");
            }
        }
        return writer.toString();
    }

}

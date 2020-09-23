package cim.examples.rdf;

import org.apache.jena.atlas.io.AWriter;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.store.Hash;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Queries {

    // Common URI prefixes for SPARQL queries
    protected static final String  PREFIXES =
            "PREFIX cim: <http://cloudinformationmodel.org/model/> " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
            "PREFIX sh: <http://www.w3.org/ns/shacl#>\n";

    // RDF model being queried
    protected final Model model;

    public Queries(Model model) {
        this.model = model;
    }

    /**
     * Counts elements in the model
     * @param modelElementType URI for the type of element being counted
     */
    public int countsQuery(String modelElementType) {
        String queryString =
                PREFIXES +
                        "SELECT (COUNT(?modelElement) AS ?total) { " +
                        "    ?modelElement a " + modelElementType + " ." +
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


    public String reconstructTable() {
        StringWriter writer = new StringWriter();
        String queryString =
                PREFIXES +
                         "SELECT DISTINCT " +
                        "  ?developerName ?description ?entityGroup ?propertyDeveloperName ?propertyDatatype ?referencedName  " +
                        "  (IF(BOUND(?minCount) && xsd:integer(?minCount) > 0, TRUE, FALSE) AS ?required)" +
                        " {" +
                        "    ?entityGroupId cim:classes ?classId ; rdfs:label ?entityGroup ." +
                        "    ?classId rdfs:label ?developerName ." +
                        "    ?classId rdfs:comment ?description ." +
                        "    ?classId sh:and*/sh:property ?propertyShape ." +
                        "    ?propertyShape sh:path ?propertyId ." +
                        "    ?propertyId rdfs:label ?propertyDeveloperName ." +
                        "    ?propertyId rdfs:comment ?propertyDescription ." +
                        "    OPTIONAL {" +
                        "      ?propertyShape sh:datatype ?propertyDatatype ." +
                        "    }" +
                        "    OPTIONAL {" +
                        "      ?propertyShape sh:node ?referencedClass ." +
                        "      ?referencedClass rdfs:label ?referencedName ." +
                        "    }" +
                        "    OPTIONAL {" +
                        "      ?propertyShape sh:minCount ?minCount ." +
                        "    }" +
                        "    FILTER (?propertyDatatype != cim:id || BOUND(?referencedName)) ." +
                        "} ORDER BY ?classId ?propertyId";

        Query query = QueryFactory.create(queryString) ;

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Literal eg = soln.getLiteral("entityGroup");
                Literal kn = soln.getLiteral("developerName");
                Literal pn = soln.getLiteral("propertyDeveloperName");
                Literal kd = soln.getLiteral("description");
                Resource pdt = soln.getResource("propertyDatatype");
                Literal pref = soln.getLiteral("referencedName");
                Literal required = soln.getLiteral("required");

                writer.write("  developerName: " + kn.getString() + "\n");
                writer.write("  description: " + kd.getString() + "\n");
                writer.write("  entityGroup: " + eg.getString() + "\n");

                if (pdt != null) {
                    writer.write("  propertyDeveloperName: " + pn.getString() + "\n");
                    writer.write("  propertyDatatype: " + pdt.getURI() + "\n");
                }
                if (pref != null) {
                    writer.write("  propertyDeveloperName: " + pref.getString().replace(" ", "") + "Id" + "\n");
                    writer.write("  propertyDatatype: " + "URI reference" + "\n");
                    writer.write("  referencedRelationshipName: " + pn.getString() + "\n");
                    writer.write("  referencedEntity: " + pref.getString() + "\n");
                }

                writer.write("  required: " + required.getBoolean() + "\n");

                writer.write("----\n");
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


    /**
     * Lists cardinalities in relationships for all entities in the model
     * @return
     */
    public String cardinalities() {
        StringWriter writer = new StringWriter();

        // collect all the classes
        ArrayList<String> classes = new ArrayList<>();

        String queryString =
                PREFIXES +
                        "SELECT * { " +
                        "    ?class a rdfs:Class ." +
                        "}";

        Query query = QueryFactory.create(queryString) ;

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource k = soln.getResource("class");
                classes.add(k.getURI());
            }
        }

        for (String classId : classes) {
            Set<String> linkedToClasses = new HashSet<>();
            Set<String> linkedFromClasses = new HashSet<>();

            queryString =
                    PREFIXES +
                            "SELECT * { " +
                            "    <"+ classId + "> a rdfs:Class ; " +
                            "                     sh:and*/sh:property/sh:node ?targetClass ." +
                            "}";

            query = QueryFactory.create(queryString) ;

            try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
                ResultSet results = qexec.execSelect() ;

                while (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    Resource k = soln.getResource("targetClass");
                    linkedToClasses.add(k.getURI());
                }
            }

            queryString =
                    PREFIXES +
                            "SELECT * { " +
                            "    ?sourceClass a rdfs:Class;" +
                            "                 sh:and*/sh:property/sh:node <" + classId + "> ." +
                            "}";

            query = QueryFactory.create(queryString) ;

            try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
                ResultSet results = qexec.execSelect() ;

                while (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    Resource k = soln.getResource("sourceClass");
                    linkedFromClasses.add(k.getURI());
                }
            }

            Set<String> oneToOne = new HashSet<>(linkedToClasses);
            oneToOne.retainAll(linkedFromClasses);

            Set<String> oneToMany = new HashSet<>(linkedFromClasses);
            oneToMany.removeAll(linkedToClasses);

            writer.append("* Class " + classId + "\n");
            writer.append("  1:1\n");
            for (String targetClassId : oneToOne) {
                writer.append("   - " + targetClassId + "\n");
            }
            writer.append("  1:n\n");
            for (String targetClassId : oneToMany) {
                writer.append("   - " + targetClassId + "\n");
            }
        }

        return writer.toString();

    }

}

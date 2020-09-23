package cim.examples.rdf;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import java.io.StringWriter;
import java.util.Arrays;

public class RegenerateQueries extends Queries {

    public static String CIM_NS = "http://cloudinformationmodel.org/model/";

    public RegenerateQueries(Model model) {
        super(model);
    }

    public String subjectAreas() {
        StringWriter writer = new StringWriter();
        String queryString =
                PREFIXES +
                        "SELECT * { " +
                        "  ?subjectAreaId a cim:SubjectArea" +
                        "  OPTIONAL {" +
                        "    ?subjectAreaId rdfs:label ?subjectAreaName" +
                        "  }" +
                        "  OPTIONAL {" +
                        "    ?subjectAreaId rdfs:comment ?description" +
                        "  }" +
                        "} ORDER BY ?subjectAreaId";

        Query query = QueryFactory.create(queryString) ;

        writer.write("subjectAreaId\tsubjectAreaName\tdescription\n");

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource sa = soln.getResource("subjectAreaId");
                Literal name = soln.getLiteral("subjectAreaName");
                Literal description = soln.getLiteral("description");

                writer.write(maybeId(sa));
                writer.write(maybeData(name));
                writer.write(maybeData(description));
                writer.write("\n");
            }
        }

        return writer.toString();
    }

    public String entityGroups() {
        StringWriter writer = new StringWriter();
        String queryString =
                PREFIXES +
                        "SELECT * { " +
                        "  ?subjectAreaId cim:entityGroup ?entityGroupId ." +
                        "   OPTIONAL {" +
                        "    ?entityGroupId  a ?type ." +
                        "  } " +
                        "  OPTIONAL {" +
                        "    ?entityGroupId rdfs:label ?entityGroupName" +
                        "  }" +
                        "  OPTIONAL {" +
                        "    ?entityGroupId rdfs:comment ?description" +
                        "  }" +
                        "  OPTIONAL {" +
                        "    ?entityGroupId cim:subjectArea ?subjectArea" +
                        "  }" +
                        "} ORDER BY ?subjectAreaId";

        Query query = QueryFactory.create(queryString) ;

        writer.write("subjectAreaId\tentityGroupId\tentityGroupN\ttype\tsubjectArea\tdescription\n");

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource sa = soln.getResource("subjectAreaId");
                Resource eg = soln.getResource("entityGroupId");
                Resource type = soln.getResource("type");
                Literal name = soln.getLiteral("entityGroupName");
                Literal description = soln.getLiteral("description");
                Literal subjectArea = soln.getLiteral("subjectArea");

                writer.write(maybeId(sa));
                writer.write(maybeId(eg));
                writer.write(maybeData(name));
                writer.write(maybeId(type));
                writer.write(maybeData(subjectArea));
                writer.write(maybeData(description));
                writer.write("\n");
            }
        }

        return writer.toString();
    }

    public String classConcepts() {
        StringWriter writer = new StringWriter();
        String queryString =
                PREFIXES +
                        "SELECT * { " +
                        "  ?subjectAreaId cim:entityGroup ?entityGroupId ." +
                        "  ?entityGroupId cim:classes ?classId ." +
                        "  ?classId rdfs:label ?className ;" +
                        "           rdfs:comment ?description ;" +
                        "           a ?type ." +
                        "   OPTIONAL {" +
                        "    ?classId  rdfs:subClassOf ?subClassOf " +
                        "  } " +
                        "} ORDER BY ?subjectAreaId ?entityGroupId ?classId";

        Query query = QueryFactory.create(queryString) ;

        writer.write("subjectAreaId\tentityGroupId\tclassName\tclassId\ttype\tsubClassOf\tdescription\n");

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource sa = soln.getResource("subjectAreaId");
                Resource eg = soln.getResource("entityGroupId");
                Resource classId = soln.getResource("classId");
                Resource subClassId = soln.getResource("subClassOf");
                Resource type = soln.getResource("type");
                Literal className = soln.getLiteral("className");
                Literal description = soln.getLiteral("description");

                writer.write(maybeId(sa));
                writer.write(maybeId(eg));
                writer.write(maybeId(classId));
                writer.write(maybeData(className));
                writer.write(maybeId(type));
                writer.write(maybeId(subClassId));
                writer.write(maybeData(description));
                writer.write("\n");
            }
        }

        return writer.toString();
    }

    public String propertyConcepts() {
        StringWriter writer = new StringWriter();
        String queryString =
                PREFIXES +
                        "SELECT * { " +
                        "  ?subjectAreaId cim:entityGroup ?entityGroupId ." +
                        "  ?entityGroupId cim:properties ?propertyId ." +
                        "  ?propertyId a ?type ;" +
                        "              rdfs:domain ?domain ." +
                        "  ?entityGroupId cim:classes ?domain ." +
                        "  OPTIONAL {" +
                        "   ?domain rdfs:subClassOf ?subClassOf " +
                        "  } " +
                        "  OPTIONAL {" +
                        "   ?shapeId sh:targetClass ?domain; " +
                        "            (sh:and/rdf:rest/rdf:first)?/sh:property ?attributeId ." +
                        "   ?attributeId sh:path ?propertyId ." +
                        "  } " +
                        "} ORDER BY ?subjectAreaId ?entityGroupId ?propertyId";

        Query query = QueryFactory.create(queryString) ;

        writer.write("subjectAreaId\tentityGroupId\tpropertyId\tprope\tdomain\tsubClassOf\tEntityAndAttribute\tpropertyGUID\n");

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource sa = soln.getResource("subjectAreaId");
                Resource eg = soln.getResource("entityGroupId");
                Resource propertyId = soln.getResource("propertyId");
                Resource subClassId = soln.getResource("subClassOf");
                Resource type = soln.getResource("type");
                Resource domain = soln.getResource("domain");
                Resource propertyGuid = soln.getResource("attributeId");

                writer.write(maybeId(sa));
                writer.write(maybeId(eg));
                writer.write(maybeId(propertyId));
                writer.write(maybeId(type));
                writer.write(maybeId(domain));
                writer.write(maybeId(subClassId));
                writer.write(maybeId(domain).replace("\t",":")+ maybeId(propertyId));
                writer.write(maybeId(propertyGuid));
                writer.write("\n");
            }
        }

        return writer.toString();
    }

    public String schemas() {
        StringWriter writer = new StringWriter();
        String queryString =
                PREFIXES +
                        "SELECT * { " +
                        "  ?schemaId sh:targetClass ?targetClass ;" +
                        "            a ?type ." +
                        "  ?subjectAreaId cim:entityGroup ?entityGroupId ." +
                        "  ?entityGroupId cim:classes ?targetClass ." +
                        "} ORDER BY ?subjectAreaId ?entityGroupId ?targetClass";

        Query query = QueryFactory.create(queryString) ;

        writer.write("targetClass\tsubjectAreaId\tentityGroupId\tschemaId\tsche\ttargetClass\n");

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource targetClass = soln.getResource("targetClass");
                Resource subjectAreaId = soln.getResource("subjectAreaId");
                Resource entityGroupId = soln.getResource("entityGroupId");
                Resource schemaId = soln.getResource("schemaId");
                Resource sche = soln.getResource("type");

                writer.write(maybeId(targetClass));
                writer.write(maybeId(subjectAreaId));
                writer.write(maybeId(entityGroupId));
                writer.write(maybeId(schemaId));
                writer.write(maybeId(sche));
                writer.write(maybeId(targetClass));
                writer.write("\n");
            }
        }

        return writer.toString();
    }

    public String schemaProperties() {
        StringWriter writer = new StringWriter();
        String queryString =
                PREFIXES +
                        "SELECT * { " +
                        "  ?schemaId sh:targetClass ?targetClass ;" +
                        "            (sh:and/rdf:rest/rdf:first)?/sh:property ?attributeId ." +
                        "  ?attributeId sh:path ?path ." +
                        "  OPTIONAL { ?attributeId sh:datatype ?datatype }" +
                        "  OPTIONAL { ?attributeId sh:minCount ?minCount }" +
                        "  OPTIONAL { ?attributeId sh:maxCount ?maxCount }" +
                        "  OPTIONAL { ?attributeId sh:node ?node }" +
                        "  ?subjectAreaId cim:entityGroup ?entityGroupId ." +
                        "  ?entityGroupId cim:classes ?targetClass ." +
                        "} ORDER BY ?subjectAreaId ?entityGroupId ?targetClass";

        Query query = QueryFactory.create(queryString) ;

        writer.write("fullPath\tpath\tsubjectAreaId\tentityGroupId\tschemaId\tpropertyId\tdatatype\tminCount\tmaxCount\tnode\tschemaName\n");

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource path = soln.getResource("path");
                Resource subjectAreaId = soln.getResource("subjectAreaId");
                Resource entityGroupId = soln.getResource("entityGroupId");
                Resource schemaId = soln.getResource("schemaId");
                Resource propertyId = soln.getResource("attributeId");
                Resource datatype = soln.getResource("datatype");
                Literal minCount = soln.getLiteral("minCount");
                Literal maxCount = soln.getLiteral("maxCount");
                Resource node = soln.getResource("node");
                Resource schemaName = soln.getResource("targetClass");

                writer.write(maybeId(schemaName).replace("\t", ":") + maybeId(path));
                writer.write(maybeId(path));
                writer.write(maybeId(subjectAreaId));
                writer.write(maybeId(entityGroupId));
                writer.write(maybeId(schemaId));
                writer.write(maybeId(propertyId));
                writer.write(maybeId(datatype));
                writer.write(maybeData(minCount));
                writer.write(maybeData(maxCount));
                writer.write(maybeId(node));
                writer.write(maybeId(schemaName));
                writer.write("\n");
            }
        }

        return writer.toString();
    }


    private String maybeData(Literal lit) {
        if (lit != null) {
            return lit.getString() +  "\t";
        } else {
            return "\t";
        }
    }

    private String maybeId(Resource res) {
        if (res != null) {
            String uri = res.getURI();

            if (uri == null) { // blank
                return "\t";
            } else {
                if (uri.contains(CIM_NS)) {
                    return res.getURI().replace(CIM_NS, "") + "\t";
                } else if (uri.contains("#")) {
                    return uri.split("#")[1] + "\t";
                } else {
                    String[] parts = uri.split("/");
                    return parts[parts.length - 1] + "\t";
                }
            }
        } else {
            return "\t";
        }
    }
}

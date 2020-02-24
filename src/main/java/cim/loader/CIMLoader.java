package cim.loader;

import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.core.JsonLdUtils;
import com.github.jsonldjava.utils.JsonUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class CIMLoader {

    private final Path cimDirectory;
    private final CIMUseCase useCase;

    public CIMLoader(Path cimDirectory, CIMUseCase useCase) {
        this.cimDirectory = cimDirectory;
        this.useCase = useCase;
    }

    // Loads the requested level as an Apache Jena Model
    public Model getJenaModel() throws IOException {
        Model model = ModelFactory.createDefaultModel();
        // Set-up some useful prefixes so we can use CURIES
        model.setNsPrefix("cim", "http://cim.org/model/");
        model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        model.setNsPrefix("sh", "http://www.w3.org/ns/shacl#");
        model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");

        String jsonld = JsonUtils.toPrettyString(getJsonLDGraph());
        model.read(new StringReader(jsonld), "", "JSON-LD");
        return model;
    }

    // Loads the requested level as JSON-LD flattened document
    public Object getJsonLDGraph() throws IOException {
        List<Object> resourceGraphs = getJsonDocuments();
        HashMap<String,Object> graph = new HashMap<>();
        graph.put("@graph", resourceGraphs);
        return JsonLdProcessor.flatten(graph, new JsonLdOptions());
    }

    // Loads the requested level as a list of parsed JSON documents
    public List<Object> getJsonDocuments() throws IOException {
        Object context = loadJsonldContext();
        return getSchemaFiles()
                .stream()
                .map((f) -> CIMLoader.file2JSON(f,context))
                .collect(Collectors.toList());
    }

    // Loads the paths for the files required for the requested level
    public List<Path> getSchemaFiles() throws IOException {
        return Files.walk(cimDirectory)
                .filter(f -> CIMLoader.mustLoad(f, useCase))
                .collect(Collectors.toList());
    }

    /**
     * Filters paths according to the requested level
     * @param file
     * @param useCase
     * @return
     */
    protected static boolean mustLoad(Path file, CIMUseCase useCase) {
        if (useCase == CIMUseCase.CANONICAL_SCHEMA) {
            return file.endsWith("schema.jsonld") || file.endsWith("about.jsonld");
        } else if (useCase == CIMUseCase.CONCEPTUAL) {
            return file.endsWith("concepts.jsonld") || file.endsWith("about.jsonld");
        } else {
            return file.endsWith("schema.jsonld") || file.endsWith("concepts.jsonld")  || file.endsWith("about.jsonld");
        }
    }

    /**
     * Loads the @context for the JSON-LD files in the model distribution
     * @return
     * @throws IOException
     */
    public Object loadJsonldContext() throws IOException {
        try {
            Path contextPath = cimDirectory.resolve("./src/context.jsonld");
            return JsonUtils.fromInputStream(new FileInputStream(contextPath.toFile()));
        } catch (FileNotFoundException e) {
            Path contextPath = cimDirectory.resolve("./context.jsonld");
            return JsonUtils.fromInputStream(new FileInputStream(contextPath.toFile()));
        }
    }

    /**
     * Replaces the @context in a JSON-LD file from the distribution (pointing to http://cim.org/context.jsonld) with
     * the actual value of the context, since we are not publishing the context yet.
     * @param f
     * @param context
     * @return
     */
    private static Object file2JSON(Path f, Object context) {
        try {
            LinkedHashMap<String, Object> json = (LinkedHashMap<String, Object>) JsonUtils.fromInputStream(new FileInputStream(f.toFile()));
            json.put("@context", context); // update the context
            return json;
        } catch (IOException e) {
            return new HashMap<String,Object>();
        }
    }

}

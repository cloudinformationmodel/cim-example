package cim.examples;

import cim.loader.CIMLoader;
import cim.loader.CIMUseCase;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.core.JsonLdUtils;
import com.github.jsonldjava.utils.JsonUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

public class JsonldExample {

    /**
     * Example where we are using the JSON data as a JSON-LD graph, so we can use some standard
     * JSON-LD functionality, like in this example framing (https://www.w3.org/TR/json-ld11-framing/).
     * This is a viable way of working with the metadata graph of the model if the right JSON-LD library are
     * used.
     * @param cimPath
     * @throws IOException
     */
    public static void run(Path cimPath) throws IOException {

        // Select the level of semantics we want to load from the model distribution
        CIMLoader loader = new CIMLoader(cimPath, CIMUseCase.CONCEPTUAL);

        // Load all of CIM into a single graph
        Object singleJsonLDGraph = loader.getJsonLDGraph();

        // Define a JSON-LD frame to extract all the properties
        Object frame = new HashMap<String, Object>() {{
            put("@context", loader.loadJsonldContext());
            put("@type", "rdf:Property");
        }};

        // Extract all the properties
        Object framed = JsonLdProcessor.frame(singleJsonLDGraph, frame, new JsonLdOptions());

        // Print the output JSON-LD document
        System.out.println(JsonUtils.toPrettyString(framed));
    }
}

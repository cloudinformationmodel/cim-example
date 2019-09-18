package cim.examples;

import cim.loader.CIMLoader;
import cim.loader.CIMUseCase;
import com.github.jsonldjava.utils.JsonUtils;

import java.io.IOException;
import java.nio.file.Path;

public class JsonExample {

    /**
     * Very simple example that just works with the files of the distribution and produces a single JSON structure.
     * It is possible to work with the distribution in this way but you are limited to manipulate JSON data.
     * @param cimPath
     * @throws IOException
     */
    public static void run(Path cimPath) throws IOException {
        // Select the level of semantics we want to load from the model distribution
        CIMLoader loader = new CIMLoader(cimPath, CIMUseCase.CONCEPTUAL);

        // Paths for the relevant files in the distribution for the selected level
        loader.getSchemaFiles().stream().forEach(System.out::println);

        // Loads all the JSON data from the distribution into a single JSON object
        Object json = loader.getJsonDocuments();

        // Print the output JSON document
        System.out.println(JsonUtils.toPrettyString(json));
    }
}

package cim;

import cim.examples.JsonExample;
import cim.examples.JsonldExample;
import cim.examples.RdfExample;
import cim.loader.CIMLoader;
import cim.loader.CIMUseCase;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Examples {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("USAGE: java -jar cim_example.jar PATH_TO_CIM_MODEL (JSON|JSON-LD|RDF)");
            System.exit(1);
        }
        Path path = Paths.get(args[0]);
        String example = args[1];


        // Raw JSON data
        if (example.equalsIgnoreCase("JSON")) {
            JsonExample.run(path);
        }

        // JSON-LD documents
        if (example.equalsIgnoreCase("JSON-LD")) {
            JsonldExample.run(path);
        }

        // RDF graph model
        if (example.equalsIgnoreCase("RDF")) {
            RdfExample.run(path);
        }
    }
}

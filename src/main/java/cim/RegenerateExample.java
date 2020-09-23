package cim;

import cim.examples.rdf.RegenerateQueries;
import cim.loader.CIMLoader;
import cim.loader.CIMUseCase;
import org.apache.jena.rdf.model.Model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RegenerateExample {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("USAGE: java -jar cim_example.jar PATH_TO_CIM_MODEL");
            System.exit(1);
        }
        Path path = Paths.get(args[0]);

        CIMLoader loader = new CIMLoader(path, CIMUseCase.CONCEPTUAL_AND_SCHEMA);
        Model model = loader.getJenaModel();

        RegenerateQueries queries = new RegenerateQueries(model);

        System.out.println(queries.subjectAreas());
        System.out.println(queries.entityGroups());
        System.out.println(queries.classConcepts());
        System.out.println(queries.propertyConcepts());
        System.out.println(queries.schemas());
        System.out.println(queries.schemaProperties());
    }


}

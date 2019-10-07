# CIM example

## Build

```shell script
$ mvn package
```

## Usage from uberjar

```shell script
$ java -jar target/cim_example-1.0-SNAPSHOT.jar path/to/cim/distribution/src LEVEL
```

Where `LEVEL` can be: `JSON`, `JSON-LD` or `RDF`.


# Distribution Component

This ATTX Distribution Component provides the interface between the Graph Component and the applications designed for consuming and disseminating the data produced by the Semantic Web Broker platform.

The Distribution Component consists of:
* Graph Framing Service that transform RDF data with a given JSON-LD frame into JSON-LD that can be indexed into Elasticsearch 5.x;
* Indexing service - provides the interface for indexing JSON-LD data into Elasticsearch, by extracting document type and performing simple or bulk indexing.


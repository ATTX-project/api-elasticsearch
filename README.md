# Distribution Component

This ATTX Distribution Component provides the interface between the Graph Component and the applications designed for consuming and disseminating the data produced by the Semantic Web Broker platform.

The Distribution Component consists of:
* ElasticSearch 1.3.4 with Siren plugin (https://github.com/sirensolutions/siren) which has the role of indexing and making the Graph Store knowledge convenient accessible via an API;
* ElasticSearch 5.x which provides the latest functionality in order to index data as plain JSON (after applying a JSON-LD frame http://json-ld.org/spec/latest/json-ld-framing/), JSON-LD or capturing logs.

## Repository Structure

Currently the repository consists of:
* Distribution Component - Integration Tests
* Elasticsearch 1.3.4 ATTX API plugin and associated tests

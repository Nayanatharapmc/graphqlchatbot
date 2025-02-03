package com.example.graphqlchatbot;

import java.nio.file.Path;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.FileSystemDocumentLoader;
// import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
// import dev.langchain4j.data.document.parser.TextDocumentParser;

public class GraphQLSchemaLoader {
    public static String loadSchema(String schemaPath) {
        Document document = FileSystemDocumentLoader.loadDocument(Path.of(schemaPath));
        return document.text();
        
    }
}

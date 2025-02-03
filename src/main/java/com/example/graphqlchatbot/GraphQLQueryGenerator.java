package com.example.graphqlchatbot;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
// import dev.langchain4j.service.AiServices;

public class GraphQLQueryGenerator {
    private final ChatLanguageModel model;
    private final ChatMemory chatMemory;
    private final String graphqlSchema;

    public GraphQLQueryGenerator(String schemaPath) {
        this.model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-4o-mini")
                .build();
        this.chatMemory = TokenWindowChatMemory.withMaxTokens(1000, new OpenAiTokenizer("gpt-4o-mini"));
        this.graphqlSchema = GraphQLSchemaLoader.loadSchema(schemaPath);
    }

    public String chat(String userMessage) {
        ChatMemory chatMemory = this.chatMemory;
        String prompt = String.format(
            "You are an AI assistant trained to convert natural language queries into GraphQL queries based on the following API schema and the chat history:\n" + 
                "GraphQL API Schema (SDL):\n" + 
                graphqlSchema + "\n" +
                "Chat History:\n" +
                chatMemory.toString() +
                "\n" + 
                "Example Queries:\n" + 
                "\n" + 
                "- Query 1: \"Give me a list of all characters.\"\n" + 
                "  Expected Query: `query { allCharacters(first: 10) { id name } }`\n" +
                "\n" + 
                "- Query 2: \"What are the reviews for Star Wars Episode V?\"\n" + 
                "  Expected Query: `query { reviews(episode: EMPIRE) { stars commentary } }`\n" + 
                "\n" +
                "- Query 3: \"What is the name of the droid with ID 'droid-123'?\"\n" + 
                "  Expected Query: `query { droid(id: \"droid-123\") { name } }`\n" + 
                "\n" + 
                "Instructions:\n" + 
                "1. Convert any natural language question related to the schema into a GraphQL query.\n" + 
                "2. Use the appropriate fields and types defined in the API schema.\n" + 
                "3. If a query cannot be generated the input is unclear, respond with: \"I'm unable to generate a valid query based on the given input.\"\n" +
                "4. You should not answer any prompts that are not related to the schema.\n" +
                "5. You should return the most optimal query based on the input and schema.\n" +
                "\n" + 
                "Question:\n" + userMessage +
                "\n" + 
                "Generate the corresponding GraphQL query below in JSON format with 'query' and 'explanation' fields.\n" +
                "Output format:\n" +
                "{\n" +
                "  \"query\": \"<generated GraphQL query>\",\n" +
                "  \"explanation\": \"<brief explanation of how the query was generated based on the input and schema.>\"\n" +
                "}\n"
        );

        chatMemory.add(UserMessage.userMessage(userMessage));
        String response = model.generate(prompt);
        chatMemory.add(AiMessage.aiMessage(response));
        return response;
    }
}

package bankai.shizuki;

import dev.langchain4j.model.openai.OpenAiChatModel;

public class Main {
    public static void main(String[] args) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .modelName("gpt-4o-mini")
                .build();
        String answer = model.chat("Say 'Hello World'");
        System.out.println(answer); // Hello World
    }
}
package bankai.shizuku.ichigo.controller.prompt;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prompt")
public class PromptTest02Controller {

    @Value("classpath:/prompt/prompts/system-message.st")
    private Resource systemPromptFile;

    @jakarta.annotation.Resource(name = "openAiChatModel")
    private ChatModel deepSeekChatModel;


    @GetMapping("/test02")
    public Mono<String> promptTest02(@RequestParam(
                                             value = "message",
                                             required = false,
                                             defaultValue = "Tell me about three famous pirates from the Golden Age of Piracy and why they did. " +
                                                     " Write at least a sentence for each pirate.") String message,
                                     @RequestParam(value = "name", required = false, defaultValue = "Bob") String name,
                                     @RequestParam(value = "voice", required = false, defaultValue = "pirate") String voice) {
        UserMessage userMessage = new UserMessage(message);
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPromptFile);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", name, "voice", voice));
        Flux<ChatResponse> stream = deepSeekChatModel.stream(
                new Prompt(List.of(
                        userMessage,
                        systemMessage)));
        Mono<String> response = stream
                .flatMap(chatResponse -> Flux.fromIterable(chatResponse.getResults()))
                .map(result -> result.getOutput().getText())
                .collect(StringBuilder::new, StringBuilder::append)
                .map(StringBuilder::toString);
        return response;
    }


}

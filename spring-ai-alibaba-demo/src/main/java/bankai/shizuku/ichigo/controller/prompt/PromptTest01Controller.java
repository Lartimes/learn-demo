package bankai.shizuku.ichigo.controller.prompt;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@RestController
@RequestMapping("/prompt")
public class PromptTest01Controller {

    @Value("classpath:/prompt/docs/wikipedia-curling.md")
    private Resource wikipediaCurlingMd;

    @Value("classpath:/prompt/prompts/qa-prompt.st")
    private Resource qaPromptSt;

    @jakarta.annotation.Resource(name = "openAiChatModel")
    private ChatModel deepSeekChatModel;



    @GetMapping("/test01")
    public Mono<String> test(@RequestParam(
                                     value = "message",
                                     required = false,
                                     defaultValue = "Which athletes won the mixed doubles gold medal in curling at the 2022 Winter Olympics?'")
                             String message,
                             @RequestParam(value = "stuffit", defaultValue = "false") boolean enable
    ) {
        PromptTemplate promptTemplate = new PromptTemplate(qaPromptSt);
        promptTemplate.add("question", message);
        HashMap<String, Object> map = new HashMap<>();
        if (enable) {
            map.put("context", wikipediaCurlingMd);
        } else {
            map.put("context", "");
        }
        Prompt prompt = promptTemplate.create(map);
        System.out.println(prompt);
        Flux<ChatResponse> stream = deepSeekChatModel.stream(prompt);

        return stream
                .flatMap(chatResponse -> Flux.fromIterable(chatResponse.getResults()))
                .map(result -> result.getOutput().getText())
                .collect(StringBuilder::new, StringBuilder::append)
                .map(StringBuilder::toString);
//        Disposable subscribe = stream.subscribe(chatResponse -> {
//                    List<Generation> results = chatResponse.getResults();
//                    results.forEach(result ->{
//
//                         sb.append(result.getOutput().getText());
//                    });
//                },
//                error -> {
//                    // 处理错误
//                    System.err.println("Error occurred: " + error.getMessage());
//                },
//                () -> {
//                    // 处理流完成的信号
//                    System.out.println("Stream completed.");
//                    System.out.println(sb);
//                }
//        );
//
//        System.out.println("？？？？");
//        return sb.toString();
    }
}

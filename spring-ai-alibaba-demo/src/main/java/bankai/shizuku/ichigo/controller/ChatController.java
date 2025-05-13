package bankai.shizuku.ichigo.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    @Resource(name = "openAiChatModel")
    private ChatModel deepSeekChatModel;

    @Resource(name = "dashscopeChatModel")
    private ChatModel chatModel;




    @GetMapping("/{prompt}")
    public String chat(@PathVariable(value = "prompt") String prompt) {
        Flux<ChatResponse> stream = chatModel.stream(new Prompt(prompt + "不要输出思考过程，只需要给出结果：" + prompt));
        stream.subscribe(System.out::println);
        return prompt;
    }

}
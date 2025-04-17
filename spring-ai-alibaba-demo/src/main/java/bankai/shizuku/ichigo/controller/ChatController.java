package bankai.shizuku.ichigo.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    @Resource(name = "openAiChatModel")
    private ChatModel deepSeekChatModel;


    @GetMapping("/{prompt}")
    public Generation chat(@PathVariable(value = "prompt") String prompt) {

        ChatResponse chatResponse = deepSeekChatModel.call(new Prompt(prompt));
        return chatResponse.getResult();
    }

}
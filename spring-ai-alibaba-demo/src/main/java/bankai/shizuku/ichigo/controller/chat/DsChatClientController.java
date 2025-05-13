package bankai.shizuku.ichigo.controller.chat;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;

//@RestController

public class DsChatClientController {

    private static final String DEFAULT_PROMPT = "你好，介绍下你自己！";

    private final ChatClient deepseekChatClient;

	@Resource(name = "openAiChatModel")
	private ChatModel openAiChatModel;

	/**
	 * 最简单的使用方式，没有任何 LLMs 参数注入。
	 * @return String types.
	 */
	@GetMapping("/simple/chat")
	public String simpleChat2() {

		return openAiChatModel.call(new Prompt(DEFAULT_PROMPT, DashScopeChatOptions
				.builder()
				.withModel(DashScopeApi.ChatModel.QWEN_PLUS.getModel())
				.build())).getResult().getOutput().getText();
	}

	/**
	 * Stream 流式调用。可以使大模型的输出信息实现打字机效果。
	 * @return Flux<String> types.
	 */
	@GetMapping("/stream/chat")
	public Flux<String> streamChat2(HttpServletResponse response) {

		// 避免返回乱码
		response.setCharacterEncoding("UTF-8");

		ChatClient.ChatClientRequestSpec prompt = deepseekChatClient.prompt(new Prompt(DEFAULT_PROMPT, DashScopeChatOptions
				.builder()
				.withModel(DashScopeApi.ChatModel.QWEN_PLUS.getModel())
				.build()));
//		Flux<ChatResponse> stream = (Flux<ChatResponse>) prompt
        return prompt.stream().chatResponse().map(a -> a.getResult().getOutput().getText());
	}


	/**
	 * 使用编程方式自定义 LLMs ChatOptions 参数， {@link com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions}
	 * 优先级高于在 application.yml 中配置的 LLMs 参数！
	 */
	@GetMapping("/custom/chat")
	public String customChat() {

		DashScopeChatOptions customOptions = DashScopeChatOptions.builder()
				.withTopP(0.7)
				.withTopK(50)
				.withTemperature(0.8)
				.build();

//		return deepseekChatClient.prompt(new Prompt(DEFAULT_PROMPT, customOptions))
//				.stream().chatResponse().map( a -> a.getResult().getOutput().getText());
		return null;
	}



    public DsChatClientController(OpenAiChatModel openAiChatModel) {
        System.out.println(openAiChatModel);
        this.deepseekChatClient = ChatClient.builder(openAiChatModel)
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .topP(0.7)
                                .build()
                )
                .build();
    }
    /**
	 * ChatClient 简单调用
	 */
	@GetMapping("/simple/chat")
	public String simpleChat() {

		return deepseekChatClient.prompt(DEFAULT_PROMPT).call().content();
	}

	/**
	 * ChatClient 流式调用
	 */
	@GetMapping("/stream/chat")
	public Flux<String> streamChat(HttpServletResponse response) {

		response.setCharacterEncoding("UTF-8");
		return deepseekChatClient.prompt(DEFAULT_PROMPT).stream().content();
	}



}

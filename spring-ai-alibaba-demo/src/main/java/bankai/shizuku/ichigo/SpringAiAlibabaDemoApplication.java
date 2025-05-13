package bankai.shizuku.ichigo;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Map;

@SpringBootApplication
public class SpringAiAlibabaDemoApplication implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(SpringAiAlibabaDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, ChatModel> map = applicationContext.getBeansOfType(ChatModel.class);
        System.out.println("===============================chatModel");
        map.forEach((k, v) -> {
            System.out.println(k + " " + v);
        });
//        Map<String, ChatClient> map1 = applicationContext.getBeansOfType(ChatClient.class);
//        System.out.println("===============================chatClient");
//        map1.forEach((k, v) -> {
//            System.out.println(k + " " + v);
//        });

    }
}

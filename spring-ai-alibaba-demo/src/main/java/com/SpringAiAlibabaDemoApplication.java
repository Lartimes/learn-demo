package com;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.bankai.shizuku.ichigo.Rag;
import org.springframework.ai.document.MetadataMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

//com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeEmbeddingAutoConfiguration
@AutoConfigurationPackage(basePackages = "com.alibaba")
@SpringBootApplication
public class SpringAiAlibabaDemoApplication implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private DashScopeEmbeddingModel vectorModel;

    public static void main(String[] args) {
        SpringApplication.run(SpringAiAlibabaDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        rag.test();
        float[] embed = vectorModel.embed("你是谁");
        System.out.println(Arrays.toString(embed));
    }
    @Autowired
    private Rag rag;

    //    public DashScopeEmbeddingModel(DashScopeApi dashScopeApi, MetadataMode metadataMode) {
//		this(dashScopeApi, metadataMode,
//				DashScopeEmbeddingOptions.builder()
//					.withModel(DashScopeApi.DEFAULT_EMBEDDING_MODEL)
//					.withTextType(DashScopeApi.DEFAULT_EMBEDDING_TEXT_TYPE)
//					.build());
//	}
    @Bean
    public DashScopeEmbeddingModel vectorModel() {
        DashScopeApi scopeApi = DashScopeApi.builder()
                .baseUrl("https://dashscope.aliyuncs.com")
                .apiKey("sk-8b67da98807649bb9886327549af564f").build();
        return new DashScopeEmbeddingModel(scopeApi, MetadataMode.EMBED);
    }


}

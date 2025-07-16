package com.bankai.shizuku.ichigo;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class Rag {

    @Autowired
    private DashScopeEmbeddingModel dashScopeEmbeddingModel;


    public void test() {
        float[] embed = dashScopeEmbeddingModel.embed("你是谁");
        System.out.println(Arrays.toString(embed));
    }

}

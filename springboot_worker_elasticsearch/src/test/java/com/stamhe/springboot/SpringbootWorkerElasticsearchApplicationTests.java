package com.stamhe.springboot;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class SpringbootWorkerElasticsearchApplicationTests {
    
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    
    @Test
    void testIndexIsExist() throws IOException {
        GetIndexRequest req = new GetIndexRequest("person");
        boolean rsp = restHighLevelClient.indices().exists(req, RequestOptions.DEFAULT);
        System.out.println(rsp);
    }
    
}

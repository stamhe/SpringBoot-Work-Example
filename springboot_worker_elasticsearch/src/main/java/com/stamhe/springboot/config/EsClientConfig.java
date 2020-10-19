package com.stamhe.springboot.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author stamhe
 * @date 2020-09-21 14:58
 */
@Configuration
public class EsClientConfig {
    @Value("${es.server.bootstraps}")
    private String serverBootstraps;
    
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        String[] serverList = serverBootstraps.split(",");
        HttpHost[] httpHosts = new HttpHost[serverList.length];
        
        for (int i = 0; i < serverList.length; i++) {
            String[] srvInfo= serverList[i].split(":");
            String host     = srvInfo[0];
            Integer port    = Integer.parseInt(srvInfo[1]);
            httpHosts[i]    = new HttpHost(host, port);
        }
        
        // 无鉴权的
        RestClientBuilder builder = RestClient.builder(httpHosts);
        
        RestHighLevelClient client = new RestHighLevelClient(builder);
        
        return client;
    }
}

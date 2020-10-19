package com.stamhe.springboot.utils;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author stamhe
 * @date 2020-09-21 14:52
 */
@Component
@Slf4j
public class EsUtils {
    
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    /**
     * 获取用户存储在es中的数据
     * @param userId ，注意：在es中需要用户id作为文档的id
     * @return
     */
    public Map<String, Object> getEsData(final Long userId, String esIndex) {
        String id = String.valueOf(userId);
        GetRequest getRequest = new GetRequest(esIndex, "_doc", id);
        Map<String, Object> result = null;
        try {
            GetResponse rsp = null;
            rsp = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            
            if (rsp.isExists()) {
                result = rsp.getSourceAsMap();
//                log.info("es id = " + id + ", result = " + rsp.getSourceAsString());
            } else {
                log.error("user not exist in es. " + userId);
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        
        return result;
    }
}

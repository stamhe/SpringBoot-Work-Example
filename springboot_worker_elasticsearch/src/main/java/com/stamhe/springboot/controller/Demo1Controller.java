package com.stamhe.springboot.controller;

import com.alibaba.fastjson.JSON;
import com.stamhe.springboot.bean.Person;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.BoostingQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ExtendedStats;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stamhe
 * @date 2020-10-16 17:33
 *
 * 索引结构
PUT /book
{
"settings": {
"number_of_shards" : 1,
"number_of_replicas": 1
},
"mappings": {
"properties": {
"name": {
"type": "text",
"analyzer": "ik_max_word",
"index": true
},
"author": {
"type": "keyword"
},
"wordcount": {
"type": "long"
},
"onsale": {
"type": "date",
"format": "yyyy-MM-dd"
},
"desc": {
"type": "text",
"analyzer": "ik_max_word"
}
}
}
}
 */
@RestController
@RequestMapping("/demo1")
public class Demo1Controller {
    
    public static final String SUCCESS = "success";
    
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    
    @RequestMapping("/getone/{id}")
    public String getoneAction(@PathVariable("id")String id) {
        return SUCCESS;
    }
    
    @RequestMapping("/createindex/{name}")
    public String createindexAction(@PathVariable("name")String name) throws IOException {
        // 1. 准备索引的 settings
        Settings settings = Settings.builder()
                .put("number_of_shards", 1)
                .put("number_of_replicas", 1).build();
        
        // 2. 准备关于索引的结构 mappings
        XContentBuilder mappings = JsonXContent.contentBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("name")
                            .field("type", "text")
                            .field("index", true)
                            .field("analyzer", "ik_max_word")
                        .endObject()
                        .startObject("author")
                            .field("type", "keyword")
                        .endObject()
                        .startObject("wordcount")
                            .field("type", "long")
                        .endObject()
                        .startObject("onsale")
                            .field("type", "date")
                            .field("format", "yyyy-MM-dd")
                        .endObject()
                        .startObject("desc")
                            .field("type", "text")
                            .field("analyzer", "ik_max_word")
                        .endObject()
                    .endObject()
                .endObject();
        
        // 3. 封装参数
        CreateIndexRequest request = new CreateIndexRequest(name)
                .settings(settings)
                .mapping(mappings);
        
        // 4. 通过 client 对象去操作 es
        CreateIndexResponse rsp = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(rsp);
    
        return SUCCESS;
    }
    
    /**
     * 检查索引是否存在
     * @param name
     * @return
     * @throws IOException
     */
    @RequestMapping("/indexexist/{name}")
    public String indexexistAction(@PathVariable("name")String name) throws IOException {
        GetIndexRequest req = new GetIndexRequest(name);
        boolean rsp = restHighLevelClient.indices().exists(req, RequestOptions.DEFAULT);
        System.out.println(rsp);
        return SUCCESS;
    }
    
    @RequestMapping("/deleteindex/{name}")
    public String deleteindexAction(@PathVariable("name")String name) throws IOException {
        DeleteIndexRequest req = new DeleteIndexRequest(name);
    
        AcknowledgedResponse rsp = restHighLevelClient.indices().delete(req, RequestOptions.DEFAULT);
        System.out.println(rsp);
        System.out.println(rsp.isAcknowledged());
    
        return SUCCESS;
    }
    
    /**
     * 新建文档
     * @return
     */
    @RequestMapping("/createdoc")
    public String createdocAction() throws IOException {
        // 1. 准备一个 json 数据
        Person person = new Person();
        person.setName("来自于Idea");
        person.setAuthor("我吃西红柿");
        person.setOnsale("2020-10-16");
        person.setWordcount(10000L);
        person.setDesc("来自于 stamhe 的 ElasticSearch 测试用例");
    
        String json = JSON.toJSONString(person);
    
        // 2. 准备一个 request 对象
        IndexRequest req = new IndexRequest();
        // 指定索引
        req.index("book");
        // 指定 id 或者 不指定 id
//        req.id(UUID.randomUUID().toString());
        
        req.source(json, XContentType.JSON);
        // 3. 通过 client 对象执行添加
        IndexResponse rsp = restHighLevelClient.index(req, RequestOptions.DEFAULT);
        
        // 4. 输出返回结果
        System.out.println(rsp.getResult().toString());
        
        return SUCCESS;
    }
    
    /**
     * 修改 或 更新文档
     */
    @RequestMapping("/updatedoc")
    public String updatedocAction() throws IOException {
        // 1. 创建一个 Map，指定要修改的内容
        Map<String, Object> doc = new HashMap<>();
        doc.put("name", "来自于全仔的修改更新");
        
        // 2. 创建 request 对象
        String index = "book";
        String id = "PnshMXUBsGP4ix-Xpt85";
        UpdateRequest req = new UpdateRequest(index, id);
        req.doc(doc);
        
        // 3. 通过 client 对象执行
        UpdateResponse rsp = restHighLevelClient.update(req, RequestOptions.DEFAULT);
        System.out.println(rsp.getResult().toString());
    
        return SUCCESS;
    }
    
    /**
     * 删除文档
     */
    @RequestMapping("/deletedoc/{id}")
    public String deletedocAction(@PathVariable("id")String id) throws IOException {
        String index = "book";
        DeleteRequest req = new DeleteRequest(index, id);
        DeleteResponse rsp = restHighLevelClient.delete(req, RequestOptions.DEFAULT);
        System.out.println(rsp.getResult().toString());
        return  SUCCESS;
    }
    
    /**
     * 批量添加
     */
    @RequestMapping("/batchcreatedoc")
    public String batchcreatedocAction() throws IOException {
        // 1. 准备多个 json 数据
    
        Person p1 = new Person("名称1", "我吃西红柿", 10001L, "2020-10-16", "描述1描述1");
        Person p2 = new Person("名称2", "我吃西红柿", 10002L, "2020-10-16", "描述2描述2");
        Person p3 = new Person("名称3", "我吃西红柿", 10003L, "2020-10-16", "描述3描述3");
        
        // 2. 创建 BulkRequest 批量操作对象，封装
        String json1 = JSON.toJSONString(p1);
        String json2 = JSON.toJSONString(p2);
        String json3 = JSON.toJSONString(p3);
        
        String index = "book";
        BulkRequest req = new BulkRequest();
        req.add(new IndexRequest().index(index).source(json1, XContentType.JSON));
        req.add(new IndexRequest().index(index).source(json2, XContentType.JSON));
        req.add(new IndexRequest().index(index).source(json3, XContentType.JSON));
        
        // 3. client 执行
        BulkResponse rsp = restHighLevelClient.bulk(req, RequestOptions.DEFAULT);
        System.out.println(rsp);
    
        return SUCCESS;
    }
    
    /**
     * 批量删除
     */
    @RequestMapping("batchdeletedoc")
    public String batchdeletedocAction() throws IOException {
        String index = "book";
        BulkRequest req = new BulkRequest();
        req.add(new DeleteRequest(index, "id1"));
        req.add(new DeleteRequest(index, "id2"));
        req.add(new DeleteRequest(index, "id3"));
    
        BulkResponse rsp = restHighLevelClient.bulk(req, RequestOptions.DEFAULT);
        System.out.println(rsp);
    
        return SUCCESS;
    }
    
    /**
     * term 查询
     */
    @RequestMapping("/term")
    public String termAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0);
        builder.size(10);
        builder.query(QueryBuilders.termQuery("author", "我吃西红柿"));
    
        req.source(builder);
        
        // 查询
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        
        // 获取 _source 中的数据
        for (SearchHit hit : rsp.getHits().getHits()) {
            Map<String, Object> result = hit.getSourceAsMap();
            System.out.println(result);
        }
    
        return SUCCESS;
    }
    
    /**
     * terms 查询
     */
    @RequestMapping("/terms")
    public String termsAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
    
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0);
        builder.size(20);
        builder.query(QueryBuilders.termsQuery("author", "我吃西红柿", "authro1"));
    
        req.source(builder);
    
        // 查询
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
    
        // 获取 _source 中的数据
        for (SearchHit hit : rsp.getHits().getHits()) {
            Map<String, Object> result = hit.getSourceAsMap();
            System.out.println(result);
        }
        
        return SUCCESS;
    }
    
    /**
     * matchall 查询
     */
    @RequestMapping("/matchall")
    public String matchallAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchAllQuery());
        // 分页相关
        builder.from(0);
        builder.size(100);
        
        req.source(builder);
    
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    
        return SUCCESS;
    }
    
    /**
     * match 查询
     */
    @RequestMapping("/match")
    public String matchAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
    
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery("name", "盘龙3"));
        
        // 分页相关
        builder.from(0);
        builder.size(100);
    
        req.source(builder);
    
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * match 布尔查询
     */
    @RequestMapping("/matchbool")
    public String matchboolAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery("name", "盘龙 3").operator(Operator.AND));
//        builder.query(QueryBuilders.matchQuery("name", "盘龙 3").operator(Operator.OR));
        
        // 分页相关
        builder.from(0);
        builder.size(100);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * multi_match 查询
     */
    @RequestMapping("/multimatch")
    public String multimatchAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.multiMatchQuery("盘龙3", "name", "desc"));
        
        // 分页相关
        builder.from(0);
        builder.size(100);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * id 查询
     */
    @RequestMapping("/id")
    public String idAction() throws IOException {
        String index = "book";
        String id = "0XtYNHUBsGP4ix-XxfEv";
    
        GetRequest req = new GetRequest(index, id);
        GetResponse rsp = restHighLevelClient.get(req, RequestOptions.DEFAULT);
        System.out.println(rsp.getSourceAsMap());
    
        return  SUCCESS;
    }
    
    /**
     * ids 查询
     */
    @RequestMapping("/ids")
    public String idsAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.idsQuery().addIds("2XutMHUBsGP4ix-X89Pa", "0XtYNHUBsGP4ix-XxfEv"));
        
        // 分页相关
        builder.from(0);
        builder.size(100);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * prefix 查询
     */
    @RequestMapping("/prefix")
    public String prefixAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.prefixQuery("author", "我"));
        
        // 分页相关
        builder.from(0);
        builder.size(100);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * fuzzy 模糊查询
     */
    @RequestMapping("/fuzzy")
    public String fuzzyAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.fuzzyQuery("author", "authorr").prefixLength(3));
        
        // 分页相关
        builder.from(0);
        builder.size(100);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * wildcard 通配查询
     */
    @RequestMapping("/wildcard")
    public String wildcardAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.wildcardQuery("name", "名称*"));
        
        // 分页相关
        builder.from(0);
        builder.size(100);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * range 范围查询(针对数值类型)
     */
    @RequestMapping("/range")
    public String rangeAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.rangeQuery("wordcount").gte(10001).lte(10002));
        
        // 分页相关
        builder.from(0);
        builder.size(100);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * scroll 深分页 - 第一页
     */
    @RequestMapping("/scroll")
    public String scrollAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
        
        // 指定 scroll 缓存信息
        req.scroll(TimeValue.timeValueMinutes(10L));
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchAllQuery());
        
        // 分页相关
        builder.from(0);
        builder.size(3);
        // 排序方式
        builder.sort("wordcount", SortOrder.DESC);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("scroll id = " + rsp.getScrollId());
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * scroll 深分页 - 下一页
     */
    @RequestMapping("/scrollnext/{scrollid}")
    public String scrollnextAction(@PathVariable("scrollid")String scrollid) throws IOException {
    
        SearchScrollRequest req = new SearchScrollRequest(scrollid);
        req.scroll(TimeValue.timeValueMinutes(10L));
    
        SearchResponse rsp = restHighLevelClient.scroll(req, RequestOptions.DEFAULT);
        System.out.println("--------- scroll 下一页 ---------");
        if(rsp.getHits().getHits() != null && rsp.getHits().getHits().length > 0) {
            for (SearchHit hit : rsp.getHits().getHits()) {
                System.out.println(hit.getSourceAsMap());
            }
        } else {
            System.out.println("没有数据了");
        }
        
        return  SUCCESS;
    }
    
    /**
     * scroll 深分页 - 删除 scrollid
     */
    @RequestMapping("/scrolldelete/{scrollid}")
    public String scrolldeleteAction(@PathVariable("scrollid")String scrollid) throws IOException {
        ClearScrollRequest req = new ClearScrollRequest();
        req.addScrollId(scrollid);
    
        ClearScrollResponse rsp = restHighLevelClient.clearScroll(req, RequestOptions.DEFAULT);
        System.out.println(rsp.isSucceeded());
    
    
        return  SUCCESS;
    }
    
    /**
     * delete by query - 范围删除
     */
    @RequestMapping("/deletebyquery")
    public String deletebyqueryAction() throws IOException {
        String index = "book";
        DeleteByQueryRequest req = new DeleteByQueryRequest(index);
        req.setQuery(QueryBuilders.rangeQuery("wordcount").lte(1000));
    
        BulkByScrollResponse rsp = restHighLevelClient.deleteByQuery(req, RequestOptions.DEFAULT);
        System.out.println("total delete = " + rsp.getTotal());
    
    
        return  SUCCESS;
    }
    
    /**
     * bool查询
     *
     * https://www.cnblogs.com/ljhdo/p/5040252.html
     * https://www.elastic.co/guide/cn/elasticsearch/guide/current/bool-query.html
     * # 查询 author 为【我是测试啊】或者【我吃西红柿】
     * # 且 wordcount > 100001
     * # 且 desc 中包含【盘龙】
     * #  "minimum_should_match": 1  控制最少需要匹配多少个 should。当有 must 时， should 不是必须匹配，当没有 must 时，should 才是必须匹配
     */
    @RequestMapping("/querybool")
    public String queryboolAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
        
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.termQuery("author", "我是测试啊"));
        boolQueryBuilder.should(QueryBuilders.termQuery("author", "我吃西红柿"));
        boolQueryBuilder.minimumShouldMatch(1);
    
        boolQueryBuilder.must(QueryBuilders.rangeQuery("wordcount").gt(100001));
        boolQueryBuilder.must(QueryBuilders.matchQuery("desc", "盘龙"));
    
        builder.query(boolQueryBuilder);
        builder.from(0);
        builder.size(3);
    
        req.source(builder);
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    
        return  SUCCESS;
    }
    
    /**
     * boost
     * # boosting 查询: 影响查询后的 score
     * # positive: 只有匹配上了positive 的查询内容，才会被放到返回的结果集中
     * # negative: 如果匹配上positive并且也匹配上 negative，就可以降低这样的文档 score
     * # negative_boost: 指定系数,必须小于 1.0
     */
    @RequestMapping("/boost")
    public String boostAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
        
        SearchSourceBuilder builder = new SearchSourceBuilder();
        // name 中必须出现，但是如果 desc 中出现了，就降权.
        BoostingQueryBuilder boostingQuery = QueryBuilders.boostingQuery(
                QueryBuilders.matchQuery("name", "盘龙3"),
                QueryBuilders.matchQuery("name", 4))
                .negativeBoost(0.5f);
    
        builder.query(boostingQuery);
        
        req.source(builder);
    
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * filter
     * # query: 根据查询条件去计算文档的匹配度得到一个分数，并且根据分数进行排序，不会做缓存的
     * # filter: 根据查询条件去查询文档，不计算分数，对会经常过滤的数据进行缓存
     */
    @RequestMapping("/filter")
    public String filterAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
        
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("author", "我吃西红柿"))
                .filter(QueryBuilders.rangeQuery("wordcount").gte(100000).lte(100003));
    
        builder.query(boolQueryBuilder);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * highlight
     # fragment_size: 指定高亮数据展示多少个字符回来
     # pre_tags: 指定前缀标签，举例：<font color="red">
     # post_tags: 指定后缀标签， 举例: </font>
     # fileds: 指定哪些字段高亮返回
     */
    @RequestMapping("/highlight")
    public String highlightAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
        
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery("desc", "描述"));
    
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("desc", 2, 5)
                .preTags("<font color='red'>")
                .postTags("</font>");
        builder.highlighter(highlightBuilder);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getHighlightFields().toString());
        }
        
        return  SUCCESS;
    }
    
    /**
     * # 聚合查询
     * # cardinality, 去重计数，将文档中的一个指定的 field 进行去重，然后统计 field 的数量.
     * # 统计有多少个作者
     */
    @RequestMapping("/cardinality")
    public String cardinalityAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
    
        SearchSourceBuilder builder = new SearchSourceBuilder();
        CardinalityAggregationBuilder aggregationBuilder = AggregationBuilders.cardinality("author_count")
                .field("author");
    
        builder.aggregation(aggregationBuilder);
    
        req.source(builder);
    
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
    
        Cardinality agg = rsp.getAggregations().get("author_count");
        System.out.println("author_count = " + agg.getValue());
    
        return  SUCCESS;
    }
    
    /**
     * 聚合查询
     * # 范围统计，一定范围内的文档个数，针对某一个 field 的值的多个范围(如0-100, 100-200， 时间，ip)等统计文档个数
     * # 数值: range, 时间: date_range, ip: ip_range
     * # from: 包含， to: 不包含
     */
    @RequestMapping("/aggsrange")
    public String aggsrangeAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
    
        SearchSourceBuilder builder = new SearchSourceBuilder();
        RangeAggregationBuilder aggregationBuilder = AggregationBuilders.range("wordcount_count")
                .field("wordcount")
                .addUnboundedTo(10002)
                .addRange(100001, 100006);
    
        builder.aggregation(aggregationBuilder);
    
        req.source(builder);
    
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
    
        Range agg = rsp.getAggregations().get("wordcount_count");
        agg.getBuckets().stream().forEach(bucket -> {
            String key = bucket.getKeyAsString();
            Object from = bucket.getFrom();
            Object to = bucket.getTo();
            long count = bucket.getDocCount();
            System.out.println("key = " + key + ", from = " + from + ", to = " + to + ", count = " + count);
        });
    
        return  SUCCESS;
    }
    
    /**
     * # 聚合查询
     * # 统计聚合: 总条数，最大值，最小值，平均值，和
     */
    @RequestMapping("/aggsstat")
    public String aggsstatAction() throws IOException {
        String index = "book";
        SearchRequest req = new SearchRequest(index);
    
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.aggregation(AggregationBuilders.extendedStats("wordcount_stat").field("wordcount"));
    
        req.source(builder);
    
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
    
        ExtendedStats agg = rsp.getAggregations().get("wordcount_stat");
        double max = agg.getMax();
        double min = agg.getMin();
        double sum = agg.getSum();
        double avg = agg.getAvg();
        System.out.println("max = " + max + ", min = " + min + ", sum = " + sum + ", avg = " + avg);
    
        return  SUCCESS;
    }
    
    /**
     * GEO 地图经纬度检索
     * https://lbs.amap.com/console/show/picker
     * # geo 检索方式之一: geo_distance 半径检索方式，获取以此半径为圆内的全部数据
     * # geo 检索方式之二: geo_bounding_box: 以两个点确定一个矩形，获取矩形内的全部数据
     * # geo 检索方式之三: geo_polygon 以多个点确定一个多边形，获取这个多边形内的全部数据
     */
    @RequestMapping("/geo")
    public String geoAction() throws IOException {
        String index = "map";
        SearchRequest req = new SearchRequest(index);
    
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        List<GeoPoint> points = new ArrayList<>();
        points.add(new GeoPoint(31.249878d, 121.455208d));
        points.add(new GeoPoint(31.237109d, 121.443364d));
        points.add(new GeoPoint(31.216705d, 121.442505d));
        points.add(new GeoPoint(31.220375d, 121.523186d));
        
        boolQueryBuilder.must(QueryBuilders.matchAllQuery())
                .filter(QueryBuilders.geoPolygonQuery("location", points));
        builder.query(boolQueryBuilder);
        
        req.source(builder);
    
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    
        return  SUCCESS;
    }
}

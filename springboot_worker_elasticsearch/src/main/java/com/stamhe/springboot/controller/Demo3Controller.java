package com.stamhe.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stamhe.springboot.bean.Book;
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
import java.util.*;

/**
 * @author stamhe
 * @date 2020-12-09 19:07
 *
 */
@RestController
@RequestMapping("/demo3")
public class Demo3Controller {
    public static final String SUCCESS = "success";
    public static final String INDEX = "book_20201210";
    
    ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    
    @RequestMapping("/createindex")
    private String createindexAction() throws Exception {
        // 1. 准备索引的 settings
        Settings.Builder settings = Settings.builder()
                .put("number_of_shards", 3)
                .put("number_of_replicas", 1);
    
        // 2. 准备索引的 mappings
        XContentBuilder mappings = JsonXContent.contentBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("name")
                            .field("type", "text")
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
                            .field("format", "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd")
                        .endObject()
                        .startObject("desc")
                            .field("type", "text")
                            .field("analyzer", "ik_max_word")
                        .endObject()
                    .endObject()
                .endObject();
        // 3. 将 settings 和 mappings 封装到 request 对象中
        CreateIndexRequest req = new CreateIndexRequest(INDEX)
                .settings(settings)
                .mapping(mappings);
        
        
        // 4. 连接 es 并创建索引
        CreateIndexResponse rsp = restHighLevelClient.indices().create(req, RequestOptions.DEFAULT);
        System.out.println(rsp.toString());
        return SUCCESS;
    }
    
    @RequestMapping("/existindex")
    public String existindexAction() throws IOException {
        GetIndexRequest req = new GetIndexRequest(INDEX);
        boolean rsp = restHighLevelClient.indices().exists(req, RequestOptions.DEFAULT);
        System.out.println(rsp);
        return SUCCESS;
    }
    
    @RequestMapping("/deleteindex")
    public String deleteindexAction() throws IOException {
        DeleteIndexRequest req = new DeleteIndexRequest(INDEX);
        AcknowledgedResponse rsp = restHighLevelClient.indices().delete(req, RequestOptions.DEFAULT);
        System.out.println(rsp.isAcknowledged());
        return SUCCESS;
    }
    
    @RequestMapping("/createdoc")
    public String createdocAction() throws IOException {
        // 1. 准备 json 数据
        Book book = new Book(10004, "标题04", "java吃西红柿", 1004L, new Date(), "我爱吃西红柿, 来自于java代码的文档描述-004");
        String jsonString = mapper.writeValueAsString(book);
        
        
        // 2. 准备 request 对象
        IndexRequest req = new IndexRequest(INDEX);
        req.source(jsonString, XContentType.JSON);
        // 指定 id 或者 不指定 id
//        req.id(UUID.randomUUID().toString());
        
        // 3. 通过 client 执行
        IndexResponse rsp = restHighLevelClient.index(req, RequestOptions.DEFAULT);
        System.out.println(rsp.getResult().toString());
        System.out.println(rsp.getId());
        return SUCCESS;
    }
    
    @RequestMapping("/updatedoc")
    public String updatedocAction() throws IOException {
        // 1. 创建一个 map， 指定要修改的内容
        Map<String, Object> doc = new HashMap<>();
        doc.put("name", "标题03-update-01");
        
        // 2. 创建 request 对象
        String id = "UDWzS3YBqhYaewchvqeR";
        UpdateRequest req = new UpdateRequest(INDEX, id);
        req.doc(doc);
        
        // 3. 通过 client 执行
        UpdateResponse rsp = restHighLevelClient.update(req, RequestOptions.DEFAULT);
        System.out.println(rsp.getResult().toString());
        
        return SUCCESS;
    }
    
    @RequestMapping("/deletedoc")
    public String deletedocAction() throws IOException {
        String id = "63zWP3UBsGP4ix-XUso3";
        DeleteRequest req = new DeleteRequest(INDEX, id);
        
        DeleteResponse rsp = restHighLevelClient.delete(req, RequestOptions.DEFAULT);
        System.out.println(rsp.getResult().toString());
        
        return SUCCESS;
    }
    
    @RequestMapping("/batchcreatedoc")
    public String batchcreatedocAction() throws IOException {
        Book a1 = new Book(10001, "标题-name-16", "我很爱西红柿", 1016L, new Date(), "测试-批量-016");
        Book a2 = new Book(10002, "标题-name-17", "我很爱西红柿", 1017L, new Date(), "测试-批量-017");
        Book a3 = new Book(10003, "标题-name-18", "我很爱西红柿", 1018L, new Date(), "测试-批量-018");
    
    
        String json1 = mapper.writeValueAsString(a1);
        String json2 = mapper.writeValueAsString(a2);
        String json3 = mapper.writeValueAsString(a3);
        
        BulkRequest req = new BulkRequest();
        req.add(new IndexRequest(INDEX).source(json1, XContentType.JSON));
        req.add(new IndexRequest(INDEX).source(json2, XContentType.JSON));
        req.add(new IndexRequest(INDEX).source(json3, XContentType.JSON));
        
        BulkResponse rsp = restHighLevelClient.bulk(req, RequestOptions.DEFAULT);
        System.out.println(rsp.toString());
        
        
        return SUCCESS;
    }
    
    @RequestMapping("/batchdeletedoc")
    public String batchdeletedocAction() throws IOException {
        
        String id1 = "B3wIQHUBsGP4ix-XBNCn";
        String id2 = "CHwIQHUBsGP4ix-XBNCn";
        
        BulkRequest req = new BulkRequest();
        req.add(new DeleteRequest(INDEX, id1));
        req.add(new DeleteRequest(INDEX, id2));
        
        BulkResponse rsp = restHighLevelClient.bulk(req, RequestOptions.DEFAULT);
        System.out.println(rsp.toString());
        
        return SUCCESS;
    }
    
    /**
     * term - 精确匹配(全文完全匹配)
     * 不会对搜索的关键字进行分词.
     * where author=我吃西红柿
     * @return
     */
    @RequestMapping("/term")
    public String termAction() throws IOException {
        // 1. 创建 request 对象
        SearchRequest req = new SearchRequest(INDEX);
        
        // 2. 指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0).size(100);
        builder.query(QueryBuilders.termQuery("author", "我吃西红柿"));
        
        req.source(builder);
        
        // 3. 执行查询
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        
        System.out.println("total hit = " + rsp.getHits().getTotalHits().value);
        
        // 4. 获取数据
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return SUCCESS;
    }
    
    /**
     * terms - 精确匹配(全文完全匹配)
     * 不会对搜索的关键字进行分词.
     * where author=我吃西红柿 or author=曹雪芹
     * @return
     */
    @RequestMapping("/terms")
    public String termsAction() throws IOException {
        // 1. 创建 request 对象
        SearchRequest req = new SearchRequest(INDEX);
        
        // 2. 指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0).size(100);
        builder.query(QueryBuilders.termsQuery("author", "我吃西红柿", "曹雪芹"));
        
        req.source(builder);
        
        // 3. 执行查询
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        
        System.out.println("total hit = " + rsp.getHits().getTotalHits().value);
        
        // 4. 获取数据
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        
        return SUCCESS;
    }
    
    /**
     * match_all 查询全部
     * 如果查询的字段是 keyword，则 match 查询不会对你指定的查询关键字进行分词.
     * @return
     * @throws IOException
     */
    @RequestMapping("/matchall")
    public String matchallAction() throws IOException {
        // 1. 创建 request 对象
        SearchRequest req = new SearchRequest(INDEX);
        
        // 2. 指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0).size(100);
        builder.query(QueryBuilders.matchAllQuery());
        
        req.source(builder);
        
        // 3. 执行查询
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        
        System.out.println("total hit = " + rsp.getHits().getTotalHits().value);
        
        // 4. 获取数据
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return SUCCESS;
    }
    
    /**
     * match 查询
     * 如果查询的字段是 keyword，则 match 查询不会对你指定的查询关键字进行分词.
     * match 查询的底层还是多个 term 查询，然后将多个 term 查询的结果进行了封装.
     * @return
     * @throws IOException
     */
    @RequestMapping("/match")
    public String matchAction() throws IOException {
        // 1. 创建 request 对象
        SearchRequest req = new SearchRequest(INDEX);
        
        // 2. 指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0).size(100);
        builder.query(QueryBuilders.matchQuery("name", "批量"));
        builder.sort("wordcount", SortOrder.DESC);
        
        req.source(builder);
        
        // 3. 执行查询
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        
        System.out.println("total hit = " + rsp.getHits().getTotalHits().value);
        
        // 4. 获取数据
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return SUCCESS;
    }
    
    /**
     * bool match 查询
     * 如果查询的字段是 keyword，则 match 查询不会对你指定的查询关键字进行分词.
     * @return
     * @throws IOException
     */
    @RequestMapping("/boolmatch")
    public String boolmatchAction() throws IOException {
        // 1. 创建 request 对象
        SearchRequest req = new SearchRequest(INDEX);
        
        // 2. 指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0).size(100);
        // 基于指定的搜索关键字的内容采取 and 或者 or 策略
        builder.query(QueryBuilders.matchQuery("name", "标题  name").operator(Operator.AND));
        builder.sort("wordcount", SortOrder.DESC);
        
        req.source(builder);
        
        // 3. 执行查询
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        
        System.out.println("total hit = " + rsp.getHits().getTotalHits().value);
        
        // 4. 获取数据
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return SUCCESS;
    }
    
    /**
     * multi_match 针对多个 field 进行检索
     * @return
     * @throws IOException
     */
    @RequestMapping("/multimatch")
    public String multimatchAction() throws IOException {
        // 1. 创建 request 对象
        SearchRequest req = new SearchRequest(INDEX);
        
        // 2. 指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0).size(100);
        // 基于指定的搜索关键字的内容采取 and 或者 or 策略
        builder.query(QueryBuilders.multiMatchQuery("西红柿", "name", "desc"));
        builder.sort("wordcount", SortOrder.DESC);
        
        req.source(builder);
        
        // 3. 执行查询
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        
        System.out.println("total hit = " + rsp.getHits().getTotalHits().value);
        
        // 4. 获取数据
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return SUCCESS;
    }
    
    /**
     * id 查询
     * 类似 mysql 中的 where id = id1;
     */
    @RequestMapping("/id")
    public String idAction() throws IOException {
        String id = "lnzzPnUBsGP4ix-Xt7T5";
        
        GetRequest req = new GetRequest(INDEX, id);
        GetResponse rsp = restHighLevelClient.get(req, RequestOptions.DEFAULT);
        System.out.println(rsp.getSourceAsMap());
        
        return  SUCCESS;
    }
    
    /**
     * ids 查询
     * 类似 mysql 中的 where id in (id1, id2, id3...)
     */
    @RequestMapping("/ids")
    public String idsAction() throws IOException {
        SearchRequest req = new SearchRequest(INDEX);
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.idsQuery().addIds("Y3zxPnUBsGP4ix-X5LQy", "dnzyPnUBsGP4ix-XmLTC"));
        
        // 分页相关
        builder.from(0);
        builder.size(100);
        // 排序相关
        builder.sort("wordcount", SortOrder.DESC);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * prefix 前缀查询
     * 和 match 的区别: 对 keyword 的类型，非常有用。
     */
    @RequestMapping("/prefix")
    public String prefixAction() throws IOException {
        SearchRequest req = new SearchRequest(INDEX);
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.prefixQuery("author", "java"));
        
        // 分页相关
        builder.from(0);
        builder.size(100);
        // 排序相关
        builder.sort("wordcount", SortOrder.DESC);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * fuzzy - 模糊查询
     * prefix_length 指定前面几个字符是确定正确
     * @return
     * @throws IOException
     */
    @RequestMapping("/fuzzy")
    public String fuzzyAction() throws IOException {
        SearchRequest req = new SearchRequest(INDEX);
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.fuzzyQuery("name", "标题-").prefixLength(2));
        
        // 分页相关
        builder.from(0);
        builder.size(100);
        // 排序相关
        builder.sort("wordcount", SortOrder.DESC);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * wildcard 查询 - 针对 keyword
     *
     * 可以在查询时，在字符串中指定通配符 * 和 占位符 ?
     *  * 多个字符匹配， ? 单个字符匹配
     * @return
     * @throws IOException
     */
    @RequestMapping("/wildcard")
    public String wildcardAction() throws IOException {
        SearchRequest req = new SearchRequest(INDEX);
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.wildcardQuery("author", "*西红柿"));
        
        // 分页相关
        builder.from(0);
        builder.size(100);
        // 排序相关
        builder.sort("wordcount", SortOrder.DESC);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * range 范围查询
     * 针对数值类型
     * @return
     * @throws IOException
     */
    @RequestMapping("/range")
    public String rangeAction() throws IOException {
        SearchRequest req = new SearchRequest(INDEX);
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.rangeQuery("wordcount").gte(1003).lte(1010));
        
        // 分页相关
        builder.from(0);
        builder.size(100);
        // 排序相关
        builder.sort("wordcount", SortOrder.DESC);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * scroll 深分页
     * es 对 from + size 有限制，from 和 size 二者之和不能超过 1W，性能很低下。
     * @return
     * @throws IOException
     */
    @RequestMapping("/scroll")
    public String scrollAction() throws IOException {
        SearchRequest req = new SearchRequest(INDEX);
        // 指定 scroll 缓存信息
        req.scroll(TimeValue.timeValueMinutes(10L));
        
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchAllQuery());
        
        // 分页相关
        builder.from(0);
        builder.size(3);
        // 排序相关
        builder.sort("wordcount", SortOrder.DESC);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("scroll_id = " + rsp.getScrollId());
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return SUCCESS;
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
        System.out.println("scroll 删除结果: " + rsp.isSucceeded());
        
        return  SUCCESS;
    }
    
    /**
     * delete by query - 范围删除
     */
    @RequestMapping("/deletebyquery")
    public String deletebyqueryAction() throws IOException {
        DeleteByQueryRequest req = new DeleteByQueryRequest(INDEX);
        req.setQuery(QueryBuilders.rangeQuery("wordcount").lte(1000));
        
        BulkByScrollResponse rsp = restHighLevelClient.deleteByQuery(req, RequestOptions.DEFAULT);
        System.out.println("total delete = " + rsp.getTotal());
        
        return  SUCCESS;
    }
    
    /**
     * bool查询
     * https://www.cnblogs.com/ljhdo/p/5040252.html
     * https://www.elastic.co/guide/cn/elasticsearch/guide/current/bool-query.html
     * # 查询 author 为【曹雪芹】或者【我吃西红柿】
     * # 且 wordcount > 1003
     * # 且 desc 中包含【描述】
     * # "minimum_should_match": 1  控制最少需要匹配多少个 should。
     * 注意：当有 must 时， should 不是必须匹配，当没有 must 时，should 才是必须匹配
     */
    @RequestMapping("/boolquery")
    public String boolqueryAction() throws IOException {
        SearchRequest req = new SearchRequest(INDEX);
        
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.termQuery("author", "曹雪芹"));
        boolQueryBuilder.should(QueryBuilders.termQuery("author", "我吃西红柿"));
        boolQueryBuilder.minimumShouldMatch(1);
        
        boolQueryBuilder.must(QueryBuilders.rangeQuery("wordcount").gte(1003));
        boolQueryBuilder.must(QueryBuilders.matchQuery("desc", "描述"));
        
        builder.query(boolQueryBuilder);
        builder.from(0);
        builder.size(100);
        // 排序相关
        builder.sort("wordcount", SortOrder.DESC);
        
        req.source(builder);
        
        SearchResponse rsp = restHighLevelClient.search(req, RequestOptions.DEFAULT);
        System.out.println("total hit = " + rsp.getHits().getHits().length);
        
        for (SearchHit hit : rsp.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        
        return  SUCCESS;
    }
    
    /**
     * boost
     * # boosting 查询: 影响查询后的 score
     * # positive: 只有匹配上了 positive 的查询内容，才会被放到返回的结果集中
     * # negative: 如果匹配上 positive 并且也匹配上 negative，就可以降低这样的文档 score
     * # negative_boost: 指定系数,必须小于 1.0
     */
    @RequestMapping("/boost")
    public String boostAction() throws IOException {
        SearchRequest req = new SearchRequest(INDEX);
        
        SearchSourceBuilder builder = new SearchSourceBuilder();
        // name 中必须出现，但是如果 desc 中出现了，就降权.
        BoostingQueryBuilder boostingQuery = QueryBuilders.boostingQuery(
                QueryBuilders.matchQuery("name", "标题"),
                QueryBuilders.matchQuery("desc", "描述"))
                .negativeBoost(0.5f);
        
        builder.query(boostingQuery);
        builder.from(0);
        builder.size(100);
        // 排序相关
        builder.sort("wordcount", SortOrder.DESC);
        
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
        SearchRequest req = new SearchRequest(INDEX);
        
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("author", "我吃西红柿"))
                .filter(QueryBuilders.rangeQuery("wordcount").gte(1003).lte(1010));
        
        builder.query(boolQueryBuilder);
        builder.from(0);
        builder.size(100);
        builder.sort("wordcount", SortOrder.DESC);
        
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
        SearchRequest req = new SearchRequest(INDEX);
        
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery("desc", "描述"));
        
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("desc", 2, 5)
                .preTags("<font color='red'>")
                .postTags("</font>");
        builder.highlighter(highlightBuilder);
        builder.from(0);
        builder.size(100);
        builder.sort("wordcount", SortOrder.DESC);
        
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
        SearchRequest req = new SearchRequest(INDEX);
        
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
     * # 聚合查询
     * # 统计聚合: 总条数，最大值，最小值，平均值，和
     */
    @RequestMapping("/aggsstat")
    public String aggsstatAction() throws IOException {
        SearchRequest req = new SearchRequest(INDEX);
        
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
        SearchRequest req = new SearchRequest(INDEX);
        
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

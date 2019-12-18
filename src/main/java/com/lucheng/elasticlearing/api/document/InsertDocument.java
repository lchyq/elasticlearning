package com.lucheng.elasticlearing.api.document;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.regexp.internal.RE;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 文档操作
 */
@Service
public class InsertDocument {
    @Autowired
    private RestHighLevelClient client;

    /**
     * 插入文档
     * @throws IOException
     */
    public void insert() throws IOException {
        //参数map
        Map<String,Object> param = new HashMap<>();
        param.put("user", "kimchy");
        param.put("postDate", new Date());
        param.put("message", "trying out Elasticsearch");
        //构造请求
        IndexRequest request = new IndexRequest("twitter");
        //加入数据
        request.id("1").source(JSON.toJSON(param));
        //可选配置,文档的刷新策略,其余配置同index配置
        //该配置为，直到该文档对于请求来说是可见的
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        //获取文档id
        String doID = response.getId();
        //获取index
        response.getIndex();
        //获取类型
        response.getType();
    }

    /**
     * 根据文档id获取文档数据
     */
    public void getDocumentById() throws IOException {
        GetRequest getRequest = new GetRequest("twitter","user","1");
        //可选配置，true表示这个请求需要返回字段
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true,new String[]{"需要过滤出来的字段"},null);
        getRequest.fetchSourceContext(fetchSourceContext);
        //可选配置,作用同 FetchSourceContext，但是返回的字段是被存储的
        getRequest.storedFields("字段名称");
        GetResponse response = client.get(getRequest,RequestOptions.DEFAULT);
        //获取每个字段
        response.getField("username").getValue();
        //获取所有的字段
        response.getFields();
    }

    /**
     * 判断该数据是否存在
     * @throws IOException
     */
    public void exits() throws IOException{
        GetRequest request = new GetRequest("twitter","user","1");
        //不需要获取字段
        FetchSourceContext fetchSourceContext = new FetchSourceContext(false);
        request.fetchSourceContext(fetchSourceContext);
        Boolean exits = client.exists(request,RequestOptions.DEFAULT);
    }

    /**
     * 删除文档
     */
    public void delete() throws IOException {
        DeleteRequest request = new DeleteRequest("twitter","user","1");
        client.delete(request,RequestOptions.DEFAULT);
    }

    /**
     * 更新文档
     * @throws IOException
     */
    public void update() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("twitter","text","1");
        //参数对象
        Object param = new Object();
        updateRequest.doc(JSON.toJSON(param));
        //设置参数,更新文档时，若此时有其他操作时需要重试的次数
        //其他配置同上
        updateRequest.retryOnConflict(3);
        UpdateResponse response = client.update(updateRequest,RequestOptions.DEFAULT);
    }

    /**
     * 批量操作api
     * @throws IOException
     */
    public void bulk() throws IOException {
        //操作三条添加index请求
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest("twitter").id("1").source(JSON.toJSON(new Object())));
        bulkRequest.add(new IndexRequest("twitter").id("2").source(JSON.toJSON(new Object())));
        bulkRequest.add(new IndexRequest("twitter").id("3").source(JSON.toJSON(new Object())));

        BulkResponse responses = client.bulk(bulkRequest,RequestOptions.DEFAULT);
    }
}

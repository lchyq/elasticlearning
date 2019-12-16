package com.lucheng.elasticlearing.api.index;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 创建index索引
 * @author lucheng28
 * @date 2019-12-16
 */
@Service
public class CreateIndex {
    @Autowired
    private RestHighLevelClient client;
    /**
     * 创建es索引
     * 类似于mysql的表
     */
    public void createIndex() throws IOException {
        //创建索引请求，并设置索引名称
        CreateIndexRequest indexRequest = new CreateIndexRequest("twitter");
        //配置索引分片以及索引副本
        indexRequest.settings(Settings.builder().
                put("index.number_of_shards",3).
                put("index.number_of_replicas",2).
                build());
        //设置index的表结构
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();
        xContentBuilder.startObject();
        {
            xContentBuilder.startObject("properties");
            {
                xContentBuilder.startObject("message");
                {
                    xContentBuilder.field("type","text");
                }
                xContentBuilder.endObject();
            }
            xContentBuilder.endObject();
        }
        xContentBuilder.endObject();
        indexRequest.mapping(xContentBuilder);
        //可选择的配置,等待所有的es节点响应，最多2min
        indexRequest.setTimeout(TimeValue.timeValueMinutes(2));
        //连接master的超时时间
        indexRequest.setMasterTimeout(TimeValue.timeValueMinutes(2));
        //获取响应对象,同步获取
        CreateIndexResponse response = client.indices().create(indexRequest, RequestOptions.DEFAULT);
    }
}

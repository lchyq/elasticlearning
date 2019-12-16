package com.lucheng.elasticlearing.api;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * es客户端 config
 * @author lucheng28
 * @date 2019-12-16
 */
@Configuration
public class GlobalConfigBean {
    @Bean
    public RestHighLevelClient client(){
        //创建client
        //HttpHost 可以配置多个
        RestClientBuilder client = RestClient.builder(new HttpHost("192.168.2.130",9200,"http"));
        return new RestHighLevelClient(client);
    }
}

package com.hmdp.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ouyang
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient(){
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.182.100:6379").setPassword("123456");
        // 创建RedissonClient对象
        return Redisson.create(config);
    }

    @Bean
    public RedissonClient redissonClient2(){
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.182.110:6379").setPassword("123456");
        // 创建RedissonClient对象
        return Redisson.create(config);
    }

    @Bean
    public RedissonClient redissonClient3(){
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.182.111:6379").setPassword("123456");
        // 创建RedissonClient对象
        return Redisson.create(config);
    }
}
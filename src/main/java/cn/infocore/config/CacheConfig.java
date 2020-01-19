package cn.infocore.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/17 15:58
 * @instructions 自定义生成的@Cacheable中keyGenerator属性
 */
@Configuration
public class CacheConfig {

    @Bean("myKeyGenerator")
    public KeyGenerator getKeyGenerator(){
        return (o, method, objects) -> method.getName() + "[" + Arrays.asList(objects) + "]";
    }
}

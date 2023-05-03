package com.dxx.takeOut.config;

import com.dxx.takeOut.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    //此处如果extends WebMvcConfigurationSupport，出现“No mapping for GET“静态资源
    //实现 WebMvcConfigurer 接口， 不会有问题

    /**
     * 遇到的问题：
     * 在spring boot的自定义配置类继承 WebMvcConfigurationSupport 后，发现自动配置的静态资源路径（classpath:/META/resources/，classpath:/resources/，classpath:/static/，classpath:/public/）不生效，访问不到页面
     * 是因为在继承继承WebMvcConfigurationSupport类后会导致mvc的自动配置失效，需要自己手动去实现
     * 由于继承WebMvcConfigurationSupport后会导致自动配置失效，所以这里要指定默认的静态资源的位置
     * https://blog.csdn.net/qq_61672548/article/details/124515694?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_utm_term~default-5-124515694-blog-124559942.235^v31^pc_relevant_default_base3&spm=1001.2101.3001.4242.4&utm_relevant_index=8
     */

    //扩展mvc框架的消息转换器
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器");
        //创建新的消息转换器，把结果转换成json，在通过输出流的方式响应给页面
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0,messageConverter);
    }

}

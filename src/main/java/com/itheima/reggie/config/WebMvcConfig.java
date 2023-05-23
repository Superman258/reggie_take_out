package com.itheima.reggie.config;

import com.itheima.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;
@Slf4j
@Configuration
@MapperScan("com.itheima.reggie.mapper")
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    /*重写，进行静态资源映射，用来把前端网页路径请求映射到这里的具体页面*/
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("backend/**").addResourceLocations("classpath:/static/backend/");
        registry.addResourceHandler("front/**").addResourceLocations("classpath:/static/front/");
        }

//        16.为了使用对象转换器JacksonObjectMapper，需要重写WebMvcConfigurationSupport的一个方法(扩展mvc框架的消息转换器)
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器");
//        创建消息转换器对象，把Java对象转换位json，再通过输出流的方式把json数据返回给页面

        MappingJackson2HttpMessageConverter messageConverter=new MappingJackson2HttpMessageConverter();

//         设置消息转换器，底层使用Jackson把Java对象转换位json,这里使用了我自己定义的JacksonObjectMapper转换器
        messageConverter.setObjectMapper(new JacksonObjectMapper());

//        将上面的消息转换器对象追加到mvc框架的converters集合中
        converters.add(0,messageConverter);
    }
}

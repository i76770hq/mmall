package pub.zjh.mall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    UserArgumentResolver userArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }

//    @Bean
//    public FilterRegistrationBean filterRegistrationBean(SessionExpireFilter sessionExpireFilter) {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//        filterRegistrationBean.setFilter(sessionExpireFilter);
//        filterRegistrationBean.setName("sessionExpireFilter");
//        filterRegistrationBean.setUrlPatterns(Lists.newArrayList("*.do"));
//        return filterRegistrationBean;
//    }

}

package top.lyfzn.media.quick.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author ZuoBro
 * date: 2021/5/20
 * time: 16:26
 */
@Component
@SuppressWarnings({
        "rawtypes",
        "nochecked"
})
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String beanName) {
        ConfigurableApplicationContext configurableApplicationContext = ((ConfigurableApplicationContext) applicationContext);
        return configurableApplicationContext.getBean(beanName);
    }
}

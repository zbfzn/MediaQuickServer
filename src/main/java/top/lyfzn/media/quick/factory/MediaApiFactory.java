package top.lyfzn.media.quick.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import top.lyfzn.media.quick.bean.mediaApi.BaseMediaApi;
import top.lyfzn.media.quick.exception.CustomerException;
import top.lyfzn.media.quick.util.SpringContextUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 解析接口工厂类
 *
 * @author ZuoBro
 * date: 2021/5/20
 * time: 3:29
 */
@Component
public class MediaApiFactory implements SmartInitializingSingleton {
    private final Logger log = LoggerFactory.getLogger(MediaApiFactory.class);

    final private List<BaseMediaApi> mediaApis;

    public MediaApiFactory() {
        mediaApis = new ArrayList<>();
    }
    public void add(BaseMediaApi baseMediaApi) {
        mediaApis.add(baseMediaApi);
    }

    public BaseMediaApi getMediaApi(String flag) {
        BaseMediaApi baseMediaApiFound = null;
        for(BaseMediaApi baseMediaApi: mediaApis) {
            if (baseMediaApi.canParse(flag)) {
                baseMediaApiFound = baseMediaApi;
            }
        }
        if (baseMediaApiFound == null) {
            throw new CustomerException("无法处理该链接");
        }
        return baseMediaApiFound;
    }

    @Override
    public void afterSingletonsInstantiated() {
        log.info("正在装载mediaApiFactory...");
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) ((ConfigurableApplicationContext) SpringContextUtil.getApplicationContext()).getBeanFactory();
        String[] beanNames = beanFactory.getBeanNamesForType(BaseMediaApi.class);
        Arrays.stream(beanNames).forEach(beanName -> {
            this.add(((BaseMediaApi) SpringContextUtil.getBean(beanName)));
            log.info(String.format("装载 [%s]", beanName));
        });
        log.info("装载mediaApiFactory结束");
    }
}

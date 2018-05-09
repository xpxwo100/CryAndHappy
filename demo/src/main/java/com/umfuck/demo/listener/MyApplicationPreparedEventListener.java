package com.umfuck.demo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 上下文创建完成后执行的事件监听器
 *
 * @author liaokailin
 * @version $Id: MyApplicationPreparedEventListener.java, v 0.1 2015年9月2日 下午11:29:38 liaokailin Exp $
 */
public class MyApplicationPreparedEventListener implements ApplicationListener<ApplicationPreparedEvent> {
    private Logger logger = LoggerFactory.getLogger(MyApplicationPreparedEventListener.class);

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        ConfigurableApplicationContext cac = event.getApplicationContext();
        passContextInfo(cac);
    }

    /**
     * 传递上下文
     *
     * @param cac
     */
    private void passContextInfo(ApplicationContext cac) {
        //dosomething()
        logger.info("spring boot上下文context创建完成，但此时spring中的bean是没有完全加载完成的。==MyApplicationPreparedEventListener==");
    }

}
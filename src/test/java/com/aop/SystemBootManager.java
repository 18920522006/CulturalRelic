package com.aop;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 通过容器刷新，回调所有的装配器
 */
public class SystemBootManager implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

    private boolean hasRunOnce = false;

    private ApplicationContext applicationContext;

    private Collection<SystemBootAddon> systemBootAddonList;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        initSystemBootAddonList();
        if (!hasRunOnce){
            for (SystemBootAddon systemBootAddon : systemBootAddonList) {
                systemBootAddon.onReady();
            }
            hasRunOnce = true;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void initSystemBootAddonList() {
        systemBootAddonList = applicationContext.getBeansOfType(SystemBootAddon.class).values();
    }


}

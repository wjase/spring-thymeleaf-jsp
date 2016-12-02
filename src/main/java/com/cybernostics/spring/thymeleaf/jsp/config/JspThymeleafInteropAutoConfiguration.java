/*
 * Copyright (c) 2016 Cybernostics Pty Ltd.
 * All rights reserved.
 */
package com.cybernostics.spring.thymeleaf.jsp.config;

import com.cybernostics.spring.thymeleaf.jsp.view.DefaultTemplateExistenceChecker;
import com.cybernostics.spring.thymeleaf.jsp.view.ThymeleafJSPViewResolver;
import com.cybernostics.spring.thymeleaf.jsp.view.TemplateExistenceChecker;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

@Configuration
@AutoConfigureAfter(value =
{
    ThymeleafAutoConfiguration.class, 
    WebMvcAutoConfiguration.class
})
@EnableConfigurationProperties(ThymeleafProperties.class)
public class JspThymeleafInteropAutoConfiguration
{

    private Logger LOG = Logger.getLogger(JspThymeleafInteropAutoConfiguration.class.getName());

    @PostConstruct
    public void reorderThymeleaf(ThymeleafViewResolver thymeleafViewResolver)
    {
        LOG.fine("Reducing priority for ThymeleafViewResolver to allow the ThymeleafJSPViewResolver to prevail.");
        thymeleafViewResolver.setOrder(Ordered.LOWEST_PRECEDENCE);
    }

    @Bean
    @ConditionalOnMissingBean(TemplateExistenceChecker.class)
    public TemplateExistenceChecker templateExistanceChecker(ApplicationContext applicationContext, ThymeleafProperties thymeleafProperties)
    {
        return new DefaultTemplateExistenceChecker(thymeleafProperties, applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean(ThymeleafJSPViewResolver.class)
    public ViewResolver thymeleafJSPViewResolver(InternalResourceViewResolver internalResourceViewResolver, ThymeleafViewResolver thymeleafViewResolver, TemplateExistenceChecker existenceChecker)
    {
        return new ThymeleafJSPViewResolver(internalResourceViewResolver, thymeleafViewResolver, existenceChecker);
    }
    
    @Bean
    @Scope(value = "session")
    public Map<String,Object> thSession(){
        return new HashMap<>();
    }
    
    @Bean
    @Scope(value = "request")
    public Map<String, Object> thRequest()
    {
        return new HashMap<>();
    }
    
}

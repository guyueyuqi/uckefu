package com.ukefu.ask;

import java.util.Date;
import java.util.Locale;

import javax.servlet.MultipartConfigElement;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.ukefu.core.UKDataContext;
import com.ukefu.util.DateConverter;

@EnableAutoConfiguration
@SpringBootApplication
@EnableJpaRepositories("com.ukefu.ask.service.repository")
public class Application {
	static{
		ConvertUtils.register(new DateLocaleConverter(Locale.CHINA , "yyyy-MM-dd HH:mm:ss"), Date.class);
		ConvertUtils.register(new DateConverter(null), Date.class);
	}
	
    @Bean   
    public MultipartConfigElement multipartConfigElement() {   
            MultipartConfigFactory factory = new MultipartConfigFactory();  
            factory.setMaxFileSize("50MB"); //KB,MB  
            factory.setMaxRequestSize("100MB");   
            return factory.createMultipartConfig();   
    }   
      
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {

                ErrorPage error = new ErrorPage("/error.html");

                container.addErrorPages(error);
            }
        };
    }
    
	public static void main(String[] args) {
		UKDataContext.setApplicationContext(SpringApplication.run(Application.class, args));
	}

}


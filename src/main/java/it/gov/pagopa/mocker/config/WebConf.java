package it.gov.pagopa.mocker.config;

import javax.servlet.http.HttpServlet;

import it.gov.pagopa.mocker.controller.ProxyServlet;
import it.gov.pagopa.mocker.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class WebConf {

    @Bean
    public ServletRegistrationBean<HttpServlet> registerServlet() {
        ServletRegistrationBean<HttpServlet> servRegBean = new ServletRegistrationBean<>();
        servRegBean.setServlet(new ProxyServlet());
        servRegBean.addUrlMappings(String.format("%s/*", Constants.MOCKER_PATH_ROOT));
        servRegBean.setLoadOnStartup(1);
        return servRegBean;
    }
}

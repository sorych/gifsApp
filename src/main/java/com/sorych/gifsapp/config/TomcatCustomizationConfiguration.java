package com.sorych.gifsapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatCustomizationConfiguration
    implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
  @Value("${server.tomcat.max-parameter-count:10000}")
  private int maxParameterCount;

  @Override
  public void customize(TomcatServletWebServerFactory factory) {
    factory.addConnectorCustomizers(connector -> connector.setMaxParameterCount(maxParameterCount));
  }
}

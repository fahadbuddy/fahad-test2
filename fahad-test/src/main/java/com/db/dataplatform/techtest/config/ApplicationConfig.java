package com.db.dataplatform.techtest.config;

import com.db.dataplatform.techtest.EmbeddedDataSourceConfiguration;
import com.db.dataplatform.techtest.client.config.RestTemplateConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RestTemplateConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class ApplicationConfig {
}

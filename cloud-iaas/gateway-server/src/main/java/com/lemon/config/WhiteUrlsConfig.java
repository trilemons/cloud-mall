package com.lemon.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 *白名单配置类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "gateway.white")
@RefreshScope //配置修改了也会自动读取
public class WhiteUrlsConfig {

    private List<String> allowUrls;
}

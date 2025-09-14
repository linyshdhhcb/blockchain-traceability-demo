package org.linyi.test.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 环境变量配置类
 * 用于加载项目根目录下的 .env 文件
 */
@Slf4j
@Configuration
public class EnvironmentConfig {

    @PostConstruct
    public void loadEnvironmentVariables() {
        // 尝试从项目根目录加载 .env 文件
        Path envPath = Paths.get(".env");
        
        if (!Files.exists(envPath)) {
            log.info("未找到 .env 文件，将使用系统环境变量或默认配置");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(envPath.toFile()))) {
            String line;
            int loadedCount = 0;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // 跳过空行和注释行
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // 解析 KEY=VALUE 格式
                int equalIndex = line.indexOf('=');
                if (equalIndex > 0) {
                    String key = line.substring(0, equalIndex).trim();
                    String value = line.substring(equalIndex + 1).trim();
                    
                    // 只有当系统环境变量中不存在该键时才设置
                    if (System.getenv(key) == null) {
                        System.setProperty(key, value);
                        loadedCount++;
                        log.debug("从 .env 文件加载环境变量: {} = {}", key, 
                                key.contains("KEY") || key.contains("PASSWORD") ? "***" : value);
                    }
                }
            }
            
            log.info("成功从 .env 文件加载了 {} 个环境变量", loadedCount);
            
        } catch (IOException e) {
            log.warn("读取 .env 文件时出错: {}", e.getMessage());
        }
    }
}

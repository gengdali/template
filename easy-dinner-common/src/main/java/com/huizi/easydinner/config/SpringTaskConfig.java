package com.huizi.easydinner.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.time.LocalDateTime;

/**
 * 定时任务配置
 * 只需要在配置类中添加一个@EnableScheduling注解即可开启SpringTask的定时任务能力。
 */
@Configuration
@EnableScheduling
public class SpringTaskConfig {
    private Logger logger = LoggerFactory.getLogger(SpringTaskConfig.class);

    @Value("${backup.datasource.dbName}")
    private String dbName;
    @Value("${backup.datasource.username}")
    private String username; // 数据库登录账号
    @Value("${backup.datasource.password}")
    private String pwd; // 数据库登录密码
    @Value("${backup.datasource.backupPath}")
    private String path; // 备份文件保存路径

    /**
     * cron表达式：Seconds Minutes Hours DayofMonth Month DayofWeek [Year]
     * 每天凌晨1点备份
     */
    @Scheduled(cron = "0 0 10 * * ?")
    private void backupsData() {
        File fl = new File(path);
        if (!fl.exists()) {
            fl.mkdirs();
        }
        // 注意：mysqldump.exe路径中不能含有空格，我将这个文件拷贝到了项目中
        try {
            String pathExe = this.getClass().getClassLoader().getResource("").getPath();
            if (pathExe.startsWith("/")) {
                pathExe = pathExe.substring(1);
            }
            String cmd = "cmd /c " + "mysqldump -u " + username + " -p" + pwd + " --databases " + dbName + " > " + path + "\\" + dbName + "_" + System.currentTimeMillis() + ".sql";
            Process process = Runtime.getRuntime().exec(cmd);
            if (process.waitFor() == 0) {
                logger.info(LocalDateTime.now() + " 备份数据库成功...");
            }
        } catch (Exception e) {
            logger.info(LocalDateTime.now() + " 备份数据库失败...");
            e.printStackTrace();
        }
    }

}

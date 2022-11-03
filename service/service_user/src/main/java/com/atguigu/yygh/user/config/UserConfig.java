package com.atguigu.yygh.user.config;


import org.mapstruct.MapperConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.atguigu.yygh.user.mapper")
public class UserConfig {
}

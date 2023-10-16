package com.c88.member;

import com.c88.affiliate.api.feign.AffiliateMemberClient;
import com.c88.feign.RiskFeignClient;
import com.c88.game.adapter.api.GameFeignClient;
import com.c88.payment.client.MemberBankClient;
import com.c88.payment.client.MemberCryptoClient;
import com.c88.payment.client.MemberRechargeClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@MapperScan(basePackages = {
        "com.c88.member.mapper"
    }
)
@SpringBootApplication(scanBasePackages = "com.c88")
@EnableFeignClients(basePackages = "com.c88.feign",
        basePackageClasses = {
                MemberBankClient.class,
                AffiliateMemberClient.class,
                MemberCryptoClient.class,
                MemberRechargeClient.class,
                RiskFeignClient.class,
                GameFeignClient.class
        }
)
@EnableHystrix
@EnableDiscoveryClient
public class C88MemberApplication {

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(C88MemberApplication.class, args);
    }

}


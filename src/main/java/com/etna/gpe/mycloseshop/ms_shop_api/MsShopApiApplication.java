package com.etna.gpe.mycloseshop.ms_shop_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.etna.gpe.mycloseshop.ms_shop_api", "com.etna.gpe.mycloseshop.security_api"})
public class MsShopApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsShopApiApplication.class, args);
	}

}

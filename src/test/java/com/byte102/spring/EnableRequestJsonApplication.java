package com.byte102.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by useheart on 2023/7/11
 * @author useheart
 */
@SpringBootApplication
@EnableRequestJson
public class EnableRequestJsonApplication {
	public static void main(String[] args) {
		SpringApplication.run(EnableRequestJsonApplication.class, args);
	}
}

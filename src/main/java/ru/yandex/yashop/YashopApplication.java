package ru.yandex.yashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class YashopApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(YashopApplication.class, args);
	}

}

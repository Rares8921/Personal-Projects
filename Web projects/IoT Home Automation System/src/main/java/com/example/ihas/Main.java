package com.example.ihas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy // pentru service-ul de audit
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        //var context = SpringApplication.run(Main.class, args);
        //AppContext appContext = context.getBean(AppContext.class);
        //run(appContext);
    }

}

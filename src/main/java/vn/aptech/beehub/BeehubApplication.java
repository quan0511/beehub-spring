package vn.aptech.beehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class BeehubApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeehubApplication.class, args);
    }

}

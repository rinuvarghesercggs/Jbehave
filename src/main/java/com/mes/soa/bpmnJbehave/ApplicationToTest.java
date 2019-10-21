package com.mes.soa.bpmnJbehave;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Created by andrew on 11/18/15.
 */
@SpringBootApplication
public class ApplicationToTest {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ApplicationToTest.class);
        builder.headless(false).run(args);
    }

}

package com.camunda.urlaub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "camunda.client.zeebe.enabled=false",
    "camunda.client.operate.enabled=false",
    "camunda.client.tasklist.enabled=false"
})
class UrlaubProcessApplicationTests {

    @Test
    void contextLoads() {
    }
}

package uk.gov.companieshouse.chs.notification.sender.api.health;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class HealthCheckControllerTest {

    private final String healthcheckUrl;
    @Autowired
    private MockMvc mockMvc;

    public HealthCheckControllerTest(
        final @Value("${management.endpoints.web.path-mapping.health}") String healthcheckUrl
    ) {
        this.healthcheckUrl = healthcheckUrl;
    }

    @Test
    void When_RequestingHealthcheck_Expect_OK_UP() throws Exception {
        mockMvc.perform(get(healthcheckUrl))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }
}

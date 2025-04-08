package uk.gov.companieshouse.chs.notification.sender.api.health;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class HealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    private final String healthcheckUrl;

    public HealthCheckControllerTest(
        final @Value("${management.endpoints.web.path-mapping.health}") String healthcheckUrl
    ) {
        this.healthcheckUrl = healthcheckUrl;
    }

    @Test
    public void When_RequestingHealthcheck_Expect_OK_UP() throws Exception {
        mockMvc.perform(get(healthcheckUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}

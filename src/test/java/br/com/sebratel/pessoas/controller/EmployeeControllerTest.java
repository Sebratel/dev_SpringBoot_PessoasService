package br.com.sebratel.pessoas.controller;

import br.com.sebratel.pessoas.BaseTest;
import br.com.sebratel.pessoas.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    @DisplayName("Should return user data from token")
    void getUserByTokenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/employee/get-user-by-token")
                        .header("X-User-Email", "test@example.com")
                        .header("X-User-Name", "Test+User"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.name").value("Test User"));
    }

    @Test
    @DisplayName("Should return person ID by email")
    void getPersonIdByEmailSuccess() throws Exception {
        when(employeeService.getPersonIdByEmail(anyString())).thenReturn(123L);

        mockMvc.perform(get("/api/v1/employee/get-person-id-by-email")
                        .param("email", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(123));
    }
}

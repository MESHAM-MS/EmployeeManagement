package com.ems;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeApiIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private long create(String url, String json) throws Exception {
        MvcResult r = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andExpect(header().exists("Location")).andReturn();
        JsonNode n = mapper.readTree(r.getResponse().getContentAsString());
        return n.get("id").asLong();
    }

    private String employeeJson(String name, String email, long dept, long desig) {
        return """
            {"name":"%s","email":"%s","salary":55000.50,"departmentId":%d,"designationId":%d,
             "address":{"city":"Pune","state":"Maharashtra","country":"India"}}
            """.formatted(name, email, dept, desig);
    }

    @Test
    void fullEmployeeLifecycle() throws Exception {
        long dept = create("/api/departments", "{\"name\":\"Engineering\"}");
        long desig = create("/api/designations", "{\"name\":\"Software Engineer\"}");
        long emp = create("/api/employees", employeeJson("John Doe", "john@example.com", dept, desig));

        // read
        mvc.perform(get("/api/employees/" + emp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.department.name").value("Engineering"))
                .andExpect(jsonPath("$.designation.name").value("Software Engineer"))
                .andExpect(jsonPath("$.address.city").value("Pune"));

        // search
        mvc.perform(get("/api/employees/search").param("name", "john"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/employees/search").param("departmentName", "engin"))
                .andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/employees/search").param("departmentId", String.valueOf(dept)).param("name", "zzz"))
                .andExpect(jsonPath("$", hasSize(0)));

        // PUT
        mvc.perform(put("/api/employees/" + emp).contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson("John D", "john@example.com", dept, desig)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("John D"));

        // PATCH (partial, incl. address)
        mvc.perform(patch("/api/employees/" + emp).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"salary\":70000,\"address\":{\"city\":\"Mumbai\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salary").value(70000.0))
                .andExpect(jsonPath("$.address.city").value("Mumbai"))
                .andExpect(jsonPath("$.address.state").value("Maharashtra"))
                .andExpect(jsonPath("$.name").value("John D"));

        // department in use cannot be deleted
        mvc.perform(delete("/api/departments/" + dept)).andExpect(status().isConflict());

        // DELETE employee, then it's gone
        mvc.perform(delete("/api/employees/" + emp)).andExpect(status().isNoContent());
        mvc.perform(get("/api/employees/" + emp)).andExpect(status().isNotFound());
        mvc.perform(delete("/api/departments/" + dept)).andExpect(status().isNoContent());
    }

    @Test
    void validationAndErrorHandling() throws Exception {
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"bad\",\"salary\":-5}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.salary").exists())
                .andExpect(jsonPath("$.validationErrors.address").exists());

        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content("{bad json"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/api/employees/abc")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/employees/999999")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found with id 999999"));
        mvc.perform(get("/api/nothing-here")).andExpect(status().isNotFound());

        // unknown department -> 404
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson("Ann", "ann@example.com", 9999, 9999)))
                .andExpect(status().isNotFound());
    }

    @Test
    void duplicateEmailAndDuplicateDepartmentReturnConflict() throws Exception {
        long dept = create("/api/departments", "{\"name\":\"HR\"}");
        long desig = create("/api/designations", "{\"name\":\"Manager\"}");
        create("/api/employees", employeeJson("Bob", "bob@example.com", dept, desig));

        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson("Bob 2", "BOB@example.com", dept, desig)))
                .andExpect(status().isConflict());
        mvc.perform(post("/api/departments").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"hr\"}"))
                .andExpect(status().isConflict());
    }
}

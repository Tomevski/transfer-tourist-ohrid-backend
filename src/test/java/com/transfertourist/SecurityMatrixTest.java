package com.transfertourist;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Enforces the {@code SecurityConfig} endpoint matrix (Milestone 2.4) end-to-end
 * through the real filter chain: public reads/customer submissions are open,
 * while admin reads and every mutation require {@code ROLE_ADMIN}. Runs on the
 * test profile (H2, schema from entities); CSRF is disabled in the config, so no
 * token is needed on the POSTs.
 *
 * <p>The key contrasts are DB-independent because Spring Security authorizes
 * <em>before</em> the request reaches a controller: a blocked request is 401 at
 * the entry point, whereas an allowed one reaches bean validation and comes back
 * 400 for an empty body. Same path, different method (POST vs GET on
 * {@code /bookings}) also proves the matrix is method-aware.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityMatrixTest {

    @Autowired
    private MockMvc mvc;

    // ----------------------------------------------------------------- public, open

    @Test
    void pingIsPublic() throws Exception {
        mvc.perform(get("/api/v1/ping")).andExpect(status().isOk());
    }

    @Test
    void createBookingIsPublicAndReachesValidation() throws Exception {
        // Reaches the controller (400 from validation), i.e. not blocked at 401.
        mvc.perform(post("/api/v1/bookings").contentType(APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void contactIsPublicAndReachesValidation() throws Exception {
        mvc.perform(post("/api/v1/contact").contentType(APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginIsPublicAndReachesValidation() throws Exception {
        mvc.perform(post("/api/v1/auth/login").contentType(APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ----------------------------------------------------------------- admin-gated, 401 without a token

    @Test
    void adminBookingListRequiresAuth() throws Exception {
        // GET on the same path that POST is public on — proves the matrix is method-aware.
        mvc.perform(get("/api/v1/bookings")).andExpect(status().isUnauthorized());
    }

    @Test
    void bookingConfirmRequiresAuth() throws Exception {
        mvc.perform(patch("/api/v1/bookings/bkg-x/confirm")).andExpect(status().isUnauthorized());
    }

    @Test
    void statisticsRequiresAuth() throws Exception {
        mvc.perform(get("/api/v1/statistics")).andExpect(status().isUnauthorized());
    }

    @Test
    void createLocationRequiresAuth() throws Exception {
        mvc.perform(post("/api/v1/locations").contentType(APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteVehicleRequiresAuth() throws Exception {
        mvc.perform(delete("/api/v1/vehicles/veh-x")).andExpect(status().isUnauthorized());
    }

    @Test
    void adminListEndpointRequiresAuth() throws Exception {
        mvc.perform(get("/api/v1/admin/locations")).andExpect(status().isUnauthorized());
    }

    // ----------------------------------------------------------------- admin token passes security

    @Test
    void adminTokenPassesSecurityToValidation() throws Exception {
        // With ROLE_ADMIN the same blocked POST now reaches validation → 400, not 401.
        mvc.perform(post("/api/v1/locations").with(user("admin").roles("ADMIN"))
                        .contentType(APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void adminTokenReachesAdminGet() throws Exception {
        // A valid admin identity must get past security on an admin read.
        assertThat(statusOf(get("/api/v1/bookings").with(user("admin").roles("ADMIN"))))
                .isNotIn(401, 403);
    }

    @Test
    void adminTokenReachesStatistics() throws Exception {
        assertThat(statusOf(get("/api/v1/statistics").with(user("admin").roles("ADMIN"))))
                .isNotIn(401, 403);
    }

    private int statusOf(MockHttpServletRequestBuilder request) throws Exception {
        return mvc.perform(request).andReturn().getResponse().getStatus();
    }
}

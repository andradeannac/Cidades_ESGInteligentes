package br.com.fiap.mobilidade.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmpresaControllerTest {

    private static final String NOVA_EMPRESA = """
            {"nome":"Bike Mais Sustentavel","cnpj":"11.222.333/0001-44",
             "setor":"Mobilidade","pontosSustentaveis":100}
            """;

    @Autowired
    private MockMvc mvc;

    @Test
    void listarEmpresasEhPublico() throws Exception {
        mvc.perform(get("/api/empresas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2)));
    }

    @Test
    void buscarEmpresaInexistenteRetorna404() throws Exception {
        mvc.perform(get("/api/empresas/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cadastrarSemAutenticacaoRetorna401() throws Exception {
        mvc.perform(post("/api/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NOVA_EMPRESA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cadastrarComAutenticacaoRetorna201() throws Exception {
        mvc.perform(post("/api/empresas")
                        .with(httpBasic("admin", "test-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NOVA_EMPRESA))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", notNullValue()))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome").value("Bike Mais Sustentavel"));
    }

    @Test
    void cadastrarEmpresaInvalidaRetorna400() throws Exception {
        mvc.perform(post("/api/empresas")
                        .with(httpBasic("admin", "test-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"cnpj\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void senhaErradaRetorna401() throws Exception {
        mvc.perform(delete("/api/empresas/1").with(httpBasic("admin", "errada")))
                .andExpect(status().isUnauthorized());
    }
}

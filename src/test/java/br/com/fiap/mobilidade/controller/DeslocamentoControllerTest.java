package br.com.fiap.mobilidade.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeslocamentoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void listarDeslocamentosRetornaCargaInicial() throws Exception {
        mvc.perform(get("/api/deslocamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$[0].usuario.nome").exists())
                .andExpect(jsonPath("$[0].tipoTransporte.nome").exists());
    }

    @Test
    void distanciaNegativaEhRejeitada() throws Exception {
        String json = """
                {"usuario":{"id":1},"tipoTransporte":{"id":1},
                 "dataDeslocamento":"2026-05-28","distanciaKm":-5,
                 "co2EvitadoKg":0,"pontosGerados":0}
                """;
        mvc.perform(post("/api/deslocamentos")
                        .with(httpBasic("admin", "test-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarTiposTransporteRetornaQuatroTipos() throws Exception {
        mvc.perform(get("/api/tipos-transporte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void listarRecompensasEResgatesEhPublico() throws Exception {
        mvc.perform(get("/api/recompensas")).andExpect(status().isOk());
        mvc.perform(get("/api/resgates-recompensa")).andExpect(status().isOk());
        mvc.perform(get("/api/usuarios")).andExpect(status().isOk());
    }
}

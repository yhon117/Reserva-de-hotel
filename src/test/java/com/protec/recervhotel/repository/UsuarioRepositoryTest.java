package com.protec.recervhotel.repository;

import com.protec.recervhotel.entities.Usuario;
import com.protec.recervhotel.enums.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .nombre("Juan Perez")
                .email("juan@test.com")
                .password("password123")
                .rol(Rol.USER)
                .build();
        usuarioRepository.save(usuario);
    }

    @Test
    void findByEmail_WhenExists_ReturnsUsuario() {
        Optional<Usuario> result = usuarioRepository.findByEmail("juan@test.com");

        assertTrue(result.isPresent());
        assertEquals("Juan Perez", result.get().getNombre());
    }

    @Test
    void findByEmail_WhenNotExists_ReturnsEmpty() {
        Optional<Usuario> result = usuarioRepository.findByEmail("noexiste@test.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void save_UsuarioWithUniqueEmail_ShouldSucceed() {
        Usuario otro = Usuario.builder()
                .nombre("Maria")
                .email("maria@test.com")
                .password("pass456")
                .rol(Rol.ADMIN)
                .build();

        Usuario saved = usuarioRepository.save(otro);

        assertNotNull(saved.getId());
        assertEquals("Maria", saved.getNombre());
        assertEquals(Rol.ADMIN, saved.getRol());
    }

    @Test
    void delete_RemovesUsuario() {
        usuarioRepository.delete(usuario);

        assertTrue(usuarioRepository.findByEmail("juan@test.com").isEmpty());
    }
}

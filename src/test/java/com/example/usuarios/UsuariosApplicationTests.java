package com.example.usuarios;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UsuariosApplicationTests {
    @Test
    void testMainMethod() {
        // Esto cubre el 100% de la clase principal
        assertDoesNotThrow(() -> {
            UsuariosApplication.main(new String[] {});
        });
    }
}

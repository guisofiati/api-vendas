package io.github.guisofiati.domain.repository;

import io.github.guisofiati.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<io.github.guisofiati.domain.entity.Usuario, Integer> {

    Optional<io.github.guisofiati.domain.entity.Usuario> findByLogin(String login);

}

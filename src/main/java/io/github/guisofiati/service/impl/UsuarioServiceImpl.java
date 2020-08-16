package io.github.guisofiati.service.impl;

import io.github.guisofiati.domain.repository.UsuarioRepository;
import io.github.guisofiati.exception.SenhaInvalidaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UsuarioRepository repository;

    public io.github.guisofiati.domain.entity.Usuario salvar(io.github.guisofiati.domain.entity.Usuario usuario){
        return repository.save(usuario);
    }

    public UserDetails autenticar ( io.github.guisofiati.domain.entity.Usuario usuario){
      UserDetails user= loadUserByUsername(usuario.getLogin());
      boolean senhasOk= encoder.matches(usuario.getSenha(), user.getPassword());
      if(senhasOk){
          return user;
      }
      throw new SenhaInvalidaException();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       io.github.guisofiati.domain.entity.Usuario usuario =repository.findByLogin(username)
               .orElseThrow( () -> new UsernameNotFoundException("Usuário não encontrado na base de dados."));

        String[] roles = usuario.isAdmin() ?
                new String[] {"ADMIN", "USER"} : new String[] {"USER"};

               return User
                       .builder()
                       .username(usuario.getLogin())
                       .password(usuario.getSenha())
                       .roles(roles)
                       .build();
    }
}

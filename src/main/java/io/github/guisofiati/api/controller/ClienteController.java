package io.github.guisofiati.api.controller;

import io.github.guisofiati.domain.entity.Cliente;
import io.github.guisofiati.domain.repository.Clientes;
import io.swagger.annotations.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@Api("Api Clientes")
public class ClienteController {

    private Clientes clientes;

    public ClienteController( Clientes clientes ) {
        this.clientes = clientes;
    }

    //Encontrar
    @GetMapping("{id}")
    @ApiOperation("Obter detalhes de um cliente")
    @ApiResponses({
            @ApiResponse( code = 200, message = "Cliente encontrado."),
            @ApiResponse(code = 404, message = "Cliente não encontrado para o ID informado.")
    })
    public Cliente getClienteById ( @PathVariable
                                    @ApiParam("Id do cliente") Integer id ){
        return clientes
                .findById(id)
                .orElseThrow( () ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente não encontrado!"));
    }

    //Salvar
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Salva um novo cliente")
    @ApiResponses({
            @ApiResponse( code = 201, message = "Cliente salvo com sucesso."),
            @ApiResponse(code = 400, message = "Erro de validação.")
    })
    public Cliente save( @RequestBody @Valid Cliente cliente ){
       return clientes.save(cliente);
    }

    //Deletar
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete( @PathVariable Integer id ){
        clientes.findById(id)
                .map( cliente -> {
                    clientes.delete(cliente);
                    return cliente;
                })
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente não encontrado!"));
    }

    //Atualizar
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)

    public void update( @PathVariable Integer id,
                        @RequestBody @Valid Cliente cliente ){

        clientes
                .findById(id)
                .map ( clienteExistente -> {
                    cliente.setId(clienteExistente.getId() );
                    clientes.save(cliente);
                    return clienteExistente;
                }).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Cliente não encontrado!") );
    }

    //Encontrar
    @GetMapping
    public List<Cliente> find( Cliente filtro ) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase() //não importa se sera caixa alta/baixa
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING); //não importa a posição da string digitada

        Example example = Example.of(filtro, matcher);
        return clientes.findAll(example);
    }
}


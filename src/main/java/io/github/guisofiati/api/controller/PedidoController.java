package io.github.guisofiati.api.controller;

import io.github.guisofiati.api.dto.InfoItemPedidoDTO;
import io.github.guisofiati.api.dto.InfoPedidoDTO;
import io.github.guisofiati.api.dto.PedidoDTO;
import io.github.guisofiati.api.dto.UpdateStatusPedidoDTO;
import io.github.guisofiati.domain.entity.ItemPedido;
import io.github.guisofiati.domain.entity.Pedido;
import io.github.guisofiati.domain.enums.StatusPedido;
import io.github.guisofiati.service.PedidoService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private PedidoService service;

    public PedidoController (PedidoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Integer save(@RequestBody @Valid PedidoDTO dto){
        Pedido pedido = service.salvar(dto);
        return pedido.getId();
    }
    @GetMapping("{id}")
    public InfoPedidoDTO getById( @PathVariable Integer id ){
        return service
                .obterPedidoCompleto(id)
                .map( p -> converter(p))
                .orElseThrow( () ->
                        new ResponseStatusException(NOT_FOUND, "Pedido não encontrado."));
    }

    @PatchMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void updateStatus( @PathVariable Integer id,
                              @RequestBody UpdateStatusPedidoDTO dto){
        String newStatus = dto.getNewStatus();
        service.updateStatus(id, StatusPedido.valueOf(newStatus) );

    }
    private InfoPedidoDTO converter(Pedido pedido){
       return InfoPedidoDTO
                .builder()
                .codigo(pedido.getId())
                .dataPedido(pedido.getData_pedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .cpf(pedido.getCliente().getCpf())
                .nomeCliente(pedido.getCliente().getNome())
                .total(pedido.getTotal())
                .status(pedido.getStatus().name())
                .itens(converter(pedido.getItens()))
                .build();
    }
    private List<InfoItemPedidoDTO> converter(List<ItemPedido> itens){
        if(CollectionUtils.isEmpty(itens)){
            return Collections.emptyList();
        }
        return itens.stream().map(
                item -> InfoItemPedidoDTO
                        .builder().descricaoProduto(item.getProduto().getDescricao())
                        .precoUnitario(item.getProduto().getPreco_unitario())
                        .quantidade(item.getQuantidade())
                        .build()
        ).collect(Collectors.toList());
    }
}

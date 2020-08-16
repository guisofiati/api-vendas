package io.github.guisofiati.service.impl;

import io.github.guisofiati.api.dto.ItemPedidoDTO;
import io.github.guisofiati.api.dto.PedidoDTO;
import io.github.guisofiati.domain.entity.Cliente;
import io.github.guisofiati.domain.entity.ItemPedido;
import io.github.guisofiati.domain.entity.Pedido;
import io.github.guisofiati.domain.entity.Produto;
import io.github.guisofiati.domain.enums.StatusPedido;
import io.github.guisofiati.domain.repository.Clientes;
import io.github.guisofiati.domain.repository.ItensPedido;
import io.github.guisofiati.domain.repository.Pedidos;
import io.github.guisofiati.domain.repository.Produtos;
import io.github.guisofiati.exception.PedidoNaoEncontradoException;
import io.github.guisofiati.exception.RegraNegocioException;
import io.github.guisofiati.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final Pedidos repository;
    private final Clientes clientesRepository;
    private final Produtos produtosRepository;
    private final ItensPedido itensPedidoRepository;

    @Override
    @Transactional
    //garante que salve tudo se tiver certo ou que se tiver erro o pedido será desfeito.
    public Pedido salvar(PedidoDTO dto) {
        Integer idCliente = dto.getCliente();
        Cliente cliente = clientesRepository
                .findById(idCliente)
                .orElseThrow(
                        () -> new RegraNegocioException("Código de cliente inválido."));
        Pedido pedido = new Pedido();
        pedido.setTotal(dto.getTotal());
        pedido.setData_pedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);

        List<ItemPedido> itensPedido = converterItens(pedido, dto.getItens());
        repository.save(pedido);
        itensPedidoRepository.saveAll(itensPedido);
        pedido.setItens(itensPedido);
        return pedido;
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return repository.findByIdFetchItens(id);
    }

    @Override
    @Transactional
    public void updateStatus(Integer id, StatusPedido statusPedido) {
        repository
                .findById(id)
                .map( pedido -> {
                    pedido.setStatus(statusPedido);
                    return repository.save(pedido);
                }).orElseThrow( () -> new PedidoNaoEncontradoException() );
    }

    private List<ItemPedido> converterItens( Pedido pedido, List<ItemPedidoDTO> itens){
        if(itens.isEmpty()){
            throw new RegraNegocioException(("Não é possível realizar um pedido sem itens."));
        }
        return itens
                .stream()
                .map ( dto -> {
                   Integer idProduto = dto.getProduto();
                   Produto produto = produtosRepository
                           .findById(idProduto)
                           .orElseThrow(
                                   () -> new RegraNegocioException("Código de produto inválido: " +idProduto));
                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setPedido(pedido);
                    itemPedido.setProduto(produto);
                    return itemPedido;
                }).collect(Collectors.toList());
    }
}

package io.github.guisofiati.service;

import io.github.guisofiati.api.dto.PedidoDTO;
import io.github.guisofiati.domain.entity.Pedido;
import io.github.guisofiati.domain.enums.StatusPedido;

import java.util.Optional;

public interface PedidoService {
    Pedido salvar(PedidoDTO dto);
    Optional <Pedido> obterPedidoCompleto(Integer id);
    void updateStatus(Integer id, StatusPedido statusPedido);

}

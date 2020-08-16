package io.github.guisofiati.domain.repository;

import io.github.guisofiati.domain.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItensPedido extends JpaRepository<ItemPedido, Integer> {

}

package com.calletuning.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.calletuning.inventario.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
package com.protec.recervhotel.persistencia;

import com.protec.recervhotel.entities.Factura;
import com.protec.recervhotel.repository.FacturaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FacturaDaoImpl implements FacturaDao {

    private final FacturaRepository facturaRepository;

    public FacturaDaoImpl(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    @Override
    public void save(Factura factura) {
        facturaRepository.save(factura);
    }

    @Override
    public Optional<Factura> findById(Long id) {
        return facturaRepository.findById(id);
    }

    @Override
    public List<Factura> findAll() {
        return facturaRepository.findAllByOrderByFechaEmisionDesc();
    }

    @Override
    public Optional<Factura> findByReservaId(Long reservaId) {
        return facturaRepository.findByReservaId(reservaId);
    }

    @Override
    public boolean existsByReservaId(Long reservaId) {
        return facturaRepository.existsByReservaId(reservaId);
    }

    @Override
    public List<Factura> findByUsuarioId(Long usuarioId) {
        return facturaRepository.findByReservaUsuarioId(usuarioId);
    }
}

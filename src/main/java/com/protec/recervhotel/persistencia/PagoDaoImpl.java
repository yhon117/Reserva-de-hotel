package com.protec.recervhotel.persistencia;

import com.protec.recervhotel.entities.Pago;
import com.protec.recervhotel.repository.PagoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PagoDaoImpl implements PagoDao {

    private final PagoRepository pagoRepository;

    public PagoDaoImpl(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Override
    public void save(Pago pago) {
        pagoRepository.save(pago);
    }

    @Override
    public Optional<Pago> findById(Long id) {
        return pagoRepository.findById(id);
    }

    @Override
    public List<Pago> findAll() {
        return pagoRepository.findAllByOrderByFechaPagoDesc();
    }

    @Override
    public List<Pago> findByReservaId(Long reservaId) {
        return pagoRepository.findByReservaIdOrderByFechaPagoDesc(reservaId);
    }
}

package com.protec.recervhotel.scheduler;

import com.protec.recervhotel.service.ReservaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservaScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReservaScheduler.class);

    private final ReservaService reservaService;

    public ReservaScheduler(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void completarReservasVencidas() {
        int count = reservaService.completarReservasVencidas();
        if (count > 0) {
            log.info("Reservas auto-completadas: {}", count);
        }
    }
}

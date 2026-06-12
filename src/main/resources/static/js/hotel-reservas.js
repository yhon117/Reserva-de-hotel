var reservasTable = null;

$(document).ready(function () {
  if (!requireAuth()) return;
  setActiveSidebar('reservas');

  var isUser = hasRol('USER');
  if (isUser) {
    $('#dataTable').closest('.table-responsive').before(
      '<div class="alert alert-info">Puedes crear una nueva reserva usando el botón "+ Nueva reserva".</div>'
    );
    $('#dataTable thead, #dataTable tbody').hide();
  } else {
    loadReservas();
    cargarSelectUsuarios();
  }

  cargarSelectHabitaciones();
  setupReservaModal();
});

function loadReservas() {
  api.getReservas().then(function (data) {
    var reservas = Array.isArray(data) ? data : [];
    var promises = reservas.map(function (r) {
      return api.getFacturaPorReserva(r.id).then(function (f) {
        r._factura = f;
        return r;
      }).catch(function () {
        r._factura = null;
        return r;
      });
    });
    return Promise.all(promises);
  }).then(function (reservasConFactura) {
    renderReservasTable(reservasConFactura);
  }).catch(function (err) {
    alert('Error: ' + err.message);
  });
}

function getEstadoReservaBadge(estado) {
  var map = { PENDIENTE: 'warning', CONFIRMADA: 'success', CANCELADA: 'danger', COMPLETADA: 'info' };
  return '<span class="badge badge-' + (map[estado] || 'secondary') + '">' + estado + '</span>';
}

function getEstadoPagoBadge(pagada) {
  return pagada
    ? '<span class="badge badge-success">Pagado</span>'
    : '<span class="badge badge-warning">Pendiente</span>';
}

function renderReservasTable(data) {
  var rows = data.map(function (r) {
    var puedeCancelar = r.estado !== 'CANCELADA' && r.estado !== 'COMPLETADA';
    var acciones = puedeCancelar
      ? '<button class="btn btn-sm btn-warning btn-cancelar-res" data-id="' + r.id + '"><i class="fas fa-times"></i> Cancelar</button>'
      : '—';

    var facturaInfo = r._factura
      ? getEstadoPagoBadge(r._factura.pagada)
      : '<span class="badge badge-secondary">—</span>';

    var facturaLink = r._factura
      ? '<a href="facturas.html" class="btn btn-sm btn-info"><i class="fas fa-file-invoice"></i></a>'
      : '—';

    return [
      r.id,
      r.usuarioNombre || (r.usuario && r.usuario.nombre) || '—',
      r.habitacionNumero || (r.habitacion && r.habitacion.numero) || '—',
      r.fechaEntrada,
      r.fechaSalida,
      '$' + (r.total || 0).toFixed(2),
      getEstadoReservaBadge(r.estado),
      facturaInfo,
      facturaLink,
      acciones
    ];
  });

  if (reservasTable) {
    reservasTable.clear().rows.add(rows).draw();
    return;
  }

  reservasTable = $('#dataTable').DataTable({
    data: rows,
    columns: [
      { title: 'ID' }, { title: 'Usuario' }, { title: 'Habitación' },
      { title: 'Entrada' }, { title: 'Salida' }, { title: 'Total' },
      { title: 'Estado' }, { title: 'Pago' },
      { title: 'Factura', orderable: false },
      { title: 'Acciones', orderable: false }
    ],
    language: { url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/es-ES.json' },
    order: [[0, 'desc']]
  });

  $('#dataTable tbody').on('click', '.btn-cancelar-res', function () {
    var id = parseInt($(this).data('id'));
    cancelarReserva(id);
  });
}

function setupReservaModal() {
  $('#btn-nueva-reserva').on('click', function () {
    $('#modalReservaTitle').text('Nueva Reserva');
    $('#form-reserva-modal')[0].reset();
    $('#res-usuario').val('');
    $('#res-habitacion').val('');

    var isUser = hasRol('USER');
    if (isUser) {
      $('#res-usuario-group').hide();
    } else {
      $('#res-usuario-group').show();
      cargarSelectUsuarios();
    }

    $('#modalReserva').modal('show');
  });

  $('#btn-guardar-reserva').on('click', guardarReserva);
}

function cargarSelectHabitaciones() {
  api.getHabitaciones().then(function (data) {
    var habs = Array.isArray(data) ? data : [];
    var opts = '<option value="">Seleccione...</option>';
    habs.filter(function (h) { return h.estado === 'DISPONIBLE'; }).forEach(function (h) {
      opts += '<option value="' + h.id + '">#' + h.numero + ' - ' + h.tipo + ' ($' + h.precioNoche + '/noche)</option>';
    });
    $('#res-habitacion').html(opts);
  }).catch(function () {
    $('#res-habitacion').html('<option value="">Error al cargar</option>');
  });
}

function cargarSelectUsuarios() {
  api.getUsuarios().then(function (data) {
    var users = Array.isArray(data) ? data : [];
    var opts = '<option value="">Seleccione...</option>';
    users.forEach(function (u) {
      opts += '<option value="' + u.id + '">' + u.nombre + ' (' + u.email + ')</option>';
    });
    $('#res-usuario').html(opts);
  }).catch(function () {
    $('#res-usuario').html('<option value="">Error al cargar</option>');
  });
}

function guardarReserva() {
  var isUser = hasRol('USER');
  var usuarioId = isUser ? getLoggedUser().id : parseInt($('#res-usuario').val());
  var metodoPago = $('#res-metodo-pago').val();
  var data = {
    usuarioId: usuarioId,
    habitacionId: parseInt($('#res-habitacion').val()),
    fechaEntrada: $('#res-fecha-entrada').val(),
    fechaSalida: $('#res-fecha-salida').val()
  };

  if (!data.habitacionId || !data.fechaEntrada || !data.fechaSalida) {
    alert('Todos los campos son obligatorios');
    return;
  }

  api.crearReserva(data).then(function (reserva) {
    if (metodoPago) {
      return api.crearPago({
        reservaId: reserva.id,
        monto: reserva.total,
        metodoPago: metodoPago
      }).then(function () {
        return reserva;
      });
    }
    return reserva;
  }).then(function () {
    $('#modalReserva').modal('hide');
    if (isUser) {
      alert('Reserva creada correctamente' + (metodoPago ? ' y pago registrado.' : '. Puedes pagar después.'));
    } else {
      loadReservas();
    }
  }).catch(function (err) {
    alert('Error: ' + err.message);
  });
}

function cancelarReserva(id) {
  if (!confirm('¿Cancelar esta reserva?')) return;
  api.cancelarReserva(id).then(loadReservas).catch(function (err) {
    alert('Error: ' + err.message);
  });
}

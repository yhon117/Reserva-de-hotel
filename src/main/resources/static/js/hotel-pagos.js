var pagosTable = null;

$(document).ready(function () {
  if (!requireAuth()) return;
  setActiveSidebar('pagos');
  loadPagos();
  setupPagoModal();
});

function loadPagos() {
  api.getPagos().then(function (data) {
    renderPagosTable(Array.isArray(data) ? data : []);
  }).catch(function (err) {
    alert('Error: ' + err.message);
  });
}

function getMetodoPagoBadge(metodo) {
  var map = {
    EFECTIVO: 'success',
    TARJETA_CREDITO: 'primary',
    TARJETA_DEBITO: 'info',
    TRANSFERENCIA: 'secondary'
  };
  var labels = {
    EFECTIVO: 'Efectivo',
    TARJETA_CREDITO: 'Tarjeta Crédito',
    TARJETA_DEBITO: 'Tarjeta Débito',
    TRANSFERENCIA: 'Transferencia'
  };
  return '<span class="badge badge-' + (map[metodo] || 'secondary') + '">' + (labels[metodo] || metodo) + '</span>';
}

function getEstadoPagoBadge(estado) {
  var map = { PAGADO: 'success', PENDIENTE: 'warning', PARCIAL: 'info', REEMBOLSADO: 'danger' };
  return '<span class="badge badge-' + (map[estado] || 'secondary') + '">' + estado + '</span>';
}

function renderPagosTable(data) {
  var rows = data.map(function (p) {
    return [
      p.codigoTransaccion || '—',
      p.reservaId || '—',
      p.huespedNombre || '—',
      p.habitacionNumero || '—',
      '$' + (p.monto || 0).toFixed(2),
      getMetodoPagoBadge(p.metodoPago),
      getEstadoPagoBadge(p.estadoPago),
      p.fechaPago ? new Date(p.fechaPago).toLocaleString('es-ES') : '—'
    ];
  });

  if (pagosTable) {
    pagosTable.clear().rows.add(rows).draw();
    return;
  }

  pagosTable = $('#dataTable').DataTable({
    data: rows,
    columns: [
      { title: 'Código' }, { title: 'Reserva' }, { title: 'Huésped' },
      { title: 'Habitación' }, { title: 'Monto' }, { title: 'Método' },
      { title: 'Estado' }, { title: 'Fecha' }
    ],
    language: { url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/es-ES.json' },
    order: [[7, 'desc']]
  });
}

function setupPagoModal() {
  $('#btn-nuevo-pago').on('click', function () {
    $('#modalPagoTitle').text('Registrar Pago');
    $('#form-pago-modal')[0].reset();
    $('#modalPago').modal('show');
  });

  $('#btn-guardar-pago').on('click', guardarPago);
}

function guardarPago() {
  var data = {
    reservaId: parseInt($('#pago-reserva-id').val()),
    monto: parseFloat($('#pago-monto').val()),
    metodoPago: $('#pago-metodo').val(),
    observaciones: $('#pago-observaciones').val() || null
  };

  if (!data.reservaId || !data.monto || !data.metodoPago) {
    alert('Todos los campos marcados con * son obligatorios');
    return;
  }

  api.crearPago(data).then(function () {
    $('#modalPago').modal('hide');
    loadPagos();
  }).catch(function (err) {
    alert('Error: ' + err.message);
  });
}

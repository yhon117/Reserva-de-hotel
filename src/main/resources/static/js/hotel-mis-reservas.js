var tabReservasTable = null;
var tabFacturasTable = null;
var _facturasCache = [];

function showTab(tab) {
  $('#tab-reservas, #tab-facturas, #tab-perfil').hide();
  $('#sidebar-mis-reservas, #sidebar-mis-facturas-tab, #sidebar-mi-perfil-tab').removeClass('active');

  if (tab === 'reservas') {
    $('#tab-reservas').show();
    $('#sidebar-mis-reservas').addClass('active');
    $('#section-title').text('Mis Reservas');
  } else if (tab === 'facturas') {
    $('#tab-facturas').show();
    $('#sidebar-mis-facturas-tab').addClass('active');
    $('#section-title').text('Mis Facturas');
  } else if (tab === 'perfil') {
    $('#tab-perfil').show();
    $('#sidebar-mi-perfil-tab').addClass('active');
    $('#section-title').text('Mi Perfil');
  }
}

function cargarCards(listaReservas, listaFacturas) {
  var total = listaReservas.length;
  var activas = listaReservas.filter(function (r) { return r.estado === 'CONFIRMADA' || r.estado === 'PENDIENTE'; }).length;
  var completadas = listaReservas.filter(function (r) { return r.estado === 'CANCELADA'; }).length;
  $('#card-total-reservas').text(total);
  $('#card-reservas-activas').text(activas);
  $('#card-reservas-completadas').text(completadas);
  $('#card-total-facturas').text(listaFacturas.length);
}

function badgeEstado(estado) {
  var map = { CONFIRMADA: 'success', PENDIENTE: 'warning', CANCELADA: 'secondary', COMPLETADA: 'info' };
  return '<span class="badge badge-' + (map[estado] || 'secondary') + '">' + estado + '</span>';
}

function badgePago(pagada) {
  return pagada
    ? '<span class="badge badge-success">Pagado</span>'
    : '<span class="badge badge-warning">Pendiente</span>';
}

function getFacturaPorReserva(reservaId) {
  for (var i = 0; i < _facturasCache.length; i++) {
    if (_facturasCache[i].reservaId === reservaId) return _facturasCache[i];
  }
  return null;
}

function cargarMisReservas() {
  api.getMisReservas().then(function (reservas) {
    if (tabReservasTable) { tabReservasTable.destroy(); tabReservasTable = null; }
    var tbody = $('#dataTable-reservas tbody').empty();

    reservas.forEach(function (r) {
      var btnCancelar = r.estado === 'CONFIRMADA' || r.estado === 'PENDIENTE'
        ? '<button class="btn btn-sm btn-danger btn-cancelar-reserva" data-id="' + r.id + '"><i class="fas fa-times"></i></button>'
        : '';
      var factura = getFacturaPorReserva(r.id);
      var linkFactura = factura
        ? '<a href="' + api.getFacturaPdfUrl(factura.id) + '" class="btn btn-sm btn-primary" target="_blank" title="Ver factura"><i class="fas fa-file-pdf"></i></a>'
        : '';
      var pagoBadge = factura ? badgePago(factura.pagada) : badgePago(false);

      tbody.append('<tr>' +
        '<td>' + r.id + '</td>' +
        '<td>' + (r.habitacionNumero || r.habitacionId) + '</td>' +
        '<td>' + r.fechaEntrada + '</td>' +
        '<td>' + r.fechaSalida + '</td>' +
        '<td>$' + (r.total ? r.total.toFixed(2) : '0.00') + '</td>' +
        '<td>' + badgeEstado(r.estado) + '</td>' +
        '<td>' + pagoBadge + '</td>' +
        '<td>' + linkFactura + '</td>' +
        '<td>' + btnCancelar + '</td>' +
        '</tr>');
    });

    tabReservasTable = $('#dataTable-reservas').DataTable({
      language: { url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/es-ES.json' },
      order: [[0, 'desc']]
    });

    $('.btn-cancelar-reserva').on('click', function () {
      var id = $(this).data('id');
      if (confirm('¿Cancelar la reserva #' + id + '?')) {
        api.cancelarReserva(id).then(function () {
          cargarMisReservas();
          cargarMisFacturas();
          cargarDashboard();
        }).catch(function (err) { alert(err.message); });
      }
    });
  }).catch(function (err) { console.error(err); });
}

function cargarMisFacturas() {
  api.getMisFacturas().then(function (facturas) {
    _facturasCache = facturas;
    if (tabFacturasTable) { tabFacturasTable.destroy(); tabFacturasTable = null; }
    var tbody = $('#dataTable-facturas tbody').empty();

    facturas.forEach(function (f) {
      tbody.append('<tr>' +
        '<td>' + f.numeroFactura + '</td>' +
        '<td>' + f.reservaId + '</td>' +
        '<td>' + f.habitacionNumero + '</td>' +
        '<td>$' + (f.total ? f.total.toFixed(2) : '0.00') + '</td>' +
        '<td>' + badgePago(f.pagada) + '</td>' +
        '<td>' + (f.fechaEmision ? new Date(f.fechaEmision).toLocaleDateString() : '') + '</td>' +
        '<td><a href="' + api.getFacturaPdfUrl(f.id) + '" class="btn btn-sm btn-primary" target="_blank"><i class="fas fa-file-pdf"></i></a></td>' +
        '</tr>');
    });

    tabFacturasTable = $('#dataTable-facturas').DataTable({
      language: { url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/es-ES.json' },
      order: [[0, 'desc']]
    });
  }).catch(function (err) { console.error(err); });
}

function cargarPerfil() {
  var user = getLoggedUser();
  if (user) {
    $('#perf-nombre').val(user.nombre || '');
    $('#perf-email').val(user.email || '');
    $('#perf-telefono').val(user.telefono || '');
  }
}

function cargarDashboard() {
  Promise.all([
    api.getMisReservas(),
    api.getMisFacturas()
  ]).then(function (results) {
    cargarCards(results[0], results[1]);
  }).catch(function (err) { console.error(err); });
}

$(document).ready(function () {
  if (!requireAuth()) return;
  setTopbarUser();
  setupLogout();

  showTab('reservas');
  cargarDashboard();
  cargarMisFacturas();
  cargarMisReservas();
  cargarPerfil();

  $('#btn-nueva-reserva-cliente').on('click', function () {
    api.getHabitaciones().then(function (habitaciones) {
      var sel = $('#res-cli-habitacion').empty().append('<option value="">Seleccione...</option>');
      habitaciones.forEach(function (h) {
        sel.append('<option value="' + h.id + '">' + h.numero + ' - ' + h.tipo + ' ($' + h.precioNoche + '/noche)</option>');
      });
    });
    $('#modalReservaCliente').modal('show');
  });

  $('#btn-guardar-reserva-cliente').on('click', function () {
    var user = getLoggedUser();
    var data = {
      usuarioId: user ? user.id : null,
      habitacionId: parseInt($('#res-cli-habitacion').val()),
      fechaEntrada: $('#res-cli-fecha-entrada').val(),
      fechaSalida: $('#res-cli-fecha-salida').val(),
      metodoPago: $('#res-cli-metodo-pago').val() || undefined
    };
    if (!data.habitacionId || !data.fechaEntrada || !data.fechaSalida) {
      alert('Complete todos los campos obligatorios');
      return;
    }
    var btn = $(this).prop('disabled', true);
    api.crearReservaCliente(data).then(function () {
      $('#modalReservaCliente').modal('hide');
      $('#form-reserva-cliente')[0].reset();
      cargarDashboard();
      cargarMisFacturas();
      cargarMisReservas();
      alert('Reserva creada con éxito');
    }).catch(function (err) {
      alert(err.message);
    }).finally(function () { btn.prop('disabled', false); });
  });

  $('#form-perfil').on('submit', function (e) {
    e.preventDefault();
    var errorEl = $('#perfil-error').addClass('d-none');
    var successEl = $('#perfil-success').addClass('d-none');
    var data = {
      nombre: $('#perf-nombre').val().trim(),
      email: $('#perf-email').val().trim(),
      telefono: $('#perf-telefono').val().trim(),
    };
    var pwd = $('#perf-password').val();
    if (pwd) data.password = pwd;
    api.actualizarMiPerfil(data).then(function (user) {
      var current = getLoggedUser();
      setLoggedUser({
        id: current.id,
        nombre: user.nombre,
        email: user.email,
        telefono: user.telefono,
        rol: current.rol
      }, getToken());
      setTopbarUser();
      successEl.text('Perfil actualizado correctamente').removeClass('d-none');
      setTimeout(function () { successEl.addClass('d-none'); }, 3000);
    }).catch(function (err) {
      errorEl.text(err.message).removeClass('d-none');
    });
  });
});

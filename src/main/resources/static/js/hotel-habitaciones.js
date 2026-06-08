var habitacionesTable = null;
var habitacionesData = [];

$(document).ready(function () {
  if (!requireAuth()) return;
  setActiveSidebar('habitaciones');

  if (hasRol('ADMIN')) {
    $('#btn-nueva-habitacion').removeClass('d-none').addClass('d-sm-inline-block');
  }

  loadHabitaciones();
  setupHabitacionModal();
});

function loadHabitaciones() {
  api.getHabitaciones().then(function (data) {
    habitacionesData = Array.isArray(data) ? data : [];
    renderHabitacionesTable();
  }).catch(function (err) {
    alert('Error: ' + err.message);
  });
}

function getEstadoBadge(estado) {
  var map = { DISPONIBLE: 'success', OCUPADA: 'danger', MANTENIMIENTO: 'warning' };
  return '<span class="badge badge-' + (map[estado] || 'secondary') + '">' + estado + '</span>';
}

function getTipoBadge(tipo) {
  var map = { SIMPLE: 'info', DOBLE: 'success', SUITE: 'warning', FAMILIAR: 'danger' };
  return '<span class="badge badge-' + (map[tipo] || 'secondary') + '">' + tipo + '</span>';
}

function renderHabitacionesTable() {
  var isAdmin = hasRol('ADMIN');
  var data = habitacionesData.map(function (h) {
    var acciones = isAdmin
      ? '<button class="btn btn-sm btn-primary btn-editar-hab" data-id="' + h.id + '"><i class="fas fa-edit"></i></button> ' +
        '<button class="btn btn-sm btn-danger btn-eliminar-hab" data-id="' + h.id + '"><i class="fas fa-trash"></i></button>'
      : '—';
    return [
      h.id,
      h.numero,
      h.piso,
      getTipoBadge(h.tipo),
      '$' + (h.precioNoche || 0).toFixed(2),
      h.capacidad,
      getEstadoBadge(h.estado),
      acciones
    ];
  });

  if (habitacionesTable) {
    habitacionesTable.clear().rows.add(data).draw();
    return;
  }

  habitacionesTable = $('#dataTable').DataTable({
    data: data,
    columns: [
      { title: 'ID' }, { title: 'Número' }, { title: 'Piso' }, { title: 'Tipo' },
      { title: 'Precio/noche' }, { title: 'Capacidad' }, { title: 'Estado' },
      { title: 'Acciones', orderable: false }
    ],
    language: { url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/es-ES.json' },
    order: [[0, 'desc']]
  });

  $('#dataTable tbody').on('click', '.btn-editar-hab', function () {
    var id = parseInt($(this).data('id'));
    editarHabitacion(id);
  });

  $('#dataTable tbody').on('click', '.btn-eliminar-hab', function () {
    var id = parseInt($(this).data('id'));
    eliminarHabitacion(id);
  });
}

function setupHabitacionModal() {
  $('#btn-nueva-habitacion').on('click', function () {
    $('#modalHabitacionTitle').text('Nueva Habitación');
    $('#hab-id').val('');
    $('#form-habitacion-modal')[0].reset();
    $('#modalHabitacion').modal('show');
  });

  $('#btn-guardar-habitacion').on('click', guardarHabitacion);
}

function editarHabitacion(id) {
  var h = habitacionesData.find(function (x) { return x.id === id; });
  if (!h) return;
  $('#modalHabitacionTitle').text('Editar Habitación');
  $('#hab-id').val(h.id);
  $('#hab-numero').val(h.numero);
  $('#hab-piso').val(h.piso);
  $('#hab-tipo').val(h.tipo);
  $('#hab-precio').val(h.precioNoche);
  $('#hab-capacidad').val(h.capacidad);
  $('#hab-estado').val(h.estado);
  $('#modalHabitacion').modal('show');
}

function guardarHabitacion() {
  var id = $('#hab-id').val();
  var data = {
    numero: String($('#hab-numero').val()),
    piso: parseInt($('#hab-piso').val()),
    tipo: $('#hab-tipo').val(),
    precioNoche: parseFloat($('#hab-precio').val()),
    capacidad: parseInt($('#hab-capacidad').val()),
    estado: $('#hab-estado').val()
  };

  var promise = id ? api.actualizarHabitacion(parseInt(id), data) : api.crearHabitacion(data);
  promise.then(function () {
    $('#modalHabitacion').modal('hide');
    loadHabitaciones();
  }).catch(function (err) {
    alert('Error: ' + err.message);
  });
}

function eliminarHabitacion(id) {
  if (!confirm('¿Eliminar esta habitación?')) return;
  api.deleteHabitacion(id).then(function () {
    loadHabitaciones();
  }).catch(function (err) {
    alert('Error: ' + err.message);
  });
}

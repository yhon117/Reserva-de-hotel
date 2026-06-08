var usuariosTable = null;
var usuariosData = [];

$(document).ready(function () {
  if (!requireAuth()) return;
  setActiveSidebar('usuarios');

  var isAdmin = hasRol('ADMIN');
  if (!isAdmin) {
    $('#btn-nuevo-usuario').hide();
  }

  loadUsuarios();
  setupUsuarioModal();
});

function loadUsuarios() {
  api.getUsuarios().then(function (data) {
    usuariosData = Array.isArray(data) ? data : [];
    renderUsuariosTable();
  }).catch(function (err) {
    alert('Error: ' + err.message);
  });
}

function getRolBadge(rol) {
  var map = { ADMIN: 'danger', RECEPCIONISTA: 'warning', USER: 'info' };
  return '<span class="badge badge-' + (map[rol] || 'secondary') + '">' + rol + '</span>';
}

function renderUsuariosTable() {
  var isAdmin = hasRol('ADMIN');

  var rows = usuariosData.map(function (u) {
    var acciones = isAdmin
      ? '<button class="btn btn-sm btn-primary btn-editar-usu" data-id="' + u.id + '"><i class="fas fa-edit"></i></button> ' +
        '<button class="btn btn-sm btn-danger btn-eliminar-usu" data-id="' + u.id + '"><i class="fas fa-trash"></i></button>'
      : '—';
    return [u.id, u.nombre, u.email, u.telefono || '—', getRolBadge(u.rol), acciones];
  });

  if (usuariosTable) {
    usuariosTable.clear().rows.add(rows).draw();
    return;
  }

  usuariosTable = $('#dataTable').DataTable({
    data: rows,
    columns: [
      { title: 'ID' }, { title: 'Nombre' }, { title: 'Email' },
      { title: 'Teléfono' }, { title: 'Rol' },
      { title: 'Acciones', orderable: false }
    ],
    language: { url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/es-ES.json' },
    order: [[0, 'desc']]
  });

  $('#dataTable tbody').on('click', '.btn-editar-usu', function () {
    var id = parseInt($(this).data('id'));
    editarUsuario(id);
  });

  $('#dataTable tbody').on('click', '.btn-eliminar-usu', function () {
    var id = parseInt($(this).data('id'));
    eliminarUsuario(id);
  });
}

function setupUsuarioModal() {
  $('#btn-nuevo-usuario').on('click', function () {
    $('#modalUsuarioTitle').text('Nuevo Usuario');
    $('#usu-id').val('');
    $('#form-usuario-modal')[0].reset();
    $('#usu-password').prop('required', true);
    $('#modalUsuario').modal('show');
  });

  $('#btn-guardar-usuario').on('click', guardarUsuario);
}

function editarUsuario(id) {
  var u = usuariosData.find(function (x) { return x.id === id; });
  if (!u) return;
  $('#modalUsuarioTitle').text('Editar Usuario');
  $('#usu-id').val(u.id);
  $('#usu-nombre').val(u.nombre);
  $('#usu-email').val(u.email);
  $('#usu-telefono').val(u.telefono || '');
  $('#usu-rol').val(u.rol);
  $('#usu-password').val('');
  $('#usu-password').prop('required', false);
  $('#modalUsuario').modal('show');
}

function guardarUsuario() {
  var id = $('#usu-id').val();
  var data = {
    nombre: $('#usu-nombre').val().trim(),
    email: $('#usu-email').val().trim(),
    password: $('#usu-password').val(),
    telefono: $('#usu-telefono').val().trim() || undefined,
    rol: $('#usu-rol').val()
  };

  if (!data.nombre || !data.email) {
    alert('Nombre y email son obligatorios');
    return;
  }

  if (!id && !data.password) {
    alert('La contraseña es obligatoria para nuevos usuarios');
    return;
  }

  var promise = id
    ? api.actualizarUsuario(parseInt(id), data)
    : api.crearUsuario(data);

  promise.then(function () {
    $('#modalUsuario').modal('hide');
    loadUsuarios();
  }).catch(function (err) {
    alert('Error: ' + err.message);
  });
}

function eliminarUsuario(id) {
  if (!confirm('¿Eliminar este usuario y todas sus reservas?')) return;
  api.deleteUsuario(id).then(loadUsuarios).catch(function (err) {
    alert('Error: ' + err.message);
  });
}

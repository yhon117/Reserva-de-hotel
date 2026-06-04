let usuariosData = [];

function renderUsuariosTable() {
  const tbody = document.querySelector('#usuarios-table tbody');
  if (!usuariosData.length) {
    tbody.innerHTML = '<tr><td colspan="5" class="empty-state">No hay usuarios registrados</td></tr>';
    return;
  }
  tbody.innerHTML = usuariosData.map(u => `
    <tr>
      <td>${u.id}</td>
      <td>${u.nombre}</td>
      <td>${u.email}</td>
      <td>${u.telefono || '—'}</td>
      <td>
        <button class="btn btn-sm btn-danger" onclick="eliminarUsuario(${u.id})">Eliminar</button>
      </td>
    </tr>
  `).join('');
}

function loadUsuarios() {
  const tbody = document.querySelector('#usuarios-table tbody');
  tbody.innerHTML = '<tr><td colspan="5" class="loading">Cargando...</td></tr>';

  api.getUsuarios()
    .then(data => {
      usuariosData = Array.isArray(data) ? data : [];
      renderUsuariosTable();
    })
    .catch(err => {
      tbody.innerHTML = `<tr><td colspan="5" class="error-message">${err.message}</td></tr>`;
    });
}

function initUsuarios() {
  loadUsuarios();
}

async function eliminarUsuario(id) {
  if (!confirm('¿Eliminar este usuario y todas sus reservas?')) return;
  try {
    await api.deleteUsuario(id);
    loadUsuarios();
  } catch (err) {
    alert('Error: ' + err.message);
  }
}

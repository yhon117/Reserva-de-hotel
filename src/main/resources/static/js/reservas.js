let reservasData = [];

function getEstadoReservaBadge(estado) {
  const map = {
    PENDIENTE: 'badge-warning',
    CONFIRMADA: 'badge-success',
    CANCELADA: 'badge-danger',
    COMPLETADA: 'badge-info',
  };
  return `<span class="badge ${map[estado] || 'badge-secondary'}">${estado}</span>`;
}

function renderReservasTable() {
  const tbody = document.querySelector('#reservas-table tbody');
  if (!reservasData.length) {
    tbody.innerHTML = '<tr><td colspan="7" class="empty-state">No hay reservas</td></tr>';
    return;
  }
  tbody.innerHTML = reservasData.map(r => `
    <tr>
      <td>${r.id}</td>
      <td>${r.usuarioNombre || r.usuario?.nombre || '—'}</td>
      <td>${r.habitacionNumero || r.habitacion?.numero || '—'}</td>
      <td>${r.fechaEntrada}</td>
      <td>${r.fechaSalida}</td>
      <td>$${(r.total || 0).toFixed(2)}</td>
      <td>${getEstadoReservaBadge(r.estado)}</td>
      <td>
        ${r.estado !== 'CANCELADA' && r.estado !== 'COMPLETADA'
          ? `<button class="btn btn-sm btn-warning" onclick="cancelarReserva(${r.id})">Cancelar</button>`
          : '—'}
      </td>
    </tr>
  `).join('');
}

function loadReservas() {
  const tbody = document.querySelector('#reservas-table tbody');
  tbody.innerHTML = '<tr><td colspan="8" class="loading">Cargando...</td></tr>';

  api.getReservas()
    .then(data => {
      reservasData = Array.isArray(data) ? data : [];
      renderReservasTable();
    })
    .catch(err => {
      tbody.innerHTML = `<tr><td colspan="8" class="error-message">${err.message}</td></tr>`;
    });
}

function initReservas() {
  loadReservas();
  cargarSelectHabitaciones();
  cargarSelectUsuarios();
}

async function cargarSelectHabitaciones() {
  const sel = document.getElementById('res-habitacion');
  try {
    const data = await api.getHabitaciones();
    const habs = Array.isArray(data) ? data : [];
    sel.innerHTML = '<option value="">Seleccione...</option>' +
      habs.filter(h => h.estado === 'DISPONIBLE')
        .map(h => `<option value="${h.id}">#${h.numero} - ${h.tipo} ($${h.precioNoche}/noche)</option>`)
        .join('');
  } catch {
    sel.innerHTML = '<option value="">Error al cargar</option>';
  }
}

async function cargarSelectUsuarios() {
  const sel = document.getElementById('res-usuario');
  try {
    const data = await api.getUsuarios();
    const users = Array.isArray(data) ? data : [];
    sel.innerHTML = '<option value="">Seleccione...</option>' +
      users.map(u => `<option value="${u.id}">${u.nombre} (${u.email})</option>`)
        .join('');
  } catch {
    sel.innerHTML = '<option value="">Error al cargar</option>';
  }
}

function openModalCrearReserva() {
  document.getElementById('modal-reserva-title').textContent = 'Nueva Reserva';
  document.getElementById('form-reserva').reset();
  document.getElementById('modal-reserva').classList.add('open');
  cargarSelectHabitaciones();
  cargarSelectUsuarios();
}

function cerrarModalReserva() {
  document.getElementById('modal-reserva').classList.remove('open');
}

async function guardarReserva() {
  const data = {
    usuarioId: parseInt(document.getElementById('res-usuario').value),
    habitacionId: parseInt(document.getElementById('res-habitacion').value),
    fechaEntrada: document.getElementById('res-fecha-entrada').value,
    fechaSalida: document.getElementById('res-fecha-salida').value,
  };

  if (!data.usuarioId || !data.habitacionId || !data.fechaEntrada || !data.fechaSalida) {
    alert('Todos los campos son obligatorios');
    return;
  }

  try {
    await api.crearReserva(data);
    cerrarModalReserva();
    loadReservas();
  } catch (err) {
    alert('Error: ' + err.message);
  }
}

async function cancelarReserva(id) {
  if (!confirm('¿Cancelar esta reserva?')) return;
  try {
    await api.cancelarReserva(id);
    loadReservas();
  } catch (err) {
    alert('Error: ' + err.message);
  }
}

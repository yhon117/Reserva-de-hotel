let habitacionesData = [];

function getEstadoHabBadge(estado) {
  const map = {
    DISPONIBLE: 'badge-success',
    OCUPADA: 'badge-danger',
    MANTENIMIENTO: 'badge-warning',
  };
  return `<span class="badge ${map[estado] || 'badge-secondary'}">${estado}</span>`;
}

function getTipoHabBadge(tipo) {
  const map = {
    SIMPLE: 'badge-info',
    DOBLE: 'badge-success',
    SUITE: 'badge-warning',
    FAMILIAR: 'badge-danger',
  };
  return `<span class="badge ${map[tipo] || 'badge-secondary'}">${tipo}</span>`;
}

function renderHabitacionesTable() {
  const tbody = document.querySelector('#habitaciones-table tbody');
  if (!habitacionesData.length) {
    tbody.innerHTML = '<tr><td colspan="8" class="empty-state">No hay habitaciones registradas</td></tr>';
    return;
  }
  tbody.innerHTML = habitacionesData.map(h => `
    <tr>
      <td>${h.id}</td>
      <td>${h.numero}</td>
      <td>${h.piso}</td>
      <td>${getTipoHabBadge(h.tipo)}</td>
      <td>$${h.precioNoche.toFixed(2)}</td>
      <td>${h.capacidad}</td>
      <td>${getEstadoHabBadge(h.estado)}</td>
      <td>
        <button class="btn btn-sm btn-outline" onclick="editarHabitacion(${h.id})">Editar</button>
        <button class="btn btn-sm btn-danger" onclick="eliminarHabitacion(${h.id})">Eliminar</button>
      </td>
    </tr>
  `).join('');
}

function loadHabitaciones() {
  const tbody = document.querySelector('#habitaciones-table tbody');
  tbody.innerHTML = '<tr><td colspan="8" class="loading">Cargando...</td></tr>';

  api.getHabitaciones()
    .then(data => {
      habitacionesData = Array.isArray(data) ? data : [];
      renderHabitacionesTable();
    })
    .catch(err => {
      tbody.innerHTML = `<tr><td colspan="8" class="error-message">${err.message}</td></tr>`;
    });
}

function initHabitaciones() {
  loadHabitaciones();
}

function openModalCrearHabitacion() {
  document.getElementById('modal-habitacion-title').textContent = 'Nueva Habitación';
  document.getElementById('form-habitacion').reset();
  document.getElementById('habitacion-id').value = '';
  document.getElementById('modal-habitacion').classList.add('open');
}

function editarHabitacion(id) {
  const h = habitacionesData.find(x => x.id === id);
  if (!h) return;
  document.getElementById('modal-habitacion-title').textContent = 'Editar Habitación';
  document.getElementById('habitacion-id').value = h.id;
  document.getElementById('hab-numero').value = h.numero;
  document.getElementById('hab-piso').value = h.piso;
  document.getElementById('hab-tipo').value = h.tipo;
  document.getElementById('hab-precio').value = h.precioNoche;
  document.getElementById('hab-capacidad').value = h.capacidad;
  document.getElementById('hab-estado').value = h.estado;
  document.getElementById('modal-habitacion').classList.add('open');
}

function cerrarModalHabitacion() {
  document.getElementById('modal-habitacion').classList.remove('open');
}

async function guardarHabitacion() {
  const id = document.getElementById('habitacion-id').value;
  const data = {
    numero: document.getElementById('hab-numero').value,
    piso: parseInt(document.getElementById('hab-piso').value),
    tipo: document.getElementById('hab-tipo').value,
    precioNoche: parseFloat(document.getElementById('hab-precio').value),
    capacidad: parseInt(document.getElementById('hab-capacidad').value),
    estado: document.getElementById('hab-estado').value,
  };

  try {
    if (id) {
      await api.actualizarHabitacion(parseInt(id), data);
    } else {
      await api.crearHabitacion(data);
    }
    cerrarModalHabitacion();
    loadHabitaciones();
  } catch (err) {
    alert('Error: ' + err.message);
  }
}

async function eliminarHabitacion(id) {
  if (!confirm('¿Eliminar esta habitación?')) return;
  try {
    await api.deleteHabitacion(id);
    loadHabitaciones();
  } catch (err) {
    alert('Error: ' + err.message);
  }
}

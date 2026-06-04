const API_BASE = 'http://localhost:8080/api';

async function apiRequest(path, options = {}) {
  const url = `${API_BASE}${path}`;
  const config = {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  };
  if (config.body && typeof config.body === 'object') {
    config.body = JSON.stringify(config.body);
  }
  const res = await fetch(url, config);
  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: 'Error desconocido' }));
    const msg = err.details
      ? Object.entries(err.details).map(([k, v]) => `${k}: ${v}`).join(', ')
      : err.error;
    throw new Error(msg || `Error ${res.status}`);
  }
  if (res.status === 204) return null;
  return res.json();
}

const api = {
  login: (data) => apiRequest('/usuarios/login', { method: 'POST', body: data }),
  registro: (data) => apiRequest('/usuarios/registro', { method: 'POST', body: data }),
  getUsuarios: () => apiRequest('/usuarios'),
  getUsuario: (id) => apiRequest(`/usuarios/${id}`),
  deleteUsuario: (id) => apiRequest(`/usuarios/${id}`, { method: 'DELETE' }),

  getHabitaciones: () => apiRequest('/habitaciones'),
  getHabitacion: (id) => apiRequest(`/habitaciones/${id}`),
  crearHabitacion: (data) => apiRequest('/habitaciones', { method: 'POST', body: data }),
  actualizarHabitacion: (id, data) => apiRequest(`/habitaciones/${id}`, { method: 'PUT', body: data }),
  deleteHabitacion: (id) => apiRequest(`/habitaciones/${id}`, { method: 'DELETE' }),
  habitacionesDisponibles: (params) => {
    const q = new URLSearchParams(params).toString();
    return apiRequest(`/habitaciones/disponibles${q ? '?' + q : ''}`);
  },

  getReservas: (params) => {
    const q = new URLSearchParams(params || {}).toString();
    return apiRequest(`/reservas${q ? '?' + q : ''}`);
  },
  getReserva: (id) => apiRequest(`/reservas/${id}`),
  crearReserva: (data) => apiRequest('/reservas', { method: 'POST', body: data }),
  cancelarReserva: (id) => apiRequest(`/reservas/${id}/cancelar`, { method: 'PUT' }),

  ocupadasPorFecha: (fecha) => apiRequest(`/reservas/estadisticas/ocupadas${fecha ? '?fecha=' + fecha : ''}`),
  ingresosPorMes: () => apiRequest('/reservas/estadisticas/ingresos-por-mes'),
  ocupacionPorMes: () => apiRequest('/reservas/estadisticas/ocupacion-por-mes'),
  tendencia: (desde, hasta) => {
    const p = new URLSearchParams();
    if (desde) p.set('desde', desde);
    if (hasta) p.set('hasta', hasta);
    const q = p.toString();
    return apiRequest(`/reservas/estadisticas/tendencia${q ? '?' + q : ''}`);
  },
  topHabitaciones: () => apiRequest('/reservas/estadisticas/habitaciones-mas-reservadas'),
};

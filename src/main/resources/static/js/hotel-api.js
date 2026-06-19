const API_BASE = 'http://localhost:8080/api';

function getAuthHeaders() {
  const token = getToken();
  const headers = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = 'Bearer ' + token;
  return headers;
}

async function apiRequest(path, options = {}) {
  const url = API_BASE + path;
  const config = $.extend({}, { headers: getAuthHeaders() }, options);
  if (config.body && typeof config.body === 'object') {
    config.body = JSON.stringify(config.body);
  }

  try {
    const res = await fetch(url, config);
    if (res.status === 401 || res.status === 403) {
      clearAuth();
      window.location.href = 'login.html';
      throw new Error('Sesión expirada');
    }
    if (!res.ok) {
      const err = await res.json().catch(() => ({ error: 'Error desconocido' }));
      const msg = err.details
        ? Object.entries(err.details).map(function (e) { return e[0] + ': ' + e[1]; }).join(', ')
        : err.error;
      throw new Error(msg || 'Error ' + res.status);
    }
    if (res.status === 204) return null;
    return res.json();
  } catch (e) {
    if (e.message === 'Failed to fetch') {
      throw new Error('No se pudo conectar con el servidor');
    }
    throw e;
  }
}

const api = {
  login: function (data) { return apiRequest('/usuarios/login', { method: 'POST', body: data }); },
  registro: function (data) { return apiRequest('/usuarios/registro', { method: 'POST', body: data }); },
  getUsuarios: function () { return apiRequest('/usuarios'); },
  getUsuario: function (id) { return apiRequest('/usuarios/' + id); },
  crearUsuario: function (data) { return apiRequest('/usuarios', { method: 'POST', body: data }); },
  actualizarUsuario: function (id, data) { return apiRequest('/usuarios/' + id, { method: 'PUT', body: data }); },
  deleteUsuario: function (id) { return apiRequest('/usuarios/' + id, { method: 'DELETE' }); },
  cambiarRol: function (id, rol) { return apiRequest('/usuarios/' + id + '/rol', { method: 'PUT', body: rol }); },

  getHabitaciones: function () { return apiRequest('/habitaciones'); },
  getHabitacion: function (id) { return apiRequest('/habitaciones/' + id); },
  crearHabitacion: function (data) { return apiRequest('/habitaciones', { method: 'POST', body: data }); },
  actualizarHabitacion: function (id, data) { return apiRequest('/habitaciones/' + id, { method: 'PUT', body: data }); },
  deleteHabitacion: function (id) { return apiRequest('/habitaciones/' + id, { method: 'DELETE' }); },
  habitacionesDisponibles: function (params) {
    var q = params ? Object.keys(params).map(function (k) { return k + '=' + params[k]; }).join('&') : '';
    return apiRequest('/habitaciones/disponibles' + (q ? '?' + q : ''));
  },

  getReservas: function (params) {
    var q = params ? Object.keys(params).map(function (k) { return k + '=' + params[k]; }).join('&') : '';
    return apiRequest('/reservas' + (q ? '?' + q : ''));
  },
  getReserva: function (id) { return apiRequest('/reservas/' + id); },
  crearReserva: function (data) { return apiRequest('/reservas', { method: 'POST', body: data }); },
  cancelarReserva: function (id) { return apiRequest('/reservas/' + id + '/cancelar', { method: 'PUT' }); },

  ocupadasPorFecha: function (fecha) {
    return apiRequest('/reservas/estadisticas/ocupadas' + (fecha ? '?fecha=' + fecha : ''));
  },
  ingresosPorMes: function () { return apiRequest('/reservas/estadisticas/ingresos-por-mes'); },
  ocupacionPorMes: function () { return apiRequest('/reservas/estadisticas/ocupacion-por-mes'); },
  tendencia: function (desde, hasta) {
    var p = [];
    if (desde) p.push('desde=' + desde);
    if (hasta) p.push('hasta=' + hasta);
    var q = p.join('&');
    return apiRequest('/reservas/estadisticas/tendencia' + (q ? '?' + q : ''));
  },
  topHabitaciones: function () { return apiRequest('/reservas/estadisticas/habitaciones-mas-reservadas'); },

  // Pagos
  getPagos: function (params) {
    var q = params ? Object.keys(params).map(function (k) { return k + '=' + params[k]; }).join('&') : '';
    return apiRequest('/pagos' + (q ? '?' + q : ''));
  },
  getPago: function (id) { return apiRequest('/pagos/' + id); },
  crearPago: function (data) { return apiRequest('/pagos', { method: 'POST', body: data }); },

  // Facturas
  getFacturas: function () { return apiRequest('/facturas'); },
  getFactura: function (id) { return apiRequest('/facturas/' + id); },
  getFacturaPorReserva: function (reservaId) { return apiRequest('/facturas/por-reserva/' + reservaId); },
  getFacturaPdfUrl: function (facturaId) {
    var token = getToken();
    return API_BASE + '/facturas/' + facturaId + '/pdf' + (token ? '?token=' + token : '');
  },

  // Cliente
  getMisReservas: function () { return apiRequest('/reservas/mias'); },
  getMisFacturas: function () { return apiRequest('/facturas/mias'); },
  crearReservaCliente: function (data) { return apiRequest('/reservas', { method: 'POST', body: data }); },
  actualizarMiPerfil: function (data) { return apiRequest('/usuarios/mi-perfil', { method: 'PUT', body: data }); },
};

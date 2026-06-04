function initEstadisticas() {
  const hoy = new Date().toISOString().split('T')[0];
  const hace30 = new Date(Date.now() - 30 * 86400000).toISOString().split('T')[0];
  const fechaOcup = document.getElementById('fecha-ocupadas');
  if (fechaOcup && !fechaOcup.value) fechaOcup.value = hoy;
  const fechaDesde = document.getElementById('fecha-tendencia-desde');
  if (fechaDesde && !fechaDesde.value) fechaDesde.value = hace30;
  const fechaHasta = document.getElementById('fecha-tendencia-hasta');
  if (fechaHasta && !fechaHasta.value) fechaHasta.value = hoy;
  cargarOcupadas();
  cargarIngresosPorMes();
  cargarOcupacionPorMes();
  cargarTopHabitaciones();
  cargarTendencia();
}

/* --- Ocupadas hoy --- */
async function cargarOcupadas() {
  const el = document.getElementById('stat-ocupadas');
  const fecha = document.getElementById('fecha-ocupadas')?.value || new Date().toISOString().split('T')[0];
  el.innerHTML = '<div class="loading">Cargando...</div>';
  try {
    const count = await api.ocupadasPorFecha(fecha);
    el.innerHTML = `
      <div style="text-align:center;padding:20px">
        <span style="font-size:48px;font-weight:700;color:var(--primary)">${count}</span>
        <p style="color:var(--gray-600);margin-top:8px">habitaciones ocupadas el ${fecha}</p>
      </div>
    `;
  } catch (err) {
    el.innerHTML = `<div class="error-message">${err.message}</div>`;
  }
}

/* --- Ingresos por mes --- */
async function cargarIngresosPorMes() {
  const el = document.getElementById('stat-ingresos');
  el.innerHTML = '<div class="loading">Cargando...</div>';
  try {
    const data = await api.ingresosPorMes();
    const rows = Array.isArray(data) ? data : [];
    if (!rows.length) {
      el.innerHTML = '<div class="empty-state">Sin datos</div>';
      return;
    }
    el.innerHTML = `
      <div class="stat-table-container">
        <table>
          <thead><tr><th>Mes</th><th>Total ingresos</th></tr></thead>
          <tbody>
            ${rows.map(r => `
              <tr>
                <td>${r.mes || r.anio + '-' + String(r.mesNumero || r.mes).padStart(2, '0')}</td>
                <td><strong>$${(r.total || r.ingresos || 0).toFixed(2)}</strong></td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
  } catch (err) {
    el.innerHTML = `<div class="error-message">${err.message}</div>`;
  }
}

/* --- Ocupacion por mes --- */
async function cargarOcupacionPorMes() {
  const el = document.getElementById('stat-ocupacion-mes');
  el.innerHTML = '<div class="loading">Cargando...</div>';
  try {
    const data = await api.ocupacionPorMes();
    const rows = Array.isArray(data) ? data : [];
    if (!rows.length) {
      el.innerHTML = '<div class="empty-state">Sin datos</div>';
      return;
    }
    el.innerHTML = `
      <div class="stat-table-container">
        <table>
          <thead><tr><th>Mes</th><th>Reservas</th></tr></thead>
          <tbody>
            ${rows.map(r => `
              <tr>
                <td>${r.mes || r.anio + '-' + String(r.mesNumero || r.mes).padStart(2, '0')}</td>
                <td><strong>${r.total || r.cantidad || 0}</strong></td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
  } catch (err) {
    el.innerHTML = `<div class="error-message">${err.message}</div>`;
  }
}

/* --- Top habitaciones --- */
async function cargarTopHabitaciones() {
  const el = document.getElementById('stat-top-hab');
  el.innerHTML = '<div class="loading">Cargando...</div>';
  try {
    const data = await api.topHabitaciones();
    const rows = Array.isArray(data) ? data : [];
    if (!rows.length) {
      el.innerHTML = '<div class="empty-state">Sin datos</div>';
      return;
    }
    el.innerHTML = `
      <div class="stat-table-container">
        <table>
          <thead><tr><th>#</th><th>Habitación</th><th>Veces reservada</th></tr></thead>
          <tbody>
            ${rows.map((r, i) => `
              <tr>
                <td>${i + 1}</td>
                <td>${r.habitacionNumero || r.numero || '#' + (r.habitacionId || '')}</td>
                <td><strong>${r.total || r.cantidad || 0}</strong></td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
  } catch (err) {
    el.innerHTML = `<div class="error-message">${err.message}</div>`;
  }
}

/* --- Tendencia --- */
async function cargarTendencia() {
  const el = document.getElementById('stat-tendencia');
  const desde = document.getElementById('fecha-tendencia-desde')?.value;
  const hasta = document.getElementById('fecha-tendencia-hasta')?.value;
  el.innerHTML = '<div class="loading">Cargando...</div>';
  try {
    const data = await api.tendencia(desde, hasta);
    const rows = Array.isArray(data) ? data : [];
    if (!rows.length) {
      el.innerHTML = '<div class="empty-state">Sin datos</div>';
      return;
    }
    el.innerHTML = `
      <div class="stat-table-container">
        <table>
          <thead><tr><th>Fecha</th><th>Reservas</th></tr></thead>
          <tbody>
            ${rows.map(r => `
              <tr>
                <td>${r.fecha || r.dia || '—'}</td>
                <td><strong>${r.total || r.cantidad || 0}</strong></td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
  } catch (err) {
    el.innerHTML = `<div class="error-message">${err.message}</div>`;
  }
}

function initDashboard() {
  const container = document.getElementById('dashboard-cards');
  container.innerHTML = '<div class="loading">Cargando dashboard...</div>';

  Promise.all([
    api.getHabitaciones(),
    api.getUsuarios(),
    api.getReservas(),
    api.ocupadasPorFecha(new Date().toISOString().split('T')[0]),
  ])
    .then(([habitaciones, usuarios, reservas, ocupadas]) => {
      const totalHab = Array.isArray(habitaciones) ? habitaciones.length : 0;
      const totalUsu = Array.isArray(usuarios) ? usuarios.length : 0;
      const totalRes = Array.isArray(reservas) ? reservas.length : 0;
      const countOcupadas = typeof ocupadas === 'number' ? ocupadas : 0;

      container.innerHTML = `
        <div class="card card-stat">
          <div class="card-stat-icon blue">🏨</div>
          <div class="card-stat-info">
            <h3>${totalHab}</h3>
            <p>Habitaciones</p>
          </div>
        </div>
        <div class="card card-stat">
          <div class="card-stat-icon green">👥</div>
          <div class="card-stat-info">
            <h3>${totalUsu}</h3>
            <p>Usuarios</p>
          </div>
        </div>
        <div class="card card-stat">
          <div class="card-stat-icon orange">📋</div>
          <div class="card-stat-info">
            <h3>${totalRes}</h3>
            <p>Reservas totales</p>
          </div>
        </div>
        <div class="card card-stat">
          <div class="card-stat-icon red">🛏️</div>
          <div class="card-stat-info">
            <h3>${countOcupadas}</h3>
            <p>Habitaciones ocupadas hoy</p>
          </div>
        </div>
      `;
    })
    .catch((err) => {
      container.innerHTML = `<div class="error-message">Error al cargar: ${err.message}</div>`;
    });
}

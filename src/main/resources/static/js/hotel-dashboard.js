$(document).ready(function () {
  var user = requireAuth();
  if (!user) return;

  setActiveSidebar('dashboard');

  var container = $('#dashboard-cards');
  var isAdminOrRecep = hasRol('ADMIN', 'RECEPCIONISTA');

  var promises = [api.getHabitaciones()];
  if (isAdminOrRecep) {
    promises.push(api.getUsuarios());
    promises.push(api.getReservas());
    promises.push(api.ocupadasPorFecha(new Date().toISOString().split('T')[0]));
  }

  Promise.allSettled(promises).then(function (results) {
    var hab = results[0].value;
    var totalHab = Array.isArray(hab) ? hab.length : 0;
    var html = '';

    html += '<div class="col-xl-3 col-md-6 mb-4"><div class="card border-left-primary shadow h-100 py-2"><div class="card-body"><div class="row no-gutters align-items-center"><div class="col mr-2"><div class="text-xs font-weight-bold text-primary text-uppercase mb-1">Habitaciones</div><div class="h5 mb-0 font-weight-bold text-gray-800">' + totalHab + '</div></div><div class="col-auto"><i class="fas fa-bed fa-2x text-gray-300"></i></div></div></div></div></div>';

    if (isAdminOrRecep && results.length >= 4) {
      var users = results[1].value;
      var reservas = results[2].value;
      var ocupadas = results[3].value;
      var totalUsu = Array.isArray(users) ? users.length : 0;
      var totalRes = Array.isArray(reservas) ? reservas.length : 0;
      var countOcup = typeof ocupadas === 'number' ? ocupadas : 0;

      html += '<div class="col-xl-3 col-md-6 mb-4"><div class="card border-left-success shadow h-100 py-2"><div class="card-body"><div class="row no-gutters align-items-center"><div class="col mr-2"><div class="text-xs font-weight-bold text-success text-uppercase mb-1">Usuarios</div><div class="h5 mb-0 font-weight-bold text-gray-800">' + totalUsu + '</div></div><div class="col-auto"><i class="fas fa-users fa-2x text-gray-300"></i></div></div></div></div></div>';
      html += '<div class="col-xl-3 col-md-6 mb-4"><div class="card border-left-info shadow h-100 py-2"><div class="card-body"><div class="row no-gutters align-items-center"><div class="col mr-2"><div class="text-xs font-weight-bold text-info text-uppercase mb-1">Reservas totales</div><div class="h5 mb-0 font-weight-bold text-gray-800">' + totalRes + '</div></div><div class="col-auto"><i class="fas fa-calendar-check fa-2x text-gray-300"></i></div></div></div></div></div>';
      html += '<div class="col-xl-3 col-md-6 mb-4"><div class="card border-left-warning shadow h-100 py-2"><div class="card-body"><div class="row no-gutters align-items-center"><div class="col mr-2"><div class="text-xs font-weight-bold text-warning text-uppercase mb-1">Ocupadas hoy</div><div class="h5 mb-0 font-weight-bold text-gray-800">' + countOcup + '</div></div><div class="col-auto"><i class="fas fa-door-closed fa-2x text-gray-300"></i></div></div></div></div></div>';
    }

    container.html(html);
  });

  if (isAdminOrRecep) {
    loadDashboardCharts();
  } else {
    $('#dashboard-charts').hide();
  }
});

function loadDashboardCharts() {
  api.ingresosPorMes().then(function (data) {
    var rows = Array.isArray(data) ? data : [];
    var labels = rows.map(function (r) { return r.mes || r.anio + '-' + String(r.mesNumero || r.mes).padStart(2, '0'); });
    var values = rows.map(function (r) { return r.total || r.ingresos || 0; });

    new Chart(document.getElementById('chartOcupacion'), {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          label: 'Ingresos',
          data: values,
          backgroundColor: 'rgba(78, 115, 223, 0.05)',
          borderColor: 'rgba(78, 115, 223, 1)',
          pointBackgroundColor: 'rgba(78, 115, 223, 1)',
          fill: true
        }]
      },
      options: {
        maintainAspectRatio: false,
        scales: { y: { beginAtZero: true, ticks: { callback: function (v) { return '$' + v; } } } }
      }
    });
  });

  api.topHabitaciones().then(function (data) {
    var rows = Array.isArray(data) ? data : [];
    var labels = rows.slice(0, 5).map(function (r) { return '#' + (r.habitacionNumero || r.numero || r.habitacionId); });
    var values = rows.slice(0, 5).map(function (r) { return r.total || r.cantidad || 0; });

    new Chart(document.getElementById('chartPie'), {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{ data: values, backgroundColor: ['#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b'] }]
      },
      options: { maintainAspectRatio: false }
    });
  });
}

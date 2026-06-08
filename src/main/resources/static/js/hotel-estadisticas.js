var chartIngresos = null;
var chartOcupacionMes = null;
var chartTendencia = null;
var chartTopHab = null;

$(document).ready(function () {
  if (!requireAuth()) return;
  setActiveSidebar('estadisticas');

  var hoy = new Date().toISOString().split('T')[0];
  var hace30 = new Date(Date.now() - 30 * 86400000).toISOString().split('T')[0];

  $('#fecha-ocupadas').val(hoy).on('change', cargarOcupadas);
  $('#fecha-tendencia-desde').val(hace30);
  $('#fecha-tendencia-hasta').val(hoy);
  $('#btn-filtrar-tendencia').on('click', cargarTendencia);

  cargarOcupadas();
  cargarIngresosPorMes();
  cargarOcupacionPorMes();
  cargarTopHabitaciones();
  cargarTendencia();
});

function cargarOcupadas() {
  var fecha = $('#fecha-ocupadas').val() || new Date().toISOString().split('T')[0];
  var el = $('#stat-ocupadas');
  el.html('<div class="text-center py-3"><i class="fas fa-spinner fa-spin"></i> Cargando...</div>');

  api.ocupadasPorFecha(fecha).then(function (count) {
    el.html('<div style="text-align:center;padding:20px"><span style="font-size:48px;font-weight:700;color:#4e73df">' + count + '</span><p style="color:#858796;margin-top:8px">habitaciones ocupadas el ' + fecha + '</p></div>');
  }).catch(function () {
    el.html('<div class="text-center text-danger py-3">Error al cargar</div>');
  });
}

function cargarIngresosPorMes() {
  api.ingresosPorMes().then(function (data) {
    var rows = Array.isArray(data) ? data : [];
    var labels = rows.map(function (r) { return r.mes || r.anio + '-' + String(r.mesNumero || r.mes).padStart(2, '0'); });
    var values = rows.map(function (r) { return r.total || r.ingresos || 0; });

    if (chartIngresos) { chartIngresos.destroy(); }
    chartIngresos = new Chart(document.getElementById('chartIngresos'), {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Ingresos',
          data: values,
          backgroundColor: '#1cc88a',
          borderColor: '#1cc88a'
        }]
      },
      options: {
        maintainAspectRatio: false,
        scales: { y: { beginAtZero: true, ticks: { callback: function (v) { return '$' + v; } } } }
      }
    });
  });
}

function cargarOcupacionPorMes() {
  api.ocupacionPorMes().then(function (data) {
    var rows = Array.isArray(data) ? data : [];
    var labels = rows.map(function (r) { return r.mes || r.anio + '-' + String(r.mesNumero || r.mes).padStart(2, '0'); });
    var values = rows.map(function (r) { return r.total || r.cantidad || 0; });

    if (chartOcupacionMes) { chartOcupacionMes.destroy(); }
    chartOcupacionMes = new Chart(document.getElementById('chartOcupacionMes'), {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          data: values,
          backgroundColor: ['#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b', '#858796']
        }]
      },
      options: { maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }
    });
  });
}

function cargarTopHabitaciones() {
  api.topHabitaciones().then(function (data) {
    var rows = Array.isArray(data) ? data : [];
    var labels = rows.slice(0, 10).map(function (r) { return '#' + (r.habitacionNumero || r.numero || r.habitacionId); });
    var values = rows.slice(0, 10).map(function (r) { return r.total || r.cantidad || 0; });

    if (chartTopHab) { chartTopHab.destroy(); }
    chartTopHab = new Chart(document.getElementById('chartTopHab'), {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Veces reservada',
          data: values,
          backgroundColor: '#e74a3b',
          borderColor: '#e74a3b'
        }]
      },
      options: {
        indexAxis: 'y',
        maintainAspectRatio: false,
        scales: { x: { beginAtZero: true, ticks: { stepSize: 1 } } }
      }
    });
  });
}

function cargarTendencia() {
  var desde = $('#fecha-tendencia-desde').val();
  var hasta = $('#fecha-tendencia-hasta').val();

  api.tendencia(desde, hasta).then(function (data) {
    var rows = Array.isArray(data) ? data : [];
    var labels = rows.map(function (r) { return r.fecha || r.dia || ''; });
    var values = rows.map(function (r) { return r.total || r.cantidad || 0; });

    if (chartTendencia) { chartTendencia.destroy(); }
    chartTendencia = new Chart(document.getElementById('chartTendencia'), {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          label: 'Reservas',
          data: values,
          backgroundColor: 'rgba(246, 194, 62, 0.05)',
          borderColor: '#f6c23e',
          pointBackgroundColor: '#f6c23e',
          fill: true
        }]
      },
      options: {
        maintainAspectRatio: false,
        scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } }
      }
    });
  });
}

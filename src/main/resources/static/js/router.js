const sections = {
  dashboard: { title: 'Dashboard', init: initDashboard },
  habitaciones: { title: 'Habitaciones', init: initHabitaciones },
  usuarios: { title: 'Usuarios', init: initUsuarios },
  reservas: { title: 'Reservas', init: initReservas },
  estadisticas: { title: 'Estadísticas', init: initEstadisticas },
};

function navigateTo(sectionId) {
  if (!requireAuth()) return;

  const section = sections[sectionId];
  if (!section) {
    window.location.hash = '#/dashboard';
    return;
  }

  document.querySelectorAll('.section').forEach(el => el.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));

  const sectionEl = document.getElementById(`section-${sectionId}`);
  if (sectionEl) sectionEl.classList.add('active');

  const navItem = document.querySelector(`[data-section="${sectionId}"]`);
  if (navItem) navItem.classList.add('active');

  document.getElementById('topbar-title').textContent = section.title;

  if (section.init) section.init();
}

function setupRouter() {
  window.addEventListener('hashchange', () => {
    const hash = window.location.hash.replace('#', '').replace(/^\//, '');
    navigateTo(hash || 'dashboard');
  });

  document.querySelectorAll('.nav-item').forEach(item => {
    item.addEventListener('click', () => {
      const section = item.dataset.section;
      window.location.hash = `#/${section}`;
    });
  });
}

function initApp() {
  if (!requireAuth()) return;
  const hash = window.location.hash.replace('#', '').replace(/^\//, '');
  navigateTo(hash || 'dashboard');
}

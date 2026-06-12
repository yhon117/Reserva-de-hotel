var AUTH_KEY = 'recervhotel_user';
var TOKEN_KEY = 'recervhotel_token';

function getLoggedUser() {
  var raw = sessionStorage.getItem(AUTH_KEY);
  return raw ? JSON.parse(raw) : null;
}

function getToken() {
  return sessionStorage.getItem(TOKEN_KEY);
}

function setLoggedUser(user, token) {
  sessionStorage.setItem(AUTH_KEY, JSON.stringify(user));
  sessionStorage.setItem(TOKEN_KEY, token);
}

function clearAuth() {
  sessionStorage.removeItem(AUTH_KEY);
  sessionStorage.removeItem(TOKEN_KEY);
}

function getRol() {
  var user = getLoggedUser();
  return user ? user.rol : null;
}

function hasRol() {
  var userRol = getRol();
  if (!userRol) return false;
  for (var i = 0; i < arguments.length; i++) {
    if (arguments[i] === userRol) return true;
  }
  return false;
}

function requireAuth() {
  var user = getLoggedUser();
  var token = getToken();
  if (!user || !token) {
    window.location.href = 'login.html';
    return null;
  }
  return user;
}

function applyRoleVisibility() {
  var isAdmin = hasRol('ADMIN');
  var isRecep = hasRol('RECEPCIONISTA');

  $('#sidebar-usuarios').toggle(isAdmin || isRecep);
  $('#sidebar-estadisticas').toggle(isAdmin || isRecep);
  $('#sidebar-facturas').toggle(isAdmin || isRecep);
  $('#sidebar-pagos').toggle(isAdmin || isRecep);
}

function setTopbarUser() {
  var user = getLoggedUser();
  if (user) {
    $('#topbar-user-name').text(user.nombre || user.email);
    $('#topbar-user-full').text((user.nombre || user.email) + ' (' + user.rol + ')');
  }
}

function setupLogout() {
  $('#btn-logout, .logout-link').on('click', function (e) {
    e.preventDefault();
    clearAuth();
    window.location.href = 'login.html';
  });
}

function setActiveSidebar(page) {
  $('.sidebar .nav-item').removeClass('active');
  $('#sidebar-' + page).addClass('active');
}

$(document).ready(function () {
  if (window.location.pathname.indexOf('login.html') === -1 &&
      window.location.pathname.indexOf('register.html') === -1) {
    if (requireAuth()) {
      applyRoleVisibility();
      setTopbarUser();
      setupLogout();
    }
  }
});

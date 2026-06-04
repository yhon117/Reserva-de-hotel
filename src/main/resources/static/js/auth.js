const AUTH_KEY = 'recervhotel_user';

function getLoggedUser() {
  const raw = sessionStorage.getItem(AUTH_KEY);
  return raw ? JSON.parse(raw) : null;
}

function setLoggedUser(user) {
  sessionStorage.setItem(AUTH_KEY, JSON.stringify(user));
}

function clearAuth() {
  sessionStorage.removeItem(AUTH_KEY);
}

function requireAuth() {
  const user = getLoggedUser();
  if (!user) {
    document.getElementById('login-page').style.display = 'flex';
    document.getElementById('app').style.display = 'none';
    return null;
  }
  document.getElementById('login-page').style.display = 'none';
  document.getElementById('app').style.display = 'flex';
  document.getElementById('topbar-user').textContent = user.nombre || user.email;
  return user;
}

function setupLogin() {
  const form = document.getElementById('login-form');
  const errorEl = document.getElementById('login-error');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    errorEl.style.display = 'none';
    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value;

    try {
      const user = await api.login({ email, password });
      setLoggedUser(user);
      requireAuth();
      window.location.hash = '#/dashboard';
    } catch (err) {
      errorEl.textContent = err.message || 'Credenciales inválidas';
      errorEl.style.display = 'block';
    }
  });
}

function setupLogout() {
  document.getElementById('btn-logout').addEventListener('click', () => {
    clearAuth();
    window.location.hash = '';
    document.getElementById('login-page').style.display = 'flex';
    document.getElementById('app').style.display = 'none';
    document.getElementById('login-email').value = '';
    document.getElementById('login-password').value = '';
  });
}

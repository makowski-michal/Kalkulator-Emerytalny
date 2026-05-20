async function requireAuth(expectedRole) {
  const res = await fetch('/api/v1/auth/me', { credentials: 'include' });
  if (!res.ok) { window.location.href = '/login.html'; return null; }
  const user = await res.json();
  if (expectedRole && user.role !== expectedRole) {
    window.location.href = user.role === 'EMPLOYER'
      ? '/employer-dashboard.html' : '/employee-dashboard.html';
    return null;
  }
  return user;
}

async function logout() {
  await fetch('/api/v1/auth/logout', { method: 'POST', credentials: 'include' });
  window.location.href = '/login.html';
}

function fmt(value) {
  if (value === null || value === undefined) return '0,00';
  return Number(value).toLocaleString('pl-PL', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function monthName(m) {
  return ['Styczeń','Luty','Marzec','Kwiecień','Maj','Czerwiec',
          'Lipiec','Sierpień','Wrzesień','Październik','Listopad','Grudzień'][m - 1];
}

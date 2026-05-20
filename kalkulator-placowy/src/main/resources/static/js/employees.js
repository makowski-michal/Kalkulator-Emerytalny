let sortField = 'lastName';
let sortDir   = 'asc';
let allEmployees = [];

async function loadEmployees() {
  const search = document.getElementById('searchInput')?.value || '';
  let url = '/api/v1/employees?';
  if (search) url += `search=${encodeURIComponent(search)}`;

  const res = await fetch(url, { credentials: 'include' });
  allEmployees = res.ok ? await res.json() : [];
  renderEmployees();
}

function sortBy(field) {
  if (sortField === field) sortDir = sortDir === 'asc' ? 'desc' : 'asc';
  else { sortField = field; sortDir = 'asc'; }
  renderEmployees();
}

function renderEmployees() {
  const sorted = [...allEmployees].sort((a, b) => {
    const va = (a[sortField] || '').toLowerCase();
    const vb = (b[sortField] || '').toLowerCase();
    return sortDir === 'asc' ? va.localeCompare(vb) : vb.localeCompare(va);
  });

  const tbody = document.getElementById('empList');
  if (!sorted.length) {
    tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-4">Brak pracowników</td></tr>';
    return;
  }
  tbody.innerHTML = sorted.map(e => `
    <tr>
      <td>${e.lastName}</td>
      <td>${e.firstName}</td>
      <td>${e.pesel || '–'}</td>
      <td class="text-end">${fmt(e.grossSalary)} zł</td>
      <td class="text-center">
        <a href="employee-form.html?id=${e.id}" class="btn btn-sm btn-outline-primary">Edytuj</a>
        <a href="payslip-form.html?employeeId=${e.id}" class="btn btn-sm btn-outline-success ms-1">Pasek</a>
        <button onclick="deactivate(${e.id})" class="btn btn-sm btn-outline-danger ms-1">Dezaktywuj</button>
      </td>
    </tr>`).join('');
}

async function deactivate(id) {
  if (!confirm('Dezaktywować tego pracownika?')) return;
  const res = await fetch(`/api/v1/employees/${id}`, { method: 'DELETE', credentials: 'include' });
  if (res.ok) await loadEmployees();
  else alert('Błąd dezaktywacji');
}

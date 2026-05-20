const absenceTypeLabel = {
  SICK_LEAVE:    'L4',
  PAID_LEAVE:    'Urlop płatny',
  UNPAID_LEAVE:  'Urlop bezpłatny',
  MATERNITY:     'Urlop macierzyński'
};

async function loadAbsences() {
  const empId = document.getElementById('filterEmp')?.value || '';
  let url = '/api/v1/absences?';
  if (empId) url += `employeeId=${empId}`;

  const res = await fetch(url, { credentials: 'include' });
  const list = res.ok ? await res.json() : [];
  const tbody = document.getElementById('absenceList');

  if (!list.length) {
    tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted py-4">Brak nieobecności</td></tr>';
    return;
  }

  const empMap = {};
  (window._employees || []).forEach(e => { empMap[e.id] = `${e.lastName} ${e.firstName}`; });

  tbody.innerHTML = list.map(a => `
    <tr>
      <td>${empMap[a.employeeId] || a.employeeId}</td>
      <td><span class="badge bg-secondary">${absenceTypeLabel[a.type] || a.type}</span></td>
      <td>${a.dateFrom}</td>
      <td>${a.dateTo}</td>
      <td class="text-center">${a.daysCount}</td>
      <td class="text-muted small">${a.note || ''}</td>
      <td>
        <button onclick="deleteAbsence(${a.id})" class="btn btn-sm btn-outline-danger">Usuń</button>
      </td>
    </tr>`).join('');
}

async function deleteAbsence(id) {
  if (!confirm('Usunąć tę nieobecność?')) return;
  const res = await fetch(`/api/v1/absences/${id}`, { method: 'DELETE', credentials: 'include' });
  if (res.ok) {
    await loadAbsences();
  } else {
    const err = await res.json().catch(() => ({}));
    const ab = document.getElementById('alertBox');
    ab.textContent = err.error || 'Nie można usunąć';
    ab.className = 'alert alert-danger';
  }
}

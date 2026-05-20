let currentPayslipId = null;

async function loadPayslips(employeeId) {
  const year  = document.getElementById('filterYear')?.value  || '';
  const month = document.getElementById('filterMonth')?.value || '';
  let url = '/api/v1/payslips?';
  if (employeeId) url += `employeeId=${employeeId}&`;
  if (year)       url += `year=${year}&`;
  if (month)      url += `month=${month}&`;

  const res = await fetch(url, { credentials: 'include' });
  const list = res.ok ? await res.json() : [];
  const tbody = document.getElementById('payslipList');

  if (!list.length) {
    tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-4">Brak pasków</td></tr>';
    return;
  }

  tbody.innerHTML = list.map(p => `
    <tr>
      <td>${monthName(p.periodMonth)} ${p.periodYear}</td>
      <td class="text-end">${fmt(p.grossSalary)} zł</td>
      <td class="text-end fw-bold text-success">${fmt(p.netSalary)} zł</td>
      <td class="text-end text-muted">${fmt(p.employerTotalCost)} zł</td>
      <td class="text-center">
        <button class="btn btn-sm btn-outline-primary" onclick="showPayslip(${p.id})">Szczegóły</button>
        <a href="/api/v1/payslips/${p.id}/pdf" class="btn btn-sm btn-outline-secondary ms-1" download>PDF</a>
      </td>
    </tr>`).join('');
}

async function showPayslip(id) {
  currentPayslipId = id;
  const res = await fetch(`/api/v1/payslips/${id}`, { credentials: 'include' });
  if (!res.ok) return;
  const p = await res.json();

  document.getElementById('payslipModalTitle').textContent =
    `Pasek płacowy – ${monthName(p.periodMonth)} ${p.periodYear}`;

  document.getElementById('payslipModalBody').innerHTML = renderPayslipDetail(p);
  new bootstrap.Modal(document.getElementById('payslipModal')).show();
}

function downloadCurrentPdf() {
  if (currentPayslipId) window.open(`/api/v1/payslips/${currentPayslipId}/pdf`, '_blank');
}

function renderPayslipDetail(p) {
  return `
  <div class="small text-muted mb-2">${p.companyName} | NIP: ${p.companyNip||'–'} | REGON: ${p.companyRegon||'–'}</div>
  <div class="small mb-3">Pracownik: <strong>${p.employeeFirstName} ${p.employeeLastName}</strong> | PESEL: ${p.employeePesel||'–'}</div>

  <div class="payslip-section-header">Składniki brutto</div>
  <div class="payslip-row"><span>Wynagrodzenie zasadnicze / brutto</span><span>${fmt(p.grossSalary)} zł</span></div>
  ${p.bonus > 0 ? `<div class="payslip-row"><span>Premia</span><span>${fmt(p.bonus)} zł</span></div>` : ''}
  ${p.allowances > 0 ? `<div class="payslip-row"><span>Dodatki</span><span>${fmt(p.allowances)} zł</span></div>` : ''}

  <div class="payslip-section-header mt-2">Składki ZUS (pracownik)</div>
  <div class="payslip-row"><span>Emerytalna 9,76%</span><span>- ${fmt(p.pensionContribEmployee)} zł</span></div>
  <div class="payslip-row"><span>Rentowa 1,50%</span><span>- ${fmt(p.disabilityContribEmployee)} zł</span></div>
  <div class="payslip-row"><span>Chorobowa 2,45%</span><span>- ${fmt(p.sicknessContrib)} zł</span></div>
  <div class="payslip-row"><span>Zdrowotna 9,00%</span><span>- ${fmt(p.healthContrib)} zł</span></div>

  <div class="payslip-section-header mt-2">Podatek</div>
  <div class="payslip-row"><span>Zaliczka PIT</span><span>- ${fmt(p.incomeTaxAdvance)} zł</span></div>

  ${p.garnishment > 0 ? `
  <div class="payslip-section-header mt-2">Potrącenia</div>
  <div class="payslip-row"><span>Komornicze</span><span>- ${fmt(p.garnishment)} zł</span></div>` : ''}
  ${p.voluntaryDeduction > 0 ? `<div class="payslip-row"><span>Dobrowolne</span><span>- ${fmt(p.voluntaryDeduction)} zł</span></div>` : ''}

  <div class="payslip-section-header mt-2">Podsumowanie</div>
  <div class="payslip-row payslip-net"><span>NETTO (do wypłaty)</span><span>${fmt(p.netSalary)} zł</span></div>
  <div class="payslip-row text-muted small"><span>Całkowity koszt pracodawcy</span><span>${fmt(p.employerTotalCost)} zł</span></div>

  ${p.sickLeaveDays > 0 ? `<div class="small text-muted mt-2">Dni L4: ${p.sickLeaveDays}</div>` : ''}
  ${p.unpaidLeaveDays > 0 ? `<div class="small text-muted">Dni urlopu bezpłatnego: ${p.unpaidLeaveDays}</div>` : ''}
  `;
}

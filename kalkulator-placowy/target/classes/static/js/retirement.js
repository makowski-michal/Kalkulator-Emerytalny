let retirementChart = null;

async function loadRetirement(employeeId) {
  const res = await fetch(`/api/v1/retirement/${employeeId}/current`, { credentials: 'include' });
  if (!res.ok) return;
  const data = await res.json();

  document.getElementById('currentPension').textContent = fmt(data.currentPension) + ' zł';
  document.getElementById('totalContribs').textContent  = fmt(data.totalPensionContribs) + ' zł';
  document.getElementById('currentAge').textContent     = data.currentAge;

  const contributions = data.monthlyContributions || [];
  if (!contributions.length) {
    document.getElementById('noChartData').classList.remove('d-none');
    return;
  }

  const labels = contributions.map(c => `${c.year}-${String(c.month).padStart(2, '0')}`);
  const values = contributions.map(c => parseFloat(c.cumulative));

  if (retirementChart) retirementChart.destroy();
  retirementChart = new Chart(document.getElementById('retirementChart'), {
    type: 'line',
    data: {
      labels,
      datasets: [{
        label: 'Skumulowane składki emerytalne (ZUS I filar) [zł]',
        data: values,
        borderColor: '#1A5276',
        backgroundColor: 'rgba(26,82,118,.12)',
        fill: true,
        tension: 0.3,
        pointRadius: values.length > 24 ? 0 : 3
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: { position: 'top' },
        tooltip: {
          callbacks: {
            label: ctx => ' ' + Number(ctx.raw).toLocaleString('pl-PL',
              { minimumFractionDigits: 2 }) + ' zł'
          }
        }
      },
      scales: {
        y: {
          ticks: {
            callback: v => Number(v).toLocaleString('pl-PL') + ' zł'
          }
        }
      }
    }
  });
}

async function calcForecast() {
  const targetAge = parseInt(document.getElementById('retirementAge').value);
  const ofe = parseFloat(document.getElementById('ofeAmount').value) || 0;

  const res = await fetch(`/api/v1/retirement/${window._employeeId}/forecast`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ targetRetirementAge: targetAge, ofeAmount: ofe }),
    credentials: 'include'
  });
  if (!res.ok) return;
  const data = await res.json();

  document.getElementById('forecastAge').textContent         = data.targetRetirementAge;
  document.getElementById('futurePension').textContent       = fmt(data.futurePension) + ' zł';
  document.getElementById('monthsToRetirement').textContent  = data.monthsToRetirement;
  document.getElementById('forecastTotal').textContent       = fmt(data.totalContribs) + ' zł';
  document.getElementById('forecastResult').classList.remove('d-none');
}

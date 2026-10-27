const form = document.getElementById('booking-form');
const message = document.getElementById('message');
const list = document.getElementById('booking-list');

async function loadBookings() {
  try {
    const response = await fetch('/api/bookings');
    if (!response.ok) throw new Error('Could not load bookings');
    const bookings = await response.json();
    if (!bookings.length) {
      list.innerHTML = '<tr><td colspan="5" class="empty">No bookings yet. Add a student above.</td></tr>';
      return;
    }
    list.innerHTML = bookings.map(b => `<tr><td>${escapeHtml(b.id)}</td><td><strong>${escapeHtml(b.name)}</strong></td><td>${escapeHtml(b.phone)}</td><td>${escapeHtml(b.course)}</td><td>${escapeHtml(b.room)}</td></tr>`).join('');
  } catch (err) {
    list.innerHTML = '<tr><td colspan="5" class="empty">Unable to load records. Start the Java server first.</td></tr>';
  }
}
function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
}
form.addEventListener('submit', async (event) => {
  event.preventDefault();
  message.className = '';
  message.textContent = 'Saving booking...';
  const body = new URLSearchParams(new FormData(form));
  try {
    const response = await fetch('/api/bookings', { method: 'POST', headers: {'Content-Type':'application/x-www-form-urlencoded'}, body });
    const result = await response.json();
    if (!response.ok) throw new Error(result.error || 'Could not save booking');
    message.className = 'success';
    message.textContent = 'Booking saved successfully!';
    form.reset();
    await loadBookings();
  } catch (err) {
    message.className = 'error';
    message.textContent = 'Could not save. Make sure the Java server is running.';
  }
});
document.getElementById('refresh').addEventListener('click', loadBookings);
loadBookings();

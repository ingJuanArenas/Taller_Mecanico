export function saveSession(user) {
  localStorage.setItem("userId",   String(user.id));
  localStorage.setItem("userName", user.name);
  localStorage.setItem("userRole", user.role);
}

export function getSession() {
  return {
    id:   localStorage.getItem("userId"),
    name: localStorage.getItem("userName"),
    role: localStorage.getItem("userRole"),
  };
}

export function clearSession() { localStorage.clear(); }

export function requireAuth(expectedRole) {
  const s = getSession();
  if (!s.id) { window.location.href = "index.html"; return; }
  if (expectedRole && s.role !== expectedRole) window.location.href = "index.html";
}

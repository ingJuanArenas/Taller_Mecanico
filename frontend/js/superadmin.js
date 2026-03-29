import { UserAPI, getOrEmpty } from "./api.js";
import { getSession, requireAuth, clearSession } from "./auth.js";

requireAuth("SUPER_ADMIN");

const session = getSession();
let usersCache = [];

function showToast(msg, type = "info") {
  const t = document.createElement("div");
  t.className = `toast toast-${type}`;
  t.textContent = msg;
  document.body.appendChild(t);
  setTimeout(() => t.remove(), 3500);
}

function roleLabel(role) {
  if (role === "SERVICE_ADVISOR") return "FACTURADOR";
  if (role === "MECHANIC") return "MECANICO";
  if (role === "SUPER_ADMIN") return "SUPER ADMIN";
  return role;
}

function showLista() {
  document.getElementById("vistaLista").classList.remove("hidden");
  document.getElementById("vistaCrear").classList.add("hidden");
}

function showCrear() {
  document.getElementById("vistaLista").classList.add("hidden");
  document.getElementById("vistaCrear").classList.remove("hidden");
}

function filterUsers() {
  const q = document.getElementById("buscarNombre").value.trim().toLowerCase();
  const list = q
    ? usersCache.filter(u => (u.name || "").toLowerCase().includes(q))
    : usersCache;
  renderUsers(list);
}

function renderUsers(list) {
  const empty = document.getElementById("usersEmpty");
  const wrap = document.getElementById("usersTableWrap");
  const body = document.getElementById("usersBody");
  body.innerHTML = "";

  if (list.length === 0) {
    empty.classList.remove("hidden");
    wrap.classList.add("hidden");
    return;
  }
  empty.classList.add("hidden");
  wrap.classList.remove("hidden");

  const myId = session.id;

  for (const u of list) {
    const tr = document.createElement("tr");
    const canDelete = String(u.id) !== myId;
    tr.innerHTML = `
      <td>${u.id}</td>
      <td>${u.name ?? ""}</td>
      <td><span class="role-badge">${roleLabel(u.role)}</span></td>
      <td>${u.accessCode ?? ""}</td>
      <td>
        ${
          canDelete
            ? `<button type="button" class="btn btn-danger btn-del-user" data-id="${u.id}">🗑️</button>`
            : "—"
        }
      </td>
    `;
    body.appendChild(tr);
  }

  body.querySelectorAll(".btn-del-user").forEach(btn => {
    btn.addEventListener("click", () => eliminarUsuario(Number(btn.dataset.id)));
  });
}

async function loadUsers() {
  usersCache = await getOrEmpty(() => UserAPI.getAll());
  filterUsers();
}

async function eliminarUsuario(id) {
  if (!confirm("¿Eliminar este usuario?")) return;
  try {
    await UserAPI.delete(id);
    showToast("Usuario eliminado", "success");
    await loadUsers();
  } catch (e) {
    showToast(e.message || "Error al eliminar", "error");
  }
}

async function crearUsuario() {
  try {
    await UserAPI.create({
      id:         parseInt(document.getElementById("uCedula").value, 10),
      name:       document.getElementById("uNombre").value.trim(),
      role:       document.getElementById("uRole").value,
      accessCode: document.getElementById("uCodigo").value.trim(),
      active:     true,
    });
    showToast("Usuario creado", "success");
    document.getElementById("uCedula").value = "";
    document.getElementById("uNombre").value = "";
    document.getElementById("uCodigo").value = "";
    await loadUsers();
    showLista();
  } catch (e) {
    showToast(e.message || "Error al crear", "error");
  }
}

document.getElementById("welcomeName").textContent = session.name.toUpperCase();

document.getElementById("btnLogout").addEventListener("click", () => {
  clearSession();
  window.location.href = "index.html";
});

document.getElementById("btnNuevoUsuario").addEventListener("click", () => {
  showCrear();
});

document.getElementById("btnVolverLista").addEventListener("click", () => {
  showLista();
});

document.getElementById("btnCrearUsuario").addEventListener("click", () => crearUsuario());

document.getElementById("buscarNombre").addEventListener("input", filterUsers);

loadUsers();

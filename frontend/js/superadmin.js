import { UserAPI, OrderAPI, getOrEmpty } from "./api.js";
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

// --- TAB LOGIC ---
document.getElementById("tabUsuarios").addEventListener("click", () => {
  document.getElementById("moduleUsuarios").classList.remove("hidden");
  document.getElementById("moduleOrdenes").classList.add("hidden");
  document.getElementById("tabUsuarios").className = "btn btn-accent";
  document.getElementById("tabOrdenes").className = "btn btn-primary";
});

document.getElementById("tabOrdenes").addEventListener("click", () => {
  document.getElementById("moduleUsuarios").classList.add("hidden");
  document.getElementById("moduleOrdenes").classList.remove("hidden");
  document.getElementById("tabUsuarios").className = "btn btn-primary";
  document.getElementById("tabOrdenes").className = "btn btn-accent";
  loadOrdenes();
});

// --- ORDER LOGIC ---
let ordenesCacheSuper = [];
let interveneOrderId = null;

async function loadOrdenes() {
  const all = await getOrEmpty(() => OrderAPI.getAll());
  ordenesCacheSuper = all.filter(o => o.status !== "COMPLETED");
  renderOrdenes(ordenesCacheSuper);
}

function renderOrdenes(list) {
  const empty = document.getElementById("ordEmpty");
  const wrap = document.getElementById("ordTableWrap");
  const body = document.getElementById("ordBody");
  body.innerHTML = "";

  if (list.length === 0) {
    empty.classList.remove("hidden");
    wrap.classList.add("hidden");
    return;
  }
  empty.classList.add("hidden");
  wrap.classList.remove("hidden");

  for (const o of list) {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${o.id}</td>
      <td>${o.status}</td>
      <td>${o.vehiclePlate ?? ""}</td>
      <td>${new Date(o.createdAt).toLocaleString("es-CO")}</td>
      <td>
        <button type="button" class="btn btn-accent btn-int-ord" data-id="${o.id}">INTERVENIR</button>
      </td>
    `;
    body.appendChild(tr);
  }

  body.querySelectorAll(".btn-int-ord").forEach(btn => {
    btn.addEventListener("click", () => openIntervenir(Number(btn.dataset.id)));
  });
}

function openIntervenir(id) {
  interveneOrderId = id;
  const o = ordenesCacheSuper.find(x => x.id === id);
  if(!o) return;
  document.getElementById("vistaOrdenes").classList.add("hidden");
  document.getElementById("vistaIntervenir").classList.remove("hidden");
  document.getElementById("tituloIntervenir").textContent = `INTERVENIR ORDEN #${o.id}`;
  document.getElementById("intEstado").value = o.status;
  document.getElementById("intMotivo").value = "";
}

document.getElementById("btnVolverOrdenes").addEventListener("click", () => {
  document.getElementById("vistaIntervenir").classList.add("hidden");
  document.getElementById("vistaOrdenes").classList.remove("hidden");
});

document.getElementById("btnIntervenir").addEventListener("click", async () => {
  if(!interveneOrderId) return;
  const motivo = document.getElementById("intMotivo").value.trim();
  const st = document.getElementById("intEstado").value;
  if(!motivo) {
    showToast("El motivo es obligatorio", "error");
    return;
  }
  try {
    await OrderAPI.intervene(interveneOrderId, { reason: motivo, status: st });
    showToast("Orden intervenida correctamente", "success");
    document.getElementById("vistaIntervenir").classList.add("hidden");
    document.getElementById("vistaOrdenes").classList.remove("hidden");
    interveneOrderId = null;
    await loadOrdenes();
  } catch(e) {
    showToast(e.message || "Error al intervenir orden", "error");
  }
});

loadUsers();

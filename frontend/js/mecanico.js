import { OrderAPI, getOrEmpty } from "./api.js";
import { getSession, requireAuth, clearSession } from "./auth.js";

requireAuth("MECHANIC");

const session = getSession();
let ordenActiva = null;
let ordenesDisponibles = [];
let modalOrderId = null;
let pollTimer = null;

function showToast(msg, type = "info") {
  const t = document.createElement("div");
  t.className = `toast toast-${type}`;
  t.textContent = msg;
  document.body.appendChild(t);
  setTimeout(() => t.remove(), 3500);
}

function formatDate(iso) {
  return new Date(iso).toLocaleString("es-CO");
}

function formatMoney(n) {
  return new Intl.NumberFormat("es-CO", { style: "currency", currency: "COP" }).format(Number(n));
}

function showCola() {
  if (pollTimer) {
    clearInterval(pollTimer);
    pollTimer = null;
  }
  document.getElementById("vistaCola").classList.remove("hidden");
  document.getElementById("vistaActualizar").classList.add("hidden");
}

function showActualizar() {
  document.getElementById("vistaCola").classList.add("hidden");
  document.getElementById("vistaActualizar").classList.remove("hidden");
  if (pollTimer) {
    clearInterval(pollTimer);
    pollTimer = null;
  }
  pollTimer = setInterval(() => { syncActiveOrderIfClosed(); }, 6000);
}

async function syncActiveOrderIfClosed() {
  const activeId = ordenActiva?.id ?? localStorage.getItem("activeOrderId");
  if (!activeId) return;
  try {
    const o = await OrderAPI.getById(Number(activeId));
    if (o.status === "COMPLETED") {
      if (pollTimer) {
        clearInterval(pollTimer);
        pollTimer = null;
      }
      localStorage.removeItem("activeOrderId");
      ordenActiva = null;
      showToast("La orden fue cerrada. Vuelves a la cola.", "info");
      await renderVistaCola();
      showCola();
    }
  } catch {
    localStorage.removeItem("activeOrderId");
    ordenActiva = null;
    if (pollTimer) {
      clearInterval(pollTimer);
      pollTimer = null;
    }
    await renderVistaCola();
    showCola();
  }
}

async function renderVistaCola() {
  ordenesDisponibles = await getOrEmpty(() => OrderAPI.getAvailable());
  const filtro = document.getElementById("filtroPlaca").value.trim().toUpperCase();
  const lista = filtro
    ? ordenesDisponibles.filter(o => (o.vehiclePlate || "").toUpperCase().includes(filtro))
    : ordenesDisponibles;

  const empty = document.getElementById("colaEmpty");
  const wrap = document.getElementById("colaTableWrap");
  const body = document.getElementById("colaBody");
  body.innerHTML = "";

  if (lista.length === 0) {
    empty.classList.remove("hidden");
    wrap.classList.add("hidden");
    return;
  }
  empty.classList.add("hidden");
  wrap.classList.remove("hidden");

  for (const o of lista) {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${o.id}</td>
      <td>${formatDate(o.createdAt)}</td>
      <td>${o.vehiclePlate ?? ""}</td>
      <td>${o.vehicleModel ?? ""}</td>
      <td><button type="button" class="btn btn-accent btn-ver-tomar" data-id="${o.id}">VER Y TOMAR</button></td>
    `;
    body.appendChild(tr);
  }

  body.querySelectorAll(".btn-ver-tomar").forEach(btn => {
    btn.addEventListener("click", () => openModal(Number(btn.dataset.id)));
  });
}

function openModal(orderId) {
  const o = ordenesDisponibles.find(x => x.id === orderId);
  if (!o) return;
  modalOrderId = orderId;
  document.getElementById("modalOrderId").textContent = String(o.id);
  document.getElementById("modalContent").innerHTML = `
    <div class="modal-detail"><strong>Cliente ID</strong> ${o.customerId}</div>
    <div class="modal-detail"><strong>Placa</strong> ${o.vehiclePlate ?? ""}</div>
    <div class="modal-detail"><strong>Modelo</strong> ${o.vehicleModel ?? ""}</div>
    <div class="modal-detail"><strong>Descripción</strong> ${o.description ?? "—"}</div>
    <div class="modal-detail"><strong>Total</strong> ${formatMoney(o.total)}</div>
    <div class="modal-detail"><strong>Estado</strong> ${o.status}</div>
  `;
  document.getElementById("modalOrden").classList.remove("hidden");
}

function closeModal() {
  modalOrderId = null;
  document.getElementById("modalOrden").classList.add("hidden");
}

async function tomarOrden(orderId) {
  const orden = await OrderAPI.takeOrder(orderId, parseInt(session.id, 10));
  localStorage.setItem("activeOrderId", String(orden.id));
  ordenActiva = orden;
  closeModal();
  renderVistaActualizar(orden);
}

function renderVistaActualizar(orden) {
  showActualizar();
  document.getElementById("tituloActualizar").textContent = `ACTUALIZAR ORDEN #${orden.id}`;
  document.getElementById("gridReadonly").innerHTML = `
    <div><span>CLIENTE</span>${orden.customerId}</div>
    <div><span>PLACA</span>${orden.vehiclePlate ?? ""}</div>
    <div><span>MODELO</span>${orden.vehicleModel ?? ""}</div>
  `;
  document.getElementById("descripcion").value = orden.description ?? "";
  document.getElementById("total").value = orden.total != null ? String(orden.total) : "";
}

async function actualizar() {
  await OrderAPI.update(ordenActiva.id, {
    description: document.getElementById("descripcion").value,
    total: parseFloat(document.getElementById("total").value),
  });
  showToast("Orden actualizada", "success");
  const refreshed = await OrderAPI.getById(ordenActiva.id);
  ordenActiva = refreshed;
  if (refreshed.status === "COMPLETED") {
    localStorage.removeItem("activeOrderId");
    ordenActiva = null;
    await renderVistaCola();
    showCola();
  }
}

async function resolverOrdenActivaSinStorage() {
  const all = await getOrEmpty(() => OrderAPI.getAll());
  const mid = parseInt(session.id, 10);
  const found = all.find(
    o => Number(o.mechanicId) === mid && o.status === "IN_PROGRESS"
  );
  if (found) {
    localStorage.setItem("activeOrderId", String(found.id));
    return OrderAPI.getById(found.id);
  }
  return null;
}

async function init() {
  document.getElementById("welcomeName").textContent = session.name.toUpperCase();

  document.getElementById("btnLogout").addEventListener("click", () => {
    clearSession();
    window.location.href = "index.html";
  });

  document.getElementById("filtroPlaca").addEventListener("input", () => renderVistaCola());

  document.getElementById("modalCerrar").addEventListener("click", closeModal);
  document.getElementById("modalOrden").addEventListener("click", e => {
    if (e.target.id === "modalOrden") closeModal();
  });
  document.getElementById("modalTomar").addEventListener("click", async () => {
    if (modalOrderId == null) return;
    try {
      await tomarOrden(modalOrderId);
    } catch (e) {
      showToast(e.message || "Error al tomar la orden", "error");
    }
  });

  document.getElementById("btnActualizar").addEventListener("click", async () => {
    try {
      await actualizar();
    } catch (e) {
      showToast(e.message || "Error al actualizar", "error");
    }
  });

  window.addEventListener("focus", () => {
    if (!document.getElementById("vistaActualizar").classList.contains("hidden")) {
      syncActiveOrderIfClosed();
    }
  });

  const tieneOrden = await OrderAPI.hasActiveOrder(parseInt(session.id, 10));
  if (tieneOrden === true) {
    const activeId = localStorage.getItem("activeOrderId");
    if (activeId) {
      try {
        ordenActiva = await OrderAPI.getById(parseInt(activeId, 10));
        if (ordenActiva.status === "COMPLETED") {
          localStorage.removeItem("activeOrderId");
          ordenActiva = null;
          await renderVistaCola();
          showCola();
          return;
        }
        renderVistaActualizar(ordenActiva);
      } catch {
        const recovered = await resolverOrdenActivaSinStorage();
        if (recovered) {
          if (recovered.status === "COMPLETED") {
            localStorage.removeItem("activeOrderId");
            ordenActiva = null;
            await renderVistaCola();
            showCola();
            return;
          }
          ordenActiva = recovered;
          renderVistaActualizar(ordenActiva);
        } else {
          await renderVistaCola();
          showCola();
        }
      }
    } else {
      const recovered = await resolverOrdenActivaSinStorage();
      if (recovered) {
        if (recovered.status === "COMPLETED") {
          localStorage.removeItem("activeOrderId");
          ordenActiva = null;
          await renderVistaCola();
          showCola();
          return;
        }
        ordenActiva = recovered;
        renderVistaActualizar(ordenActiva);
      } else {
        await renderVistaCola();
        showCola();
      }
    }
  } else {
    localStorage.removeItem("activeOrderId");
    await renderVistaCola();
    showCola();
  }
}

init();

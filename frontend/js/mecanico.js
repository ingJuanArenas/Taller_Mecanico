import { OrderAPI, ProcedureAPI, getOrEmpty } from "./api.js";
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
    if (o.status === "COMPLETED" || o.mechanicFinished === true) {
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
  document.getElementById("procNombre").value = "";
  document.getElementById("procDesc").value = "";
  document.getElementById("procTiempo").value = "";
  loadAndRenderProcedures(orden.id);
}

async function loadAndRenderProcedures(orderId) {
  const list = await getOrEmpty(() => ProcedureAPI.getByOrderId(orderId));
  const cnt = document.getElementById("listaProcedimientos");
  cnt.innerHTML = "";
  if (list.length === 0) {
    cnt.innerHTML = "<div class='empty-msg text-sm'>No hay procedimientos.</div>";
    return;
  }
  list.forEach(p => {
    const d = document.createElement("div");
    d.style.cssText = "border-bottom:1px solid #eee; padding-bottom:8px; margin-bottom:8px;";
    d.innerHTML = `
      <div style="display:flex; justify-content:space-between; align-items:flex-start;">
        <div>
          <strong>${p.name}</strong> <span style="font-size:0.85em; color:#666;">(${p.executionTime} min)</span>
          <div style="font-size:0.9em; margin-top:4px;">${p.description || "—"}</div>
        </div>
        <button type="button" class="btn btn-danger btn-sm btn-del-proc" data-id="${p.id}" style="padding:4px 8px; font-size:12px;">X</button>
      </div>`;
    cnt.appendChild(d);
  });
  cnt.querySelectorAll(".btn-del-proc").forEach(b => {
    b.addEventListener("click", async () => {
      if(!confirm("¿Eliminar procedimiento?")) return;
      try {
        await ProcedureAPI.delete(Number(b.dataset.id));
        showToast("Procedimiento eliminado", "success");
        await loadAndRenderProcedures(orderId);
      } catch(e) {
        showToast(e.message, "error");
      }
    });
  });
}

async function terminarTrabajo() {
  try {
    const pList = await getOrEmpty(() => ProcedureAPI.getByOrderId(ordenActiva.id));
    if(pList.length === 0) {
      showToast("La orden debe tener al menos un procedimiento", "error");
      return;
    }
    await OrderAPI.release(ordenActiva.id, parseInt(session.id, 10));
    showToast("Trabajo finalizado", "success");
    localStorage.removeItem("activeOrderId");
    ordenActiva = null;
    await renderVistaCola();
    showCola();
  } catch(e) {
    showToast(e.message || "Error al finalizar el trabajo", "error");
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

  document.getElementById("btnCrearProc").addEventListener("click", async () => {
    if(!ordenActiva) return;
    const name = document.getElementById("procNombre").value.trim();
    const desc = document.getElementById("procDesc").value.trim();
    const time = parseInt(document.getElementById("procTiempo").value, 10);
    if(!name || !time || time < 1) {
      showToast("Faltan datos o tiempo es inválido", "error");
      return;
    }
    try {
      await ProcedureAPI.create({
        orderId: ordenActiva.id,
        name: name,
        description: desc,
        executionTime: time
      });
      showToast("Procedimiento añadido", "success");
      document.getElementById("procNombre").value = "";
      document.getElementById("procDesc").value = "";
      document.getElementById("procTiempo").value = "";
      await loadAndRenderProcedures(ordenActiva.id);
    } catch(e) {
      showToast(e.message || "Error al crear procedimiento", "error");
    }
  });

  document.getElementById("btnTerminarTrabajo").addEventListener("click", async () => {
    try {
      await terminarTrabajo();
    } catch (e) {
      showToast(e.message || "Error al terminar trabajo", "error");
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

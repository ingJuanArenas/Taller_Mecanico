import { OrderAPI, getOrEmpty } from "./api.js";
import { getSession, requireAuth, clearSession } from "./auth.js";

requireAuth("SERVICE_ADVISOR");

const session = getSession();
let ordenesCache = [];
let finalizeOrderId = null;
let finalizeDescription = "";
let debounceTimer = null;

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

function showDashboard() {
  document.getElementById("vistaDashboard").classList.remove("hidden");
  document.getElementById("vistaCrear").classList.add("hidden");
  document.getElementById("vistaFinalizar").classList.add("hidden");
}

function showCrear() {
  document.getElementById("vistaDashboard").classList.add("hidden");
  document.getElementById("vistaCrear").classList.remove("hidden");
  document.getElementById("vistaFinalizar").classList.add("hidden");
}

function showFinalizar() {
  document.getElementById("vistaDashboard").classList.add("hidden");
  document.getElementById("vistaCrear").classList.add("hidden");
  document.getElementById("vistaFinalizar").classList.remove("hidden");
}

async function loadDashboard() {
  const raw = document.getElementById("buscarCedula").value.trim();
  let list;
  if (raw) {
    try {
      list = await OrderAPI.getByCustomer(parseInt(raw, 10));
    } catch (e) {
      if (e.message.startsWith("404")) list = [];
      else throw e;
    }
  } else {
    list = await getOrEmpty(() => OrderAPI.getAll());
  }
  list = list.filter(o => o.status !== "COMPLETED");
  ordenesCache = list;
  renderTable(list);
  showDashboard();
}

function renderTable(list) {
  const empty = document.getElementById("dashEmpty");
  const wrap = document.getElementById("dashTableWrap");
  const body = document.getElementById("dashBody");
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
    let actions = "";
    if (o.status === "CREATED") {
      actions = `
        <button type="button" class="btn btn-accent btn-edit" data-id="${o.id}" title="Editar">✏️</button>
        <button type="button" class="btn btn-danger btn-del" data-id="${o.id}" title="Eliminar">🗑️</button>
      `;
    } else if (o.status === "IN_PROGRESS") {
      actions = `<button type="button" class="btn btn-accent btn-fin" data-id="${o.id}" title="Finalizar">✏️</button>`;
    }
    tr.innerHTML = `
      <td>${o.id}</td>
      <td>${formatDate(o.createdAt)}</td>
      <td>${o.vehiclePlate ?? ""}</td>
      <td class="actions-cell">${actions}</td>
    `;
    body.appendChild(tr);
  }

  body.querySelectorAll(".btn-edit").forEach(btn => {
    btn.addEventListener("click", () => openEditCreated(Number(btn.dataset.id)));
  });
  body.querySelectorAll(".btn-del").forEach(btn => {
    btn.addEventListener("click", () => eliminarOrden(Number(btn.dataset.id)));
  });
  body.querySelectorAll(".btn-fin").forEach(btn => {
    btn.addEventListener("click", () => openFinalizar(Number(btn.dataset.id)));
  });
}

function openEditCreated(orderId) {
  const o = ordenesCache.find(x => x.id === orderId);
  if (!o || o.status !== "CREATED") return;
  document.getElementById("cCedula").value = String(o.customerId);
  document.getElementById("cPlaca").value = o.vehiclePlate ?? "";
  document.getElementById("cModelo").value = o.vehicleModel ?? "";
  document.getElementById("cMotivo").value = o.description ?? "";
  showCrear();
  document.getElementById("btnCrearOrden").dataset.editId = String(orderId);
  document.querySelector("#vistaCrear h2").textContent = "Editar orden";
}

async function eliminarOrden(id) {
  if (!confirm("¿Eliminar esta orden?")) return;
  try {
    await OrderAPI.delete(id);
    showToast("Orden eliminada", "success");
    await loadDashboard();
  } catch (e) {
    showToast(e.message || "Error al eliminar", "error");
  }
}

function openFinalizar(orderId) {
  const o = ordenesCache.find(x => x.id === orderId);
  if (!o) return;
  finalizeOrderId = orderId;
  finalizeDescription = o.description ?? "";
  document.getElementById("tituloFinalizar").textContent = `FINALIZAR ORDEN #${o.id}`;
  document.getElementById("finalReadonly").innerHTML = `
    <div class="readonly-block"><span>CLIENTE</span>${o.customerId}</div>
    <div class="readonly-block"><span>PLACA</span>${o.vehiclePlate ?? ""}</div>
    <div class="readonly-block"><span>MODELO</span>${o.vehicleModel ?? ""}</div>
    <div class="readonly-block"><span>TRABAJO / DESCRIPCIÓN</span>${finalizeDescription || "—"}</div>
  `;
  document.getElementById("fTotal").value = o.total != null ? String(o.total) : "";
  showFinalizar();
}

async function crearOrden() {
  const editId = document.getElementById("btnCrearOrden").dataset.editId;
  const payload = {
    customerId:   parseInt(document.getElementById("cCedula").value, 10),
    vehiclePlate: document.getElementById("cPlaca").value.trim(),
    vehicleModel: document.getElementById("cModelo").value.trim(),
    description:  document.getElementById("cMotivo").value.trim() || null,
    total:        1,
    advisorId:    parseInt(session.id, 10),
  };
  try {
    if (editId) {
      await OrderAPI.delete(parseInt(editId, 10));
      await OrderAPI.create(payload);
      delete document.getElementById("btnCrearOrden").dataset.editId;
      document.querySelector("#vistaCrear h2").textContent = "Nueva orden";
      showToast("Orden actualizada", "success");
    } else {
      await OrderAPI.create(payload);
      showToast("Orden creada", "success");
    }
    document.getElementById("cCedula").value = "";
    document.getElementById("cPlaca").value = "";
    document.getElementById("cModelo").value = "";
    document.getElementById("cMotivo").value = "";
    await loadDashboard();
  } catch (e) {
    showToast(e.message || "Error al guardar", "error");
  }
}

async function finalizarOrden() {
  const total = parseFloat(document.getElementById("fTotal").value);
  if (!total || total < 1) {
    showToast("Total inválido", "error");
    return;
  }
  try {
    await OrderAPI.update(finalizeOrderId, {
      description: finalizeDescription,
      total,
      complete: true,
    });
    showToast("Orden finalizada", "success");
    finalizeOrderId = null;
    await loadDashboard();
  } catch (e) {
    showToast(e.message || "Error al finalizar", "error");
  }
}

document.getElementById("welcomeName").textContent = session.name.toUpperCase();

document.getElementById("btnLogout").addEventListener("click", () => {
  clearSession();
  window.location.href = "index.html";
});

document.getElementById("btnNuevaOrden").addEventListener("click", () => {
  delete document.getElementById("btnCrearOrden").dataset.editId;
  document.querySelector("#vistaCrear h2").textContent = "Nueva orden";
  document.getElementById("cCedula").value = "";
  document.getElementById("cPlaca").value = "";
  document.getElementById("cModelo").value = "";
  document.getElementById("cMotivo").value = "";
  showCrear();
});

document.getElementById("btnVolverCrear").addEventListener("click", () => loadDashboard());

document.getElementById("btnCrearOrden").addEventListener("click", () => crearOrden());

document.getElementById("btnVolverFinalizar").addEventListener("click", () => loadDashboard());

document.getElementById("btnFinalizarOrden").addEventListener("click", () => finalizarOrden());

document.getElementById("buscarCedula").addEventListener("input", () => {
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(() => loadDashboard(), 350);
});

loadDashboard();

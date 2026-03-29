const USERS_BASE  = "http://localhost:8080/users";
const ORDERS_BASE = "http://localhost:8080/api/orders";
const JSON_HEADERS = { "Content-Type": "application/json" };

async function handleResponse(res) {
  if (!res.ok) {
    const err = await res.text().catch(() => res.statusText);
    throw new Error(`${res.status}: ${err}`);
  }
  if (res.status === 204) return null;
  return res.json();
}

export const UserAPI = {
  login:  (code) => fetch(`${USERS_BASE}/find?code=${encodeURIComponent(code)}`).then(handleResponse),
  getAll: ()     => fetch(USERS_BASE).then(handleResponse),
  create: (data) => fetch(USERS_BASE, { method: "POST", headers: JSON_HEADERS, body: JSON.stringify(data) }).then(handleResponse),
  update: (id, data) => fetch(`${USERS_BASE}/${id}`, { method: "PUT", headers: JSON_HEADERS, body: JSON.stringify(data) }).then(handleResponse),
  delete: (id)   => fetch(`${USERS_BASE}/${id}`, { method: "DELETE" }).then(handleResponse),
};

export const OrderAPI = {
  getAll:        ()     => fetch(ORDERS_BASE).then(handleResponse),
  getById:       (id)   => fetch(`${ORDERS_BASE}/${id}`).then(handleResponse),
  getByCustomer: (cid)  => fetch(`${ORDERS_BASE}/search/customer?customer=${cid}`).then(handleResponse),
  getAvailable:  ()     => fetch(`${ORDERS_BASE}/available`).then(handleResponse),
  hasActiveOrder:(mid)  => fetch(`${ORDERS_BASE}/active/${mid}`).then(handleResponse),
  create:  (data)       => fetch(ORDERS_BASE, { method: "POST", headers: JSON_HEADERS, body: JSON.stringify(data) }).then(handleResponse),
  update:  (id, data)   => fetch(`${ORDERS_BASE}/update/${id}`, { method: "PUT", headers: JSON_HEADERS, body: JSON.stringify(data) }).then(handleResponse),
  takeOrder: (oid, mid) => fetch(`${ORDERS_BASE}/take/${oid}?mechanicId=${mid}`, { method: "PUT" }).then(handleResponse),
  delete:  (id)         => fetch(`${ORDERS_BASE}/${id}`, { method: "DELETE" }).then(handleResponse),
};

export async function getOrEmpty(apiFn) {
  try { return await apiFn(); }
  catch (err) {
    if (err.message.startsWith("404")) return [];
    throw err;
  }
}

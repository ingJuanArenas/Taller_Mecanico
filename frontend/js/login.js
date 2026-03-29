import { UserAPI } from "./api.js";
import { saveSession } from "./auth.js";

document.getElementById("btnLogin").addEventListener("click", async () => {
  const code = document.getElementById("accessCode").value.trim();
  if (!code) return;
  document.getElementById("loginError").classList.add("hidden");
  try {
    const user = await UserAPI.login(code);
    saveSession(user);
    const routes = {
      "SUPER_ADMIN":     "superadmin.html",
      "SERVICE_ADVISOR": "facturador.html",
      "MECHANIC":        "mecanico.html",
    };
    window.location.href = routes[user.role] ?? "index.html";
  } catch {
    document.getElementById("loginError").classList.remove("hidden");
  }
});
document.getElementById("accessCode").addEventListener("keydown", e => {
  if (e.key === "Enter") document.getElementById("btnLogin").click();
});

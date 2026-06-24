const API_HEADERS = {
    "Content-Type": "application/json"
};

const TOKEN_KEY = "polideportivoToken";
const USER_KEY = "polideportivoUser";
const THEME_KEY = "polideportivoTheme";

function applyStoredTheme() {
    const theme = localStorage.getItem(THEME_KEY) || "light";
    document.documentElement.dataset.theme = theme;
}

function setupThemeToggle() {
    const header = document.querySelector(".home-nav");
    const logoutButton = document.querySelector("[data-logout]");
    let button = document.querySelector("[data-theme-toggle]");

    function sync() {
        const isDark = document.documentElement.dataset.theme === "dark";
        button.textContent = isDark ? "Modo claro" : "Modo oscuro";
        button.setAttribute("aria-pressed", String(isDark));
    }

    if (!button) {
        if (!header || !logoutButton) {
            return;
        }

        button = document.createElement("button");
        button.className = "theme-toggle";
        button.type = "button";
        button.dataset.themeToggle = "true";
        header.insertBefore(button, logoutButton);
    }

    button.addEventListener("click", () => {
        const nextTheme = document.documentElement.dataset.theme === "dark" ? "light" : "dark";
        localStorage.setItem(THEME_KEY, nextTheme);
        document.documentElement.dataset.theme = nextTheme;
        sync();
    });

    sync();
}

applyStoredTheme();

function getErrorMessage(payload, fallback) {
    if (payload && typeof payload === "object") {
        return payload.message || payload.error || fallback;
    }

    if (typeof payload === "string" && payload.trim()) {
        return payload;
    }

    return fallback;
}

async function readResponse(response) {
    const contentType = response.headers.get("content-type") || "";

    if (contentType.includes("application/json")) {
        return response.json();
    }

    return response.text();
}

async function apiFetch(url, options = {}) {
    const headers = {
        ...API_HEADERS,
        ...(options.headers || {})
    };
    const token = getStoredToken();

    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    const response = await fetch(url, {
        ...options,
        headers
    });
    const payload = await readResponse(response);

    if (!response.ok) {
        throw new Error(getErrorMessage(payload, "No se pudo completar la operación."));
    }

    return payload;
}

function decodeJwtPayload(token) {
    try {
        const payload = token.split(".")[1];
        const normalized = payload.replace(/-/g, "+").replace(/_/g, "/");
        const decoded = atob(normalized);
        return JSON.parse(decoded);
    } catch (error) {
        return {};
    }
}

function getStoredToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function isTokenActive(token) {
    if (!token) {
        return false;
    }

    const payload = decodeJwtPayload(token);
    const expiresAt = Number(payload.exp || 0) * 1000;

    return Boolean(expiresAt && expiresAt > Date.now());
}

function requireActiveSession() {
    if (document.body.dataset.requiresAuth !== "true") {
        return true;
    }

    if (!isTokenActive(getStoredToken())) {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        window.location.replace("/login");
        return false;
    }

    return true;
}

function setMessage(element, type, text) {
    if (!element) {
        return;
    }

    element.textContent = text;
    element.className = `form-message ${type} is-visible`;
}

function setSubmitting(form, isSubmitting) {
    const button = form.querySelector("button[type='submit']");

    if (!button) {
        return;
    }

    if (!button.dataset.defaultText) {
        button.dataset.defaultText = button.textContent;
    }

    button.disabled = isSubmitting;
    button.textContent = isSubmitting ? "Procesando..." : button.dataset.defaultText;
}

function formatMoney(value) {
    const amount = Number(value || 0);
    return amount.toLocaleString("es-CR", {
        style: "currency",
        currency: "CRC",
        maximumFractionDigits: 0
    });
}

function formatTime(time) {
    return String(time || "").slice(0, 5);
}

function timeToHour(time) {
    const [hours] = formatTime(time).split(":");
    return Number(hours || 0);
}

function hourToTime(hour) {
    return `${String(hour).padStart(2, "0")}:00`;
}

function todayIsoDate() {
    return new Date().toISOString().slice(0, 10);
}

function setupTimeRangePair(form, startKey, endKey, bounds = {}) {
    const startRange = form.querySelector(`[data-time-range="${startKey}"]`);
    const endRange = form.querySelector(`[data-time-range="${endKey}"]`);
    const startOutput = form.querySelector(`[data-time-output="${startKey}"]`);
    const endOutput = form.querySelector(`[data-time-output="${endKey}"]`);
    const startHidden = form.elements.horaInicio;
    const endHidden = form.elements.horaFin;

    if (!startRange || !endRange || !startHidden || !endHidden) {
        return;
    }

    if (bounds.minStart != null) {
        startRange.min = bounds.minStart;
        startRange.value = Math.max(Number(startRange.value), bounds.minStart);
        endRange.min = bounds.minStart + 1;
        endRange.value = Math.max(Number(endRange.value), bounds.minStart + 1);
    }

    if (bounds.maxEnd != null) {
        endRange.max = bounds.maxEnd;
        endRange.value = Math.min(Number(endRange.value), bounds.maxEnd);
        startRange.max = bounds.maxEnd - 1;
        startRange.value = Math.min(Number(startRange.value), bounds.maxEnd - 1);
    }

    function sync(changedStart = false) {
        let start = Number(startRange.value);
        let end = Number(endRange.value);

        if (start >= end) {
            if (changedStart) {
                end = Math.min(start + 1, Number(endRange.max));
            } else {
                start = Math.max(end - 1, Number(startRange.min));
            }
        }

        startRange.value = start;
        endRange.value = end;
        startHidden.value = hourToTime(start);
        endHidden.value = hourToTime(end);

        if (startOutput) {
            startOutput.textContent = hourToTime(start);
        }

        if (endOutput) {
            endOutput.textContent = hourToTime(end);
        }
    }

    startRange.addEventListener("input", () => sync(true));
    endRange.addEventListener("input", () => sync(false));
    sync(true);
}

function setupAdminTimeRangePair(form, startName, endName) {
    const startRange = form.querySelector(`[data-admin-time-range="${startName}"]`);
    const endRange = form.querySelector(`[data-admin-time-range="${endName}"]`);
    const startOutput = form.querySelector(`[data-admin-time-output="${startName}"]`);
    const endOutput = form.querySelector(`[data-admin-time-output="${endName}"]`);
    const startHidden = form.elements[startName];
    const endHidden = form.elements[endName];

    if (!startRange || !endRange || !startHidden || !endHidden) {
        return;
    }

    function sync(changedStart = false) {
        let start = Number(startRange.value);
        let end = Number(endRange.value);

        if (start >= end) {
            if (changedStart) {
                end = Math.min(start + 1, Number(endRange.max));
            } else {
                start = Math.max(end - 1, Number(startRange.min));
            }
        }

        startRange.value = start;
        endRange.value = end;
        startHidden.value = hourToTime(start);
        endHidden.value = hourToTime(end);

        if (startOutput) {
            startOutput.textContent = hourToTime(start);
        }

        if (endOutput) {
            endOutput.textContent = hourToTime(end);
        }
    }

    startRange.addEventListener("input", () => sync(true));
    endRange.addEventListener("input", () => sync(false));
    sync(true);
}

function setupAdminSwitches(form) {
    form.querySelectorAll("[data-admin-switch]").forEach((input) => {
        const state = input.closest(".admin-switch")?.querySelector("[data-admin-switch-state]");

        function sync() {
            if (state) {
                state.textContent = input.checked ? "Activo" : "Inactivo";
            }
        }

        input.addEventListener("change", sync);
        sync();
    });
}

function ensureAppDialog() {
    let dialog = document.querySelector("[data-app-dialog]");

    if (dialog) {
        return dialog;
    }

    dialog = document.createElement("div");
    dialog.className = "app-dialog-backdrop";
    dialog.dataset.appDialog = "true";
    dialog.innerHTML = `
        <div class="app-dialog" role="dialog" aria-modal="true">
            <p class="eyebrow" data-dialog-kicker></p>
            <h2 data-dialog-title></h2>
            <p data-dialog-message></p>
            <div class="dialog-actions" data-dialog-actions></div>
        </div>
    `;
    document.body.appendChild(dialog);

    return dialog;
}

function showAppNotice({ title, message, actionText = "Entendido" }) {
    const dialog = ensureAppDialog();
    const actions = dialog.querySelector("[data-dialog-actions]");

    dialog.querySelector("[data-dialog-kicker]").textContent = "Aviso";
    dialog.querySelector("[data-dialog-title]").textContent = title;
    dialog.querySelector("[data-dialog-message]").textContent = message;
    actions.innerHTML = `<button class="primary-button dialog-button" type="button">${actionText}</button>`;
    dialog.classList.add("is-open");

    return new Promise((resolve) => {
        actions.querySelector("button").addEventListener("click", () => {
            dialog.classList.remove("is-open");
            resolve();
        }, { once: true });
    });
}

function showAppConfirm({ title, message, confirmText = "Eliminar", cancelText = "Cancelar" }) {
    const dialog = ensureAppDialog();
    const actions = dialog.querySelector("[data-dialog-actions]");

    dialog.querySelector("[data-dialog-kicker]").textContent = "Confirmación";
    dialog.querySelector("[data-dialog-title]").textContent = title;
    dialog.querySelector("[data-dialog-message]").textContent = message;
    actions.innerHTML = `
        <button class="secondary-inline-button dialog-cancel" type="button">${cancelText}</button>
        <button class="danger-button dialog-confirm" type="button">${confirmText}</button>
    `;
    dialog.classList.add("is-open");

    return new Promise((resolve) => {
        actions.querySelector(".dialog-cancel").addEventListener("click", () => {
            dialog.classList.remove("is-open");
            resolve(false);
        }, { once: true });

        actions.querySelector(".dialog-confirm").addEventListener("click", () => {
            dialog.classList.remove("is-open");
            resolve(true);
        }, { once: true });
    });
}

function askPaymentMethod() {
    const dialog = ensureAppDialog();
    const actions = dialog.querySelector("[data-dialog-actions]");
    const methods = ["EFECTIVO", "SINPE", "TARJETA"];

    dialog.querySelector("[data-dialog-kicker]").textContent = "Pago de reserva";
    dialog.querySelector("[data-dialog-title]").textContent = "Selecciona el método de pago";
    dialog.querySelector("[data-dialog-message]").textContent = "Elige cómo quieres registrar el pago de esta reserva.";
    actions.innerHTML = methods
        .map((method) => `<button class="payment-choice" type="button" data-payment-method="${method}">${method}</button>`)
        .join("");
    dialog.classList.add("is-open");

    return new Promise((resolve) => {
        actions.querySelectorAll("[data-payment-method]").forEach((button) => {
            button.addEventListener("click", () => {
                dialog.classList.remove("is-open");
                resolve(button.dataset.paymentMethod);
            }, { once: true });
        });
    });
}

function getEffectiveReservationState(reserva) {
    if (reserva.estado !== "CANCELADA") {
        const end = new Date(`${reserva.fechaReserva}T${formatTime(reserva.horaFin)}`);

        if (!Number.isNaN(end.getTime()) && end < new Date()) {
            return "FINALIZADA";
        }
    }

    return reserva.estado;
}

function getSpaceIdFromUrl() {
    return new URLSearchParams(window.location.search).get("espacio");
}

function getSpaceDisciplineId(space) {
    return space?.disciplina?.idDisciplina;
}

function getQueryParam(name) {
    return new URLSearchParams(window.location.search).get(name);
}

function getDisciplineVisual(nombre = "") {
    const text = nombre.toLowerCase();

    if (text.includes("futbol")) return "futbol";
    if (text.includes("tenis") || text.includes("ping pong")) return "raqueta";
    if (text.includes("baloncesto")) return "baloncesto";
    if (text.includes("voleibol")) return "voleibol";
    if (text.includes("natacion")) return "natacion";
    if (text.includes("atletismo")) return "atletismo";
    if (text.includes("golf")) return "golf";
    if (text.includes("boxeo")) return "boxeo";
    if (text.includes("marcial") || text.includes("karate") || text.includes("judo") || text.includes("taekwondo")) return "artes-marciales";
    if (text.includes("escalada")) return "escalada";
    if (text.includes("beisbol")) return "beisbol";

    return "general";
}

function getStoredUser() {
    return JSON.parse(localStorage.getItem(USER_KEY) || "{}");
}

function isAdminUser() {
    return getStoredUser().rol === "ADMINISTRADOR" || document.body.classList.contains("is-admin");
}

async function login(nombreUsuario, contrasena) {
    const response = await fetch("/auth/login", {
        method: "POST",
        headers: API_HEADERS,
        body: JSON.stringify({ nombreUsuario, contrasena })
    });

    const payload = await readResponse(response);

    if (!response.ok) {
        throw new Error(getErrorMessage(payload, "No se pudo iniciar sesión."));
    }

    return payload;
}

async function registerUser(user) {
    const response = await fetch("/usuarios", {
        method: "POST",
        headers: API_HEADERS,
        body: JSON.stringify(user)
    });

    const payload = await readResponse(response);

    if (!response.ok) {
        throw new Error(getErrorMessage(payload, "No se pudo crear la cuenta."));
    }

    return payload;
}

async function loadDisciplines() {
    return apiFetch("/disciplinas");
}

async function loadSpaces(filters = {}) {
    const hasDiscipline = Boolean(filters.disciplina);
    const hasTimeRange = Boolean(filters.horaInicio && filters.horaFin);
    let url = "/espacios/activos";

    if (hasDiscipline && hasTimeRange) {
        url = `/espacios/disciplina/${filters.disciplina}/horario?horaInicio=${filters.horaInicio}&horaFin=${filters.horaFin}`;
    } else if (hasDiscipline) {
        url = `/espacios/disciplina/${filters.disciplina}/activos`;
    } else if (hasTimeRange) {
        url = `/espacios/horario?horaInicio=${filters.horaInicio}&horaFin=${filters.horaFin}`;
    } else if (filters.capacidad) {
        url = `/espacios/capacidad?capacidad=${filters.capacidad}`;
    }

    const spaces = await apiFetch(url);
    const capacidad = Number(filters.capacidad || 0);
    const presupuesto = Number(filters.presupuesto || 0);

    return spaces.filter((space) => {
        const matchesCapacity = !capacidad || Number(space.capacidad || 0) >= capacidad;
        const matchesBudget = !presupuesto || Number(space.precioHora || 0) <= presupuesto;
        return matchesCapacity && matchesBudget;
    });
}

async function loadActiveSpacesByDiscipline(idDisciplina) {
    return apiFetch(`/espacios/disciplina/${idDisciplina}/activos`);
}

async function loadSpace(idEspacio) {
    return apiFetch(`/espacios/${idEspacio}`);
}

async function loadEquipmentsByDiscipline(idDisciplina) {
    if (!idDisciplina) {
        return [];
    }

    return apiFetch(`/equipamientos/disciplina/${idDisciplina}/activos`);
}

async function createMyReservation(reservation) {
    return apiFetch("/reservas/mis-reservas", {
        method: "POST",
        body: JSON.stringify(reservation)
    });
}

async function updateMyReservation(idReserva, reservation) {
    return apiFetch(`/reservas/mis-reservas/${idReserva}`, {
        method: "PATCH",
        body: JSON.stringify(reservation)
    });
}

async function cancelMyReservation(idReserva) {
    return apiFetch(`/reservas/mis-reservas/${idReserva}/cancelar`, {
        method: "PATCH",
        body: JSON.stringify({})
    });
}

async function loadMyProfile() {
    return apiFetch("/usuarios/me");
}

async function updateMyProfile(profile) {
    return apiFetch("/usuarios/me", {
        method: "PATCH",
        body: JSON.stringify(profile)
    });
}

async function loadUsers() {
    return apiFetch("/usuarios");
}

async function searchUsersByName(nombre) {
    return apiFetch(`/usuarios/buscar?nombre=${encodeURIComponent(nombre)}`);
}

async function searchUserByEmail(correo) {
    return apiFetch(`/usuarios/correo/${encodeURIComponent(correo)}`);
}

async function updateUser(idUsuario, user) {
    return apiFetch(`/usuarios/${idUsuario}`, {
        method: "PATCH",
        body: JSON.stringify(user)
    });
}

async function deleteUser(idUsuario) {
    return apiFetch(`/usuarios/${idUsuario}`, {
        method: "DELETE"
    });
}

async function adminList(endpoint) {
    return apiFetch(endpoint);
}

async function adminCreate(endpoint, payload) {
    return apiFetch(endpoint, {
        method: "POST",
        body: JSON.stringify(payload)
    });
}

async function adminPatch(endpoint, payload) {
    return apiFetch(endpoint, {
        method: "PATCH",
        body: JSON.stringify(payload)
    });
}

async function adminPut(endpoint, payload) {
    return apiFetch(endpoint, {
        method: "PUT",
        body: JSON.stringify(payload)
    });
}

async function adminDelete(endpoint) {
    return apiFetch(endpoint, {
        method: "DELETE"
    });
}

function buildReservaEstadoPayload(item, estado) {
    return {
        usuario: { idUsuario: item.usuario?.idUsuario },
        espacio: { idEspacio: item.espacio?.idEspacio },
        cantidadPersonas: Number(item.cantidadPersonas),
        fechaReserva: item.fechaReserva,
        horaInicio: formatTime(item.horaInicio),
        horaFin: formatTime(item.horaFin),
        estado,
        montoTotal: Number(item.montoTotal)
    };
}

function updateReservaEstado(item, estado) {
    return adminPatch(`/reservas/${item.idReserva}`, buildReservaEstadoPayload(item, estado));
}

function compareById(idKey) {
    return (a, b) => Number(a[idKey] || 0) - Number(b[idKey] || 0);
}

function compareReservaDateTime(a, b) {
    const first = new Date(`${a.fechaReserva}T${formatTime(a.horaInicio)}`).getTime();
    const second = new Date(`${b.fechaReserva}T${formatTime(b.horaInicio)}`).getTime();
    return first - second;
}

async function payMyReservation(idReserva, metodoPago) {
    return apiFetch("/pagos/mis-pagos", {
        method: "POST",
        body: JSON.stringify({
            reserva: { idReserva },
            metodoPago,
            estadoPago: "APROBADO"
        })
    });
}

async function loadMyReservations() {
    return apiFetch("/reservas/mis-reservas");
}

async function loadReservationEquipment(idReserva) {
    return apiFetch(`/reserva-equipamientos/mis-reservas/${idReserva}`);
}

async function addEquipmentToReservation(idReserva, idEquipamiento) {
    return apiFetch("/reserva-equipamientos/mis-equipamientos", {
        method: "POST",
        body: JSON.stringify({
            reserva: { idReserva },
            equipamiento: { idEquipamiento },
            cantidad: 1
        })
    });
}

async function removeReservationEquipment(idReservaEquipamiento) {
    return apiFetch(`/reserva-equipamientos/mis-equipamientos/${idReservaEquipamiento}`, {
        method: "DELETE"
    });
}

function setupLoginForm() {
    const form = document.querySelector("[data-login-form]");
    const message = document.querySelector("[data-form-message]");

    if (!form) {
        return;
    }

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        setSubmitting(form, true);

        const formData = new FormData(form);
        const nombreUsuario = String(formData.get("nombreUsuario") || "").trim();
        const contrasena = String(formData.get("contrasena") || "");

        try {
            const auth = await login(nombreUsuario, contrasena);
            const user = decodeJwtPayload(auth.token);

            localStorage.setItem(TOKEN_KEY, auth.token);
            localStorage.setItem(USER_KEY, JSON.stringify(user));
            setMessage(message, "success", "Sesión iniciada correctamente.");
            form.reset();

            window.setTimeout(() => {
                window.location.href = user.rol === "ADMINISTRADOR" ? "/admin" : "/inicio";
            }, 700);
        } catch (error) {
            setMessage(message, "error", error.message);
        } finally {
            setSubmitting(form, false);
        }
    });
}

function setupRegisterForm() {
    const form = document.querySelector("[data-register-form]");
    const message = document.querySelector("[data-form-message]");

    if (!form) {
        return;
    }

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        setSubmitting(form, true);

        const formData = new FormData(form);
        const contrasena = String(formData.get("contrasena") || "");
        const confirmarContrasena = String(formData.get("confirmarContrasena") || "");

        if (contrasena !== confirmarContrasena) {
            setMessage(message, "error", "Las contraseñas no coinciden.");
            setSubmitting(form, false);
            return;
        }

        const user = {
            nombre: String(formData.get("nombre") || "").trim(),
            primerApellido: String(formData.get("primerApellido") || "").trim(),
            segundoApellido: String(formData.get("segundoApellido") || "").trim(),
            correo: String(formData.get("correo") || "").trim(),
            nombreUsuario: String(formData.get("nombreUsuario") || "").trim(),
            contrasena
        };

        try {
            await registerUser(user);
            setMessage(message, "success", "Cuenta creada correctamente. Redirigiendo al inicio de sesión...");
            form.reset();

            window.setTimeout(() => {
                window.location.href = "/login";
            }, 1200);
        } catch (error) {
            setMessage(message, "error", error.message);
        } finally {
            setSubmitting(form, false);
        }
    });
}

function setupAuthenticatedPage() {
    const userName = document.querySelector("[data-user-name]");
    const roleBadge = document.querySelector("[data-role-badge]");
    const logoutButton = document.querySelector("[data-logout]");
    const storedUser = JSON.parse(localStorage.getItem(USER_KEY) || "{}");
    const isAdmin = storedUser.rol === "ADMINISTRADOR";

    if (userName && storedUser.nombreUsuario) {
        userName.textContent = storedUser.nombreUsuario;
    }

    if (userName && document.body.dataset.requiresAuth === "true") {
        loadMyProfile()
            .then((profile) => {
                userName.textContent = profile.nombre || storedUser.nombreUsuario || "Usuario";
            })
            .catch(() => {
                userName.textContent = storedUser.nombreUsuario || "Usuario";
            });
    }

    if (isAdmin) {
        document.body.classList.add("is-admin");

        if (roleBadge) {
            roleBadge.hidden = false;
        }

        document.querySelectorAll("[data-admin-only]").forEach((element) => {
            element.hidden = false;
        });
    }

    if (document.body.dataset.requiresAdmin === "true" && !isAdmin) {
        window.location.replace("/inicio");
        return;
    }

    if (logoutButton) {
        logoutButton.addEventListener("click", () => {
            localStorage.removeItem(TOKEN_KEY);
            localStorage.removeItem(USER_KEY);
            window.location.href = "/login";
        });
    }
}

const adminModules = {
    disciplinas: {
        title: "Disciplinas",
        endpoint: "/disciplinas",
        id: "idDisciplina",
        fields: [
            { name: "nombre", label: "Nombre", type: "text", required: true },
            { name: "descripcion", label: "Descripción", type: "textarea" }
        ],
        toPayload: (data) => ({
            nombre: data.nombre,
            descripcion: data.descripcion
        }),
        sort: compareById("idDisciplina"),
        summary: (item) => `${item.nombre} · ${item.descripcion || "Sin descripción"}`
    },
    espacios: {
        title: "Espacios",
        endpoint: "/espacios",
        id: "idEspacio",
        fields: [
            { name: "nombre", label: "Nombre", type: "text", required: true },
            { name: "descripcion", label: "Descripción", type: "textarea" },
            { name: "idDisciplina", label: "Disciplina", type: "select", source: "disciplinas", required: true },
            { name: "capacidad", label: "Capacidad", type: "number", required: true },
            { name: "horaApertura", label: "Hora apertura", type: "range-time", defaultHour: 6, required: true },
            { name: "horaCierre", label: "Hora cierre", type: "range-time", defaultHour: 22, required: true },
            { name: "precioHora", label: "Precio por hora", type: "number", step: "0.01", required: true },
            { name: "activo", label: "Activo", type: "checkbox" }
        ],
        timePairs: [["horaApertura", "horaCierre"]],
        toPayload: (data) => ({
            nombre: data.nombre,
            descripcion: data.descripcion,
            disciplina: { idDisciplina: Number(data.idDisciplina) },
            capacidad: Number(data.capacidad),
            horaApertura: data.horaApertura,
            horaCierre: data.horaCierre,
            precioHora: Number(data.precioHora),
            activo: data.activo === "on"
        }),
        fromItem: (item) => ({
            ...item,
            idDisciplina: item.disciplina?.idDisciplina
        }),
        sort: compareById("idEspacio"),
        summary: (item) => `${item.nombre} · ${item.disciplina?.nombre || "Sin disciplina"} · ${item.activo ? "Activo" : "Inactivo"}`,
        className: (item) => item.activo ? "is-active-space" : "is-inactive-space",
        toggleActive: async (item, checked) => adminPatch(`/espacios/${item.idEspacio}/${checked ? "activar" : "desactivar"}`, {})
    },
    reservas: {
        title: "Reservas",
        endpoint: "/reservas",
        id: "idReserva",
        fields: [
            { name: "idUsuario", label: "Usuario", type: "select", source: "usuarios", required: true },
            { name: "idEspacio", label: "Espacio", type: "select", source: "espacios", required: true },
            { name: "cantidadPersonas", label: "Personas", type: "number", required: true },
            { name: "fechaReserva", label: "Fecha", type: "date", required: true },
            { name: "horaInicio", label: "Hora inicio", type: "range-time", defaultHour: 8, required: true },
            { name: "horaFin", label: "Hora fin", type: "range-time", defaultHour: 10, required: true },
            { name: "estado", label: "Estado", type: "select", options: ["PENDIENTE", "CONFIRMADA", "CANCELADA", "FINALIZADA"], required: true },
            { name: "montoTotal", label: "Monto total", type: "number", step: "0.01", required: true }
        ],
        timePairs: [["horaInicio", "horaFin"]],
        toPayload: (data) => ({
            usuario: { idUsuario: Number(data.idUsuario) },
            espacio: { idEspacio: Number(data.idEspacio) },
            cantidadPersonas: Number(data.cantidadPersonas),
            fechaReserva: data.fechaReserva,
            horaInicio: data.horaInicio,
            horaFin: data.horaFin,
            estado: data.estado,
            montoTotal: Number(data.montoTotal)
        }),
        fromItem: (item) => ({
            ...item,
            idUsuario: item.usuario?.idUsuario,
            idEspacio: item.espacio?.idEspacio
        }),
        createPayload: (data) => ({
            usuario: { idUsuario: Number(data.idUsuario) },
            espacio: { idEspacio: Number(data.idEspacio) },
            cantidadPersonas: Number(data.cantidadPersonas),
            fechaReserva: data.fechaReserva,
            horaInicio: data.horaInicio,
            horaFin: data.horaFin,
            estado: data.estado || "PENDIENTE",
            montoTotal: data.montoTotal ? Number(data.montoTotal) : null
        }),
        sort: (a, b) => compareReservaDateTime(b, a),
        summary: (item) => `${item.espacio?.nombre || "Espacio"} · ${item.usuario?.nombreUsuario || "Usuario"} · ${item.fechaReserva} · ${item.estado}`,
        className: (item) => `is-reservation-${String(item.estado || "").toLowerCase()}`,
        extraActions: [
            { label: "Pendiente", action: (item) => updateReservaEstado(item, "PENDIENTE") },
            { label: "Confirmar", action: (item) => updateReservaEstado(item, "CONFIRMADA") },
            { label: "Cancelar", action: (item) => updateReservaEstado(item, "CANCELADA") },
            { label: "Finalizar", action: (item) => updateReservaEstado(item, "FINALIZADA") }
        ]
    },
    equipamientos: {
        title: "Equipamiento",
        endpoint: "/equipamientos",
        id: "idEquipamiento",
        fields: [
            { name: "nombre", label: "Nombre", type: "text", required: true },
            { name: "cantidadTotal", label: "Cantidad total", type: "number", required: true },
            { name: "idDisciplina", label: "Disciplina", type: "select", source: "disciplinas", required: true },
            { name: "activo", label: "Activo", type: "checkbox" }
        ],
        toPayload: (data) => ({
            nombre: data.nombre,
            cantidadTotal: Number(data.cantidadTotal),
            disciplina: { idDisciplina: Number(data.idDisciplina) },
            activo: data.activo === "on"
        }),
        fromItem: (item) => ({
            ...item,
            idDisciplina: item.disciplina?.idDisciplina
        }),
        summary: (item) => `${item.nombre} · ${item.disciplina?.nombre || "Sin disciplina"} · ${item.cantidadTotal || 0} unidades`,
        extraActions: [
            { label: "Activar", action: (item) => adminPatch(`/equipamientos/${item.idEquipamiento}/activar`, {}) },
            { label: "Desactivar", action: (item) => adminPatch(`/equipamientos/${item.idEquipamiento}/desactivar`, {}) }
        ]
    },
    usuarios: {
        title: "Usuarios",
        endpoint: "/usuarios",
        id: "idUsuario",
        fields: [
            { name: "nombre", label: "Nombre", type: "text", required: true },
            { name: "primerApellido", label: "Primer apellido", type: "text" },
            { name: "segundoApellido", label: "Segundo apellido", type: "text" },
            { name: "correo", label: "Correo", type: "email", required: true },
            { name: "nombreUsuario", label: "Usuario", type: "text", required: true, createOnly: true },
            { name: "contrasena", label: "Contraseña", type: "password", createOnly: true },
            { name: "rol", label: "Rol", type: "select", options: ["CLIENTE", "ADMINISTRADOR"], editOnly: true }
        ],
        toPayload: (data, mode) => ({
            nombre: data.nombre,
            primerApellido: data.primerApellido,
            segundoApellido: data.segundoApellido,
            correo: data.correo,
            ...(mode === "create" ? {
                nombreUsuario: data.nombreUsuario,
                contrasena: data.contrasena || "123456"
            } : {
                rol: data.rol
            })
        }),
        sort: (a, b) => {
            const roleOrder = { ADMINISTRADOR: 0, CLIENTE: 1 };
            const firstRole = roleOrder[a.rol] ?? 2;
            const secondRole = roleOrder[b.rol] ?? 2;

            if (firstRole !== secondRole) {
                return firstRole - secondRole;
            }

            return Number(a.idUsuario || 0) - Number(b.idUsuario || 0);
        },
        summary: (item) => `${item.nombre || ""} ${item.primerApellido || ""} · ${item.nombreUsuario} · ${item.rol}`
    },
    pagos: {
        title: "Pagos",
        endpoint: "/pagos",
        id: "idPago",
        fields: [
            { name: "idReserva", label: "Reserva", type: "select", source: "reservas", required: true },
            { name: "metodoPago", label: "Método", type: "select", options: ["EFECTIVO", "SINPE", "TARJETA"], required: true },
            { name: "estadoPago", label: "Estado", type: "select", options: ["PENDIENTE", "APROBADO"], required: true }
        ],
        toPayload: (data) => ({
            reserva: { idReserva: Number(data.idReserva) },
            metodoPago: data.metodoPago,
            estadoPago: data.estadoPago
        }),
        fromItem: (item) => ({
            ...item,
            idReserva: item.reserva?.idReserva
        }),
        update: (id, data) => adminPatch(`/pagos/${id}/estado?estadoPago=${encodeURIComponent(data.estadoPago)}`, {}),
        sort: compareById("idPago"),
        summary: (item) => `Reserva #${item.reserva?.idReserva || "-"} · ${item.metodoPago} · ${item.estadoPago} · ${formatMoney(item.monto)}`,
        className: (item) => `is-payment-${String(item.estadoPago || "").toLowerCase()}`
    }
};

function fillProfileForm(form, profile) {
    form.nombre.value = profile.nombre || "";
    form.primerApellido.value = profile.primerApellido || "";
    form.segundoApellido.value = profile.segundoApellido || "";
    form.correo.value = profile.correo || "";
    form.nombreUsuario.value = profile.nombreUsuario || "";
}

function renderUsers(users) {
    const list = document.querySelector("[data-users-list]");

    if (!list) {
        return;
    }

    if (!users.length) {
        list.innerHTML = "<p class=\"empty-state\">No hay usuarios para mostrar.</p>";
        return;
    }

    list.innerHTML = users.map((user) => `
        <article class="user-card" data-user-id="${user.idUsuario}">
            <div>
                <span class="status-pill">${user.rol || "CLIENTE"}</span>
                <h2>${user.nombre || ""} ${user.primerApellido || ""}</h2>
                <p>${user.correo || "Sin correo"} · ${user.nombreUsuario || "Sin usuario"}</p>
            </div>
            <div class="user-actions">
                <button class="secondary-inline-button" type="button" data-edit-user>Editar</button>
                <button class="danger-button" type="button" data-delete-user>Eliminar</button>
            </div>
        </article>
    `).join("");
}

function setupUserActions(message) {
    document.querySelectorAll("[data-user-id]").forEach((card) => {
        const idUsuario = Number(card.dataset.userId);

        card.querySelector("[data-edit-user]")?.addEventListener("click", async () => {
            const nombre = window.prompt("Nombre");
            const correo = window.prompt("Correo");

            if (!nombre && !correo) {
                return;
            }

            try {
                await updateUser(idUsuario, {
                    ...(nombre ? { nombre } : {}),
                    ...(correo ? { correo } : {})
                });
                setMessage(message, "success", "Usuario actualizado.");
                const users = await loadUsers();
                renderUsers(users);
                setupUserActions(message);
            } catch (error) {
                setMessage(message, "error", error.message);
            }
        });

        card.querySelector("[data-delete-user]")?.addEventListener("click", async () => {
            if (!window.confirm("¿Eliminar este usuario?")) {
                return;
            }

            try {
                await deleteUser(idUsuario);
                setMessage(message, "success", "Usuario eliminado.");
                card.remove();
            } catch (error) {
                setMessage(message, "error", error.message);
            }
        });
    });
}

async function setupProfilePage() {
    const form = document.querySelector("[data-profile-form]");
    const message = document.querySelector("[data-profile-message]");
    const adminPanel = document.querySelector("[data-admin-users]");
    const usersMessage = document.querySelector("[data-users-message]");
    const searchForm = document.querySelector("[data-user-search-form]");
    const loadUsersButton = document.querySelector("[data-load-users]");

    if (!form) {
        return;
    }

    let profile;

    try {
        profile = await loadMyProfile();
        fillProfileForm(form, profile);
    } catch (error) {
        setMessage(message, "error", error.message);
        return;
    }

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        setSubmitting(form, true);
        const data = Object.fromEntries(new FormData(form).entries());

        try {
            const updated = await updateMyProfile({
                nombre: data.nombre,
                primerApellido: data.primerApellido,
                segundoApellido: data.segundoApellido,
                correo: data.correo
            });
            fillProfileForm(form, updated);
            setMessage(message, "success", "Perfil actualizado correctamente.");
        } catch (error) {
            setMessage(message, "error", error.message);
        } finally {
            setSubmitting(form, false);
        }
    });

    if (profile.rol !== "ADMINISTRADOR" || !adminPanel) {
        return;
    }

    adminPanel.hidden = false;

    async function loadAndRenderUsers(loader) {
        try {
            const result = await loader();
            const users = Array.isArray(result) ? result : [result];
            renderUsers(users);
            setupUserActions(usersMessage);
        } catch (error) {
            setMessage(usersMessage, "error", error.message);
        }
    }

    loadUsersButton?.addEventListener("click", () => loadAndRenderUsers(loadUsers));
    searchForm?.addEventListener("submit", (event) => {
        event.preventDefault();
        const data = Object.fromEntries(new FormData(searchForm).entries());

        if (data.correo) {
            loadAndRenderUsers(() => searchUserByEmail(data.correo));
            return;
        }

        if (data.nombre) {
            loadAndRenderUsers(() => searchUsersByName(data.nombre));
            return;
        }

        loadAndRenderUsers(loadUsers);
    });

    await loadAndRenderUsers(loadUsers);
}

async function getAdminSources() {
    const [disciplinas, espacios, usuarios, reservas] = await Promise.all([
        adminList("/disciplinas"),
        adminList("/espacios"),
        adminList("/usuarios"),
        adminList("/reservas")
    ]);

    return { disciplinas, espacios, usuarios, reservas };
}

function getSourceOptions(sourceName, sources) {
    const items = sources[sourceName] || [];

    return items.map((item) => {
        if (sourceName === "disciplinas") {
            return { value: item.idDisciplina, label: item.nombre };
        }

        if (sourceName === "espacios") {
            return { value: item.idEspacio, label: item.nombre };
        }

        if (sourceName === "usuarios") {
            return { value: item.idUsuario, label: `${item.nombreUsuario} (${item.nombre || "Sin nombre"})` };
        }

        if (sourceName === "reservas") {
            return { value: item.idReserva, label: `#${item.idReserva} · ${item.espacio?.nombre || "Espacio"} · ${item.estado}` };
        }

        return [];
    });
}

function renderAdminField(field, value, sources, mode) {
    if (field.createOnly && mode !== "create") {
        return "";
    }

    if (field.editOnly && mode !== "edit") {
        return "";
    }

    const required = field.required ? "required" : "";
    const currentValue = value ?? "";

    if (field.type === "textarea") {
        return `
            <label class="field">
                <span>${field.label}</span>
                <textarea name="${field.name}" ${required}>${currentValue}</textarea>
            </label>
        `;
    }

    if (field.type === "select") {
        const options = field.source
            ? getSourceOptions(field.source, sources)
            : field.options.map((option) => ({ value: option, label: option }));

        return `
            <label class="field">
                <span>${field.label}</span>
                <select name="${field.name}" ${required}>
                    <option value="">Seleccionar</option>
                    ${options.map((option) => `<option value="${option.value}" ${String(option.value) === String(currentValue) ? "selected" : ""}>${option.label}</option>`).join("")}
                </select>
            </label>
        `;
    }

    if (field.type === "checkbox") {
        return `
            <label class="admin-switch">
                <input name="${field.name}" type="checkbox" data-admin-switch ${currentValue ? "checked" : ""}>
                <span class="admin-switch-track" aria-hidden="true"></span>
                <span>
                    ${field.label}
                    <strong data-admin-switch-state></strong>
                </span>
            </label>
        `;
    }

    if (field.type === "range-time") {
        const hour = currentValue ? timeToHour(currentValue) : Number(field.defaultHour || 8);

        return `
            <label class="field time-slider-field admin-time-field">
                <span>${field.label} <strong data-admin-time-output="${field.name}">${hourToTime(hour)}</strong></span>
                <input type="range" min="0" max="23" step="1" value="${hour}" data-admin-time-range="${field.name}">
                <input name="${field.name}" type="hidden" value="${hourToTime(hour)}" ${required}>
            </label>
        `;
    }

    return `
        <label class="field">
            <span>${field.label}</span>
            <input name="${field.name}" type="${field.type}" value="${currentValue}" step="${field.step || ""}" ${required}>
        </label>
    `;
}

function renderAdminForm(moduleKey, module, sources, item = null) {
    const form = document.querySelector("[data-admin-form]");
    const mode = item ? "edit" : "create";
    const values = item && module.fromItem ? module.fromItem(item) : (item || {});

    form.dataset.module = moduleKey;
    form.dataset.mode = mode;
    form.dataset.recordId = item ? item[module.id] : "";
    form.innerHTML = `
        <h2>${mode === "create" ? "Crear" : "Editar"} ${module.title.toLowerCase()}</h2>
        <div class="admin-form-grid">
            ${module.fields.map((field) => renderAdminField(field, values[field.name], sources, mode)).join("")}
        </div>
        <div class="admin-form-actions">
            <button class="primary-button compact-button" type="submit">${mode === "create" ? "Crear" : "Guardar"}</button>
            ${mode === "edit" ? "<button class=\"secondary-inline-button\" type=\"button\" data-admin-new>Nuevo</button>" : ""}
        </div>
    `;

    (module.timePairs || []).forEach(([startName, endName]) => setupAdminTimeRangePair(form, startName, endName));
    setupAdminSwitches(form);
}

function renderAdminList(moduleKey, module, items) {
    const list = document.querySelector("[data-admin-list]");

    if (!items.length) {
        list.innerHTML = "<p class=\"empty-state\">No hay registros para mostrar.</p>";
        return;
    }

    list.innerHTML = items.map((item) => `
        <article class="admin-record ${module.className ? module.className(item) : ""}" data-admin-record="${item[module.id]}">
            <div>
                <span class="status-pill">#${item[module.id]}</span>
                <h3>${module.summary(item)}</h3>
            </div>
            <div class="admin-record-actions">
                <button class="secondary-inline-button" type="button" data-admin-edit>Editar</button>
                ${module.toggleActive ? `
                    <label class="admin-switch admin-list-switch">
                        <input type="checkbox" data-admin-active-toggle ${item.activo ? "checked" : ""}>
                        <span class="admin-switch-track" aria-hidden="true"></span>
                        <span><strong>${item.activo ? "Activo" : "Inactivo"}</strong></span>
                    </label>
                ` : ""}
                ${(module.extraActions || []).map((action, index) => `<button class="secondary-inline-button" type="button" data-admin-extra="${index}">${action.label}</button>`).join("")}
                <button class="danger-button" type="button" data-admin-delete>Eliminar</button>
            </div>
        </article>
    `).join("");
}

async function loadAdminModule(moduleKey, sources) {
    const module = adminModules[moduleKey];
    const title = document.querySelector("[data-admin-title]");
    const message = document.querySelector("[data-admin-message]");
    const tabs = document.querySelectorAll("[data-admin-module]");

    tabs.forEach((tab) => tab.classList.toggle("is-active", tab.dataset.adminModule === moduleKey));
    title.textContent = module.title;

    try {
        const items = await adminList(module.endpoint);
        const sortedItems = module.sort ? [...items].sort(module.sort) : items;

        renderAdminForm(moduleKey, module, sources);
        renderAdminList(moduleKey, module, sortedItems);
        setupAdminRecordActions(moduleKey, module, sortedItems, sources);
    } catch (error) {
        setMessage(message, "error", error.message);
    }
}

function setupAdminRecordActions(moduleKey, module, items, sources) {
    const message = document.querySelector("[data-admin-message]");

    document.querySelectorAll("[data-admin-record]").forEach((record) => {
        const id = Number(record.dataset.adminRecord);
        const item = items.find((current) => Number(current[module.id]) === id);

        record.querySelector("[data-admin-edit]")?.addEventListener("click", () => {
            renderAdminForm(moduleKey, module, sources, item);
        });

        record.querySelector("[data-admin-delete]")?.addEventListener("click", async () => {
            const confirmed = await showAppConfirm({
                title: "Eliminar registro",
                message: "Esta acción no se puede deshacer. ¿Quieres eliminar este registro?",
                confirmText: "Eliminar"
            });

            if (!confirmed) {
                return;
            }

            try {
                await adminDelete(`${module.endpoint}/${id}`);
                setMessage(message, "success", "Registro eliminado.");
                await loadAdminModule(moduleKey, sources);
            } catch (error) {
                setMessage(message, "error", error.message);
            }
        });

        record.querySelector("[data-admin-active-toggle]")?.addEventListener("change", async (event) => {
            try {
                await module.toggleActive(item, event.target.checked);
                setMessage(message, "success", event.target.checked ? "Espacio activado." : "Espacio desactivado.");
                await loadAdminModule(moduleKey, sources);
            } catch (error) {
                event.target.checked = !event.target.checked;
                setMessage(message, "error", error.message);
            }
        });

        record.querySelectorAll("[data-admin-extra]").forEach((button) => {
            button.addEventListener("click", async () => {
                const action = module.extraActions[Number(button.dataset.adminExtra)];

                try {
                    await action.action(item);
                    setMessage(message, "success", "Acción aplicada.");
                    await loadAdminModule(moduleKey, sources);
                } catch (error) {
                    setMessage(message, "error", error.message);
                }
            });
        });
    });
}

async function setupAdminPage() {
    const form = document.querySelector("[data-admin-form]");
    const refreshButton = document.querySelector("[data-admin-refresh]");
    const message = document.querySelector("[data-admin-message]");

    if (!form) {
        return;
    }

    if (!isAdminUser()) {
        window.location.replace("/inicio");
        return;
    }

    const sources = await getAdminSources();
    let currentModule = document.body.dataset.adminModulePage || "disciplinas";

    if (!adminModules[currentModule]) {
        currentModule = "disciplinas";
    }

    document.querySelectorAll("[data-admin-module]").forEach((button) => {
        button.addEventListener("click", async () => {
            currentModule = button.dataset.adminModule;
            await loadAdminModule(currentModule, sources);
        });
    });

    refreshButton?.addEventListener("click", async () => {
        await loadAdminModule(currentModule, sources);
    });

    form.addEventListener("click", (event) => {
        if (event.target.matches("[data-admin-new]")) {
            renderAdminForm(currentModule, adminModules[currentModule], sources);
        }
    });

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        const module = adminModules[currentModule];
        const data = Object.fromEntries(new FormData(form).entries());
        const mode = form.dataset.mode;
        const id = form.dataset.recordId;

        try {
            if (mode === "create") {
                const payload = module.createPayload ? module.createPayload(data) : module.toPayload(data, "create");
                await adminCreate(module.endpoint, payload);
                setMessage(message, "success", "Registro creado.");
            } else if (module.update) {
                await module.update(id, data);
                setMessage(message, "success", "Registro actualizado.");
            } else {
                const payload = module.toPayload(data, "edit");
                await adminPatch(`${module.endpoint}/${id}`, payload);
                setMessage(message, "success", "Registro actualizado.");
            }

            const nextSources = await getAdminSources();
            Object.assign(sources, nextSources);
            await loadAdminModule(currentModule, sources);
        } catch (error) {
            setMessage(message, "error", error.message);
        }
    });

    await loadAdminModule(currentModule, sources);
}

function renderSpaces(spaces) {
    const list = document.querySelector("[data-spaces-list]");

    if (!list) {
        return;
    }

    if (!spaces.length) {
        list.innerHTML = "<p class=\"empty-state\">No se encontraron espacios con esos filtros.</p>";
        return;
    }

    list.innerHTML = spaces.map((space) => `
        <article class="space-card">
            <div>
                <span class="space-discipline">${space.disciplina?.nombre || "Sin disciplina"}</span>
                <h2>${space.nombre}</h2>
                <p>${space.descripcion || "Espacio disponible para actividades deportivas."}</p>
            </div>
            <dl class="space-meta">
                <div><dt>Capacidad</dt><dd>${space.capacidad || 0} personas</dd></div>
                <div><dt>Horario</dt><dd>${formatTime(space.horaApertura)} - ${formatTime(space.horaCierre)}</dd></div>
                <div><dt>Precio</dt><dd>${formatMoney(space.precioHora)} / hora</dd></div>
            </dl>
            <a class="reserve-hover" href="/reservar?espacio=${space.idEspacio}">Reservar</a>
        </article>
    `).join("");
}

async function setupSpacesPage() {
    const form = document.querySelector("[data-spaces-filter]");
    const disciplineSelect = document.querySelector("[data-discipline-filter]");
    const message = document.querySelector("[data-page-message]");

    if (!form) {
        return;
    }

    setupTimeRangePair(form, "filterInicio", "filterFin");

    try {
        const disciplines = await loadDisciplines();
        const selectedDiscipline = getQueryParam("disciplina");

        disciplineSelect.innerHTML = "<option value=\"\">Todas</option>" + disciplines
            .map((discipline) => `<option value="${discipline.idDisciplina}">${discipline.nombre}</option>`)
            .join("");
        if (selectedDiscipline) {
            disciplineSelect.value = selectedDiscipline;
        }

        renderSpaces(await loadSpaces({ disciplina: selectedDiscipline || "" }));
    } catch (error) {
        setMessage(message, "error", error.message);
    }

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        const data = Object.fromEntries(new FormData(form).entries());

        try {
            renderSpaces(await loadSpaces(data));
        } catch (error) {
            setMessage(message, "error", error.message);
        }
    });
}

async function setupDisciplinesPage() {
    const list = document.querySelector("[data-disciplines-list]");
    const message = document.querySelector("[data-page-message]");

    if (!list) {
        return;
    }

    try {
        const disciplines = await loadDisciplines();
        const items = await Promise.all(disciplines.map(async (discipline) => {
            const spaces = await loadActiveSpacesByDiscipline(discipline.idDisciplina);
            return { discipline, spacesCount: spaces.length };
        }));

        list.innerHTML = items.map(({ discipline, spacesCount }) => `
            <a class="discipline-card discipline-${getDisciplineVisual(discipline.nombre)}" href="/espacios-disponibles?disciplina=${discipline.idDisciplina}">
                <span>${spacesCount} ${spacesCount === 1 ? "espacio" : "espacios"}</span>
                <h2>${discipline.nombre}</h2>
                <p>${discipline.descripcion || "Disciplina disponible en el polideportivo."}</p>
            </a>
        `).join("");
    } catch (error) {
        setMessage(message, "error", error.message);
    }
}

async function setupReservePage() {
    const form = document.querySelector("[data-reserve-form]");
    const title = document.querySelector("[data-reserve-title]");
    const description = document.querySelector("[data-reserve-description]");
    const message = document.querySelector("[data-page-message]");

    if (!form) {
        return;
    }

    const idEspacio = getSpaceIdFromUrl();

    if (!idEspacio) {
        setMessage(message, "error", "Selecciona un espacio antes de reservar.");
        return;
    }

    let space;

    try {
        space = await loadSpace(idEspacio);
        title.textContent = `Reservar ${space.nombre}`;
        description.textContent = `${space.disciplina?.nombre || "Espacio"} · ${formatTime(space.horaApertura)} a ${formatTime(space.horaCierre)} · ${formatMoney(space.precioHora)} por hora`;
        form.fechaReserva.min = todayIsoDate();

        setupTimeRangePair(form, "reserveInicio", "reserveFin", {
            minStart: timeToHour(space.horaApertura),
            maxEnd: timeToHour(space.horaCierre)
        });
    } catch (error) {
        setMessage(message, "error", error.message);
        return;
    }

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        setSubmitting(form, true);
        const data = Object.fromEntries(new FormData(form).entries());

        try {
            const reservation = await createMyReservation({
                espacio: { idEspacio: Number(idEspacio) },
                cantidadPersonas: Number(space.capacidad || 1),
                fechaReserva: data.fechaReserva,
                horaInicio: data.horaInicio,
                horaFin: data.horaFin
            });

            if (form.equipamiento.checked) {
                const equipments = await loadEquipmentsByDiscipline(getSpaceDisciplineId(space));

                if (equipments.length) {
                    await addEquipmentToReservation(reservation.idReserva, equipments[0].idEquipamiento);
                }
            }

            await showAppNotice({
                title: "Reserva creada",
                message: "Recuerda pagar tu reserva antes de la fecha de inicio de la actividad.",
                actionText: "Ver mis reservas"
            });
            window.location.href = "/mis-reservas";
        } catch (error) {
            setMessage(message, "error", error.message);
        } finally {
            setSubmitting(form, false);
        }
    });
}

function renderReservations(items) {
    const list = document.querySelector("[data-reservations-list]");

    if (!list) {
        return;
    }

    if (!items.length) {
        list.innerHTML = "<p class=\"empty-state\">Todavía no tienes reservas registradas.</p>";
        return;
    }

    list.innerHTML = items.map(({ reserva, equipamientos }) => {
        const estado = getEffectiveReservationState(reserva);
        const editable = estado === "PENDIENTE";
        const hasEquipment = equipamientos.length > 0;

        return `
            <article class="reservation-card" data-reservation-id="${reserva.idReserva}">
                <div class="reservation-main">
                    <span class="status-pill status-${estado.toLowerCase()}">${estado}</span>
                    <h2>${reserva.espacio?.nombre || "Reserva"}</h2>
                    <p>${reserva.fechaReserva} · ${formatTime(reserva.horaInicio)} - ${formatTime(reserva.horaFin)}</p>
                    <p>${reserva.cantidadPersonas || 0} personas · ${formatMoney(reserva.montoTotal)}</p>
                </div>
                <div class="reservation-controls">
                    <label class="check-row compact-check">
                        <input type="checkbox" data-equipment-toggle ${hasEquipment ? "checked" : ""} ${editable ? "" : "disabled"}>
                        <span>Equipamiento</span>
                    </label>
                    <button class="danger-button" type="button" data-cancel-reservation ${editable ? "" : "disabled"}>Cancelar</button>
                    <button class="success-button" type="button" data-pay-reservation ${editable ? "" : "disabled"}>Pagar</button>
                </div>
            </article>
        `;
    }).join("");
}

async function loadReservationsView() {
    const message = document.querySelector("[data-page-message]");
    const reservations = await loadMyReservations();
    const enriched = await Promise.all(reservations.map(async (reserva) => {
        try {
            return {
                reserva,
                equipamientos: await loadReservationEquipment(reserva.idReserva)
            };
        } catch (error) {
            return { reserva, equipamientos: [] };
        }
    }));
    const statePriority = {
        PENDIENTE: 0,
        CONFIRMADA: 1,
        CANCELADA: 2,
        FINALIZADA: 3
    };

    enriched.sort((a, b) => {
        const stateA = getEffectiveReservationState(a.reserva);
        const stateB = getEffectiveReservationState(b.reserva);
        const priorityDiff = (statePriority[stateA] ?? 9) - (statePriority[stateB] ?? 9);

        if (priorityDiff !== 0) {
            return priorityDiff;
        }

        const dateA = new Date(`${a.reserva.fechaReserva}T${formatTime(a.reserva.horaInicio)}`);
        const dateB = new Date(`${b.reserva.fechaReserva}T${formatTime(b.reserva.horaInicio)}`);

        return dateA - dateB;
    });

    renderReservations(enriched);
    setupReservationActions(enriched, message);
}

function setupReservationActions(enriched, message) {
    document.querySelectorAll("[data-reservation-id]").forEach((card) => {
        const idReserva = Number(card.dataset.reservationId);
        const current = enriched.find((item) => item.reserva.idReserva === idReserva);
        const toggle = card.querySelector("[data-equipment-toggle]");
        const cancelButton = card.querySelector("[data-cancel-reservation]");
        const payButton = card.querySelector("[data-pay-reservation]");

        toggle?.addEventListener("change", async () => {
            try {
                if (toggle.checked) {
                    const equipments = await loadEquipmentsByDiscipline(getSpaceDisciplineId(current.reserva.espacio));

                    if (!equipments.length) {
                        toggle.checked = false;
                        setMessage(message, "error", "No hay equipamiento activo para esta disciplina.");
                        return;
                    }

                    await addEquipmentToReservation(idReserva, equipments[0].idEquipamiento);
                } else {
                    await Promise.all(current.equipamientos.map((item) => removeReservationEquipment(item.idReservaEquipamiento)));
                }

                setMessage(message, "success", "Reserva actualizada correctamente.");
                await loadReservationsView();
            } catch (error) {
                setMessage(message, "error", error.message);
                await loadReservationsView();
            }
        });

        cancelButton?.addEventListener("click", async () => {
            try {
                await cancelMyReservation(idReserva);
                setMessage(message, "success", "Reserva cancelada correctamente.");
                await loadReservationsView();
            } catch (error) {
                setMessage(message, "error", error.message);
            }
        });

        payButton?.addEventListener("click", async () => {
            try {
                const metodoPago = await askPaymentMethod();
                await payMyReservation(idReserva, metodoPago);
                setMessage(message, "success", "Pago registrado. La reserva quedó confirmada.");
                await loadReservationsView();
            } catch (error) {
                setMessage(message, "error", error.message);
            }
        });
    });
}

async function setupReservationsPage() {
    if (!document.querySelector("[data-reservations-list]")) {
        return;
    }

    const message = document.querySelector("[data-page-message]");

    try {
        await loadReservationsView();
    } catch (error) {
        setMessage(message, "error", error.message);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    if (!requireActiveSession()) {
        return;
    }

    setupThemeToggle();
    setupLoginForm();
    setupRegisterForm();
    setupAuthenticatedPage();
    setupProfilePage();
    setupAdminPage();
    setupDisciplinesPage();
    setupSpacesPage();
    setupReservePage();
    setupReservationsPage();
});

/* ==========================================================
   1. CONFIGURACION
   Reemplaza los dos valores con los de tu registro en Entra ID.
   ========================================================== */

const msalConfig = {
  auth: {
    // Application (client) ID de tu registro de FRONTEND en Azure
    clientId: "a4c9aac0-2590-4e9b-baab-31ac26dc6e47",

    // Tu Tenant ID institucional
    authority: "https://login.microsoftonline.com/03bb15ff-2ba9-4e00-8ebd-10c0986f5c28",

    redirectUri: window.location.origin
  },
  cache: {
    cacheLocation: "sessionStorage"
  }
};

// Permisos que pedimos. User.Read permite leer el perfil del usuario en Graph.
// Si tuvieras tu propia API, aqui iria: ["api://TU_CLIENT_ID/tu.scope"]
const peticion = { scopes: ["api://e07cac17-7005-4a0b-87ff-45971efefb1f/.default"] };

// OJO con el nombre: la libreria del CDN ocupa la variable global "msal",
// asi que la instancia se llama distinto para no pisarla.
const msalInstance = new msal.PublicClientApplication(msalConfig);


/* ==========================================================
   2. REFERENCIAS AL DOM Y AYUDANTES
   ========================================================== */

const $ = (id) => document.getElementById(id);

// Muestra un mensaje de estado. tipo: "" | "ok" | "error"
function estado(mensaje, tipo = "") {
  $("estado").textContent = mensaje;
  $("estado").className = "estado " + tipo;
}

// Habilita o deshabilita los botones segun haya sesion activa
function actualizarBotones(haySesion) {
  $("btnLogin").disabled  = haySesion;
  $("btnToken").disabled  = !haySesion;
  $("btnGraph").disabled  = !haySesion;
  $("btnLogout").disabled = !haySesion;
}

// Decodifica el payload del JWT para MOSTRARLO en pantalla.
// Un JWT tiene 3 partes separadas por punto: header.payload.firma
// Solo leemos la del medio. Esto NO verifica nada, es solo inspeccion.
function verClaims(token) {
  const payload = token.split(".")[1];
  // Base64Url -> Base64 estandar antes de decodificar
  const json = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));
  return JSON.parse(json);
}


/* ==========================================================
   3. LOGIN
   ========================================================== */

async function login() {
  try {
    // loginPopup abre una ventana emergente.
    // Alternativa: loginRedirect(), que navega fuera de la pagina.
    const resultado = await msalInstance.loginPopup(peticion);

    // Guardamos la cuenta como activa para no tener que pasarla
    // en cada llamada posterior.
    msalInstance.setActiveAccount(resultado.account);

    estado("Sesion iniciada: " + resultado.account.username, "ok");
    actualizarBotones(true);
  } catch (error) {
    estado("Error en el login: " + error.message, "error");
  }
}


/* ==========================================================
   4. OBTENER EL ACCESS TOKEN
   Primero se intenta en silencio (desde la cache de MSAL).
   Si no se puede, se le vuelve a preguntar al usuario.
   ========================================================== */

async function obtenerToken() {
  try {
    const resultado = await msalInstance.acquireTokenSilent(peticion);
    return resultado.accessToken;
  } catch (error) {
    // Caso tipico: el token vencio y hace falta interaccion del usuario
    // (por ejemplo, volver a aceptar permisos o pasar MFA).
    const resultado = await msalInstance.acquireTokenPopup(peticion);
    return resultado.accessToken;
  }
}

// Handler del boton: pide el token y lo muestra en pantalla
async function mostrarToken() {
  try {
    const token = await obtenerToken();
    $("token").textContent = token;
    $("claims").textContent = JSON.stringify(verClaims(token), null, 2);
    estado("Token obtenido correctamente.", "ok");
  } catch (error) {
    estado("No se pudo obtener el token: " + error.message, "error");
  }
}


/* ==========================================================
   5. LLAMAR A UNA API CON EL TOKEN
   Este es el patron que usaras despues contra tu propio backend:
   solo cambia la URL.
   ========================================================== */

async function llamarGraph() {
  try {
    const token = await obtenerToken();

    const respuesta = await fetch("https://mxw86gy6c3.execute-api.us-east-1.amazonaws.com/api/catalog/services", {
      headers: {
        // El esquema "Bearer" seguido del token es el estandar OAuth 2.0
        Authorization: "Bearer " + token
      }
    });

    // fetch no lanza error con un 401 o 403: hay que revisar el status
    if (!respuesta.ok) {
      throw new Error("Graph respondio " + respuesta.status);
    }

    const datos = await respuesta.json();
    $("graph").textContent = JSON.stringify(datos, null, 2);
    estado("Graph respondio 200 OK.", "ok");
  } catch (error) {
    $("graph").textContent = "—";
    estado("Fallo la llamada: " + error.message, "error");
  }
}


/* ==========================================================
   6. LOGOUT
   ========================================================== */

async function logout() {
  // Limpia la cache local y cierra la sesion en Entra ID
  await msalInstance.logoutPopup();

  $("token").textContent  = "—";
  $("claims").textContent = "—";
  $("graph").textContent  = "—";
  estado("Sesion cerrada.");
  actualizarBotones(false);
}


/* ==========================================================
   7. ARRANQUE
   Al recargar la pagina, MSAL puede tener una cuenta guardada
   en la cache. Hay que recuperarla para no pedir login de nuevo.
   ========================================================== */

function iniciar() {
  const cuentas = msalInstance.getAllAccounts();

  if (cuentas.length > 0) {
    msalInstance.setActiveAccount(cuentas[0]);
    estado("Sesion recuperada: " + cuentas[0].username, "ok");
    actualizarBotones(true);
  } else {
    actualizarBotones(false);
  }

  $("btnLogin").addEventListener("click", login);
  $("btnToken").addEventListener("click", mostrarToken);
  $("btnGraph").addEventListener("click", llamarGraph);
  $("btnLogout").addEventListener("click", logout);
}

iniciar();
// Validaciones ligeras del formulario de preferencias (RF-01) en el cliente,
// complementarias a las validaciones @Valid del backend (PreferenciaDTO).
// El backend vuelve a validar siempre: esto solo evita un viaje al servidor.
document.addEventListener("DOMContentLoaded", function () {
  var formulario = document.querySelector("form[action$='/preferencias']");
  if (!formulario) {
    return;
  }

  var aviso = document.createElement("div");
  aviso.className = "alert alert-warning mt-3 d-none";
  aviso.setAttribute("role", "alert");
  aviso.textContent = "Selecciona al menos un tipo de turismo.";
  formulario.appendChild(aviso);

  formulario.addEventListener("submit", function (evento) {
    var marcados = formulario.querySelectorAll("input[name='idsTipoTurismo']:checked");
    if (marcados.length === 0) {
      evento.preventDefault();
      aviso.classList.remove("d-none");
      aviso.scrollIntoView({ behavior: "smooth", block: "center" });
      return;
    }
    aviso.classList.add("d-none");
  });
});

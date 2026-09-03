// Validaciones ligeras del formulario de preferencias (RF-01) en el cliente,
// complementarias a las validaciones @Valid del backend (PreferenciaDTO).
document.addEventListener("DOMContentLoaded", function () {
  var formulario = document.querySelector("form[action='/preferencias']");
  if (!formulario) {
    return;
  }

  formulario.addEventListener("submit", function (evento) {
    var checkboxes = formulario.querySelectorAll("input[name='idsTipoTurismo']:checked");
    if (checkboxes.length === 0) {
      evento.preventDefault();
      alert("Selecciona al menos un tipo de turismo.");
    }
  });
});

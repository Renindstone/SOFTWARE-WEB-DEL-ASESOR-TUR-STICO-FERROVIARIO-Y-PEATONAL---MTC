// Dibuja, sobre Leaflet.js, la estacion de origen, la zona turistica de
// destino y el trazo de la ruta de ida y vuelta (RF-04/RF-05).
// TODO Sprint 2: recibir coordenadas reales de origen/destino desde el
// backend (data-attributes en el contenedor del mapa) y trazar la
// polyline de ida y vuelta.
document.addEventListener("DOMContentLoaded", function () {
  var contenedor = document.querySelector("[id^='mapa-']");
  if (!contenedor || typeof L === "undefined") {
    return;
  }

  var latOrigen = parseFloat(contenedor.dataset.latOrigen) || -13.5183;
  var lonOrigen = parseFloat(contenedor.dataset.lonOrigen) || -71.9781;

  var mapa = L.map(contenedor.id).setView([latOrigen, lonOrigen], 13);

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "&copy; OpenStreetMap contributors"
  }).addTo(mapa);

  L.marker([latOrigen, lonOrigen]).addTo(mapa).bindPopup("Estación de origen");
});

// Dibuja, sobre Leaflet.js, la estacion de origen, la zona turistica de
// destino y el trazo de la ruta de ida y vuelta (RF-04/RF-05, seccion 5.1).
//
// Las coordenadas llegan como data-attributes del contenedor del mapa, que la
// vista cliente/ruta-detalle.html rellena desde Estacion y ZonaTuristica.
document.addEventListener("DOMContentLoaded", function () {
  var contenedor = document.querySelector("[id^='mapa-']");
  if (!contenedor || typeof L === "undefined") {
    return;
  }

  var latOrigen = parseFloat(contenedor.dataset.latOrigen);
  var lonOrigen = parseFloat(contenedor.dataset.lonOrigen);
  if (isNaN(latOrigen) || isNaN(lonOrigen)) {
    return;
  }

  var mapa = L.map(contenedor.id).setView([latOrigen, lonOrigen], 14);

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    maxZoom: 19,
    attribution: "&copy; OpenStreetMap contributors"
  }).addTo(mapa);

  var nombreOrigen = contenedor.dataset.nombreOrigen || "Estación de origen";
  var marcadorOrigen = L.marker([latOrigen, lonOrigen]).addTo(mapa);
  marcadorOrigen.bindPopup("<strong>Inicio y retorno</strong><br>" + nombreOrigen);

  var latDestino = parseFloat(contenedor.dataset.latDestino);
  var lonDestino = parseFloat(contenedor.dataset.lonDestino);
  if (isNaN(latDestino) || isNaN(lonDestino)) {
    marcadorOrigen.openPopup();
    return;
  }

  var nombreDestino = contenedor.dataset.nombreDestino || "Zona turística";
  L.marker([latDestino, lonDestino]).addTo(mapa)
    .bindPopup("<strong>Destino</strong><br>" + nombreDestino);

  // RNF-04: el trazo vuelve a la estacion de origen, para que el circuito
  // cerrado de ida y vuelta se vea explicitamente en el mapa.
  var trazo = [
    [latOrigen, lonOrigen],
    [latDestino, lonDestino],
    [latOrigen, lonOrigen]
  ];
  L.polyline(trazo, {
    color: "#14453D",
    weight: 4,
    opacity: 0.85,
    dashArray: "8, 6"
  }).addTo(mapa);

  mapa.fitBounds(L.latLngBounds([
    [latOrigen, lonOrigen],
    [latDestino, lonDestino]
  ]).pad(0.35));
});

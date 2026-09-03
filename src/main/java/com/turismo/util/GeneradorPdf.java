package com.turismo.util;

import com.turismo.dto.InformeConsolidadoDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Genera la representacion PDF del informe consolidado (RF-08) a partir
 * de InformeConsolidadoDTO, usando OpenPDF 2.0.5 (com.github.librepdf:openpdf,
 * paquete com.lowagie.text; fijado en 2.x porque la serie 3.x requiere Java 21).
 */
@Component
public class GeneradorPdf {

    // OpenPDF 2.x (com.lowagie.text.Font) usa la familia como constante int
    // (Font.HELVETICA), no el enum Font.FontFamily.HELVETICA de la serie 3.x.
    private static final Font FUENTE_TITULO = new Font(Font.HELVETICA, 18, Font.BOLD);
    private static final Font FUENTE_SUBTITULO = new Font(Font.HELVETICA, 11, Font.ITALIC, Color.GRAY);
    private static final Font FUENTE_ETIQUETA = new Font(Font.HELVETICA, 10, Font.BOLD);
    private static final Font FUENTE_VALOR = new Font(Font.HELVETICA, 10);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generar(InformeConsolidadoDTO informe) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 40, 40, 50, 40);

        try {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            documento.add(new Paragraph("Asesor Turístico Ferroviario y Peatonal - MTC", FUENTE_TITULO));
            documento.add(new Paragraph("Informe consolidado de visita  ·  Código: " + informe.getCodigo(), FUENTE_SUBTITULO));
            documento.add(Chunk.NEWLINE);

            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{1.3f, 2f});

            agregarFila(tabla, "Fecha de visita", formatearFecha(informe));
            agregarFila(tabla, "Estación de origen", informe.getEstacionOrigen());
            agregarFila(tabla, "Zona turística", informe.getZonaDestino());

            if (informe.getRuta() != null) {
                agregarFila(tabla, "Distancia (ida y vuelta)", valor(informe.getRuta().getDistanciaKm(), " km"));
                agregarFila(tabla, "Tiempo estimado", valor(informe.getRuta().getTiempoEstimadoMin(), " min"));
                agregarFila(tabla, "Dificultad", informe.getRuta().getDificultad());
            }

            agregarFila(tabla, "Clima previsto",
                    valor(informe.getTemperaturaMinimaC(), "") + " - " + valor(informe.getTemperaturaMaximaC(), "°C, ")
                            + valor(informe.getProbabilidadLluvia(), "% lluvia")
                            + (informe.getEstadoClima() != null ? " (" + informe.getEstadoClima() + ")" : ""));
            agregarFila(tabla, "Tarifa del tren", valor(informe.getTarifaTren(), null));
            agregarFila(tabla, "Total estimado", valor(informe.getTotalEstimado(), null));

            documento.add(tabla);
            documento.add(Chunk.NEWLINE);
            documento.add(new Paragraph("Generado automáticamente por el sistema del MTC.", FUENTE_SUBTITULO));

            documento.close();
        } catch (DocumentException ex) {
            throw new IllegalStateException("No se pudo generar el PDF del informe consolidado", ex);
        }

        return salida.toByteArray();
    }

    private void agregarFila(PdfPTable tabla, String etiqueta, Object valor) {
        PdfPCell celdaEtiqueta = new PdfPCell(new Paragraph(etiqueta, FUENTE_ETIQUETA));
        celdaEtiqueta.setBorderColor(Color.LIGHT_GRAY);
        celdaEtiqueta.setPadding(6);

        PdfPCell celdaValor = new PdfPCell(new Paragraph(valor == null ? "-" : valor.toString(), FUENTE_VALOR));
        celdaValor.setBorderColor(Color.LIGHT_GRAY);
        celdaValor.setPadding(6);

        tabla.addCell(celdaEtiqueta);
        tabla.addCell(celdaValor);
    }

    private String formatearFecha(InformeConsolidadoDTO informe) {
        return informe.getFechaVisita() == null ? "-" : informe.getFechaVisita().format(FORMATO_FECHA);
    }

    private String valor(Object dato, String sufijo) {
        if (dato == null) {
            return "";
        }
        return dato + (sufijo == null ? "" : sufijo);
    }
}

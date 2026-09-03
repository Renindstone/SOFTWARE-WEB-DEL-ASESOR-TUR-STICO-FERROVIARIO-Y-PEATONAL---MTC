package com.turismo.util;

import com.turismo.dto.InformeConsolidadoDTO;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

/**
 * Genera la representacion PDF del informe consolidado (RF-08) a partir
 * de InformeConsolidadoDTO, usando la vista Thymeleaf renderizada por
 * InformeService como fuente del contenido (OpenPDF / iText).
 *
 * TODO: integrar la libreria PDF elegida (OpenPDF o iText) en Sprint 4.
 */
@Component
public class GeneradorPdf {

    public byte[] generar(InformeConsolidadoDTO informe) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        // TODO: renderizar InformeConsolidadoDTO a PDF (OpenPDF/iText).
        return salida.toByteArray();
    }
}

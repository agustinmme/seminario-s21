package cfp.pkg402.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.awt.Desktop;
import cfp.pkg402.config.AppConfig;

// Util para crear certificados o completarlos
public class CertificadoPDFUtil {

    // Constantes para el documento
    private static final String TITULO_PROVINCIA = "PROVINCIA DE BUENOS AIRES";
    private static final String CFP_NUMERO = AppConfig.CFP_NUMERO;
    private static final String DISTRITO = AppConfig.DISTRITO;

    // Fuentes para modo normal
    private static Font fontTituloNormal;
    private static Font fontNormalNormal;
    private static Font fontPequenaNormal;
    private static Font fontNormalBold;

    // Fuentes para modo invertido
    private static Font fontTituloInvertido;
    private static Font fontNormalInvertido;
    private static Font fontPequenaInvertido;
    private static Font fontNormalBoldInvertido;

    static {
        try {
            // Fuentes modo normal
            fontTituloNormal = AppConfig.FONT_PDF_TITULO_16;
            fontNormalNormal = AppConfig.FONT_PDF_NORMAL;
            fontPequenaNormal = AppConfig.FONT_PDF_TABLA;
            fontNormalBold = AppConfig.FONT_PDF_NORMAL_BOLD;

            // Fuentes modo invertido
            fontTituloInvertido = AppConfig.FONT_PDF_TITULO_16_WHITE;
            fontNormalInvertido = AppConfig.FONT_PDF_NORMAL_WHITE;
            fontPequenaInvertido = AppConfig.FONT_PDF_TABLA_WHITE;
            fontNormalBoldInvertido = AppConfig.FONT_PDF_NORMAL_BOLD;

        } catch (Exception e) {
            // Fallback a fuentes  - TODO: Cargar otro itext
            // Fuentes modo normal
            fontTituloNormal = AppConfig.FONT_PDF_TITULO_16;
            fontNormalNormal = AppConfig.FONT_PDF_NORMAL;
            fontPequenaNormal = AppConfig.FONT_PDF_TABLA;
            fontNormalBold = AppConfig.FONT_PDF_NORMAL_BOLD;

            // Fuentes modo invertido
            fontTituloInvertido = AppConfig.FONT_PDF_TITULO_16_WHITE;
            fontNormalInvertido = AppConfig.FONT_PDF_NORMAL_WHITE;
            fontPequenaInvertido = AppConfig.FONT_PDF_TABLA_WHITE;
            fontNormalBoldInvertido = AppConfig.FONT_PDF_NORMAL_BOLD;
        }
    }

    // Generar el pdf en negro
    public static boolean generarCertificado(String nombreAlumno, String dni, String nombreCurso,
            String horasDuracion, List<String> modulos) {
        return generarCertificadoConModo(nombreAlumno, dni, nombreCurso, horasDuracion, modulos, false);
    }

    // Completar certificado - remplazo el texto por color blanco
    public static boolean generarCertificadoInvertido(String nombreAlumno, String dni, String nombreCurso,
            String horasDuracion, List<String> modulos) {
        return generarCertificadoConModo(nombreAlumno, dni, nombreCurso, horasDuracion, modulos, true);
    }

    // Generar certificado
    private static boolean generarCertificadoConModo(String nombreAlumno, String dni, String nombreCurso,
            String horasDuracion, List<String> modulos, boolean modoInvertido) {

        // Selector de archivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Certificado");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos PDF", "pdf"));

        // TODO : Agregar carpeta de curso y para aguardarlo por curso
        String sufijo = modoInvertido ? "_FormatoFisico" : "";
        String nombreArchivo = "Certificado_" + nombreAlumno.replaceAll("[^a-zA-Z]", "") + sufijo + "_"
                + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + ".pdf";
        fileChooser.setSelectedFile(new File(nombreArchivo));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String rutaArchivo = fileChooser.getSelectedFile().getAbsolutePath();
            if (!rutaArchivo.toLowerCase().endsWith(".pdf")) {
                rutaArchivo += ".pdf";
            }

            try {
                generarPDF(rutaArchivo, nombreAlumno, dni, nombreCurso, horasDuracion, modulos, modoInvertido);

                int opcion = JOptionPane.showConfirmDialog(null,
                        "Certificado generado exitosamente en:\n" + rutaArchivo
                        + "\n\nDesea abrir el archivo?",
                        "Certificado Generado",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);

                if (opcion == JOptionPane.YES_OPTION) {
                    abrirArchivo(rutaArchivo);
                }

                return true;

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error al generar el certificado:\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    // Generar formato
    private static void generarPDF(String rutaArchivo, String nombreAlumno, String dni,
            String nombreCurso, String horasDuracion, List<String> modulos, boolean modoInvertido)
            throws DocumentException, IOException {

        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));

        document.open();

        // genera Cara 
        generarPaginaPrincipal(document, nombreAlumno, dni, nombreCurso, horasDuracion, modoInvertido);

        // generar Contra cara
        document.newPage();
        generarPaginaModulos(document, modulos, modoInvertido);

        document.close();
    }

    // switcheo de fuente segun mod 
    private static Font[] obtenerFuentes(boolean modoInvertido) {
        if (modoInvertido) {
            return new Font[]{fontTituloInvertido, fontNormalInvertido, fontPequenaInvertido, fontNormalBoldInvertido};
        } else {
            return new Font[]{fontTituloNormal, fontNormalNormal, fontPequenaNormal, fontNormalBold};
        }
    }

    // Gnerar Cara
    private static void generarPaginaPrincipal(Document document, String nombreAlumno, String dni,
            String nombreCurso, String horasDuracion, boolean modoInvertido)
            throws DocumentException {

        Font[] fuentes = obtenerFuentes(modoInvertido);
        Font fontTitulo = fuentes[0];
        Font fontNormal = fuentes[1];
        Font fontPequena = fuentes[2];
        Font fontNormalBold = fuentes[3];

        // Espacio
        document.add(new Paragraph(" ", fontNormal));
        document.add(new Paragraph(" ", fontNormal));

        // Escudo(Placeholder) y titulo
        Font fontEscudo = modoInvertido
                ? new Font(Font.FontFamily.HELVETICA, 24, Font.NORMAL, BaseColor.WHITE)
                : new Font(Font.FontFamily.HELVETICA, 24);
        Paragraph escudo = new Paragraph("BA", fontEscudo);
        escudo.setAlignment(Element.ALIGN_CENTER);
        escudo.setSpacingBefore(-20);
        escudo.setSpacingAfter(10);
        document.add(escudo);

        Paragraph titulo = new Paragraph(TITULO_PROVINCIA, fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(30);
        document.add(titulo);

        // Espacios
        document.add(new Paragraph(" ", fontNormal));
        document.add(new Paragraph(" ", fontNormal));
        document.add(new Paragraph(" ", fontNormal));

        // Nobmre
        Paragraph porCuanto = new Paragraph();
        porCuanto.add(new Chunk("Por cuanto ", fontNormal));
        porCuanto.add(new Chunk(nombreAlumno.toUpperCase(), fontNormalBold));
        porCuanto.add(new Chunk(crearLineaConPuntos(50), fontNormal));
        porCuanto.setAlignment(Element.ALIGN_LEFT);
        porCuanto.setSpacingAfter(15);
        document.add(porCuanto);

        // DNI
        Paragraph lineaDni = new Paragraph();
        lineaDni.add(new Chunk("D.U. Nº ", fontNormal));
        lineaDni.add(new Chunk(dni, fontNormalBold));
        lineaDni.add(new Chunk(crearLineaConPuntos(20), fontNormal));
        lineaDni.add(new Chunk(" ha completado y aprobado el Trayecto/Curso de FORMACION PROFESIONAL", fontNormal));
        lineaDni.setAlignment(Element.ALIGN_LEFT);
        lineaDni.setSpacingAfter(15);
        document.add(lineaDni);

        // Curso
        Paragraph lineaCurso = new Paragraph();
        lineaCurso.add(new Chunk("de ", fontNormal));
        lineaCurso.add(new Chunk(nombreCurso.toUpperCase(), fontNormalBold));
        lineaCurso.add(new Chunk(crearLineaConPuntos(30), fontNormal));
        lineaCurso.add(new Chunk(" de", fontNormal));
        lineaCurso.add(new Chunk(crearLineaConPuntos(10), fontNormal));
        lineaCurso.add(new Chunk(horasDuracion, fontNormalBold));
        lineaCurso.add(new Chunk(" horas de duracion.", fontNormal));
        lineaCurso.setAlignment(Element.ALIGN_LEFT);
        lineaCurso.setSpacingAfter(50);
        document.add(lineaCurso);

        // Espacios antes de la fecha
        document.add(new Paragraph(" ", fontNormal));
        document.add(new Paragraph(" ", fontNormal));
        document.add(new Paragraph(" ", fontNormal));

        // Fecha actual
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaActual = sdf.format(new Date());
        String[] parteFecha = fechaActual.split("/");

        Paragraph fecha = new Paragraph();
        fecha.add(new Chunk(crearLineaConPuntos(10), fontNormal));
        fecha.add(new Chunk(parteFecha[0], fontNormalBold)); // DIA
        fecha.add(new Chunk(crearLineaConPuntos(10), fontNormal));
        fecha.add(new Chunk("de", fontNormal));
        fecha.add(new Chunk(crearLineaConPuntos(10), fontNormal));
        fecha.add(new Chunk(obtenerNombreMes(Integer.parseInt(parteFecha[1])), fontNormalBold)); // MES
        fecha.add(new Chunk(crearLineaConPuntos(30), fontNormal));
        fecha.add(new Chunk("de 20", fontNormal));
        fecha.add(new Chunk(parteFecha[2].substring(2), fontNormalBold)); // ANO
        fecha.add(new Chunk(crearLineaConPuntos(5), fontNormal));
        fecha.setAlignment(Element.ALIGN_CENTER);
        fecha.setSpacingAfter(50);
        document.add(fecha);

        // Espacios antes de la firma
        document.add(new Paragraph(" ", fontNormal));
        document.add(new Paragraph(" ", fontNormal));

        // Inspector
        Paragraph inspector = new Paragraph();
        inspector.add(new Chunk(crearLineaConPuntos(40), fontNormal));
        inspector.setAlignment(Element.ALIGN_CENTER);
        document.add(inspector);

        Paragraph labelInspector = new Paragraph("Inspector", fontPequena);
        labelInspector.setAlignment(Element.ALIGN_CENTER);
        labelInspector.setSpacingAfter(30);
        document.add(labelInspector);

        // Sello
        Paragraph sello = new Paragraph(" ", fontNormal);
        sello.setSpacingAfter(50);
        document.add(sello);

        Paragraph labelSello = new Paragraph("Sello", fontPequena);
        labelSello.setAlignment(Element.ALIGN_CENTER);
        document.add(labelSello);
    }

    // Contra cara
    private static void generarPaginaModulos(Document document, List<String> modulos, boolean modoInvertido)
            throws DocumentException {

        Font[] fuentes = obtenerFuentes(modoInvertido);
        Font fontTitulo = fuentes[0];
        Font fontNormal = fuentes[1];
        Font fontPequena = fuentes[2];
        Font fontNormalBold = fuentes[3];

        // Modulos
        Paragraph tituloModulos = new Paragraph("En caso que la formacion realizada contenga Modulos, se indican a continuacion:", fontNormal);
        tituloModulos.setAlignment(Element.ALIGN_LEFT);
        tituloModulos.setSpacingAfter(20);
        tituloModulos.setPaddingTop(10);
        document.add(tituloModulos);

        // Crear tabla de modulos en dos columnas
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(30);

        // Configurar color de borde segun el modo
        BaseColor colorBorde = modoInvertido ? BaseColor.WHITE : BaseColor.BLACK;

        // Agregar modulos max 10 TODO: se podria mejorar
        for (int i = 0; i < 10; i++) {

            if (i < 5) {
                PdfPCell cellLeft = new PdfPCell();
                cellLeft.setBorder(Rectangle.BOX);
                cellLeft.setBorderColor(colorBorde);
                cellLeft.setPadding(8);

                Paragraph moduloIzq = new Paragraph();
                moduloIzq.add(new Chunk((i + 1) + " ", fontNormal));

                if (i < modulos.size() && !modulos.get(i).trim().isEmpty()) {
                    moduloIzq.add(new Chunk(modulos.get(i), fontNormalBold));
                    moduloIzq.add(new Chunk(crearLineaConPuntos(30), fontNormal));
                } else {
                    moduloIzq.add(new Chunk(crearLineaConPuntos(50), fontNormal));
                }
                moduloIzq.add(Chunk.NEWLINE);
                moduloIzq.add(new Chunk(crearLineaConPuntos(50), fontNormal));

                cellLeft.addElement(moduloIzq);
                table.addCell(cellLeft);
            }

            // Columna derecha 
            if (i < 5) {
                int moduloDerechaIndex = i + 5;
                PdfPCell cellRight = new PdfPCell();
                cellRight.setBorder(Rectangle.BOX);
                cellRight.setBorderColor(colorBorde);
                cellRight.setPadding(8);

                Paragraph moduloDer = new Paragraph();
                moduloDer.add(new Chunk((moduloDerechaIndex + 1) + " ", fontNormal));

                if (moduloDerechaIndex < modulos.size() && !modulos.get(moduloDerechaIndex).trim().isEmpty()) {
                    moduloDer.add(new Chunk(modulos.get(moduloDerechaIndex), fontNormalBold));
                    moduloDer.add(new Chunk(crearLineaConPuntos(30), fontNormal));
                } else {
                    moduloDer.add(new Chunk(crearLineaConPuntos(50), fontNormal));
                }
                moduloDer.add(Chunk.NEWLINE);
                moduloDer.add(new Chunk(crearLineaConPuntos(50), fontNormal));

                cellRight.addElement(moduloDer);
                table.addCell(cellRight);
            }
        }

        document.add(table);

        // Espacios
        document.add(new Paragraph(" ", fontNormal));
        document.add(new Paragraph(" ", fontNormal));

        // Firma directivos
        Paragraph firmaDirector = new Paragraph();
        firmaDirector.add(new Chunk(crearLineaConPuntos(50), fontNormal));
        firmaDirector.setAlignment(Element.ALIGN_CENTER);
        document.add(firmaDirector);

        Paragraph labelDirector = new Paragraph("DIRECTOR / SECRETARIO", fontPequena);
        labelDirector.setAlignment(Element.ALIGN_CENTER);
        labelDirector.setSpacingAfter(40);
        document.add(labelDirector);

        // Segundo sello
        Paragraph sello2 = new Paragraph(" ", fontNormal);
        sello2.setSpacingAfter(30);
        document.add(sello2);

        Paragraph labelSello2 = new Paragraph("Sello", fontPequena);
        labelSello2.setAlignment(Element.ALIGN_CENTER);
        labelSello2.setSpacingAfter(30);
        document.add(labelSello2);

        // final
        Paragraph certificacion = new Paragraph("CERTIFICO que las firmas que anteceden, son autenticas y corresponden al Director / Secretario", fontNormal);
        certificacion.setAlignment(Element.ALIGN_LEFT);
        certificacion.setSpacingAfter(15);
        document.add(certificacion);

        Paragraph cfpInfo = new Paragraph();
        cfpInfo.add(new Chunk("del C.F.P Nº ", fontNormal));
        cfpInfo.add(new Chunk(crearLineaConPuntos(30), fontNormal));
        cfpInfo.add(new Chunk(CFP_NUMERO, fontNormalBold));
        cfpInfo.add(new Chunk(crearLineaConPuntos(30), fontNormal));
        cfpInfo.add(new Chunk(" del Distrito de ", fontNormal));
        cfpInfo.add(new Chunk(crearLineaConPuntos(30), fontNormal));
        cfpInfo.add(new Chunk(DISTRITO, fontNormalBold));
        cfpInfo.add(new Chunk(crearLineaConPuntos(30), fontNormal));
        cfpInfo.setAlignment(Element.ALIGN_LEFT);
        cfpInfo.setSpacingAfter(40);
        document.add(cfpInfo);

        // Linea para Autoridad Certificante
        Paragraph autoridadCert = new Paragraph();
        autoridadCert.add(new Chunk(crearLineaConPuntos(50), fontNormal));
        autoridadCert.setAlignment(Element.ALIGN_CENTER);
        document.add(autoridadCert);

        Paragraph labelAutoridad = new Paragraph("Autoridad Certificante", fontPequena);
        labelAutoridad.setAlignment(Element.ALIGN_CENTER);
        document.add(labelAutoridad);
    }

    // Linea
    private static String crearLineaConPuntos(int tamano) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tamano; i++) {
            sb.append(".");
        }
        return sb.toString();
    }

    // Aux meses
    private static String obtenerNombreMes(int mes) {
        String[] meses = {
            "enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
        };
        return meses[mes - 1];
    }

    // Abrir el archivo generado
    private static void abrirArchivo(String rutaArchivo) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(rutaArchivo));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo abrir el archivo\n"
                    + "Puede encontrarlo en: " + rutaArchivo,
                    "Informacion",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

}

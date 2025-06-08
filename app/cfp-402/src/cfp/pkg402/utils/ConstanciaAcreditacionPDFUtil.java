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

// Util para crear acreditaciones
public class ConstanciaAcreditacionPDFUtil {

    // Constantes para el documento
    private static final String TITULO_PROVINCIA = "PROVINCIA DE BUENOS AIRES";
    private static final String TITULO_CONSTANCIA = "CONSTANCIA DE ACREDITACION";

    // Fuentes
    private static Font fontTitulo;
    private static Font fontSubtitulo;
    private static Font fontNormal;
    private static Font fontTabla;
    private static Font fontPequena;

    static {
        try {
            fontTitulo = AppConfig.FONT_PDF_TITULO;
            fontSubtitulo = AppConfig.FONT_PDF_SUBTITULO;
            fontNormal = AppConfig.FONT_PDF_NORMAL;
            fontTabla = AppConfig.FONT_PDF_TABLA;
            fontPequena = AppConfig.FONT_PDF_PEQUENA;

        } catch (Exception e) {
            // Fallback a fuentes base - TODO: Cargar otro itext
            fontTitulo = AppConfig.FONT_PDF_TITULO;
            fontSubtitulo = AppConfig.FONT_PDF_SUBTITULO;
            fontNormal = AppConfig.FONT_PDF_NORMAL;
            fontTabla = AppConfig.FONT_PDF_TABLA;
            fontPequena = AppConfig.FONT_PDF_PEQUENA;
        }
    }

    // Generar acreditacion
    public static boolean generarConstancia(String nombreAlumno, String dni, String nombreCurso,
            String horasDuracion, List<ModuloInfo> modulos) {

        // Selector de archivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Constancia de Acreditacion");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos PDF", "pdf"));

        // TODO : Agregar carpeta de curso y para aguarlo por curso
        String nombreArchivo = "Constancia_" + nombreAlumno.replaceAll("[^a-zA-Z]", "") + "_"
                + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + ".pdf";
        fileChooser.setSelectedFile(new File(nombreArchivo));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String rutaArchivo = fileChooser.getSelectedFile().getAbsolutePath();
            if (!rutaArchivo.toLowerCase().endsWith(".pdf")) {
                rutaArchivo += ".pdf";
            }

            try {
                // Metodo itext
                generarPDF(rutaArchivo, nombreAlumno, dni, nombreCurso, horasDuracion, modulos);

                int opcion = JOptionPane.showConfirmDialog(null,
                        "Constancia generada exitosamente en:\n" + rutaArchivo
                        + "\n\nDesea abrir el archivo?",
                        "Constancia Generada",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);

                if (opcion == JOptionPane.YES_OPTION) {
                    abrirArchivo(rutaArchivo);
                }

                return true;

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error al generar la constancia:\n" + e.getMessage(),
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
            String nombreCurso, String horasDuracion, List<ModuloInfo> modulos)
            throws DocumentException, IOException {

        Document document = new Document(PageSize.A4);
        // Margenes: izq, der, arr, aba
        document.setMargins(50, 10, 50, 50); 
        PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));

        document.open();

        // Generar el contenido de la constancia
        generarContenidoConstancia(document, nombreAlumno, dni, nombreCurso, horasDuracion, modulos);

        document.close();
    }

    // Plantilla constancia
    private static void generarContenidoConstancia(Document document, String nombreAlumno, String dni,
            String nombreCurso, String horasDuracion,
            List<ModuloInfo> modulos) throws DocumentException {

        // Escudo (Placeholder)
        Paragraph escudo = new Paragraph("BA", new Font(Font.FontFamily.HELVETICA, 36));
        escudo.setAlignment(Element.ALIGN_CENTER);
        escudo.setSpacingBefore(-20);
        escudo.setSpacingAfter(10);
        document.add(escudo);

        // Titulo PROVINCIA DE BUENOS AIRES
        Paragraph tituloProvincia = new Paragraph(TITULO_PROVINCIA, fontTitulo);
        tituloProvincia.setAlignment(Element.ALIGN_CENTER);
        tituloProvincia.setSpacingAfter(20);
        document.add(tituloProvincia);

        // Espaciado - A VECES FUNCIONA / SINO FUNCIONA USAR setSpacingAfter O Before
        document.add(new Paragraph(" ", fontNormal));

        // Marco para CONSTANCIA DE ACREDITACION
        PdfPTable tituloTable = new PdfPTable(1);
        tituloTable.setWidthPercentage(60);
        tituloTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell tituloCell = new PdfPCell();
        tituloCell.setBorder(Rectangle.BOX);
        tituloCell.setBorderWidth(2);
        tituloCell.setPadding(8);
        tituloCell.setPaddingTop(-4);
        tituloCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph tituloConstancia = new Paragraph(TITULO_CONSTANCIA, fontSubtitulo);
        tituloConstancia.setAlignment(Element.ALIGN_CENTER);
        tituloCell.addElement(tituloConstancia);

        tituloTable.addCell(tituloCell);
        tituloTable.setSpacingAfter(30);
        document.add(tituloTable);

        // Crear la tabla principal de modulos
        generarTablaModulos(document, modulos);

        // Espaciado
        document.add(new Paragraph(" ", fontNormal));
        document.add(new Paragraph(" ", fontNormal));

        // Firma
        generarLineasFirma(document);
    }

    // Gnerar tablas modulos
    private static void generarTablaModulos(Document document, List<ModuloInfo> modulos)
            throws DocumentException {

        // Crear tabla con 3 columnas: Orden, descripcion, Horas
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.25f, 6f, 1.5f}); // Proporciones de las columnas
        table.setSpacingAfter(30);

        // Encabezados de la tabla
        agregarCeldaEncabezado(table, "Orden");
        agregarCeldaEncabezado(table, "Descripcion del modulo");
        agregarCeldaEncabezado(table, "Horas");

        // Agregar filas agregar hasta 10
        for (int i = 1; i <= 10; i++) {
            // Columna Orden
            PdfPCell cellOrden = new PdfPCell();
            cellOrden.setBorder(Rectangle.BOX);
            cellOrden.setPadding(8);
            cellOrden.setMinimumHeight(30);
            cellOrden.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellOrden.setVerticalAlignment(Element.ALIGN_MIDDLE);

            Paragraph orden = new Paragraph(String.valueOf(i), fontTabla);
            cellOrden.addElement(orden);
            table.addCell(cellOrden);

            // Columna Descripcion
            PdfPCell cellDesc = new PdfPCell();
            cellDesc.setBorder(Rectangle.BOX);
            cellDesc.setPadding(8);
            cellDesc.setMinimumHeight(30);
            cellDesc.setVerticalAlignment(Element.ALIGN_MIDDLE);
            // Completar celda
            if (i <= modulos.size() && modulos.get(i - 1) != null) {
                ModuloInfo modulo = modulos.get(i - 1);
                Paragraph desc = new Paragraph(modulo.getDescripcion(), fontTabla);
                cellDesc.addElement(desc);
            } else {
                Paragraph noModulos = new Paragraph("-------- FIN DE MODULOS --------", fontTabla);
                noModulos.setAlignment(Element.ALIGN_CENTER);
                cellDesc.addElement(noModulos);
            }
            table.addCell(cellDesc);

            // Columna Horas
            PdfPCell cellHoras = new PdfPCell();
            cellHoras.setBorder(Rectangle.BOX);
            cellHoras.setPadding(8);
            cellHoras.setMinimumHeight(30);
            cellHoras.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellHoras.setVerticalAlignment(Element.ALIGN_MIDDLE);
            // Completar celda
            if (i <= modulos.size() && modulos.get(i - 1) != null) {
                ModuloInfo modulo = modulos.get(i - 1);
                Paragraph horas = new Paragraph(String.valueOf(modulo.getHoras()), fontTabla);
                cellHoras.addElement(horas);
            } else {
                Paragraph noHoras = new Paragraph("----- X -----", fontTabla);
                noHoras.setAlignment(Element.ALIGN_CENTER);
                cellHoras.addElement(noHoras);
            }
            table.addCell(cellHoras);
        }

        document.add(table);
    }

    // Header tabla
    private static void agregarCeldaEncabezado(PdfPTable table, String texto) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setBorderWidth(1.5f);
        cell.setPadding(10);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph p = new Paragraph(texto, new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD));
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);

        table.addCell(cell);
    }

    // Lineas de firma
    private static void generarLineasFirma(Document document) throws DocumentException {

        // Crear tabla para las dos firmas
        PdfPTable firmaTable = new PdfPTable(2);
        firmaTable.setWidthPercentage(80);
        firmaTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        firmaTable.setSpacingBefore(50);

        // Firma izquierda
        PdfPCell firmaIzq = new PdfPCell();
        firmaIzq.setBorder(Rectangle.NO_BORDER);
        firmaIzq.setHorizontalAlignment(Element.ALIGN_CENTER);
        firmaIzq.setPaddingRight(20);

        // linea para firma
        Paragraph lineaFirma1 = new Paragraph();
        for (int i = 0; i < 25; i++) {
            lineaFirma1.add("_");
        }
        lineaFirma1.setAlignment(Element.ALIGN_CENTER);
        lineaFirma1.setSpacingAfter(10);
        firmaIzq.addElement(lineaFirma1);

        Paragraph label1 = new Paragraph("Director/Secretario", fontPequena);
        label1.setAlignment(Element.ALIGN_CENTER);
        firmaIzq.addElement(label1);

        firmaTable.addCell(firmaIzq);

        // Firma derecha
        PdfPCell firmaDer = new PdfPCell();
        firmaDer.setBorder(Rectangle.NO_BORDER);
        firmaDer.setHorizontalAlignment(Element.ALIGN_CENTER);
        firmaDer.setPaddingLeft(20);

        // LinEa para firma
        Paragraph lineaFirma2 = new Paragraph();
        for (int i = 0; i < 25; i++) {
            lineaFirma2.add("_");
        }
        lineaFirma2.setAlignment(Element.ALIGN_CENTER);
        lineaFirma2.setSpacingAfter(10);
        firmaDer.addElement(lineaFirma2);

        Paragraph label2 = new Paragraph("Autoridad Certificante", fontPequena);
        label2.setAlignment(Element.ALIGN_CENTER);
        firmaDer.addElement(label2);

        firmaTable.addCell(firmaDer);

        document.add(firmaTable);
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

    // Aux modulo info - TODO: Cambiar cuando este modulo funcionando...
    public static class ModuloInfo {

        private String descripcion;
        private int horas;

        public ModuloInfo(String descripcion, int horas) {
            this.descripcion = descripcion;
            this.horas = horas;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public int getHoras() {
            return horas;
        }

        public void setHoras(int horas) {
            this.horas = horas;
        }
    }
}

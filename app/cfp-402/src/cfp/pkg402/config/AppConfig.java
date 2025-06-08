package cfp.pkg402.config;

import java.awt.Color;
import java.awt.Font;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.FontFactory;
/**
 * Clase que contiene configuraciones globales de la aplicacion.
 */
public class AppConfig {
    // Colores 
    public static final Color COLOR_FONDO = new Color(220, 220, 225);
    public static final Color COLOR_PANEL_TITULO = new Color(240, 240, 240);
    public static final Color COLOR_BORDE = new Color(220, 220, 220);
    public static final Color COLOR_ICONO = new Color(100, 110, 120);
    public static final Color COLOR_FONT = Color.BLACK;
    
    // Fuentes 
    public static final Font FONT_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBTITULO = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_NORMAL_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    
    // Dimensiones
    public static final int ANCHO_VENTANA = 800;
    public static final int ALTO_VENTANA = 500;
    public static final int ANCHO_PANEL_OPCION = 220;
    public static final int ALTO_PANEL_OPCION = 250;
    
    // Titulo de la app
    public static final String TITULO_APP = "Sistema de Gestion - CFP N° 402";

    // Informacion institucional
    public static final String CFP_NUMERO = "402";
    public static final String DISTRITO = "BERISSO";

    // Fuentes para PDF 
    public static final com.itextpdf.text.Font FONT_PDF_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
    public static final com.itextpdf.text.Font FONT_PDF_TITULO_16 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
    public static final com.itextpdf.text.Font FONT_PDF_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
    public static final com.itextpdf.text.Font FONT_PDF_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
    public static final com.itextpdf.text.Font FONT_PDF_NORMAL_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
    public static final com.itextpdf.text.Font FONT_PDF_TABLA = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
    public static final com.itextpdf.text.Font FONT_PDF_PEQUENA = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
    
    // Fuentes para PDF - Modo Invertido
    public static final com.itextpdf.text.Font FONT_PDF_TITULO_16_WHITE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.WHITE);
    public static final com.itextpdf.text.Font FONT_PDF_NORMAL_WHITE = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.WHITE);
    public static final com.itextpdf.text.Font FONT_PDF_TABLA_WHITE = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.WHITE);
}
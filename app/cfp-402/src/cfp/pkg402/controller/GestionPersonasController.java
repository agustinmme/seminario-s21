package cfp.pkg402.controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import cfp.pkg402.view.GestionPersonasView;
import cfp.pkg402.model.GestionPersonasModel;
import cfp.pkg402.model.Persona;

// Controlador de la vista gestion persona - ACTUALIZADO CON DAO
public class GestionPersonasController {

    private GestionPersonasView view;
    private GestionPersonasModel model;
    private boolean modoEdicion = false;
    private boolean esNuevo = false;
    private int filaSeleccionada = -1;
    private String dniOriginal = "";

    // Regex & formatos
    private static final Pattern PATRON_EMAIL = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    private static final Pattern PATRON_TELEFONO = Pattern.compile(
            "^[0-9]{2,3}-[0-9]{4}-[0-9]{4}$|^[0-9]{8,12}$"
    );
    private static final Pattern PATRON_DNI = Pattern.compile("^[0-9]{7,8}$");

    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");

    // Constructor - Inicializa y Configura vista / controlador
    public GestionPersonasController(GestionPersonasView view) {
        this.view = view;
        this.model = new GestionPersonasModel();
        FORMATO_FECHA.setLenient(false); // forzar que ingresen bien la fecha
    }

    // Cargar datos desde la base de datos (reemplaza cargarDatosPrueba)
    public void cargarDatos() {
        try {
            List<Object[]> personas = model.obtenerTodasPersonas();

            // Limpiar tabla
            DefaultTableModel modeloTabla = view.getModeloPersonas();
            modeloTabla.setRowCount(0);

            // Cargar datos
            for (Object[] persona : personas) {
                modeloTabla.addRow(persona);
            }

            // Seleccionar primera fila si hay datos
            if (view.getTablaPersonas().getRowCount() > 0) {
                view.getTablaPersonas().setRowSelectionInterval(0, 0);
                cargarDetallesPersonaSeleccionada();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    view,
                    "Error al cargar los datos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Controlar crear nueva persona
    public void nuevaPersona() {
        if (modoEdicion) {
            int opcion = JOptionPane.showConfirmDialog(
                    view,
                    "Hay cambios sin guardar. Desea continuar sin guardar?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            // Si no es cancelar
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Limpiar todo
        view.getTablaPersonas().clearSelection();
        view.limpiarCampos();

        // Habilitar edicion
        view.habilitarEdicion(true);
        modoEdicion = true;
        esNuevo = true;
        dniOriginal = "";
        view.habilitarBtnNuevo(false);
        view.habilitarBtnEditar(false);
        view.habilitarBtnBuscar(false);
        view.habilitarTabla(false);
        view.habilitarBtnGuardar(true);
        view.habilitarBtnCancelar(true);
        // Enfocar el primer campo
        view.getTxtDNI().requestFocus();
        // Mensaje al crear
        JOptionPane.showMessageDialog(
                view,
                "Complete los campos obligatorios (*) para crear una nueva persona",
                "Nueva Persona",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Controlar Editar persona
    public void editarPersona() {
        int selectedRow = view.getTablaPersonas().getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                    view,
                    "Por favor, seleccione una persona para editar",
                    "Seleccion requerida",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Guardar DNI original para validaciones
        dniOriginal = view.getTxtDNI().getText().trim();

        // Habilitar edicion
        modoEdicion = true;
        esNuevo = false;
        filaSeleccionada = selectedRow;
        view.habilitarBtnNuevo(false);
        view.habilitarBtnEditar(false);
        view.habilitarBtnBuscar(false);
        view.habilitarTabla(false);
        view.habilitarBtnGuardar(true);
        view.habilitarBtnCancelar(true);
        // Habilitar campos
        view.habilitarEdicion(true);
        // Mensaje al editar
        JOptionPane.showMessageDialog(
                view,
                "Puede modificar los datos de la persona seleccionada",
                "Editar Persona",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Controlar guardar persona(actualizar o nueva)
    public void guardarPersona() {
        if (!modoEdicion) {
            JOptionPane.showMessageDialog(
                    view,
                    "No hay datos para guardar. Usar 'Nuevo' o 'Editar' primero",
                    "Sin cambios",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        // Validar datos
        if (!validarDatos()) {
            return;
        }

        try {
            boolean eraNuevo = esNuevo;

            if (eraNuevo) {
                guardarNuevaPersona();
            } else {
                actualizarPersonaExistente();
            }

            // Resetear estado despues del exito
            modoEdicion = false;
            esNuevo = false;
            dniOriginal = "";

            // Deshabilitar edicion y botones
            view.habilitarEdicion(false);
            view.habilitarBtnNuevo(true);
            view.habilitarBtnEditar(true);
            view.habilitarBtnBuscar(true);
            view.habilitarTabla(true);
            view.habilitarBtnGuardar(false);
            view.habilitarBtnCancelar(false);

            // Recargar datos para reflejar cambios
            cargarDatos();

            // Mensaje de exito
            JOptionPane.showMessageDialog(
                    view,
                    "Persona guardada correctamente",
                    "Exito",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    view,
                    "Error al guardar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Controlar guardar nueva persona (usa el model)
    private void guardarNuevaPersona() {
        boolean resultado = model.agregarPersona(
                view.getTxtDNI().getText().trim(),
                view.getTxtApellido().getText().trim(),
                view.getTxtNombre().getText().trim(),
                view.getTxtFechaNacimiento().getText().trim(),
                view.getTxtDireccion().getText().trim(),
                view.getTxtTelefono().getText().trim(),
                view.getTxtEmail().getText().trim(),
                view.getCmbRol().getSelectedItem().toString()
        );

        if (!resultado) {
            throw new RuntimeException("Error al insertar la persona en la base de datos");
        }
    }

    // Actualizar persona (usa el model)
    private void actualizarPersonaExistente() {
        boolean resultado = model.actualizarPersona(
                dniOriginal,
                view.getTxtDNI().getText().trim(),
                view.getTxtApellido().getText().trim(),
                view.getTxtNombre().getText().trim(),
                view.getTxtFechaNacimiento().getText().trim(),
                view.getTxtDireccion().getText().trim(),
                view.getTxtTelefono().getText().trim(),
                view.getTxtEmail().getText().trim(),
                view.getCmbRol().getSelectedItem().toString()
        );

        if (!resultado) {
            throw new RuntimeException("Error al actualizar la persona en la base de datos");
        }
    }

    // Controlar Buscar persona (usa el model)
    public void buscarPersona() {
        String textoBusqueda = view.getTxtBuscar().getText().trim();

        // Verificar que tenga datos el campo de busqueda
        if (textoBusqueda.isEmpty()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Ingrese un dni, nombre o apellido",
                    "Buscar vacia",
                    JOptionPane.WARNING_MESSAGE
            );
            view.getTxtBuscar().requestFocus();
            return;
        }

        try {
            List<Object[]> resultados = model.buscarPersonas(textoBusqueda);

            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(
                        view,
                        "No se encontraron personas que coincidan con: " + textoBusqueda,
                        "Sin resultados",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            // Actualizar tabla con resultados
            DefaultTableModel modeloTabla = view.getModeloPersonas();
            modeloTabla.setRowCount(0);

            for (Object[] resultado : resultados) {
                modeloTabla.addRow(resultado);
            }

            // Seleccionar primera fila
            if (view.getTablaPersonas().getRowCount() > 0) {
                view.getTablaPersonas().setRowSelectionInterval(0, 0);
                cargarDetallesPersonaSeleccionada();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    view,
                    "Error al realizar la busqueda: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Cargar detalles de la persona seleccionada desde la base de datos
    private void cargarDetallesPersonaSeleccionada() {
        int selectedRow = view.getTablaPersonas().getSelectedRow();
        if (selectedRow >= 0) {
            String dni = view.getModeloPersonas().getValueAt(selectedRow, 0).toString();

            try {
                Persona persona = model.obtenerPersonaPorDni(dni);
                if (persona != null) {
                    view.getTxtDNI().setText(persona.getDni());
                    view.getTxtApellido().setText(persona.getApellido());
                    view.getTxtNombre().setText(persona.getNombre());

                    if (persona.getFechaNacimiento() != null) {
                        view.getTxtFechaNacimiento().setText(FORMATO_FECHA.format(persona.getFechaNacimiento()));
                    } else {
                        view.getTxtFechaNacimiento().setText("");
                    }

                    view.getTxtDireccion().setText(persona.getDireccion() != null ? persona.getDireccion() : "");
                    view.getTxtTelefono().setText(persona.getTelefono() != null ? persona.getTelefono() : "");
                    view.getTxtEmail().setText(persona.getEmail() != null ? persona.getEmail() : "");
                    view.getCmbRol().setSelectedItem(persona.getRol());
                }
            } catch (Exception e) {
                System.err.println("Error al cargar detalles: " + e.getMessage());
            }
        }
    }

    // Controlar cuando esta seleccionada
    public void personaSeleccionada() {
        if (!view.getTablaPersonas().isEnabled()) {
            return;
        }
        if (modoEdicion) {
            // Si esta en modo edicion
            int opcion = JOptionPane.showConfirmDialog(
                    view,
                    "Hay cambios sin guardar. Desea cancelar los cambios?",
                    "Cambios sin guardar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (opcion != JOptionPane.YES_OPTION) {
                // Restaurar seleccion anterior
                if (filaSeleccionada >= 0 && filaSeleccionada < view.getTablaPersonas().getRowCount()) {
                    view.getTablaPersonas().setRowSelectionInterval(filaSeleccionada, filaSeleccionada);
                }
                return;
            } else {
                // Cancelar edicion
                cancelar();
            }
        }

        // Cargar detalles de la persona seleccionada
        cargarDetallesPersonaSeleccionada();
    }

    // Validador de datos (actualizado para usar el model en validacion de DNI)
    private boolean validarDatos() {
        StringBuilder errores = new StringBuilder();

        // Validar DNI
        String dni = view.getTxtDNI().getText().trim();
        if (dni.isEmpty()) {
            errores.append("- El DNI es obligatorio\n");
        } else if (!PATRON_DNI.matcher(dni).matches()) {
            errores.append("- El DNI debe tener entre 7 y 8 digitos\n");
        } else if (model.existeDni(dni, dniOriginal)) {
            errores.append("- Ya existe una persona con el DNI: ").append(dni).append("\n");
        }

        // Validar Apellido
        String apellido = view.getTxtApellido().getText().trim();
        if (apellido.isEmpty()) {
            errores.append("- El apellido es obligatorio\n");
        } else if (apellido.length() < 2) {
            errores.append("- El apellido debe tener al menos 2 caracteres\n");
        }

        // Validar Nombre
        String nombre = view.getTxtNombre().getText().trim();
        if (nombre.isEmpty()) {
            errores.append("- El nombre es obligatorio\n");
        } else if (nombre.length() < 2) {
            errores.append("- El nombre debe tener al menos 2 caracteres\n");
        }

        // Validar Fecha de Nacimiento (opcional)
        String fechaNac = view.getTxtFechaNacimiento().getText().trim();
        if (!fechaNac.isEmpty()) {
            if (!validarFecha(fechaNac)) {
                errores.append("- La fecha de nacimiento debe tener formato dd/MM/yyyy y no ser futura\n");
            }
        }

        // Validar Email (opcional)
        String email = view.getTxtEmail().getText().trim();
        if (!email.isEmpty()) {
            if (!PATRON_EMAIL.matcher(email).matches()) {
                errores.append("- El formato del email no es valido\n");
            }
        }

        // Validar Telefono (opcional)
        String telefono = view.getTxtTelefono().getText().trim();
        if (!telefono.isEmpty()) {
            if (!PATRON_TELEFONO.matcher(telefono).matches()) {
                errores.append("- El telefono debe tener formato: XX-XXXX-XXXX o 8-12 digitos\n");
            }
        }

        // Mostrar errores si existen
        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(
                    view,
                    "Por favor, corrija los siguientes errores:\n\n" + errores.toString(),
                    "Errores de validacion",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        return true;
    }

    // Validador fecha
    private boolean validarFecha(String fecha) {
        try {
            Date fechaParseada = FORMATO_FECHA.parse(fecha);

            // Verificar que la fecha no sea futura
            Date hoy = new Date();
            if (fechaParseada.after(hoy)) {
                return false;
            }

            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // get modo edicion
    public boolean isModoEdicion() {
        return modoEdicion;
    }

    // Cancelar
    public void cancelar() {
        if (modoEdicion) {
            modoEdicion = false;
            esNuevo = false;
            dniOriginal = "";
            view.habilitarEdicion(false);
            view.habilitarBtnNuevo(true);
            view.habilitarBtnEditar(true);
            view.habilitarBtnBuscar(true);
            view.habilitarTabla(true);
            view.habilitarBtnGuardar(false);
            view.habilitarBtnCancelar(false);

            // Recargar datos originales
            cargarDatos();
        }
    }
}

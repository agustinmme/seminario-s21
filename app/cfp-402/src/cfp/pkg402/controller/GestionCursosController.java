package cfp.pkg402.controller;

import javax.swing.JOptionPane;

import cfp.pkg402.view.GestionCursosView;
import cfp.pkg402.model.GestionCursosModel;
import cfp.pkg402.view.components.AlumnoFormDialog;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// Controlador de la vista GestionCurso - Se comunica con el Modelo  para obtener/modificar datos y actualiza la Vista.
public class GestionCursosController {

    private GestionCursosView view;
    private GestionCursosModel model;

    // Contructor - Inicializa y Configura vista / modelo
    public GestionCursosController(GestionCursosView view) {
        this.view = view;
        this.model = new GestionCursosModel();
    }

    // Carga la UI de gestion de datos
    public void cargarDatosIniciales() {
        // Obtener cursos del modelo
        List<Object[]> cursos = model.obtenerTodosCursos();

        // Cargar cursos en la tabla
        for (Object[] curso : cursos) {
            // La tabla muestra codig, nombre, fechaInicio, fechaFin, profesor
            Object[] filaCurso = {curso[0], curso[1], curso[3], curso[4], curso[5]};
            view.getModeloCursos().addRow(filaCurso);
        }

        // Seleccionar el primer curso por defecto
        if (view.getTablaCursos().getRowCount() > 0) {
            view.getTablaCursos().setRowSelectionInterval(0, 0);
            cursoSeleccionado();
        }
    }

    // Getter datos de curso por CODIGO del curso
    public Object[] obtenerCursoCompleto(String codigo) {
        return model.obtenerCursoPorCodigo(codigo);
    }

    // Select curso
    public void cursoSeleccionado() {
        int selectedRow = view.getTablaCursos().getSelectedRow();
        if (selectedRow >= 0) {
            // Cargar los detalles del curso seleccionado
            view.cargarDetallesCurso();

            // Obtener el codigo del curso seleccionado
            String codigoCurso = view.getModeloCursos().getValueAt(selectedRow, 0).toString();
            String nombreCurso = view.getModeloCursos().getValueAt(selectedRow, 1).toString();

            // Actualizar la tabla de alumnos
            cargarAlumnosPorCurso(codigoCurso, nombreCurso);

        }
    }

    // Actualizar Datos de la tabla
    private void cargarAlumnosPorCurso(String codigoCurso, String nombreCurso) {
        // Limpiar la tabla de alumnos
        view.getModeloAlumnos().setRowCount(0);

        // Actualizar el titulo
        view.actualizarTituloAlumnos("Alumnos del Curso \"" + nombreCurso + "\"");

        // Obtener los alumnos del modelo
        List<Object[]> alumnos = model.obtenerAlumnosPorCurso(codigoCurso);

        if (!alumnos.isEmpty()) {
            for (Object[] alumno : alumnos) {
                view.getModeloAlumnos().addRow(alumno);
            }
        } else {
            view.getModeloAlumnos().addRow(new Object[]{"", "Sin alumnos inscriptos", "", ""});
        }
    }

    // Actualizar tabla de alumnos
    private void actualizarTablaAlumnos(String codigoCurso, String nombreCurso) {
        view.getModeloAlumnos().setRowCount(0);
        view.actualizarTituloAlumnos("Alumnos del Curso \"" + nombreCurso + "\"");

        List<Object[]> alumnos = model.obtenerAlumnosPorCurso(codigoCurso);

        if (!alumnos.isEmpty()) {
            for (Object[] alumno : alumnos) {
                view.getModeloAlumnos().addRow(alumno);
            }
            // Selecionar al ultimo agregado
            int ultimaFila = view.getModeloAlumnos().getRowCount() - 1;
            view.getTablaAlumnos().setRowSelectionInterval(ultimaFila, ultimaFila);
        } else {
            view.getModeloAlumnos().addRow(new Object[]{"", "Sin alumnos inscriptos", "", ""});
        }
    }

    // Controlar crear Curso
    public void nuevoCurso() {
        view.habilitarEdicion(true);
        view.limpiarCampos();

        // Limpiar tabla
        view.getTablaCursos().clearSelection();

        // Cambiar estado de botones
        view.habilitarBtnNuevo(false);
        view.habilitarBtnEditar(false);
        view.habilitarBtnGuardar(true);
        view.habilitarBtnCancelar(true);
        view.habilitarBtnBuscar(false);
        view.habilitarTablas(false);

        // Generar nuevo codigo del curso
        String nuevoCodigo = model.generarNuevoCodigo();
        view.getTxtCodigo().setText(nuevoCodigo);
        view.getTxtNombre().requestFocus();

    }

    // Controlar Editar Curso
    public void editarCurso() {
        int selectedRow = view.getTablaCursos().getSelectedRow();
        if (selectedRow >= 0) {
            view.habilitarEdicion(true);

            view.habilitarBtnNuevo(false);
            view.habilitarBtnEditar(false);
            view.habilitarBtnGuardar(true);
            view.habilitarBtnCancelar(true);
            view.habilitarBtnBuscar(false);
            view.habilitarTablas(false);

            view.getTxtNombre().requestFocus();
        } else {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un curso para editar",
                    "Curso no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // Controla guardar curso
    public void guardarCurso() {
        String codigo = view.getTxtCodigo().getText().trim();
        String nombre = view.getTxtNombre().getText().trim();
        String descripcion = view.getTxtDescripcion().getText().trim();
        String fechaInicio = view.getTxtFechaInicio().getText().trim();
        String fechaFin = view.getTxtFechaFin().getText().trim();
        String profesor = (String) view.getCboProfesor().getSelectedItem();
        List<String> modulos = view.getModulosTexto();

        // Validar campos obligatorios
        if (codigo.isEmpty() || nombre.isEmpty() || fechaInicio.isEmpty()
                || fechaFin.isEmpty() || profesor == null) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, complete todos los campos obligatorios (marcados con *)",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar modulos duplicados
        if (!modulos.isEmpty()) {
            Set<String> modulosUnicos = new HashSet<>();
            for (String modulo : modulos) {
                if (modulosUnicos.contains(modulo)) {
                    JOptionPane.showMessageDialog(view,
                            "No se pueden repetir modulos. El modulo \"" + modulo + "\" esta seleccionado mas de una vez",
                            "Modulos duplicados",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                modulosUnicos.add(modulo);
            }
        }

        // Validar formato de fechas 
        if (!model.validarFormatoFecha(fechaInicio) || !model.validarFormatoFecha(fechaFin)) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, ingrese las fechas en formato dd/MM/yyyy",
                    "Formato de fecha incorrecto",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object[] curso = {codigo, nombre, descripcion, fechaInicio, fechaFin, profesor, modulos};

        // Verificar si es un curso nuevo o una edicon
        int selectedRow = view.getTablaCursos().getSelectedRow();
        boolean exito = false;
        boolean esNuevo = selectedRow < 0;

        if (!esNuevo) {
            // Actualizar curso existente en el modelo
            String codigoOriginal = view.getModeloCursos().getValueAt(selectedRow, 0).toString();
            exito = model.actualizarCurso(codigoOriginal, curso);

            if (exito) {
                // Actualizar en la tabla
                view.getModeloCursos().setValueAt(codigo, selectedRow, 0);
                view.getModeloCursos().setValueAt(nombre, selectedRow, 1);
                view.getModeloCursos().setValueAt(fechaInicio, selectedRow, 2);
                view.getModeloCursos().setValueAt(fechaFin, selectedRow, 3);
                view.getModeloCursos().setValueAt(profesor, selectedRow, 4);
            }
        } else {
            // Agregar nuevo curso al modelo
            exito = model.agregarCurso(curso);

            if (exito) {
                // Agregar a la tabla
                Object[] filaCurso = {codigo, nombre, fechaInicio, fechaFin, profesor};
                view.getModeloCursos().addRow(filaCurso);

                // Seleccionar el nuevo curso
                int ultimaFila = view.getModeloCursos().getRowCount() - 1;
                view.getTablaCursos().setRowSelectionInterval(ultimaFila, ultimaFila);

            }
        }

        if (exito) {
            // Volver al modo consulta
            cancelar();

            // Forzar actualizacion de la vista
            view.getTablaCursos().repaint();

            // Mostrar mensaje
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("Curso ").append(esNuevo ? "creado" : "actualizado").append(" exitosamente:\n");
            mensaje.append(codigo).append(" - ").append(nombre).append("\n");
            mensaje.append("Profesor: ").append(profesor).append("\n");

            if (!modulos.isEmpty()) {
                mensaje.append("Modulos (").append(modulos.size()).append("):\n");
                for (int i = 0; i < modulos.size(); i++) {
                    mensaje.append("- ").append(modulos.get(i));
                    if (i < modulos.size() - 1) {
                        mensaje.append("\n");
                    }
                }
            } else {
                mensaje.append("Sin modulo asignados");
            }

            JOptionPane.showMessageDialog(view,
                    mensaje.toString(),
                    "Guardar Curso",
                    JOptionPane.INFORMATION_MESSAGE);

            // Si es nuevo, cargar los detalles del curso
            if (esNuevo) {
                cursoSeleccionado();
            }
        } else {
            JOptionPane.showMessageDialog(view,
                    "Error al guardar el curso. Verifique que el codigo no exista",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cancelar
    public void cancelar() {
        view.habilitarEdicion(false);

        view.habilitarBtnNuevo(true);
        view.habilitarBtnEditar(true);
        view.habilitarBtnGuardar(false);
        view.habilitarBtnCancelar(false);
        view.habilitarBtnBuscar(true);
        view.habilitarTablas(true);

        // Solo cargar detalles si hay una fila seleccionada
        int selectedRow = view.getTablaCursos().getSelectedRow();
        if (selectedRow >= 0) {
            view.cargarDetallesCurso();
        } else {
            // Si no limpiar los campos
            view.limpiarCampos();
        }

    }

    // Buscar curso
    public void buscarCurso() {
        String textoBusqueda = view.getTxtBuscar().getText().trim();

        // Buscar en el modelo
        List<Object[]> cursosEncontrados = model.buscarCursos(textoBusqueda);

        // Actualizar la tabla 
        view.getModeloCursos().setRowCount(0);
        for (Object[] curso : cursosEncontrados) {
            // Mostrar campos visibles en la tabla
            Object[] filaCurso = {curso[0], curso[1], curso[3], curso[4], curso[5]};
            view.getModeloCursos().addRow(filaCurso);
        }

        // Si no hay resultados
        if (cursosEncontrados.isEmpty() && !textoBusqueda.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "No se encontraron cursos que coincidan con: \"" + textoBusqueda + "\"",
                    "Sin resultados",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Dialog Agregar alumno
    public void mostrarFormularioAgregarAlumno() {
        int selectedRowCurso = view.getTablaCursos().getSelectedRow();

        if (selectedRowCurso < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un curso primero",
                    "Curso no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigoCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 0).toString();
        String nombreCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 1).toString();

        // TODO: podria ir en otro controller pero se mantiene, para refactor pos MVP
        // Crear Controlador buscado dialog
        AlumnoFormDialog.AlumnoBuscador buscador = new AlumnoFormDialog.AlumnoBuscador() {
            @Override
            public List<Object[]> buscarAlumnosPorDni(String dni) {
                // Buscar alumnos que no inscriptos
                List<Object[]> todosLosAlumnos = model.buscarAlumnosPorDni(dni);
                List<Object[]> alumnosDisponibles = new ArrayList<>();

                // Obtener DNIs ya inscriptos en el curso actual
                List<Object[]> alumnosEnCurso = model.obtenerAlumnosPorCurso(codigoCurso);
                Set<String> dnisEnCurso = new HashSet<>();
                for (Object[] alumno : alumnosEnCurso) {
                    dnisEnCurso.add((String) alumno[0]);
                }

                // Filtrar alumnos sin el curso
                for (Object[] alumno : todosLosAlumnos) {
                    String dniAlumno = (String) alumno[0];
                    if (!dnisEnCurso.contains(dniAlumno)) {
                        alumnosDisponibles.add(alumno);
                    }
                }

                return alumnosDisponibles;
            }
        };

        // Crear el dialog
        AlumnoFormDialog dialog = new AlumnoFormDialog(view, nombreCurso, buscador);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Object[] alumno = dialog.getDatosAlumno();

            if (alumno != null) {
                // Agregar al modelo
                boolean exito = model.agregarAlumno(codigoCurso, alumno);
                // Se agrego bien
                if (exito) {
                    // Actualizar la tabla de alumnos
                    actualizarTablaAlumnos(codigoCurso, nombreCurso);

                    JOptionPane.showMessageDialog(view,
                            "Alumno agregado exitosamente:\n"
                            + dialog.getApellido() + ", " + dialog.getNombre() + "\n"
                            + "DNI: " + dialog.getDni() + "\n"
                            + "Modulos aprobados: " + dialog.getModulosAprobados(),
                            "Alumno Agregado",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Fallo porque al buscarlo esta duplicado
                    JOptionPane.showMessageDialog(view,
                            "Error: El DNI ya esta inscripto en este curso",
                            "DNI Duplicado",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // No existe
                JOptionPane.showMessageDialog(view,
                        "Error: No se pudieron obtener los datos del alumno",
                        "Error de Datos",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Crear un Certificado
    public void generarCertificado() {
        // Validaciones seleccion
        int selectedRowAlumno = view.getTablaAlumnos().getSelectedRow();
        int selectedRowCurso = view.getTablaCursos().getSelectedRow();

        if (selectedRowCurso < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un curso primero",
                    "Curso no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedRowAlumno < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un alumno para generar su certificado",
                    "Alumno no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que no sea la fila de "Sin alumnos inscriptos"
        String dni = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 0).toString();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "No hay alumnos valido para certificar en este curso",
                    "Sin alumnos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Obtener datos del alumno
            String apellido = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 1).toString();
            String nombre = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 2).toString();
            String nombreCompleto = apellido + ", " + nombre;

            // Obtener datos del curso
            String codigoCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 0).toString();
            String nombreCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 1).toString();

            // Obtener datos completos del curso
            Object[] cursoCompleto = model.obtenerCursoPorCodigo(codigoCurso);
            List<String> modulos = new ArrayList<>();
            String horasDuracion = "120"; // Valor por defecto con la db va cambiar

            if (cursoCompleto != null && cursoCompleto.length > 6 && cursoCompleto[6] instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> modulosCurso = (List<String>) cursoCompleto[6];
                modulos = modulosCurso;
            }

            // Llamar util generador de PDF 
            boolean exito = cfp.pkg402.utils.CertificadoPDFUtil.generarCertificado(
                    nombreCompleto,
                    dni,
                    nombreCurso,
                    horasDuracion,
                    modulos
            );

            if (!exito) {
                JOptionPane.showMessageDialog(view,
                        "La generacion del certificado fue cancelada por el usuario",
                        "Operacion Cancelada",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Error al generar el certificado:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Crear lo mismo pero remplaza texto negro por blanco
    public void completarCertificadoFisico() {
        // Verificar seleccion
        int selectedRowAlumno = view.getTablaAlumnos().getSelectedRow();
        int selectedRowCurso = view.getTablaCursos().getSelectedRow();

        if (selectedRowCurso < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un curso primero",
                    "Curso no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedRowAlumno < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un alumno para generar su certificado",
                    "Alumno no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que no sea la fila de "Sin alumnos inscriptos"
        String dni = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 0).toString();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "No hay alumnos valido para certificar en este curso",
                    "Sin alumnos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Obtener datos del alumno
            String apellido = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 1).toString();
            String nombre = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 2).toString();
            String nombreCompleto = apellido + ", " + nombre;

            // Obtener datos del curso
            String codigoCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 0).toString();
            String nombreCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 1).toString();

            // Obtener datos completos 
            Object[] cursoCompleto = model.obtenerCursoPorCodigo(codigoCurso);
            List<String> modulos = new ArrayList<>();
            String horasDuracion = "120"; // Valor por defecto

            if (cursoCompleto != null && cursoCompleto.length > 6 && cursoCompleto[6] instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> modulosCurso = (List<String>) cursoCompleto[6];
                modulos = modulosCurso;
            }

            // Llamar util generador de PDF
            boolean exito = cfp.pkg402.utils.CertificadoPDFUtil.generarCertificadoInvertido(
                    nombreCompleto,
                    dni,
                    nombreCurso,
                    horasDuracion,
                    modulos
            );

            if (!exito) {
                JOptionPane.showMessageDialog(view,
                        "La generacion del certificado fue cancelada por el usuario",
                        "Operacion Cancelada",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Error al generar el certificado:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Mas de lo mismo pero otro metodo - TODO: Reutilizar y cambiar funcion generadora pos MVP FINAL
    public void generarFichaAcreditacion() {
        // Verificar que hay un alumno seleccionado
        int selectedRowAlumno = view.getTablaAlumnos().getSelectedRow();
        int selectedRowCurso = view.getTablaCursos().getSelectedRow();

        if (selectedRowCurso < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un curso primero",
                    "Curso no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedRowAlumno < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un alumno para generar su constancia de acreditacion",
                    "Alumno no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que no sea la fila de "Sin alumnos inscriptos"
        String dni = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 0).toString();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "No hay alumnos valido para acreditar en este curso",
                    "Sin alumnos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Obtener datos del alumno
            String apellido = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 1).toString();
            String nombre = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 2).toString();
            String nombreCompleto = apellido + ", " + nombre;

            // Obtener datos del curso
            String codigoCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 0).toString();
            String nombreCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 1).toString();

            // Obtener datos completos del curso
            Object[] cursoCompleto = model.obtenerCursoPorCodigo(codigoCurso);
            List<cfp.pkg402.utils.ConstanciaAcreditacionPDFUtil.ModuloInfo> modulos = new ArrayList<>();
            String horasDuracion = "120"; // Valor por defecto

            if (cursoCompleto != null && cursoCompleto.length > 6 && cursoCompleto[6] instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> modulosCurso = (List<String>) cursoCompleto[6];

                // Convertir los modulos de String a ModuloInfo
                for (int i = 0; i < modulosCurso.size(); i++) {
                    String descripcionModulo = modulosCurso.get(i);
                    int horasModulo = 12; // Horas por defecto por modulo

                    modulos.add(new cfp.pkg402.utils.ConstanciaAcreditacionPDFUtil.ModuloInfo(
                            descripcionModulo,
                            horasModulo
                    ));
                }
            }

            // Llamar al util generador de PDF de constancia de acreditacion
            boolean exito = cfp.pkg402.utils.ConstanciaAcreditacionPDFUtil.generarConstancia(
                    nombreCompleto,
                    dni,
                    nombreCurso,
                    horasDuracion,
                    modulos
            );

            if (!exito) {
                JOptionPane.showMessageDialog(view,
                        "La generacion de la constancia de acreditacion fue cancelada por el usuario",
                        "Operacion Cancelada",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Error al generar la constancia de acreditacion:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

}

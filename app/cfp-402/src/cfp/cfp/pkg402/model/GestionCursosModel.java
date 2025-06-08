package cfp.pkg402.model;

import java.util.*;

// Modelo gestion datos cursos - MOCK hasta meter cambios de DB y agregar el DAO
public class GestionCursosModel {

    // Datos en memoria
    private List<Object[]> listaCursos;
    private Map<String, List<Object[]>> alumnosPorCurso;

    public GestionCursosModel() {
        inicializarDatos();
    }

    // Cargar datos
    private void inicializarDatos() {
        inicializarCursos();
        inicializarAlumnos();
    }

    // mock
    private void inicializarCursos() {
        listaCursos = new ArrayList<>();

        // Curso 1: Electricidad Domiciliaria
        List<String> modulosElectricidad = Arrays.asList(
                "Introduccion al Curso",
                "Practica Basica",
                "Tecnicas Avanzadas",
                "Resolucion de Problemas"
        );
        listaCursos.add(new Object[]{
            "0023",
            "Electricidad Domiciliaria",
            "Instalaciones electricas residenciales, normas de seguridad y conexiones basicas. Incluye circuitos basicos, tableros electricos y medidas de proteccion.",
            "01/03/2025",
            "30/06/2025",
            "Juan Perez",
            modulosElectricidad
        });

        // Curso 2: Soldadura Basica
        List<String> modulosSoldadura = Arrays.asList(
                "Introduccion al Curso",
                "Practica Basica",
                "Tecnicas Avanzadas",
                "Resolucion de Problemas",
                "etica Profesional"
        );
        listaCursos.add(new Object[]{
            "0024",
            "Soldadura Basica",
            "Tecnicas fundamentales de soldadura, manejo de equipos y medidas de seguridad. Soldadura por arco electrico y oxiacetileno.",
            "15/03/2025",
            "16/07/2025",
            "Maria Gonzalez",
            modulosSoldadura
        });

        // Curso 3: Programacion Web
        List<String> modulosProgramacion = Arrays.asList(
                "Proyecto Final",
                "Evaluacion Teorica",
                "Evaluacion Practica",
                "Trabajo en Equipo"
        );
        listaCursos.add(new Object[]{
            "0025",
            "Programacion Web",
            "Desarrollo de sitios web con HTML, CSS y JavaScript. Bases de programacion frontend, responsive design y buenas practicas de desarrollo.",
            "01/04/2025",
            "30/08/2025",
            "Carlos Rodriguez",
            modulosProgramacion
        });
    }

    // Mock alumnos
    private void inicializarAlumnos() {
        alumnosPorCurso = new HashMap<>();

        // Alumnos para el curso "0023" - Electricidad Domiciliaria
        List<Object[]> alumnosElectricidad = new ArrayList<>();
        alumnosElectricidad.add(new Object[]{"28123456", "Morales", "Pedro", "3"});
        alumnosElectricidad.add(new Object[]{"29234567", "Ruiz", "Carmen", "4"});
        alumnosElectricidad.add(new Object[]{"30345678", "Vega", "Miguel", "2"});
        alumnosPorCurso.put("0023", alumnosElectricidad);

        // Alumnos para el curso "0024" - Soldadura Basica
        List<Object[]> alumnosSoldadura = new ArrayList<>();
        alumnosSoldadura.add(new Object[]{"31456789", "Torres", "Antonio", "3"});
        alumnosSoldadura.add(new Object[]{"32567890", "Silva", "Rosa", "4"});
        alumnosSoldadura.add(new Object[]{"33678901", "Herrera", "Jose", "1"});
        alumnosSoldadura.add(new Object[]{"34789012", "Castro", "Maria", "2"});
        alumnosPorCurso.put("0024", alumnosSoldadura);

        // Alumnos para el curso "0025" - Programacion Web
        List<Object[]> alumnosProgramacion = new ArrayList<>();
        alumnosProgramacion.add(new Object[]{"30123456", "Martinez", "Ana", "3"});
        alumnosProgramacion.add(new Object[]{"31234567", "Garcia", "Luis", "3"});
        alumnosProgramacion.add(new Object[]{"32345678", "Lopez", "Elena", "2"});
        alumnosProgramacion.add(new Object[]{"33456789", "Fernandez", "Roberto", "1"});
        alumnosProgramacion.add(new Object[]{"35890123", "Jimenez", "Laura", "3"});
        alumnosPorCurso.put("0025", alumnosProgramacion);
    }

    // Getters
    public List<Object[]> obtenerTodosCursos() {
        return new ArrayList<>(listaCursos);
    }

    public Object[] obtenerCursoPorCodigo(String codigo) {
        for (Object[] curso : listaCursos) {
            if (curso[0].equals(codigo)) {
                // Crear copia profunda del curso incluyendo lista de modulos
                Object[] copia = new Object[curso.length];
                System.arraycopy(curso, 0, copia, 0, curso.length - 1);

                // Copiar lista de modulos
                @SuppressWarnings("unchecked")
                List<String> modulosOriginales = (List<String>) curso[6];
                copia[6] = new ArrayList<>(modulosOriginales);

                return copia;
            }
        }
        return null;
    }

    public List<Object[]> obtenerAlumnosPorCurso(String codigoCurso) {
        List<Object[]> alumnos = alumnosPorCurso.get(codigoCurso);
        return alumnos != null ? new ArrayList<>(alumnos) : new ArrayList<>();
    }

    public List<String> obtenerModulosPorCurso(String codigoCurso) {
        Object[] curso = obtenerCursoPorCodigo(codigoCurso);
        if (curso != null && curso.length > 6) {
            @SuppressWarnings("unchecked")
            List<String> modulos = (List<String>) curso[6];
            return new ArrayList<>(modulos);
        }
        return new ArrayList<>();
    }

    // Set
    public boolean agregarCurso(Object[] curso) {
        // Verificar que no exista el codigo
        if (obtenerCursoPorCodigo((String) curso[0]) != null) {
            return false;
        }

        // Validar que tenga modulos
        if (curso.length < 7 || !(curso[6] instanceof List)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        List<String> modulos = (List<String>) curso[6];
        if (modulos.isEmpty()) {
            return false;
        }

        boolean resultado = listaCursos.add(curso);
        if (resultado) {
            // Inicializar lista vacia de alumnos para el nuevo curso
            alumnosPorCurso.put((String) curso[0], new ArrayList<>());
        }
        return resultado;
    }

    public boolean agregarAlumno(String codigoCurso, Object[] alumno) {
        List<Object[]> alumnos = alumnosPorCurso.computeIfAbsent(codigoCurso, k -> new ArrayList<>());

        // Verificar que no exista el DNI en este curso
        String dniNuevo = (String) alumno[0];
        for (Object[] alumnoExistente : alumnos) {
            if (alumnoExistente[0].equals(dniNuevo)) {
                return false; // Ya existe
            }
        }

        return alumnos.add(alumno);
    }

    // Update
    public boolean actualizarCurso(String codigo, Object[] cursoActualizado) {
        // Validar que tenga modulos
        if (cursoActualizado.length < 7 || !(cursoActualizado[6] instanceof List)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        List<String> modulos = (List<String>) cursoActualizado[6];
        if (modulos.isEmpty()) {
            return false;
        }

        for (int i = 0; i < listaCursos.size(); i++) {
            if (listaCursos.get(i)[0].equals(codigo)) {
                listaCursos.set(i, cursoActualizado);
                return true;
            }
        }
        return false;
    }

    public boolean actualizarModulosCurso(String codigoCurso, List<String> nuevosModulos) {
        if (nuevosModulos == null || nuevosModulos.isEmpty()) {
            return false;
        }

        for (Object[] curso : listaCursos) {
            if (curso[0].equals(codigoCurso)) {
                curso[6] = new ArrayList<>(nuevosModulos);
                return true;
            }
        }
        return false;
    }

    // Search
    public List<Object[]> buscarCursos(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return obtenerTodosCursos();
        }

        List<Object[]> resultado = new ArrayList<>();
        String terminoLower = termino.toLowerCase();

        for (Object[] curso : listaCursos) {
            String codigo = ((String) curso[0]).toLowerCase();
            String nombre = ((String) curso[1]).toLowerCase();
            String instructor = ((String) curso[5]).toLowerCase();

            // Buscar tambien en modulos
            @SuppressWarnings("unchecked")
            List<String> modulos = (List<String>) curso[6];
            boolean encontradoEnModulos = false;
            for (String modulo : modulos) {
                if (modulo.toLowerCase().contains(terminoLower)) {
                    encontradoEnModulos = true;
                    break;
                }
            }

            if (codigo.contains(terminoLower)
                    || nombre.contains(terminoLower)
                    || instructor.contains(terminoLower)
                    || encontradoEnModulos) {
                resultado.add(curso);
            }
        }

        return resultado;
    }

    public List<Object[]> buscarAlumnosPorDni(String dni) {
        List<Object[]> resultado = new ArrayList<>();

        if (dni == null || dni.trim().isEmpty()) {
            // Si no hay DNI, devolver todos los alumnos
            for (Map.Entry<String, List<Object[]>> entry : alumnosPorCurso.entrySet()) {
                List<Object[]> alumnos = entry.getValue();
                for (Object[] alumno : alumnos) {
                    // Verificar si ya esta en el resultado (evitar duplicados)
                    boolean yaAgregado = false;
                    for (Object[] alumnoResultado : resultado) {
                        if (alumnoResultado[0].equals(alumno[0])) {
                            yaAgregado = true;
                            break;
                        }
                    }
                    if (!yaAgregado) {
                        Object[] alumnoEncontrado = {
                            alumno[0], // DNI
                            alumno[1], // Apellido
                            alumno[2], // Nombre
                            alumno[3] // Modulos aprobados
                        };
                        resultado.add(alumnoEncontrado);
                    }
                }
            }
            return resultado;
        }

        String dniLimpio = dni.trim();

        // Buscar en todos los cursos
        for (Map.Entry<String, List<Object[]>> entry : alumnosPorCurso.entrySet()) {
            List<Object[]> alumnos = entry.getValue();

            for (Object[] alumno : alumnos) {
                String dniAlumno = (String) alumno[0];

                // Busqueda exacta o parcial
                if (dniAlumno.contains(dniLimpio)) {
                    // Verificar si ya esta en el resultado
                    boolean yaAgregado = false;
                    for (Object[] alumnoResultado : resultado) {
                        if (alumnoResultado[0].equals(alumno[0])) {
                            yaAgregado = true;
                            break;
                        }
                    }

                    if (!yaAgregado) {
                        Object[] alumnoEncontrado = {
                            alumno[0], // DNI
                            alumno[1], // Apellido
                            alumno[2], // Nombre
                            alumno[3] // Modulos aprobados
                        };
                        resultado.add(alumnoEncontrado);
                    }
                }
            }
        }

        return resultado;
    }

    // Generar automaticamente un id - MOCK
    public String generarNuevoCodigo() {
        int maxCodigo = 0;
        for (Object[] curso : listaCursos) {
            try {
                int codigo = Integer.parseInt((String) curso[0]);
                if (codigo > maxCodigo) {
                    maxCodigo = codigo;
                }
            } catch (NumberFormatException e) {
                // Ignorar codigos no numericos
            }
        }
        return String.format("%04d", maxCodigo + 1);
    }

    // Valida el formato basico de fecha
    public boolean validarFormatoFecha(String fecha) {
        if (fecha == null || fecha.length() != 10) {
            return false;
        }

        String[] partes = fecha.split("/");
        if (partes.length != 3) {
            return false;
        }

        try {
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int anio = Integer.parseInt(partes[2]);

            return dia >= 1 && dia <= 31 && mes >= 1 && mes <= 12 && anio >= 1900;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validarModulos(List<String> modulos) {
        if (modulos == null || modulos.isEmpty()) {
            return false;
        }

        // Verificar que ningun modulo este vacio
        for (String modulo : modulos) {
            if (modulo == null || modulo.trim().isEmpty()) {
                return false;
            }
        }

        // Verificar que no haya modulos duplicados
        Set<String> modulosUnicos = new HashSet<>();
        for (String modulo : modulos) {
            String moduloLimpio = modulo.trim().toLowerCase();
            if (modulosUnicos.contains(moduloLimpio)) {
                return false; // Duplicado encontrado
            }
            modulosUnicos.add(moduloLimpio);
        }

        return true;
    }

}

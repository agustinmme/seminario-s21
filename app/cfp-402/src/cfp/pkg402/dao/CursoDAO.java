package cfp.pkg402.dao;

import cfp.pkg402.dao.DatabaseConfig;
import cfp.pkg402.model.Curso;
import cfp.pkg402.model.AlumnoInscripcion;
import cfp.pkg402.model.Modulo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CursoDAO {

    // Agregar curso
    public boolean insertar(Curso curso, List<String> modulos) {
        String sqlCurso = "INSERT INTO cursos (codigo, nombre, descripcion, fecha_inicio, fecha_fin, profesor_id, especialidad, horas_totales, cupo_maximo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlCurso, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setString(1, curso.getCodigo());
                stmt.setString(2, curso.getNombre());
                stmt.setString(3, curso.getDescripcion());
                stmt.setDate(4, new java.sql.Date(curso.getFechaInicio().getTime()));
                stmt.setDate(5, new java.sql.Date(curso.getFechaFin().getTime()));
                stmt.setInt(6, curso.getProfesorId());
                stmt.setString(7, curso.getEspecialidad());
                stmt.setInt(8, curso.getHorasTotales());
                stmt.setInt(9, curso.getCupoMaximo());
                
                int filasAfectadas = stmt.executeUpdate();
                
                if (filasAfectadas > 0) {
                    // Obtener el ID generado
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        int cursoId = rs.getInt(1);
                        curso.setId(cursoId);
                        
                        // Insertar modulos si los hay
                        if (modulos != null && !modulos.isEmpty()) {
                            insertarModulosCurso(conn, cursoId, modulos);
                        }
                        
                        conn.commit();
                        return true;
                    }
                }
                
                conn.rollback();
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al insertar curso: " + e.getMessage());
        }
        
        return false;
    }
    
    // Actulizar curso
    public boolean actualizar(Curso curso, List<String> modulos) {
        String sql = "UPDATE cursos SET codigo = ?, nombre = ?, descripcion = ?, fecha_inicio = ?, fecha_fin = ?, profesor_id = ?, especialidad = ?, horas_totales = ?, cupo_maximo = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, curso.getCodigo());
                stmt.setString(2, curso.getNombre());
                stmt.setString(3, curso.getDescripcion());
                stmt.setDate(4, new java.sql.Date(curso.getFechaInicio().getTime()));
                stmt.setDate(5, new java.sql.Date(curso.getFechaFin().getTime()));
                stmt.setInt(6, curso.getProfesorId());
                stmt.setString(7, curso.getEspecialidad());
                stmt.setInt(8, curso.getHorasTotales());
                stmt.setInt(9, curso.getCupoMaximo());
                stmt.setInt(10, curso.getId());
                
                int filasAfectadas = stmt.executeUpdate();
                
                if (filasAfectadas > 0) {
                    // Actualizar modulos
                    eliminarModulosCurso(conn, curso.getId());
                    if (modulos != null && !modulos.isEmpty()) {
                        insertarModulosCurso(conn, curso.getId(), modulos);
                    }
                    
                    conn.commit();
                    return true;
                }
                
                conn.rollback();
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar curso: " + e.getMessage());
        }
        
        return false;
    }

    // Agregar modulo al curso
    private void insertarModulosCurso(Connection conn, int cursoId, List<String> modulos) throws SQLException {
        String sqlModulo = "INSERT IGNORE INTO modulos (nombre) VALUES (?)";
        String sqlCursoModulo = "INSERT INTO curso_modulos (curso_id, modulo_id, orden_en_curso) " +
                               "SELECT ?, id, ? FROM modulos WHERE nombre = ?";
        
        try (PreparedStatement stmtModulo = conn.prepareStatement(sqlModulo);
             PreparedStatement stmtCursoModulo = conn.prepareStatement(sqlCursoModulo)) {
            
            for (int i = 0; i < modulos.size(); i++) {
                String nombreModulo = modulos.get(i);
                
                // Insertar modulo si no existe
                stmtModulo.setString(1, nombreModulo);
                stmtModulo.executeUpdate();
                
                // Relacionar curso con modulo
                stmtCursoModulo.setInt(1, cursoId);
                stmtCursoModulo.setInt(2, i + 1);
                stmtCursoModulo.setString(3, nombreModulo);
                stmtCursoModulo.executeUpdate();
            }
        }
    }
    
    // Delete modulo, auxiliar para cuando se actualiza el curso
    private void eliminarModulosCurso(Connection conn, int cursoId) throws SQLException {
        String sql = "DELETE FROM curso_modulos WHERE curso_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cursoId);
            stmt.executeUpdate();
        }
    }
    // Get all cursos
    public List<Curso> obtenerTodos() {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT c.id, c.codigo, c.nombre, c.descripcion, c.fecha_inicio, c.fecha_fin, "
                + "c.profesor_id, c.especialidad, c.horas_totales, c.cupo_maximo, "
                + "CONCAT(p.nombre, ' ', p.apellido) as nombre_profesor "
                + "FROM cursos c "
                + "JOIN personas p ON c.profesor_id = p.id "
                + "WHERE p.rol = 'Profesor' "
                + "ORDER BY c.codigo";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            // Primero cargar todos los cursos basicos
            while (rs.next()) {
                Curso curso = mapearCurso(rs);
                cursos.add(curso);
            }

            // Luego cargar los modulos para cada curso
            for (Curso curso : cursos) {
                curso.setModulos(obtenerModulosPorCurso(curso.getId()));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener cursos: " + e.getMessage());
        }

        return cursos;
    }

    // Get curso por codigo
    public Curso obtenerPorCodigo(String codigo) {
        String sql = "SELECT c.id, c.codigo, c.nombre, c.descripcion, c.fecha_inicio, c.fecha_fin, "
                + "c.profesor_id, c.especialidad, c.horas_totales, c.cupo_maximo, "
                + "CONCAT(p.nombre, ' ', p.apellido) as nombre_profesor "
                + "FROM cursos c "
                + "JOIN personas p ON c.profesor_id = p.id "
                + "WHERE c.codigo = ? AND p.rol = 'Profesor'";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Curso curso = mapearCurso(rs);
                curso.setModulos(obtenerModulosPorCurso(curso.getId()));
                return curso;
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener curso por codigo: " + e.getMessage());
        }

        return null;
    }

    // Buscar curso por codigo, nombre, profesor o modulos
    public List<Curso> buscarCursos(String termino) {
        List<Curso> cursos = new ArrayList<>();

        if (termino == null || termino.trim().isEmpty()) {
            return obtenerTodos();
        }

        String sql = "SELECT DISTINCT c.id, c.codigo, c.nombre, c.descripcion, c.fecha_inicio, c.fecha_fin, "
                + "c.profesor_id, c.especialidad, c.horas_totales, c.cupo_maximo, "
                + "CONCAT(p.nombre, ' ', p.apellido) as nombre_profesor "
                + "FROM cursos c "
                + "JOIN personas p ON c.profesor_id = p.id "
                + "LEFT JOIN curso_modulos cm ON c.id = cm.curso_id "
                + "LEFT JOIN modulos m ON cm.modulo_id = m.id "
                + "WHERE (c.codigo LIKE ? OR c.nombre LIKE ? OR "
                + "CONCAT(p.nombre, ' ', p.apellido) LIKE ? OR m.nombre LIKE ?) "
                + "AND p.rol = 'Profesor' "
                + "ORDER BY c.codigo";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            String patron = "%" + termino + "%";
            stmt.setString(1, patron);
            stmt.setString(2, patron);
            stmt.setString(3, patron);
            stmt.setString(4, patron);

            ResultSet rs = stmt.executeQuery();

            // Primero cargar todos los cursos basicos
            while (rs.next()) {
                Curso curso = mapearCurso(rs);
                cursos.add(curso);
            }

            // Luego cargar los modulos para cada curso
            for (Curso curso : cursos) {
                curso.setModulos(obtenerModulosPorCurso(curso.getId()));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar cursos: " + e.getMessage());
        }

        return cursos;
    }

    // Crear codigo del curso
    public String generarNuevoCodigo() {
        String sql = "SELECT MAX(CAST(codigo AS UNSIGNED)) as max_codigo FROM cursos WHERE codigo REGEXP '^[0-9]+$'";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int maxCodigo = rs.getInt("max_codigo");
                return String.format("%04d", maxCodigo + 1);
            }

        } catch (SQLException e) {
            System.err.println("Error al generar nuevo codigo: " + e.getMessage());
        }

        return "0001"; 
    }

    // Buscar alumsno del cruso
    public List<AlumnoInscripcion> obtenerAlumnosPorCurso(String codigoCurso) {
        List<AlumnoInscripcion> alumnos = new ArrayList<>();
        String sql = "SELECT p.dni, p.apellido, p.nombre, i.estado, i.certificado_generado, "
                + "COUNT(DISTINCT cm.modulo_id) as total_modulos_curso, "
                + "COUNT(DISTINCT CASE WHEN am.estado = 'aprobado' THEN am.modulo_id END) as modulos_aprobados "
                + "FROM inscripciones i "
                + "JOIN personas p ON i.alumno_id = p.id "
                + "JOIN cursos c ON i.curso_id = c.id "
                + "LEFT JOIN curso_modulos cm ON c.id = cm.curso_id "
                + "LEFT JOIN alumno_modulos am ON i.id = am.inscripcion_id AND cm.modulo_id = am.modulo_id "
                + "WHERE c.codigo = ? AND p.rol = 'Alumno' "
                + "GROUP BY i.id, p.dni, p.apellido, p.nombre, i.estado, i.certificado_generado "
                + "ORDER BY p.apellido, p.nombre";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoCurso);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AlumnoInscripcion alumno = new AlumnoInscripcion();
                alumno.setDni(rs.getString("dni"));
                alumno.setApellido(rs.getString("apellido"));
                alumno.setNombre(rs.getString("nombre"));
                alumno.setModulosAprobados(rs.getInt("modulos_aprobados"));
                alumno.setEstado(rs.getString("estado"));
                alumno.setCertificadoGenerado(rs.getBoolean("certificado_generado"));
                alumnos.add(alumno);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener alumnos por curso: " + e.getMessage());
            e.printStackTrace();
        }

        return alumnos;
    }

    // Buscar los alumnos que no esten en el curso
    public List<AlumnoInscripcion> buscarAlumnosDisponibles(String dni, String codigoCurso) {
        List<AlumnoInscripcion> alumnos = new ArrayList<>();
        String sql = "SELECT p.dni, p.apellido, p.nombre "
                + "FROM personas p "
                + "WHERE p.rol = 'Alumno' "
                + "AND (? IS NULL OR ? = '' OR p.dni LIKE ?) "
                + "AND p.id NOT IN ("
                + "    SELECT i.alumno_id "
                + "    FROM inscripciones i "
                + "    JOIN cursos c ON i.curso_id = c.id "
                + "    WHERE c.codigo = ?"
                + ") "
                + "ORDER BY p.apellido, p.nombre";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            String patron = dni != null && !dni.trim().isEmpty() ? "%" + dni + "%" : null;
            stmt.setString(1, dni);
            stmt.setString(2, dni);
            stmt.setString(3, patron);
            stmt.setString(4, codigoCurso);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AlumnoInscripcion alumno = new AlumnoInscripcion();
                alumno.setDni(rs.getString("dni"));
                alumno.setApellido(rs.getString("apellido"));
                alumno.setNombre(rs.getString("nombre"));
                alumno.setModulosAprobados(0);
                alumnos.add(alumno);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar alumnos disponibles: " + e.getMessage());
        }

        return alumnos;
    }

    // Inscribe un alumno a un curso
    // Cuenta con 2 pasos, agregar al alumno e asignar los moudlos aprobados...
    public boolean inscribirAlumno(String codigoCurso, String dniAlumno, int modulosAprobados) {
        String sqlInscripcion = "INSERT INTO inscripciones (alumno_id, curso_id) "
                + "SELECT p.id, c.id "
                + "FROM personas p, cursos c "
                + "WHERE p.dni = ? AND c.codigo = ? AND p.rol = 'Alumno'";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Inscribir al alumno al curso
                int inscripcionId = -1;
                int cursoId = -1;

                try (PreparedStatement stmt = conn.prepareStatement(sqlInscripcion, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, dniAlumno);
                    stmt.setString(2, codigoCurso);

                    int filasAfectadas = stmt.executeUpdate();
                    if (filasAfectadas > 0) {
                        ResultSet rs = stmt.getGeneratedKeys();
                        if (rs.next()) {
                            inscripcionId = rs.getInt(1);
                        }
                    }
                }

                if (inscripcionId == -1) {
                    conn.rollback();
                    return false;
                }

                // Obtener el ID del curso
                String sqlCursoId = "SELECT id FROM cursos WHERE codigo = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlCursoId)) {
                    stmt.setString(1, codigoCurso);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        cursoId = rs.getInt("id");
                    }
                }

                // Si hay modulos aprobados se agregan
                if (modulosAprobados > 0 && cursoId != -1) {
                    registrarModulosAprobadosEspecificos(conn, inscripcionId, cursoId, modulosAprobados);
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Error al inscribir alumno: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // uPdate los modulos aprobados de un alumno especifico
    public boolean actualizarModulosAlumno(String codigoCurso, String dniAlumno, int nuevosModulosAprobados) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Obtener la inscripcion y el curso ID
                String sqlInscripcion = "SELECT i.id, c.id as curso_id "
                        + "FROM inscripciones i "
                        + "JOIN personas p ON i.alumno_id = p.id "
                        + "JOIN cursos c ON i.curso_id = c.id "
                        + "WHERE p.dni = ? AND c.codigo = ?";

                int inscripcionId = -1;
                int cursoId = -1;

                try (PreparedStatement stmt = conn.prepareStatement(sqlInscripcion)) {
                    stmt.setString(1, dniAlumno);
                    stmt.setString(2, codigoCurso);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        inscripcionId = rs.getInt("id");
                        cursoId = rs.getInt("curso_id");
                    }
                }

                if (inscripcionId == -1) {
                    conn.rollback();
                    return false;
                }

                // Eliminar registros previos de modulos para esta inscripcion
                String sqlEliminar = "DELETE FROM alumno_modulos WHERE inscripcion_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlEliminar)) {
                    stmt.setInt(1, inscripcionId);
                    stmt.executeUpdate();
                }

                // Si hay modulos aprobados, registrar los especificos del curso
                if (nuevosModulosAprobados > 0) {
                    registrarModulosAprobadosEspecificos(conn, inscripcionId, cursoId, nuevosModulosAprobados);
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar modulos del alumno: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Get informacion detallada de modulos aprobados por un alumno
    public List<String> obtenerModulosAprobadosPorAlumno(String codigoCurso, String dniAlumno) {
        List<String> modulosAprobados = new ArrayList<>();
        String sql = "SELECT m.nombre "
                + "FROM alumno_modulos am "
                + "JOIN inscripciones i ON am.inscripcion_id = i.id "
                + "JOIN modulos m ON am.modulo_id = m.id "
                + "JOIN personas p ON i.alumno_id = p.id "
                + "JOIN cursos c ON i.curso_id = c.id "
                + "JOIN curso_modulos cm ON c.id = cm.curso_id AND m.id = cm.modulo_id "
                + "WHERE p.dni = ? AND c.codigo = ? AND am.estado = 'aprobado' "
                + "ORDER BY cm.orden_en_curso";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dniAlumno);
            stmt.setString(2, codigoCurso);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modulosAprobados.add(rs.getString("nombre"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener modulos aprobados por alumno: " + e.getMessage());
            e.printStackTrace();
        }

        return modulosAprobados;
    }

    // Get lista de modulos de un curso
    public List<Modulo> obtenerModulosInfoPorCurso(int cursoId) {
        List<Modulo> modulos = new ArrayList<>();

        String sql = "SELECT m.nombre, m.descripcion, "
                + "COALESCE(cm.horas_asignadas, m.horas_duracion, 12) as horas "
                + "FROM curso_modulos cm "
                + "JOIN modulos m ON cm.modulo_id = m.id "
                + "WHERE cm.curso_id = ? "
                + "ORDER BY cm.orden_en_curso";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cursoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Modulo modulo = new Modulo();
                    modulo.setNombre(rs.getString("nombre"));
                    modulo.setHoras(rs.getInt("horas"));
                    modulo.setDescripcion(rs.getString("descripcion"));
                    modulos.add(modulo);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener modulos info para curso ID " + cursoId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return modulos;
    }

    // Get horas totales del curso
    public int obtenerHorasTotalesReales(int cursoId) {
        String sql = "SELECT COALESCE(SUM(COALESCE(cm.horas_asignadas, m.horas_duracion, 12)), 0) as total_horas "
                + "FROM curso_modulos cm "
                + "JOIN modulos m ON cm.modulo_id = m.id "
                + "WHERE cm.curso_id = ?";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cursoId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_horas");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener horas totales para curso ID " + cursoId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    // Auxiliar para pasar datos de una fila a un obj
    private Curso mapearCurso(ResultSet rs) throws SQLException {
        Curso curso = new Curso();
        curso.setId(rs.getInt("id"));
        curso.setCodigo(rs.getString("codigo"));
        curso.setNombre(rs.getString("nombre"));
        curso.setDescripcion(rs.getString("descripcion"));
        curso.setFechaInicio(rs.getDate("fecha_inicio"));
        curso.setFechaFin(rs.getDate("fecha_fin"));
        curso.setProfesorId(rs.getInt("profesor_id"));
        curso.setEspecialidad(rs.getString("especialidad"));
        curso.setHorasTotales(rs.getInt("horas_totales"));
        curso.setCupoMaximo(rs.getInt("cupo_maximo"));
        curso.setNombreProfesor(rs.getString("nombre_profesor"));
        return curso;
    }

    // Cargar los modulos aprobados por numeros 
    // Si el curso tiene 10 modulos y la persona pone 5, se cargan los primeros 5...
    private void registrarModulosAprobadosEspecificos(Connection conn, int inscripcionId, int cursoId, int cantidadAprobados) throws SQLException {
        // Primero verificamos cuantos modulos tiene realmente el curso
        String sqlContarModulos = "SELECT COUNT(*) as total FROM curso_modulos WHERE curso_id = ?";
        int totalModulosCurso = 0;

        try (PreparedStatement stmt = conn.prepareStatement(sqlContarModulos)) {
            stmt.setInt(1, cursoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalModulosCurso = rs.getInt("total");
            }
        }

        // Validar que no se aprueben mas modulos de los que tiene el curso
        int modulosAMarcar = Math.min(cantidadAprobados, totalModulosCurso);

        if (modulosAMarcar > 0) {
            // Insertar los primeros X modulos como aprobados segun el orden del curso
            String sqlModuloAprobado = "INSERT INTO alumno_modulos (inscripcion_id, modulo_id, estado, fecha_aprobacion) "
                    + "SELECT ?, cm.modulo_id, 'aprobado', CURRENT_TIMESTAMP "
                    + "FROM curso_modulos cm "
                    + "WHERE cm.curso_id = ? "
                    + "ORDER BY cm.orden_en_curso "
                    + "LIMIT ?";

            try (PreparedStatement stmt = conn.prepareStatement(sqlModuloAprobado)) {
                stmt.setInt(1, inscripcionId);
                stmt.setInt(2, cursoId);
                stmt.setInt(3, modulosAMarcar);

                int insertados = stmt.executeUpdate();
                System.out.println("Modulos marcados como aprobados: " + insertados + " de " + cantidadAprobados + " solicitados");
            }
        }
    }

    // Get Modulos x curso
    private List<String> obtenerModulosPorCurso(int cursoId) {
        List<String> modulos = new ArrayList<>();
        String sql = "SELECT m.nombre "
                + "FROM curso_modulos cm "
                + "JOIN modulos m ON cm.modulo_id = m.id "
                + "WHERE cm.curso_id = ? "
                + "ORDER BY cm.orden_en_curso";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cursoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    modulos.add(rs.getString("nombre"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener modulos para curso ID " + cursoId + ": " + e.getMessage());
        }

        return modulos;
    }
}

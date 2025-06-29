package cfp.pkg402.dao;

import cfp.pkg402.dao.DatabaseConfig;
import cfp.pkg402.model.Persona;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    // Get tods
    public List<Persona> obtenerTodas() {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT * FROM personas ORDER BY apellido, nombre";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                personas.add(mapearPersona(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener personas: " + e.getMessage());
        }

        return personas;
    }

    // Get Profes
    public List<Persona> obtenerProfesores() {
        List<Persona> profesores = new ArrayList<>();
        String sql = "SELECT * FROM personas WHERE rol = 'Profesor' ORDER BY apellido, nombre";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                profesores.add(mapearPersona(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener profesores: " + e.getMessage());
        }

        return profesores;
    }

    // Get alumnos
    public List<Persona> obtenerAlumnos() {
        List<Persona> alumnos = new ArrayList<>();
        String sql = "SELECT * FROM personas WHERE rol = 'Alumno' ORDER BY apellido, nombre";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                alumnos.add(mapearPersona(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener alumnos: " + e.getMessage());
        }

        return alumnos;
    }

    // Buscar persona por DNI
    public List<Persona> buscarPorDni(String dni) {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT * FROM personas WHERE dni LIKE ?";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + dni + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                personas.add(mapearPersona(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar por DNI: " + e.getMessage());
        }

        return personas;
    }

    // Buscar persona por nombre o apellido
    public List<Persona> buscarPorNombre(String texto) {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT * FROM personas WHERE (nombre LIKE ? OR apellido LIKE ?)";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            String patron = "%" + texto + "%";
            stmt.setString(1, patron);
            stmt.setString(2, patron);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                personas.add(mapearPersona(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar por nombre: " + e.getMessage());
        }

        return personas;
    }

    // Get persona x id, TODO: Se podria sacar pero por ahi mas adelante se le agrega algo...
    public Persona obtenerPorId(int id) {
        String sql = "SELECT * FROM personas WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearPersona(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener persona por ID: " + e.getMessage());
        }

        return null;
    }

    // Get persona x DNI, TODO: Se podria sacar pero por ahi mas adelante se le agrega algo...
    public Persona obtenerPorDni(String dni) {
        String sql = "SELECT * FROM personas WHERE dni = ?";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dni);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearPersona(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener persona por DNI: " + e.getMessage());
        }

        return null;
    }

    // Agregar nueva persona
    public boolean insertar(Persona persona) {
        String sql = "INSERT INTO personas (dni, nombre, apellido, fecha_nacimiento, direccion, telefono, email, rol) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, persona.getDni());
            stmt.setString(2, persona.getNombre());
            stmt.setString(3, persona.getApellido());

            if (persona.getFechaNacimiento() != null) {
                stmt.setDate(4, new java.sql.Date(persona.getFechaNacimiento().getTime()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setString(5, persona.getDireccion());
            stmt.setString(6, persona.getTelefono());
            stmt.setString(7, persona.getEmail());
            stmt.setString(8, persona.getRol());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    persona.setId(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar persona: " + e.getMessage());
        }

        return false;
    }

    // Actualiza una persona existente
    public boolean actualizar(Persona persona) {
        String sql = "UPDATE personas SET dni = ?, nombre = ?, apellido = ?, fecha_nacimiento = ?, direccion = ?, telefono = ?, email = ?, rol = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, persona.getDni());
            stmt.setString(2, persona.getNombre());
            stmt.setString(3, persona.getApellido());

            if (persona.getFechaNacimiento() != null) {
                stmt.setDate(4, new java.sql.Date(persona.getFechaNacimiento().getTime()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setString(5, persona.getDireccion());
            stmt.setString(6, persona.getTelefono());
            stmt.setString(7, persona.getEmail());
            stmt.setString(8, persona.getRol());
            stmt.setInt(9, persona.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar persona: " + e.getMessage());
        }

        return false;
    }

    // Verifica si existe un DNI
    public boolean existeDni(String dni) {
        String sql = "SELECT COUNT(*) FROM personas WHERE dni = ?";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dni);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar DNI: " + e.getMessage());
        }

        return false;
    }

    // Auxiliar para pasar datos de una fila a un obj
    private Persona mapearPersona(ResultSet rs) throws SQLException {
        Persona persona = new Persona();
        persona.setId(rs.getInt("id"));
        persona.setDni(rs.getString("dni"));
        persona.setNombre(rs.getString("nombre"));
        persona.setApellido(rs.getString("apellido"));
        persona.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
        persona.setDireccion(rs.getString("direccion"));
        persona.setTelefono(rs.getString("telefono"));
        persona.setEmail(rs.getString("email"));
        persona.setRol(rs.getString("rol"));
        return persona;
    }
}

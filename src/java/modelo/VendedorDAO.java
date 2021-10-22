
package modelo;

import conexionsql.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class VendedorDAO {
    Connection con;
    Conexion cn = new Conexion();
    PreparedStatement ps;
    ResultSet rs;
public Vendedor Validar(String email, String pass) {
        String sql="select * from vendedor where Email=? and Password=?";
        Vendedor v=new Vendedor();
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, pass);
            rs = ps.executeQuery();
            while (rs.next()) {
                v.setId(rs.getInt(1));
                v.setDni(rs.getString(2));
                v.setNombres(rs.getString(3));
                v.setDireccion(rs.getString(4));
                v.setEmail(rs.getString(5));
                v.setPass(rs.getString(6));
            }
        } catch (Exception e) {
        }
        return v;        
    }
}

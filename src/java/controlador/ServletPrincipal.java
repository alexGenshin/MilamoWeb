package controlador;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelo.CarroCompras;
import modelo.Cliente;
import modelo.ClienteDAO;
import modelo.Producto;
import modelo.ProductoDAO;
import modelo.Vendedor;
import modelo.VendedorDAO;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import conexionsql.Conexion;
public class ServletPrincipal extends HttpServlet {
                
    Cliente cl = new Cliente();
    ClienteDAO cldao = new ClienteDAO();
    ProductoDAO prsql = new ProductoDAO();
    Producto p = new Producto();
    List<Producto> productos = new ArrayList<>();
    List<CarroCompras> listcar = new ArrayList<>();
    Vendedor ve = new Vendedor();
    VendedorDAO vdao = new VendedorDAO();
    String logueo = "Iniciar Sesion";
    String correo = "Iniciar Sesion";

    int cont;
    double totalPago = 0.0;
    int cant = 1;
    int idpro;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.setAttribute("logueo", logueo);
        session.setAttribute("correo", correo);
        String op = request.getParameter("op");
        productos = prsql.listar();
        switch (op) {
            case "Validar":
                String email = request.getParameter("txtemail");
                String pass = request.getParameter("txtpass");
                
                cl = cldao.Validar(email, pass);
                ve = vdao.Validar(email, pass);
                if (cl.getId() != 0) {
                    logueo = cl.getNombres();
                    correo = cl.getEmail();
                    response.sendRedirect("ServletPrincipal?op=home");
                } else if (ve.getId() != 0) {
                    logueo = ve.getNombres();
                    correo = ve.getEmail();
                    response.sendRedirect("ServletPrincipal?op=NuevoProducto");
                }

                break;
            case "Registrar":
                String nom = request.getParameter("txtnom");
                String dni = request.getParameter("txtdni");
                String em = request.getParameter("txtemail");
                String pas = request.getParameter("txtpass");
                String dir = request.getParameter("txtdire");
                cl.setNombres(nom);
                cl.setDni(dni);
                cl.setEmail(em);
                cl.setPass(pas);
                cl.setDireccion(dir);
                cldao.AgregarCliente(cl);
                request.getRequestDispatcher("ServletPrincipal?op=home").forward(request, response);
                break;
            case "Salir":
                listcar = new ArrayList();
                cl = new Cliente();
                ve = new Vendedor();
                session.invalidate();
                logueo = "Iniciar Sesion";
                correo = "Iniciar Sesion";
                request.getRequestDispatcher("ServletPrincipal?op=home").forward(request, response);
                break;
            case "AgregarCarro":
                int idProd = Integer.parseInt(request.getParameter("id"));
                p = prsql.listarId(idProd);
                cont = cont + 1;
                CarroCompras car = new CarroCompras();
                car.setItem(cont);
                car.setIdProducto(p.getId());
                car.setNombres(p.getNombre());
                car.setDescripcion(p.getDescripcion());
                car.setCantidad(cant);
                car.setSubTotal(cant * p.getPrecio());
                listcar.add(car);
                request.setAttribute("cont", listcar.size());

                request.getRequestDispatcher("ServletPrincipal?op=home").forward(request, response);
                break;
            case "Carro":
                totalPago = 0.0;
                request.setAttribute("ListaPr", listcar);
                for (int i = 0; i < listcar.size(); i++) {
                    totalPago = totalPago + listcar.get(i).getSubTotal();
                }
                request.setAttribute("totalPago", totalPago);
                request.getRequestDispatcher("carroCompras.jsp").forward(request, response);
                break;
            case "GuardarProducto":
                ArrayList<String> pro = new ArrayList<>();
                try {
                    FileItemFactory factory = new DiskFileItemFactory();
                    ServletFileUpload fileUpload = new ServletFileUpload(factory);
                    List items = fileUpload.parseRequest(request);
                    for (int i = 0; i < items.size(); i++) {
                        FileItem fileItem = (FileItem) items.get(i);
                        if (!fileItem.isFormField()) {
                            File file = new File("D:\\wamp64\\www\\carrito1" + fileItem.getName());
                            fileItem.write(file);
                            p.setImagen("http://localhost/carrito1/" + fileItem.getName());
                        } else {
                            pro.add(fileItem.getString());
                        }
                    }
                    p.setNombre(pro.get(0));
                    p.setDescripcion(pro.get(1));
                    p.setPrecio(Double.parseDouble(pro.get(2)));
                    p.setStock(Integer.parseInt(pro.get(3)));
                    prsql.AgregarNuevoProducto(p);

                } catch (Exception e) {
                    System.err.println("" + e);
                }
                request.getRequestDispatcher("ServletPrincipal?op=NuevoProducto").forward(request, response);
                break;
            case "editarproducto":
                idpro = Integer.parseInt(request.getParameter("id"));
                p=prsql.listarId(idpro);
                request.setAttribute("producto", p);
                request.getRequestDispatcher("ServletPrincipal?op=NuevoProducto").forward(request, response);
                 break;
            case "actualizar":
                 ArrayList<String> pro1 = new ArrayList<>();
                try {
                    FileItemFactory factory = new DiskFileItemFactory();
                    ServletFileUpload fileUpload = new ServletFileUpload(factory);
                    List items = fileUpload.parseRequest(request);
                    for (int i = 0; i < items.size(); i++) {
                        FileItem fileItem = (FileItem) items.get(i);
                        if (!fileItem.isFormField()) {
                            File file = new File("C:\\wamp64\\www\\carrito1" + fileItem.getName());
                            fileItem.write(file);
                            p.setImagen("http://localhost/carrito1/" + fileItem.getName());
                        } else {
                            pro1.add(fileItem.getString());
                        }
                    }
                    p.setNombre(pro1.get(0));
                    p.setDescripcion(pro1.get(1));
                    p.setPrecio(Double.parseDouble(pro1.get(2)));
                    p.setStock(Integer.parseInt(pro1.get(3)));
                    p.setId(idpro);
                    prsql.actualizar(p);

                } catch (Exception e) {
                    System.err.println("" + e);
                }
                request.getRequestDispatcher("ServletPrincipal?op=NuevoProducto").forward(request, response);
                break;
            case "eliminarproducto":
                idpro = Integer.parseInt(request.getParameter("id"));
                prsql.delete(idpro);
                request.getRequestDispatcher("ServletPrincipal?op=NuevoProducto").forward(request, response);
                break;
            case "NuevoProducto":
                request.setAttribute("productos", productos);
                request.getRequestDispatcher("./dashboard.jsp").forward(request, response);
                break;
            default:
                request.setAttribute("productos", productos);
                request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
        
        
        try {
String email = request.getParameter("txtemail");
String pass = request.getParameter("txtpass");
          Conexion cn = new Conexion();               
     PreparedStatement pst = cn.getConnection().prepareStatement("select * from cliente where Email=? and Password=?");
        pst.setString(1, email);
        pst.setString(2, pass);
        ResultSet rs=pst.executeQuery();
        if(rs.next()){
            HttpSession sesionOk=request.getSession();
            sesionOk.setAttribute("nombre", rs.getString(3));
            sesionOk.setAttribute("correo", rs.getString(5));
            sesionOk.setAttribute("rol", rs.getString(7));
            cn.getConnection().close();
            request.getRequestDispatcher("index.jsp").forward(request, response);
            

        }else{
            String msg="USUARIO O PASSWORD INCORRECTOS";
             cn.getConnection().close();
            request.setAttribute("msg", msg);
            request.getRequestDispatcher("Vistas/Login.jsp").forward(request, response);
        
        }

           
        } catch (Exception e) {
            System.out.println("Error de Login" + e);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

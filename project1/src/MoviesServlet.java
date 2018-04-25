import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */ 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
			Connection dbcon = dataSource.getConnection();
			Statement statement = dbcon.createStatement();
			String query = "SELECT * FROM movies";
			ResultSet rs = statement.executeQuery(query);
			JsonArray jsonArray = new JsonArray();
			
			while(rs.next()) {
				String movieId = rs.getString(1);
				String movieName = rs.getString(2);
				String movieYear = rs.getString(3);
				String director = rs.getString(4);
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_id", movieId);
				jsonObject.addProperty("movie_name", movieName);
				jsonObject.addProperty("movie_year", movieYear);
				jsonObject.addProperty("movie_director", director);
				
				jsonArray.add(jsonObject);
			}
			out.write(jsonArray.toString());
			response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
		}catch(Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			response.setStatus(500);
		}
        out.close();

    }
}

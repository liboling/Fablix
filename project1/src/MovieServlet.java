

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MovieServlet
 */
@WebServlet("/movies")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public MovieServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // change this to your own mysql username and password
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
		
        // set response mime type
        response.setContentType("text/html"); 

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fabflix</title></head>");
        
        
        try {
        		Class.forName("com.mysql.jdbc.Driver").newInstance();
        		// create database connection
        		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        		// declare statement
        		Statement statement = connection.createStatement();
        		// prepare query
        		String query = "select sub.*, ratings.rating \n" + 
        				"from\n" + 
        				"(\n" + 
        				"	select m.id, title, year, director, group_concat(s.name, ','), group_concat(g.name,',') \n" + 
        				"	from movies m, stars_in_movies sim, stars s, genres g, genres_in_movies gim\n" + 
        				"	where m.id = sim.movieId and sim.starId = s.id and m.id = gim.movieId and gim.genreId = g.id\n" + 
        				"	group by m.id\n" + 
        				") sub, ratings\n" + 
        				"where sub.id = ratings.movieId LIMIT 10";
        		// execute query
        		ResultSet resultSet = statement.executeQuery(query);

        		out.println("<body>");
        		out.println("<h1>MovieDB Movies</h1>");
        		
        		out.println("<table border>");
        		
        		// add table header row
        		out.println("<tr>");
        		out.println("<td>id</td>");
        		out.println("<td>title</td>");
        		out.println("<td>year</td>");
        		out.println("<td>director</td>");
        		out.println("<td>list of stars</td>");
        		out.println("<td>list of genres</td>");
        		out.println("<td>ratings</td>");
        		out.println("</tr>");
        		
        		// add a row for every star result
        		while (resultSet.next()) {
        			// get a star from result set
        			String movieId = resultSet.getString("id");
        			String title = resultSet.getString("title");
        			String year = resultSet.getString("year");
        			String director = resultSet.getString("director");
        			String listOfStars = resultSet.getString(5);
        			String listOfGenres = resultSet.getString(6);
        			String rating = resultSet.getString("rating");
        			
        			out.println("<tr>");
        			out.println("<td>" + movieId + "</td>");
        			out.println("<td>" + title + "</td>");
        			out.println("<td>" + year + "</td>");
        			out.println("<td>" + director + "</td>");
        			out.println("<td>" + listOfStars + "</td>");
        			out.println("<td>" + listOfGenres + "</td>");
        			out.println("<td>" + rating + "</td>");
        			out.println("</tr>");
        		}
        		
        		out.println("</table>");
        		
        		out.println("</body>");
        		
        		resultSet.close();
        		statement.close();
        		connection.close();
        		
        } catch (Exception e) {
        		/*
        		 * After you deploy the WAR file through tomcat manager webpage,
        		 *   there's no console to see the print messages.
        		 * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
        		 * 
        		 * To view the last n lines (for example, 100 lines) of messages you can use:
        		 *   tail -100 catalina.out
        		 * This can help you debug your program after deploying it on AWS.
        		 */
        		e.printStackTrace();
        		
        		out.println("<body>");
        		out.println("<p>");
        		out.println("Exception in doGet: " + e.getMessage());
        		out.println("</p>");
        		out.print("</body>");
        }
        
        out.println("</html>");
        out.close();
        
	}


}

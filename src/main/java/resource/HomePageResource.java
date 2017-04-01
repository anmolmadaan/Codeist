package resource;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import bean.Super;
import bean.Tile;
import bean.User;
import bean.test;
import pdao.HomePage;
import service.GeneralServices;
import service.objectupload;

@Path("/homepage")
public class HomePageResource {

	/*@GET
	@Path("/history/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Tile> getUserHistory(@PathParam("username") String username)
	{
    	return new HomePage().getHistory(username);
	}*/
	
//	@GET
//	@Path("/project/{username}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public ArrayList<Tile> getProjects(@PathParam("username") String username)
//	{
//		return new HomePage().getProjects(username);
//	}
	
	@GET
	@Path("/forum/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Tile> getQuestion(@Context HttpServletRequest req,@PathParam("username") String username)
	{
		return new HomePage().getQuestions(username);
//		return new HomePage().getQuestions(req.getSession().getAttribute("username").toString());
	
	}
	/*
	@GET
	@Path("/projects-feed")
	@Produces(MediaType.APPLICATION_JSON)
	public Super getProject(@Context HttpServletRequest req)
	{
		Super s = new Super();
		ArrayList<Tile> altile = new HomePage().getProjects(req.getSession().getAttribute("username").toString());
		s.setAlsuper(altile);
		s.setLogged("logged");
    	return s; //req.getSession().getAttribute("username").toString());
	}
	
	
	@GET
	@Path("/topuser")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<User> getTopUser()
	{
    	return new HomePage().topUsers();
	}
	@POST
	@Path("/img")
	@Produces(MediaType.APPLICATION_JSON)
	public void img(test test)
	{
    	try {
			new objectupload().upload(test.getImg());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}

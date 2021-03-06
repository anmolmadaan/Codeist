package service;

import java.awt.image.BufferedImage;
import static com.mongodb.client.model.Filters.*;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.imageio.ImageIO;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import bean.Acknowledgement;
import bean.Notifications;
import bean.Project;
import bean.Signup;
import bean.Tag;
import bean.Tile;
import dao.RegionDao;
import dao.UserDao;
import interfaces.service.AtoSCon;
import interfaces.service.StoACon;
import interfaces.service.StoAcknowCon;
import org.apache.tomcat.util.codec.binary.Base64;

public class GeneralServices {
	static final HashMap<Object, Tile> hm = new HashMap<Object, Tile>();

	public String atosmethod(AtoSCon<String> ats, ArrayList<String> al) {
		return ats.convertAtoS(al);
	}

	public Acknowledgement stoacknowmethod(StoAcknowCon stacknow, String acknow) {
		return stacknow.convertStoAcknow(acknow);
	}

public Tile returnTile(Document d,String source,String subject)
{
    	Tile tl = new Tile();
	    tl.setId(d.get("_id").toString());
	    ArrayList<String> altwo = new ArrayList<String>();
	    if(altwo.size() < 1)
	    	altwo.add("interesting");
	    tl.setSource(altwo);
	    tl.setSubject(subject);
	    tl.setUsername(d.getString("username"));
	    tl.setPositioncount(1);
	    if(subject.equals("project")){
	    	tl.setTitle(d.getString("title"));
	    	tl.setUrl_title(GeneralServices.spaceRemover(d.getString("title")));
			tl.setDescription(d.getString("description"));
			tl.setTags((ArrayList<String>)d.get("tags"));
	    	Document doc = (Document) d.get("info");
//	    	if((ArrayList<String>)doc.get("upvotes") != null){
	    		tl.setUpvotes((ArrayList<String>) doc.get("upvotes"));
	//    	}
//	    	if(doc.get("downvotes") != null){
	    		tl.setDownvotes((ArrayList<String>) doc.get("downvotes"));
//	    	}
	    
	    }
	    else{
	    	tl.setQuestion(d.getString("question"));
	    	tl.setUrl_title(GeneralServices.spaceRemover(d.getString("question")));
	        tl.setDescription(d.getString("description"));
	   tl.setTags((ArrayList<String>)d.get("tags"));
	   //"/questions/{id}"
	   // 	tl.setUpvotecount(d.getLong("upvotecount"));
	   // 	tl.setDownvotecount(d.getLong("downvotecount"));
	   Document doc = (Document) d.get("info");
//   	if((ArrayList<String>)doc.get("upvotes") != null){
   		tl.setUpvotes((ArrayList<String>) doc.get("upvotes"));
//    	}
//   	if(doc.get("downvotes") != null){
   		tl.setDownvotes((ArrayList<String>) doc.get("downvotes"));
//   	}
   
	    }
		tl.setViewcount(1);
		return tl;
	}

	public Acknowledgement response(Notifications notify) {
		Acknowledgement acknow = new Acknowledgement();
		//acknow.setMessage(notify.getMsg());
		if(notify!=null){
			acknow.setMessage(notify.getMsg());
			acknow.setLoggedin(true);
		}
		else
			acknow.setLoggedin(false);
		// {
		// if(s!=null&&s.equals("insert"))
		// {
		// Acknowledgement ac2 = new Acknowledgement();
		// ac2.setMatchedCount("1");
		// ac2.setModifiedCount("1");
		// ac2.setUpsertedId("0");
		// ac2.setMessage("success");
		// return ac2;
		// }
		//
		// else if(s!=null&&s.equals("already exist"))
		// { Acknowledgement ac2 = new Acknowledgement();
		// ac2.setMatchedCount("2");
		// ac2.setModifiedCount("1");
		// ac2.setUpsertedId("0");
		// ac2.setMessage("success");
		// return ac2;
		// }
		// else if(s!=null)
		// { Acknowledgement ac2 = new Acknowledgement();
		// String sa [] =
		// s.substring(s.indexOf("{")+1,s.indexOf("}")).split(",");
		// ac2.setMatchedCount(sa[0]);
		// ac2.setModifiedCount(sa[1]);
		// ac2.setUpsertedId(sa[2]);
		// return ac2;
		// }
		// else {
		// Acknowledgement ac2 = new Acknowledgement();
		// ac2.setMessage("user is not logged in");
		// return ac2;
		// }
		return acknow;
	}

	public static String spaceRemover(String s) {
		s = s.replaceAll(" ", "-");
		return s;
	}

	public static String spaceAdder(String s) {
		s = s.replaceAll("-", " ");
		return s;
	}

	public static String get_SHA_256_SecurePassword(String username, String passwordToHash) {
		byte[] salt = username.getBytes();
		String generatedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt);
			byte[] bytes = md.digest(passwordToHash.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}

	public static List<Project> nullProject() {
		List<Project> pro = new ArrayList<Project>();
		pro.add(new Project());
		return pro;
	}

	public static Date getCurrentDate() {
		Date date = null;
		try {
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			String strDate = sdfDate.format(new Date());
			SimpleDateFormat sdf1 = new SimpleDateFormat();
			sdf1.applyPattern("yyyy-MM-dd HH:mm:ss.SSS");
			date = sdf1.parse(strDate);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String linkDecrypter(byte[] _bytes) {
		String file_string = "";

		for (int i = 0; i < _bytes.length; i++) {
			file_string += (char) _bytes[i];
		}

		return file_string;
	}

	public static String linkEncryptCreator(String encrypt1, String encrypt2) {
		String x;
		// encrypt2 == date
		x = Base64.encodeBase64URLSafeString((encrypt1 + "|" + encrypt2).getBytes());
		System.out.println(x);
		// System.out.println(openFileToString(Base64.decodeBase64(x)));
		return x;
	}

	public static String urlGenerator(Notifications notify, String id, String name) {
		name = spaceRemover(name);
		String module = notify.getMsg();
		String url = "/" + module + "/" + id + "/" + name;
		return url;
	}

	public static HashMap<String, String> urlDegenerate(String url) {
		url = url.substring(1);
		int index = url.indexOf("/") + 1;
		String module = url.substring(0, url.indexOf("/"));
		String id = url.substring(index, url.lastIndexOf("/"));
		String name = url.substring((url.lastIndexOf("/") + 1), url.length());
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("name", name);
		hm.put("module", module);
		hm.put("id", id);
		return hm;
	}

	public void update(String base) {
		BufferedImage image = null;
		byte[] imageByte;

		byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base);

		// write the image to a file
		try (BufferedOutputStream bw = new BufferedOutputStream(
				new FileOutputStream("C:/Users/dell 1/Documents/Advance java/img.png"))) {
			bw.write(imageBytes);
			// no need to close it.
			// bw.close();
			System.out.println("Done");

		} catch (IOException e) {

			e.printStackTrace();

		}
	}

	public String signup(Signup signup) {
		String username = signup.getUsername();
		String emailid = signup.getEmailid();
		String password = signup.getPassword();
		System.out.println("password" + "::" + password);
		password = GeneralServices.get_SHA_256_SecurePassword(username, password);
		Date date = GeneralServices.getCurrentDate();

		long dateepoch = date.getTime();
		String hashed1 = linkEncryptCreator(username, String.valueOf(dateepoch));
		String hashed2 = linkEncryptCreator(emailid, String.valueOf(dateepoch));
		// enter for verification
		new UserDao().signupUser(username, password, emailid, date);
		try {
			SendEmail.SendSimple(emailid, hashed1, hashed2);
		} catch (Exception e) {
			System.out.println("mail send");
		}
		return "success";
	}

	public static Long datetoepoch(Date date) {
		return date.getTime();
	}

	// public void contributor(String projectid,ArrayList<String> alcontributor)
	// {
	// ArrayList<String> emailid = new
	// UserDao().getContributorEmailid(alcontributor);
	// for(int i=0;i<alcontributor.size();i++)
	// { String hashed1 =
	// linkEncryptCreator("y*"+alcontributor.get(i),projectid);
	// String hashed2 = linkEncryptCreator("n*"+alcontributor.get(i),projectid);
	//
	// SendEmail.SendContributor(emailid.get(i),hashed1,hashed2);
	// }
	// this method will add problem matching with instiute tags into homepage of
	// institue
	public void addProblem(FindIterable<Document> doc, String question_id) {
		// doc is document of institute
		MongoCollection<Document> tc = new DatabaseServices().getDb().getCollection("institute");
		for (Document d : doc) {
			String id = d.get("_id").toString();
			ObjectId oid = new ObjectId(id);
			tc.updateMany(eq("_id", oid), new Document("$addToSet", new Document("question_id", question_id)));
		}
	}

public void setRating(String username)
{
	MongoCollection<Document> tc = new DatabaseServices().getDb().getCollection("institute");
	Document doc = tc.find(eq("username",username)).first();
Long pupvote = doc.getLong("project_upvote");
Long pdownvote = doc.getLong("project_downvote");
Long qupvote = doc.getLong("qa_upvote");
Long qdownvote = doc.getLong("qa_downvote");

Long score  = 10*(pupvote+qupvote);
score = score - 5*pdownvote;
score = score - 5*qdownvote;
tc.updateOne(eq("username",username),new Document("$set",new Document("rating",new Long(score))));
}

public void setUserRating(String username)
{
	MongoCollection<Document> tc = new DatabaseServices().getDb().getCollection("testuserdata");
	Document doc = tc.find(eq("username",username)).first();
Long pupvote = doc.getLong("project_upvote");
Long pdownvote = doc.getLong("project_downvote");
Long qupvote = doc.getLong("qa_upvote");
Long qdownvote = doc.getLong("qa_downvote");

Long score  = 10*(pupvote+qupvote);
score = score - 5*pdownvote;
score = score - 5*qdownvote;
tc.updateOne(eq("username",username),new Document("$set",new Document("rating",new Long(score))));
	
}

public void ratingupdate(String i,String username){
	
	if(i.equals("i")){
		new GeneralServices().setUserRating(username);
	}
	else if(i.equals("c")){
		new GeneralServices().setRating(username);
	}
}


}

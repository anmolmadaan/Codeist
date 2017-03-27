package dao;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import bean.Acknowledgement;
import bean.Answer;
import bean.Notifications;
import bean.Question;
import service.DatabaseServices;
import service.GeneralServices;
import service.NotificationService;
import static com.mongodb.client.model.Filters.*;
import java.util.ArrayList;

import javax.management.Notification;
import javax.servlet.http.HttpServletRequest;

public class QADao {
	
	MongoCollection<Document> tc = new DatabaseServices().getDb().getCollection("testqa");
    public Acknowledgement insertQuestion(Question question, HttpServletRequest req)
	{   String userfromsession = req.getSession().getAttribute("username").toString();
	    Document doc2 = tc.find(and(eq("username",userfromsession),eq("question",question.getQuestion()))).first();
		if(doc2==null)
		{Document info=new Document().append("upvotes",new ArrayList<String>()).append("downvotes",new ArrayList<String>());
			Document doc = new Document("username",userfromsession)
	    		.append("question",question.getQuestion())
	    		.append("date",question.getDate())
	    		.append("info",info)
	    		.append("featured_points", question.getFeatured_points());
	       tc.insertOne(doc);
	    
	    String id = tc.find(and(eq("username",userfromsession),eq("question",question.getQuestion()))).first().get("_id").toString();

	    // for userdata 
	    new UserDao().moduleIDAdder(Notifications.QUESTIONMODULE,userfromsession, id);
	    
	    // for url
	    String url = GeneralServices.urlGenerator(Notifications.QUESTIONMODULE, id, question.getQuestion());
	    tc.updateOne(and(eq("username",userfromsession),eq("question",question.getQuestion())),new Document("$set",new Document("url",url)));
	    Acknowledgement acknow = new Acknowledgement();
	    acknow.setModifiedCount("1");
	    acknow.setMatchedCount("0");
		return acknow;
		}
	    else{
			Acknowledgement acknow = new Acknowledgement();
		     acknow.setModifiedCount("0");
		     acknow.setMatchedCount("1");
	    	return acknow;
	    }
	}
	
	@SuppressWarnings("unchecked")
	public Question getQuestion(String question)
	{
	Document doc = tc.find(eq("question",question)).first();	
	Question quest = new Question();
	quest.setUsername(doc.getString("username"));
	quest.setDate(doc.getDate("date"));
	quest.setTags((ArrayList<String>)doc.get("tags"));
	quest.setUrl(doc.getString("url"));
	Document document=(Document) doc.get("info");
	
	quest.setDownvotes((ArrayList<String>)document.get("downvotes"));
	quest.setUpvotes((ArrayList<String>)document.get("upvotes"));
	quest.setFeatured_points(doc.getLong("featured_points"));
	ArrayList<Answer> alansw = new ArrayList<Answer>();
	ArrayList<Document> aldo = (ArrayList<Document>) doc.get("answers");
	for(Document d:aldo)
	{	Answer answer =  new Answer();
		answer.setUsername(d.getString("username"));
		answer.setAnswer(d.getString("answer"));
		answer.setDate(d.getDate("date"));
		Document in=(Document) d.get("info");
		answer.setDownvotes((ArrayList<String>)in.get("downvotes"));
		answer.setUpvotes((ArrayList<String>)in.get("upvotes"));
		answer.setFeatured_points(d.getLong("featured_points"));
		alansw.add(answer);
	}
	quest.setAnswer(alansw);
	return quest;
	}
	
	public Acknowledgement insertAnswer(Answer answer,HttpServletRequest req)
	{   Document info=new Document().append("upvotes",new ArrayList<String>()).append("downvotes",new ArrayList<String>());	
    	String userfromsession = req.getSession().getAttribute("username").toString();
	    String q_id = tc.find(and(eq("username",answer.getUsername()),eq("question",answer.getQuestion()))).first().get("_id").toString();
	    Document doc = new Document("username",userfromsession)
	    		.append("answer",answer.getAnswer())
	    		.append("date",GeneralServices.getCurrentDate())
	    		.append("info",info)
	    		.append("featured_points", answer.getFeatured_points());
        String acknow =tc.updateOne(eq("question",answer.getQuestion()),new Document("$push",new Document("answers",doc))).toString();
        Acknowledgement acknowledge = new GeneralServices().response(acknow);
        //answer.getUsername is owner of question
        new NotificationService().answerNotification(answer.getUsername(),answer.getQuestion(),q_id,req.getSession().getAttribute("username").toString(),answer.getAnswer(),Notifications.QUESTIONSOLVED);
        return acknowledge;
	}
	
	@SuppressWarnings("unchecked")
	public Acknowledgement upques(String username,Question question,String user){
		//username ques owner //user who votes
		Document d = tc.find(and(eq("username",username),eq("question",question.getQuestion()))).first();
		Document infodetails=(Document)d.get("info");
			ArrayList<String> up=(ArrayList<String>)infodetails.get("upvotes");
			ArrayList<String> down=(ArrayList<String>)infodetails.get("downvotes");
			if(up!=null){
				if(!up.contains(user)){
					if(down!=null &&down.contains(user)){
						down.remove(user);
						 String acknow2 = tc.updateOne(and(eq("username", username),eq("question",question.getQuestion())),new Document("$set",new Document("info.downvotes",down))).toString();	 
					}
				up.add(user);
				String acknow2 = tc.updateOne(and(eq("username", username),eq("question",question.getQuestion())),new Document("$set",new Document("info.upvotes",up))).toString();	 
		//public void voteNotification(String username,String pqname,String pqid,String commitername,Notifications notify)
		String q_id = tc.find(and(eq("username",question.getUsername()),eq("question",question.getQuestion()))).first().get("_id").toString();
				   
				new NotificationService().voteNotification(username,question.getQuestion(),q_id,user,Notifications.UPVOTESQUESTION);
				 return new GeneralServices().response(acknow2);}
				else{
					up.remove(user);
					String acknow2 = tc.updateOne(and(eq("username", username),eq("question",question.getQuestion())),new Document("$set",new Document("info.upvotes",up))).toString();	 	 
				}
			return new GeneralServices().response("already exist");
			}
			else{
				if(down!=null && down.contains(user)){
					down.remove(user);
					 String acknow2 = tc.updateOne(and(eq("username", username),eq("question",question.getQuestion())),new Document("$set",new Document("info.downvotes",down))).toString();	 
				}
				up=new ArrayList<String>();
				up.add(user);
				 String acknow2 = tc.updateOne(and(eq("username", username),eq("question",question.getQuestion())),new Document("$set",new Document("info.upvotes",up))).toString();	 
				 String q_id = tc.find(and(eq("username",question.getUsername()),eq("question",question.getQuestion()))).first().get("_id").toString();
				 new NotificationService().voteNotification(username,question.getQuestion(),q_id,user,Notifications.UPVOTESQUESTION);
			return new GeneralServices().response(acknow2);
				
			}
	}
	@SuppressWarnings("unchecked")
	public Acknowledgement downques(String username,Question question,String user){
		Document d = tc.find(and(eq("username",username),eq("question",question.getQuestion()))).first();
		Document infodetails=(Document)d.get("info");
			ArrayList<String> up=(ArrayList<String>)infodetails.get("upvotes");
			ArrayList<String> down=(ArrayList<String>)infodetails.get("downvotes");
			
			if(down!=null){
				if(!down.contains(user)){
					if(up!=null && up.contains(user)){
						up.remove(user);
						 String acknow2 = tc.updateOne(and(eq("username", username),eq("question",question.getQuestion())),new Document("$set",new Document("info.upvotes",up))).toString();	 
					}
				down.add(user);
				String acknow2 = tc.updateOne(and(eq("username", username),eq("question",question.getQuestion())),new Document("$set",new Document("info.downvotes",down))).toString();	 
				 String q_id = tc.find(and(eq("username",question.getUsername()),eq("question",question.getQuestion()))).first().get("_id").toString();
				 new NotificationService().voteNotification(username,question.getQuestion(),q_id,user,Notifications.DOWNVOTESQUESTION);
				 return new GeneralServices().response(acknow2);}
				else{
					down.remove(user);
					String acknow2 = tc.updateOne(and(eq("username", username),eq("question",question.getQuestion())),new Document("$set",new Document("info.downvotes",down))).toString();	 	 
				}
			return new GeneralServices().response("already exist");
			}
			else{
				if(up!=null && up.contains(user)){
					up.remove(user);
					 String acknow2 = tc.updateOne(and(eq("username", username),eq("question",question.getQuestion())),new Document("$set",new Document("info.upvotes",up))).toString();	 
				}
				down=new ArrayList<String>();
				down.add(user);
				 String acknow2 = tc.updateOne(and(eq("username", username),eq("question",question.getQuestion())),new Document("$set",new Document("info.downvotes",down))).toString();	 
				 String q_id = tc.find(and(eq("username",question.getUsername()),eq("question",question.getQuestion()))).first().get("_id").toString();
				 new NotificationService().voteNotification(username,question.getQuestion(),q_id,user,Notifications.DOWNVOTESQUESTION);
				 return new GeneralServices().response(acknow2);
				
			}
	}


	@SuppressWarnings("unchecked")
	public Acknowledgement upanswer(String username,Answer answer,String user){
		//answer  username:whos answer ,question :full question
		//username whose question
		ArrayList<String> up=null,down=null;
		int i=0;
		Document d = tc.find(and(eq("username",username),eq("question",answer.getQuestion()))).first();
		ArrayList<Document> aldo = (ArrayList<Document>) d.get("answers");
		for(Document d1:aldo){
			if(d1.get("username").equals(answer.getUsername())){
			    Document infodetails=(Document)d1.get("info");
				up=(ArrayList<String>)infodetails.get("upvotes");
				down=(ArrayList<String>)infodetails.get("downvotes");
				break;
					}
			i++;
		}
		String ups="answers."+i+".info.upvotes";
		String downs="answers."+i+".info.downvotes";
			if(up!=null){
				if(!up.contains(user)){
					if(down!=null &&down.contains(user)){
						down.remove(user);
						 String acknow2 = tc.updateOne(and(eq("username", username),eq("question",answer.getQuestion())),new Document("$set",new Document(downs,down))).toString();	 
					}
				up.add(user);
				String acknow2 = tc.updateOne(and(eq("username", username),eq("question",answer.getQuestion())),new Document("$set",new Document(ups,up))).toString();	 
		//public void voteNotification(String username,String pqname,String pqid,String commitername,Notifications notify)
		String q_id = tc.find(and(eq("username",username),eq("question",answer.getQuestion()))).first().get("_id").toString();
				   
				new NotificationService().voteNotification(username,answer.getQuestion(),q_id,user,Notifications.UPVOTESANSWER);
				 return new GeneralServices().response(acknow2);}
				else{
					up.remove(user);
					String acknow2 = tc.updateOne(and(eq("username", username),eq("question",answer.getQuestion())),new Document("$set",new Document(ups,up))).toString();	 	 
				}
			return new GeneralServices().response("already exist");
			}
			else{
				if(down!=null && down.contains(user)){
					down.remove(user);
					 String acknow2 = tc.updateOne(and(eq("username", username),eq("question",answer.getQuestion())),new Document("$set",new Document(downs,down))).toString();	 
				}
				up=new ArrayList<String>();
				up.add(user);
				 String acknow2 = tc.updateOne(and(eq("username", username),eq("question",answer.getQuestion())),new Document("$set",new Document(ups,up))).toString();	 
				 String q_id = tc.find(and(eq("username",username),eq("question",answer.getQuestion()))).first().get("_id").toString();
				 new NotificationService().voteNotification(username,answer.getQuestion(),q_id,user,Notifications.UPVOTESANSWER);
			return new GeneralServices().response(acknow2);
				
			}
	}
	@SuppressWarnings("unchecked")
	public Acknowledgement downanswer(String username,Answer answer,String user){
		ArrayList<String> up=null,down=null;
		int i=0;
		Document d = tc.find(and(eq("username",username),eq("question",answer.getQuestion()))).first();
		ArrayList<Document> aldo = (ArrayList<Document>) d.get("answers");
		for(Document d1:aldo){
			if(d1.get("username").equals(answer.getUsername())){
				 Document infodetails=(Document)d1.get("info");
			up=(ArrayList<String>)infodetails.get("upvotes");
			 down=(ArrayList<String>)infodetails.get("downvotes");
			break;
						}
				i++;
			}
		String ups="answers."+i+".info.upvotes";
		String downs="answers."+i+".info.downvotes";
			if(down!=null){
				if(!down.contains(user)){
					if(up!=null && up.contains(user)){
						up.remove(user);
						 String acknow2 = tc.updateOne(and(eq("username", username),eq("question",answer.getQuestion())),new Document("$set",new Document(ups,up))).toString();	 
					}
				down.add(user);
				String acknow2 = tc.updateOne(and(eq("username", username),eq("question",answer.getQuestion())),new Document("$set",new Document(downs,down))).toString();	 
				 String q_id = tc.find(and(eq("username",username),eq("question",answer.getQuestion()))).first().get("_id").toString();
				 new NotificationService().voteNotification(username,answer.getQuestion(),q_id,user,Notifications.DOWNVOTESQUESTION);
				 return new GeneralServices().response(acknow2);}
				else{
					down.remove(user);
					String acknow2 = tc.updateOne(and(eq("username", username),eq("question",answer.getQuestion())),new Document("$set",new Document(downs,down))).toString();	 	 
				}
			return new GeneralServices().response("already exist");
			}
			else{
				if(up!=null && up.contains(user)){
					up.remove(user);
					 String acknow2 = tc.updateOne(and(eq("username", username),eq("question",answer.getQuestion())),new Document("$set",new Document(ups,up))).toString();	 
				}
				down=new ArrayList<String>();
				down.add(user);
				 String acknow2 = tc.updateOne(and(eq("username", username),eq("question",answer.getQuestion())),new Document("$set",new Document(downs,down))).toString();	 
				 String q_id = tc.find(and(eq("username",username),eq("question",answer.getQuestion()))).first().get("_id").toString();
				 new NotificationService().voteNotification(username,answer.getQuestion(),q_id,user,Notifications.DOWNVOTESQUESTION);
				 return new GeneralServices().response(acknow2);
				
			}
	}
	
}

//to see local db: http://localhost:8888/_ah/admin/

package shopoffice;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class ShopOfficeServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
		
		/* USE THIS WHEN YOUR DATASTORE USES NUMBERS TO REPRESENT AND ID/NAME */
		//String id = req.getParameter("id");
		//int iid = -1;
		//try{ iid = Integer.parseInt(id); } catch(Exception ex) {}
		
		/* USE THIS WHEN YOUR DATASTORE USES STRINGS TO REPRESENT AND ID/NAME */
		String skey = req.getParameter("key");
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		if(skey != null && skey.trim().length() > 0) {
			//------------if there is a key specified that means access to /score?key=****
			try {
				//---------get data with the key from datastore
				//---------createKey(entity-kind, id/name)
				Key key = KeyFactory.createKey("Scores", skey);
				Entity entity = datastore.get(key);
				JSONObject jentity = new JSONObject(entity.getProperties());
				try {
					jentity.put("key", entity.getKey().getName());
				} catch (JSONException e) {}
				resp.getWriter().print(jentity);		
			} catch (EntityNotFoundException e) {
				resp.getWriter().print("{\"status\": \"entity not found\"}");
			}
		} else {
			//------------if there is no key specified that means access to just /scores
			//------------make a query called "Scores" with sorting date
 			Query q = new Query("Scores");
			q.addSort("date", SortDirection.DESCENDING);
			//------------don't care about this line
			PreparedQuery pq = datastore.prepare(q);
			//------------make an array for json data
			JSONArray jresults = new JSONArray();
			List<Entity> entities = pq.asList(FetchOptions.Builder.withLimit(25).offset(0));
			for(Entity entity : entities) {
				entity.setProperty("key", entity.getKey().getName());
				jresults.put(entity.getProperties());
			}
			resp.getWriter().print(jresults);
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		//---------set type as json format
		resp.setContentType("application/json");
		
		//---------prepare/get the data to store
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		int hour = cal.get(Calendar.HOUR);
		
		String key = day + "" + (month+1) + "" + year + "" + hour; 
		
		String office = req.getParameter("office");
		String shop = req.getParameter("shop");
		
		//---------convert string to integer, if there is no value, return -1
		int ioffice = -1;
		try{ ioffice = Integer.parseInt(office); } catch(Exception ex) {}
		int ishop = -1;
		try{ ishop = Integer.parseInt(shop); } catch(Exception ex) {}

		//---------set entity (db)
		if(ioffice > -1 && ishop > -1) {
			//---------entity (entity-kind, id/name) if there is no id/name passed, id/names will be increment numbers
			Entity entity = new Entity("Scores", key);
			entity.setProperty("office", ioffice);
			entity.setProperty("shop", ishop);
			entity.setProperty("date", date);			
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			datastore.put(entity);
			
			//---------convert entity to json format
			JSONObject jentity = new JSONObject(entity.getProperties());

			//---------add status to json
			try {
				jentity.put("key", entity.getKey().getName());
				jentity.put("status", "saved");
			} catch (JSONException e) { }
			
			//---------resp.getWriter().print(val) returns val to browser eg.)data of call back function(data) in js
			resp.getWriter().print(jentity);
			return;
		}
		resp.getWriter().print("{\"status\": \"not saved\"}");
	}
}

/**
 * This class contains the file i/o to read and write to the userlist json file
 * @author CISC213.N81
 */

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class userListIO {
	public static void loadUsersFromJson() {
		try {
        	JSONParser parser = new JSONParser();
        	Object jsonData = parser.parse(new FileReader("userList.json"));

        	// Check if the JSON data is an object basically check if it has the correct attributes
        	if (jsonData instanceof JSONObject) {
        		JSONObject json = (JSONObject) jsonData;
        		JSONArray userArray = (JSONArray) json.get("Users");
        		for (Object obj : userArray) {
        			JSONObject userJson = (JSONObject) obj;
        			User user = createUserFromJson(userJson);
        			if (user != null) {
        				Player.userList.add(user);
        			}
        		}
        	}else if (jsonData instanceof JSONArray) {
        		// Handle array of objects
        		JSONArray userArray = (JSONArray) jsonData;
        		for (Object obj : userArray) {
        			JSONObject userJson = (JSONObject) obj;
        			User user = createUserFromJson(userJson);
        			if (user != null) {
        				Player.userList.add(user);
        			}
        		}
        	}
        } catch (Exception e) {
        	System.err.println("Error parsing JSON file: " + e.getMessage());
        }
	}

	public static User createUserFromJson(JSONObject userJson) {
    	try {
    		String username = (String) userJson.get("Username");
    		String firstName = (String) userJson.get("First name");
    		String lastName = (String) userJson.get("Last name");
    		String email = (String) userJson.get("Email");
    		String password = (String) userJson.get("Password");
    		Number userIDNumber = (Number) userJson.get("userID");
    		int userID = userIDNumber != null ? userIDNumber.intValue() : 0;
    		return new User(firstName, lastName, email, password, username, userID);
    	} catch (Exception e) {
    		System.err.println("Error creating user from JSON: " + e.getMessage());
    		return null;
    	}
    }
	
	@SuppressWarnings("unchecked")
	public static void updateUserJsonFile() {
		JSONObject currentUserListJson = new JSONObject();
		JSONArray usersJsonArray = new JSONArray();
    	for (User user : Player.userList) {
			JSONObject currentUserJson = new JSONObject();
			currentUserJson.put("Username", user.getUsername());
			currentUserJson.put("First name",  user.getFirstname());
			currentUserJson.put("Last name", user.getLastname());
			currentUserJson.put("Email", user.getEmail());
			currentUserJson.put("Password", user.getPassword());
			currentUserJson.put("userID", user.getUserID());
			currentUserJson.put("playlistFileName", user.getPlaylistFileName());
			usersJsonArray.add(currentUserJson);
    	}
    	currentUserListJson.put("Users", usersJsonArray);
    	try {
			Files.write(Paths.get("userList.json"), currentUserListJson.toJSONString().getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}


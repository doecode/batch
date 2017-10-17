/*
     Goes to the OSTI Elink Authority API and grabs values from the various lists and turns them into java files
 */
package gov.osti.processes;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author smithwa
 */
public class CreateLists {

     private static final String DEFAULT_FILE_LOCATION = "./";

     public static void main(String[] args) {
          String file_write_location = args.length > 0 ? args[0] : DEFAULT_FILE_LOCATION;
          System.out.println("Starting lists");
          //countries
          System.out.println("Countries List");
          gatherAndWriteFile("https://www.osti.gov/elink/api/authorities/simple/countries-list", "countries", "countriesList.json", file_write_location);

          //Sponsoring Orgs
          JsonArray USDOE = new JsonArray();
          JsonObject BlankAttributes = new JsonObject();
          BlankAttributes.add("name", "");
          BlankAttributes.add("code", "");
          BlankAttributes.add("status", "");
          JsonObject USDOEAttributes = new JsonObject();
          USDOEAttributes.add("name", "USDOE");
          USDOEAttributes.add("code", "USDOE");
          USDOEAttributes.add("status", "C");
          USDOE.add(BlankAttributes);
          USDOE.add(USDOEAttributes);
          System.out.println("Sponsor Orgs List");
          gatherAndWriteFile("https://www.osti.gov/elink/api/authorities/sponsor/sponsor-org-list", "sponsorOrgs", "sponsorOrgsList.json", file_write_location, USDOE, true);

          //Research Org
          System.out.println("Research Orgs List");
          gatherAndWriteFile("https://www.osti.gov/elink/api/authorities/research/orig-research-org-list", "researchOrgs", "researchOrgList.json", file_write_location);

          //Affiliations
          System.out.println("Affiliations List");
          gatherAndWriteFile("https://www.osti.gov/elink/api/authorities/affiliations/affiliations-list", "affiliations", "affiliationsList.json", file_write_location);
          System.out.println("Ending");
     }

     public static void gatherAndWriteFile(String url, String key, String fileName, String fileLocation) {
          JsonObject data = pullData(url, key, null, false);
          writeFile(data.toString(), fileName, fileLocation);
     }

     public static void gatherAndWriteFile(String url, String key, String fileName, String fileLocation, JsonArray extraData, boolean isBefore) {
          JsonObject data = pullData(url, key, extraData, isBefore);
          writeFile(data.toString(), fileName, fileLocation);
     }

     public static JsonObject pullData(String url, String key, JsonArray extraData, boolean isBefore) {
          JsonArray return_data_list = new JsonArray();
          StringBuilder jsonString = new StringBuilder();
          try {
               //Create the client, and ask for JSON
               DefaultHttpClient httpClient = new DefaultHttpClient();
               HttpGet getRequest = new HttpGet(url);
               getRequest.addHeader("accept", "application/json");
               HttpResponse response = httpClient.execute(getRequest);

               //IF we didn't get a 200, we had an error
               int statusCode = response.getStatusLine().getStatusCode();
               if (statusCode != 200) {
                    throw new IOException("Didn't get a 200: Got a " + statusCode);
               }

               //Grab the contents
               BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

               //Go through, and append it to our big json string
               String output;
               while ((output = br.readLine()) != null) {
                    jsonString.append(output);
               }

               //close the connection. Only keep it open as long as needed
               httpClient.getConnectionManager().shutdown();
          } catch (IOException ex) {
               System.out.println("An error has occurred in pulling the data: " + ex.getMessage());
          }

          //Parse out the json string into something usable
          return_data_list = Json.parse(jsonString.toString()).asArray();

          if (extraData != null) {
               //If it's before, we create a new array starting with what we want at the beginning, and then tacking on what we already have at the end
               if (isBefore) {
                    JsonArray newArray = extraData;
                    for (JsonValue j : return_data_list) {
                         newArray.add(j);
                    }
                    return_data_list = newArray;
               } else {//Just add the data at the end
                    for (JsonValue j : extraData) {
                         return_data_list.add(j);
                    }
               }
          }

          //Now, we loop through and assign different values to the files
          JsonArray adjusted_list = new JsonArray();

          if (return_data_list.size() > 0) {
               //Go through the list and make some objets with label, value, and title
               for (JsonValue v : return_data_list) {
                    String name = (v.isString()) ? v.asString() : v.asObject().getString("name", "");
                    JsonObject adjustedItem = new JsonObject();
                    adjustedItem.add("label", name);
                    adjustedItem.add("value", name);
                    adjustedItem.add("title", name);
                    adjusted_list.add(adjustedItem);
               }
          }

          JsonObject return_data = new JsonObject();
          return_data.add(key, adjusted_list);
          return return_data;
     }

     public static void writeFile(String fileContents, String fileName, String file_location) {
          FileWriter fileWriter = null;
          try {
               File file = new File(file_location + fileName);
               fileWriter = new FileWriter(file);
               fileWriter.write(fileContents);
               fileWriter.flush();
          } catch (IOException ex) {
               System.out.println("Error in writing file " + fileName + " :" + ex.getMessage());
          } finally {
               try {
                    fileWriter.close();
               } catch (IOException ex) {
                    System.out.println("Couldn't close file writer properly: " + ex.getMessage());
               }
          }
     }
}

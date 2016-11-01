
import java.io.IOException;
import java.util.Map;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hugo
 * This class contains the different queries we have build.
 */

public class ConnectorQueries {
    public TransportClient client;
    
    public ConnectorQueries(TransportClient client){
        this.client=client;
    }
    
    public void getAllIds(){
        //This method gets all Ids stored under the index movies on ES.
        MatchQueryBuilder query = QueryBuilders.matchQuery("_index", "retrirumors");
        SearchResponse response1 = client.prepareSearch().setQuery(query).setSize(2000).execute().actionGet();
        
        System.out.println("Complete Response");
        System.out.println(response1.toString());
        System.out.println("Total Hits = " +response1.getHits().getTotalHits());
        for(int i = 0; i<response1.getHits().getTotalHits(); i++){
                Map<String, Object> fields = response1.getHits().getAt(i).getSource();
                System.out.println(fields.get("id"));
                System.out.println(fields.size());
        }
        
    }

    public void getAll(){
            SearchResponse response = client.prepareSearch().execute().actionGet();
            System.out.println("Complete Response:");
            System.out.println(response.toString());
    }
    
    public void getTweetTextByKeywords(String keywordsOriginal) throws IOException{
        //This method gets all tweet text that contains one or more of the keywords.
       
        String keywords=keywordsOriginal.replace(",", "");
        
        //Creating outputHandler instance
        OutputHandler outputH= new OutputHandler();
        
        //MatchQueryBuilder query = QueryBuilders.matchQuery("text", keywords);.must(termQuery("text", keywords))
        QueryBuilder query = boolQuery().must(matchQuery("_index", "retrirumors")).must(matchQuery("text", keywords)); 
       
        
        SearchResponse response1 = client.prepareSearch().setQuery(query).setSize(10000).execute().actionGet();
        
        //System.out.println("Complete Response");
        //System.out.println(response1.toString());
        outputH.writeOutputFileText(keywordsOriginal);
        for (SearchHit hit : response1.getHits()){
                Map<String, Object> fields = hit.getSource();
                //System.out.println(fields.get("text"));
                //System.out.println(((String)fields.get("text")).trim());
                String ids = Long.toString((long) fields.get("id"));
                String text = replaceCommas((String) fields.get("text"));
                String finalText = replaceLineBreaks(text);
                String endLine=ids + ", " + finalText;
                outputH.writeOutputFileText(endLine);
                outputH.writeOutputFileText("-----------------------------------------------------------");
        }
        
        outputH.closeOutputHanlder();
        
        
    }
    
    public void getBasicInfoByKeywords(String keywordsOriginal) throws IOException{
        //This method gets basic information and retrieves an output file (csv)
       
        String keywords=keywordsOriginal.replace(",", "");
        //Creating outputHandler instance
        OutputHandler outputH= new OutputHandler();
        outputH.writeOutputFileBasic(keywordsOriginal);
        outputH.writeOutputFileBasic("id, tweet_text, screen_name, protected, verified, followers_count,"
                    + "following, statuses_count, default_profile, default_profile_image, retweet_count, favorite_count,"
                    + "favorited, retweeted, hashtags, urls");
        //MatchQueryBuilder query = QueryBuilders.matchQuery("text", keywords);.must(termQuery("text", keywords))
        QueryBuilder query = boolQuery().must(matchQuery("_index", "retrirumors")).must(matchQuery("text", keywords)); 
       
        SearchResponse response1 = client.prepareSearch().setQuery(query).setSize(10000).execute().actionGet();
        
        System.out.println("Complete Response");
        System.out.println(response1.toString());
        
        for (SearchHit hit : response1.getHits()){
                Map<String, Object> fields = hit.getSource();
                Map<String, Object> user = (Map<String, Object>) fields.get("user");
                Map<String, Object> entities = (Map<String, Object>) fields.get("entities");
                //Map≤String, Object> userFields=hit.getSource();
                String text = replaceCommas((String) fields.get("text"));
                String finalText=replaceLineBreaks(text);
                String line=fields.get("id") + "," +
                    finalText + "," +
                    user.get("screen_name") + "," +
                    user.get("protected") + "," +
                    user.get("verified") + "," +
                    user.get("followers_count") + "," +
                    user.get("following") + "," +
                    user.get("statuses_count") + "," +
                    user.get("default_profile") + "," +
                    user.get("default_profile_image") + "," +
                    fields.get("retweet_count") + "," +
                    fields.get("favorite_count") + "," +
                    fields.get("favorited") + "," +
                    fields.get("retweeted") + "," +
                    entities.get("hashtags") + "," +
                    entities.get("urls") + ",";
                outputH.writeOutputFileBasic(line);
        }
        
        outputH.closeOutputHanlder();
        
        
    }
    
    public String replaceCommas(String line){
        String newLine = line.replace(",", " ");
        return newLine;
    }
    
    public String replaceLineBreaks(String line){
        String newLine = line.replace("\n", " ");
        String finalLine = newLine.replace("\r", " ");
        return finalLine;
    }
}

package ca.mcgill.mymcgill.object;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;

/**
 * Author: Julien
 * Date: 16/02/14, 4:34 PM
 */
public class UserInfo implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String mName, mId;

    public UserInfo(String ebillString){
        //Parse the string to get the relevant info
        Document doc = Jsoup.parse(ebillString);
        Element ebillTable = doc.getElementsByClass("datadisplaytable").first();

        //Parse the user info
        Elements userInfo = ebillTable.getElementsByTag("caption");
        String id = userInfo.get(0).text().replace("Statements for ", "");
        String[] userInfoItems = id.split(" - ");
        this.mName = userInfoItems[1].trim();
        this.mId = userInfoItems[0].trim();
    }

    public String getName(){
        return mName;
    }

    public String getId(){
        return mId;
    }
}

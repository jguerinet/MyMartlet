package ca.mcgill.mymcgill.object;

/**
 * Author: Julien
 * Date: 16/02/14, 4:34 PM
 */
public class UserInfo {
    private String mName, mId;

    public UserInfo(String name, String id){
        this.mName = name;
        this.mId = id;
    }

    public String getName(){
        return mName;
    }

    public String getId(){
        return mId;
    }
}

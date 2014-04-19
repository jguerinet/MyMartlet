package stringparser;

/**
 * Project : Immunize Android
 * Author : Julien
 * Date : 04/11/13, 9:02 AM
 * Package : main.java.com.stringsparser
 */
public class Strings {
    private String key, en, fr, comments, ontario;

    public Strings(){

    }

    public Strings(String key, String en, String fr, String comments){
        this.key = key;
        this.en = en;
        this.fr = fr;
        this.comments = comments;
        this.ontario = ontario;
    }

    public void setKey(String key){
        this.key = key;
    }

    public void setEn(String en){
        this.en = en;
    }

    public void setFr(String fr){
        this.fr = fr;
    }

    public void setComments(String comments){
        this.comments = comments;
    }

    public void setOntario(String ontario){
        this.ontario = ontario;
    }

    public String getKey(){
        return this.key;
    }

    public String getEn(){
        //If null, return empty String
        return this.en == null ? "" : this.en;
    }

    public String getFr(){
        //If null, return empty String
        return this.fr == null ? "" : this.fr;
    }

    public String getOntarioString(){
        return this.ontario == null ? "" : this.ontario;
    }

}

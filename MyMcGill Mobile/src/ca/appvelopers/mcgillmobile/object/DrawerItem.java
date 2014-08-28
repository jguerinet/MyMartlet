package ca.appvelopers.mcgillmobile.object;

/**
 * Author: Julien
 * Date: 24/02/14, 11:50 PM
 */
public class DrawerItem {
    private String mTitle, mIcon;

    public DrawerItem(String title, String icon){
        this.mTitle = title;
        this.mIcon = icon;
    }

    public String getTitle(){
        return this.mTitle;
    }

    public String getIcon(){
        return this.mIcon;
    }
}

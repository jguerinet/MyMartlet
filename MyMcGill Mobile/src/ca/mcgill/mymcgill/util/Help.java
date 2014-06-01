package ca.mcgill.mymcgill.util;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ca.mcgill.mymcgill.R;

/**
 * Class that contains various useful static help methods
 * Author: Julien
 * Date: 04/02/14, 7:45 PM
 */
public class Help {

    public static boolean timeIsAM(int hour){
        return hour / 12 == 0;
    }

    public static String getShortTimeString(Context context, int hour){
        //This is so that 12 does not become 0
        String hours = hour == 12 ? "12" : String.valueOf(hour % 12) ;

        if(timeIsAM(hour)){
            return context.getResources().getString(R.string.am, hours);
        }
        return context.getResources().getString(R.string.pm, hours);
    }

    public static String getLongTimeString(Context context, int hour, int minute){
        //This is so that 12 does not become 0
        String hours = (hour == 12) ? "12" : String.valueOf(hour % 12) ;

        //This is so minutes has 2 0's
        String minutes = String.format("%02d", minute);

        if(timeIsAM(hour)){
            return context.getResources().getString(R.string.am_long, hours, minutes);
        }
        return context.getResources().getString(R.string.pm_long, hours, minutes);
    }

    /**
     * Get the height of the display
     * @param display The display to measure
     * @return The height of the given display
     */
    public static int getDisplayHeight(Display display){
        Point size = new Point();

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            size.set(display.getWidth(), display.getHeight());
        }
        else{
            display.getSize(size);
        }

        return size.y;
    }

    /**
     * Get the width of the display
     * @param display The display to measure
     * @return The width of the given display
     */
    public static int getDisplayWidth(Display display){
        Point size = new Point();

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            size.set(display.getWidth(), display.getHeight());
        }
        else{
            display.getSize(size);
        }

        return size.x;
    }

    public static String readFromFile(Context context, int fileResource) {
        //create return string
        String ret = "";

        try {
            InputStream inputStream =  context.getResources().openRawResource(fileResource);


            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.toString());
        } catch (IOException e) {
            System.out.println("Can not read file: " + e.toString());
        }
        catch(Exception e){
            System.out.println("Exception: " + e.toString());
        }
        finally{

            //always return something
            return ret;
        }
    }
}

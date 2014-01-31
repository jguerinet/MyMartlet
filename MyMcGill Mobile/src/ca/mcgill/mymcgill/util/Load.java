package ca.mcgill.mymcgill.util;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

import ca.mcgill.mymcgill.object.Transcript;

/**
 * Author: Julien
 * Date: 31/01/14, 5:50 PM
 * Class that loads objects from internal storage
 */
public class Load {
    private static final String TRANSCRIPT_FILE_NAME = "transcript";

    public static Transcript loadTranscript(Context context){
        Transcript transcript = null;

        try{
            FileInputStream fis = context.openFileInput(TRANSCRIPT_FILE_NAME);
            ObjectInputStream in = new ObjectInputStream(fis);
            transcript= (Transcript) in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("Load Transcript Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (OptionalDataException e) {
            Log.e("Load Transcript Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.e("Load Transcript Failure", "File not found");
        } catch (StreamCorruptedException e) {
            Log.e("Load Transcript Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Load Transcript Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        }

        return transcript;
    }
}

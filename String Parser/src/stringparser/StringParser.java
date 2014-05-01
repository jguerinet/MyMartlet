package stringparser;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StringParser{
    public static final String URL = "https://docs.google.com/spreadsheet/pub?key=0AhHWqylMWqY4dGZFcEZQYThKV1ZjRVhIQ3dHOWNsOFE&single=true&gid=1&output=csv";

    public static final String ANDROID_ENGLISH_STRINGS_PATH = "MyMcGill Mobile/res/values/strings.xml";
    public static final String ANDROID_FRENCH_STRINGS_PATH = "MyMcGill Mobile/res/values-fr/strings.xml";

    public static final String IOS_ENGLISH_STRINGS_PATH = "english_Localizable.strings";
    public static final String IOS_FRENCH_STRINGS_PATH = "french_Localizable.strings";

    public static final String WINDOWS_ENGLISH_STRINGS_PATH = "";
    public static final String WINDOWS_FRENCH_STRINGS_PATH = "";

    //Stuff for Android Strings
    public static final String XML_OPENER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
    public static final String RESOURCES_OPENER = "<resources>";
    public static final String RESOURCES_CLOSER = "</resources>";

    private static final int ANDROID = 0;
    private static final int IOS = 1;
    private static final int WINDOWS = 2;
    public static int platform;

    public static void main(String[] args) throws IOException {
        if(args.length != 1){
            System.out.println("Usage: android/ios/windows");
            return;
        }
        if(args[0].equalsIgnoreCase("Android")){
            platform = ANDROID;
        }
        else if(args[0].equalsIgnoreCase("IOS")){
            platform = IOS;
        }
        else if(args[0].equalsIgnoreCase("Windows")){
            platform = WINDOWS;
        }
        else{
            System.out.println("Usage: android/ios/windows");
            return;
        }

        URL link = new URL(URL);
        HttpURLConnection httpConnection = (HttpURLConnection) link.openConnection();
        httpConnection.setRequestMethod("GET");
        httpConnection.connect();

        List<Strings> strings = new ArrayList<Strings>();
        CsvBeanReader reader = new CsvBeanReader(new InputStreamReader(httpConnection.getInputStream()), CsvPreference.EXCEL_PREFERENCE);

        try {
            final String[] header = reader.getHeader(true);
            final CellProcessor[] processors = getProcessors();

            Strings currentLine;
            while( (currentLine = reader.read(Strings.class, header, processors)) != null){
                strings.add(currentLine);
            }
            if(!strings.isEmpty()){
                processStrings(strings);
            }
        }
        finally {
            reader.close();
        }


    }

    public static CellProcessor[] getProcessors(){
        return new CellProcessor[]{
                null,
                null,
                null,
                null,
        };
    }

    public static void processStrings(List<Strings> strings)throws FileNotFoundException, UnsupportedEncodingException{
        if(platform == ANDROID){
            processAndroidStrings(strings);
        }
        else if(platform == IOS){
            processIOSStrings(strings);
        }
        else{
            //TODO Windows Code
        }
    }

    /** ANDROID STRING PARSING **/
    public static void processAndroidStrings(List<Strings> strings)throws FileNotFoundException, UnsupportedEncodingException{
        //Android English Strings
        PrintWriter androidEnglishWriter = new PrintWriter(ANDROID_ENGLISH_STRINGS_PATH, "UTF-8");
        //Android French Strings
        PrintWriter androidFrenchWriter = new PrintWriter(ANDROID_FRENCH_STRINGS_PATH, "UTF-8");

        //Add the XML header for Android files
        androidEnglishWriter.println(XML_OPENER);
        androidFrenchWriter.println(XML_OPENER);

        //Add the resources opening for Android files
        androidEnglishWriter.println(RESOURCES_OPENER);
        androidFrenchWriter.println(RESOURCES_OPENER);

        //Go through the strings
        for(Strings currentStrings : strings){
            //Android strings
            String androidEnglishString = addAndroidEnglishString(currentStrings);
            String androidFrenchString = addAndroidFrenchString(currentStrings);

            //If one is null, there is no value, so do not add it
            if(androidEnglishString != null){
                androidEnglishWriter.println(androidEnglishString);
            }
            if(androidFrenchString != null){
                androidFrenchWriter.println(androidFrenchString);
            }
        }

        //Add the resources closing to android files
        androidEnglishWriter.println(RESOURCES_CLOSER);
        androidFrenchWriter.println(RESOURCES_CLOSER);

        //Close the writers
        androidEnglishWriter.close();
        androidFrenchWriter.close();
    }

    public static String addAndroidEnglishString(Strings strings){
        return addAndroidString(strings.getKey(), strings.getEn());
    }

    public static String addAndroidFrenchString(Strings strings){
        //For headers in the french XML
        if(strings.getKey().equalsIgnoreCase("header")){
            return addAndroidString(strings.getKey(), strings.getEn());
        }
        return addAndroidString(strings.getKey(), strings.getFr());
    }

    public static String addAndroidString(String key, String string){
        //First check if string is empty: if it is, return null
        if(string.isEmpty()){
            return null;
        }

        //Add initial indentation
        String xmlString = "    ";

        //Check if it's a header section
        if(key.trim().equalsIgnoreCase("header")){
            xmlString = "\n" + xmlString + "<!-- " + string + " -->";
        }
        //If not, treat is as a normal string
        else{
            /* Character checks */
            //Unescaped apostrophes
            string = string.replace("\'", "\\" + "\'");

            //Unescaped @ signs
            string = string.replace("@", "\\" + "@");

            if(string.contains("<html>") || string.contains("<HTML>")){
                //Take care of html tags
                string = string.replace("<html>", "<![CDATA[");
                string = string.replace("</html>", "]]>");
                string = string.replace("<HTML>", "<![CDATA[");
                string = string.replace("</HTML>", "]]>");
            }
            else{
                //Ampersands
                if(string.contains("&")){
                    //If it's an icon, do not do anything
                    if(!string.contains("&#x")){
                        string = string.replace("&", "&amp;");
                    }
                }

                //Copyright
                string = string.replace("(c)", "\u00A9");

                //Ellipses
                string = string.replace("...", "&#8230;");
            }

            xmlString = xmlString + "<string name=\"" + key.trim() + "\">" + string + "</string>";
        }
        return xmlString;
    }

    /** IOS STRING PARSING **/
    public static void processIOSStrings(List<Strings> strings)throws FileNotFoundException, UnsupportedEncodingException{
        //IOS English Strings
        PrintWriter iOSEnglishWriter = new PrintWriter(IOS_ENGLISH_STRINGS_PATH,"UTF-8");
        //IOS French Strings
        PrintWriter iOSFrenchWriter = new PrintWriter(IOS_FRENCH_STRINGS_PATH,"UTF-8");

        //Go through the strings
        for(Strings currentStrings : strings){
            //iOS strings
            String iOSEnglishString = addIOSEnglishString(currentStrings);
            String iOSFrenchString = addIOSFrenchString(currentStrings);

            //If one is null, there is no value, so do not add it
            if(iOSEnglishString != null) {
                iOSEnglishWriter.println(iOSEnglishString);
            }
            if(iOSFrenchString != null) {
                iOSFrenchWriter.println(iOSFrenchString);
            }
        }

        iOSEnglishWriter.close();
        iOSFrenchWriter.close();
    }

    public static String addIOSEnglishString(Strings strings){
        return addIOSString(strings.getKey(), strings.getEn());
    }

    public static String addIOSFrenchString(Strings strings){
        //For headers in the french XML
        if(strings.getKey().equalsIgnoreCase("header")){
            return addIOSString(strings.getKey(), strings.getEn());
        }
        return addIOSString(strings.getKey(), strings.getFr());
    }

    public static String addIOSString(String key, String string){
        //First check if string is empty: if it is, return null
        if (string.isEmpty()) {
            return null;
        }

        //Add initial indentation
        String xmlString = "";

        //Replace %s format specifiers with %@
        string = string.replace("%s","%@");
        string = string.replace("$s", "$@");

        //Remove <html> </html>tags
        string = string.replace("<html>", "");
        string = string.replace("</html>", "");
        string = string.replace("<HTML>", "");
        string = string.replace("</HTML>", "");
        string = string.replace("(c)", "\u00A9");

        //Check if it's a header section
        if(key.equalsIgnoreCase("header")){
            xmlString = "\n" + xmlString + "/*  " + string + " */";
        }
        //If not, treat is as a normal string
        else{
            xmlString = "\"" + key + "\" = \"" + string + "\";";
        }
        return xmlString;
    }

    /**WINDOWS STRING PARSING**/
}

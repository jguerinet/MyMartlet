package ca.mcgill.mymcgill.object;

public enum Language {
    ENGLISH,
    FRENCH;

    public int getLanguageInt(){
        switch(this){
            case FRENCH:
                return 1;
            default:
                return 0;
        }
    }

    public String getLanguageString(){
        switch (this){
            case FRENCH:
                return "fr";
            default:
                return "en";
        }
    }

    public static Language getLanguage(int languageInt){
        switch (languageInt){
            case 1:
                return FRENCH;
            default:
                return ENGLISH;
        }
    }
}

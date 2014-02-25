package ca.mcgill.mymcgill.object;

public enum Language {
    ENGLISH,
    FRENCH;

    public String getLanguageString(){
        switch (this){
            case FRENCH:
                return "fr";
            default:
                return "en";
        }
    }
}

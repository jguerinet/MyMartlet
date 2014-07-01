package ca.appvelopers.mcgillmobile.object;

/**
 * Created by Adnan on 6/29/2014.
 */
public class HelpItem {
    private String question;
    private String answer;

    public HelpItem (String question,String answer)
    {
        this.question = question;
        this.answer  = answer;
    }

    public String getQuestion()
    {
        return question;
    }

    public String getAnswer()
    {
        return answer;
    }

}

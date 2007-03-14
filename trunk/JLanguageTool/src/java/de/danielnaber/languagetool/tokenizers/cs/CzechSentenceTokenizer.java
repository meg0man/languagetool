/*
 * CzechSentenceTokenizer.java
 *
 * Created on 25.1.2007, 11:45
 */

package de.danielnaber.languagetool.tokenizers.cs;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import de.danielnaber.languagetool.tokenizers.SentenceTokenizer;

/**
 *
 * @author Jozef Licko
 */
public class CzechSentenceTokenizer extends SentenceTokenizer
{
    
      
    /** End of sentence marker.
     */
    private static final String EOS = "\0";
    // private final static String EOS = "#"; // for testing only
    
    /** Punctuation.
     *
     */
    private static final String P = "[\\.!?â€¦]";
    
    /** After punctuation.
     *
     */
    private static final String AP = "(?:'|Â«|\"|â€ť|\\)|\\]|\\})?";
    private static final String PAP = P + AP;
    
    // Check out the private methods for comments and examples about these
    // regular expressions:
    
    private Pattern paragraph = null;
    private static final Pattern paragraphByTwoLineBreaks = Pattern.compile("(\\n\\s*\\n)");
    private static final Pattern paragraphByLineBreak = Pattern.compile("(\\n)");
    
    private static final Pattern punctWhitespace = Pattern.compile("(" + PAP + "\\s)");
    // \p{Lu} = uppercase, with obeying Unicode (\p{Upper} is just US-ASCII!):
    private static final Pattern punctUpperLower = Pattern.compile("(" + PAP
            + ")([\\p{Lu}][^\\p{Lu}.])");
    private static final Pattern letterPunct = Pattern.compile("(\\s[\\wĂˇÄŤÄŹĂ©Ä›Ă­ĹĂłĹ™ĹˇĹĄĂşĹŻĂ˝ĹľĂÄŚÄŽĂ‰ÄšĂŤĹ‡Ă“ĹĹ Ĺ¤ĂšĹ®ĂťĹ˝]" + P + ")");
    private static final Pattern abbrev1 = Pattern.compile("([^-\\wĂˇÄŤÄŹĂ©Ä›Ă­ĹĂłĹ™ĹˇĹĄĂşĹŻĂ˝ĹľĂÄŚÄŽĂ‰ÄšĂŤĹ‡Ă“ĹĹ Ĺ¤ĂšĹ®ĂťĹ˝][\\wĂˇÄŤÄŹĂ©Ä›Ă­ĹĂłĹ™ĹˇĹĄĂşĹŻĂ˝ĹľĂÄŚÄŽĂ‰ÄšĂŤĹ‡Ă“ĹĹ Ĺ¤ĂšĹ®ĂťĹ˝]" + PAP + "\\s)" + EOS);
    private static final Pattern abbrev2 = Pattern.compile("([^-\\wĂˇÄŤÄŹĂ©Ä›Ă­ĹĂłĹ™ĹˇĹĄĂşĹŻĂ˝ĹľĂÄŚÄŽĂ‰ÄšĂŤĹ‡Ă“ĹĹ Ĺ¤ĂšĹ®ĂťĹ˝][\\wĂˇÄŤÄŹĂ©Ä›Ă­ĹĂłĹ™ĹˇĹĄĂşĹŻĂ˝ĹľĂÄŚÄŽĂ‰ÄšĂŤĹ‡Ă“ĹĹ Ĺ¤ĂšĹ®ĂťĹ˝]" + P + ")" + EOS);
    private static final Pattern abbrev3 = Pattern.compile("(\\s[\\wĂˇÄŤÄŹĂ©Ä›Ă­ĹĂłĹ™ĹˇĹĄĂşĹŻĂ˝ĹľĂÄŚÄŽĂ‰ÄšĂŤĹ‡Ă“ĹĹ Ĺ¤ĂšĹ®ĂťĹ˝]\\.\\s+)" + EOS);
    private static final Pattern abbrev4 = Pattern.compile("(\\.\\.\\. )" + EOS + "([\\p{Ll}])");
    private static final Pattern abbrev5 = Pattern.compile("(['\"]" + P + "['\"]\\s+)" + EOS);
    private static final Pattern abbrev6 = Pattern.compile("([\"']\\s*)" + EOS + "(\\s*[\\p{Ll}])");
    private static final Pattern abbrev7 = Pattern.compile("(\\s" + PAP + "\\s)" + EOS);
    // z.b. 3.10. (im Datum):
    private static final Pattern abbrev8 = Pattern.compile("(\\d{1,2}\\.\\d{1,2}\\.\\s+)" + EOS);
    private static final Pattern repair1 = Pattern.compile("('[\\wĂˇÄŤÄŹĂ©Ä›Ă­ĹĂłĹ™ĹˇĹĄĂşĹŻĂ˝ĹľĂÄŚÄŽĂ‰ÄšĂŤĹ‡Ă“ĹĹ Ĺ¤ĂšĹ®ĂťĹ˝]" + P + ")(\\s)");
    private static final Pattern repair2 = Pattern.compile("(\\sno\\.)(\\s+)(?!\\d)");
    
    // Czech abbreviations (ver. 0.1, probably needs revision & update)
    // as a single regexp:
    private static final String abbrev_list = "abt|ad|a.i|aj|angl|anon|apod|atd|atp|aut|bd|biogr|b.m|b.p|b.r|cca|cit|cizojaz|c.k|col|ÄŤes|ÄŤĂ­n|ÄŤj|ed|facs|fasc|fol|fot|franc|h.c|hist|hl|hrsg|ibid|il|ind|inv.ÄŤ|jap|jhdt|jv|koed|kol|korej|kl|krit|lat|lit|m.a|maÄŹ|mj|mp|nĂˇsl|napĹ™|nepubl|nÄ›m|no|nr|n.s|okr|odd|odp|obr|opr|orig|phil|pl|pokraÄŤ|pol|port|pozn|prof|pĹ™.kr|pĹ™.n.l|pĹ™el|pĹ™eprac|pĹ™Ă­l|pseud|pt|red|repr|resp|revid|rkp|roÄŤ|roz|rozĹˇ|samost|sect|sest|seĹˇ|sign|sl|srv|stol|sv|Ĺˇk|Ĺˇk.ro|Ĺˇpan|tab|t.ÄŤ|tis|tj|tĹ™|tzv|univ|uspoĹ™|vol|vl.jm|vs|vyd|vyobr|zal|zejm|zkr|zprac|zvl|n.p";
    
    private StringTokenizer stringTokenizer = null;
    
    /**
     * Create a sentence tokenizer.
     */
    public CzechSentenceTokenizer()
    {
        setSingleLineBreaksMarksParagraph(false);
    }
    
    /**
     * @param lineBreakParagraphs if <code>true</code>, single lines breaks are assumed to end a paragraph,
     *  with <code>false</code>, only two ore more consecutive line breaks end a paragraph
     */
    public final void setSingleLineBreaksMarksParagraph(final boolean lineBreakParagraphs)
    {
        if (lineBreakParagraphs)
            paragraph = paragraphByLineBreak;
        else
            paragraph = paragraphByTwoLineBreaks;
    }
    
    public final List<String> tokenize(String s)
    {
        s = firstSentenceSplitting(s);
        s = removeFalseEndOfSentence(s);
        s = splitUnsplitStuff(s);
        stringTokenizer = new StringTokenizer(s, EOS);
        List<String> l = new ArrayList<String>();
        while (stringTokenizer.hasMoreTokens())
        {
            String sentence = stringTokenizer.nextToken();
            l.add(sentence);
        }
        return l;
    }
    
    /**
     * Add a special break character at all places with typical sentence delimiters.
     */
    private String firstSentenceSplitting(String s)
    {
        // Double new-line means a new sentence:
        s = paragraph.matcher(s).replaceAll("$1" + EOS);
        // Punctuation followed by whitespace means a new sentence:
        s = punctWhitespace.matcher(s).replaceAll("$1" + EOS);
        // New (compared to the perl module): Punctuation followed by uppercase followed
        // by non-uppercase character (except dot) means a new sentence:
        s = punctUpperLower.matcher(s).replaceAll("$1" + EOS + "$2");
        // Break also when single letter comes before punctuation:
        s = letterPunct.matcher(s).replaceAll("$1" + EOS);
        return s;
    }
    
    /**
     * Repair some positions that don't require a split, i.e. remove the special break character at
     * those positions.
     */
    private String removeFalseEndOfSentence(String s)
    {
        // Don't split at e.g. "U. S. A.":
        s = abbrev1.matcher(s).replaceAll("$1");
        // Don't split at e.g. "U.S.A.":
        s = abbrev2.matcher(s).replaceAll("$1");
        // Don't split after a white-space followed by a single letter followed
        // by a dot followed by another whitespace.
        // e.g. " p. "
        s = abbrev3.matcher(s).replaceAll("$1");
        // Don't split at "bla bla... yada yada" (TODO: use \.\.\.\s+ instead?)
        s = abbrev4.matcher(s).replaceAll("$1$2");
        // Don't split [.?!] when the're quoted:
        s = abbrev5.matcher(s).replaceAll("$1");
        
        // Don't split at abbreviations, treat them case insensitive
        //TODO: don't split at some abbreviations followed by uppercase
        //E.g., "Wojna rozpoczÄ™Ĺ‚a siÄ™ w 1918 r. To byĹ‚a krwawa jatka"
        //should be split at "r."... But
        //"Ks. Jankowski jest analfabetÄ…" shouldn't be split...
        //this requires a special list of abbrevs used before names etc.
        
        //removing the loop and using only one regexp - this is definitely much, much faster
        Pattern pattern = Pattern.compile("(?u)(\\b(" + abbrev_list +")"+ PAP + "\\s)" + EOS);
        s = pattern.matcher(s).replaceAll("$1");
        
        // Don't break after quote unless there's a capital letter:
        // e.g.: "That's right!" he said.
        s = abbrev6.matcher(s).replaceAll("$1$2");
        
        // fixme? not sure where this should occur, leaving it commented out:
        // don't break: text . . some more text.
        // text=~s/(\s\.\s)$EOS(\s*)/$1$2/sg;
        
        // e.g. "Das ist . so." -> assume one sentence
        s = abbrev7.matcher(s).replaceAll("$1");
        
        // e.g. "Das ist . so." -> assume one sentence
        s = abbrev8.matcher(s).replaceAll("$1");
        
        // extension by dnaber --commented out, doesn't help:
        // text = re.compile("(:\s+)%s(\s*[%s])" % (self.EOS, string.lowercase),
        // re.DOTALL).sub("\\1\\2", text)
        
        s = s.replaceAll("(\\d+\\.) " + EOS + "([\\p{L}&&[^\\p{Lu}]]+)", "$1 $2");
        
        // z.B. "Das hier ist ein(!) Satz."
        s = s.replaceAll("\\(([!?]+)\\) " + EOS, "($1) ");
        return s;
    }
    
    /**
     * Treat some more special cases that make up a sentence boundary. Insert the special break
     * character at these positions.
     */
    private String splitUnsplitStuff(String s)
    {
        // e.g. "x5. bla..." -- not sure, leaving commented out:
        // text = re.compile("(\D\d+)(%s)(\s+)" % self.P, re.DOTALL).sub("\\1\\2%s\\3" % self.EOS, text)
        // Not sure about this one, leaving out four now:
        // text = re.compile("(%s\s)(\s*\()" % self.PAP, re.DOTALL).sub("\\1%s\\2" % self.EOS, text)
        // Split e.g.: He won't. #Really.
        s = repair1.matcher(s).replaceAll("$1" + EOS + "$2");
        // Split e.g.: He won't say no. Not really.
        s = repair2.matcher(s).replaceAll("$1" + EOS + "$2");
        return s;
    }
    
}

package br.edu.utfpr.recominer.metric.discussion;

import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class LuceneUtilTest {
    
    @Test
    public void testTokenizeString() {
        List<String> tokenizeString = LuceneUtil.tokenizeString("p>The bug is as shown in the following code:"
                + "</p><div class=\"code panel\"style=\"border-width: 1px;\">"
                + "<div class=\"codeContent panelContent\"><pre class=\"code-java\">"
                + "<span class=\"code-comment\">// Having Base.class in the union"
                + " results in infinite recursion</span>@Union ({Base.class, Derived.class})"
                + "<span class=\"code-comment\">// Having no Base.class in the union fails PolymorphicDO.obj2</span>@Union "
                + "({Derived.class})<span class=\"code-keyword\">private</span><span class=\"code-keyword\">static</span>"
                + "class Base{<span class=\"code-object\">Integer</span>a = 5;}<span class=\"code-keyword\">private</span>"
                + "<span class=\"code-keyword\">static</span>class Derived<span class=\"code-keyword\">extends</span>Base{"
                + "<span class=\"code-object\">String</span>b =<span class=\"code-quote\">\"Foo\"</span>;}<span class=\"code-keyword\">"
                + "private</span><span class=\"code-keyword\">static</span>class PolymorphicDO{Base obj =<span class=\"code-keyword\">"
                + "new</span>Derived();Base obj2 =<span class=\"code-keyword\">new</span>Base();}</pre></div></div>");
        
        final int expected = 41;
        assertEquals(expected, tokenizeString.size());
    }
    
}

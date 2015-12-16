package br.edu.utfpr.recominer.util;

import br.edu.utfpr.recominer.util.PairUtils;
import br.edu.utfpr.recominer.model.Commenter;
import br.edu.utfpr.recominer.model.UserToUserEdgeByCommentDirectional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class PairUtilsTest {

    @Test
    public void testPairCommenters() {
        List<Commenter> commenters = new ArrayList<>();
        Commenter commenterA = new Commenter(1, "A", "A", true);
        Commenter commenterB = new Commenter(2, "B", "B", true);
        Commenter commenterC = new Commenter(3, "C", "C", false);

        commenters.add(commenterA);
        commenters.add(commenterB);
        commenters.add(commenterA);
        commenters.add(commenterB);
        commenters.add(commenterB);
        commenters.add(commenterC);

        Map<UserToUserEdgeByCommentDirectional, UserToUserEdgeByCommentDirectional> expResult = new HashMap<>();
        UserToUserEdgeByCommentDirectional ba = new UserToUserEdgeByCommentDirectional("B", "A");
        UserToUserEdgeByCommentDirectional ab = new UserToUserEdgeByCommentDirectional("A", "B");
        UserToUserEdgeByCommentDirectional ca = new UserToUserEdgeByCommentDirectional("C", "A");
        UserToUserEdgeByCommentDirectional cb = new UserToUserEdgeByCommentDirectional("C", "B");
        expResult.put(ba, ba);
        expResult.put(ab, ab);
        expResult.put(ca, ca);
        expResult.put(cb, cb);

        Map<UserToUserEdgeByCommentDirectional, UserToUserEdgeByCommentDirectional> result = PairUtils.pairCommenters(commenters);

        for (UserToUserEdgeByCommentDirectional keySet : result.keySet()) {
            System.out.println(keySet.toString());
        }

        assertEquals(expResult, result);
    }

}

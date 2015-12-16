
package br.edu.utfpr.recominer.model;

import br.edu.utfpr.recominer.model.UserToUserEdgeByCommentDirectional;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class UserToUserEdgeDirectionalTest {

    private UserToUserEdgeByCommentDirectional instance;
    private UserToUserEdgeByCommentDirectional instance2;

    @Before
    public void setup() {
        instance = new UserToUserEdgeByCommentDirectional("User1", "Email1", "User2", "Email2");
        instance2 = new UserToUserEdgeByCommentDirectional(null, "Email1", null, "Email2");
    }

    @After
    public void tearDown() {
        instance = null;
        instance2 = null;
    }

    /**
     * Test of equals method. It is equals if the (filename 1 and filename 2)
     * equal a (filename 1 and filename 2) or (filename2 and filename1)
     */
    @Test
    public void testEqualsTrue() {
        UserToUserEdgeByCommentDirectional equal1 = new UserToUserEdgeByCommentDirectional("User1", "User2");
        UserToUserEdgeByCommentDirectional equal2 = new UserToUserEdgeByCommentDirectional("User1", "Email1", "User2", "Email2");
        UserToUserEdgeByCommentDirectional equal3 = new UserToUserEdgeByCommentDirectional(null, "Email1", null, "Email2");

        assertTrue(instance.equals(equal1));
        assertTrue(instance.hashCode() == equal1.hashCode());
        assertTrue(instance.equals(equal2));
        assertTrue(instance.hashCode() == equal2.hashCode());
        assertTrue(instance2.equals(equal3));
        assertTrue(instance2.hashCode() == equal3.hashCode());
    }

    /**
     * Test of equals method. It is not equals if the (filename 1 and filename
     * 2) not equal a (filename 1 and filename 2) and (filename2 and filename1)
     */
    @Test
    public void testEqualsFalse() {
        UserToUserEdgeByCommentDirectional notEqual1 = new UserToUserEdgeByCommentDirectional("User2", "User1");
        UserToUserEdgeByCommentDirectional notEqual2 = new UserToUserEdgeByCommentDirectional(null, "Email1", null, "Email2");
        UserToUserEdgeByCommentDirectional notEqual3 = new UserToUserEdgeByCommentDirectional(null, "Email2", null, "Email1");

        assertFalse(instance.equals(notEqual1));
        assertFalse(instance.hashCode() == notEqual1.hashCode());
        assertFalse(instance.equals(notEqual2));
        assertFalse(instance.hashCode() == notEqual2.hashCode());
        assertFalse(instance2.equals(notEqual3));
        assertFalse(instance2.hashCode() == notEqual3.hashCode());
    }
}

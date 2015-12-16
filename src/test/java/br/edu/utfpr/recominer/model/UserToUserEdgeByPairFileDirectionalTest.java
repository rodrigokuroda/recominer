
package br.edu.utfpr.recominer.model;

import br.edu.utfpr.recominer.model.UserToUserEdgeByFilePairDirectional;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class UserToUserEdgeByPairFileDirectionalTest {

    private UserToUserEdgeByFilePairDirectional instance;

    @Before
    public void setup() {
        instance = new UserToUserEdgeByFilePairDirectional("User1", "FileA.java", "FileB.java", "User2", 1);
    }

    @After
    public void tearDown() {
        instance = null;
    }

    /**
     * Test of equals method. It is equals if the (filename 1 and filename 2)
     * equal a (filename 1 and filename 2) or (filename2 and filename1)
     */
    @Test
    public void testEqualsTrue() {
        UserToUserEdgeByFilePairDirectional equal1 = new UserToUserEdgeByFilePairDirectional("User1", "FileA.java", "FileB.java", "User2", 2);
        UserToUserEdgeByFilePairDirectional equal2 = new UserToUserEdgeByFilePairDirectional("User1", "FileB.java", "FileA.java", "User2", 2);

        assertTrue(instance.equals(equal1));
        assertTrue(instance.hashCode() == equal1.hashCode());
        assertTrue(instance.equals(equal2));
        assertTrue(instance.hashCode() == equal2.hashCode());
    }

    /**
     * Test of equals method. It is not equals if the (filename 1 and filename
     * 2) not equal a (filename 1 and filename 2) and (filename2 and filename1)
     */
    @Test
    public void testEqualsFalse() {
        UserToUserEdgeByFilePairDirectional notEqual1 = new UserToUserEdgeByFilePairDirectional("User2", "FileA.java", "FileB.java", "User1", 1);
        UserToUserEdgeByFilePairDirectional notEqual2 = new UserToUserEdgeByFilePairDirectional("User2", "FileB.java", "FileA.java", "User1", 1);

        assertFalse(instance.equals(notEqual1));
        assertFalse(instance.hashCode() == notEqual1.hashCode());
        assertFalse(instance.equals(notEqual2));
        assertFalse(instance.hashCode() == notEqual2.hashCode());
    }
}

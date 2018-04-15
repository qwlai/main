package seedu.address.model.movie;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import seedu.address.testutil.Assert;
//@@author slothhy
public class DurationTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new Duration(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidDuration = "";
        Assert.assertThrows(IllegalArgumentException.class, () -> new Duration(invalidDuration));
    }

    @Test
    public void isValidDuration() {
        // null duration
        Assert.assertThrows(NullPointerException.class, () -> Duration.isValidDuration(null));

        // invalid duration
        assertFalse(Duration.isValidDuration("")); // empty string
        assertFalse(Duration.isValidDuration(" ")); // spaces only
        assertFalse(Duration.isValidDuration("phone")); // non-numeric
        assertFalse(Duration.isValidDuration("9011p041")); // alphabets within digits
        assertFalse(Duration.isValidDuration("9312 1534")); // spaces within digits

        // valid duration
        assertTrue(Duration.isValidDuration("60"));
        assertTrue(Duration.isValidDuration("120"));
    }
}

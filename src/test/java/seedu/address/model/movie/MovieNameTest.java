package seedu.address.model.movie;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import seedu.address.testutil.Assert;

public class MovieNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new MovieName(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        Assert.assertThrows(IllegalArgumentException.class, () -> new MovieName(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        Assert.assertThrows(NullPointerException.class, () -> MovieName.isValidName(null));

        // invalid name
        assertFalse(MovieName.isValidName("")); // empty string
        assertFalse(MovieName.isValidName(" ")); // spaces only
        assertFalse(MovieName.isValidName("^")); // only non-alphanumeric characters
        assertFalse(MovieName.isValidName("peter*")); // contains non-alphanumeric characters

        // valid name
        assertTrue(MovieName.isValidName("peter jack")); // alphabets only
        assertTrue(MovieName.isValidName("12345")); // numbers only
        assertTrue(MovieName.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(MovieName.isValidName("Capital Tan")); // with capital letters
        assertTrue(MovieName.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
    }
}

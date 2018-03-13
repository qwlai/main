package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMOFTHEATERS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.logic.commands.AddCommand;
import seedu.address.model.cinema.Cinema;

/**
 * A utility class for Cinema.
 */
public class CinemaUtil {

    /**
     * Uses the add command word
     * Returns an add command string for adding the {@code cinema}.
     */
    public static String getAddCommand(Cinema cinema) {
        return AddCommand.COMMAND_WORD + " " + getCinemaDetails(cinema);
    }

    /**
     * Uses the add command alias
     * Returns an add command string for adding the {@code cinema}.
     */
    public static String getAddUsingAliasCommand(Cinema cinema) {
        return AddCommand.COMMAND_ALIAS + " " + getCinemaDetails(cinema);
    }

    /**
     * Returns the part of command string for the given {@code cinema}'s details.
     */
    public static String getCinemaDetails(Cinema cinema) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + cinema.getName().fullName + " ");
        sb.append(PREFIX_PHONE + cinema.getPhone().value + " ");
        sb.append(PREFIX_EMAIL + cinema.getEmail().value + " ");
        sb.append(PREFIX_ADDRESS + cinema.getAddress().value + " ");
        cinema.getTags().stream().forEach(s -> sb.append(PREFIX_TAG + s.tagName + " "));
        sb.append(PREFIX_NUMOFTHEATERS + Integer.toString(cinema.getTheaters().size()) + " ");
        return sb.toString();
    }
}

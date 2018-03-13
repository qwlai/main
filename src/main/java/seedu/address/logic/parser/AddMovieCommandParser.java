package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DURATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RATING;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STARTDATE;

import java.util.stream.Stream;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.AddMovieCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.movie.Duration;
import seedu.address.model.movie.Movie;
import seedu.address.model.movie.MovieName;
import seedu.address.model.movie.Rating;
import seedu.address.model.movie.StartDate;


/**
 * Parses input arguments and creates a new AddMovieCommand object
 */
public class AddMovieCommandParser implements Parser<AddMovieCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddMovieCommand
     * and returns an AddMovieCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddMovieCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_DURATION, PREFIX_RATING, PREFIX_STARTDATE);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_DURATION, PREFIX_RATING, PREFIX_STARTDATE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddMovieCommand.MESSAGE_USAGE));
        }

        try {
            MovieName name = ParserUtil.parseMovieName(argMultimap.getValue(PREFIX_NAME).get());
            Duration duration = ParserUtil.parseDuration(argMultimap.getValue(PREFIX_DURATION).get());
            Rating rating = ParserUtil.parseRating(argMultimap.getValue(PREFIX_RATING).get());
            StartDate startDate = ParserUtil.parseStartDate(argMultimap.getValue(PREFIX_STARTDATE).get());

            Movie movie = new Movie(name, duration, rating, startDate);

            return new AddMovieCommand(movie);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}

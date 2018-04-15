package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.cinema.Cinema;
import seedu.address.model.cinema.Theater;
import seedu.address.model.movie.Movie;
import seedu.address.model.movie.exceptions.MovieNotFoundException;
import seedu.address.model.screening.Screening;
//@@author slothhy
/**
 * Deletes a movie identified using it's last displayed index from the movie planner.
 */
public class DeleteMovieCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "deletemovie";
    public static final String COMMAND_ALIAS = "dm";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the movie identified by the index number used in the last movie listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_MOVIE_SUCCESS = "Deleted Movie: %1$s";

    private final Index targetIndex;

    private Movie movieToDelete;

    public DeleteMovieCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    /**
     * Delete screenings associated with the movie
     */
    private void deleteScreenings() {
        for (Cinema c : model.getFilteredCinemaList()) {
            for (Theater t : c.getTheaters()) {
                ArrayList<Screening> deleteList = new ArrayList<>();
                for (Screening s : t.getScreeningList()) {
                    if (s.getMovieName().equals(movieToDelete.getName().toString())) {
                        deleteList.add(s);
                    }
                }
                for (int i = 0; i < deleteList.size(); i++) {
                    t.deleteScreening(deleteList.get(i));
                }
            }
        }
    }

    @Override
    public CommandResult executeUndoableCommand() {
        requireNonNull(movieToDelete);
        try {
            deleteScreenings();
            model.deleteMovie(movieToDelete);
        } catch (MovieNotFoundException mnfe) {
            throw new AssertionError("The target movie cannot be missing");
        }

        return new CommandResult(String.format(MESSAGE_DELETE_MOVIE_SUCCESS, movieToDelete));
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        List<Movie> lastShownList = model.getFilteredMovieList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_MOVIE_DISPLAYED_INDEX);
        }

        movieToDelete = lastShownList.get(targetIndex.getZeroBased());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteMovieCommand // instanceof handles nulls
                && this.targetIndex.equals(((DeleteMovieCommand) other).targetIndex) // state check
                && Objects.equals(this.movieToDelete, ((DeleteMovieCommand) other).movieToDelete));
    }
}

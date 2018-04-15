# tinyjy94
###### \java\seedu\address\commons\util\SecurityUtilTest.java
``` java
public class SecurityUtilTest {

    private static final String TEST_DATA_FOLDER = FileUtil.getPath("./src/test/data/SecurityUtilTest/");
    private static final String password = "pass";
    private static final File plainFile = new File(TEST_DATA_FOLDER + "movieplanner.xml");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void encrypt_nullOutputFile_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        SecurityUtil.encrypt(plainFile, null, password);
    }

    @Test
    public void generateKey_nullPassword_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        SecurityUtil.generateKey(null);
    }

}
```
###### \java\seedu\address\logic\commands\AddTheaterCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model, UndoCommand) and unit tests for AddTheaterCommand.
 */

public class AddTheaterCommandTest {

    private Model model = new ModelManager(getTypicalMoviePlanner(), new UserPrefs(), new EmailManager());

    @Test
    public void execute_invalidCinemaIndexFilteredList_failure() {
        showCinemaAtIndex(model, INDEX_FIRST_CINEMA);
        Index outOfBoundIndex = INDEX_SECOND_CINEMA;
        // ensures that outOfBoundIndex is still in bounds of movie planner list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getMoviePlanner().getCinemaList().size());
        Cinema resizedCinema = new CinemaBuilder().build();

        AddTheaterCommand addTheaterCommand = prepareCommand(outOfBoundIndex, resizedCinema);

        assertCommandFailure(addTheaterCommand, model, Messages.MESSAGE_INVALID_CINEMA_DISPLAYED_INDEX);
    }

    @Test
    public void executeUndo_validIndexUnfilteredList_success() throws Exception {
        UndoRedoStack undoRedoStack = new UndoRedoStack();
        UndoCommand undoCommand = prepareUndoCommand(model, undoRedoStack);
        Cinema resizedCinema = new CinemaBuilder().build();
        AddTheaterCommand addTheaterCommand = prepareCommand(INDEX_FIRST_CINEMA, resizedCinema);
        Model expectedModel = new ModelManager(new MoviePlanner(model.getMoviePlanner()), new UserPrefs(),
                                              new EmailManager());

        // edit -> first cinema edited
        addTheaterCommand.execute();
        undoRedoStack.push(addTheaterCommand);

        // undo -> reverts movieplanner back to previous state and filtered cinema list to show all cinemas
        assertCommandSuccess(undoCommand, model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() {
        ArrayList<Theater> firstTheater = new ArrayList<>(VALID_NUMOFTHEATERS);
        ArrayList<Theater> secondTheater = new ArrayList<>(VALID_NUMOFNEWTHEATERS);
        ArrayList<Theater> thirdTheater = new ArrayList<>(VALID_NUMOFTHEATERS);
        AddTheaterCommand firstTheaterCommand = new AddTheaterCommand(INDEX_FIRST_CINEMA, firstTheater.size());
        AddTheaterCommand secondTheaterCommand = new AddTheaterCommand(INDEX_SECOND_CINEMA, secondTheater.size());
        AddTheaterCommand thirdTheaterCommand = new AddTheaterCommand(INDEX_FIRST_CINEMA, thirdTheater.size());

        // null -> returns false
        assertFalse(firstTheaterCommand.equals(null));

        // not equals -> returns false
        assertFalse(firstTheaterCommand.equals(secondTheaterCommand));

        // same value -> returns true
        assertTrue(firstTheaterCommand.equals(thirdTheaterCommand));

        // same object -> returns true
        assertTrue(firstTheaterCommand.equals(firstTheaterCommand));
    }

    /**
     * Returns an {@code EditCommand} with parameters {@code index} and {@code descriptor}
     */
    private AddTheaterCommand prepareCommand (Index index, Cinema cinema) {
        AddTheaterCommand addTheaterCommand = new AddTheaterCommand(index, cinema.getTheaters().size());
        addTheaterCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return addTheaterCommand;
    }
}
```
###### \java\seedu\address\logic\commands\DeleteTheaterCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model, UndoCommand) and unit tests for DeleteTheaterCommand.
 */
public class DeleteTheaterCommandTest {

    private Model model = new ModelManager(getTypicalMoviePlanner(), new UserPrefs(), new EmailManager());

    @Test
    public void execute_invalidCinemaIndexFilteredList_failure() {
        showCinemaAtIndex(model, INDEX_FIRST_CINEMA);
        Index outOfBoundIndex = INDEX_SECOND_CINEMA;
        // ensures that outOfBoundIndex is still in bounds of movie planner list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getMoviePlanner().getCinemaList().size());
        Cinema resizedCinema = new CinemaBuilder().build();

        DeleteTheaterCommand deleteTheaterCommand = prepareCommand(outOfBoundIndex, resizedCinema);

        assertCommandFailure(deleteTheaterCommand, model, Messages.MESSAGE_INVALID_CINEMA_DISPLAYED_INDEX);
    }

    @Test
    public void executeUndo_validIndexUnfilteredList_success() throws Exception {
        UndoRedoStack undoRedoStack = new UndoRedoStack();
        UndoCommand undoCommand = prepareUndoCommand(model, undoRedoStack);
        Cinema resizedCinema = new CinemaBuilder().build();
        DeleteTheaterCommand deleteTheaterCommand = prepareCommand(INDEX_FIRST_CINEMA, resizedCinema);
        Model expectedModel = new ModelManager(new MoviePlanner(model.getMoviePlanner()), new UserPrefs(),
                                              new EmailManager());

        // resize -> first cinema resized
        deleteTheaterCommand.execute();
        undoRedoStack.push(deleteTheaterCommand);

        // undo -> reverts movieplanner back to previous state and filtered cinema list to show all cinemas
        assertCommandSuccess(undoCommand, model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() {
        ArrayList<Theater> firstTheater = new ArrayList<>(VALID_NUMOFTHEATERS);
        ArrayList<Theater> secondTheater = new ArrayList<>(VALID_NUMOFNEWTHEATERS);
        ArrayList<Theater> thirdTheater = new ArrayList<>(VALID_NUMOFTHEATERS);
        DeleteTheaterCommand firstTheaterCommand = new DeleteTheaterCommand(INDEX_FIRST_CINEMA, firstTheater.size());
        DeleteTheaterCommand secondTheaterCommand = new DeleteTheaterCommand(INDEX_SECOND_CINEMA, secondTheater.size());
        DeleteTheaterCommand thirdTheaterCommand = new DeleteTheaterCommand(INDEX_FIRST_CINEMA, thirdTheater.size());

        // null -> returns false
        assertFalse(firstTheaterCommand.equals(null));

        // not equals -> returns false
        assertFalse(firstTheaterCommand.equals(secondTheaterCommand));

        // same value -> returns true
        assertTrue(firstTheaterCommand.equals(thirdTheaterCommand));

        // same object -> returns true
        assertTrue(firstTheaterCommand.equals(firstTheaterCommand));
    }

    /**
     * Returns a {@code DeleteTheaterCommand} with parameters {@code index} and {@code cinema}
     */
    private DeleteTheaterCommand prepareCommand(Index index, Cinema cinema) {
        DeleteTheaterCommand deleteTheaterCommand = new DeleteTheaterCommand(index, cinema.getTheaters().size() - 1);
        deleteTheaterCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return deleteTheaterCommand;
    }
}
```
###### \java\seedu\address\logic\parser\AddTheaterCommandParserTest.java
``` java
public class AddTheaterCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTheaterCommand.MESSAGE_USAGE);

    private AddTheaterCommandParser parser = new AddTheaterCommandParser();
    private Index targetIndex = INDEX_FIRST_CINEMA;

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_SENGKANG, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + THEATER_DESC_FIVE, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + THEATER_DESC_THREE, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_THEATER_DESC,
                           Theater.MESSAGE_THEATER_CONSTRAINTS); // invalid theater number
    }

    @Test
    public void parse_zeroFieldSpecified_failure() {
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT); // invalid format
    }

    @Test
    public void parse_singleFieldSpecified_success() {

        String userInput = targetIndex.getOneBased() + THEATER_DESC_THREE;
        Cinema cinema = new CinemaBuilder().withTheater(VALID_NUMOFTHEATERS).build();

        AddTheaterCommand expectedCommand = new AddTheaterCommand(targetIndex, cinema.getTheaters().size());

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_acceptsLast() {
        // both values are valid but last one is accepted
        String userInput = targetIndex.getOneBased() + THEATER_DESC_THREE + THEATER_DESC_FIVE;
        Cinema newCinema = new CinemaBuilder().withTheater(VALID_NUMOFNEWTHEATERS).build();

        AddTheaterCommand expectedCommand = new AddTheaterCommand(targetIndex, newCinema.getTheaters().size());

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_invalidValueFollowedByValidValue_success() {
        // last value is accepted
        String userInput = targetIndex.getOneBased() + INVALID_THEATER_DESC + THEATER_DESC_THREE;
        Cinema newCinema = new CinemaBuilder().withTheater(VALID_NUMOFTHEATERS).build();

        AddTheaterCommand expectedCommand = new AddTheaterCommand(targetIndex, newCinema.getTheaters().size());
        assertParseSuccess(parser, userInput, expectedCommand);
    }

}
```
###### \java\seedu\address\logic\parser\DeleteTheaterCommandParserTest.java
``` java
public class DeleteTheaterCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteTheaterCommand.MESSAGE_USAGE);

    private DeleteTheaterCommandParser parser = new DeleteTheaterCommandParser();
    private Index targetIndex = INDEX_FIRST_CINEMA;

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_SENGKANG, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + THEATER_DESC_FIVE, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + THEATER_DESC_THREE, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_THEATER_DESC,
                Theater.MESSAGE_THEATER_CONSTRAINTS); // invalid theater number
    }

    @Test
    public void parse_zeroFieldSpecified_failure() {
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT); // invalid format
    }

    @Test
    public void parse_singleFieldSpecified_success() {

        String userInput = targetIndex.getOneBased() + THEATER_DESC_THREE;
        Cinema cinema = new CinemaBuilder().withTheater(VALID_NUMOFTHEATERS).build();

        DeleteTheaterCommand expectedCommand = new DeleteTheaterCommand(targetIndex, cinema.getTheaters().size());

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_acceptsLast() {
        // both values are valid but last one is accepted
        String userInput = targetIndex.getOneBased() + THEATER_DESC_THREE + THEATER_DESC_FIVE;
        Cinema newCinema = new CinemaBuilder().withTheater(VALID_NUMOFNEWTHEATERS).build();

        DeleteTheaterCommand expectedCommand = new DeleteTheaterCommand(targetIndex, newCinema.getTheaters().size());

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_invalidValueFollowedByValidValue_success() {
        // last value is accepted
        String userInput = targetIndex.getOneBased() + INVALID_THEATER_DESC + THEATER_DESC_THREE;
        Cinema newCinema = new CinemaBuilder().withTheater(VALID_NUMOFTHEATERS).build();

        DeleteTheaterCommand expectedCommand = new DeleteTheaterCommand(targetIndex, newCinema.getTheaters().size());
        assertParseSuccess(parser, userInput, expectedCommand);
    }

}
```
###### \java\seedu\address\model\cinema\TheaterTest.java
``` java
public class TheaterTest {

    @Test
    public void constructor_invalidTheaterNumber_throwsIllegalArgumentException() {
        int invalidTheaterNumber = 0;
        Assert.assertThrows(IllegalArgumentException.class, () -> new Theater(invalidTheaterNumber));
    }

    @Test
    public void isValidTheaterNumber() {
        // invalid theater numbers
        assertFalse(Theater.isValidTheater("")); // empty string
        assertFalse(Theater.isValidTheater(" ")); // spaces only
        assertFalse(Theater.isValidTheater("phone")); // non-numeric
        assertFalse(Theater.isValidTheater("a9011p041")); // alphabets and digits
        assertFalse(Theater.isValidTheater("9312 1534")); // spaces within digits

        // valid theater numbers
        assertTrue(Theater.isValidTheater("1")); // 1 digit
        assertTrue(Theater.isValidTheater("123456")); // multiple digits
    }
}

```

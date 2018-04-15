# qwlai
###### \java\seedu\address\commons\events\ui\JumpToDateRequestEvent.java
``` java
/**
 * Indicates a request to jump a to specified date
 */
public class JumpToDateRequestEvent extends BaseEvent {

    private LocalDate date;

    public JumpToDateRequestEvent(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public LocalDate getDate() {
        return date;
    }
}
```
###### \java\seedu\address\commons\events\ui\ReloadBrowserPanelEvent.java
``` java
/**
 * Indicates that there is a need to reload browser panel
 */
public class ReloadBrowserPanelEvent extends BaseEvent {
    private Cinema cinema;
    private LocalDateTime date;
    private ReadOnlyMoviePlanner moviePlanner;

    public ReloadBrowserPanelEvent(ReadOnlyMoviePlanner moviePlanner) {
        this.moviePlanner = moviePlanner;
    }
    public ReloadBrowserPanelEvent(Cinema cinema, LocalDateTime date) {
        this.cinema = cinema;
        this.date = date;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public Cinema getCinema() {
        return cinema;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public ReadOnlyMoviePlanner getMoviePlanner() {
        return moviePlanner;
    }
}
```
###### \java\seedu\address\logic\commands\DeleteScreeningCommand.java
``` java
/**
 * Delete a movie screening from a cinema theater.
 */
public class DeleteScreeningCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "deletescreening";
    public static final String COMMAND_ALIAS = "ds";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes a movie screening from a cinema theater. "
            + "Parameters: "
            + PREFIX_CINEMA_INDEX + "CINEMA_INDEX "
            + PREFIX_NUMOFTHEATERS + "THEATER_NUMBER "
            + PREFIX_SCREENING_DATE_TIME + "SCREEN_DATE_TIME \n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CINEMA_INDEX + "2 "
            + PREFIX_NUMOFTHEATERS + "3 "
            + PREFIX_SCREENING_DATE_TIME + "13/03/2018 13:35";

    public static final String MESSAGE_SUCCESS = "Screening deleted: %1$s";

    private final Index cinemaIndex;
    private final int theaterNumber;
    private final LocalDateTime toDeleteScreeningDateTime;

    private Cinema cinema;
    private Cinema updatedCinema;
    private Screening toDelete;

    /**
     * Creates an DeleteScreeningCommand to add the specified {@code Screening}
     */
    public DeleteScreeningCommand(Index cinemaIndex, int theaterNumber, LocalDateTime toDeleteScreeningDateTime) {
        requireNonNull(cinemaIndex);
        requireNonNull(theaterNumber);
        requireNonNull(toDeleteScreeningDateTime);
        this.cinemaIndex = cinemaIndex;
        this.theaterNumber = theaterNumber;
        this.toDeleteScreeningDateTime = toDeleteScreeningDateTime;
    }

    /**
     * Adds a screening to a cinema and updates the cinema
     * @return CommandResult on successful add screening
     */
    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);
        requireNonNull(cinema);
        requireNonNull(updatedCinema);

        try {
            model.updateCinema(cinema, updatedCinema);
            EventsCenter.getInstance().post(new ReloadBrowserPanelEvent(updatedCinema, toDeleteScreeningDateTime));
        } catch (CinemaNotFoundException cnfe) {
            throw new AssertionError("The target cinema cannot be missing");
        } catch (DuplicateCinemaException dce) {
            throw new CommandException(AddCommand.MESSAGE_DUPLICATE_CINEMA);
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, toDelete));
    }

    /**
     * Checks that a screening entry is valid and adds it to the updated cinema
     * @throws CommandException if screening is invalid
     */
    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        cinema = getValidCinema();
        Theater theater = getValidTheater(cinema);
        toDelete = getValidScreening(theater);
        if (toDelete == null) {
            throw new CommandException(Messages.MESSAGE_INVALID_DELETE_SCREENING_DATE_TIME);
        }
        updatedCinema = generateUpdatedCinema(theater);
    }

    /**
     * Returns a valid screening if it's found
     */
    private Screening getValidScreening(Theater theater) throws CommandException {
        Screening screening = null;
        for (Screening s : theater.getScreeningList()) {
            if (s.getScreeningDateTime().equals(toDeleteScreeningDateTime)) {
                screening = s;
            }
        }
        return screening;
    }

    /**
     * Creates and returns a {@code Cinema} with the screening removed
     */
    private Cinema generateUpdatedCinema(Theater theater) {
        ArrayList<Theater> updatedTheaterList = generateUpdatedTheaterList(theater);
        return new Cinema(cinema.getName(), cinema.getPhone(), cinema.getEmail(),
                cinema.getAddress(), updatedTheaterList);
    }

    /**
     * Generates and returns an updated list of theaters, with the screening removed
     */
    public ArrayList<Theater> generateUpdatedTheaterList(Theater theater) {
        ArrayList<Theater> updatedTheaterList = new ArrayList<>();

        for (Theater t : cinema.getTheaters()) {
            if (t.equals(theater)) {
                Theater theaterToBeUpdated = new Theater(t.getTheaterNumber());
                ArrayList<Screening> updatedScreeningList = new ArrayList<>();

                addScreeningsToExistingTheater(t, theaterToBeUpdated, updatedScreeningList);
                updatedTheaterList.add(theaterToBeUpdated);
            } else {
                updatedTheaterList.add(t);
            }
        }
        return updatedTheaterList;
    }

    /**
     * Populates the list of screenings in a theater in the given list
     */
    private void addScreeningsToExistingTheater(Theater theater, Theater updatedTheater,
                                                ArrayList<Screening> screeningList) {
        for (Screening s : theater.getScreeningList()) {
            if (!s.equals(toDelete)) {
                screeningList.add(s);
            }
        }
        updatedTheater.setScreeningList(screeningList);
        updatedTheater.sortScreeningList();
    }

    /**
     * Gets a valid cinema based on the cinema index
     * @return a valid cinema based on the cinema index
     */
    private Cinema getValidCinema() throws CommandException {
        List<Cinema> lastShownCinemaList = model.getFilteredCinemaList();

        if (cinemaIndex.getZeroBased() >= lastShownCinemaList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CINEMA_DISPLAYED_INDEX);
        }

        Cinema cinema = lastShownCinemaList.get(cinemaIndex.getZeroBased());
        return cinema;
    }

    /**
     * Gets a valid theater based on the cinema provided
     * @return a valid theater based on the cinema provided and theater number
     */
    private Theater getValidTheater(Cinema cinema) throws CommandException {
        int theaterIndex = theaterNumber - 1;

        if (theaterIndex >= cinema.getTheaters().size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_THEATER_NUMBER);
        }

        Theater theater = cinema.getTheaters().get(theaterIndex);
        return theater;
    }

    @Override
    public boolean equals(Object other) {
        DeleteScreeningCommand ds = (DeleteScreeningCommand) other;

        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteScreeningCommand)) {
            return false;
        }

        return  this.cinemaIndex.equals(ds.cinemaIndex)
                && this.theaterNumber == ds.theaterNumber
                && Objects.equals(this.cinema, ds.cinema)
                && Objects.equals(this.updatedCinema, ds.updatedCinema)
                && Objects.equals(this.toDelete, ds.toDelete)
                && Objects.equals(this.toDeleteScreeningDateTime, ds.toDeleteScreeningDateTime);
    }
}
```
###### \java\seedu\address\logic\commands\JumpCommand.java
``` java
/**
 * Jumps to a specified date as specified by the user
 */
public class JumpCommand extends Command {

    public static final String COMMAND_WORD = "jump";
    public static final String COMMAND_ALIAS = "j";
    private static final String MESSAGE_JUMP = "Jumping to ";
    private static final String MESSAGE_WARNING = ", please select a cinema "
            + "if you do not see any schedule on the right.";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Jumps to a date for the cinema's schedule. Please remember to input a valid date.\n"
            + "Parameters: dd/MM/yyyy\n"
            + "Example: " + COMMAND_WORD + " 05/01/2018";

    private LocalDate date;

    public JumpCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public CommandResult execute() {
        String dateToJump = date.format(DateTimeFormatter.ofPattern(JumpCommandParser.DATE_FORMAT));
        EventsCenter.getInstance().post(new JumpToDateRequestEvent(date));
        return new CommandResult(MESSAGE_JUMP + dateToJump + MESSAGE_WARNING);
    }
}
```
###### \java\seedu\address\logic\parser\AddScreeningCommandParser.java
``` java
/**
 * Parses input arguments and creates a new AddScreeningCommand object
 */
public class AddScreeningCommandParser implements Parser<AddScreeningCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddScreeningCommand
     * and returns an AddScreeningCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddScreeningCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_MOVIE_INDEX, PREFIX_CINEMA_INDEX,
                        PREFIX_NUMOFTHEATERS, PREFIX_SCREENING_DATE_TIME);

        if (!arePrefixesPresent(argMultimap, PREFIX_MOVIE_INDEX, PREFIX_CINEMA_INDEX, PREFIX_NUMOFTHEATERS,
                PREFIX_SCREENING_DATE_TIME) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddScreeningCommand.MESSAGE_USAGE));
        }

        try {
            Index cinemaIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_CINEMA_INDEX).get());
            Index movieIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_MOVIE_INDEX).get());
            int theaterNumber = ParserUtil.parseTheaterNumber(argMultimap.getValue(PREFIX_NUMOFTHEATERS).get());
            LocalDateTime screeningDateTime = ParserUtil.parseScreeningDateTime(
                    argMultimap.getValue(PREFIX_SCREENING_DATE_TIME).get());

            return new AddScreeningCommand(movieIndex, cinemaIndex, theaterNumber, screeningDateTime);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        } catch (DateTimeParseException dtpe) {
            throw new ParseException(MESSAGE_INVALID_SCREEN_DATE_TIME, dtpe);
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
```
###### \java\seedu\address\logic\parser\DeleteScreeningCommandParser.java
``` java
package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_SCREEN_DATE_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CINEMA_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMOFTHEATERS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SCREENING_DATE_TIME;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.DeleteScreeningCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteScreeningCommand object
 */
public class DeleteScreeningCommandParser implements Parser<DeleteScreeningCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteScreeningCommand
     * and returns an DeleteScreeningCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteScreeningCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_CINEMA_INDEX,
                        PREFIX_NUMOFTHEATERS, PREFIX_SCREENING_DATE_TIME);

        if (!arePrefixesPresent(argMultimap, PREFIX_CINEMA_INDEX, PREFIX_NUMOFTHEATERS,
                PREFIX_SCREENING_DATE_TIME) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteScreeningCommand.MESSAGE_USAGE));
        }

        try {
            Index cinemaIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_CINEMA_INDEX).get());
            int theaterNumber = ParserUtil.parseTheaterNumber(argMultimap.getValue(PREFIX_NUMOFTHEATERS).get());
            LocalDateTime screeningDateTime = ParserUtil.parseScreeningDateTime(
                    argMultimap.getValue(PREFIX_SCREENING_DATE_TIME).get());

            return new DeleteScreeningCommand(cinemaIndex, theaterNumber, screeningDateTime);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        } catch (DateTimeParseException dtpe) {
            throw new ParseException(MESSAGE_INVALID_SCREEN_DATE_TIME, dtpe);
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
```
###### \java\seedu\address\logic\parser\JumpCommandParser.java
``` java
/**
 * Parses input arguments and creates a new FindCommand object
 */
public class JumpCommandParser implements Parser<JumpCommand> {

    public static final String DATE_FORMAT = "dd/MM/uuuu";
    private static final int START_YEAR_LIMIT = 2000;
    private static final int END_YEAR_LIMIT = 2030;

    /**
     * Parses the given {@code String} of arguments in the context of the JumpCommand
     * and returns an JumpCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public JumpCommand parse(String args) throws ParseException {
        requireNonNull(args);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT).withResolverStyle(ResolverStyle.STRICT);
        String trimmedDate = args.trim();
        try {
            LocalDate dateProvided = LocalDate.parse(trimmedDate, dtf);
            if (dateProvided.getYear() > END_YEAR_LIMIT || dateProvided.getYear() < START_YEAR_LIMIT) {
                throw new IllegalValueException(MESSAGE_INVALID_YEAR);
            }
            return new JumpCommand(dateProvided);
        } catch (DateTimeParseException e) {
            throw new ParseException (
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, JumpCommand.MESSAGE_USAGE));
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }
    }
}
```
###### \java\seedu\address\logic\parser\ParserUtil.java
``` java
    /**
     * Parses {@code String theater} into a {@code int theaterNumber}.
     * Leading and trailing whitespaces will be trimmed.
     * @throws IllegalValueException if the given {@code String theater} is invalid.
     */
    public static int parseTheaterNumber(String theater) throws IllegalValueException {
        requireNonNull(theater);
        String trimmedTheaterNumber = theater.trim();
        try {
            int theaterNumber = Integer.parseInt(trimmedTheaterNumber);
            if (theaterNumber <= 0) {
                throw new IllegalValueException(Theater.MESSAGE_THEATER_CONSTRAINTS);
            }
            return theaterNumber;
        } catch (NumberFormatException nfe) {
            throw new IllegalValueException(Theater.MESSAGE_THEATER_CONSTRAINTS);
        }
    }

    /**
     * Parses {@code String dateTime} into a {@code LocalDateTime screeningDateTime}.
     * @throws DateTimeParseException if the given {@code String dateTime} is invalid.
     * @throws IllegalValueException if the given Time is not divisible by 5.
     */
    public static LocalDateTime parseScreeningDateTime(String dateTime)
            throws IllegalValueException, DateTimeParseException {
        requireNonNull(dateTime);
        String trimmedDateTime = dateTime.trim();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withResolverStyle(ResolverStyle.STRICT);
        LocalDateTime screeningDateTime = LocalDateTime.parse(trimmedDateTime, dtf);

        if (screeningDateTime.getYear() > END_YEAR_LIMIT || screeningDateTime.getYear() < START_YEAR_LIMIT) {
            throw new IllegalValueException(Messages.MESSAGE_INVALID_YEAR);
        }

        if (screeningDateTime.getMinute() % MINUTES_USED_IN_ROUNDING_OFF != 0) {
            throw new IllegalValueException(Messages.MESSAGE_INVALID_SCREEN_DATE_TIME);
        }
        return screeningDateTime;
    }

```
###### \java\seedu\address\model\screening\Screening.java
``` java
/**
 * Represents a movie screening in a cinema theater
 */
public class Screening {

    private final String movieName;
    private Theater theater;
    private final LocalDateTime screeningDateTime;
    private final LocalDateTime screeningEndDateTime;

    public Screening(String movieName, Theater theater, LocalDateTime screeningDateTime,
                     LocalDateTime screeningEndDateTime) {
        this.movieName = movieName;
        this.theater = theater;
        this.screeningDateTime = screeningDateTime;
        this.screeningEndDateTime = screeningEndDateTime;
    }

    public String getMovieName() {
        return movieName;
    }

    public Theater getTheater() {
        return theater;
    }

    public LocalDateTime getScreeningDateTime() {
        return screeningDateTime;
    }

    public LocalDateTime getScreeningEndDateTime() {
        return screeningEndDateTime;
    }

    public void setTheater(Theater t) {
        this.theater = t;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Movie: ")
                .append(getMovieName())
                .append(" Theater: ")
                .append(theater.getTheaterNumber())
                .append(" Date: ")
                .append(screeningDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        return builder.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Screening)) {
            return false;
        }

        Screening otherScreening = (Screening) other;
        return otherScreening.getMovieName().equals(this.getMovieName())
                && otherScreening.getScreeningDateTime().equals(this.getScreeningDateTime())
                && otherScreening.getScreeningEndDateTime().equals(this.getScreeningEndDateTime());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(movieName, theater, screeningDateTime, screeningEndDateTime);
    }
}
```
###### \java\seedu\address\storage\XmlAdaptedScreening.java
``` java
/**
 * JAXB-friendly adapted version of the Screening.
 */
public class XmlAdaptedScreening {

    private static final int MINUTES_USED_IN_ROUNDING_OFF = 5;
    private static final String DATE_TIME_FORMAT = "dd/MM/uuuu HH:mm";

    @XmlElement(required = true, name = "movie")
    private String movieName;
    @XmlElement(required = true, name = "startDateTime")
    private String screeningDateTime;
    @XmlElement(required = true, name = "endDateTime")
    private String screeningEndDateTime;

    /**
     * Constructs an XmlAdaptedScreening.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedScreening() {}

    /**
     * Constructs a {@code XmlAdaptedScreening} with the given {@code theaternum}.
     */
    public XmlAdaptedScreening(String movieName, String screeningDateTime, String screeningEndDateTime) {
        this.movieName = movieName;
        this.screeningDateTime = screeningDateTime;
        this.screeningEndDateTime = screeningEndDateTime;
    }

    /**
     * Converts a given Screening into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created
     */
    public XmlAdaptedScreening(Screening source) {
        movieName = source.getMovieName();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        screeningDateTime = source.getScreeningDateTime().format(dtf);
        screeningEndDateTime = source.getScreeningEndDateTime().format(dtf);
    }

    /**
     * Returns a hashmap containing movieName as the key
     * startDateTime and endDateTime is stored in the arrayList and put as the value in the hashmap
     * @throws IllegalValueException if there were any data constraints violated in the adapted screening
     */
    public HashMap<String, ArrayList<LocalDateTime>> toScreening() throws IllegalValueException {
        HashMap<String, ArrayList<LocalDateTime>> screeningMap = new HashMap<>();

        if (!MovieName.isValidName(movieName)) {
            throw new IllegalValueException(MovieName.MESSAGE_MOVIENAME_CONSTRAINTS);
        }

        if (getValidDateTime().size() == 2) {
            screeningMap.put(movieName, getValidDateTime());
        }

        return screeningMap;
    }

    /**
     * Returns an ArrayList of LocalDateTime consisting of the startDateTime and endDateTime
     * @throws IllegalValueException if the date time given is not in the right format
     */
    private ArrayList<LocalDateTime> getValidDateTime() throws IllegalValueException {
        ArrayList<LocalDateTime> dateTimeDetailsList = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withResolverStyle(ResolverStyle.STRICT);

        try {
            LocalDateTime dateTime = LocalDateTime.parse(screeningDateTime, dtf);
            LocalDateTime endDateTime = LocalDateTime.parse(screeningEndDateTime, dtf);

            if (dateTime.getMinute() % MINUTES_USED_IN_ROUNDING_OFF != 0
                    || endDateTime.getMinute() % MINUTES_USED_IN_ROUNDING_OFF != 0) {
                throw new IllegalValueException(Messages.MESSAGE_INVALID_SCREEN_DATE_TIME);
            }

            dateTimeDetailsList.add(dateTime);
            dateTimeDetailsList.add(endDateTime);

        } catch (DateTimeParseException dtpe) {
            throw new ParseException(MESSAGE_INVALID_SCREEN_DATE_TIME, dtpe);
        }
        return dateTimeDetailsList;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof XmlAdaptedScreening)) {
            return false;
        }

        XmlAdaptedScreening otherScreening = (XmlAdaptedScreening) other;
        return Objects.equals(movieName, otherScreening.movieName)
                && Objects.equals(screeningDateTime, otherScreening.screeningDateTime)
                && Objects.equals(screeningEndDateTime, otherScreening.screeningEndDateTime);
    }
}
```
###### \java\seedu\address\storage\XmlAdaptedTheater.java
``` java
    /**
     * Constructs a {@code XmlAdaptedTheater} with the given {@code theaterNumber}.
     */
    public XmlAdaptedTheater(int theaterNumber, ArrayList<XmlAdaptedScreening> screenings) {
        this.theaterNumber = theaterNumber;
        if (screenings != null) {
            this.screenings = new ArrayList<>(screenings);
        }
    }

    /**
     * Converts a given Theater into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created
     */
    public XmlAdaptedTheater(Theater source) {
        this.theaterNumber = source.getTheaterNumber();

        for (Screening screening : source.getScreeningList()) {
            screenings.add(new XmlAdaptedScreening(screening));
        }
    }


    /**
     * Converts this jaxb-friendly adapted theater object into the model's Theater object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted theater
     */
    public Theater toModelType() throws IllegalValueException {
        if (!Theater.isValidTheater(String.valueOf(theaterNumber))) {
            throw new IllegalValueException(Theater.MESSAGE_THEATER_CONSTRAINTS);
        }

        Theater theater = new Theater(theaterNumber);
        for (XmlAdaptedScreening s : screenings) {
            String movieName = s.toScreening().keySet().stream().findFirst().get();
            if (movieName != null) {
                LocalDateTime startDateTime = s.toScreening().get(movieName).get(0);
                LocalDateTime endDateTime = s.toScreening().get(movieName).get(1);
                Screening screening = new Screening(movieName, theater, startDateTime, endDateTime);
                theater.addScreeningToTheater(screening);
            }
        }

        return theater;
    }

```
###### \java\seedu\address\ui\BrowserPanel.java
``` java
/**
 * The Browser Panel of the App.
 */
public class BrowserPanel extends UiPart<Region> {

    private static final String FXML = "BrowserPanel.fxml";
    private static final String DATE_FORMAT = "dd/MM/uu";
    private static final String TIME_FORMAT = "HH:mm";
    private static final String SCREENING_DISPLAY_FORMAT = "%s\n%s - %s";

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    @FXML
    private StackPane browserPane;
    @FXML
    private Label date;
    @FXML
    private Label cinema;

    private Cinema currentCinema = null;
    private LocalDate currentDate = null;

    public BrowserPanel() {
        super(FXML);
        // To prevent triggering events for typing inside the loaded Web page.
        getRoot().setOnKeyPressed(Event::consume);
        registerAsAnEventHandler(this);
    }

    /**
     * Loads the schedule of the provided cinema
     * @param cinema
     */
    private void loadCinemaSchedule(Cinema cinema, LocalDate providedDate) {
        DetailedDayView detailedDayView = new DetailedDayView();
        setUpDayView(detailedDayView, providedDate);

        ArrayList<Theater> theaterList = cinema.getTheaters();
        CalendarSource theatersSchedule = new CalendarSource("Theaters");

        int count = 1;

        for (Theater t : theaterList) {
            Calendar c = new Calendar(Integer.toString(t.getTheaterNumber()));
            c.setShortName(Integer.toString(t.getTheaterNumber()));
            setColorStyle(c, count);
            c.setReadOnly(true);
            theatersSchedule.getCalendars().add(c);

            ArrayList<Screening> screeningList = t.getScreeningList();

            // add entry
            for (Screening s : screeningList) {
                Entry<String> movieScreening = new Entry<>(s.getMovieName());
                String startTime = s.getScreeningDateTime().format(DateTimeFormatter.ofPattern(TIME_FORMAT));
                String endTime = s.getScreeningEndDateTime().format(DateTimeFormatter.ofPattern(TIME_FORMAT));
                movieScreening.setInterval(s.getScreeningDateTime(), s.getScreeningEndDateTime());
                movieScreening.setTitle(String.format(SCREENING_DISPLAY_FORMAT, s.getMovieName(), startTime, endTime));
                c.addEntry(movieScreening);
            }
            count++;
        }

        detailedDayView.getCalendarSources().setAll(theatersSchedule);
        addNodesToBrowserPane(detailedDayView);
    }

    /**
     * Adds node objects into browser pane
     */
    private void addNodesToBrowserPane(DetailedDayView detailedDayView) {
        browserPane.getChildren().add(detailedDayView);
        browserPane.setMargin(detailedDayView, new Insets(30, 0, 0, 0));
        browserPane.getChildren().add(this.cinema);
        browserPane.getChildren().add(this.date);
    }

    /**
     * Sets the color style for the provided calendar
     */
    private void setColorStyle(Calendar c, int count) {
        switch (count % 7) {
        case 1:
            c.setStyle(Calendar.Style.STYLE1);
            break;
        case 2:
            c.setStyle(Calendar.Style.STYLE2);
            break;
        case 3:
            c.setStyle(Calendar.Style.STYLE3);
            break;
        case 4:
            c.setStyle(Calendar.Style.STYLE4);
            break;
        case 5:
            c.setStyle(Calendar.Style.STYLE5);
            break;
        case 6:
            c.setStyle(Calendar.Style.STYLE6);
            break;
        case 7:
            c.setStyle(Calendar.Style.STYLE7);
            break;
        default:
            c.setStyle(Calendar.Style.STYLE1);
        }
    }

    /**
     * Sets up the day view for scheduler
     */
    private void setUpDayView(DetailedDayView detailedDayView, LocalDate providedDate) {
        date.setText(providedDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        cinema.setText(currentCinema.getName().toString());
        detailedDayView.setLayoutY(200);
        detailedDayView.setLayout(DateControl.Layout.SWIMLANE);
        detailedDayView.setEnableCurrentTimeMarker(false);
        detailedDayView.setDate(providedDate);
        detailedDayView.setMouseTransparent(true);
        detailedDayView.setShowAllDayView(false);
        detailedDayView.setShowScrollBar(false);
        detailedDayView.setVisibleHours(24);

        detailedDayView.setBackground(new Background(new BackgroundFill(Color.web("#ffffff"),
                CornerRadii.EMPTY, Insets.EMPTY)));
    }

    /**
     * Checks if a cinema exist in a given list of cinemas
     */
    private boolean hasCinema(ObservableList<Cinema> cinemas) {
        for (Cinema c : cinemas) {
            if (c.getName().equals(currentCinema.getName())) {
                currentCinema = c;
                return true;
            }
        }
        return false;
    }

    /**
     * Handles CinemaPanelSelectionChangedEvent
     * Reloads the schedule of newly selected cinema
     */
    @Subscribe
    private void handleCinemaPanelSelectionChangedEvent(CinemaPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        browserPane.getChildren().clear();
        currentDate = LocalDate.now();
        currentCinema = event.getNewSelection().cinema;
        loadCinemaSchedule(currentCinema, currentDate);
    }

    /**
     * Reloads the schedule of the cinema provided
     */
    @Subscribe
    private void handleReloadBrowserPanelEvent(ReloadBrowserPanelEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Local data changed, refreshing browser view"));
        browserPane.getChildren().clear();

        if (event.getMoviePlanner() != null) { // handling undo, redo, clear all
            if (hasCinema(event.getMoviePlanner().getCinemaList())) {
                loadCinemaSchedule(currentCinema, currentDate);
            }
        } else { // handling new screening
            currentCinema = event.getCinema();
            currentDate = event.getDate().toLocalDate();
            loadCinemaSchedule(currentCinema, currentDate);
        }
    }

    /**
     * Handles JumpToDateRequestEvent
     * Jumps to specified date in the scheduler
     */
    @Subscribe
    private void handleJumpCommandEvent(JumpToDateRequestEvent event) {
        try {
            logger.info(LogsCenter.getEventHandlingLogMessage(event, "Jumping to date: " + event.getDate()));
            currentDate = event.getDate();
            browserPane.getChildren().clear();
            loadCinemaSchedule(currentCinema, currentDate);
        } catch (NullPointerException npe) {
            logger.severe(LogsCenter.getEventHandlingLogMessage(event, "Null cinema card."));
        }
    }
}
```
###### \java\seedu\address\ui\MovieCard.java
``` java
/**
 * An UI component that displays information of a {@code Movie}.
 */
public class MovieCard extends UiPart<Region> {

    private static final String FXML = "MovieListCard.fxml";
    private static final String[] TAG_COLORS = {"red", "blue", "orange", "green", "yellow", "grey", "white", "black",
        "pink", "brown"};

    public final Movie movie;

    @FXML
    private HBox cardPane;
    @FXML
    private Label movieName;
    @FXML
    private Label id;
    @FXML
    private Label duration;
    @FXML
    private Label rating;
    @FXML
    private Label startDate;
    @FXML
    private FlowPane tags;

    public MovieCard(Movie movie, int displayedIndex) {
        super(FXML);
        this.movie = movie;
        id.setText(displayedIndex + ". ");
        movieName.setText(movie.getName().toString());
        duration.setText((movie.getDuration().toString()));
        rating.setText((movie.getRating().toString()));
        startDate.setText((movie.getStartDate().toString()));
        initializeTags(movie);
    }

    /**
     * Returns color for {@code tagName} label
     */
    private String getTagColor(String tagName) {
        return TAG_COLORS[Math.abs(tagName.hashCode()) % TAG_COLORS.length];
    }

    /**
     * Create tag labels for {@code movie}
     */
    private void initializeTags(Movie movie) {
        movie.getTags().forEach(tag -> {
            Label tagLabel = new Label(tag.tagName);
            tagLabel.getStyleClass().add(getTagColor(tag.tagName));
            tags.getChildren().add(tagLabel);
        });
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MovieCard)) {
            return false;
        }

        // state check
        MovieCard card = (MovieCard) other;
        return id.getText().equals(card.id.getText())
                && movie.equals(card.movie);
    }
}
```
###### \java\seedu\address\ui\MovieListPanel.java
``` java
/**
 * Panel containing the list of movies.
 */
public class MovieListPanel extends UiPart<Region> {
    private static final String FXML = "MovieListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(MovieListPanel.class);

    @FXML
    private ListView<MovieCard> movieListView;

    public MovieListPanel(ObservableList<Movie> movieList) {
        super(FXML);
        setConnections(movieList);
        registerAsAnEventHandler(this);
    }

    private void setConnections(ObservableList<Movie> movieList) {
        ObservableList<MovieCard> mappedList = EasyBind.map(
                movieList, (movie) -> new MovieCard(movie, movieList.indexOf(movie) + 1));
        movieListView.setItems(mappedList);
        movieListView.setCellFactory(listView -> new MovieListViewCell());
    }

    /**
     * Scrolls to the {@code MovieCard} at the {@code index} and selects it.
     */
    private void scrollTo(int index) {
        Platform.runLater(() -> {
            movieListView.scrollTo(index);
            movieListView.getSelectionModel().clearAndSelect(index);
        });
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code MovieCard}.
     */
    class MovieListViewCell extends ListCell<MovieCard> {

        @Override
        protected void updateItem(MovieCard movie, boolean empty) {
            super.updateItem(movie, empty);

            if (empty || movie == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(movie.getRoot());
            }
        }
    }
}
```
###### \resources\view\BrowserPanel.fxml
``` fxml
<StackPane fx:id="browserPane" prefHeight="412.0" prefWidth="313.0" xmlns="http://javafx.com/javafx/9.0.1" xmlns:fx="http://javafx.com/fxml/1">
   <padding>
      <Insets bottom="10.0" left="10.0" right="10.0" top="10.0" />
   </padding>
   <children>
       <Label fx:id="cinema" StackPane.alignment="TOP_CENTER"/>
      <Label fx:id="date" StackPane.alignment="TOP_LEFT">
         <StackPane.margin>
            <Insets left="7.0" top="35.0" />
         </StackPane.margin>
      </Label>
   </children>
</StackPane>
```

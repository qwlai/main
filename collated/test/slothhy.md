# slothhy
###### \java\seedu\address\logic\commands\AddMovieCommandTest.java
``` java
public class AddMovieCommandTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void constructor_nullMovie_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new AddMovieCommand(null);
    }

    @Test
    public void execute_movieAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingMovieAdded modelStub = new ModelStubAcceptingMovieAdded();
        Movie validMovie = new MovieBuilder().build();

        CommandResult commandResult = getAddMovieCommandForMovie(validMovie, modelStub).execute();

        assertEquals(String.format(AddMovieCommand.MESSAGE_SUCCESS, validMovie), commandResult.feedbackToUser);
        assertEquals(Arrays.asList(validMovie), modelStub.moviesAdded);
    }

    @Test
    public void execute_duplicateMovie_throwsCommandException() throws Exception {
        ModelStub modelStub = new ModelStubThrowingDuplicateMovieException();
        Movie validMovie = new MovieBuilder().build();

        thrown.expect(CommandException.class);
        thrown.expectMessage(AddMovieCommand.MESSAGE_DUPLICATE_MOVIE);

        getAddMovieCommandForMovie(validMovie, modelStub).execute();
    }

    @Test
    public void equals() {
        Movie incredibles = new MovieBuilder().withMovieName("The Incredibles").build();
        Movie batman = new MovieBuilder().withMovieName("Batman Begins").build();
        AddMovieCommand addIncrediblesCommand = new AddMovieCommand(incredibles);
        AddMovieCommand addBatmanCommand = new AddMovieCommand(batman);

        // same object -> returns true
        assertTrue(addIncrediblesCommand.equals(addIncrediblesCommand));

        // same values -> returns true
        AddMovieCommand addIncrediblesCommandCopy = new AddMovieCommand(incredibles);
        assertTrue(addIncrediblesCommand.equals(addIncrediblesCommandCopy));

        // different types -> returns false
        assertFalse(addIncrediblesCommand.equals(1));

        // null -> returns false
        assertFalse(addIncrediblesCommand.equals(null));

        // different movie -> returns false
        assertFalse(addIncrediblesCommand.equals(addBatmanCommand));
    }

    /**
     * Generates a new AddMovieCommand with the details of the given movie.
     */
    private AddMovieCommand getAddMovieCommandForMovie(Movie movie, Model model) {
        AddMovieCommand command = new AddMovieCommand(movie);
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void addCinema(Cinema cinema) throws DuplicateCinemaException {
            fail("This method should not be called.");
        }

        @Override
        public void resetData(ReadOnlyMoviePlanner newData) {
            fail("This method should not be called.");
        }

        @Override
        public ReadOnlyMoviePlanner getMoviePlanner() {
            fail("This method should not be called.");
            return null;
        }

        @Override
        public void deleteCinema(Cinema target) throws CinemaNotFoundException {
            fail("This method should not be called.");
        }

        @Override
        public void updateCinema(Cinema target, Cinema editedCinema)
                throws DuplicateCinemaException {
            fail("This method should not be called.");
        }

        @Override
        public void deleteTag(Tag tag) throws TagNotFoundException {
            fail("This method should not be called.");
        }

        @Override
        public ObservableList<Cinema> getFilteredCinemaList() {
            fail("This method should not be called.");
            return null;
        }

        @Override
        public ObservableList<Movie> getFilteredMovieList() {
            fail("This method should not be called.");
            return null;
        }

        @Override
        public void addMovie(Movie movie) throws DuplicateMovieException {
            fail("This method should not be called.");
        }

        @Override
        public void deleteMovie(Movie target) throws MovieNotFoundException {
            fail("This method should not be called.");
        }

        @Override
        public void updateMovie(Movie target, Movie editedMovie)
                throws DuplicateMovieException {
            fail("This method should not be called.");
        }

        @Override
        public void updateFilteredCinemaList(Predicate<Cinema> predicate) {
            fail("This method should not be called.");
        }

        @Override
        public void updateFilteredMovieList(Predicate<Movie> predicate) {
            fail("This method should not be called.");
        }

        @Override
        public Email getEmailManager() {
            fail("This method should not be called.");
            return null;
        }

        @Override
        public void sendEmail(MessageDraft message) throws EmailLoginInvalidException, EmailMessageEmptyException,
                EmailRecipientsEmptyException, AuthenticationFailedException {
            fail("This method should not be called.");
        }

        @Override
        public void loginEmailAccount(String [] loginDetails) throws EmailLoginInvalidException {
            fail("This method should not be called.");
        }

        @Override
        public String getEmailStatus() {
            fail("This method should not be called.");
            return null;
        }

        @Override
        public void clearEmailDraft() {
            fail("This method should not be called.");
        }

        @Override
        public void draftEmail(MessageDraft message) {
            fail("This method should not be called.");
        }
    }

    /**
     * A Model stub that always throw a DuplicateMovieException when trying to add a movie.
     */
    private class ModelStubThrowingDuplicateMovieException extends ModelStub {
        @Override
        public void addMovie(Movie movie) throws DuplicateMovieException {
            throw new DuplicateMovieException();
        }

        @Override
        public ReadOnlyMoviePlanner getMoviePlanner() {
            return new MoviePlanner();
        }
    }

    /**
     * A Model stub that always accept the movie being added.
     */
    private class ModelStubAcceptingMovieAdded extends ModelStub {
        final ArrayList<Movie> moviesAdded = new ArrayList<>();

        @Override
        public void addMovie(Movie movie) throws DuplicateMovieException {
            requireNonNull(movie);
            moviesAdded.add(movie);
        }

        @Override
        public ReadOnlyMoviePlanner getMoviePlanner() {
            return new MoviePlanner();
        }
    }

}
```
###### \java\seedu\address\logic\parser\AddMovieCommandParserTest.java
``` java
public class AddMovieCommandParserTest {
    private AddMovieCommandParser parser = new AddMovieCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Movie expectedMovie = new MovieBuilder().withMovieName(VALID_MOVIENAME_MARVEL)
                .withDuration(VALID_DURATION_MARVEL).withRating(VALID_RATING_MARVEL)
                .withStartDate(VALID_STARTDATE_MARVEL).withTags(VALID_TAG_SUPERHERO).build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + MOVIENAME_DESC_MARVEL
                        + DURATION_DESC_MARVEL + RATING_DESC_MARVEL + STARTDATE_DESC_MARVEL + TAG_DESC_SUPERHERO,
                new AddMovieCommand(expectedMovie));

        // multiple movie names - last movie name accepted
        assertParseSuccess(parser, MOVIENAME_DESC_INCREDIBLES + MOVIENAME_DESC_MARVEL
                        + DURATION_DESC_MARVEL + RATING_DESC_MARVEL + STARTDATE_DESC_MARVEL + TAG_DESC_SUPERHERO,
                new AddMovieCommand(expectedMovie));

        // multiple durations - last duration accepted
        assertParseSuccess(parser, MOVIENAME_DESC_MARVEL + DURATION_DESC_INCREDIBLES
                        + DURATION_DESC_MARVEL + RATING_DESC_MARVEL + STARTDATE_DESC_MARVEL + TAG_DESC_SUPERHERO,
                new AddMovieCommand(expectedMovie));

        // multiple ratings - last rating accepted
        assertParseSuccess(parser, MOVIENAME_DESC_MARVEL + DURATION_DESC_MARVEL
                        + RATING_DESC_INCREDIBLES + RATING_DESC_MARVEL + STARTDATE_DESC_MARVEL + TAG_DESC_SUPERHERO,
                new AddMovieCommand(expectedMovie));

        // multiple startDates - last startDate accepted
        assertParseSuccess(parser, MOVIENAME_DESC_MARVEL + DURATION_DESC_MARVEL
                        + RATING_DESC_MARVEL + STARTDATE_DESC_INCREDIBLES + STARTDATE_DESC_MARVEL + TAG_DESC_SUPERHERO,
                new AddMovieCommand(expectedMovie));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddMovieCommand.MESSAGE_USAGE);

        // missing movieName prefix
        assertParseFailure(parser, VALID_MOVIENAME_INCREDIBLES + DURATION_DESC_INCREDIBLES
                + RATING_DESC_INCREDIBLES + STARTDATE_DESC_INCREDIBLES + TAG_DESC_COMEDY, expectedMessage);

        // missing duration prefix
        assertParseFailure(parser, MOVIENAME_DESC_INCREDIBLES + VALID_DURATION_INCREDIBLES
                + RATING_DESC_INCREDIBLES + STARTDATE_DESC_INCREDIBLES + TAG_DESC_COMEDY, expectedMessage);

        // missing rating prefix
        assertParseFailure(parser, MOVIENAME_DESC_INCREDIBLES + DURATION_DESC_INCREDIBLES
                + VALID_RATING_INCREDIBLES + STARTDATE_DESC_INCREDIBLES + TAG_DESC_COMEDY, expectedMessage);

        // missing startDate prefix
        assertParseFailure(parser, MOVIENAME_DESC_INCREDIBLES + DURATION_DESC_INCREDIBLES
                + RATING_DESC_INCREDIBLES + VALID_STARTDATE_INCREDIBLES + TAG_DESC_COMEDY, expectedMessage);

        // all prefixes missing
        assertParseFailure(parser, VALID_MOVIENAME_INCREDIBLES + VALID_DURATION_INCREDIBLES
                + VALID_RATING_INCREDIBLES + VALID_STARTDATE_INCREDIBLES + VALID_TAG_COMEDY, expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid movieName
        assertParseFailure(parser, INVALID_MOVIENAME_DESC + DURATION_DESC_MARVEL + RATING_DESC_MARVEL
                + STARTDATE_DESC_MARVEL + TAG_DESC_SUPERHERO, MovieName.MESSAGE_MOVIENAME_CONSTRAINTS);

        // invalid duration
        assertParseFailure(parser, MOVIENAME_DESC_MARVEL + INVALID_DURATION_DESC + RATING_DESC_MARVEL
                + STARTDATE_DESC_MARVEL + TAG_DESC_SUPERHERO, Duration.MESSAGE_DURATION_CONSTRAINTS);

        // invalid rating
        assertParseFailure(parser, MOVIENAME_DESC_MARVEL + DURATION_DESC_MARVEL + INVALID_RATING_DESC
                + STARTDATE_DESC_MARVEL + TAG_DESC_SUPERHERO, Rating.MESSAGE_RATING_CONSTRAINTS);

        // invalid startDate
        assertParseFailure(parser, MOVIENAME_DESC_MARVEL + DURATION_DESC_MARVEL + RATING_DESC_MARVEL
                + INVALID_STARTDATE_DESC + TAG_DESC_SUPERHERO, StartDate.MESSAGE_STARTDATE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, INVALID_MOVIENAME_DESC + DURATION_DESC_MARVEL + RATING_DESC_MARVEL
                + INVALID_STARTDATE_DESC + TAG_DESC_SUPERHERO, MovieName.MESSAGE_MOVIENAME_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + MOVIENAME_DESC_MARVEL + DURATION_DESC_MARVEL
                        + RATING_DESC_MARVEL + STARTDATE_DESC_MARVEL,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddMovieCommand.MESSAGE_USAGE));
    }
}
```
###### \java\seedu\address\model\movie\DurationTest.java
``` java
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
```
###### \java\seedu\address\model\movie\MovieNameTest.java
``` java
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
```
###### \java\seedu\address\model\movie\NameContainsKeywordsPredicateTest.java
``` java
public class NameContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameContainsKeywordsPredicate firstPredicateCopy = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different cinema -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Collections.singletonList("Ghost"));
        assertTrue(predicate.test(new MovieBuilder().withMovieName("Ghost Hunter").build()));

        // Multiple keywords
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("Ghost", "Hunter"));
        assertTrue(predicate.test(new MovieBuilder().withMovieName("Ghost Hunter").build()));

        // Mixed-case keywords
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("sPiDerMan"));
        assertTrue(predicate.test(new MovieBuilder().withMovieName("Spiderman").build()));

        // Zero keywords
        predicate = new NameContainsKeywordsPredicate(Collections.emptyList());
        assertTrue(predicate.test(new MovieBuilder().withMovieName("Ghost").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Non-matching keyword
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Arrays.asList("Incredibles"));
        assertFalse(predicate.test(new MovieBuilder().withMovieName("Spiderman").build()));
    }
}
```
###### \java\seedu\address\storage\XmlAdaptedMovieTest.java
``` java
public class XmlAdaptedMovieTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_DURATION = "+651234";
    private static final String INVALID_RATING = " ";
    private static final String INVALID_STARTDATE = "03031231";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = ABTM4.getName().toString();
    private static final String VALID_DURATION = ABTM4.getDuration().toString();
    private static final String VALID_RATING = ABTM4.getRating().toString();
    private static final String VALID_STARTDATE = ABTM4.getStartDate().toString();
    private static final List<XmlAdaptedTag> VALID_TAGS = ABTM4.getTags().stream()
            .map(XmlAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validMovieDetails_returnsMovie() throws Exception {
        XmlAdaptedMovie movie = new XmlAdaptedMovie(ABTM4);
        assertEquals(ABTM4, movie.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        XmlAdaptedMovie movie =
                new XmlAdaptedMovie(INVALID_NAME, VALID_DURATION, VALID_RATING, VALID_STARTDATE, VALID_TAGS);
        String expectedMessage = MovieName.MESSAGE_MOVIENAME_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, movie::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        XmlAdaptedMovie movie = new XmlAdaptedMovie(null, VALID_DURATION, VALID_RATING, VALID_STARTDATE,
                VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, MovieName.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, movie::toModelType);
    }

    @Test
    public void toModelType_invalidDuration_throwsIllegalValueException() {
        XmlAdaptedMovie movie =
                new XmlAdaptedMovie(VALID_NAME, INVALID_DURATION, VALID_RATING, VALID_STARTDATE, VALID_TAGS);
        String expectedMessage = Duration.MESSAGE_DURATION_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, movie::toModelType);
    }

    @Test
    public void toModelType_nullDuration_throwsIllegalValueException() {
        XmlAdaptedMovie movie = new XmlAdaptedMovie(VALID_NAME, null, VALID_RATING,
                VALID_STARTDATE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Duration.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, movie::toModelType);
    }

    @Test
    public void toModelType_invalidRating_throwsIllegalValueException() {
        XmlAdaptedMovie movie =
                new XmlAdaptedMovie(VALID_NAME, VALID_DURATION, INVALID_RATING, VALID_STARTDATE, VALID_TAGS);
        String expectedMessage = Rating.MESSAGE_RATING_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, movie::toModelType);
    }

    @Test
    public void toModelType_nullRating_throwsIllegalValueException() {
        XmlAdaptedMovie movie = new XmlAdaptedMovie(VALID_NAME, VALID_DURATION, null,
                VALID_STARTDATE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Rating.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, movie::toModelType);
    }

    @Test
    public void toModelType_invalidStartDate_throwsIllegalValueException() {
        XmlAdaptedMovie movie =
                new XmlAdaptedMovie(VALID_NAME, VALID_DURATION, VALID_RATING, INVALID_STARTDATE, VALID_TAGS);
        String expectedMessage = StartDate.MESSAGE_STARTDATE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, movie::toModelType);
    }

    @Test
    public void toModelType_nullStartDate_throwsIllegalValueException() {
        XmlAdaptedMovie movie = new XmlAdaptedMovie(VALID_NAME, VALID_DURATION, VALID_RATING, null,
                VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, StartDate.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, movie::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<XmlAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new XmlAdaptedTag(INVALID_TAG));
        XmlAdaptedMovie movie =
                new XmlAdaptedMovie(VALID_NAME, VALID_DURATION, VALID_RATING, VALID_STARTDATE, invalidTags);
        Assert.assertThrows(IllegalValueException.class, movie::toModelType);
    }

}
```
###### \java\seedu\address\testutil\MovieBuilder.java
``` java
/**
 * A utility class to help with building Movie objects.
 */
public class MovieBuilder {

    public static final String DEFAULT_MOVIENAME = "The Incredibles";
    public static final String DEFAULT_DURATION = "90";
    public static final String DEFAULT_RATING = "PG";
    public static final String DEFAULT_STARTDATE = "13/03/2018";
    public static final String DEFAULT_TAGS = "superhero";

    private MovieName movieName;
    private Duration duration;
    private Rating rating;
    private StartDate startDate;
    private Set<Tag> tags;

    public MovieBuilder() {
        movieName = new MovieName(DEFAULT_MOVIENAME);
        duration = new Duration(DEFAULT_DURATION);
        rating = new Rating(DEFAULT_RATING);
        startDate = new StartDate(DEFAULT_STARTDATE);
        tags = SampleDataUtil.getTagSet(DEFAULT_TAGS);
    }

    /**
     * Initializes the MovieBuilder with the data of {@code movieToCopy}.
     */
    public MovieBuilder(Movie movieToCopy) {
        movieName = movieToCopy.getName();
        duration = movieToCopy.getDuration();
        rating = movieToCopy.getRating();
        startDate = movieToCopy.getStartDate();
        tags = new HashSet<>(movieToCopy.getTags());
    }

    /**
     * Sets the {@code MovieName} of the {@code Movie} that we are building.
     */
    public MovieBuilder withMovieName(String movieName) {
        this.movieName = new MovieName(movieName);
        return this;
    }

    /**
     * Sets the {@code Duration} of the {@code Movie} that we are building.
     */
    public MovieBuilder withDuration(String duration) {
        this.duration = new Duration(duration);
        return this;
    }

    /**
     * Sets the {@code Rating} of the {@code Movie} that we are building.
     */
    public MovieBuilder withRating(String rating) {
        this.rating = new Rating(rating);
        return this;
    }

    /**
     * Sets the {@code StartDate} of the {@code Movie} that we are building.
     */
    public MovieBuilder withStartDate(String startDate) {
        this.startDate = new StartDate(startDate);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code Cinema} that we are building.
     */
    public MovieBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    public Movie build() {
        return new Movie(movieName, duration, rating, startDate, tags);
    }

}
```
###### \java\seedu\address\testutil\MovieUtil.java
``` java
/**
 * A utility class for Movie.
 */
public class MovieUtil {

    /**
     * Uses the add command word
     * Returns an add command string for adding the {@code movie}.
     */
    public static String getAddCommand(Movie movie) {
        return AddMovieCommand.COMMAND_WORD + " " + getMovieDetails(movie);
    }

    /**
     * Uses the add command alias
     * Returns an add command string for adding the {@code movie}.
     */
    public static String getAddUsingAliasCommand(Movie movie) {
        return AddMovieCommand.COMMAND_ALIAS + " " + getMovieDetails(movie);
    }

    /**
     * Returns the part of command string for the given {@code movie}'s details.
     */
    public static String getMovieDetails(Movie movie) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + movie.getName().movieName + " ");
        sb.append(PREFIX_DURATION + movie.getDuration().duration + " ");
        sb.append(PREFIX_RATING + movie.getRating().rating + " ");
        sb.append(PREFIX_STARTDATE + movie.getStartDate().startDate + " ");
        movie.getTags().stream().forEach(s -> sb.append(PREFIX_TAG + s.tagName + " "));
        return sb.toString();
    }
}
```

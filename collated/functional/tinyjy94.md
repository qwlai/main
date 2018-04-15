# tinyjy94
###### \java\seedu\address\commons\events\storage\DecryptionRequestEvent.java
``` java
/**
 * Indicates a request for decryption
 */
public class DecryptionRequestEvent extends BaseEvent {
    private final String password;

    public DecryptionRequestEvent(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return password;
    }

    public String getPassword() {
        return password;
    }
}
```
###### \java\seedu\address\commons\events\storage\EncryptionRequestEvent.java
``` java
/**
 * Indicates a request for encryption
 */
public class EncryptionRequestEvent extends BaseEvent {
    private final String password;

    public EncryptionRequestEvent(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return password;
    }

    public String getPassword() {
        return password;
    }
}
```
###### \java\seedu\address\commons\util\SecurityUtil.java
``` java
/**
 * Contains encryption and decryption functions
 */
public class SecurityUtil {

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final int AES_Key_Size = 128;
    private static final int iteration = 65536;

    public static void encrypt(String filepath, String encryptedFilePath, String password) {
        encrypt(new File(filepath), new File(encryptedFilePath), password);
    }

    /**
     * Encrypts the given file using AES key using Key given.
     */
    public static void encrypt(File inputFile, File outputFile, String password) {
        try {
            Key pw = generateKey(password);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pw);
            processFile(cipher, inputFile, outputFile);
        } catch (InvalidKeyException ike) {
            logger.severe("Invalid key length provided " + StringUtil.getDetails(ike));
            throw new AssertionError("Invalid key length.");
        } catch (NoSuchAlgorithmException nsae) {
            logger.severe("Invalid algorithm provided " + StringUtil.getDetails(nsae));
            throw new AssertionError("Invalid algorithm.");
        } catch (NoSuchPaddingException bpe) {
            logger.severe("Invalid padding provided " + StringUtil.getDetails(bpe));
            throw new AssertionError("Invalid padding.");
        } catch (IOException e) {
            logger.severe("File does not exist " + StringUtil.getDetails(e));
            throw new AssertionError("Invalid file provided.");
        }
    }

    public static void decrypt(String filepath, String encryptedFilePath, String password) throws IOException {
        decrypt(new File(encryptedFilePath), new File(filepath), password);
    }

    /**
     * Decrypts the given file using AES key using Key given.
     */
    public static void decrypt(File inputFile, File outputFile, String password) throws IOException {
        try {
            Key pw = generateKey(password);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, pw);
            processFile(cipher, inputFile, outputFile); //decrypt takes in encrypted file as input
        } catch (InvalidKeyException ike) {
            logger.severe("Invalid key length provided " + StringUtil.getDetails(ike));
            throw new AssertionError("Invalid key length.");
        } catch (NoSuchAlgorithmException nsae) {
            logger.severe("Invalid algorithm provided " + StringUtil.getDetails(nsae));
            throw new AssertionError("Invalid algorithm.");
        } catch (NoSuchPaddingException bpe) {
            logger.severe("Invalid padding provided " + StringUtil.getDetails(bpe));
            throw new AssertionError("Invalid padding.");
        }
    }

    /**
     * Encrypts or decrypts the {@code inputFile} and write out into the same {@code inputFile} based on cipher given.
     */
    private static void processFile(Cipher cipher, File inputFile, File outputFile) throws IOException {

        try {
            byte[] inputByteArray = new byte[(int) inputFile.length()];
            FileInputStream fis = new FileInputStream(inputFile); //create file input stream
            fis.read(inputByteArray); //reads from fis to byte[]

            byte[] outputBytes = cipher.doFinal(inputByteArray); //encrypt or decrypt the byte[]
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(outputBytes);

            fis.close();
            fos.close();

        } catch (BadPaddingException bpe) {
            logger.severe("Bad padding provided" + StringUtil.getDetails(bpe));
            throw new AssertionError("Bad padding provided");
        } catch (IllegalBlockSizeException ibse) {
            // user decrypt from plaintext
            logger.info("File is in plain text, no decryption required." + StringUtil.getDetails(ibse));
        }
    }

    /**
     * Generate a secret AES key
     */
    public static Key generateKey(String password) {
        try {
            byte[] inputByte = new byte[16];
            KeySpec spec = new PBEKeySpec(password.toCharArray(), inputByte, iteration, AES_Key_Size);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            SecretKey secretkey = factory.generateSecret(spec);
            return new SecretKeySpec(secretkey.getEncoded(), "AES");

        } catch (NoSuchAlgorithmException nsae) {
            logger.severe("Invalid algorithm provided " + StringUtil.getDetails(nsae));
            throw new AssertionError("Invalid algorithm.");
        } catch (InvalidKeySpecException ikse) {
            logger.severe("Invalid key specifications provided " + StringUtil.getDetails(ikse));
            throw new AssertionError("Invalid key specifications.");
        }
    }

    @Subscribe
    public void handleEncryptionRequestEvent(EncryptionRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Local data changed, saving to file"));
    }

    @Subscribe
    public void handleDecryptionRequestEvent(DecryptionRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Local data changed, saving to file"));
    }

}
```
###### \java\seedu\address\logic\commands\AddTheaterCommand.java
``` java
/**
 * Adds theaters to existing cinema
 */
public class AddTheaterCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "addtheater";
    public static final String COMMAND_ALIAS = "at";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": add theaters to cinema "
            + "by the index number used in the last cinema listing. "
            + "Existing number of theaters will be added with input value.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_NUMOFTHEATERS + "THEATERS\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_NUMOFTHEATERS + "3 ";

    public static final String MESSAGE_RESIZE_CINEMA_SUCCESS = "Resized Cinema: %1$s";
    public static final String MESSAGE_DUPLICATE_CINEMA = "This cinema already exists in the movie planner.";
    public static final String MESSAGE_INVALID_THEATERSIZE = "You can only have up to 20 theaters per cinema!";

    private final Index index;
    private final int newTheaters;

    private Cinema cinemaToResize;
    private Cinema resizedCinema;

    /**
     * @param index of the cinema in the filtered cinema list to resize
     * @param newTheaters to resize the cinema with
     */
    public AddTheaterCommand(Index index, int newTheaters) {
        requireNonNull(index);
        this.index = index;
        this.newTheaters = newTheaters;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        try {
            if (resizedCinema.getTheaters().size() > 20) {
                throw new CommandException(MESSAGE_INVALID_THEATERSIZE);
            }
            model.updateCinema(cinemaToResize, resizedCinema);
        } catch (DuplicateCinemaException dce) {
            throw new CommandException(MESSAGE_DUPLICATE_CINEMA);
        } catch (CinemaNotFoundException cnfe) {
            throw new AssertionError("The target cinema cannot be missing");
        }

        model.updateFilteredCinemaList(PREDICATE_SHOW_ALL_CINEMAS);
        return new CommandResult(String.format(MESSAGE_RESIZE_CINEMA_SUCCESS, resizedCinema));
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        List<Cinema> lastShownList = model.getFilteredCinemaList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CINEMA_DISPLAYED_INDEX);
        }

        cinemaToResize = lastShownList.get(index.getZeroBased());
        resizedCinema = createResizedCinema(cinemaToResize, newTheaters);
    }

    /**
     * Creates and returns a {@code Cinema} with the details of existing cinema and user input
     */
    private Cinema createResizedCinema(Cinema cinemaToResize, int newTheaters) {
        assert cinemaToResize != null;
        int oldTheaterSize = cinemaToResize.getTheaters().size();
        ArrayList<Theater> updatedTheaterList = new ArrayList<>();
        for (Theater theaters : cinemaToResize.getTheaters()) {
            updatedTheaterList.add(theaters);
        }
        int newSize = newTheaters + oldTheaterSize;

        for (int i = oldTheaterSize + 1; i <= newSize; i++) {
            updatedTheaterList.add(new Theater(i));
        }

        return new Cinema(cinemaToResize.getName(), cinemaToResize.getPhone(), cinemaToResize.getEmail(),
                cinemaToResize.getAddress(), updatedTheaterList);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddTheaterCommand)) {
            return false;
        }

        // state check
        AddTheaterCommand e = (AddTheaterCommand) other;
        return index.equals(e.index)
                && Objects.equals(cinemaToResize, e.cinemaToResize);
    }
}
```
###### \java\seedu\address\logic\commands\DecryptCommand.java
``` java
/**
 * Decrypts data found in encryptedmovieplanner file.
 */
public class DecryptCommand extends Command {
    public static final String COMMAND_WORD = "decrypt";
    public static final String COMMAND_ALIAS = "dec";
    public static final String MESSAGE_SUCCESS = "MoviePlanner Decrypted! "
            + "Please restart the application to see the changes.";
    public static final String MESSAGE_WRONGPASSWORD = "Password is wrong!";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Decrypts MoviePlanner file to view contents.\n "
            + "Parameters: " + PREFIX_PASSWORD + " PASSWORD\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_PASSWORD + "dummypass ";

    private String password;

    public DecryptCommand(String password) {
        this.password = password;
    }

    @Override
    public CommandResult execute() {
        //user request to decrypt
        raise(new DecryptionRequestEvent(password));
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
```
###### \java\seedu\address\logic\commands\DeleteTheaterCommand.java
``` java
/**
 * Deletes theater from existing cinema
 */
public class DeleteTheaterCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "deletetheater";
    public static final String COMMAND_ALIAS = "dt";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": delete theaters from cinema "
            + "by the index number used in the last cinema listing. "
            + "Existing number of theaters will be deducted by the input value.\n"
            + "Value provided must be less than current number of theaters.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_NUMOFTHEATERS + "THEATERS\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_NUMOFTHEATERS + "3 ";

    public static final String MESSAGE_RESIZE_CINEMA_SUCCESS = "Resized Cinema: %1$s";
    public static final String MESSAGE_DUPLICATE_CINEMA = "This cinema already exists in the movie planner.";
    public static final String MESSAGE_RESIZE_CINEMA_FAIL = "Cinema cannot have 0 or negative number of theaters!";

    private final Index index;
    private final int newTheaters;

    private Cinema cinemaToResize;
    private Cinema resizedCinema;

    /**
     * @param index       of the cinema in the filtered cinema list to resize
     * @param newTheaters to resize the cinema with
     */
    public DeleteTheaterCommand(Index index, int newTheaters) {
        requireNonNull(index);
        this.index = index;
        this.newTheaters = newTheaters;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        try {
            model.updateCinema(cinemaToResize, resizedCinema);
        } catch (DuplicateCinemaException dce) {
            throw new CommandException(MESSAGE_DUPLICATE_CINEMA);
        } catch (CinemaNotFoundException cnfe) {
            throw new AssertionError("The target cinema cannot be missing");
        }

        model.updateFilteredCinemaList(PREDICATE_SHOW_ALL_CINEMAS);
        return new CommandResult(String.format(MESSAGE_RESIZE_CINEMA_SUCCESS, resizedCinema));
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        List<Cinema> lastShownList = model.getFilteredCinemaList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CINEMA_DISPLAYED_INDEX);
        }

        cinemaToResize = lastShownList.get(index.getZeroBased());
        resizedCinema = createResizedCinema(cinemaToResize, newTheaters);
    }

    /**
     * Creates and returns a {@code Cinema} with the details of existing cinema and user input
     */
    private Cinema createResizedCinema(Cinema cinemaToResize, int newTheaters) throws CommandException {
        assert cinemaToResize != null;
        int oldTheaterSize = cinemaToResize.getTheaters().size();

        if (newTheaters >= oldTheaterSize) {
            throw new CommandException(String.format(MESSAGE_RESIZE_CINEMA_FAIL, resizedCinema));
        }

        ArrayList<Theater> updatedTheaterList = new ArrayList<>();

        for (Theater theaters : cinemaToResize.getTheaters()) {
            updatedTheaterList.add(theaters);
        }

        for (int i = oldTheaterSize; i > oldTheaterSize - newTheaters; i--) {
            updatedTheaterList.remove(updatedTheaterList.size() - 1);
        }

        return new Cinema(cinemaToResize.getName(), cinemaToResize.getPhone(), cinemaToResize.getEmail(),
                cinemaToResize.getAddress(), updatedTheaterList);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteTheaterCommand)) {
            return false;
        }

        // state check
        DeleteTheaterCommand e = (DeleteTheaterCommand) other;
        return index.equals(e.index)
                && Objects.equals(cinemaToResize, e.cinemaToResize);
    }
}
```
###### \java\seedu\address\logic\commands\EncryptCommand.java
``` java
/**
 * Encrypts data stored in movieplanner file.
 */
public class EncryptCommand extends Command {

    public static final String COMMAND_WORD = "encrypt";
    public static final String COMMAND_ALIAS = "enc";
    public static final String MESSAGE_SUCCESS = "MoviePlanner Encrypted!";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Encrypts MoviePlanner file to prevent data leak. "
            + "Ensure that movieplanner.xml exists in data folder.\n"
            + "Parameters: " + PREFIX_PASSWORD + " PASSWORD\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_PASSWORD + "dummypass ";

    private String password;

    public EncryptCommand(String password) {
        this.password = password;
    }

    @Override
    public CommandResult execute() {
        //user request to encrypt
        raise(new EncryptionRequestEvent(password));
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
```
###### \java\seedu\address\logic\parser\AddTheaterCommandParser.java
``` java
/**
 * Parses input arguments and creates a new AddTheaterCommand object
 */
public class AddTheaterCommandParser implements Parser<AddTheaterCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddTheaterCommand
     * and returns an AddTheaterCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddTheaterCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NUMOFTHEATERS);

        if (!arePrefixesPresent(argMultimap, PREFIX_NUMOFTHEATERS)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTheaterCommand.MESSAGE_USAGE));
        }

        Index index;
        ArrayList<Theater> newTheaters;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTheaterCommand.MESSAGE_USAGE));
        }

        try {
            newTheaters = ParserUtil.parseTheaters(argMultimap.getValue(PREFIX_NUMOFTHEATERS)).get();
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }

        return new AddTheaterCommand(index, newTheaters.size());
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
###### \java\seedu\address\logic\parser\DecryptCommandParser.java
``` java
/**
 * Parses input arguments and creates a new DecryptCommand object
 */
public class DecryptCommandParser implements Parser<DecryptCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DecryptCommand
     * and returns an DecryptCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DecryptCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_PASSWORD);
        String password = args.trim();

        if (!arePrefixesPresent(argMultimap, PREFIX_PASSWORD)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DecryptCommand.MESSAGE_USAGE));
        } else {
            try {
                Key pw = SecurityUtil.generateKey(password);

                UserPrefs pref = new UserPrefs();
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, pw);
                File inputFile = new File(pref.getEncryptedMoviePlannerFilePath());
                byte[] inputByteArray = new byte[(int) inputFile.length()];
                FileInputStream fis = new FileInputStream(inputFile); //create file input stream
                fis.read(inputByteArray);
                cipher.doFinal(inputByteArray);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                throw new ParseException(String.format(DecryptCommand.MESSAGE_WRONGPASSWORD,
                        DecryptCommand.MESSAGE_USAGE));
            } catch (IOException e) {
                throw new ParseException(String.format(MESSAGE_ENCRYPTED_FILE_NOT_FOUND, DecryptCommand.MESSAGE_USAGE));
            }
        }

        return new DecryptCommand(password);
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
###### \java\seedu\address\logic\parser\DeleteTheaterCommandParser.java
``` java
/**
 * Parses input arguments and creates a new DeleteTheaterCommand object
 */
public class DeleteTheaterCommandParser implements Parser<DeleteTheaterCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteTheaterCommand
     * and returns a {@code DeleteTheaterCommand} object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteTheaterCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NUMOFTHEATERS);

        Index index;
        ArrayList<Theater> newTheaters;

        if (!arePrefixesPresent(argMultimap, PREFIX_NUMOFTHEATERS)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteTheaterCommand.MESSAGE_USAGE));
        }

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteTheaterCommand.MESSAGE_USAGE));
        }

        try {
            newTheaters = ParserUtil.parseTheaters(argMultimap.getValue(PREFIX_NUMOFTHEATERS)).get();
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }

        return new DeleteTheaterCommand(index, newTheaters.size());
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
###### \java\seedu\address\logic\parser\EncryptCommandParser.java
``` java
/**
 * Parses input arguments and creates a new EncryptCommand object
 */
public class EncryptCommandParser implements Parser<EncryptCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EncryptCommand
     * and returns an EncryptCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EncryptCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_PASSWORD);

        if (!arePrefixesPresent(argMultimap, PREFIX_PASSWORD)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EncryptCommand.MESSAGE_USAGE));
        }
        String password = args.trim();
        UserPrefs pref = new UserPrefs();
        File filePath = new File(pref.getMoviePlannerFilePath());

        if (!filePath.exists()) {
            throw new ParseException(String.format(MESSAGE_FILE_NOT_FOUND, EncryptCommand.MESSAGE_USAGE));
        }

        return new EncryptCommand(password);
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
###### \java\seedu\address\model\cinema\Theater.java
``` java
/**
 * Represents a theater in cinema
 */
public class Theater {

    public static final String MESSAGE_THEATER_CONSTRAINTS = "Theater number given should be a positive integer!";
    public static final String MESSAGE_THEATERSIZE_CONSTRAINTS = " Theater number given should be less than"
            + " or equals to 20.";

    /**
     * Theater number must be positive
     */
    public static final String THEATER_VALIDATION_REGEX = "^[1-9]\\d*$";

    private int theaterNumber;
    private ArrayList<Screening> screeningList;

    public Theater(int theaterNumber) {
        requireNonNull(theaterNumber);
        checkArgument(isValidTheater(String.valueOf(theaterNumber)), MESSAGE_THEATER_CONSTRAINTS);
        this.theaterNumber = theaterNumber;
        this.screeningList = new ArrayList<>();
    }

    /**
     * Returns true if a given string is a valid theater number.
     */
    public static boolean isValidTheater(String test) {
        return test.matches(THEATER_VALIDATION_REGEX);
    }

    public int getTheaterNumber() {
        return theaterNumber;
    }

    public void setTheaterNumber(int theaterNumber) {
        this.theaterNumber = theaterNumber;
    }

    /**
     * Adds a screening to the sorted screening list of the theater
     */
    public void addScreeningToTheater(Screening screening) {
        screeningList.add(screening);
    }

    /**
     * Adds a screening to the sorted screening list of the theater
     */
    public void setScreeningList(ArrayList<Screening> screeningList) {
        this.screeningList = screeningList;
    }

    /**
     * Sorts the screening list by screening date time
     */
    public void sortScreeningList() {
        screeningList.sort(Comparator.comparing(Screening::getScreeningDateTime));
    }

    /**
     * Returns a list of screenings in the theater
     */
    public ArrayList<Screening> getScreeningList() {
        return screeningList;
    }

    /**
     * Delete a screening given in the theater
     */
    public void deleteScreening(Screening screeningToBeDeleted) {
        screeningList.remove(screeningToBeDeleted);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Theater)) {
            return false;
        }

        Theater otherTheater = (Theater) other;
        return otherTheater.getTheaterNumber() == this.getTheaterNumber();
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(theaterNumber, screeningList);
    }
}
```
###### \java\seedu\address\storage\StorageManager.java
``` java
    @Override
    public String getEncryptedMoviePlannerFilePath() {
        return moviePlannerStorage.getEncryptedMoviePlannerFilePath();
    }

    @Override
    public Optional<ReadOnlyMoviePlanner> readMoviePlanner() throws DataConversionException, IOException {
        return readMoviePlanner(moviePlannerStorage.getMoviePlannerFilePath(),
                moviePlannerStorage.getEncryptedMoviePlannerFilePath());
    }
```
###### \java\seedu\address\storage\StorageManager.java
``` java
    @Override
    public Optional<ReadOnlyMoviePlanner> readMoviePlanner(String filePath, String encryptedFilePath)
            throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return moviePlannerStorage.readMoviePlanner(filePath, encryptedFilePath);
    }
```
###### \java\seedu\address\storage\StorageManager.java
``` java
    @Subscribe
    public void handleEncryptionRequestEvent(EncryptionRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Encrypted and saving to file"));
        SecurityUtil.encrypt(moviePlannerStorage.getMoviePlannerFilePath(),
                moviePlannerStorage.getEncryptedMoviePlannerFilePath(), event.getPassword());

    }

    @Subscribe
    public void handleDecryptionRequestEvent(DecryptionRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Decrypted and saving to file"));
        try {
            SecurityUtil.decrypt(moviePlannerStorage.getMoviePlannerFilePath(),
                    moviePlannerStorage.getEncryptedMoviePlannerFilePath(), event.getPassword());
        } catch (IOException e) {
            System.out.println(DecryptCommand.MESSAGE_WRONGPASSWORD);
        }
    }
```
###### \java\seedu\address\storage\XmlMoviePlannerStorage.java
``` java
    public XmlMoviePlannerStorage(String filePath, String encryptedFilePath) {
        this.filePath = filePath;
        this.encryptedFilePath = encryptedFilePath;
    }
```
###### \java\seedu\address\storage\XmlMoviePlannerStorage.java
``` java
    /**
     * Similar to {@link #readMoviePlanner()}
     *
     * @param filePath location of the data. Cannot be null
     * @param encryptedFilePath location of the encrypted data. Returns empty String if null
     * @throws DataConversionException if the file is not in the correct format.
     */
    public Optional<ReadOnlyMoviePlanner> readMoviePlanner(String filePath, String encryptedFilePath)
            throws DataConversionException, IOException {
        requireNonNull(filePath);

        File moviePlannerFile = new File(filePath);
        if (encryptedFilePath == null) {
            encryptedFilePath = "";
        }
        File moviePlannerEncryptedFile = new File(encryptedFilePath);

        if (!moviePlannerFile.exists()) {
            if (!moviePlannerEncryptedFile.exists()) {
                logger.info("MoviePlanner file " + moviePlannerFile + " not found");
                return Optional.empty();
            } else {
                return Optional.empty();
            }
        }

        XmlSerializableMoviePlanner xmlMoviePlanner = XmlFileStorage.loadDataFromSaveFile(new File(filePath));
        try {
            return Optional.of(xmlMoviePlanner.toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + moviePlannerFile + ": " + ive.getMessage());
            throw new DataConversionException(ive);
        }
    }
```

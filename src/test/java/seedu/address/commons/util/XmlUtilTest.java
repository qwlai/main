package seedu.address.commons.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.address.model.AddressBook;
import seedu.address.storage.XmlAdaptedCinema;
import seedu.address.storage.XmlAdaptedTag;
import seedu.address.storage.XmlAdaptedTheater;
import seedu.address.storage.XmlSerializableAddressBook;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.CinemaBuilder;
import seedu.address.testutil.TestUtil;

public class XmlUtilTest {

    private static final String TEST_DATA_FOLDER = FileUtil.getPath("src/test/data/XmlUtilTest/");
    private static final File EMPTY_FILE = new File(TEST_DATA_FOLDER + "empty.xml");
    private static final File MISSING_FILE = new File(TEST_DATA_FOLDER + "missing.xml");
    private static final File VALID_FILE = new File(TEST_DATA_FOLDER + "validAddressBook.xml");
    private static final File MISSING_CINEMA_FIELD_FILE = new File(TEST_DATA_FOLDER + "missingCinemaField.xml");
    private static final File INVALID_CINEMA_FIELD_FILE = new File(TEST_DATA_FOLDER + "invalidCinemaField.xml");
    private static final File VALID_CINEMA_FILE = new File(TEST_DATA_FOLDER + "validCinema.xml");
    private static final File TEMP_FILE = new File(TestUtil.getFilePathInSandboxFolder("tempAddressBook.xml"));

    private static final String INVALID_PHONE = "9482asf424";

    private static final String VALID_NAME = "Hans Muster";
    private static final String VALID_PHONE = "9482424";
    private static final String VALID_EMAIL = "hans@example";
    private static final String VALID_ADDRESS = "4th street";
    private static final List<XmlAdaptedTag> VALID_TAGS = Collections.singletonList(new XmlAdaptedTag("friends"));
    private static final List<XmlAdaptedTheater> VALID_THEATERLIST =
            Collections.singletonList(new XmlAdaptedTheater(1));
    private static final ArrayList<XmlAdaptedTheater> VALID_THEATER = new ArrayList<>(VALID_THEATERLIST);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getDataFromFile_nullFile_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        XmlUtil.getDataFromFile(null, AddressBook.class);
    }

    @Test
    public void getDataFromFile_nullClass_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        XmlUtil.getDataFromFile(VALID_FILE, null);
    }

    @Test
    public void getDataFromFile_missingFile_fileNotFoundException() throws Exception {
        thrown.expect(FileNotFoundException.class);
        XmlUtil.getDataFromFile(MISSING_FILE, AddressBook.class);
    }

    @Test
    public void getDataFromFile_emptyFile_dataFormatMismatchException() throws Exception {
        thrown.expect(JAXBException.class);
        XmlUtil.getDataFromFile(EMPTY_FILE, AddressBook.class);
    }

    @Test
    public void getDataFromFile_validFile_validResult() throws Exception {
        AddressBook dataFromFile = XmlUtil.getDataFromFile(VALID_FILE, XmlSerializableAddressBook.class).toModelType();
        assertEquals(9, dataFromFile.getCinemaList().size());
        assertEquals(0, dataFromFile.getTagList().size());
    }

    @Test
    public void xmlAdaptedCinemaFromFile_fileWithMissingCinemaField_validResult() throws Exception {
        XmlAdaptedCinema actualCinema = XmlUtil.getDataFromFile(
                MISSING_CINEMA_FIELD_FILE, XmlAdaptedCinemaWithRootElement.class);
        XmlAdaptedCinema expectedCinema = new XmlAdaptedCinema(
                null, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_TAGS, VALID_THEATER);
        assertEquals(expectedCinema, actualCinema);
    }

    @Test
    public void xmlAdaptedCinemaFromFile_fileWithInvalidCinemaField_validResult() throws Exception {
        XmlAdaptedCinema actualCinema = XmlUtil.getDataFromFile(
                INVALID_CINEMA_FIELD_FILE, XmlAdaptedCinemaWithRootElement.class);
        XmlAdaptedCinema expectedCinema = new XmlAdaptedCinema(
                VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_TAGS, VALID_THEATER);
        assertEquals(expectedCinema, actualCinema);
    }

    @Test
    public void xmlAdaptedCinemaFromFile_fileWithValidCinema_validResult() throws Exception {
        XmlAdaptedCinema actualCinema = XmlUtil.getDataFromFile(
                VALID_CINEMA_FILE, XmlAdaptedCinemaWithRootElement.class);
        XmlAdaptedCinema expectedCinema = new XmlAdaptedCinema(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_TAGS, VALID_THEATER);
        assertEquals(expectedCinema, actualCinema);
    }

    @Test
    public void saveDataToFile_nullFile_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        XmlUtil.saveDataToFile(null, new AddressBook());
    }

    @Test
    public void saveDataToFile_nullClass_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        XmlUtil.saveDataToFile(VALID_FILE, null);
    }

    @Test
    public void saveDataToFile_missingFile_fileNotFoundException() throws Exception {
        thrown.expect(FileNotFoundException.class);
        XmlUtil.saveDataToFile(MISSING_FILE, new AddressBook());
    }

    @Test
    public void saveDataToFile_validFile_dataSaved() throws Exception {
        TEMP_FILE.createNewFile();
        XmlSerializableAddressBook dataToWrite = new XmlSerializableAddressBook(new AddressBook());
        XmlUtil.saveDataToFile(TEMP_FILE, dataToWrite);
        XmlSerializableAddressBook dataFromFile = XmlUtil.getDataFromFile(TEMP_FILE, XmlSerializableAddressBook.class);
        assertEquals(dataToWrite, dataFromFile);

        AddressBookBuilder builder = new AddressBookBuilder(new AddressBook());
        dataToWrite = new XmlSerializableAddressBook(
                builder.withCinema(new CinemaBuilder().build()).withTag("Friends").build());

        XmlUtil.saveDataToFile(TEMP_FILE, dataToWrite);
        dataFromFile = XmlUtil.getDataFromFile(TEMP_FILE, XmlSerializableAddressBook.class);
        assertEquals(dataToWrite, dataFromFile);
    }

    /**
     * Test class annotated with {@code XmlRootElement} to allow unmarshalling of .xml data to {@code XmlAdaptedCinema}
     * objects.
     */
    @XmlRootElement(name = "cinema")
    private static class XmlAdaptedCinemaWithRootElement extends XmlAdaptedCinema {}
}

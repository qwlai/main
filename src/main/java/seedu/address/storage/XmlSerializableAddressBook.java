package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * An Immutable AddressBook that is serializable to XML format
 */
@XmlRootElement(name = "addressbook")
public class XmlSerializableAddressBook {

    @XmlElement
    private List<XmlAdaptedCinema> cinemas;
    @XmlElement
    private List<XmlAdaptedTag> tags;
    @XmlElement
    private List<XmlAdaptedMovie> movies;
    @XmlElement
    private List<XmlAdaptedTheater> theaters;

    /**
     * Creates an empty XmlSerializableAddressBook.
     * This empty constructor is required for marshalling.
     */
    public XmlSerializableAddressBook() {
        cinemas = new ArrayList<>();
        tags = new ArrayList<>();
        movies = new ArrayList<>();
        theaters = new ArrayList<>();
    }

    /**
     * Conversion
     */
    public XmlSerializableAddressBook(ReadOnlyAddressBook src) {
        this();
        cinemas.addAll(src.getCinemaList().stream().map(XmlAdaptedCinema::new).collect(Collectors.toList()));
        tags.addAll(src.getTagList().stream().map(XmlAdaptedTag::new).collect(Collectors.toList()));
        movies.addAll(src.getMovieList().stream().map(XmlAdaptedMovie::new).collect(Collectors.toList()));
        theaters.addAll(src.getTheaterList().stream().map(XmlAdaptedTheater::new).collect(Collectors.toList()));
    }

    /**
     * Converts this addressbook into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated or duplicates in the
     * {@code XmlAdaptedCinema} or {@code XmlAdaptedTag}.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (XmlAdaptedTag t : tags) {
            addressBook.addTag(t.toModelType());
        }
        for (XmlAdaptedCinema c : cinemas) {
            addressBook.addCinema(c.toModelType());
        }

        for (XmlAdaptedMovie m : movies) {
            addressBook.addMovie(m.toModelType());
        }

        for (XmlAdaptedTheater th : theaters) {
            addressBook.addTheater(th.toModelType());
        }
        return addressBook;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof XmlSerializableAddressBook)) {
            return false;
        }

        XmlSerializableAddressBook otherAb = (XmlSerializableAddressBook) other;
        return cinemas.equals(otherAb.cinemas)
                && tags.equals(otherAb.tags)
                && theaters.equals(otherAb.theaters)
                && movies.equals(otherAb.movies);
    }
}

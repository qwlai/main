package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.cinema.Address;
import seedu.address.model.cinema.Cinema;
import seedu.address.model.cinema.Email;
import seedu.address.model.cinema.Name;
import seedu.address.model.cinema.Phone;
import seedu.address.model.cinema.Theater;
import seedu.address.model.tag.Tag;

/**
 * JAXB-friendly version of the Cinema.
 */
public class XmlAdaptedCinema {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Cinema's %s field is missing!";

    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String phone;
    @XmlElement(required = true)
    private String email;
    @XmlElement(required = true)
    private String address;

    @XmlElement
    private ArrayList<XmlAdaptedTheater> theaters = new ArrayList<>();

    @XmlElement
    private List<XmlAdaptedTag> tagged = new ArrayList<>();

    /**
     * Constructs an XmlAdaptedCinema.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedCinema() {}

    /**
     * Constructs an {@code XmlAdaptedCinema} with the given cinema details.
     */
    public XmlAdaptedCinema(String name, String phone, String email, String address,
                            List<XmlAdaptedTag> tagged, ArrayList<XmlAdaptedTheater> theaters) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        if (tagged != null) {
            this.tagged = new ArrayList<>(tagged);
        }
        if (theaters != null) {
            this.theaters = new ArrayList<>(theaters);
        }
    }

    /**
     * Converts a given Cinema into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedCinema
     */
    public XmlAdaptedCinema(Cinema source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        tagged = new ArrayList<>();
        for (Tag tag : source.getTags()) {
            tagged.add(new XmlAdaptedTag(tag));
        }
        for (Theater theater : source.getTheaters()) {
            theaters.add(new XmlAdaptedTheater(theater));
        }
    }

    /**
     * Converts this jaxb-friendly adapted cinema object into the model's Cinema object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted cinema
     */
    public Cinema toModelType() throws IllegalValueException {
        final List<Tag> cinemaTags = new ArrayList<>();
        for (XmlAdaptedTag tag : tagged) {
            cinemaTags.add(tag.toModelType());
        }
        final List<Theater> cinemaTheater = new ArrayList<>();
        for (XmlAdaptedTheater theater : theaters) {
            cinemaTheater.add(theater.toModelType());
        }

        if (this.name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(this.name)) {
            throw new IllegalValueException(Name.MESSAGE_NAME_CONSTRAINTS);
        }
        final Name name = new Name(this.name);

        if (this.phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(this.phone)) {
            throw new IllegalValueException(Phone.MESSAGE_PHONE_CONSTRAINTS);
        }
        final Phone phone = new Phone(this.phone);

        if (this.email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(this.email)) {
            throw new IllegalValueException(Email.MESSAGE_EMAIL_CONSTRAINTS);
        }
        final Email email = new Email(this.email);

        if (this.address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(this.address)) {
            throw new IllegalValueException(Address.MESSAGE_ADDRESS_CONSTRAINTS);
        }
        final Address address = new Address(this.address);

        if (this.theaters == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Theater.class.getSimpleName()));
        }

        if (!Theater.isValidTheater(this.theaters.size())) {
            throw new IllegalValueException(Theater.MESSAGE_THEATER_CONSTRAINTS);
        }

        final Set<Tag> tags = new HashSet<>(cinemaTags);
        final ArrayList<Theater> theaters = new ArrayList<>(cinemaTheater);
        return new Cinema(name, phone, email, address, tags, theaters);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof XmlAdaptedCinema)) {
            return false;
        }

        XmlAdaptedCinema otherCinema = (XmlAdaptedCinema) other;
        return Objects.equals(name, otherCinema.name)
                && Objects.equals(phone, otherCinema.phone)
                && Objects.equals(email, otherCinema.email)
                && Objects.equals(address, otherCinema.address)
                && theaters.equals(otherCinema.theaters)
                && tagged.equals(otherCinema.tagged);
    }
}

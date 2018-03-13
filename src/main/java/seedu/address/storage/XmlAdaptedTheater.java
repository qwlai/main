package seedu.address.storage;

import javax.xml.bind.annotation.XmlValue;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.cinema.Theater;

/**
 * JAXB-friendly adapted version of the Theater.
 */
public class XmlAdaptedTheater {

    @XmlValue
    private int theaters;

    /**
     * Constructs an XmlAdaptedTheater.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedTheater() {}

    /**
     * Constructs a {@code XmlAdaptedTheater} with the given {@code theaternum}.
     */
    public XmlAdaptedTheater(int theaternum) {
        this.theaters = theaternum;
    }

    /**
     * Converts a given Tag into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created
     */
    public XmlAdaptedTheater(Theater source) {
        theaters = source.getTheaterNumber();
    }

    /**
     * Converts this jaxb-friendly adapted theater object into the model's Theater object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted cinema
     */
    public Theater toModelType() throws IllegalValueException {
        if (!Theater.isValidTheater(theaters)) {
            throw new IllegalValueException(Theater.MESSAGE_THEATER_CONSTRAINTS);
        }
        return new Theater(theaters);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof XmlAdaptedTheater)) {
            return false;
        }

        return theaters == ((XmlAdaptedTheater) other).theaters;
    }
}

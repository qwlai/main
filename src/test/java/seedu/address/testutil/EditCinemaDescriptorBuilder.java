package seedu.address.testutil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.commands.EditCommand.EditCinemaDescriptor;
import seedu.address.model.cinema.Address;
import seedu.address.model.cinema.Cinema;
import seedu.address.model.cinema.Email;
import seedu.address.model.cinema.Name;
import seedu.address.model.cinema.Phone;
import seedu.address.model.cinema.Theater;
import seedu.address.model.tag.Tag;

/**
 * A utility class to help with building EditCinemaDescriptor objects.
 */
public class EditCinemaDescriptorBuilder {

    private EditCinemaDescriptor descriptor;

    public EditCinemaDescriptorBuilder() {
        descriptor = new EditCinemaDescriptor();
    }

    public EditCinemaDescriptorBuilder(EditCinemaDescriptor descriptor) {
        this.descriptor = new EditCinemaDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditCinemaDescriptor} with fields containing {@code cinema}'s details
     */
    public EditCinemaDescriptorBuilder(Cinema cinema) {
        descriptor = new EditCinemaDescriptor();
        descriptor.setName(cinema.getName());
        descriptor.setPhone(cinema.getPhone());
        descriptor.setEmail(cinema.getEmail());
        descriptor.setAddress(cinema.getAddress());
        descriptor.setTags(cinema.getTags());
        descriptor.setTheaters(cinema.getTheaters());
    }

    /**
     * Sets the {@code Name} of the {@code EditCinemaDescriptor} that we are building.
     */
    public EditCinemaDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditCinemaDescriptor} that we are building.
     */
    public EditCinemaDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code EditCinemaDescriptor} that we are building.
     */
    public EditCinemaDescriptorBuilder withEmail(String email) {
        descriptor.setEmail(new Email(email));
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code EditCinemaDescriptor} that we are building.
     */
    public EditCinemaDescriptorBuilder withAddress(String address) {
        descriptor.setAddress(new Address(address));
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code EditCinemaDescriptor}
     * that we are building.
     */
    public EditCinemaDescriptorBuilder withTags(String... tags) {
        Set<Tag> tagSet = Stream.of(tags).map(Tag::new).collect(Collectors.toSet());
        descriptor.setTags(tagSet);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code EditCinemaDescriptor}
     * that we are building.
     */
    public EditCinemaDescriptorBuilder withTheaters(int numOfTheater) {
        List<Theater> theaterList = Stream.of(numOfTheater).map(Theater::new).collect(Collectors.toList());
        ArrayList<Theater> newTheaterList = new ArrayList<>(theaterList);
        descriptor.setTheaters(newTheaterList);
        return this;
    }


    public EditCinemaDescriptor build() {
        return descriptor;
    }
}

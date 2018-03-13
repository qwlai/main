package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMOFTHEATERS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_CINEMAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.cinema.Address;
import seedu.address.model.cinema.Cinema;
import seedu.address.model.cinema.Email;
import seedu.address.model.cinema.Name;
import seedu.address.model.cinema.Phone;
import seedu.address.model.cinema.Theater;
import seedu.address.model.cinema.exceptions.CinemaNotFoundException;
import seedu.address.model.cinema.exceptions.DuplicateCinemaException;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing cinema in the address book.
 */
public class EditCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "edit";
    public static final String COMMAND_ALIAS = "e";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the cinema identified "
            + "by the index number used in the last cinema listing. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]... "
            + "[" + PREFIX_NUMOFTHEATERS + "THEATERS]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com "
            + PREFIX_NUMOFTHEATERS + "3 ";

    public static final String MESSAGE_EDIT_CINEMA_SUCCESS = "Edited Cinema: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_CINEMA = "This cinema already exists in the address book.";

    private final Index index;
    private final EditCinemaDescriptor editCinemaDescriptor;

    private Cinema cinemaToEdit;
    private Cinema editedCinema;

    /**
     * @param index of the cinema in the filtered cinema list to edit
     * @param editCinemaDescriptor details to edit the cinema with
     */
    public EditCommand(Index index, EditCinemaDescriptor editCinemaDescriptor) {
        requireNonNull(index);
        requireNonNull(editCinemaDescriptor);

        this.index = index;
        this.editCinemaDescriptor = new EditCinemaDescriptor(editCinemaDescriptor);
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        try {
            model.updateCinema(cinemaToEdit, editedCinema);
        } catch (DuplicateCinemaException dce) {
            throw new CommandException(MESSAGE_DUPLICATE_CINEMA);
        } catch (CinemaNotFoundException cnfe) {
            throw new AssertionError("The target cinema cannot be missing");
        }
        model.updateFilteredCinemaList(PREDICATE_SHOW_ALL_CINEMAS);
        return new CommandResult(String.format(MESSAGE_EDIT_CINEMA_SUCCESS, editedCinema));
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        List<Cinema> lastShownList = model.getFilteredCinemaList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CINEMA_DISPLAYED_INDEX);
        }

        cinemaToEdit = lastShownList.get(index.getZeroBased());
        editedCinema = createEditedCinema(cinemaToEdit, editCinemaDescriptor);
    }

    /**
     * Creates and returns a {@code Cinema} with the details of {@code cinemaToEdit}
     * edited with {@code editCinemaDescriptor}.
     */
    private static Cinema createEditedCinema(Cinema cinemaToEdit, EditCinemaDescriptor editCinemaDescriptor) {
        assert cinemaToEdit != null;

        Name updatedName = editCinemaDescriptor.getName().orElse(cinemaToEdit.getName());
        Phone updatedPhone = editCinemaDescriptor.getPhone().orElse(cinemaToEdit.getPhone());
        Email updatedEmail = editCinemaDescriptor.getEmail().orElse(cinemaToEdit.getEmail());
        Address updatedAddress = editCinemaDescriptor.getAddress().orElse(cinemaToEdit.getAddress());
        Set<Tag> updatedTags = editCinemaDescriptor.getTags().orElse(cinemaToEdit.getTags());
        List<Theater> updatedTheaters = editCinemaDescriptor.getTheaters().orElse(cinemaToEdit.getTheaters());
        ArrayList<Theater> updatedTheaterList = new ArrayList<>(updatedTheaters);

        return new Cinema(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedTags, updatedTheaterList);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        EditCommand e = (EditCommand) other;
        return index.equals(e.index)
                && editCinemaDescriptor.equals(e.editCinemaDescriptor)
                && Objects.equals(cinemaToEdit, e.cinemaToEdit);
    }

    /**
     * Stores the details to edit the cinema with. Each non-empty field value will replace the
     * corresponding field value of the cinema.
     */
    public static class EditCinemaDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Tag> tags;
        private ArrayList<Theater> theaters;

        public EditCinemaDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditCinemaDescriptor(EditCinemaDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
            setTheaters(toCopy.theaters);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(this.name, this.phone, this.email,
                                                     this.address, this.tags, this.theaters);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        /**
         * Sets {@code theaters} to this object's {@code theaters}.
         * A defensive copy of {@code theaters} is used internally.
         */
        public void setTheaters(ArrayList<Theater> theaters) {
            this.theaters = (theaters != null) ? new ArrayList<>(theaters) : null;
        }

        public Optional<List<Theater>> getTheaters() {
            return (theaters != null) ? Optional.of(Collections.unmodifiableList(theaters)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditCinemaDescriptor)) {
                return false;
            }

            // state check
            EditCinemaDescriptor e = (EditCinemaDescriptor) other;

            return getName().equals(e.getName())
                    && getPhone().equals(e.getPhone())
                    && getEmail().equals(e.getEmail())
                    && getAddress().equals(e.getAddress())
                    && getTags().equals(e.getTags())
                    && getTheaters().equals(e.getTheaters());
        }
    }
}

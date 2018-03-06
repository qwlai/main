package seedu.address.model.theater;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A list of theaters in the cinema.
 * Supports minimal set of list operations for the app's features.
 **/
public class UniqueTheaterList {

    private final ObservableList<Theater> internalList = FXCollections.observableArrayList();

    /**
     * Constructs empty TheaterList.
     */
    public UniqueTheaterList() {}

    /**
     * Creates UniqueTheaterList depending on the number of theater.
     */
    public UniqueTheaterList(int numberOfTheaters) {
        for (int i = 1; i <= numberOfTheaters; i++) {
            internalList.add(new Theater(i);
        }
    }
}

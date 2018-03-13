package seedu.address.ui;

import java.util.logging.Logger;

import org.fxmisc.easybind.EasyBind;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.movie.Movie;

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

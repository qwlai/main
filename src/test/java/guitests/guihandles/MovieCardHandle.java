package guitests.guihandles;

import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * Provides a handle to a cinema card in the cinema list panel.
 */
public class MovieCardHandle extends NodeHandle<Node> {
    private static final String ID_FIELD_ID = "#id";
    private static final String NAME_FIELD_ID = "#name";
    private static final String DURATION_FIELD_ID = "#duration";
    private static final String RATING_FIELD_ID = "#rating";
    private static final String STARTDATE_FIELD_ID = "#startDate";

    private final Label idLabel;
    private final Label nameLabel;
    private final Label durationLabel;
    private final Label ratingLabel;
    private final Label startDateLabel;

    public MovieCardHandle(Node cardNode) {
        super(cardNode);

        this.idLabel = getChildNode(ID_FIELD_ID);
        this.nameLabel = getChildNode(NAME_FIELD_ID);
        this.durationLabel = getChildNode(DURATION_FIELD_ID);
        this.ratingLabel = getChildNode(RATING_FIELD_ID);
        this.startDateLabel = getChildNode(STARTDATE_FIELD_ID);
    }

    public String getId() {
        return idLabel.getText();
    }

    public String getName() {
        return nameLabel.getText();
    }

    public String getDuration() {
        return durationLabel.getText();
    }

    public String getRating() {
        return ratingLabel.getText();
    }

    public String getStartDate() {
        return startDateLabel.getText();
    }
}

package csseditors;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.paint.Stop;

/**
 *
 * @author Siddhesh Rane
 */
public class StopList extends ListView<Stop> {

    public StopList() {
        setEditable(true);
        setCellFactory((lv) -> {
            return new StopCell();
        });
        getStylesheets().add("/csseditors/gradients.css");
        setOnEditCommit((ListView.EditEvent<Stop> b) -> {
            final ObservableList<Stop> items = b.getSource().getItems();
            if (b.getNewValue() == null) {
                items.remove(b.getIndex());
            } else {
                items.set(b.getIndex(), b.getNewValue());
            }
        });
    }

}

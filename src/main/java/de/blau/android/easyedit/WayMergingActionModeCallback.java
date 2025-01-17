package de.blau.android.easyedit;

import java.util.List;
import java.util.Set;

import android.content.res.Resources.NotFoundException;
import android.util.Log;
import android.view.Menu;
import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import de.blau.android.R;
import de.blau.android.dialogs.TagConflictDialog;
import de.blau.android.exception.OsmIllegalOperationException;
import de.blau.android.osm.OsmElement;
import de.blau.android.osm.Result;
import de.blau.android.osm.Way;
import de.blau.android.util.ScreenMessage;

public class WayMergingActionModeCallback extends NonSimpleActionModeCallback {
    private static final String   DEBUG_TAG = "WayMerging...";
    private final Way             way;
    private final Set<OsmElement> ways;

    /**
     * Construct a new WayMergingActionModeCallback from an existing Way and potentially mergable Ways
     * 
     * @param manager the current EasyEditManager instance
     * @param way the existing Way
     * @param mergeableWays a Set of ways that could be merged
     */
    public WayMergingActionModeCallback(@NonNull EasyEditManager manager, @NonNull Way way, @NonNull Set<OsmElement> mergeableWays) {
        super(manager);
        this.way = way;
        ways = mergeableWays;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        helpTopic = R.string.help_mergingways;
        mode.setSubtitle(R.string.menu_merge);
        logic.setClickableElements(ways);
        logic.setReturnRelations(false);
        super.onCreateActionMode(mode, menu);
        return true;
    }

    @Override
    public boolean handleElementClick(OsmElement element) { // due to clickableElements, only valid ways can be
                                                            // clicked
        super.handleElementClick(element);
        // race conditions with touch events seem to make the impossible possible
        // TODO fix properly
        if (!(element instanceof Way)) {
            return false;
        }
        if (!findMergeableWays(way).contains(element)) {
            return false;
        }
        try {
            List<Result> result = logic.performMerge(main, way, (Way) element);
            Result r = result.get(0);
            main.startSupportActionMode(new WaySelectionActionModeCallback(manager, (Way) r.getElement()));
            if (result.size() > 1 || r.hasIssue()) {
                TagConflictDialog.showDialog(main, result);
            }
        } catch (OsmIllegalOperationException e) {
            ScreenMessage.barError(main, e.getLocalizedMessage());
        } catch (NotFoundException e) {
            Log.d(DEBUG_TAG, "handleElementClick got exception " + e.getMessage());
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        logic.setClickableElements(null);
        logic.setReturnRelations(true);
        super.onDestroyActionMode(mode);
    }
}

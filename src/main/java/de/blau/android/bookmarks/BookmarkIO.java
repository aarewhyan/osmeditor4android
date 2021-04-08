package de.blau.android.bookmarks;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.blau.android.osm.ViewBox;
import de.blau.android.util.SavingHelper;

/**
 * Handles Bookmark reading/writing operations
 *
 */
public class  BookmarkIO {
    private BookmarksStorage currentBookmarkStorage;
    private List<BookmarksStorage> bookmarksStorage;
    private final String fileName = "bookmarks.ser";
    SavingHelper<ArrayList<BookmarksStorage>> savingHelper;

    /**
     * BookmarkIO constructor
     */
    public BookmarkIO(){
        this.currentBookmarkStorage = new BookmarksStorage();
        this.bookmarksStorage = new ArrayList<>();
        this.savingHelper = new SavingHelper<>();
    }
    /**
     * Adds the text and viewbox to a list
     *
     * @param comment Bookmark name/comment
     * @param viewbox Map Viewbox
     */
    public void addDatatolist(@NonNull String comment,@NonNull ViewBox viewbox) {
        currentBookmarkStorage.set(comment, viewbox);
        bookmarksStorage.add(currentBookmarkStorage);
    }

    /**
     * (Over)Writes the bookmark file to storage.
     *
     * @param context the Android Context
     * @param bookmarksStorage Arraylist containing BookmarksStorage objects
     */
    public void writeList(@NonNull Context context,@NonNull ArrayList<BookmarksStorage> bookmarksStorage) {
        savingHelper.save(context, fileName, (ArrayList<BookmarksStorage>) bookmarksStorage, true);
    }

    /**
     * Reads the bookmark file from storage
     *
     * @param context the Android context
     * @return Arraylist containg saved bookmarks
     */

    @NonNull
    public ArrayList<BookmarksStorage> readList(@NonNull Context context) {

        ArrayList<BookmarksStorage> savedList = savingHelper.load(context,fileName,true);
        if(savedList==null){
            return (ArrayList<BookmarksStorage>) this.bookmarksStorage;
        }
        return savedList;
    }

    /**
     * Utility for a saving bookmark
     *
     * @param context the Android context
     * @param comments Bookmark name/comment
     * @param viewBox map viewbox
     */
    public void writer(@NonNull Context context,@NonNull String comments,@NonNull ViewBox viewBox){
        this.bookmarksStorage = readList(context);
        addDatatolist(comments,viewBox);
        writeList(context, (ArrayList<BookmarksStorage>) this.bookmarksStorage);
    }

}





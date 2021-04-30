package dartmouth.cs.qiyaozuo;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

class CommentListLoader extends AsyncTaskLoader<List<ExerciseEntryModel>> {

    private final CommentsDataSource dataSource;

    //initialize database
    public CommentListLoader(@NonNull Context context) {
        super(context);
        dataSource = new CommentsDataSource(context);
        dataSource.open();
    }

    @Nullable
    @Override
    //get all database entries
    public List<ExerciseEntryModel> loadInBackground() {
        return dataSource.getAllComments();
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}

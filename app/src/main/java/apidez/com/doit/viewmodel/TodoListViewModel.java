package apidez.com.doit.viewmodel;

import android.databinding.ObservableList;

import apidez.com.doit.decorator.TodoDecorator;
import rx.Observable;

/**
 * Created by nongdenchet on 2/8/16.
 */
public interface TodoListViewModel {
    /**
     * Get all to-do decorator
     */
    ObservableList<TodoDecorator> getTodoItems();

    /**
     * Fetch data from database
     */
    Observable fetchAllTodo();

    /**
     * Check complete
     */
    Observable<Boolean> checkChangeItem(int position);
}

package apidez.com.doit.repository;

import java.util.List;

import apidez.com.doit.model.Todo;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by nongdenchet on 2/8/16.
 */
public class TodoRepositoryImpl implements TodoRepository {

    @Override
    public Observable<Long> createOrUpdate(Todo todo) {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                try {
                    subscriber.onNext(todo.save());
                    subscriber.onCompleted();
                } catch (Exception ex) {
                    subscriber.onError(ex);
                }
            }
        });
    }

    @Override
    public Observable<Boolean> delete(Long id) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    Todo todo = Todo.findById(Todo.class, id);
                    subscriber.onNext(todo.delete());
                    subscriber.onCompleted();
                } catch (Exception ex) {
                    subscriber.onError(ex);
                }
            }
        });
    }

    @Override
    public Observable<List<Todo>> getAll() {
        return Observable.just(Todo.listAll(Todo.class));
    }
}

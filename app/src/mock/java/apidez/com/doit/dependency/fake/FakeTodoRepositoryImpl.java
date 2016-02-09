package apidez.com.doit.dependency.fake;

import java.util.List;
import java.util.Random;

import apidez.com.doit.model.Todo;
import apidez.com.doit.repository.TodoRepository;
import apidez.com.doit.utils.DataUtils;

/**
 * Created by nongdenchet on 2/8/16.
 */
public class FakeTodoRepositoryImpl implements TodoRepository {

    @Override
    public Todo create(Todo todo) {
        return todo;
    }

    @Override
    public List<Todo> getAll() {
        return DataUtils.provideLongMockTodoList();
    }

    @Override
    public boolean delete(String id) {
        return true;
    }

    @Override
    public boolean update(Todo todo) throws Exception {
        Random random = new Random();
        boolean result = random.nextBoolean();
        if (!result) {
            throw new Exception("Some error occurs");
        }
        return true;
    }
}

package apidez.com.doit.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import apidez.com.doit.DoItApp;
import apidez.com.doit.R;
import apidez.com.doit.databinding.FragmentTodoListBinding;
import apidez.com.doit.dependency.module.TodoListModule;
import apidez.com.doit.model.Todo;
import apidez.com.doit.view.adapter.CustomLinearLayoutManager;
import apidez.com.doit.view.adapter.DisableLinearLayoutManager;
import apidez.com.doit.view.adapter.TodoListAdapter;
import apidez.com.doit.viewmodel.TodoListViewModel;
import butterknife.InjectView;

/**
 * Created by nongdenchet on 2/8/16.
 */
public class TodoListFragment extends BaseFragment implements TodoDialogFragment.CallbackSuccess {
    private TodoListAdapter mTodoListAdapter;
    private FragmentTodoListBinding mBinding;
    private TodoDialogFragment mTodoDialogFragment;

    @InjectView(R.id.todoList)
    RecyclerView mTodoList;

    @Inject
    TodoListViewModel mViewModel;

    public static TodoListFragment newInstance() {
        Bundle args = new Bundle();
        TodoListFragment fragment = new TodoListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        DoItApp.app().component()
                .plus(new TodoListModule())
                .inject(this);
    }

    @Override
    protected int layout() {
        return R.layout.fragment_todo_list;
    }

    @Override
    protected void onSetUpView(View rootView) {
        bindViewModel(rootView);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        mTodoList.setLayoutManager(new DisableLinearLayoutManager(getContext(), false));
        mTodoListAdapter = new TodoListAdapter(getContext());
        mTodoList.setAdapter(mTodoListAdapter);
        startObserve(mTodoListAdapter.animationEnd()).subscribe(done -> {
            if (done) mTodoList.setLayoutManager(new CustomLinearLayoutManager(getContext()));
        });
    }

    private void bindViewModel(View rootView) {
        mBinding = DataBindingUtil.bind(rootView);
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_todo_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                showTodoDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showTodoDialog() {
        mTodoDialogFragment = TodoDialogFragment.newInstance();
        setCallbackAndShowDialog();
    }

    private void showTodoDialog(Todo todo) {
        mTodoDialogFragment = TodoDialogFragment.newInstance(todo);
        setCallbackAndShowDialog();
    }

    private void setCallbackAndShowDialog() {
        if (mTodoDialogFragment != null) {
            mTodoDialogFragment.setCallbackSuccess(this);
            mTodoDialogFragment.show(getFragmentManager(), TodoDialogFragment.TAG);
        }
    }

    @Override
    public void onSuccess() {
        fetchAllTodo();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionBar();
        fetchAllTodo();
    }

    private void setActionBar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.toolbar);
    }

    private void fetchAllTodo() {
        startObserve(mViewModel.fetchAllTodo()).subscribe(response -> {});
    }

    // Events
    public void onEvent(TodoListAdapter.CheckItemEvent event) {
        startObserve(mViewModel.checkChangeItem(event.viewModel)).subscribe(id -> {
            event.callBack.onCheckChange(event.viewModel.isCompleted());
        }, throwable -> {
            showShortToast(throwable.getMessage());
        });
    }

    public void onEvent(TodoListAdapter.ShowActionItemEvent event) {
        mViewModel.switchEnable();
        mTodoList.getLayoutManager().smoothScrollToPosition(mTodoList, null, event.position);
    }

    public void onEvent(TodoListAdapter.UpdateActionItemEvent event) {
        showTodoDialog(event.todo);
    }

    public void onEvent(TodoListAdapter.DeleteActionItemEvent event) {
        startObserve(mViewModel.deleteItem(event.position)).subscribe(success -> {
            if (success) mTodoListAdapter.resetState();
        }, throwable -> {
            showShortToast(throwable.getMessage());
        });
    }
}

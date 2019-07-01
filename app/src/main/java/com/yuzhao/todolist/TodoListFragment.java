package com.yuzhao.todolist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TodoListFragment extends Fragment
{
	List<TaskItem> m_list = new ArrayList<TaskItem>();
	private DBHelper m_dbhelper = null;
	ArrayAdapter<TaskItem> m_adapter = null;
	static final int REQUEST_CODE = 1;
	public TodoListFragment()
	{
	}
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	/*@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		// Add menu items to the app bar if it is present.
		inflater.inflate(R.menu.menu_todo_list, menu);
	}*/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_todo_list, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		try
		{
			m_dbhelper = new DBHelper(getActivity());
			m_list = m_dbhelper.selectAll();
		}
		catch (Exception e)
		{
			Toast.makeText(getActivity(), "Error occured when creating database.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		ListView list = (ListView)getActivity().findViewById(R.id.LIST_TASKS);
		m_adapter = new TaskArrayAdapter(getActivity(), R.layout.list_layout, m_list);
		list.setAdapter(m_adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					onEdit(view, position);
				}
			}
		);
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
			{
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
				{
					onDelete(view, position);
					return true;
				}
			}
		);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.MENU_ADD:
				onEdit(null, -1);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// Update task list if data changed.
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
		{
			m_list = m_dbhelper.selectAll();
			m_adapter.clear();
			m_adapter.addAll(m_list);
			m_adapter.notifyDataSetChanged();
		}
		// Remove the soft keyboard after hitting the save button.
		//InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		//inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
	}
	public void onEdit(View view, int position)
	{
		TaskDetailFragment.m_dbhelper = m_dbhelper;
		Intent intent = new Intent(getActivity(), TaskDetail.class);
		if (position >= 0)
			intent.putExtra(TaskDetailFragment.INTENT_INIT, m_adapter.getItem(position).id);
		startActivityForResult(intent, REQUEST_CODE);
	}
	private void onDelete(View view, int position)
	{
		TaskItem task = m_adapter.getItem(position);
		if (task != null)
		{
			// database delete record.
			m_dbhelper.delete(task.id);
			// Removes the object from the array.
			m_adapter.remove(task);
			m_adapter.notifyDataSetChanged();
		}
		// Remove the soft keyboard after hitting the save button.
		InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
	}
}

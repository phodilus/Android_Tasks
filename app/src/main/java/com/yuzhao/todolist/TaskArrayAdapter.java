package com.yuzhao.todolist;

import android.content.Context;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskArrayAdapter extends ArrayAdapter
{
	private Context m_context;
	private int m_resid;
	private List<TaskItem> m_data;
	private LayoutInflater m_inflater;

	public TaskArrayAdapter(Context context, int id, List<TaskItem> data)
	{
		super(context, id, data);
		this.m_context = context;
		this.m_resid = id;
		this.m_data = data;
		m_inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	private class ItemHolder
	{
		ImageView logo;
		TextView title;
		TextView duedate;
		TextView status;
		TextView description;
	}
	@Override
	public View getView(int position, View view, ViewGroup parent)
	{
		ItemHolder holder = null;
		if (view == null)
		{
			view = m_inflater.inflate(m_resid, parent, false);
			holder = new ItemHolder();
			holder.logo = (ImageView)view.findViewById(R.id.IMG_LOGO);
			holder.title = (TextView)view.findViewById(R.id.TXT_TITLE);
			holder.duedate = (TextView)view.findViewById(R.id.TXT_DUEDATE);
			holder.status = (TextView)view.findViewById(R.id.TXT_STATUS);
			holder.description = (TextView)view.findViewById(R.id.TXT_DESCRIPTION);
			// Tags can be used to store data within a view.
			view.setTag(holder);
		}
		else
		{
			holder = (ItemHolder)view.getTag();
		}
		// Display the information for that item.
		TaskItem item = m_data.get(position);
		if (item.status == 0)//Finished
			holder.logo.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
		else if (item.status == 1)//Delay
			holder.logo.setImageResource(R.drawable.ic_assignment_late_black_24dp);
		else if (item.status == 2)//On Time
			holder.logo.setImageResource(R.drawable.ic_assignment_black_24dp);
		String[] status = m_context.getResources().getStringArray(R.array.task_status);
		holder.status.setText(status[item.status]);
		holder.title.setText(item.title);
		holder.duedate.setText(item.duedate);
		holder.description.setText(item.description);
		return view;
	}
}

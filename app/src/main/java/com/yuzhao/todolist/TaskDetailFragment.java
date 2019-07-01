package com.yuzhao.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class TaskDetailFragment extends Fragment
{
	public static String INTENT_INIT = "ID";
	public static DBHelper m_dbhelper = null;
	private long m_taskid = 0xFFFFFFFF;
	public TaskDetailFragment()
	{
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_task_detail, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// Initialize controls.
		Bundle bundle = getActivity().getIntent().getExtras();
		if (bundle != null)
		{
			m_taskid = bundle.getLong(INTENT_INIT);
			TaskItem task = m_dbhelper.select(m_taskid);
			((EditText)getActivity().findViewById(R.id.EDIT_TITLE)).setText(task.title);
			((Button)getActivity().findViewById(R.id.BTN_DATE)).setText(task.duedate);
			((Spinner)getActivity().findViewById(R.id.SPIN_STATUS)).setSelection(task.status);
			((EditText)getActivity().findViewById(R.id.EDIT_DESCRIPTION)).setText(task.description);
			((EditText)getActivity().findViewById(R.id.EDIT_DETAIL)).setText(task.detail);
		}
		else
		{
			// Set default value for new task.
			((Spinner)getActivity().findViewById(R.id.SPIN_STATUS)).setSelection(2);//On Time
			Calendar cale = Calendar.getInstance();
			((Button)getActivity().findViewById(R.id.BTN_DATE)).setText(cale.get(Calendar.YEAR) + "-" + (cale.get(Calendar.MONTH) + 1) + "-" + cale.get(Calendar.DAY_OF_MONTH));
		}
		// Set events.
		Button dateButton = (Button)getActivity().findViewById(R.id.BTN_DATE);
		dateButton.setOnClickListener(new View.OnClickListener()
									  {
										  public void onClick(View view) { onShowDate(); }
									  }
		);
		Button saveButton = (Button)getActivity().findViewById(R.id.BTN_SAVE);
		saveButton.setOnClickListener(new View.OnClickListener()
									  {
										  public void onClick(View view) { onSave(); }
									  }
		);
		Button cancelButton = (Button)getActivity().findViewById(R.id.BTN_CANCEL);
		cancelButton.setOnClickListener(new View.OnClickListener()
									  {
										  public void onClick(View view) { onCancel(); }
									  }
		);
		Button delButton = (Button)getActivity().findViewById(R.id.BTN_DELETE);
		delButton.setOnClickListener(new View.OnClickListener()
									  {
										  public void onClick(View view) { onDelete(); }
									  }
		);
	}
	public void onShowDate()
	{
		String date = ((Button)getActivity().findViewById(R.id.BTN_DATE)).getText().toString();
		new DatePickerDialog(
				getContext(),
				new DatePickerDialog.OnDateSetListener()
				{
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
					{
						((Button)getActivity().findViewById(R.id.BTN_DATE)).setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
					}
				},
				Integer.parseInt(date.substring(0, date.indexOf("-"))),
				Integer.parseInt(date.substring( date.indexOf("-") + 1, date.indexOf("-", date.indexOf("-") + 1) )) - 1,
				Integer.parseInt(date.substring( date.indexOf("-", date.indexOf("-") + 1) + 1 ))
		).show();

		// Remove the soft keyboard after hitting the save button.
		InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
	}
	private void onSave()
	{
		// Check whether title & date is empty to avoid empty database field.
		EditText title = (EditText)getActivity().findViewById(R.id.EDIT_TITLE);
		if (title.getText().toString().isEmpty() == true)
		{
			Toast.makeText(getActivity(), "Please input the title.", Toast.LENGTH_SHORT).show();
			return;
		}
		Button date = (Button)getActivity().findViewById(R.id.BTN_DATE);
		if (date.getText().toString().isEmpty() == true)
		{
			Toast.makeText(getActivity(), "Please choose the due date.", Toast.LENGTH_SHORT).show();
			return;
		}
		EditText detail = (EditText)getActivity().findViewById(R.id.EDIT_DETAIL);
		if (TextUtils.isEmpty(detail.getText().toString()))
		{
			showAlert("Notice", "No additional information was saved.");
			//(continue)
		}
		EditText description = (EditText)getActivity().findViewById(R.id.EDIT_DESCRIPTION);
		Spinner status = (Spinner)getActivity().findViewById(R.id.SPIN_STATUS);

		// Save task data into database.
		TaskItem task = new TaskItem();
		task.id = m_taskid;
		task.title = title.getText().toString();
		task.duedate = date.getText().toString();
		task.description = description.getText().toString();
		task.status = status.getSelectedItemPosition();
		task.detail = detail.getText().toString();
		if (m_taskid != 0xFFFFFFFF)
			m_dbhelper.update(task);
		else
			task.id = m_dbhelper.insert(task);

		getActivity().setResult(Activity.RESULT_OK);
		getActivity().finish();

		// Add the object at the end of the array.
		//m_adapter.add(task);
		// Notifies the adapter that the underlying data has changed, any View reflecting the data should refresh itself.
		//m_adapter.notifyDataSetChanged();
		// Remove the soft keyboard after hitting the save button.
		//InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		//inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
	}
	public void onCancel()
	{
		// Return to the main activity.
		getActivity().setResult(Activity.RESULT_CANCELED);
		getActivity().finish();
	}
	public void onDelete()
	{
		// Delete database record.
		if (m_taskid != 0xFFFFFFFF)
		{
			m_dbhelper.delete(m_taskid);
			getActivity().setResult(Activity.RESULT_OK);
		}
		else
		{
			getActivity().setResult(Activity.RESULT_CANCELED);
		}
		// Return to the main activity.
		getActivity().finish();
	}
	public void showAlert(String title, String detail)
	{
		// AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		ContextThemeWrapper ctw = new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(title);
		builder.setIcon(android.R.drawable.ic_dialog_alert);

		// Det dialog message
		builder	.setMessage(detail)
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								dialog.cancel();
							}
						}
				);
		// create alert dialog
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}

package com.pyt.postyourfun.dynamoDBManager.tableTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.pyt.postyourfun.constants.Constants;
import com.pyt.postyourfun.dynamoDBManager.DynamoDBManagerTaskResult;

public class UserDBManagerTask extends AsyncTask<String, Void, DynamoDBManagerTaskResult> {
	Context _context;
	String device_id;

	public UserDBManagerTask(Context context) {
		this._context = context;
	}

	@Override
	protected DynamoDBManagerTaskResult doInBackground(String... values) {

		String tableStatus = null;
		tableStatus = UserDBManager.sharedInstance(_context).getTableStatus(Constants.USER_TABLE_NAME);

		DynamoDBManagerTaskResult result = new DynamoDBManagerTaskResult();
		result.setTableStatus(tableStatus);
		result.setTaskType(values[0]);

		if (values[0] == Constants.DDB_CREATE_TABLE) {
			if (tableStatus.length() == 0) {
				UserDBManager.createTable(Constants.USER_TABLE_NAME, "userId");
			}
		} else if (values[0] == Constants.DDB_INSERT_USER) {
			if (tableStatus.equalsIgnoreCase("ACTIVE")) {
//                    insertUsers();
				UserDBManager.insertUsers(values[1], values[2], values[3], values[4], values[5], values[6], values[7]);
				device_id = values[1];
			}
		} else if (values[0] == Constants.DDB_GET_USER) {
			if (tableStatus.equalsIgnoreCase("ACTIVE")) {
//                result.setResults(UserDBManager.getUserList());
			}
		} else if (values[0] == Constants.DDB_CLEAN_UP) {
			if (tableStatus.equalsIgnoreCase("ACTIVE")) {
//                    cleanUp();
			}
		}
		return result;
	}

	@Override
	protected void onPostExecute(DynamoDBManagerTaskResult result) {

		if (result.getTaskType() == Constants.DDB_CREATE_TABLE) {

			if (result.getTableStatus().length() != 0) {
//                Toast.makeText(
//                        _context,
//                        "The test table already exists.\nTable Status: "
//                                + result.getTableStatus(),
//                        Toast.LENGTH_LONG).show();
			}
		} else if (result.getTaskType() == Constants.DDB_LIST_USERS && result.getTableStatus().equalsIgnoreCase("ACTIVE")) {
//                startActivity(new Intent(_context,
//                        UserListActivity.class));

		} else if (!result.getTableStatus().equalsIgnoreCase("ACTIVE")) {

//            Toast.makeText(
//                    _context,
//                    "The test table is not ready yet.\nTable Status: "
//                            + result.getTableStatus(), Toast.LENGTH_LONG)
//                    .show();
		} else if (result.getTableStatus().equalsIgnoreCase("ACTIVE") && result.getTaskType() == Constants.DDB_INSERT_USER) {
//            Toast.makeText(_context,
//                    "Users inserted successfully!", Toast.LENGTH_SHORT).show();
		}
	}
}

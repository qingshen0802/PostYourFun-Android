package com.pyt.postyourfun.dynamoDBManager.tableTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.pyt.postyourfun.constants.Constants;
import com.pyt.postyourfun.dynamoDBManager.DynamoDBManagerTaskResult;

/**
 * Created by Administrator on 7/7/2015.
 */
public class UserFacebookDBManagerTask extends AsyncTask<String, Void, DynamoDBManagerTaskResult> {

	Context _context;

	public UserFacebookDBManagerTask(Context context) {
		this._context = context;
	}

	@Override
	protected DynamoDBManagerTaskResult doInBackground(String... params) {

		String tableStatus = null;
		tableStatus = UserFacebookDBManager.sharedInstance(_context).getTableStatus(Constants.USER_FACEBOOK_TABLE_NAME);

		DynamoDBManagerTaskResult result = new DynamoDBManagerTaskResult();
		result.setTableStatus(tableStatus);
		result.setTaskType(params[0]);

		if (params[0] == Constants.DDB_CREATE_TABLE) {
			if (tableStatus.length() == 0) {
//                UserFacebookDBManager.createTable(Constants.USER_FACEBOOK_TABLE_NAME, "userId");
			}
		} else if (params[0] == Constants.DDB_INSERT_USER) {
			if (tableStatus.equalsIgnoreCase("ACTIVE")) {
//                    insertUsers();
				UserFacebookDBManager.insertUsers(params[1], params[2], params[3], params[4]);
			}
		} else if (params[0] == Constants.DDB_LIST_USERS) {
			if (tableStatus.equalsIgnoreCase("ACTIVE")) {
//                    getUserList();
			}
		} else if (params[0] == Constants.DDB_CLEAN_UP) {
			if (tableStatus.equalsIgnoreCase("ACTIVE")) {
//                    cleanUp();
			}
		}
		return result;
	}

	@Override
	protected void onPostExecute(DynamoDBManagerTaskResult result) {
		if (result.getTaskType() == Constants.DDB_CREATE_TABLE) {

//            if (result.getTableStatus().length() != 0) {
//                Toast.makeText(
//                        _context,
//                        "The test table already exists.\nTable Status: "
//                                + result.getTableStatus(),
//                        Toast.LENGTH_LONG).show();
//            }

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

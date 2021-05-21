package com.pyt.postyourfun.dynamoDBManager;

import java.util.ArrayList;

public class DynamoDBManagerTaskResult {
	private String taskType;
	private String tableStatus;

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getTableStatus() {
		return tableStatus;
	}

	public void setTableStatus(String tableStatus) {
		this.tableStatus = tableStatus;
	}
}

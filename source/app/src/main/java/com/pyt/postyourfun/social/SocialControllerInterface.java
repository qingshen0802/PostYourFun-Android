package com.pyt.postyourfun.social;

public interface SocialControllerInterface {
	public static int FACEBOOK = 0;
	public static int GOOGLE = 1;
	public static int TWITTER = 2;

	public enum ACTION {
		login(0),
		logout(1);

		private final int value;

		private ACTION(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	;

	public void onSuccess(int type, int action);

	public void onFailure(int type, int action);
}

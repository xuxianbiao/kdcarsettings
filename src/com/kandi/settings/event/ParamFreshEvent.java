package com.kandi.settings.event;

public class ParamFreshEvent {
	public static final int PARAMFRESH_STATE = 1;
	public static final int PARAMFRESH_NOSTATE = 0;
	public int type; // 0idle 1left 2longleft 3right 4longright
}

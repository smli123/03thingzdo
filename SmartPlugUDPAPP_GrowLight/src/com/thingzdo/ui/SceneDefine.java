package com.thingzdo.ui;

import java.io.Serializable;

public class SceneDefine implements Serializable {
	public int mId;
	public String mSceneName;
	public int mSceneId;

	public int mPowerEnable;
	public String mPowerModuleID;
	public int mPowerStatus;

	public int mCurtainEnable;
	public String mCurtainModuleID;
	public int mCurtainStatus;

	public int mAirConEnable;
	public String mAirConModuleID;
	public int mAirConStatus;
	public int mAirConTemperature;

	public int mPCEnable;
	public String mPCModuleID;
	public String mPCMacModuleID;
	public String mPCMacAddress;
	public int mPCStatus;
}

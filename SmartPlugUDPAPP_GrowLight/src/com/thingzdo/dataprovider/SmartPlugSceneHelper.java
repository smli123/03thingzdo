package com.thingzdo.dataprovider;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.thingzdo.ui.SceneDefine;

public class SmartPlugSceneHelper {
	private Context mContext;
	private ContentResolver mContentResolver;
	private String TAG = "SceneHelper";

	public SmartPlugSceneHelper(Context context) {
		mContext = context;
		if (mContext == null) {
			Log.e(TAG, "Context is empty");
		} else {
			mContentResolver = mContext.getContentResolver();
		}
	}

	public ArrayList<SceneDefine> getAllScene() {
		ArrayList<SceneDefine> timers = new ArrayList<SceneDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String order = SmartPlugContentDefine.SmartPlugScene._ID + " asc";
		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugScene.ALL_CONTENT_URI, null,
				null, null, order);

		if (null != cur) {
			while (cur.moveToNext()) {
				SceneDefine timer = new SceneDefine();
				timer.mId = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.ID_COLUMN);
				timer.mSceneId = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_ID_COLUMN);
				timer.mSceneName = cur
						.getString(SmartPlugContentDefine.SmartPlugScene.SCENE_NAME_COLUMN);
				timer.mPowerEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_POWER_ENABLE_COLUMN);
				timer.mPowerModuleID = cur
						.getString(SmartPlugContentDefine.SmartPlugScene.SCENE_POWER_MODULEID_COLUMN);
				timer.mPowerStatus = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_POWER_STATUS_COLUMN);

				timer.mCurtainEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_CURTAIN_ENABLE_COLUMN);
				timer.mCurtainModuleID = cur
						.getString(SmartPlugContentDefine.SmartPlugScene.SCENE_CURTAIN_MODULEID_COLUMN);
				timer.mCurtainStatus = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_CURTAIN_STATUS_COLUMN);

				timer.mAirConEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_ENABLE_COLUMN);
				timer.mAirConModuleID = cur
						.getString(SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_MODULEID_COLUMN);
				timer.mAirConStatus = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_STATUS_COLUMN);
				timer.mAirConTemperature = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_TEMPERATURE_COLUMN);

				timer.mPCEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_ENABLE_COLUMN);
				timer.mPCModuleID = cur
						.getString(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_MODULEID_COLUMN);
				timer.mPCMacModuleID = cur
						.getString(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_MAC_MODULEID_COLUMN);
				timer.mPCMacAddress = cur
						.getString(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_MAC_ADDRESS_COLUMN);
				timer.mPCStatus = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_STATUS_COLUMN);
				timers.add(timer);
			}
			cur.close();
		}
		return timers;
	}

	public SceneDefine getScene(int timerId) {
		SceneDefine timer = new SceneDefine();
		if (null == mContentResolver) {
			return null;
		}

		String where = SmartPlugContentDefine.SmartPlugScene.SCENE_ID + "='"
				+ timerId + "'";

		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugScene.ALL_CONTENT_URI, null,
				where, null, null);
		if (null != cur) {
			while (cur.moveToNext()) {
				timer.mId = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.ID_COLUMN);
				timer.mSceneId = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_ID_COLUMN);
				timer.mSceneName = cur
						.getString(SmartPlugContentDefine.SmartPlugScene.SCENE_NAME_COLUMN);
				timer.mPowerEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_POWER_ENABLE_COLUMN);
				timer.mPowerModuleID = cur
						.getString(SmartPlugContentDefine.SmartPlugScene.SCENE_POWER_MODULEID_COLUMN);
				timer.mPowerStatus = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_POWER_STATUS_COLUMN);

				timer.mCurtainEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_CURTAIN_ENABLE_COLUMN);
				timer.mCurtainModuleID = cur
						.getString(SmartPlugContentDefine.SmartPlugScene.SCENE_CURTAIN_MODULEID_COLUMN);
				timer.mCurtainStatus = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_CURTAIN_STATUS_COLUMN);

				timer.mAirConEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_ENABLE_COLUMN);
				timer.mAirConModuleID = cur
						.getString(SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_MODULEID_COLUMN);
				timer.mAirConStatus = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_STATUS_COLUMN);
				timer.mAirConTemperature = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_TEMPERATURE_COLUMN);

				timer.mPCEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_ENABLE_COLUMN);
				timer.mPCModuleID = cur
						.getString(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_MODULEID_COLUMN);
				timer.mPCMacModuleID = cur
						.getString(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_MAC_MODULEID_COLUMN);
				timer.mPCMacAddress = cur
						.getString(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_MAC_ADDRESS_COLUMN);
				timer.mPCStatus = cur
						.getInt(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_STATUS_COLUMN);
			}
			cur.close();
			return timer;
		} else {
			return null;
		}
	}

	public long addScene(final SceneDefine timer) {
		if (null == mContentResolver) {
			return 0;
		}
		if (null == timer) {
			return 0;
		}

		ContentValues values = new ContentValues();
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_ID,
				timer.mSceneId);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_NAME,
				timer.mSceneName);

		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_POWER_ENABLE,
				timer.mPowerEnable);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_POWER_MODULEID,
				timer.mPowerModuleID);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_POWER_STATUS,
				timer.mPowerStatus);

		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_CURTAIN_ENABLE,
				timer.mCurtainEnable);
		values.put(
				SmartPlugContentDefine.SmartPlugScene.SCENE_CURTAIN_MODULEID,
				timer.mCurtainModuleID);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_CURTAIN_STATUS,
				timer.mCurtainStatus);

		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_ENABLE,
				timer.mAirConEnable);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_MODULEID,
				timer.mAirConModuleID);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_STATUS,
				timer.mAirConStatus);
		values.put(
				SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_TEMPERATURE,
				timer.mAirConTemperature);

		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_ENABLE,
				timer.mPCEnable);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_MODULEID,
				timer.mPCModuleID);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_MAC_MODULEID,
				timer.mPCMacModuleID);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_MAC_ADDRESS,
				timer.mPCMacAddress);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_STATUS,
				timer.mPCStatus);

		Uri uri = mContentResolver.insert(
				SmartPlugContentDefine.SmartPlugScene.ALL_CONTENT_URI, values);

		if (null == uri) {
			return 0;
		}
		try {
			long id = ContentUris.parseId(uri);
			return id;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return 0;
		}
	}

	public boolean deleteScene(int timerId) {
		if (null == mContentResolver) {
			return false;
		}
		String where = SmartPlugContentDefine.SmartPlugScene.SCENE_ID + "='"
				+ timerId + "'";
		int count = mContentResolver.delete(
				SmartPlugContentDefine.SmartPlugScene.ALL_CONTENT_URI, where,
				null);
		return count > 0 ? true : false;
	}

	public boolean clearScene() {
		if (null == mContentResolver) {
			return false;
		}
		int count = mContentResolver.delete(
				SmartPlugContentDefine.SmartPlugScene.ALL_CONTENT_URI, null,
				null);
		return count > 0 ? true : false;
	}

	public int modifyScene(SceneDefine timer) {
		if (null == mContentResolver) {
			return -1;
		}
		if (null == timer) {
			return -1;
		}

		ContentValues values = new ContentValues();
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_ID,
				timer.mSceneId);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_NAME,
				timer.mSceneName);

		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_POWER_ENABLE,
				timer.mPowerEnable);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_POWER_MODULEID,
				timer.mPowerModuleID);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_POWER_STATUS,
				timer.mPowerStatus);

		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_CURTAIN_ENABLE,
				timer.mCurtainEnable);
		values.put(
				SmartPlugContentDefine.SmartPlugScene.SCENE_CURTAIN_MODULEID,
				timer.mCurtainModuleID);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_CURTAIN_STATUS,
				timer.mCurtainStatus);

		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_ENABLE,
				timer.mAirConEnable);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_MODULEID,
				timer.mAirConModuleID);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_STATUS,
				timer.mAirConStatus);
		values.put(
				SmartPlugContentDefine.SmartPlugScene.SCENE_AIRCON_TEMPERATURE,
				timer.mAirConTemperature);

		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_ENABLE,
				timer.mPCEnable);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_MODULEID,
				timer.mPCModuleID);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_MAC_MODULEID,
				timer.mPCMacModuleID);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_MAC_ADDRESS,
				timer.mPCMacAddress);
		values.put(SmartPlugContentDefine.SmartPlugScene.SCENE_PC_STATUS,
				timer.mPCStatus);

		String where = SmartPlugContentDefine.SmartPlugScene.SCENE_ID + "='"
				+ timer.mSceneId + "'";
		int index = mContentResolver.update(
				SmartPlugContentDefine.SmartPlugScene.ALL_CONTENT_URI, values,
				where, null);
		return index;
	}

	public int getMaxSceneId() {
		if (null == mContentResolver) {
			return 0;
		}
		int maxid = 0;
		String[] fields = {"max("
				+ SmartPlugContentDefine.SmartPlugScene.SCENE_ID + ") as maxid"};

		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugScene.ALL_CONTENT_URI, fields,
				null, null, null);

		if (null != cur) {
			while (cur.moveToNext()) {
				maxid = cur.getInt(0);
			}
			cur.close();
		}
		maxid = (maxid < 100) ? 100 : maxid;
		return maxid + 1;
	}

}
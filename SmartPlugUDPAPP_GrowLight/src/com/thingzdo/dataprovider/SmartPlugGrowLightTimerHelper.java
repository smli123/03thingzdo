package com.thingzdo.dataprovider;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.thingzdo.ui.GrowLightTimerDefine;

public class SmartPlugGrowLightTimerHelper {
	private Context mContext;
	private ContentResolver mContentResolver;
	private String TAG = "SmartPlugGrowLightTimerHelper";

	public SmartPlugGrowLightTimerHelper(Context context) {
		mContext = context;
		if (mContext == null) {
			Log.e(TAG, "Context is empty");
		} else {
			mContentResolver = mContext.getContentResolver();
		}
	}

	// ���ָ�����������ж�ʱ����
	public ArrayList<GrowLightTimerDefine> getAllTimer(String plugId) {
		ArrayList<GrowLightTimerDefine> timers = new ArrayList<GrowLightTimerDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String where = SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_ID
				+ " = '" + plugId + "'";
		String order = SmartPlugContentDefine.SmartPlugGrowLightTimer._ID
				+ " asc";
		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugGrowLightTimer.ALL_CONTENT_URI,
				null, where, null, order);

		if (null != cur) {
			while (cur.moveToNext()) {
				GrowLightTimerDefine timer = new GrowLightTimerDefine();
				timer.mId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimer.ID_COLUMN);
				timer.mTimerId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimer.TIMER_ID_COLUMN);
				timer.mPlugId = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_ID_COLUMN);
				timer.mType = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimer.TIMER_TYPE_COLUMN);
				timer.mEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_TIMER_ENABLE_COLUMN) == 1
						? true
						: false;
				timer.mPeriod = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_PERIOD_COLUMN);
				timer.mPowerOnTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_BEGINTIME_COLUMN);
				timer.mPowerOffTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_ENDTIME_COLUMN);
				timer.light01 = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT01_COLUMN);
				timer.light02 = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT02_COLUMN);
				timer.light03 = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT03_COLUMN);
				timer.light04 = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT04_COLUMN);
				timer.light05 = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT05_COLUMN);
				timers.add(timer);
			}
			cur.close();
		}
		return timers;
	}

	// ������ж�ʱ����
	public ArrayList<GrowLightTimerDefine> getAllTimer() {
		ArrayList<GrowLightTimerDefine> timers = new ArrayList<GrowLightTimerDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String order = SmartPlugContentDefine.SmartPlugGrowLightTimer._ID
				+ " asc";
		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugGrowLightTimer.ALL_CONTENT_URI,
				null, null, null, order);

		if (null != cur) {
			while (cur.moveToNext()) {
				GrowLightTimerDefine timer = new GrowLightTimerDefine();
				timer.mId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimer.ID_COLUMN);
				timer.mTimerId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimer.TIMER_ID_COLUMN);
				timer.mPlugId = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_ID_COLUMN);
				timer.mType = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimer.TIMER_TYPE_COLUMN);
				timer.mEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_TIMER_ENABLE_COLUMN) == 1
						? true
						: false;
				timer.mPeriod = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_PERIOD_COLUMN);
				timer.mPowerOnTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_BEGINTIME_COLUMN);
				timer.mPowerOffTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_ENDTIME_COLUMN);
				timer.light01 = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT01_COLUMN);
				timer.light02 = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT02_COLUMN);
				timer.light03 = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT03_COLUMN);
				timer.light04 = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT04_COLUMN);
				timer.light05 = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT05_COLUMN);
				timers.add(timer);
			}
			cur.close();
		}
		return timers;
	}

	// ��ѯһ��timer
	public GrowLightTimerDefine getTimer(String plugId, int timerId) {
		GrowLightTimerDefine timer = new GrowLightTimerDefine();
		if (null == mContentResolver) {
			return null;
		}

		String where = SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_ID
				+ " = '" + plugId + "'" + " AND "
				+ SmartPlugContentDefine.SmartPlugGrowLightTimer.TIMER_ID
				+ " = '" + timerId + "'";
		String order = SmartPlugContentDefine.SmartPlugGrowLightTimer._ID
				+ " asc";
		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugGrowLightTimer.ALL_CONTENT_URI,
				null, where, null, order);
		if (null != cur) {
			while (cur.moveToNext()) {
				timer.mId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimer.ID_COLUMN);
				timer.mTimerId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimer.TIMER_ID_COLUMN);
				timer.mPlugId = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_ID_COLUMN);
				timer.mType = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimer.TIMER_TYPE_COLUMN);
				timer.mEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_TIMER_ENABLE_COLUMN) == 1
						? true
						: false;
				timer.mPeriod = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_PERIOD_COLUMN);
				timer.mPowerOnTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_BEGINTIME_COLUMN);
				timer.mPowerOffTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_ENDTIME_COLUMN);
				timer.light01 = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT01_COLUMN);
				timer.light02 = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT02_COLUMN);
				timer.light03 = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT03_COLUMN);
				timer.light04 = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT04_COLUMN);
				timer.light05 = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT05_COLUMN);
			}
			cur.close();
			return timer;
		} else {
			return null;
		}
	}
	// ����һ����ʱ����
	public long addTimer(final GrowLightTimerDefine timer) {
		if (null == mContentResolver) {
			return 0;
		}
		if (null == timer) {
			return 0;
		}

		ContentValues values = new ContentValues();
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.TIMER_ID,
				timer.mTimerId);
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_ID,
				timer.mPlugId);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_TIMER_TYPE,
				timer.mType);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_TIMER_ENABLE,
				String.valueOf(timer.mEnable == true ? 1 : false));
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_PERIOD,
				timer.mPeriod);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_BEGINTIME,
				timer.mPowerOnTime);
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_ENDTIME,
				timer.mPowerOffTime);
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT01,
				timer.light01);
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT02,
				timer.light02);
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT03,
				timer.light03);
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT04,
				timer.light04);
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT05,
				timer.light05);

		Uri uri = mContentResolver.insert(
				SmartPlugContentDefine.SmartPlugGrowLightTimer.ALL_CONTENT_URI,
				values);

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

	// ɾ��һ����ʱ����
	public boolean deleteTimer(int timerId) {
		if (null == mContentResolver) {
			return false;
		}
		String where = SmartPlugContentDefine.SmartPlugGrowLightTimer._ID
				+ "='" + timerId + "'";
		int count = mContentResolver.delete(
				SmartPlugContentDefine.SmartPlugGrowLightTimer.ALL_CONTENT_URI,
				where, null);
		return count > 0 ? true : false;
	}

	// ɾ��һ�����������ж�ʱ����
	public boolean clearTimer(String lpugId) {
		if (null == mContentResolver) {
			return false;
		}
		String where = SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_ID
				+ "='" + lpugId + "'";
		int count = mContentResolver.delete(
				SmartPlugContentDefine.SmartPlugGrowLightTimer.ALL_CONTENT_URI,
				where, null);
		return count > 0 ? true : false;
	}

	// �޸�һ����ʱ����
	public int modifyTimer(GrowLightTimerDefine timer) {
		if (null == mContentResolver) {
			return -1;
		}
		if (null == timer) {
			return -1;
		}

		ContentValues values = new ContentValues();
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_TIMER_TYPE,
				timer.mType);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_TIMER_ENABLE,
				timer.mEnable);
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_PERIOD,
				timer.mPeriod);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_BEGINTIME,
				timer.mPowerOnTime);
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_ENDTIME,
				timer.mPowerOffTime);
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT01,
				timer.light01);
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT02,
				timer.light02);
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT03,
				timer.light03);
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT04,
				timer.light04);
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_LIGHT05,
				timer.light05);

		String where = SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_ID
				+ "='" + timer.mPlugId + "' AND "
				+ SmartPlugContentDefine.SmartPlugGrowLightTimer.TIMER_ID
				+ "='" + timer.mTimerId + "'";
		int index = mContentResolver.update(
				SmartPlugContentDefine.SmartPlugGrowLightTimer.ALL_CONTENT_URI,
				values, where, null);
		return index;
	}

	public int getMaxTimerId(String plugid) {
		if (null == mContentResolver) {
			return 0;
		}
		int maxid = 0;
		String[] fields = {"max("
				+ SmartPlugContentDefine.SmartPlugGrowLightTimer.TIMER_ID
				+ ") as maxid"};
		String sWhere = SmartPlugContentDefine.SmartPlugGrowLightTimer.PLUG_ID
				+ "='" + plugid + "'";
		Cursor cur = mContentResolver.query(
				SmartPlugContentDefine.SmartPlugGrowLightTimer.ALL_CONTENT_URI,
				fields, sWhere, null, null);

		if (null != cur) {
			while (cur.moveToNext()) {
				maxid = cur.getInt(0);
			}
			cur.close();
		}
		return maxid;
	}

}
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

public class SmartPlugGrowLightTimerCurveHelper {
	private Context mContext;
	private ContentResolver mContentResolver;
	private String TAG = "SmartPlugGrowLightTimerCurveHelper";

	public SmartPlugGrowLightTimerCurveHelper(Context context) {
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
		String where = SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_ID
				+ " = '" + plugId + "'";
		String order = SmartPlugContentDefine.SmartPlugGrowLightTimerCurve._ID
				+ " asc";
		Cursor cur = mContentResolver
				.query(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.ALL_CONTENT_URI,
						null, where, null, order);

		if (null != cur) {
			while (cur.moveToNext()) {
				GrowLightTimerDefine timer = new GrowLightTimerDefine();
				timer.mId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.ID_COLUMN);
				timer.mTimerId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.TIMER_ID_COLUMN);
				timer.mPlugId = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_ID_COLUMN);
				timer.mType = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.TIMER_TYPE_COLUMN);
				timer.mEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_TIMER_ENABLE_COLUMN) == 1
						? true
						: false;
				timer.mPeriod = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_PERIOD_COLUMN);
				timer.mPowerOnTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_BEGINTIME_COLUMN);
				timer.mPowerOffTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_ENDTIME_COLUMN);
				timer.light01 = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT01_COLUMN);
				timer.light02 = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT02_COLUMN);
				timer.light03 = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT03_COLUMN);
				timer.light04 = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT04_COLUMN);
				timer.light05 = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT05_COLUMN);
				timer.light_lushu = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT_LUSHU_COLUMN);
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
		String order = SmartPlugContentDefine.SmartPlugGrowLightTimerCurve._ID
				+ " asc";
		Cursor cur = mContentResolver
				.query(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.ALL_CONTENT_URI,
						null, null, null, order);

		if (null != cur) {
			while (cur.moveToNext()) {
				GrowLightTimerDefine timer = new GrowLightTimerDefine();
				timer.mId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.ID_COLUMN);
				timer.mTimerId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.TIMER_ID_COLUMN);
				timer.mPlugId = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_ID_COLUMN);
				timer.mType = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.TIMER_TYPE_COLUMN);
				timer.mEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_TIMER_ENABLE_COLUMN) == 1
						? true
						: false;
				timer.mPeriod = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_PERIOD_COLUMN);
				timer.mPowerOnTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_BEGINTIME_COLUMN);
				timer.mPowerOffTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_ENDTIME_COLUMN);
				timer.light01 = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT01_COLUMN);
				timer.light02 = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT02_COLUMN);
				timer.light03 = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT03_COLUMN);
				timer.light04 = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT04_COLUMN);
				timer.light05 = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT05_COLUMN);
				timer.light_lushu = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT_LUSHU_COLUMN);
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

		String where = SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_ID
				+ " = '"
				+ plugId
				+ "'"
				+ " AND "
				+ SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.TIMER_ID
				+ " = '" + timerId + "'";
		String order = SmartPlugContentDefine.SmartPlugGrowLightTimerCurve._ID
				+ " asc";
		Cursor cur = mContentResolver
				.query(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.ALL_CONTENT_URI,
						null, where, null, order);
		if (null != cur) {
			while (cur.moveToNext()) {
				timer.mId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.ID_COLUMN);
				timer.mTimerId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.TIMER_ID_COLUMN);
				timer.mPlugId = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_ID_COLUMN);
				timer.mType = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.TIMER_TYPE_COLUMN);
				timer.mEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_TIMER_ENABLE_COLUMN) == 1
						? true
						: false;
				timer.mPeriod = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_PERIOD_COLUMN);
				timer.mPowerOnTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_BEGINTIME_COLUMN);
				timer.mPowerOffTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_ENDTIME_COLUMN);
				timer.light01 = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT01_COLUMN);
				timer.light02 = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT02_COLUMN);
				timer.light03 = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT03_COLUMN);
				timer.light04 = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT04_COLUMN);
				timer.light05 = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT05_COLUMN);
				timer.light_lushu = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT_LUSHU_COLUMN);
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
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.TIMER_ID,
				timer.mTimerId);
		values.put(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_ID,
				timer.mPlugId);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_TIMER_TYPE,
				timer.mType);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_TIMER_ENABLE,
				String.valueOf(timer.mEnable == true ? 1 : false));
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_PERIOD,
				timer.mPeriod);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_BEGINTIME,
				timer.mPowerOnTime);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_ENDTIME,
				timer.mPowerOffTime);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT01,
				timer.light01);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT02,
				timer.light02);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT03,
				timer.light03);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT04,
				timer.light04);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT05,
				timer.light05);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT_LUSHU,
				timer.light_lushu);

		Uri uri = mContentResolver
				.insert(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.ALL_CONTENT_URI,
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
		String where = SmartPlugContentDefine.SmartPlugGrowLightTimerCurve._ID
				+ "='" + timerId + "'";
		int count = mContentResolver
				.delete(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.ALL_CONTENT_URI,
						where, null);
		return count > 0 ? true : false;
	}

	// ɾ��һ�����������ж�ʱ����
	public boolean clearTimer(String lpugId) {
		if (null == mContentResolver) {
			return false;
		}
		String where = SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_ID
				+ "='" + lpugId + "'";
		int count = mContentResolver
				.delete(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.ALL_CONTENT_URI,
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
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_TIMER_TYPE,
				timer.mType);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_TIMER_ENABLE,
				timer.mEnable);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_PERIOD,
				timer.mPeriod);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_BEGINTIME,
				timer.mPowerOnTime);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_ENDTIME,
				timer.mPowerOffTime);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT01,
				timer.light01);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT02,
				timer.light02);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT03,
				timer.light03);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT04,
				timer.light04);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT05,
				timer.light05);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_LIGHT_LUSHU,
				timer.light_lushu);

		String where = SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_ID
				+ "='"
				+ timer.mPlugId
				+ "' AND "
				+ SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.TIMER_ID
				+ "='" + timer.mTimerId + "'";
		int index = mContentResolver
				.update(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.ALL_CONTENT_URI,
						values, where, null);
		return index;
	}

	public int getMaxTimerId(String plugid) {
		if (null == mContentResolver) {
			return 0;
		}
		int maxid = 0;
		String[] fields = {"max("
				+ SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.TIMER_ID
				+ ") as maxid"};
		String sWhere = SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.PLUG_ID
				+ "='" + plugid + "'";
		Cursor cur = mContentResolver
				.query(SmartPlugContentDefine.SmartPlugGrowLightTimerCurve.ALL_CONTENT_URI,
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
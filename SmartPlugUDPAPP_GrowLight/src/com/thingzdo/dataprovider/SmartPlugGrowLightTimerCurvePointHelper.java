package com.thingzdo.dataprovider;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.thingzdo.ui.GrowLightTimerCurvePointDefine;

public class SmartPlugGrowLightTimerCurvePointHelper {
	private Context mContext;
	private ContentResolver mContentResolver;
	private String TAG = "SmartPlugGrowLightTimerCurvePointHelper";

	public SmartPlugGrowLightTimerCurvePointHelper(Context context) {
		mContext = context;
		if (mContext == null) {
			Log.e(TAG, "Context is empty");
		} else {
			mContentResolver = mContext.getContentResolver();
		}
	}

	// ���ָ�����������ж�ʱ����
	public ArrayList<GrowLightTimerCurvePointDefine> getAllTimer(String plugId) {
		ArrayList<GrowLightTimerCurvePointDefine> timers = new ArrayList<GrowLightTimerCurvePointDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String where = SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_ID
				+ " = '" + plugId + "'";
		String order = SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint._ID
				+ " asc";
		Cursor cur = mContentResolver
				.query(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.ALL_CONTENT_URI,
						null, where, null, order);

		if (null != cur) {
			while (cur.moveToNext()) {
				GrowLightTimerCurvePointDefine timer = new GrowLightTimerCurvePointDefine();
				timer.mId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.ID_COLUMN);
				timer.mTimerId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.TIMER_ID_COLUMN);
				timer.mPlugId = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_ID_COLUMN);
				timer.mType = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.TIMER_TYPE_COLUMN);
				timer.mEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_TIMER_ENABLE_COLUMN) == 1
						? true
						: false;
				timer.mPeriod = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_PERIOD_COLUMN);
				timer.mPowerOnTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_BEGINTIME_COLUMN);
				timer.light = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_LIGHT_COLUMN);
				timer.light_channel = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_LIGHT_CHANNEL_COLUMN);
				timers.add(timer);
			}
			cur.close();
		}
		return timers;
	}
	// ���ָ�����������ж�ʱ����
	public ArrayList<GrowLightTimerCurvePointDefine> getAllTimer(String plugId,
			int channelId) {
		ArrayList<GrowLightTimerCurvePointDefine> timers = new ArrayList<GrowLightTimerCurvePointDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String where = SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_ID
				+ " = '"
				+ plugId
				+ "'"
				+ " AND "
				+ SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_LIGHT_CHANNEL
				+ " = '" + channelId + "'";
		String order = SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_BEGINTIME
				+ " asc";
		Cursor cur = mContentResolver
				.query(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.ALL_CONTENT_URI,
						null, where, null, order);

		if (null != cur) {
			while (cur.moveToNext()) {
				GrowLightTimerCurvePointDefine timer = new GrowLightTimerCurvePointDefine();
				timer.mId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.ID_COLUMN);
				timer.mTimerId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.TIMER_ID_COLUMN);
				timer.mPlugId = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_ID_COLUMN);
				timer.mType = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.TIMER_TYPE_COLUMN);
				timer.mEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_TIMER_ENABLE_COLUMN) == 1
						? true
						: false;
				timer.mPeriod = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_PERIOD_COLUMN);
				timer.mPowerOnTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_BEGINTIME_COLUMN);
				timer.light = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_LIGHT_COLUMN);
				timer.light_channel = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_LIGHT_CHANNEL_COLUMN);
				timers.add(timer);
			}
			cur.close();
		}
		return timers;
	}

	// ������ж�ʱ����
	public ArrayList<GrowLightTimerCurvePointDefine> getAllTimer() {
		ArrayList<GrowLightTimerCurvePointDefine> timers = new ArrayList<GrowLightTimerCurvePointDefine>();
		if (null == mContentResolver) {
			return null;
		}
		String order = SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint._ID
				+ " asc";
		Cursor cur = mContentResolver
				.query(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.ALL_CONTENT_URI,
						null, null, null, order);

		if (null != cur) {
			while (cur.moveToNext()) {
				GrowLightTimerCurvePointDefine timer = new GrowLightTimerCurvePointDefine();
				timer.mId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.ID_COLUMN);
				timer.mTimerId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.TIMER_ID_COLUMN);
				timer.mPlugId = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_ID_COLUMN);
				timer.mType = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.TIMER_TYPE_COLUMN);
				timer.mEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_TIMER_ENABLE_COLUMN) == 1
						? true
						: false;
				timer.mPeriod = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_PERIOD_COLUMN);
				timer.mPowerOnTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_BEGINTIME_COLUMN);
				timer.light = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_LIGHT_COLUMN);
				timer.light_channel = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_LIGHT_CHANNEL_COLUMN);
				timers.add(timer);
			}
			cur.close();
		}
		return timers;
	}

	// ��ѯһ��timer
	public GrowLightTimerCurvePointDefine getTimer(String plugId, int timerId) {
		GrowLightTimerCurvePointDefine timer = new GrowLightTimerCurvePointDefine();
		if (null == mContentResolver) {
			return null;
		}

		String where = SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_ID
				+ " = '"
				+ plugId
				+ "'"
				+ " AND "
				+ SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.TIMER_ID
				+ " = '" + timerId + "'";
		String order = SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint._ID
				+ " asc";
		Cursor cur = mContentResolver
				.query(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.ALL_CONTENT_URI,
						null, where, null, order);
		if (null != cur) {
			while (cur.moveToNext()) {
				timer.mId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.ID_COLUMN);
				timer.mTimerId = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.TIMER_ID_COLUMN);
				timer.mPlugId = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_ID_COLUMN);
				timer.mType = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.TIMER_TYPE_COLUMN);
				timer.mEnable = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_TIMER_ENABLE_COLUMN) == 1
						? true
						: false;
				timer.mPeriod = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_PERIOD_COLUMN);
				timer.mPowerOnTime = cur
						.getString(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_BEGINTIME_COLUMN);
				timer.light = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_LIGHT_COLUMN);
				timer.light_channel = cur
						.getInt(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_LIGHT_CHANNEL_COLUMN);
			}
			cur.close();
			return timer;
		} else {
			return null;
		}
	}
	// ����һ����ʱ����
	public long addTimer(final GrowLightTimerCurvePointDefine timer) {
		if (null == mContentResolver) {
			return 0;
		}
		if (null == timer) {
			return 0;
		}

		ContentValues values = new ContentValues();
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.TIMER_ID,
				timer.mTimerId);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_ID,
				timer.mPlugId);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_TIMER_TYPE,
				timer.mType);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_TIMER_ENABLE,
				String.valueOf(timer.mEnable == true ? 1 : false));
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_PERIOD,
				timer.mPeriod);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_BEGINTIME,
				timer.mPowerOnTime);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_LIGHT,
				timer.light);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_LIGHT_CHANNEL,
				timer.light_channel);

		Uri uri = mContentResolver
				.insert(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.ALL_CONTENT_URI,
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
		String where = SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint._ID
				+ "='" + timerId + "'";
		int count = mContentResolver
				.delete(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.ALL_CONTENT_URI,
						where, null);
		return count > 0 ? true : false;
	}

	// ɾ��һ�����������ж�ʱ����
	public boolean clearTimer(String lpugId) {
		if (null == mContentResolver) {
			return false;
		}
		String where = SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_ID
				+ "='" + lpugId + "'";
		int count = mContentResolver
				.delete(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.ALL_CONTENT_URI,
						where, null);
		return count > 0 ? true : false;
	}

	// ɾ��һ�����������ж�ʱ����
	public boolean clearTimer(String lpugId, int channel) {
		if (null == mContentResolver) {
			return false;
		}
		String where = SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_ID
				+ "='"
				+ lpugId
				+ "'"
				+ " AND "
				+ SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_LIGHT_CHANNEL
				+ " = '" + channel + "'";
		int count = mContentResolver
				.delete(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.ALL_CONTENT_URI,
						where, null);
		return count > 0 ? true : false;
	}

	// �޸�һ����ʱ����
	public int modifyTimer(GrowLightTimerCurvePointDefine timer) {
		if (null == mContentResolver) {
			return -1;
		}
		if (null == timer) {
			return -1;
		}

		ContentValues values = new ContentValues();
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_TIMER_TYPE,
				timer.mType);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_TIMER_ENABLE,
				timer.mEnable);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_PERIOD,
				timer.mPeriod);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_BEGINTIME,
				timer.mPowerOnTime);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_LIGHT,
				timer.light);
		values.put(
				SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_LIGHT_CHANNEL,
				timer.light_channel);

		String where = SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_ID
				+ "='"
				+ timer.mPlugId
				+ "' AND "
				+ SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.TIMER_ID
				+ "='" + timer.mTimerId + "'";
		int index = mContentResolver
				.update(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.ALL_CONTENT_URI,
						values, where, null);
		return index;
	}

	public int getMaxTimerId(String plugid) {
		if (null == mContentResolver) {
			return 0;
		}
		int maxid = 0;
		String[] fields = {"max("
				+ SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.TIMER_ID
				+ ") as maxid"};
		String sWhere = SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.PLUG_ID
				+ "='" + plugid + "'";
		Cursor cur = mContentResolver
				.query(SmartPlugContentDefine.SmartPlugGrowLightTimerCurvePoint.ALL_CONTENT_URI,
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
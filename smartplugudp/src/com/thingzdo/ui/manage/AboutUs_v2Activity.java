package com.thingzdo.ui.manage;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class AboutUs_v2Activity extends TitledActivity
		implements
			OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_about_v2, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		setTitleLeftButton(R.string.smartplug_goback,
				R.drawable.title_btn_selector, this);

		TextView verText = (TextView) findViewById(R.id.tv_smartplug_version);
		verText.setText(PubFunc.getAppVersion());
		RelativeLayout rl_company_web = (RelativeLayout) findViewById(R.id.rl_company_web);
		rl_company_web.setOnClickListener(this);
		RelativeLayout rl_service_phone = (RelativeLayout) findViewById(R.id.rl_service_phone);
		rl_service_phone.setOnClickListener(this);

		TextView tv_app_version = (TextView) findViewById(R.id.tv_app_version);
		tv_app_version.setText(getVersion(this));
	}

	public static String getVersion(Context context)// 获取版本号
	{
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return context
					.getString(R.string.smartplug_service_version_unknown);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.titlebar_leftbutton :
				finish();
				break;
			case R.id.rl_company_web : // web address
				String url = SmartPlugApplication.getContext().getString(
						R.string.smartplug_company_web_address);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivity(intent);
				break;
			case R.id.rl_service_phone : // 直接播出电话
				Uri uri = Uri.parse("tel:"
						+ SmartPlugApplication.getContext().getString(
								R.string.smartplug_service_phone_number));
				Intent call = new Intent(Intent.ACTION_CALL, uri);
				startActivity(call);
				break;
		}
	}
}

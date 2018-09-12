package com.thingzdo.ui.manage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thingzdo.smartplug_udp.R;

public class UpdateManager {
	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;

	private static final int START_UPDATE_CHECK = 3;
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;

	private Context mContext;
	/* 更新进度条 */
	private ProgressBar mProgress;
	private TextView tv_progress;
	private Dialog mDownloadDialog;

	private String xmlString = null;
	private int versionType = 0; // 0: Release, 1: debug

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 正在下载
				case DOWNLOAD :
					// 设置进度条位置
					tv_progress.setText(String.format("%d%%", progress));
					mProgress.setProgress(progress);
					break;
				case DOWNLOAD_FINISH :
					// 安装文件
					installApk();
					break;
				case START_UPDATE_CHECK :
					checkUpdate();
					break;
				default :
					break;
			}
		};
	};

	public UpdateManager(Context context, int verType) {
		this.mContext = context;
		this.versionType = verType;
	}

	/**
	 * 获取XML文件并进行后续处理
	 */
	public void XMLFile_UpdateCheck() {
		new downloadXMLThread().execute();
	}

	/**
	 * 检测软件更新
	 */
	public void checkUpdate() {
		int result = isUpdate();
		if (result >= 0) {
			// 显示提示对话框
			showNoticeDialog(result);
		} else {
			Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_LONG)
					.show();
		}
	}
	/**
	 * 检查软件是否有更新版本
	 * 
	 * @return
	 */
	private int isUpdate() {
		// 获取当前软件版本
		int versionCode = getVersionCode(mContext);
		// 把version.xml放到网络上，然后获取文件信息
		// InputStream inStream = UpdateManager.class.getClassLoader()
		// .getResourceAsStream("version.xml");
		// // "http://fx.thingzdo.com/download/version.xml");

		// File xmlFile =
		// downLoadVersionFile("http://fx.thingzdo.com/download/version.xml");
		// InputStream inStream = null;

		if (xmlString == null) {
			return -1;
		}
		InputStream inStream = null;
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(
					xmlString.getBytes());
			inStream = stream;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 解析XML文件。 由于XML文件比较小，因此使用DOM方式进行解析
		ParseXmlService service = new ParseXmlService();
		try {
			mHashMap = service.parseXml(inStream);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (null != mHashMap) {
			int serviceCode = Integer.valueOf(mHashMap.get("version"));
			// 版本判断
			return (serviceCode - versionCode);
			// if (serviceCode > versionCode) {
			// return true;
			// }
		}
		return -1;
	}

	class downloadXMLThread extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			xmlString = downLoadVersionFile("http://fx.thingzdo.com/download/version.xml");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mHandler.sendEmptyMessage(START_UPDATE_CHECK);
		}

	};

	// 下载version.xml
	private String downLoadVersionFile(String url) {
		String response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) { // 访问服务器成功
				HttpEntity entity = httpResponse.getEntity();
				response = EntityUtils.toString(entity, "utf-8");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	// 下载version.xml
	protected File downLoadVersionFile_Ex(String httpUrl) {
		// TODO Auto-generated method stub
		final String fileName = "version.xml";
		File tmpFile = new File("/sdcard/update");
		if (!tmpFile.exists()) {
			tmpFile.mkdir();
		}
		final File file = new File("/sdcard/update/" + fileName);

		try {
			URL url = new URL(httpUrl);
			try {
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buf = new byte[256];
				conn.connect();
				double count = 0;
				if (conn.getResponseCode() >= 400) {
					Toast.makeText(mContext, "连接超时", Toast.LENGTH_SHORT).show();
				} else {
					while (count <= 100) {
						if (is != null) {
							int numRead = is.read(buf);
							if (numRead <= 0) {
								break;
							} else {
								fos.write(buf, 0, numRead);
							}
						} else {
							break;
						}
					}
				}

				conn.disconnect();
				fos.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return file;
	}

	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(
					"com.thingzdo.smartplug_udp", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	// final OnClickListener okListener = new OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// // 显示下载对话框
	// showDownloadDialog();
	//
	// }
	// };

	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog(int result) {
		// MyAlertDialog builder = new MyAlertDialog(mContext).builder();
		//
		// builder.setMsg(mContext.getString(R.string.soft_update_info));
		// builder.setPositiveButton(
		// mContext.getString(R.string.soft_update_updatebtn),
		// new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		//
		// }
		// });
		// builder.setNegativeButton(
		// mContext.getString(R.string.smartplug_cancel),
		// new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		//
		// }
		// });
		// builder.setCancelable(true).show();

		// 构造对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_title);
		int info = result > 0
				? R.string.soft_update_info
				: R.string.soft_update_info_equle;
		builder.setMessage(info);
		// 更新
		builder.setPositiveButton(R.string.soft_update_updatebtn,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 显示下载对话框
						showDownloadDialog();
					}
				});
		// 稍后更新
		builder.setNegativeButton(R.string.soft_update_later,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		// builder.setCancelable(false);
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}
	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog() {
		// 构造软件下载对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		tv_progress = (TextView) v.findViewById(R.id.tv_progress);
		builder.setView(v);
		// 取消更新
		builder.setNegativeButton(R.string.soft_update_cancel,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置取消状态
						cancelUpdate = true;
					}
				});
		builder.setCancelable(false);
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// 现在文件
		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk() {
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 * 
	 * @author coolszy
	 */
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "update";
					String label = "url";
					if (versionType == 0) {
						label = "url";
					} else if (versionType == 1) {
						label = "debugurl";
					}

					URL url = new URL(mHashMap.get(label));
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do {
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
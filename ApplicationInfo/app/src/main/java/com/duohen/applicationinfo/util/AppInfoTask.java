package com.duohen.applicationinfo.util;

import java.util.List;

import com.duohen.applicationinfo.R;
import com.duohen.applicationinfo.adapter.AppInfoAdapter;
import com.duohen.applicationinfo.model.AppInfo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ListView;

/**
 * ��ѯAppInfo�����߳�
 * �������Ͳ����ֱ�Ϊ����Ĳ��������ȵ�λ������ֵ����
 * Void��ʾ����Ҫ�������
 */
public class AppInfoTask extends AsyncTask<Integer, Integer, AppInfoAdapter> {
    public static final int GET_LAUNCH_APP = -1; // ����Intent��ȡ������APP
    public static final int GET_ALL_APP = AppInfoUtil.GET_ALL_APP; // ����APP
    public static final int GET_SYSTEM_APP = AppInfoUtil.GET_SYSTEM_APP; // ϵͳԤװAPP
    public static final int GET_THIRD_APP = AppInfoUtil.GET_THIRD_APP; // ������APP
    public static final int GET_SDCARD_APP = AppInfoUtil.GET_SDCARD_APP; // SDCard��APP
    
    private Context context;
    private ListView lvApp;
    private ProgressDialog progressDialog;

    public AppInfoTask(Context context, ListView listView) {
        super();
        this.context = context;
        this.lvApp = listView;
    }

    /** ����ʼִ��֮ǰ���ã����ڳ�ʼ������ **/
    @Override
    protected void onPreExecute() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("���ڼ���");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /** ���д��붼�����߳������У����ڴ����ʱ���񣬲��ܲ���UI **/
    @Override
    protected AppInfoAdapter doInBackground(Integer... params) {
        int type = params[0];
        AppInfoUtil appInfoUtil = AppInfoUtil.getInstance(context);
        List<AppInfo> appInfoList = null;
        switch (type) {
        case GET_LAUNCH_APP:
            Intent launchIntent = new Intent(Intent.ACTION_MAIN, null);
            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            appInfoList = appInfoUtil.getAppInfoByIntent(launchIntent);
            break;
        case GET_ALL_APP:
        case GET_SYSTEM_APP:
        case GET_THIRD_APP:
        case GET_SDCARD_APP:
            appInfoList = appInfoUtil.getInstalledApps(type);
        default:
            break;
        }
        AppInfoAdapter adapter = new AppInfoAdapter(context, R.layout.listview_app_info, appInfoList);
        return adapter;
    }
    
    /** ��̨����ͨ��publishProgress()�����󣬴˷����ܿ챻���ã��ɲ���UI **/
    @Override
    protected void onProgressUpdate(Integer... values) {
        
    }
    
    /** ��̨����ִ����ϲ�ʹ��return����ʱ���˷����ܿ챻���ã��ɲ���UI **/
    @Override
    protected void onPostExecute(AppInfoAdapter result) {
        lvApp.setAdapter(result);
        // ��Activity�ı���
        ((Activity) context).setTitle("�ҵ�APP " + result.getCount() + " ��");
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }   
}


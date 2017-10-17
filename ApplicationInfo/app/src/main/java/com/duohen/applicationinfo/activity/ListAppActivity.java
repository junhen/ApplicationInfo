package com.duohen.applicationinfo.activity;

import java.lang.reflect.Method;

import com.duohen.applicationinfo.R;
import com.duohen.applicationinfo.adapter.AppInfoAdapter;
import com.duohen.applicationinfo.model.AppInfo;
import com.duohen.applicationinfo.util.AppInfoTask;
import com.duohen.applicationinfo.util.AppInfoUtil;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * APP列表
 */
public class ListAppActivity extends BaseActivity implements OnItemClickListener {

    public static final int GET_LAUNCH_APP = AppInfoTask.GET_LAUNCH_APP; // ������APP
    public static final int GET_ALL_APP = AppInfoTask.GET_ALL_APP; // ����APP
    public static final int GET_SYSTEM_APP = AppInfoTask.GET_SYSTEM_APP; // ϵͳԤװAPP
    public static final int GET_THIRD_APP = AppInfoTask.GET_THIRD_APP; // ������APP
    public static final int GET_SDCARD_APP = AppInfoTask.GET_SDCARD_APP; // SDCard��APP
    
    private static String TAG = "APP_SIZE";
    
    private int type = GET_LAUNCH_APP;
    
    private ListView lvApp;
    private ProgressBar pbLaunch;
    private LayoutInflater inflater = null;

    private AppInfoUtil appInfoUtil;
    private AppInfo clickedAppInfo;
    
    // ��ǰ��ѯ������Ϣ  
    private long cachesize ; //�����С  
    private long datasize  ;  //���ݴ�С   
    private long codesize  ;  //Ӧ�ó����С  
    private long totalsize ; //�ܴ�С 
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_app_layout);
        init();
    }

    /** ��ʼʼ�� **/
    private void init() {
        lvApp = (ListView) findViewById(R.id.lvAppInfo);
        // ���ʱ�Żᴥ�����еĴ��룬����ʱ���߳��е������Ѿ�ִ�����
        lvApp.setOnItemClickListener(this);
        
        setTitle("�����С���");
        
        Intent intent = getIntent();
        type = intent.getIntExtra("type", GET_LAUNCH_APP); 
        
        new AppInfoTask(this, lvApp).execute(type);
    }
    
    /** 
     * ������Activity
     */
    public static void actionStart(Context context, int type) {
        Intent intent = new Intent(context, ListAppActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Log.i(TAG, "����OnItemClick");
        AppInfoAdapter adapter = (AppInfoAdapter) lvApp.getAdapter();
        AppInfo appInfo = adapter.getItem(position);
        clickedAppInfo = appInfo;
        switch (type) {
        case GET_LAUNCH_APP:
            Intent intent = new Intent();
            Log.i("Test", "--------------------------");
            Log.i("Test", "Ŀ�������" + appInfo.getPackageName());
            Log.i("Test", "Ŀ��Activity����" + appInfo.getLaunchActivityName());
            intent.setComponent(new ComponentName(appInfo.getPackageName(), appInfo.getLaunchActivityName()));
            startActivity(intent);
            break;
        case GET_ALL_APP:
        case GET_SYSTEM_APP:
        case GET_THIRD_APP:
        case GET_SDCARD_APP:
            // ��ȡ����С�����ݣ�������ʱ�ϳ�����û�����ν������ʱ����
            try {
                queryPackageSize(appInfo.getPackageName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        default:
            break;
        }
        
    }
    
    /** ����AlertDialog����ʾ **/
    private void showAlertDialog() {
        // ������ʾ�����õ�View
        inflater = LayoutInflater.from(this);
        View dialog = inflater.inflate(R.layout.dialog_app_size, null);
        TextView tvCacheSize = (TextView) dialog.findViewById(R.id.tv_cache_size);
        TextView tvDataSize = (TextView) dialog.findViewById(R.id.tv_data_size);
        TextView tvCodeSize = (TextView) dialog.findViewById(R.id.tv_code_size);
        TextView tvTotalSize = (TextView) dialog.findViewById(R.id.tv_total_size);
        
        //����ת������ֵ  
        tvCacheSize.setText(formateFileSize(cachesize));  
        tvDataSize.setText(formateFileSize(datasize)) ;  
        tvCodeSize.setText(formateFileSize(codesize)) ;  
        tvTotalSize.setText(formateFileSize(totalsize)) ; 
        
        //��ʾ�Զ���Ի���  
        AlertDialog.Builder builder =new AlertDialog.Builder(ListAppActivity.this) ;  
        builder.setView(dialog) ;  
        builder.setTitle(clickedAppInfo.getAppName()+"�Ĵ�С��ϢΪ��") ;  
        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.cancel() ;  
            }  
              
        });  
        builder.create().show() ;  
    }
    
    /** ��ȡָ�����Ĵ�С��Ϣ **/
    public void queryPackageSize(String packageName) throws Exception {
        Log.i(TAG, "packageName:" + packageName);
        if (packageName != null) {
            // ʹ�÷�����Ƶõ�PackageManager������غ���getPackageSizeInfo  
            PackageManager pManager = getPackageManager();
            //ͨ��������ƻ�ø����غ���  
            Method getPackageSizeInfo = pManager.getClass().getMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);  
            //���øú��������Ҹ��������� ��������������ɺ��ص�PkgSizeObserver��ĺ���  
            getPackageSizeInfo.invoke(pManager, packageName,new PkgSizeObserver());  
        }
    }
    
    /** aidl�ļ��γɵ�Bindler���Ʒ����� **/
    public class PkgSizeObserver extends IPackageStatsObserver.Stub{
        /*** 回调函数，
         * @param pStats ,返回数据封装在PackageStats对象中
         * @param succeeded  代表回调成功
         */
        @Override  
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)  
                throws RemoteException {  
            cachesize = pStats.cacheSize  ; //�����С  
            datasize = pStats.dataSize  ;  //���ݴ�С   
            codesize = pStats.codeSize  ;  //Ӧ�ó����С  
            totalsize = cachesize + datasize + codesize ;              
            Log.i(TAG, "cachesize--->"+cachesize+" datasize---->"+datasize+ " codeSize---->"+codesize); 
            Log.i(TAG, "Ŀǰ���߳�����" + Thread.currentThread().getName());
            // ��ȡ��������ʾAlertDialog
            // ���Եõ�����δ�����߳���Binder_1,2,3�����ȣ�����Ҫ�������̵߳���showAlertDialog()
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showAlertDialog();
                }
            });
        }  
    }  
    
    /** ϵͳ�������ַ���ת��**/
    private String formateFileSize(long size){
        String str = "";
        double newSize = 0;
        if (size == 0) {
            str = "0.00 B";
        } else if (size < (1 << 10)) {
            newSize = size;
            str = newSize + " B";
        } else if (size < (1 << 20)){
            newSize = 1.0 * size / (1 << 10);
            str = String.format("%.2f", newSize) + " KB";
        } else if (size < (1 << 30)) {
            newSize = 1.0 * size / (1 << 20);
            str = String.format("%.2f", newSize) + " MB";
        }
        return str;   
    }  

}


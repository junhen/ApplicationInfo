package com.duohen.applicationinfo.adapter;

import java.util.List;

import com.duohen.applicationinfo.R;
import com.duohen.applicationinfo.model.AppInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppInfoAdapter extends ArrayAdapter<AppInfo> {
    
    private int resourceId;
    
    public AppInfoAdapter(Context context, int resource, List<AppInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    /**
     * ������д�˷���
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ��ȡ��λ�ö�Ӧ������
        AppInfo appInfo = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            // δ����View�����벼���ļ������
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        // ��ÿ�����������Ӧ����Ϣ
        viewHolder.ivIcon.setImageDrawable(appInfo.getIcon());
        viewHolder.tvAppName.setText(appInfo.getAppName());
        viewHolder.tvPackageName.setText(appInfo.getPackageName());
        return view;
    }
    
    private class ViewHolder {
        ImageView ivIcon;
        TextView tvAppName;
        TextView tvPackageName;
        
        public ViewHolder(View view) {
            ivIcon = (ImageView) view.findViewById(R.id.ivApp);
            tvAppName = (TextView) view.findViewById(R.id.tvAppName);
            tvPackageName = (TextView) view.findViewById(R.id.tvPackageName);
        }
    }

}

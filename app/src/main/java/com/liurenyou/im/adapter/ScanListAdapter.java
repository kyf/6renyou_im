package com.liurenyou.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liurenyou.im.R;

import java.util.List;

/**
 * Created by keyf on 2015/10/31.
 */
public class ScanListAdapter extends BaseAdapter {

    private List<String> ds;

    private Context mContext;

    public ScanListAdapter(List<String> ds, Context context){
        this.ds = ds;
        mContext = context;
    }

    @Override
    public int getCount(){
        return ds.size();
    }

    @Override
    public Object getItem(int position){
        return ds.get(position);
    }

    @Override
    public long getItemId(int position){
        return (long)position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View result;
        TextView macAddr;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            result = inflater.inflate(R.layout.scanlistitem, null);
            macAddr = (TextView)result.findViewById(R.id.mac_addr);
            ScanHolder holder = new ScanHolder();
            holder.MacAddr = macAddr;
            result.setTag(holder);
        }else{
            result = convertView;
            ScanHolder holder = (ScanHolder)result.getTag();
            macAddr = holder.MacAddr;
        }

        String mac = ds.get(position);
        macAddr.setText(mac);
        return result;
    }

    class ScanHolder {

        public TextView MacAddr;
    }
}

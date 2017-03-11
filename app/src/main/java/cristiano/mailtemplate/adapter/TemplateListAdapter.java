package cristiano.mailtemplate.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/4/30.
 */
public class TemplateListAdapter extends BaseAdapter{

    String[] data;
    Context context;

    public TemplateListAdapter(String[] data, Context context){
        this.data=data;
        this.context=context;
    }

    @Override
    public int getCount() {
        if(data == null){
            return 0;
        } else {
            return data.length;
        }
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = new TextView(context);
        tv.setTextSize(20);
        tv.setTextColor(Color.BLUE);
        tv.setText(data[position]);
        return tv;
    }
}

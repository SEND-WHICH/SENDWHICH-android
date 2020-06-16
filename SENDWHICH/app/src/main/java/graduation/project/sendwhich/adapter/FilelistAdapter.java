package graduation.project.sendwhich.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import graduation.project.sendwhich.R;
import graduation.project.sendwhich.bean.FilelistBean;

public class FilelistAdapter extends BaseAdapter {

    private Context mContext;
    private List<FilelistBean> mList;


    public FilelistAdapter(Context context, List<FilelistBean> list) {
        mContext = context;
        mList = list;

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.view_recieve_list, null);

        final FilelistBean bean = mList.get(position);


        TextView txt_send_id = convertView.findViewById(R.id.txt_send_id);
        TextView txt_file_name = convertView.findViewById(R.id.txt_file_name);
        TextView txt_file_size = convertView.findViewById(R.id.txt_file_size);
        TextView txt_time = convertView.findViewById(R.id.txt_time);

        Button btn_download = convertView.findViewById(R.id.btn_download);

        return convertView;

    }
}

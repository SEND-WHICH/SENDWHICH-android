package graduation.project.sendwhich.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import graduation.project.sendwhich.R;
import graduation.project.sendwhich.bean.UserBean;

public class SendToAdapter extends BaseAdapter {
    private Context mContext;
    private List<UserBean> mList;


    public SendToAdapter(Context context, List<UserBean> list) {
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
        convertView = inflater.inflate(R.layout.view_user_list, null);

        final UserBean bean = mList.get(position);


        TextView txt_user_id = convertView.findViewById(R.id.txt_user_id);
        TextView txt_user_name = convertView.findViewById(R.id.txt_user_name);


        txt_user_id.setText(bean.userEmail);
        txt_user_name.setText(bean.userName);


        return convertView;

    }


}

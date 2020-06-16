package graduation.project.sendwhich;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import graduation.project.sendwhich.adapter.SendToAdapter;
import graduation.project.sendwhich.bean.UserBean;

public class SendToActivity extends AppCompatActivity {

    private static final String TAG = "imagesearchexample";
    public static final int LOAD_SUCCESS = 101;

    private ListView lst_send_to;

    private String filelist;

    private SendToAdapter m_send_to_adapter;
    private UserBean bean;
    private String URL = "http://ec2-13-209-157-83.ap-northeast-2.compute.amazonaws.com:3000/user/showmem";
    private String PER_PAGE = "&per_page=50";
    private String SORT = "&sort=interestingness-desc";
    private String FORMAT = "&format=json";
    private String CONTECT_TYPE = "&content_type=1";
    private String SEARCH_TEXT = "&text=";
    private String REQUEST_URL = URL + PER_PAGE + SORT + FORMAT + CONTECT_TYPE + SEARCH_TEXT;

    private SimpleAdapter adapter = null;
    private List<HashMap<String,String>> photoinfoList = null;
    private String userEmail = "tomato@naver.com";

    private LinearLayout test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to);

        Button btn_sendto_pre = (Button)findViewById(R.id.btn_sendto_pre);
        Button btn_sendto_next = (Button)findViewById(R.id.btn_sendto_next);

        lst_send_to = (ListView)findViewById(R.id.lst_send_to);

        new JSONTask().execute("http://ec2-13-209-157-83.ap-northeast-2.compute.amazonaws.com:3000/user/showmem");

        btn_sendto_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SendToActivity.this, SuccessActivity.class);
                startActivity(i);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
            }
        });

        btn_sendto_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SendToActivity.this, ExamineResultActivity.class);
                startActivity(i);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
            }
        });

    }


    public class JSONTask extends AsyncTask<String, String, String> {
        String resultJson = "";

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("user_id", "androidTest");
                jsonObject.accumulate("name", "yun");

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

//                    con.setRequestMethod("POST");//POST방식으로 보냄
//                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
//                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
//                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
//                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
//                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

//                    //서버로 보내기위해서 스트림 만듬
//                    OutputStream outStream = con.getOutputStream();
//                    //버퍼를 생성하고 넣음
//                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
//                    writer.write(jsonObject.toString());
//                    writer.flush();
//                    writer.close();//버퍼를 닫아줌
//
//                    //서버로 부터 데이터를 받음
//                    InputStream stream = con.getInputStream();
//
//                    reader = new BufferedReader(new InputStreamReader(stream));
//
//                    StringBuffer buffer = new StringBuffer();
//
//                    String line = "";
//                    while((line = reader.readLine()) != null){
//                        buffer.append(line);
//
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }
                    resultJson = buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임
                    return resultJson;

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String[] from = {"UserEmail", "UserName"};
            int[] to = {R.id.txt_user_id, R.id.txt_user_name};
            ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> hashmap;

            try {
                //JSONObject json = new JSONObject(resultJson);
                JSONArray jArray = new JSONArray(resultJson);

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject friend = jArray.getJSONObject(i);

                    String nameOS = friend.getString("UserEmail");
                    String username = friend.getString("UserName");

                    hashmap = new HashMap<String, String>();
                    hashmap.put("UserEmail", nameOS);
                    hashmap.put("UserName", username);
                    arrayList.add(hashmap);
                }

                final SimpleAdapter adapter = new SimpleAdapter(SendToActivity.this, arrayList, R.layout.view_user_list, from, to);
                lst_send_to.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    public boolean jsonParser(String jsonString){


        if (jsonString == null ) return false;

        jsonString = jsonString.replace("jsonFlickrApi(", "");
        jsonString = jsonString.replace(")", "");

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject photos = jsonObject.getJSONObject("photos");
            JSONArray photo = photos.getJSONArray("photo");

            photoinfoList.clear();

            for (int i = 0; i < photo.length(); i++) {
                JSONObject photoInfo = photo.getJSONObject(i);

                String userEmail = photoInfo.getString("UserEmail");
                String userName = photoInfo.getString("UserName");
                HashMap<String, String> photoinfoMap = new HashMap<String, String>();
                photoinfoMap.put("UserEail", userEmail);
                photoinfoMap.put("UserName", userName);

                photoinfoList.add(photoinfoMap);

            }

            return true;
        } catch (JSONException e) {

            Log.d(TAG, e.toString() );
        }

        return false;
    }
//
//    public class JSONTask extends AsyncTask<String, String, String> {
//        @Override
//        protected String doInBackground(String... urls) {
//            try {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.accumulate("user_id", "androidTest");
//                jsonObject.accumulate("name", "yun");
//
//                HttpURLConnection con = null;
//                BufferedReader reader = null;
//
//                try{
//                    //URL url = new URL("http://192.168.25.16:3000/users");
//                    URL url = new URL(urls[0]);
//                    con = (HttpURLConnection) url.openConnection();
//                    con.connect();
//
//                    InputStream stream = con.getInputStream();
//
//                    reader = new BufferedReader(new InputStreamReader(stream));
//
//                    StringBuffer buffer = new StringBuffer();
//
//                    String line = "";
//                    while((line = reader.readLine()) != null){
//                        buffer.append(line);
//                    }
//
//                    return buffer.toString();
//
//                } catch (MalformedURLException e){
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    if(con != null){
//                        con.disconnect();
//                    }
//                    try {
//                        if(reader != null){
//                            reader.close();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            bean = new UserBean();
//            m_send_to_adapter = new SendToAdapter(SendToActivity.this, result);
//            lst_send_to.add();
//        }
}

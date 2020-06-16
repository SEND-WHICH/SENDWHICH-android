package graduation.project.sendwhich;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.regions.Regions;

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

import androidx.appcompat.app.AppCompatActivity;

public class RecieveActivity extends AppCompatActivity {
    private ListView lst_get_list;
    String filelist;

    private TextView txt_send_id;
    private TextView txt_file_name;
    private TextView txt_file_size;
    private TextView txt_time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recieve);
        txt_send_id = (TextView)findViewById(R.id.txt_send_id);
        txt_file_name = (TextView)findViewById(R.id.txt_file_name);
        txt_file_size = (TextView)findViewById(R.id.txt_file_size);
        txt_time = (TextView)findViewById(R.id.txt_time);

        Button btn_download = (Button)findViewById(R.id.btn_download);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        lst_get_list = findViewById(R.id.lst_get_list);
        new JSONTask().execute("http://ec2-13-209-157-83.ap-northeast-2.compute.amazonaws.com:3000/getList");

    }

    public class JSONTask extends AsyncTask<String, String, String> {
        String resultJson = "";
        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("user_id", "androidTest");
                jsonObject.accumulate("name", "yun");

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.connect();

                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    resultJson = buffer.toString();//서버로 부터 받은 값을 리턴
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
                            reader.close();
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

                final SimpleAdapter adapter = new SimpleAdapter(RecieveActivity.this, arrayList, R.layout.view_recieve_list, from, to);
                lst_get_list.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

        private void downloadWithTransferUtility() {
            // Cognito 샘플 코드. CredentialsProvider 객체 생성
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "ap-northeast-2:4d85cf6b-cba7-4e2c-be1b-4910ac6740c7", // 자격 증명 풀 ID
                    Regions.AP_NORTHEAST_2 // 리전
            );

            // 반드시 호출해야 한다.
            TransferNetworkLossHandler.getInstance(getApplicationContext());
/*
            // TransferUtility 객체 생성
            val transferUtility = TransferUtility.builder()
                    .context(applicationContext)
                    .defaultBucket("Bucket_Name") // 디폴트 버킷 이름.
                    .s3Client(AmazonS3Client(credentialsProvider, Region.getRegion(Regions.AP_NORTHEAST_2)))
                    .build()

            // 다운로드 실행. object: "SomeFile.mp4". 두 번째 파라메터는 Local경로 File 객체.
            val downloadObserver = transferUtility.download("SomeFile.mp4", File(filesDir.absolutePath + "/SomeFile.mp4"))

            // 다운로드 과정을 알 수 있도록 Listener를 추가할 수 있다.
            downloadObserver.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState) {
                    if (state == TransferState.COMPLETED) {
                        Log.d("AWS", "DOWNLOAD Completed!")
                    }
                }

                override fun onProgressChanged(id: Int, current: Long, total: Long) {
                    try {
                        val done = (((current.toDouble() / total) * 100.0).toInt()) //as Int
                        Log.d("AWS", "DOWNLOAD - - ID: $id, percent done = $done")
                    }
                    catch (e: Exception) {
                        Log.d("AWS", "Trouble calculating progress percent", e)
                    }
                }

                override fun onError(id: Int, ex: Exception) {
                    Log.d("AWS", "DOWNLOAD ERROR - - ID: $id - - EX: ${ex.message.toString()}")
                }
            })*/
        }
}

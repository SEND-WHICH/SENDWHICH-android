package graduation.project.sendwhich;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendSelectActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private static final String COGNITO_POOL_ID = "ap-northeast-2:4d85cf6b-cba7-4e2c-be1b-4910ac6740c7"; // 자격 증명 풀 ID
    private static final String BUCKET_NAME = "sendwhich";
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private static final String TAG = MainActivity.class.getCanonicalName();

    ProgressDialog progressDialogUpload;
    ProgressDialog progressDialogDownload;
    Uri imageUri;

    TransferUtility transferUtility;

    private Button btn_find_file;
    private TextView txt_file_name;
    private Button btn_file_delete;
    private LinearLayout lay_add_file;
    private ImageView img_add_file;
    private TextView txt_add_file;
    private CheckBox box_select_examine;
    private Button btn_select_next;
    private String send_file_name;
    private ServiceApi service;

    private AmazonS3 amazonS3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_select);

        btn_file_delete = (Button)findViewById(R.id.btn_file_delete);
        txt_file_name = (TextView)findViewById(R.id.txt_file_name);
        btn_find_file = (Button)findViewById(R.id.btn_find_file);
        lay_add_file = (LinearLayout)findViewById(R.id.lay_add_file);
        img_add_file = (ImageView)findViewById(R.id.img_add_file);
        txt_add_file = (TextView)findViewById(R.id.txt_add_file);
        box_select_examine = (CheckBox)findViewById(R.id.box_select_examine);
        btn_select_next = (Button)findViewById(R.id.btn_select_next);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        createTransferUtility();

        btn_select_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    if (!TextUtils.isEmpty(txt_file_name.getText().toString())){
                        String objectKey = txt_file_name.getText().toString();
                        File file = null;
                        try {
                            file = createFileFromUri(imageUri, objectKey);
                            upload(file, objectKey);

                            String sendId = "potato";
                            String getId = "tomato";
                            String filename = txt_file_name.getText().toString();
                            String n = "null";
                            int test = 123456;
                            String timestamp = new SimpleDateFormat("yyyy/MM/dd_hh:mm:ss").format(new Date());


                            startSend(new sendFileinfoData(sendId, getId, filename,timestamp,n,n,test));

                        } catch (IOException e) {
                            Log.e(TAG, "onClick: ", e);
                            Toast.makeText(SendSelectActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SendSelectActivity.this, "Enter object key in EditText", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SendSelectActivity.this, "Pick any image to upload", Toast.LENGTH_SHORT).show();
                }

                Intent i = new Intent(SendSelectActivity.this, ExamineResultActivity.class);
                startActivity(i);
            }
        });

        btn_find_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
            }
        });

    }
    private void startSend(sendFileinfoData data) {
        service.sendFiledata(data).enqueue(new Callback<sendFileinfoResponse>() {
            @Override
            public void onResponse(Call<sendFileinfoResponse> call, Response<sendFileinfoResponse> response) {
                sendFileinfoResponse result = response.body();
                finish();
            }

            @Override
            public void onFailure(Call<sendFileinfoResponse> call, Throwable t) {
                Log.e("회원가입 에러 발생", t.getMessage());
            }
        });
    }

    private void createTransferUtility() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                COGNITO_POOL_ID,
                Regions.AP_NORTHEAST_2 // 리전
        );
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3Client, getApplicationContext());
    }

    void upload(File file, final String objectKey) {
        TransferObserver uploadObserver = transferUtility.upload(
                BUCKET_NAME,
                objectKey,
                file
        );
        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, "onStateChanged: " + state);
                if (TransferState.COMPLETED.equals(state)) {
                    send_file_name= objectKey;
                    //progressDialogUpload.dismiss();
                    Toast.makeText(SendSelectActivity.this, "Uploaded Success", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

            @Override
            public void onError(int id, Exception ex) {
                progressDialogUpload.dismiss();
                Log.e(TAG, "onError: ", ex);
                Toast.makeText(SendSelectActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case PICK_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    imageUri = uri;
                    txt_file_name.setText(getFileNameFromUri(uri));
                    btn_select_next.setEnabled(true);
                }
        }
    }

    String getFileNameFromUri(Uri uri) {
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
        int nameIndex = 0;
        if (returnCursor != null) {
            nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = returnCursor.getString(nameIndex);
            returnCursor.close();
            return name;
        } else {
            return "";
        }
    }

    File createFileFromUri(Uri uri, String objectKey) throws IOException {
        InputStream is = getContentResolver().openInputStream(uri);
        File file = new File(getCacheDir(), objectKey);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buf = new byte[2046];
        int read = -1;
        while ((read = is.read(buf)) != -1) {
            fos.write(buf, 0, read);
        }
        fos.flush();
        fos.close();
        return file;
    }

//    private void uploadFile(String ImgURL, String ImgName) {
////
////        /**
////         * 현재 연결된 서버의 URL을 받아옴
////         */
////        String url = "http://ec2-13-209-157-83.ap-northeast-2.compute.amazonaws.com:3000";
////
////        /**
////         * 다시 연결 시도
////         */
////        // create upload service client
////        Retrofit retrofit = new Retrofit.Builder()
////                .baseUrl(url)
////                .build();
////
////        ServiceApi service = retrofit.create(ServiceApi.class);
////
////
////        /**
////         * 서버로 보낼 파일의 전체 url을 이용해 작업
////         */
////
////        File photo = new File(ImgURL);
////        RequestBody photoBody = RequestBody.create(MediaType.parse("image/jpg"), photo);
////
////        // MultipartBody.Part is used to send also the actual file name
////        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", photo.getName(), photoBody);
////
//////        Log.i("myTag","this file'name is "+ photo.getName());
////
////        /**
////         * 서버에 사진이외의 텍스트를 보낼 경우를 생각해서 일단 넣어둠
////         */
////        // add another part within the multipart request
////        String descriptionString = "android";
////
////        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
////
////
////        /**
////         * 사진 업로드하는 부분 // POST방식 이용
////         */
////        Call<ResponseBody> call = service.upload(body, description);
////        call.enqueue(new Callback<ResponseBody>() {
////            @Override
////            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
////
////                if(response.isSuccessful()){
////
////                    Gson gson = new Gson();
////                    try {
////                        String getResult = response.body().string();
////
////                        JsonParser parser = new JsonParser();
////                        JsonElement rootObejct = parser.parse(getResult);
////
//////                        Log.i("mytag",rootObejct.toString());
////
////                        UploadResult example = gson.fromJson(rootObejct, UploadResult.class);
////
////                        Log.i("mytag",example.url);
////
////                        String result = example.result;
////
////                        if(result.equals("success")){
////                            Toast.makeText(getApplicationContext(),"사진 업로드 성공!!!!",Toast.LENGTH_SHORT).show();
////                        }
////
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                        Log.i("MyTag", "error : "+e.getMessage());
////                    }
////
////
////                }else{
////                    Toast.makeText(getApplicationContext(),"사진 업로드 실패!!!!",Toast.LENGTH_SHORT).show();
////                }
////
////
////                // dismiss dialog
////            }
////
////            @Override
////            public void onFailure(Call<ResponseBody> call, Throwable t) {
////                Log.e("Upload error:", t.getMessage());
////
////            }
////
////
////
////        });
////    }

}

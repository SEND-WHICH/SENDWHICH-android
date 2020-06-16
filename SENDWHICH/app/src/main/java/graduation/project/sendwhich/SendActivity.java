package graduation.project.sendwhich;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

public class SendActivity extends AppCompatActivity {

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
    File tempSelectFile;

    private AmazonS3 amazonS3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        btn_file_delete = (Button) findViewById(R.id.btn_file_delete);
        txt_file_name = (TextView) findViewById(R.id.txt_file_name);
        btn_find_file = (Button) findViewById(R.id.btn_find_file);
        lay_add_file = (LinearLayout) findViewById(R.id.lay_add_file);
        img_add_file = (ImageView) findViewById(R.id.img_add_file);
        txt_add_file = (TextView) findViewById(R.id.txt_add_file);
        box_select_examine = (CheckBox) findViewById(R.id.box_select_examine);
        btn_select_next = (Button) findViewById(R.id.btn_select_next);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        btn_select_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUploadUtils.goSend(tempSelectFile);
                Intent i = new Intent(SendActivity.this, ExamineResultActivity.class);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 1 || resultCode != RESULT_OK) {
            return;
        }

        Uri dataUri = data.getData();
        imageUri = data.getData();
        txt_file_name.setText(getFileNameFromUri(imageUri));

        try {

            // 선택한 이미지 임시 저장
            String date = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(new Date());
            tempSelectFile = new File("/Pictures/Test/", txt_add_file+"temp_" + date + ".jpeg");
            OutputStream out = new FileOutputStream(tempSelectFile);
        } catch(IOException ioe) {
            ioe.printStackTrace();
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

}

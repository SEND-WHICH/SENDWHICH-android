package graduation.project.sendwhich;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;



public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;
    private AmazonS3 amazonS3;
    private static final int REQUEST_CODE = 0;
    private static final String COGNITO_POOL_ID = "ap-northeast-2:4d85cf6b-cba7-4e2c-be1b-4910ac6740c7"; // 자격 증명 풀 ID
    private static final String BUCKET_NAME = "sendwhich";
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private static final String TAG = MainActivity.class.getCanonicalName();

    ProgressDialog progressDialogUpload;
    ProgressDialog progressDialogDownload;
    Uri imageUri;

    TransferUtility transferUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);
        createTransferUtility();

        //Button temp_down = (Button) findViewById(R.id.btn_temp_down);

        Button btn_open = (Button)findViewById(R.id.btn_open);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        ImageView img_close = (ImageView)findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });

        TextView txt_login = (TextView) findViewById(R.id.txt_login);
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

        Button btn_send = (Button)findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SendActivity.class);
                startActivity(i);
            }
        });

        Button btn_recieve = (Button)findViewById(R.id.btn_recieve);
        btn_recieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,RecieveActivity.class);
                startActivity(i);
            }
        });

        drawerLayout.setDrawerListener(listener);
        drawerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

    }

    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View view, float v) {

        }

        @Override
        public void onDrawerOpened(@NonNull View view) {

        }

        @Override
        public void onDrawerClosed(@NonNull View view) {

        }

        @Override
        public void onDrawerStateChanged(int i) {

        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case PICK_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    imageUri = uri;
                    //txt_file_name.setText(getFileNameFromUri(uri));
                    //btn_select_next.setEnabled(true);
                }
        }
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

    void download(String objectKey) {
        final File fileDownload = new File(getCacheDir(), objectKey);

        TransferObserver transferObserver = transferUtility.download(
                BUCKET_NAME,
                objectKey,
                fileDownload
        );
        transferObserver.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, "onStateChanged: " + state);
                if (TransferState.COMPLETED.equals(state)) {
                    //imageViewDownload.setImageBitmap(BitmapFactory.decodeFile(fileDownload.getAbsolutePath()));
                    progressDialogDownload.dismiss();
                    //Toast.makeText(UploadActivity.this, "Image downloaded", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

            @Override
            public void onError(int id, Exception ex) {
                progressDialogDownload.dismiss();
                Log.e(TAG, "onError: ", ex);
                //Toast.makeText(UploadActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

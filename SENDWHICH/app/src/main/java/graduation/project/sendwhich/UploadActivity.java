package graduation.project.sendwhich;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatActivity;

public class UploadActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private static final String COGNITO_POOL_ID = "ap-northeast-2:4d85cf6b-cba7-4e2c-be1b-4910ac6740c7"; // 자격 증명 풀 ID
    private static final String BUCKET_NAME = "sendwhich";

    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private static final String TAG = MainActivity.class.getCanonicalName();

    EditText editTextUpload;
    EditText editTextDownload;
    ImageView imageViewUpload;
    ImageView imageViewDownload;
    Button buttonPickImage;
    Button buttonUpload;
    Button buttonDownload;
    Uri imageUri;
    ProgressDialog progressDialogUpload;
    ProgressDialog progressDialogDownload;

    TransferUtility transferUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        initUI();
        createTransferUtility();
    }

    private void initUI() {
        editTextUpload = (EditText) findViewById(R.id.edit_upload);
        editTextDownload = (EditText) findViewById(R.id.edit_download);

        imageViewUpload = (ImageView)findViewById(R.id.img_upload);
        imageViewDownload = (ImageView)findViewById(R.id.img_download);

        buttonPickImage = (Button) findViewById(R.id.btn_pick);
        buttonPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
            }
        });

        buttonUpload = (Button) findViewById(R.id.btn_upload);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    if (!TextUtils.isEmpty(editTextUpload.getText().toString())){
                        String objectKey = editTextUpload.getText().toString();
                        File file = null;
                        try {
                            file = createFileFromUri(imageUri, objectKey);
                            upload(file, objectKey);

                            progressDialogUpload = new ProgressDialog(UploadActivity.this);
                            progressDialogUpload.setMessage("Uploading file " + file.getName());
                            progressDialogUpload.setIndeterminate(true);
                            progressDialogUpload.setCancelable(false);
                            progressDialogUpload.show();

                        } catch (IOException e) {
                            Log.e(TAG, "onClick: ", e);
                            Toast.makeText(UploadActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UploadActivity.this, "Enter object key in EditText", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UploadActivity.this, "Pick any image to upload", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonDownload = (Button) findViewById(R.id.btn_download);
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editTextDownload.getText().toString())) {
                    String objectKey = editTextDownload.getText().toString();

                    progressDialogDownload = new ProgressDialog(UploadActivity.this);
                    progressDialogDownload.setMessage("Downloading object key " + objectKey);
                    progressDialogDownload.setIndeterminate(true);
                    progressDialogDownload.setCancelable(false);
                    progressDialogDownload.show();

                    download(objectKey);
                } else {
                    Toast.makeText(UploadActivity.this, "Enter object key in EditText", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case PICK_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    imageUri = uri;
                    imageViewUpload.setImageURI(uri);
                    editTextUpload.setText(getFileNameFromUri(uri));
                    buttonUpload.setEnabled(true);
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

    void upload(File file, final String objectKey) {
        TransferObserver transferObserver = transferUtility.upload(
                BUCKET_NAME,
                objectKey,
                file
        );
        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, "onStateChanged: " + state);
                if (TransferState.COMPLETED.equals(state)) {
                    editTextDownload.setText(objectKey);
                    progressDialogUpload.dismiss();
                    Toast.makeText(UploadActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

            @Override
            public void onError(int id, Exception ex) {
                progressDialogUpload.dismiss();
                Log.e(TAG, "onError: ", ex);
                Toast.makeText(UploadActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
                    imageViewDownload.setImageBitmap(BitmapFactory.decodeFile(fileDownload.getAbsolutePath()));
                    progressDialogDownload.dismiss();
                    Toast.makeText(UploadActivity.this, "Image downloaded", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

            @Override
            public void onError(int id, Exception ex) {
                progressDialogDownload.dismiss();
                Log.e(TAG, "onError: ", ex);
                Toast.makeText(UploadActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

package graduation.project.sendwhich;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SendToActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to);

        Button btn_sendto_pre = (Button)findViewById(R.id.btn_sendto_pre);
        Button btn_sendto_next = (Button)findViewById(R.id.btn_sendto_next);

        btn_sendto_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SendToActivity.this, SuccessActivity.class);
                startActivity(i);
            }
        });

        btn_sendto_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SendToActivity.this, ExamineResultActivity.class);
                startActivity(i);
            }
        });
    }
}

package graduation.project.sendwhich;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ExamineResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examine_result);

        Button btn_examine_next = (Button)findViewById(R.id.btn_examine_next);
        btn_examine_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ExamineResultActivity.this, SendToActivity.class);
                startActivity(i);
            }
        });

        Button btn_examin_pre = (Button)findViewById(R.id.btn_examin_pre);
        btn_examin_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ExamineResultActivity.this, SendSelectActivity.class);
                startActivity(i);
            }
        });
    }
}

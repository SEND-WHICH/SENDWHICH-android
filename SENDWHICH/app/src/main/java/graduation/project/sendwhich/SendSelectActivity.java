package graduation.project.sendwhich;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SendSelectActivity extends AppCompatActivity {

    private Button btn_find_file;
    private TextView txt_file_name;
    private Button btn_file_delete;
    private LinearLayout lay_add_file;
    private ImageView img_add_file;
    private TextView txt_add_file;
    private CheckBox box_select_examine;
    private Button btn_select_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_select);

        btn_file_delete = (Button)findViewById(R.id.btn_file_delete);
        txt_file_name = (TextView)findViewById(R.id.txt_file_name);
        btn_file_delete = (Button)findViewById(R.id.btn_file_delete);
        lay_add_file = (LinearLayout)findViewById(R.id.lay_add_file);
        img_add_file = (ImageView)findViewById(R.id.img_add_file);
        txt_add_file = (TextView)findViewById(R.id.txt_add_file);
        box_select_examine = (CheckBox)findViewById(R.id.box_select_examine);
        btn_select_next = (Button)findViewById(R.id.btn_select_next);

        btn_select_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SendSelectActivity.this, ExamineResultActivity.class);
                startActivity(i);
            }
        });

    }
}

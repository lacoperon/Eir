package tech.eir.eir;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import android.widget.TextView;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyMessage";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button callButton = (Button) findViewById(R.id.callButton);

        callButton.setOnClickListener(
                new Button.OnClickListener() {

                    public void onClick(View v) {
                        TextView myText = (TextView) findViewById(R.id.myText);
                        myText.setText("hi");

                    }
                }


        );


        callButton.setOnLongClickListener(
                new Button.OnLongClickListener() {

                    public boolean onLongClick(View v) {

                        Intent nextScreen = new Intent(getApplicationContext(), Call.class);
                        startActivity(nextScreen);
//                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(intent, 0);
                        return true;
                    }

                }

        );

    }


}

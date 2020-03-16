package me.singleneuron.originalmusicnotification_debugtool;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        textView.setText("");
        toHook();
    }

    public void toHook() {
        textView.setText("原生音乐通知未找到");
    }

    public void writeToTextview(String string) {
        textView.setText(textView.getText() + string + "\n");
    }

}

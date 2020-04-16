package demo.com.ledvediocontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DeviceTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_type);

        String[] ss = getResources().getStringArray(R.array.devcie_type);

        ListView listView = findViewById(android.R.id.list);
        listView.setAdapter(new ArrayAdapter<String>(DeviceTypeActivity.this,R.layout.list_menu_item,ss));
    }
}

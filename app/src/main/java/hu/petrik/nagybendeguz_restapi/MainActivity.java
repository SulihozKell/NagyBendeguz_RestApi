package hu.petrik.nagybendeguz_restapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import hu.petrik.nagybendeguz_restapi.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnList.setOnClickListener(view -> {
            Intent toListView = new Intent(MainActivity.this, ListResultActivity.class);
            startActivity(toListView);
            finish();
        });
        binding.btnNew.setOnClickListener(view -> {
            Intent toNewView = new Intent(MainActivity.this, InsertActivity.class);
            startActivity(toNewView);
            finish();
        });
    }
}
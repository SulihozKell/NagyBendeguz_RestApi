package hu.petrik.nagybendeguz_restapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import hu.petrik.nagybendeguz_restapi.databinding.ActivityListResultBinding;
import hu.petrik.nagybendeguz_restapi.databinding.CityListItemBinding;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListResultActivity extends AppCompatActivity {
    ActivityListResultBinding binding;
    List<City> cities = new ArrayList<>();
    private final String url = "https://retoolapi.dev/c0bLcR/data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(view -> {
            Intent toMainView = new Intent(ListResultActivity.this, MainActivity.class);
            startActivity(toMainView);
            finish();
        });
        CityTask task = new CityTask(url);
        task.execute();
    }

    private class CityAdapter extends ArrayAdapter<City> {
        public CityAdapter() {
            super(ListResultActivity.this, R.layout.city_list_item, cities);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            CityListItemBinding listItemBinding = CityListItemBinding.inflate(getLayoutInflater());
            City actual = cities.get(position);
            String name = actual.getNev();
            String country = actual.getOrszag();
            if (name.length() > 10) {
                name = actual.getNev().substring(0, 10) + "...";
            }
            if (country.length() > 10) {
                country = actual.getOrszag().substring(0, 10) + "...";
            }
            listItemBinding.name.setText(name);
            listItemBinding.country.setText(country);
            listItemBinding.population.setText(String.valueOf(actual.getLakossag()));
            return listItemBinding.getRoot().getRootView();
        }
    }

    private class CityTask extends AsyncTask<Void, Void, Response> {
        String requestUrl;

        public CityTask(String requestUrl) {
            this.requestUrl = requestUrl;
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                response = RequestHandler.get(requestUrl);
            }
            catch (IOException e) {
                runOnUiThread(() ->
                    Toast.makeText(ListResultActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson converter = new Gson();
            if (response.getResponseCode() >= 400) {
                Toast.makeText(ListResultActivity.this, "Hiba történt a kérés feldolgozása során!", Toast.LENGTH_SHORT).show();
                Log.d("onPostExecuteError: ", response.getContent());
            }
            City[] citiesArray = converter.fromJson(response.getContent(), City[].class);
            cities.clear();
            cities.addAll(Arrays.asList(citiesArray));
            binding.listViewCities.setAdapter(new CityAdapter());
        }
    }
}
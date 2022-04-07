package hu.petrik.nagybendeguz_restapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.petrik.nagybendeguz_restapi.databinding.ActivityInsertBinding;

public class InsertActivity extends AppCompatActivity {
    ActivityInsertBinding binding;
    List<City> cities = new ArrayList<>();
    private final String url = "https://retoolapi.dev/c0bLcR/data";
    boolean nameOk, countryOk, populationOK = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        disableButton();
        CityTask task = new CityTask(url, "GET", null);
        task.execute();
        binding.btnBack.setOnClickListener(view -> {
            Intent toMainView = new Intent(InsertActivity.this, MainActivity.class);
            startActivity(toMainView);
            finish();
        });
        binding.btnInsert.setOnClickListener(view -> {
            insert();
        });
        binding.editTextName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String nameInput = binding.editTextName.getText().toString().trim();
                for (int i = 0; i < cities.size(); i++) {
                    if (cities.get(i).getNev().equals(nameInput)) {
                        binding.editTextName.setTextColor(Color.RED);
                        nameOk = false;
                        disableButton();
                        break;
                    }
                    else {
                        binding.editTextName.setTextColor(Color.GREEN);
                    }
                }
                if (binding.editTextName.getCurrentTextColor() == Color.GREEN) {
                    nameOk = true;
                    disableButton();
                }
            }
            else {
                binding.editTextName.setTextColor(Color.BLACK);
            }
        });
        binding.editTextCountry.setOnFocusChangeListener((v, hasFocus) -> {
            String country = binding.editTextCountry.getText().toString().trim();
            if (country.isEmpty()) {
                countryOk = false;
                Toast.makeText(this, "Ország megadása kötelező", Toast.LENGTH_SHORT).show();
            }
            else {
                countryOk = true;
            }
            disableButton();
        });
        binding.editTextPopulation.setOnFocusChangeListener((v, hasFocus) -> {
            String population = binding.editTextPopulation.getText().toString().trim();
            if (population.isEmpty()) {
                populationOK = false;
                Toast.makeText(this, "Lakosság megadása kötelező", Toast.LENGTH_SHORT).show();
            }
            else {
                populationOK = true;
            }
            disableButton();
        });
    }

    private void insert() {
        String name = binding.editTextName.getText().toString().trim();
        String country = binding.editTextCountry.getText().toString().trim();
        String populationText = binding.editTextPopulation.getText().toString().trim();
        if (!validate(name, country, populationText)) {
            return;
        }
        disableButton();
        int population = Integer.parseInt(populationText);
        City city = new City(0, name, country, population);
        Gson jsonConvert = new Gson();
        CityTask task = new CityTask(url, "POST", jsonConvert.toJson(city));
        task.execute();
    }

    private boolean validate(String name, String country, String populationText){
        if (name.isEmpty()) {
            Toast.makeText(this, "Név megadása kötelező!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (country.isEmpty()) {
            Toast.makeText(this, "Ország megadása kötelező!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (populationText.isEmpty()) {
            Toast.makeText(this, "Lakosság megadása kötelező!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private class CityTask extends AsyncTask<Void, Void, Response> {
        String requestUrl;
        String requestType;
        String requestParams;

        public CityTask(String requestUrl, String requestType, String requestParams) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                if (requestType.equals("GET")) {
                    response = RequestHandler.get(requestUrl);
                }
                else {
                    response = RequestHandler.post(requestUrl, requestParams);
                }
            }
            catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(InsertActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson converter = new Gson();
            if (requestType.equals("GET")) {
                if (response.getResponseCode() >= 400) {
                    Toast.makeText(InsertActivity.this, "Hiba történt a kérés feldolgozása során!", Toast.LENGTH_SHORT).show();
                    Log.d("onPostExecuteError: ", response.getContent());
                }
                City[] citiesArray = converter.fromJson(response.getContent(), City[].class);
                cities.clear();
                cities.addAll(Arrays.asList(citiesArray));
            }
            else {
                if (response.getResponseCode() >= 400) {
                    Toast.makeText(InsertActivity.this, "Sikertelen felvétel!", Toast.LENGTH_SHORT).show();
                    Log.d("onPostExecuteError: ", response.getContent());
                }
                City city = converter.fromJson(response.getContent(), City.class);
                cities.add(0, city);
                clearInput();
                Toast.makeText(InsertActivity.this, "Sikeres felvétel!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void clearInput() {
        binding.editTextName.setText("");
        binding.editTextCountry.setText("");
        binding.editTextPopulation.setText("");
    }

    private void disableButton() {
        binding.btnInsert.setEnabled(nameOk && countryOk && populationOK);
    }
}
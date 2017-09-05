package com.wallstreetstocks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Abhz on 7/14/2017.
 */

public class SectorFragment extends Fragment {
    private static final String TAG = "SectorFragment";

    private Button refreshBtn;
    private ListView listView;
    public ProgressDialog pDialog;
    ArrayList<String> sectors;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_sector, container, false);
        sectors = new ArrayList<String>();

        refreshBtn = (Button) view.findViewById(R.id.refreshButton);
        listView = (ListView) view.findViewById(R.id.sectorsList);
        refreshBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                GetSectorApi gsa = new GetSectorApi();
                gsa.execute();
            }
        });

        return view;
    }   // END onCreateView()


    // NESTED CLASSES
    public class GetSectorApi extends  AsyncTask<Void, Void, String> {

        private String url = "https://www.alphavantage.co/query?function=SECTOR&apikey=" + Config.API_KEY;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Retrieving Sector Data");
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
        }   // END onPreExecute()

        protected String doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject sectorsObj = jsonObj.getJSONObject("Rank A: Real-Time Performance");
                    for(int i = 0; i<sectorsObj.names().length(); i++) {
                        String result = sectorsObj.names().getString(i);
                        result += " : " + sectorsObj.get(sectorsObj.names().getString(i));
                        sectors.add(result);
                    }
                    return "New data received";
                }
                catch (Exception ex){
                    return "There was a problem parsing the data";
                }
            }
            else {
                return "Could not retrieve data";
            }
        }   // END doInBackground()

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (result.equals("New data received")){
                ArrayAdapter<String> adapter =  new ArrayAdapter<String>(getContext(), R.layout.list_item, R.id.sectorData, sectors);
                listView.setAdapter(adapter);
            }
            else if (result.equals("Could not retrieve data")) {
                Toast.makeText(getActivity(), "No Internet: Could not retrieve data", Toast.LENGTH_SHORT).show();
            }
            else if (result.equals("There was a problem parsing the data")) {
                Toast.makeText(getActivity(), "Problem with received data", Toast.LENGTH_SHORT).show();
            }
        }   // END onPostExecute()

    }   // END GetSectorApi{}

}   // END SectorFragment{}

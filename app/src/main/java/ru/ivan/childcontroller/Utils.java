package ru.ivan.childcontroller;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Utils {
    final static String KEY_LOCATION_UPDATES_REQUESTED = "location-updates-requested";
    final static String KEY_LOCATION_UPDATES_RESULT = "location-update-result";
    final static String CHANNEL_ID = "channel_01";
    private static String URL = "https://2e35cd1e.ngrok.io/api";

    static void setRequestingLocationUpdates(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_LOCATION_UPDATES_REQUESTED, value)
                .apply();



    }

    static boolean getRequestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_LOCATION_UPDATES_REQUESTED, false);
    }


    static void sendNotification(Context context, String notificationDetails) {
       /* Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("from_notification", true);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle("Location update")
                .setContentText(notificationDetails)
                .setContentIntent(notificationPendingIntent);
        builder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
            builder.setChannelId(CHANNEL_ID);
        }
        mNotificationManager.notify(0, builder.build());*/
    }


    static String getLocationResultTitle(Context context, List<Location> locations) {
        String numLocationsReported = context.getResources().getQuantityString(
                R.plurals.num_locations_reported, locations.size(), locations.size());
        return numLocationsReported + ": " + DateFormat.getDateTimeInstance().format(new Date());
    }


    private static String getLocationResultText(Context context, List<Location> locations) {
        if (locations.isEmpty()) {
            return context.getString(R.string.unknown_location);
        }
        StringBuilder sb = new StringBuilder();
        for (Location location : locations) {
            sb.append("(");
            sb.append(location.getLatitude());
            sb.append(", ");
            sb.append(location.getLongitude());
            sb.append(")");
            sb.append("\n");
        }
        return sb.toString();
    }

    static void setLocationUpdatesResult(Context context, List<Location> locations) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_LOCATION_UPDATES_RESULT, getLocationResultTitle(context, locations)
                        + "\n" + getLocationResultText(context, locations))
                .apply();
    }

    static String getLocationUpdatesResult(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_LOCATION_UPDATES_RESULT, "");
    }


    static void sendLocation(List<Location> locations) {
        String url = URL + "/sendCoordinates";
        ArrayList<JSONObject> location = new ArrayList<>();
        for (Location local : locations) {
            JSONObject o = new JSONObject();
            try {
                o.put("latitude", local.getLatitude());
                o.put("longitude", local.getLongitude());

                location.add(o);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        new CallAPI().execute(url, location.toString());
    }

    static void sendCallInfo(String phoneNumber, String phoneState) {
        if(phoneNumber == null || phoneNumber.equals("")) return;
        Log.d("incomingNumber", "phoneNumber " + phoneNumber);
        Log.d("incomingNumber", "phoneState " + phoneState);
        String url = URL + "/sendCallInfo";
        JSONObject o = new JSONObject();
        try {
            o.put("phone_number", phoneNumber);
            o.put("phone_state", phoneState);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new CallAPI().execute(url, o.toString());
    }

    public static class CallAPI extends AsyncTask<String, String, String> {

        public CallAPI() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]; // URL to call
            String data = params[1]; //data to post
            OutputStream out = null;

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");

                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                out = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));


                String request = "{\"data\":" + data + "}";
                writer.write(request);
                writer.flush();
                writer.close();
                out.close();
                urlConnection.connect();
                Log.d("locationCocation", "data:" + urlConnection.getResponseCode());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                Log.d("locationCocation", "error " + e.toString());

            }

            return null;
        }

    }
}


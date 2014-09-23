package de.uniheidelberg.geog.navigationmobileclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class RouteMapFragment extends SupportMapFragment {
	public static final String EXTRA_START_LOCATION = "de.uniheidelberg.geog.navigationmobileclient.RouteSelectionFragment.StartLocation";
	public static final String EXTRA_TARGET_LOCATION = "de.uniheidelberg.geog.navigationmobileclient.RouteSelectionFragment.TargetLocation";
	
	private WaypointLocation mStartLoc;
	private WaypointLocation mTargetLoc;
	
	private Route mRoute;
	
	private GoogleMap mGoogleMap;
	
	
	
	public static RouteMapFragment newInstance() {
		RouteMapFragment rf = new RouteMapFragment();
		return rf;
		
		/*Intent i =  getIntent();
		if(i.hasExtra(EXTRA_START_LOCATION))
			mStartLoc = (WaypointLocation) i.getExtras().get(EXTRA_START_LOCATION);
		else
			mStartLoc = null;
		if(i.hasExtra(EXTRA_TARGET_LOCATION))
			mTargetLoc = (WaypointLocation) i.getExtras().get(EXTRA_TARGET_LOCATION);
		else
			mTargetLoc = null;
		
		setRetainInstance(true);
		
		new GetRouteTask().execute();*/
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, parent, savedInstanceState);
		
		// Stash references
		mGoogleMap = getMap();
		// show location
		mGoogleMap.setMyLocationEnabled(true);
		
		return v;
	}
	
	private void updateDisplay() {
		
	}
	
	private class GetRouteTask extends AsyncTask<Void, Void, Route> {
		@Override
		protected Route doInBackground(Void... params) {
			Route rt = new Route();
			if(mTargetLoc != null && mStartLoc != null) {
				try {
					//http://wheelmap.org/api/categories?api_key=EPR2UP3TmegX7zHkhyZW&locale=en&page=2&per_page=5
					OrsRouteGetter getter = new OrsRouteGetter();
					String url = getString(R.string.ors_url);			
					
					String apiUrl = url + "/CreateRoute";
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(apiUrl);				
					
					
					JSONObject json = new JSONObject();
					JSONObject routeRequest = new JSONObject();
					routeRequest.put("distanceUnit", "KM");
					JSONObject routePlan = new JSONObject();
					routePlan.put("RoutePreference", "Pedestrian");
					JSONObject waypointList = new JSONObject();
					JSONObject startPos = new JSONObject();
					startPos.put("srsName", "EPSG:4326");
					startPos.put("pos", mStartLoc.getLat() + " " + mStartLoc.getLong());
					JSONObject endPos = new JSONObject();
					endPos.put("srsName", "EPSG:4326");
					endPos.put("pos", mTargetLoc.getLat() + " " + mTargetLoc.getLong());
					waypointList.put("StartPosition", startPos);
					waypointList.put("EndPosition", endPos);
					routePlan.put("WayPointList", waypointList);
					routeRequest.put("RoutePlan", routePlan);
					json.put("DetermineRouteRequest", routeRequest);
					
					httpPost.setHeader("content-type", "application/json; charset=utf8");
					httpPost.setHeader("accept", "application/json");
					
					//httpPost.setHeader("Content-Length", json.toString());
					
					Log.d("url", url);
					Log.d("json in", json.toString());
					
					StringEntity se = new StringEntity(json.toString());
					httpPost.setEntity(se);
					HttpResponse response = httpClient.execute(httpPost);
					
					InputStream iStream = response.getEntity().getContent();
					
					String result = "";
					if(iStream != null) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
						String line = "";
						while((line = reader.readLine()) != null) {
							result += line;
						}
						iStream.close();
					}
					
					Log.d("response", "data" + result);
					rt = getter.parseItem(new JSONObject(result));
					
				} catch (IOException ioe) {
					Log.e("url", "Failed to fetch URL: ", ioe);
				} catch (JSONException jsone) {
					Log.e("json", "Error with json: ", jsone);
				}
			}
			return rt;
		}
		
		@Override
		protected void onPostExecute(Route route) {
			mRoute = route;
			Log.d("SelectionPost", "Route: " + route.getId());
			updateDisplay();
		}
	}
}

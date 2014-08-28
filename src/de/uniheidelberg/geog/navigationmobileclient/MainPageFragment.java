package de.uniheidelberg.geog.navigationmobileclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainPageFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main_page, parent, false);
		
		Button newNavigation = (Button) v.findViewById(R.id.fragment_main_new_button);
		newNavigation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), PersonaActivity.class);//RouteSettingsActivity.class);
				startActivity(i);
			}
		});
		return v;
	}
}

package com.example.tonino.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tonino.login.Types.Route;

/**
 * Created by Danny on 17/06/2015.
 */
public class SelectRouteFragment extends Fragment implements
        ServiceInquirer.OnResponseReceivedCallback {

    MainActivity_operator mainActivity;
    ViewGroup routesContainer;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity_operator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_select_route_operator, container, false);
        routesContainer = (ViewGroup) rootView.findViewById(R.id.routes_container);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity.routeMap == null) {
            mainActivity.initializeMaps(this, false);
            // will resume execution in onResponseReceived, and mappings will be ready
        }
        else {
            // mappings are already ready, calling onResponseReceived with dummy parameters, that
            // are not read anyway
            onResponseReceived(null, 0, null);
        }
    }

    @Override
    public void onResponseReceived(String originalUri, int statusCode, String response) {
        // mappings are already set if this function was called
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        for(int routeId : mainActivity.routeMap.keySet()) {
            Route route = mainActivity.routeMap.get(routeId);
            ViewGroup routeView = (ViewGroup) inflater
                    .inflate(R.layout.template_route_select_operator, routesContainer, false);
            route.fillView(inflater, routeView);
            routeView.findViewById(R.id.select_btn)
                    .setOnClickListener(new RouteSelector(mainActivity, routeId));
            routesContainer.addView(routeView);
        }
        mainActivity.dismissLoadingDialog();
    }

    public static class RouteSelector implements View.OnClickListener {
        protected MainActivity_operator mainActivity;
        protected int routeId;

        public RouteSelector(MainActivity_operator mainActivity, int routeId) {
            this.mainActivity = mainActivity;
            this.routeId = routeId;
        }

        @Override
        public void onClick(View v) {
            mainActivity.loadPrizeFragment(routeId);
        }
    }

}

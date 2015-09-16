package com.example.tonino.login.Dialogs;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tonino.login.R;
import com.example.tonino.login.Types.Prize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Danny on 19/06/2015.
 */
public class ScrollViewDialog extends ConfirmCancelDialog implements View.OnClickListener {

    public static final String ARG_PRIZES = "prizes";
    public static final String ARG_OUT_SELECTED_ID = "selected_id";

    protected Map<View, Integer> idMap;
    protected ViewGroup viewContainer;
    protected int selectedId;

    public ScrollViewDialog() {
        bodyLayout = R.layout.dialog_scroll_view_operator;
        setNoConfirmButton();
    }

    @Override
    protected void onContinueCreateDialog(LayoutInflater inflater,
                                          ViewGroup dialogView, Bundle savedInstanceState) {
        super.onContinueCreateDialog(inflater, dialogView, savedInstanceState);
        viewContainer = (ViewGroup) dialogView.findViewById(R.id.container);
        Bundle args = getArguments();
        if (args != null) {
            ArrayList<Parcelable> prizesArray = args.getParcelableArrayList(ARG_PRIZES);
            idMap = new HashMap<>();
            for (Parcelable parcelable : prizesArray) {
                Prize prize = (Prize) parcelable;
                ViewGroup prizeView = (ViewGroup) inflater
                        .inflate(R.layout.template_prize_select_operator, viewContainer, false);
                prize.fillView(prizeView, Prize.NO_PROBABILITY);
                View selectBtn = prizeView.findViewById(R.id.select_btn);
                selectBtn.setOnClickListener(this);
                idMap.put(selectBtn, prize.id);
                viewContainer.addView(prizeView);
            }
        }
    }

    @Override
    protected void onConfirmFillUserInputBundle(Bundle userInput) {
        userInput.putInt(ARG_OUT_SELECTED_ID, selectedId);
    }

    @Override
    public void onClick(View v) {
        selectedId = idMap.get(v);
        dismiss();
        super.onConfirmClick();
    }

    public static class Builder extends ConfirmCancelDialog.Builder {

        public Builder(FragmentActivity activity) {
            super(activity);
        }

        @Override
        protected ConfirmCancelDialog createDialog() {
            return new ScrollViewDialog();
        }

        public Builder setParams(Map<Integer, Prize> prizeMap) {
            Bundle args = new Bundle();
            ArrayList<Parcelable> prizesArray = new ArrayList<>(prizeMap.size());
            for(int prizeId : prizeMap.keySet()) {
                prizesArray.add(prizeMap.get(prizeId));
            }
            args.putParcelableArrayList(ARG_PRIZES, prizesArray);
            setArguments(args);
            return this;
        }

    }

}

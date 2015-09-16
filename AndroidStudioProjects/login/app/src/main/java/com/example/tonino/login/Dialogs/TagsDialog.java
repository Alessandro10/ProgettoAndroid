package com.example.tonino.login.Dialogs;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tonino.login.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by Danny on 14/06/2015.
 */
public class TagsDialog extends ConfirmCancelDialog implements View.OnClickListener {

    public static final String STATE_SELECTED_TAGS = "selected_tags";

    public static final String ARG_ALL_TAGS = "all_tags";
    public static final String ARG_ROUTE_TAGS = "route_tags";
    public static final String ARG_SELECTED_TAGS = "selected_tags";

    protected Set<String> selectedTags;

    public TagsDialog() {
        bodyLayout = R.layout.dialog_add_tags_operator;
        selectedTags = new HashSet<>();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(STATE_SELECTED_TAGS, new ArrayList<String>(selectedTags));
    }

    @Override
    protected void onContinueCreateDialog(LayoutInflater inflater,
                                          ViewGroup dialogView, Bundle savedInstanceState) {
        super.onContinueCreateDialog(inflater, dialogView, savedInstanceState);
        Set<String> allTags = new HashSet<>();
        Bundle args = getArguments();
        if (args != null) {
            allTags.addAll(args.getStringArrayList(ARG_ALL_TAGS));
            allTags.removeAll(args.getStringArrayList(ARG_ROUTE_TAGS));
        }
        if (savedInstanceState != null) {
            selectedTags.addAll(savedInstanceState.getStringArrayList(STATE_SELECTED_TAGS));
        }
        ViewGroup tagsContainer = (ViewGroup) dialogView.findViewById(R.id.tags_container);
        for(String tag : allTags) {
            TextView tagView = (TextView) inflater
                    .inflate(R.layout.template_tag_to_add_operator, tagsContainer, false);
            tagView.setText(tag);
            if (selectedTags.contains(tag)) {
                tagView.setBackgroundResource(R.drawable.button_green);
            }
            tagView.setOnClickListener(this);
            tagsContainer.addView(tagView);
        }
    }

    @Override
    public void onClick(View v) {
        String tag = ((TextView) v).getText().toString();
        if (!selectedTags.contains(tag)) {
            selectedTags.add(tag);
            v.setBackgroundResource(R.drawable.button_green);
        }
        else {
            selectedTags.remove(tag);
            v.setBackgroundResource(R.drawable.bordo);
        }
    }

    @Override
    protected void onConfirmFillUserInputBundle(Bundle userInput) {
        userInput.putStringArrayList(ARG_SELECTED_TAGS, new ArrayList<String>(selectedTags));
    }

    public static class Builder extends ConfirmCancelDialog.Builder {

        public Builder(FragmentActivity activity) {
            super(activity);
        }

        @Override
        protected ConfirmCancelDialog createDialog() {
            return new TagsDialog();
        }

        public Builder setParams(Set<String> allTags, Set<String> routeTags) {
            Bundle args = new Bundle();
            args.putStringArrayList(ARG_ALL_TAGS, new ArrayList<>(allTags));
            args.putStringArrayList(ARG_ROUTE_TAGS, new ArrayList<>(routeTags));
            setArguments(args);
            return this;
        }

    }

}


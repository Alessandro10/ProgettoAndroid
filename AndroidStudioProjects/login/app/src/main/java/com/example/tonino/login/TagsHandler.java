package com.example.tonino.login;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tonino.login.Dialogs.ConfirmCancelDialog;
import com.example.tonino.login.Dialogs.TagsDialog;

import java.util.Set;


/**
 * Created by Danny on 21/06/2015.
 */
public class TagsHandler implements View.OnClickListener {

    public static final String HANDLER_DIALOG_TAGS = "handler_dialog_tags";

    protected ViewGroup tagsContainer;
    public Set<String> selectedTags;

    public TagsHandler(ViewGroup tagsContainer, Set<String> selectedTags) {
        this.tagsContainer = tagsContainer;
        this.selectedTags = selectedTags;
    }

    @Override
    public void onClick(View v) {
        deleteTag((ViewGroup) v.getParent());
    }

    protected String deleteTag(ViewGroup tagView) {
        String tag = ((TextView) tagView.findViewById(R.id.tag_name)).getText().toString();
        tagsContainer.removeView(tagView);
        selectedTags.remove(tag);
        return tag;
    }

    public void hideDeleteTagBtns() {
        // hide delete tag btns if they didn't change, otherwise tags were re-inflated and
        // the delete btns are already hidden
        for (int i = 0; i < tagsContainer.getChildCount(); i++) {
            tagsContainer.getChildAt(i).findViewById(R.id.tag_delete_btn)
                    .setVisibility(View.GONE);
        }
    }

    public void showAndHandleDeleteTagBtnTouch() {
        for (int i = 0; i < tagsContainer.getChildCount(); i++) {
            View deleteTagBtn = tagsContainer.getChildAt(i).findViewById(R.id.tag_delete_btn);
            deleteTagBtn.setVisibility(View.VISIBLE);
            deleteTagBtn.setOnClickListener(this);
        }
    }

    public TagsDialog.Builder createTagsDialog(FragmentActivity activity, Set<String> allTags) {
        Bundle customParams = new Bundle();
        customParams.putBoolean(HANDLER_DIALOG_TAGS, true);
        ConfirmCancelDialog.Builder builder = new TagsDialog.Builder(activity)
                .setParams(allTags, selectedTags)
                .setTitle(R.string.dialog_title_tags)
                .setCustomParams(customParams);
        return (TagsDialog.Builder) builder;
    }

    /**
     * Fills the tagsContainer with the tags, inflating the template_tag.xml
     *
     * @param inflater the inflater to create the new tag views
     */
    public void writeTags(LayoutInflater inflater) {
        tagsContainer.removeAllViews();
        for(String tag : selectedTags) {
            View tagView = inflater.inflate(R.layout.template_tag_operator, tagsContainer, false);
            ((TextView) tagView.findViewById(R.id.tag_name)).setText(tag);
            tagsContainer.addView(tagView);
        }
        showAndHandleDeleteTagBtnTouch();
    }

}

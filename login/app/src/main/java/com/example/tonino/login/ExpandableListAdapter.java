package com.example.tonino.login;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private int colore = 0;

    private Dictionary<String , List<Integer>> dictIdPercorsi = null;

    public ExpandableListAdapter(Context context, Dictionary<String , List<Integer>> dictIdPercorsi2 , List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData ,
                                 int color) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        colore = color;
        dictIdPercorsi = dictIdPercorsi2;
    }


    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        int i = 0;
        List<Integer> list_id = dictIdPercorsi.get(this._listDataHeader.get(groupPosition));
        Iterator iterator = list_id.iterator();
        while (iterator.hasNext())
        {
            int idP = (int)iterator.next();
            if(i == childPosition)
            {
                return idP;
            }
            i++;
        }
        return -1;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }



    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            switch(colore) {
                case 0:
                    convertView = infalInflater.inflate(R.layout.list_group, null);
                    break;
                case 1:
                    convertView = infalInflater.inflate(R.layout.list_group_red, null);
                    break;
                case 2:
                    convertView = infalInflater.inflate(R.layout.list_group_green, null);
                    break;
                default:
                    convertView = infalInflater.inflate(R.layout.list_group, null);
                    break;
            }
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

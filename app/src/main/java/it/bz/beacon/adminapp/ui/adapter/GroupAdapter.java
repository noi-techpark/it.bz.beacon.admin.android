package it.bz.beacon.adminapp.ui.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.Group;

public class GroupAdapter extends ArrayAdapter<String> implements Filterable {

    private List<Group> groupList;
    private Filter mFilter;
    private ArrayList<String> _items = new ArrayList<String>();
    private ArrayList<String> orig = new ArrayList<String>();
    private final Object mLock = new Object();

    public GroupAdapter(Context context) {
        super(context, R.layout.simple_dropdown_item_1line);
        setGroups(null);
    }

    private String getDefaultGroup() {
        return getContext().getString(R.string.default_group);
    }

    public void setGroups(List<Group> groups) {
        this.groupList = groups;
        ArrayList groupString = new ArrayList();

        groupString.add(getDefaultGroup());
        if (groups != null) {
            for (int i = 0; i < groups.size(); i++) {
                groupString.add(groups.get(i).getName());
            }
        }
        orig = new ArrayList<>(groupString);
        notifyDataSetChanged();
    }

    public String getNameById(Long groupId) {
        if (groupId != null && groupList != null) {
            return groupList.stream().filter(group -> group.getId() == groupId).findAny().map(Group::getName).orElse(getDefaultGroup());
        }
        return getDefaultGroup();
    }

    @Override
    public int getCount() {
        if (_items != null)
            return _items.size();
        else
            return 0;
    }

    @Override
    public String getItem(int arg0) {
        return _items.get(arg0);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults oReturn = new FilterResults();

                String temp;
                if (constraint != null) {

                    _items.clear();
                    if (orig != null && orig.size() > 0) {
                        for (int i = 0; i < orig.size(); i++) {
                            temp = orig.get(i).toUpperCase();
                            if (temp.startsWith(constraint.toString().toUpperCase())) {
                                _items.add(orig.get(i));
                            }
                        }
                    }
                    _items.addAll(orig.stream().filter(s -> _items.stream().noneMatch(s1 -> s.equals(s1))).collect(Collectors.toList()));
                    oReturn.values = _items;
                    oReturn.count = _items.size();
                } else {
                    oReturn.values = orig;
                    oReturn.count = orig.size();
                }
                return oReturn;
            }


            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }

            }

        };

        return filter;

    }

    public boolean contains(String groupName) {
        return orig.stream().anyMatch(s -> s.equals(groupName));
    }

    public Long getGroupId(String groupName) {
        if(groupName != null && groupList != null) {
            return groupList.stream().filter(group -> group.getName().equals(groupName)).findFirst().map(group -> group.getId()).orElse(null);
        }
        return null;
    }
}

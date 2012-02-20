package com.social.lazylist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeMap;

import com.social.bubbles.Bubble;
import com.social.bubbles.BubbleLauncher;
import com.social.bubbles.BubbleManager;
import com.social.bubbles.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter implements Filterable{
    
    private Activity activity;
    private LinkedList<Bubble> data = new LinkedList<Bubble>();
    private LinkedList<Bubble> filtered = new LinkedList<Bubble>();

    private static LayoutInflater inflater=null;
    
    public LazyAdapter(Activity act) {
        activity = act;
        data = new LinkedList<Bubble>();
        for(Bubble b : BubbleManager.getSortedFreshList()){
        	data.add(b);
        	filtered.add(b);
        	 Log.d("Description: ", " "+b.getDescription());
      		  Log.d("		Last Activity: ", " "+b.getLastActivity().toString());
      		  Log.d("											Rank: ", " "+Long.toString(b.rank));
        }
        Log.d("LazyAdapter", "data obtained Fresh List from BM with size: " +data.size());
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
    }

    public int getCount() {
        return data.size();
    }

    public Bubble getItem(int position) {
    	if(position >= 0 && position < data.size())
    		return data.get(position);
    	return null;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public static class ViewHolder{
        public TextView text;
        public TextView location;
        public TextView addr;

        public ImageView image;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolder holder;
        if(convertView==null){
            vi = inflater.inflate(R.layout.item, null);
            holder=new ViewHolder();
            holder.text=(TextView)vi.findViewById(R.id.text);
            holder.location=(TextView)vi.findViewById(R.id.location);
            holder.addr=(TextView)vi.findViewById(R.id.addr);
            holder.image=(ImageView)vi.findViewById(R.id.image);
            vi.setTag(holder);
        }
        else
            holder=(ViewHolder)vi.getTag();
        
        if(data.get(position).getDescription() == null){
        	Log.d("WTF description is null ID=", data.get(position).getId());
        }
        int upper = Math.min(40, data.get(position).getDescription().length());
        
        holder.text.setText(data.get(position).getDescription().substring(0, upper)+(data.get(position).getDescription().length()>40?"...":""));
        holder.location.setText(data.get(position).getType());
        holder.addr.setText(data.get(position).getTimeAgo() + "      " + (data.get(position).getDistance()>0?data.get(position).getDistance():"")+" meters");

        holder.image.setTag(data.get(position).getDescription());
        //imageLoader.DisplayImage(data.get(position).picUrl, activity, holder.image);
        holder.image.setImageBitmap(BubbleLauncher.getImage(data.get(position).getId(), data.get(position).getType()));
        
        return vi;
    }

    Filter filter;
    @Override
    public Filter getFilter()
    {
        if(filter == null)
            filter = new ItemFilter();
        return filter;
    }
    private class ItemFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // NOTE: this function is *always* called from a background thread, and
            // not the UI thread.
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                LinkedList<Bubble> filt = new LinkedList<Bubble>();
                LinkedList<Bubble> lItems = new LinkedList<Bubble>();
                synchronized (this)
                {
                    lItems.addAll(data);
                }
                for(int i = 0, l = lItems.size(); i < l; i++)
                {
                	Bubble m = lItems.get(i);
                    if(m.getDescription().toLowerCase().contains(constraint))
                        filt.add(m);
                    else if(m.getLastActivity().toString().toLowerCase().contains(constraint))
                        filt.add(m);
                    else if(m.getType().toString().toLowerCase().contains(constraint))
                        filt.add(m);
                }
                result.count = filt.size();
                result.values = filt;
            }
            else
            {
                synchronized(this)
                {
                    result.values = data;
                    result.count = data.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
            filtered = (LinkedList<Bubble>)results.values;
            notifyDataSetChanged();
            data.clear();
            for(int i = 0, l = filtered.size(); i < l; i++)
            	data.add(filtered.get(i));
            notifyDataSetInvalidated();
        }

    }
}
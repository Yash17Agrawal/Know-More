package yash.googlehackathon.Aditya;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import yash.googlehackathon.R;

public class article_fragment extends Fragment{

    PrefManager prefManager;
    TextView tv;
    public article_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.article_fragment_layout, container, false);
        prefManager=new PrefManager(getContext());
        String var=prefManager.get_matter();
        tv=(TextView)rootView.findViewById(R.id.article);
        tv.setText(var);
        return rootView;
    }

}
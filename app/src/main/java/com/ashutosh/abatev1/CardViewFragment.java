package com.ashutosh.abatev1;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardViewFragment extends Fragment {

    View rootView;

    public CardViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_card_view , container, false);
        Context ctx = getActivity();

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_cardViewFragment);
        LinearLayoutManager llm = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(llm);         // adapter will be set in the UIHelper class
        recyclerView.setHasFixedSize(true);

        if(savedInstanceState == null) {
            UIHelper uiHelper = new UIHelper(ctx, rootView);
            uiHelper.execute();
//            recyclerView.setBackgroundColor(Color.rgb(0,200,0));

//        CardView.LayoutParams params = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        CardView cardView = new CardView(ctx);
//        cardView.setLayoutParams(params);
//        View view = getViewForCard();
//        cardView.addView(view);

        }
        return rootView;        // return the rootView
    }

   /* public void showView(ArrayList<Bitmap> bitmaps){

        Context ctx = getActivity();
        ArrayList<String> strings = new ArrayList<>(Arrays.asList("Pollution", "Corruption", "Poverty", "Crime", "Poor literacy",
                "x", "y", "z", "9","10"));

        ArrayList<Bitmap> drawables = bitmaps;
        RecyclerView recyclerView = new RecyclerView(ctx);
        LinearLayoutManager llm = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(llm);
        RecyclerView.Adapter adapter = new MyRecyclerAdapter(strings, drawables);
        recyclerView.setAdapter(adapter);
        ((ViewGroup) rootView).addView(recyclerView);

    }*/
}

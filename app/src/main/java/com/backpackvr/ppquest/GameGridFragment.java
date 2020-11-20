package com.backpackvr.ppquest;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

public class GameGridFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    public static ArrayList<Game> games = new ArrayList<Game>();
    public static GameCellAdapter mAdapter = new GameCellAdapter(games);
    private Activity activity;

    public GameGridFragment() {
        // Required empty public constructor
    }

    public static GameGridFragment newInstance(String param1, String param2) {
        GameGridFragment fragment = new GameGridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    RecyclerView recyclerView;
    public void BuildRecycleView(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.game_grid);



        LinearLayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new GameCellAdapter(games);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new GameCellAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                Log.i("FRAGMENT", "pos: " + pos);
                OpenGame(games.get(pos).game_id);
            }
        });

    }

    public void OpenGame(String gameId) {
        ((MainActivity)getActivity()).openGame(gameId);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (games.size() == 0) {
            downloadDatabase();
        } else {
            title.setText(games.size() + "/" + Database.gameList.size());
        }
    }

    TextView title;

    Button buttonNew;
    Button buttonPopular;
    Button buttonSize;
    Button buttonPrice;

    boolean free = true;
    boolean paid = true;
    boolean nsfw = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_top_charts, container, false);

        BuildRecycleView(view);


        title =  (TextView) view.findViewById(R.id.textPaid);
        title.setText(games.size() + "/" + Database.gameList.size());

        buttonNew  = (Button) view.findViewById(R.id.buttonNew);
        buttonPopular  = (Button) view.findViewById(R.id.buttonPopular);
        buttonSize  = (Button) view.findViewById(R.id.buttonSize);
        buttonPrice  = (Button) view.findViewById(R.id.buttonPrice);
        buttonNew.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                sortByTimestamp();
                mAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
            }});
        buttonPopular.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                sortByPopularity();

                mAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
            }});
        buttonSize.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                sortBySize();

                mAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
            }});
        buttonPrice.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                sortByPrice();

                mAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
            }});
        return view;
    }

    public void sortByPrice() {
        Collections.sort(games, new Comparator<Game>(){
            public int compare(Game obj1, Game obj2) {
                return Integer.valueOf(obj2.priceCents).compareTo(Integer.valueOf(obj1.priceCents));
            }
        });
    }

    public void sortBySize() {
        Collections.sort(games, new Comparator<Game>(){
            public int compare(Game obj1, Game obj2) {
                return Integer.valueOf(obj2.sizeMB).compareTo(Integer.valueOf(obj1.sizeMB));
            }
        });
    }

    public void sortByPopularity() {
        Collections.sort(games, new Comparator<Game>(){
            public int compare(Game obj1, Game obj2) {
                return Integer.valueOf(obj1.popularity).compareTo(Integer.valueOf(obj2.popularity));
            }
        });
    }

    public void sortByTimestamp() {
        Collections.sort(games, new Comparator<Game>(){
            public int compare(Game obj1, Game obj2) {
                return Integer.valueOf(obj2.timestamp).compareTo(Integer.valueOf(obj1.timestamp));
            }
        });
    }
    public void updateList() {
        games.clear();
        for (String gameId : Database.gameList.keySet()) {
            Game g = Database.gameList.get(gameId);
            if (g.price.equals("free") && !free) {
                continue;
            }
            if (!g.price.equals("free") && !paid) {
                continue;
            }
            if (g.NSFW && !nsfw) {
                continue;
            }
            games.add(g);
        }
        title.setText(games.size() + "/" + Database.gameList.size());
        mAdapter.notifyDataSetChanged();
    }

    public void downloadDatabase() {
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url ="https://www.reddit.com/r/ppquest/wiki/index.json";


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObject = new JSONObject(response);

                            String[] rows = jObject.getJSONObject("data").getString("content_md").split("\\r\\n");
                            String[] header = rows[0].split(Pattern.quote(" | "));

                            Log.i("DatabaseDownload", "itemsLength " + rows.length);
                            Log.i("DatabaseDownload", "headerLength " + header.length);

                            //ArrayList<Game> gameList = new ArrayList<Game>();
                            for (int i = 2; i < rows.length; ++i) {
                                String[] data = rows[i].split(Pattern.quote(" | "));
                                Game g = new Game();
                                g.popularity = i;
                                for (int j = 0; j < header.length; ++j) {
                                    if (header[j].equals("link")) {
                                        g.link = data[j];
                                    }
                                    if (header[j].equals("id")){
                                        g.game_id = data[j];
                                    }
                                    if (header[j].equals("title")) {
                                        g.title = data[j];
                                    }
                                    if (header[j].equals("nsfw")){
                                        g.NSFW = data[j].equals("nsfw");
                                    }
                                    if (header[j].equals("price")) {
                                        if (data[j].equals("-")) {
                                            g.price = "free";
                                            g.priceCents = 0;
                                        } else {
                                            g.price = data[j];
                                            String priceParts_ = g.price.replace("$","");
                                            String[] priceParts = priceParts_.split("\\.");
                                            g.priceCents = Integer.parseInt(priceParts[0]) * 100;
                                            if (priceParts.length > 1) {
                                                g.priceCents += Integer.parseInt(priceParts[1]);
                                            }
                                        }
                                    }
                                    if (header[j].equals("size")) {
                                        g.size = data[j];
                                        if (!g.size.equals("-")) {
                                            String[] sizeParts1 = g.size.split(" ");
                                            g.sizeMB = Integer.parseInt(sizeParts1[0].replaceAll(",",""));
                                            if (sizeParts1[1].equals("GB")) {
                                                g.sizeMB *= 1024;
                                            }
                                        } else {
                                            g.sizeMB = 0;
                                        }

                                    }
                                    if (header[j].equals("timestamp")) {
                                        g.timestamp = Integer.parseInt(data[j]);
                                    }
                                    if (header[j].equals("thumbnail")){
                                        g.images.add(data[j]);
                                    }


                                    if (header[j].equals("genre")){
                                        g.genre = data[j];
                                        if (!data[j].equals("-")) {
                                            g.tags.add(data[j]);
                                        }

                                    }
                                    if (header[j].equals("tags") && !data[j].equals("-")){

                                        String[] tags = data[j].split(", ");
                                        for (String tag : tags) {
                                            if (tag.equals("oculus-quest")) {
                                                continue;
                                            }
                                            if (tag.equals("Virtual Reality (VR)")) {
                                                continue;
                                            }
                                            if (tag.equals("sidequest")) {
                                                continue;
                                            }
                                            if (tag.equals("Oculus Rift")) {
                                                continue;
                                            }
                                            if (tag.equals("3D")) {
                                                continue;
                                            }
                                            if (tag.equals("oculus")) {
                                                continue;
                                            }
                                            if (tag.equals("oculus-go")) {
                                                continue;
                                            }
                                            if (tag.equals("oculusquest")) {
                                                continue;
                                            }
                                            g.tags.add(tag);
                                        }
                                    }

                                    if (header[j].equals("screenshots")){
                                        String[] screenshots = data[j].split(",");
                                        for (String s : screenshots) {
                                            g.images.add(s);
                                        }
                                    }
                                    if (header[j].equals("images")){
                                        String[] screenshots = data[j].split(",");
                                        for (String s : screenshots) {
                                            g.images.add(s);
                                        }
                                    }
                                }
                                Database.gameList.put(g.game_id, g);
                                games.add(g);

                            }

                            mAdapter.notifyDataSetChanged();

                            title.setText(games.size() + "/" + Database.gameList.size());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TopCharts", error.toString());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }


}
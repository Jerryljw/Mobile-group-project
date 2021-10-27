package com.comp90018.proj2.ui.finder;
import com.comp90018.proj2.ui.post.*;
import android.os.Bundle;
import com.comp90018.proj2.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.comp90018.proj2.R;
import java.util.ArrayList;


public class FinderFragment extends Fragment {

    // space between each card RecyclerView
    private int space=15;

    // store items
    private ArrayList<CardItem> cardItemArrayList = new ArrayList<>();
    private View view;
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;

    private static final String TAG = "Extract";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_finder,container,false);

        // get data
        initData();

        // show data
        initRecycleView();

        return view;
    }

    private void initRecycleView(){
        // Create a recyclerView object，initialize xml
        recyclerView=(RecyclerView) view.findViewById(R.id.home_item);

        // create an adapter to put items
        homeAdapter = new HomeAdapter(getActivity(),cardItemArrayList);

        // set format of view，2 cols
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        // add item space to recycleView
        recyclerView.addItemDecoration(new space_item(space));

        //add adapter to recyclerView
        recyclerView.setAdapter(homeAdapter);

        // create click listener on adapter
        homeAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, CardItem cardItem) {
//                Toast.makeText(getActivity(),"我是item",Toast.LENGTH_SHORT).show();

                // jump to post activity
                // TODO: need to pass USERID to PostActivity, use !postId!
                Intent intent = new Intent(getActivity(), PostActivity.class);
                Bundle bundle = new Bundle();
                Log.e(TAG, String.valueOf(cardItem.getPostId()));
                bundle.putInt("postId",cardItem.getPostId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    // TODO: According latest posttime iteratively get the post information
    private void initData() {
        for (int i = 0; i<5;i++){
            CardItem cardItem = new CardItem();
            cardItem.setImg(R.drawable.ic_card_image);
            cardItem.setHeadsIcon(R.drawable.ic_card_portrait);
            cardItem.setTitles("Title"+i);
            cardItem.setUsernames("Ivan"+i);
            cardItem.setDistance(i+" km");
            cardItem.setPostId(i);
            cardItemArrayList.add(cardItem);
        }
    }


    // inner adapter class
    static class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        private Context context;
        private ArrayList<CardItem> cardItemArrayList;

        public HomeAdapter(Context context, ArrayList<CardItem> cardItemArrayList) {
            this.context = context;
            this.cardItemArrayList = cardItemArrayList;
        }

        @NonNull
        @Override
        // loading layout, and return MyViewHolder object
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Create view object
            View view = LayoutInflater.from(context).inflate(R.layout.carditem ,parent,false);

            // Create MyViewHolder object
            MyViewHolder myViewHolder=new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        // get data and showing
        public void onBindViewHolder(@NonNull HomeAdapter.MyViewHolder holder, int position) {
            CardItem cardData = cardItemArrayList.get(position);
            holder.img.setImageResource(cardData.getImg());
            holder.title.setText(cardData.getTitles());
            holder.head.setImageResource(cardData.getHeadsIcon());
            holder.username.setText(cardData.getUsernames());
            holder.distance.setText(cardData.getDistance());

            // 0 means unsolved
            if (cardData.getPostType()==0){
                holder.postType.setImageResource(R.drawable.ic_finder_question);
            }else{

                holder.postType.setImageResource(R.drawable.ic_finder_bingo);
            }
        }

        @Override
        public int getItemCount() {
            // get the number of all card items
            return cardItemArrayList.size();
        }


        // inner class
        class MyViewHolder extends RecyclerView.ViewHolder{

            ImageView img,head,postType;
            TextView title,username,distance;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                img=itemView.findViewById(R.id.home_item_img);
                title=itemView.findViewById(R.id.home_item_title);
                head=itemView.findViewById(R.id.home_item_head);
                username=itemView.findViewById(R.id.home_item_username);
                distance=itemView.findViewById(R.id.home_item_location);
                postType=itemView.findViewById(R.id.home_item_post_type);

                // click listener
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null){
                            onItemClickListener.OnItemClick(v,cardItemArrayList.get(getLayoutPosition()));
                        }
                    }
                });
            }
        }

        // need to be overwrited in adapter, enable to click item
        public interface OnItemClickListener{
            void OnItemClick(View view, CardItem cardItem);
        }

        private OnItemClickListener onItemClickListener;
        public void setOnItemClickListener(OnItemClickListener onItemClickListener){
            this.onItemClickListener = onItemClickListener;
        }
    }
    class space_item extends RecyclerView.ItemDecoration{
        // the space between items
        private int space=5;
        public space_item(int space){
            this.space=space;
        }
        public void getItemOffsets(Rect outRect,View view,RecyclerView parent,RecyclerView.State state){
            outRect.bottom=space;
            outRect.top=space;
        }
    }
}
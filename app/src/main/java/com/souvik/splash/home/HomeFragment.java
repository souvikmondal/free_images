package com.souvik.splash.home;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.souvik.splash.MainActivity;
import com.souvik.splash.R;
import com.souvik.splash.model.PinModel;
import com.souvik.splash.util.BitmapCache;
import com.souvik.splash.util.DownloadedDrawable;
import com.souvik.splash.util.ImageLoaderCallback;


public class HomeFragment extends Fragment implements HomeView,
        LoaderManager.LoaderCallbacks<ApiResult> {

    private static final int ID_LOADER = 1;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Adapter adapter;
    private int pageNumber = 1;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private Parcelable listState;


    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view, savedInstanceState);

        if (savedInstanceState != null) {
            pageNumber = savedInstanceState.getInt("page");
            listState = savedInstanceState.getParcelable("list_state");
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("page", pageNumber);
        Parcelable state = staggeredGridLayoutManager.onSaveInstanceState();
        outState.putParcelable("list_state", state);
    }

    private void initView(View view, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        int col_count = (int) getResources().getInteger(R.integer.grid_col_count);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(col_count,
                StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view,
                                       RecyclerView parent, RecyclerView.State state) {
                outRect.right = 0;
                outRect.bottom = 0;
                outRect.top = 0;
                outRect.top = 5;
            }
        });


//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int[] lastVisibleItemPositions = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
//            }
//        });

//        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                pageNumber = page;
//                restartLoader();
//            }
//        });
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swip_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                restartLoader();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        swipeRefreshLayout.setRefreshing(true);
        initLoader();
    }

    private void initLoader() {
        getLoaderManager().initLoader(ID_LOADER, null, this);
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(ID_LOADER, null, this);
    }

    private void hookRecycler(PinModel[] models) {
        if (adapter == null) {
            adapter = new Adapter(models);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.update(models);
        }

        if (listState != null) {
            staggeredGridLayoutManager.onRestoreInstanceState(listState);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            openSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openSettings() {

    }

    @Override
    public void openUserProfile() {

    }

    @Override
    public void openFullImage() {

    }

    @Override
    public void openSearchBar() {

    }

    @Override
    public void loadImageListData() {

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new ImageListLoader(getActivity(), pageNumber);
    }

    @Override
    public void onLoadFinished(Loader loader, ApiResult data) {
        swipeRefreshLayout.setRefreshing(false);
        if (data.errorMessage == null) {
            hookRecycler(data.model);
        } else {
            ((MainActivity)getActivity()).displayMessage(data.errorMessage);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView mainImage, profileImage;
        TextView profileName;

        public Holder(View itemView) {
            super(itemView);
            mainImage = (ImageView) itemView.findViewById(R.id.image);
//            profileImage = (ImageView) itemView.findViewById(R.id.profile);
//            profileName = (TextView) itemView.findViewById(R.id.user_name);
        }
    }

    class Adapter extends RecyclerView.Adapter<Holder> {

        PinModel[] data;

        Adapter(PinModel[] data) {
            this.data = data;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.image_row, null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = recyclerView.getChildLayoutPosition(v);
                    PinModel pinModel = getItemAt(itemPosition);
                    ((MainActivity)getActivity()).showDialog(pinModel.getUrls().get("regular"));
                }
            });
            Holder holder = new Holder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            PinModel pinModel = getItemAt(position);
            String imageUrl = pinModel.getUrls().get("small");
            if(cancelPotentialDownload(imageUrl, holder.mainImage)) {
                ImageLoaderCallback imageLoaderCallback = new ImageLoaderCallback(holder.mainImage, imageUrl);
                DownloadedDrawable downloadedDrawableImage = new DownloadedDrawable(imageLoaderCallback);
                holder.mainImage.setImageDrawable(downloadedDrawableImage);
                BitmapCache.getCache().get(imageUrl, imageLoaderCallback, false);
            }
        }

        public PinModel getItemAt(int position) {
            return data[position];
        }

        @Override
        public int getItemCount() {
            return data.length;
        }

        public void update(PinModel[] models) {
            this.data = models;
            notifyDataSetChanged();
        }
    }

    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        ImageLoaderCallback imageLoaderCallback = ImageLoaderCallback.getImageLoaderCallback(imageView);

        if (imageLoaderCallback != null ) {
            if (!url.equals(imageLoaderCallback.url)) {
                BitmapCache.getCache().cancel(url);
            } else {
                //already being downloaded
                return false;
            }
        }
        return true;
    }




}

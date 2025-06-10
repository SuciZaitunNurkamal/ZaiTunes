package com.project.finalmobile.zaitunes.ui.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.project.finalmobile.R;
import com.project.finalmobile.databinding.FragmentHomeBinding;
import com.project.finalmobile.zaitunes.local.DatabaseContract.TrackColumns;
import com.project.finalmobile.zaitunes.local.MappingHelper;
import com.project.finalmobile.zaitunes.local.RatedTrack;
import com.project.finalmobile.zaitunes.local.RatedTrackHelper;
import com.project.finalmobile.zaitunes.api.ApiService;
import com.project.finalmobile.zaitunes.api.RetrofitClient;
import com.project.finalmobile.zaitunes.model.ResultsItem;
import com.project.finalmobile.zaitunes.model.RssResponse;
import com.project.finalmobile.zaitunes.ui.adapter.PopularSongAdapter;
import com.project.finalmobile.zaitunes.ui.adapter.RatedTrackAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.project.finalmobile.zaitunes.ui.adapter.DisplayMode;

public class HomeFragment extends Fragment implements PopularSongAdapter.OnTrackClickListener, RatedTrackAdapter.OnTrackClickListener {

    private FragmentHomeBinding binding;
    private RatedTrackHelper ratedTrackHelper; // Mengganti AppDatabase
    private ApiService apiService;
    private RatedTrackAdapter ratedTrackAdapter;
    private PopularSongAdapter popularSongAdapter;

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Menggunakan RatedTrackHelper
        ratedTrackHelper = RatedTrackHelper.getInstance(requireContext());
        ratedTrackHelper.open();

        apiService = RetrofitClient.getApiService();
        setupRecyclerViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecentlyRatedTracks();
        fetchPopularSongs();
    }

    private void setupRecyclerViews() {
        ratedTrackAdapter = new RatedTrackAdapter(new ArrayList<>(), this, DisplayMode.HOME);
        binding.recentlyRatedRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recentlyRatedRv.setAdapter(ratedTrackAdapter);
        binding.recentlyRatedRv.setNestedScrollingEnabled(false);

        popularSongAdapter = new PopularSongAdapter(new ArrayList<>(), this);
        binding.popularSongsRv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.popularSongsRv.setAdapter(popularSongAdapter);
    }

    private void loadRecentlyRatedTracks() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Mengambil data menggunakan Cursor
            Cursor cursor = ratedTrackHelper.queryAll();
            // Mengubah Cursor menjadi ArrayList
            final List<RatedTrack> tracks = MappingHelper.mapCursorToArrayList(cursor);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    // Logika empty state tetap sama
                    ratedTrackAdapter.updateData(tracks);
                });
            }
        });
    }

    private void fetchPopularSongs() {
        // Metode ini tidak berubah karena tidak menyentuh database lokal
        // ... (biarkan isi metode fetchPopularSongs tetap sama)
    }

    @Override
    public void onPopularSongClick(ResultsItem song) {
        if (binding == null || song == null) return;
        // Menggunakan ContentValues untuk menyimpan data
        ContentValues values = new ContentValues();
        values.put(TrackColumns._ID, song.getTrackId());
        values.put(TrackColumns.TRACK_NAME, song.getTrackName());
        values.put(TrackColumns.ARTIST_NAME, song.getArtistName());
        values.put(TrackColumns.COLLECTION_NAME, song.getCollectionName());
        values.put(TrackColumns.GENRE, song.getPrimaryGenreName());
        values.put(TrackColumns.RELEASE_DATE, song.getReleaseDate());
        values.put(TrackColumns.TRACK_VIEW_URL, song.getTrackViewUrl());
        values.put(TrackColumns.RATING, 0); // Rating awal

        Executors.newSingleThreadExecutor().execute(() -> ratedTrackHelper.insert(values));

        Bundle bundle = new Bundle();
        bundle.putLong("trackId", song.getTrackId());
        Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_playerFragment, bundle);
    }

    @Override
    public void onRatedTrackClick(RatedTrack track) {
        if (binding == null || track == null) return;
        Bundle bundle = new Bundle();
        bundle.putLong("trackId", track.getTrackId());
        Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_playerFragment, bundle);
    }

    @Override
    public void onDeleteClick(RatedTrack track) {
        // Implementasi delete akan kita lakukan di FavoritesFragment
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ratedTrackHelper.close(); // Tutup helper
        binding = null;
    }
}
package com.project.finalmobile.zaitunes.ui.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
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
import com.project.finalmobile.zaitunes.model.Response;
import com.project.finalmobile.zaitunes.ui.adapter.PopularSongAdapter;
import com.project.finalmobile.zaitunes.ui.adapter.RatedTrackAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import com.project.finalmobile.zaitunes.ui.adapter.DisplayMode;

// Fragment untuk menampilkan halaman utama aplikasi
public class HomeFragment extends Fragment implements PopularSongAdapter.OnTrackClickListener, RatedTrackAdapter.OnTrackClickListener {

    private FragmentHomeBinding binding;
    private RatedTrackHelper ratedTrackHelper;
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
        // Inisialisasi database helper
        ratedTrackHelper = RatedTrackHelper.getInstance(requireContext());
        ratedTrackHelper.open();

        // Inisialisasi API service
        apiService = RetrofitClient.getApiService();
        setupRecyclerViews();

        // Setup tombol settings
        binding.btnSettings.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.settingsFragment);
        });

        // Setup tombol refresh
        binding.btnRefresh.setOnClickListener(v -> {
            loadRecentlyRatedTracks();
            fetchPopularSongs();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecentlyRatedTracks();
        fetchPopularSongs();
    }

    // Setup RecyclerView untuk daftar lagu
    private void setupRecyclerViews() {
        // Adapter untuk lagu yang baru di-rating
        ratedTrackAdapter = new RatedTrackAdapter(new ArrayList<>(), this, DisplayMode.HOME);
        binding.recentlyRatedRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recentlyRatedRv.setAdapter(ratedTrackAdapter);
        binding.recentlyRatedRv.setNestedScrollingEnabled(false);

        // Adapter untuk lagu populer
        popularSongAdapter = new PopularSongAdapter(new ArrayList<>(), this);
        binding.popularSongsRv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.popularSongsRv.setAdapter(popularSongAdapter);
    }

    // Load lagu yang baru di-rating dari database
    private void loadRecentlyRatedTracks() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Cursor cursor = ratedTrackHelper.queryAll();
            final List<RatedTrack> tracks = MappingHelper.mapCursorToArrayList(cursor);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    if (tracks.isEmpty()) {
                        binding.recentlyRatedRv.setVisibility(View.GONE);
                        binding.emptyRecentlyRated.setVisibility(View.VISIBLE);
                    } else {
                        binding.recentlyRatedRv.setVisibility(View.VISIBLE);
                        binding.emptyRecentlyRated.setVisibility(View.GONE);
                        ratedTrackAdapter.updateData(tracks);
                    }
                });
            }
        });
    }

    // Fetch lagu populer dari API
    private void fetchPopularSongs() {
        binding.popularSongsProgressBar.setVisibility(View.VISIBLE);
        binding.popularSongsRv.setVisibility(View.GONE);
        binding.emptyPopularSongs.setVisibility(View.GONE);

        apiService.getPopularSongs("top").enqueue(new Callback<Response>() {
            @Override
            public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                binding.popularSongsProgressBar.setVisibility(View.GONE);
                if (binding == null) return;
                
                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                    List<ResultsItem> songs = response.body().getResults();
                    if (!songs.isEmpty()) {
                        binding.popularSongsRv.setVisibility(View.VISIBLE);
                        binding.emptyPopularSongs.setVisibility(View.GONE);
                        popularSongAdapter.updateData(songs);
                    } else {
                        binding.popularSongsRv.setVisibility(View.GONE);
                        binding.emptyPopularSongs.setVisibility(View.VISIBLE);
                        binding.emptyPopularSongs.setText(getString(R.string.tidak_ada_lagu_populer));
                    }
                } else {
                    binding.popularSongsRv.setVisibility(View.GONE);
                    binding.emptyPopularSongs.setVisibility(View.VISIBLE);
                    binding.emptyPopularSongs.setText(getString(R.string.gagal_memuat_lagu_populer));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                if (binding == null) return;
                binding.popularSongsProgressBar.setVisibility(View.GONE);
                binding.popularSongsRv.setVisibility(View.GONE);
                binding.emptyPopularSongs.setVisibility(View.VISIBLE);
                binding.emptyPopularSongs.setText(getString(R.string.error) + ": " + getString(R.string.periksa_koneksi));
            }
        });
    }

    @Override
    public void onPopularSongClick(ResultsItem song) {
        if (binding == null || song == null) return;
        ContentValues values = new ContentValues();
        values.put(TrackColumns.TRACK_ID, song.getTrackId());
        values.put(TrackColumns.TRACK_NAME, song.getTrackName());
        values.put(TrackColumns.ARTIST_NAME, song.getArtistName());
        values.put(TrackColumns.COLLECTION_NAME, song.getCollectionName());
        values.put(TrackColumns.GENRE, song.getPrimaryGenreName());
        values.put(TrackColumns.RELEASE_DATE, song.getReleaseDate());
        values.put(TrackColumns.TRACK_VIEW_URL, song.getTrackViewUrl());
        values.put(TrackColumns.ARTWORK_URL, song.getArtworkUrl100());
        values.put(TrackColumns.RATING, 0);

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
        // Implementasi delete ada di FavoritesFragment
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
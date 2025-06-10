package com.project.finalmobile.zaitunes.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.project.finalmobile.R;
import com.project.finalmobile.databinding.FragmentFavoritesBinding;
import com.project.finalmobile.zaitunes.local.MappingHelper;
import com.project.finalmobile.zaitunes.local.RatedTrack;
import com.project.finalmobile.zaitunes.local.RatedTrackHelper;
import com.project.finalmobile.zaitunes.ui.adapter.RatedTrackAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import com.project.finalmobile.zaitunes.ui.adapter.DisplayMode;
import android.util.Log;

// Fragment untuk menampilkan lagu favorit
public class FavoritesFragment extends Fragment implements RatedTrackAdapter.OnTrackClickListener {

    private FragmentFavoritesBinding binding;
    private RatedTrackHelper ratedTrackHelper;
    private RatedTrackAdapter ratedTrackAdapter;

    public FavoritesFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inisialisasi database helper
        ratedTrackHelper = RatedTrackHelper.getInstance(requireContext());
        ratedTrackHelper.open();
        setupRecyclerView();
        // Tambahkan aksi klik untuk tombol refresh
        binding.btnRefresh.setOnClickListener(v -> {
            loadRatedTracks();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRatedTracks();
    }

    // Setup RecyclerView untuk daftar favorit
    private void setupRecyclerView() {
        ratedTrackAdapter = new RatedTrackAdapter(new ArrayList<>(), this, DisplayMode.FAVORITES);
        binding.favoritesRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.favoritesRv.setAdapter(ratedTrackAdapter);
    }

    // Load lagu favorit dari database
    private void loadRatedTracks() {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (ratedTrackHelper == null) return;
            Cursor dataCursor = null;
            try {
                dataCursor = ratedTrackHelper.queryAll();
                final List<RatedTrack> tracks = MappingHelper.mapCursorToArrayList(dataCursor);
                Log.d("FavoritesFragment", "Loaded " + tracks.size() + " tracks");
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (binding == null) return;
                        if (tracks.isEmpty()) {
                            binding.favoritesRv.setVisibility(View.GONE);
                            binding.emptyViewText.setVisibility(View.VISIBLE);
                        } else {
                            binding.favoritesRv.setVisibility(View.VISIBLE);
                            binding.emptyViewText.setVisibility(View.GONE);
                            ratedTrackAdapter.updateData(tracks);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("FavoritesFragment", "Error loading tracks: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (dataCursor != null) {
                    dataCursor.close();
                }
            }
        });
    }

    // Handle klik pada lagu favorit
    @Override
    public void onRatedTrackClick(RatedTrack track) {
        if (track != null) {
            Log.d("FavoritesFragment", "Track clicked: " + track.getTrackName() + " (ID: " + track.getTrackId() + ")");
            Bundle bundle = new Bundle();
            bundle.putLong("trackId", track.getTrackId());
            Navigation.findNavController(requireView()).navigate(R.id.action_favoritesFragment_to_playerFragment, bundle);
        }
    }

    @Override
    public void onDeleteClick(RatedTrack track) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Hapus Favorit")
                .setMessage("Anda yakin ingin menghapus '" + track.getTrackName() + "'?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        if (ratedTrackHelper != null) {
                            int deleted = ratedTrackHelper.delete(String.valueOf(track.getTrackId()));
                            Log.d("FavoritesFragment", "Deleted " + deleted + " tracks");
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(this::loadRatedTracks);
                            }
                        }
                    });
                    Toast.makeText(getContext(), "Lagu dihapus", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (ratedTrackHelper != null) {
            ratedTrackHelper.close();
        }
        binding = null;
    }
}
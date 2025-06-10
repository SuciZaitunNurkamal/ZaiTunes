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

public class FavoritesFragment extends Fragment implements RatedTrackAdapter.OnTrackClickListener {

    private FragmentFavoritesBinding binding;
    private RatedTrackAdapter ratedTrackAdapter;
    private RatedTrackHelper ratedTrackHelper;

    public FavoritesFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ratedTrackHelper = RatedTrackHelper.getInstance(requireContext());
        ratedTrackHelper.open();
        setupRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRatedTracks();
    }

    private void setupRecyclerView() {
        ratedTrackAdapter = new RatedTrackAdapter(new ArrayList<>(), this, DisplayMode.FAVORITES);
        binding.favoritesRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.favoritesRv.setAdapter(ratedTrackAdapter);
    }

    private void loadRatedTracks() {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (ratedTrackHelper == null) return;
            Cursor dataCursor = ratedTrackHelper.queryAll();
            final List<RatedTrack> tracks = MappingHelper.mapCursorToArrayList(dataCursor);
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
        });
    }

    @Override
    public void onRatedTrackClick(RatedTrack track) {
        if (track != null) {
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
                            ratedTrackHelper.delete(String.valueOf(track.getTrackId()));
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
        // Do not close ratedTrackHelper here if it's shared
        binding = null;
    }
}
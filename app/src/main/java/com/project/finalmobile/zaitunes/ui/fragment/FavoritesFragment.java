package com.project.finalmobile.zaitunes.ui.fragment;

import android.content.DialogInterface;
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
import com.project.finalmobile.zaitunes.local.AppDatabase;
import com.project.finalmobile.zaitunes.local.RatedTrack;
import com.project.finalmobile.zaitunes.ui.adapter.RatedTrackAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import com.project.finalmobile.zaitunes.ui.adapter.DisplayMode;

// Langkah 1: Pastikan Fragment mengimplementasikan interface dari adapter
public class FavoritesFragment extends Fragment implements RatedTrackAdapter.OnTrackClickListener {

    private FragmentFavoritesBinding binding;
    private RatedTrackAdapter ratedTrackAdapter;
    private AppDatabase db;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = AppDatabase.getDatabase(requireContext());
        setupRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Selalu muat ulang data saat kembali ke halaman ini agar daftar selalu update
        loadRatedTracks();
    }

    private void setupRecyclerView() {
        // Langkah 2: Pastikan adapter diinisialisasi dengan listener (this)
        ratedTrackAdapter = new RatedTrackAdapter(new ArrayList<>(), this, DisplayMode.FAVORITES);
        binding.favoritesRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.favoritesRv.setAdapter(ratedTrackAdapter);
    }

    private void loadRatedTracks() {
        Executors.newSingleThreadExecutor().execute(() -> {
            final List<RatedTrack> tracks = db.ratedTrackDao().getAllRatedTracks();
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

    // --- Langkah 3: Implementasikan Metode dari Interface ---

    // Metode ini dipanggil saat item lagu diklik (untuk Edit)
    @Override
    public void onRatedTrackClick(RatedTrack track) {
        if (track != null) {
            Bundle bundle = new Bundle();
            bundle.putLong("trackId", track.getTrackId());
            // Pastikan Anda sudah membuat action ini di nav_graph.xml
            Navigation.findNavController(requireView()).navigate(R.id.action_favoritesFragment_to_playerFragment, bundle);
        }
    }

    // Metode ini dipanggil saat ikon tempat sampah diklik (untuk Hapus)
    @Override
    public void onDeleteClick(RatedTrack track) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Hapus Favorit")
                .setMessage("Anda yakin ingin menghapus '" + track.getTrackName() + "'?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.ratedTrackDao().delete(track);
                        // Muat ulang daftar setelah selesai menghapus
                        loadRatedTracks();
                    });
                    Toast.makeText(getContext(), "Lagu dihapus", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
    // ----------------------------------------------------

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.project.finalmobile.zaitunes.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.project.finalmobile.R;
import com.project.finalmobile.databinding.ItemTrackBinding;
import com.project.finalmobile.zaitunes.local.RatedTrack;
import java.util.List;

public class RatedTrackAdapter extends RecyclerView.Adapter<RatedTrackAdapter.RatedTrackViewHolder> {

    private List<RatedTrack> trackList;
    private OnTrackClickListener listener;
    private DisplayMode mode; // Variabel untuk menyimpan mode

    public interface OnTrackClickListener {
        void onRatedTrackClick(RatedTrack track);
        void onDeleteClick(RatedTrack track);
    }

    // Constructor diubah untuk menerima DisplayMode
    public RatedTrackAdapter(List<RatedTrack> trackList, OnTrackClickListener listener, DisplayMode mode) {
        this.trackList = trackList;
        this.listener = listener;
        this.mode = mode;
    }

    public void updateData(List<RatedTrack> newTrackList) {
        this.trackList = newTrackList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RatedTrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrackBinding binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RatedTrackViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RatedTrackViewHolder holder, int position) {
        RatedTrack track = trackList.get(position);
        holder.bind(track, listener, mode); // Kirim mode ke ViewHolder
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    static class RatedTrackViewHolder extends RecyclerView.ViewHolder {
        private ItemTrackBinding binding;

        public RatedTrackViewHolder(ItemTrackBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Metode bind diubah untuk menerima DisplayMode
        void bind(final RatedTrack track, final OnTrackClickListener listener, DisplayMode mode) {
            binding.titleTrack.setText(track.getTrackName());
            binding.artistName.setText(track.getArtistName());
            binding.albumName.setVisibility(View.GONE);

            binding.ratingBar.setVisibility(View.VISIBLE);
            binding.ratingBar.setRating(track.getRating());
            binding.ratingBar.setIsIndicator(true);

            Glide.with(itemView.getContext())
                    .load(track.getArtworkUrl100())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(binding.albumCover);

            // --- LOGIKA UTAMA UNTUK MENAMPILKAN/MENYEMBUNYIKAN IKON HAPUS ---
            if (mode == DisplayMode.FAVORITES) {
                binding.deleteIcon.setVisibility(View.VISIBLE);
                binding.deleteIcon.setImageResource(R.drawable.ic_delete);
                binding.deleteIcon.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteClick(track);
                    }
                });
            } else { // Mode HOME atau mode lainnya
                binding.deleteIcon.setVisibility(View.GONE);
            }
            // -----------------------------------------------------------------

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRatedTrackClick(track);
                }
            });
        }
    }
}
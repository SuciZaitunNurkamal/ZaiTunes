package com.project.finalmobile.zaitunes.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.project.finalmobile.R;
import com.project.finalmobile.databinding.ItemTrackHorizontalBinding;
import com.project.finalmobile.zaitunes.model.ResultsItem;
import java.util.List;

public class PopularSongAdapter extends RecyclerView.Adapter<PopularSongAdapter.PopularSongViewHolder> {

    private List<ResultsItem> songList;
    private OnTrackClickListener listener; // <-- Tambahkan listener

    public interface OnTrackClickListener {
        void onPopularSongClick(ResultsItem song);
    }

    public PopularSongAdapter(List<ResultsItem> songList, OnTrackClickListener listener) {
        this.songList = songList;
        this.listener = listener; // <-- Inisialisasi listener
    }

    public void updateData(List<ResultsItem> newSongList) {
        this.songList = newSongList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PopularSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrackHorizontalBinding binding = ItemTrackHorizontalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PopularSongViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularSongViewHolder holder, int position) {
        ResultsItem song = songList.get(position);
        holder.bind(song, listener); // <-- Kirim listener ke ViewHolder
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    static class PopularSongViewHolder extends RecyclerView.ViewHolder {
        private ItemTrackHorizontalBinding binding;

        public PopularSongViewHolder(ItemTrackHorizontalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final ResultsItem song, final OnTrackClickListener listener) {
            binding.titleTrack.setText(song.getTrackName());
            binding.artistName.setText(song.getArtistName());

            Glide.with(itemView.getContext())
                    .load(song.getArtworkUrl100())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(binding.albumCover);

            // Set listener untuk seluruh item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPopularSongClick(song);
                }
            });
        }
    }
}
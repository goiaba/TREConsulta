package br.com.goiaba.treconsulta.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import br.com.goiaba.treconsulta.R;
import br.com.goiaba.treconsulta.model.Voter;

import java.util.List;

public class ResultRecyclerViewAdapter extends RecyclerView.Adapter<VoterViewHolder> {
    private List<Voter> voters;
    private Context context;
    private LayoutInflater mLayoutInflater;

    public ResultRecyclerViewAdapter(Context context, List<Voter> data) {
        this.context = context;
        this.voters = data;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public VoterViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View recyclerViewItem = mLayoutInflater.inflate(R.layout.recyclerview_item_layout, parent, false);
//        recyclerViewItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handleRecyclerItemClick( (RecyclerView)parent, v);
//            }
//        });
        return new VoterViewHolder(recyclerViewItem);
    }


    @Override
    public void onBindViewHolder(VoterViewHolder holder, int position) {
        Voter voter = this.voters.get(position);
        holder.name.setText(voter.getName());
        holder.birthdate.setText(voter.getBirthdate());
        holder.voterId.setText(voter.getVoterId());
        holder.votingSection.setText(voter.getVotingSection());
        holder.votingPlace.setText(voter.getVotingPlace());
        holder.county.setText(voter.getCounty());
        holder.electoralZone.setText(voter.getElectoralZone());
    }

    @Override
    public int getItemCount() {
        return this.voters.size();
    }

//    private void handleRecyclerItemClick(RecyclerView recyclerView, View itemView) {
//        int itemPosition = recyclerView.getChildLayoutPosition(itemView);
//        Voter voter  = this.voters.get(itemPosition);
//        Toast.makeText(this.context, voter.getName(), Toast.LENGTH_LONG).show();
//    }
}

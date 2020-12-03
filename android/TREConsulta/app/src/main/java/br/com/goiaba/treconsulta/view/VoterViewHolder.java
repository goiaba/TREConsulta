package br.com.goiaba.treconsulta.view;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.com.goiaba.treconsulta.R;

public class VoterViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView birthdate;
    TextView voterId;
    TextView votingSection;
    TextView votingPlace;
    TextView county;
    TextView electoralZone;

    public VoterViewHolder(@NonNull View itemView) {
        super(itemView);
        this.name = itemView.findViewById(R.id.name);
        this.birthdate = itemView.findViewById(R.id.birthdate);
        this.voterId = itemView.findViewById(R.id.voterId);
        this.votingSection = itemView.findViewById(R.id.votingSection);
        this.votingPlace = itemView.findViewById(R.id.votingPlace);
        this.county = itemView.findViewById(R.id.county);
        this.electoralZone = itemView.findViewById(R.id.electoralZone);
    }
}

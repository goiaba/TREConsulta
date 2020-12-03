package br.com.goiaba.treconsulta.view;

import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.goiaba.treconsulta.R;
import br.com.goiaba.treconsulta.dao.VoterDao;
import br.com.goiaba.treconsulta.model.Voter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TRESearchActivity extends AppCompatActivity {

    public static final String LOG_TAG = "TREConsulta";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView voterName = findViewById(R.id.voterName);
        final TextView birthdate = findViewById(R.id.birthdate);
        final Button clearBtn = findViewById(R.id.clearBtn);
        final Button searchBtn = findViewById(R.id.searchBtn);
        final RecyclerView resultView = findViewById(R.id.resultView);
        final VoterDao voterDao = new VoterDao(getApplicationContext());

        resultView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        birthdate.addTextChangedListener(MaskWatcher.buildBirthdate());

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voterName.setText("");
                voterName.setError(null);
                birthdate.setText("");
                birthdate.setError(null);
                resultView.setVisibility(View.GONE);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidFields(voterName, birthdate)) {
                    String voterNameStr = voterName.getText().toString().toUpperCase();
                    String birthdateStr = birthdate.getText().toString() + "/XXXX";
                    List<Voter> voters = voterDao.getVoterInfo(voterNameStr, birthdateStr);
                    if (voters.isEmpty()) {
                        resultView.setVisibility(View.GONE);
                        String msg = "Nenhum eleitor encontrado. Entre em contato com o Cartório Eleitoral.";
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    } else {
                        resultView.setAdapter(new ResultRecyclerViewAdapter(TRESearchActivity.this, voters));
                        resultView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private boolean isValidFields(TextView voterName, TextView birthdate) {
        String voterNameStr = voterName.getText().toString();
        String birthdateStr = birthdate.getText().toString();
        if (voterNameStr.isEmpty()) {
            voterName.setError("Nome do eleitor");
        }
        Pattern pattern = Pattern.compile("[0123][0-9]/[01][0-9]");
        Matcher matcher = pattern.matcher(birthdateStr);
        if (birthdateStr.isEmpty() || !matcher.matches()) {
            birthdate.setError("Formato: DD/MM");
        }
        return !voterNameStr.isEmpty() && !birthdateStr.isEmpty() && matcher.matches();
    }
}
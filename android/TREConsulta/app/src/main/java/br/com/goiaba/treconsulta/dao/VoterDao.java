package br.com.goiaba.treconsulta.dao;

import android.content.Context;
import android.database.Cursor;
import br.com.goiaba.treconsulta.model.Voter;

import java.util.ArrayList;
import java.util.List;

public class VoterDao extends BaseDao {

    public static final String QUERY_VOTER_INFO =
            "   select v.name, v.birthdate, v.voter_id, vs.code as section," +
            "          vp.name as place, c.name as county, e.code as zone" +
            "     from voter v," +
            "          votingsection vs," +
            "          votingplace vp," +
            "          county c," +
            "          electoralzone e" +
            "    where v.name like ?" +
            "      and v.birthdate = ?" +
            "      and v.voting_section_id == vs.id" +
            "      and vs.voting_place_id = vp.id" +
            "      and vp.county_id = c.id" +
            "      and c.electoral_zone_id = e.id" +
            " order by v.name";

    public VoterDao(Context context) {
        super(context);
    }

    public List<Voter> getVoterInfo(String name, String birthdate) {
        List<Voter> resultList = new ArrayList<>();
        open();
        Cursor cursor = database.rawQuery(QUERY_VOTER_INFO, new String[] {name + "%", birthdate});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            resultList.add(new Voter(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6)));
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return resultList;
    }
}

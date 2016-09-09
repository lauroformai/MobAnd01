package it.eng.moband.listdetail;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.eng.moband.R;
import it.eng.moband.db.CptHelperClass;
import it.eng.moband.db.CptQueryHelperClass;
import it.eng.moband.db.CptRecord;
import it.eng.moband.exceptions.NullObjectException;
import it.eng.moband.Constants.CptConstants;
import it.eng.moband.exceptions.TooManyRecordsException;

public class DetailRecordActivity extends AppCompatActivity {

    //private final String ITEM_ID = "ITEM_ID";
    private long idRecord = -1;

    private SQLiteDatabase db;
    private CptHelperClass cptDatabaseH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_dettaglio);

        Log.d("DetailRecordActivity", "onCreate(...)");

        Intent intent = getIntent();
        idRecord = getIdFromIntent(intent);
        Log.d("DetailRecordActivity", "L'intent ha \"passato\" idRecord = " + idRecord);

        OpenDB();
        if (idRecord > 0)
        {
            CptQueryHelperClass qhc = new CptQueryHelperClass(db);

            Log.d("DetailRecordActivity", "Dal DB recupera il record con idRecord = " + idRecord);
            Cursor c = getRecordDetailQuakeAsCursor(idRecord);


            if (c != null) {
                CptRecord cr = new CptRecord();
                try {
                    cr.extractRecord(c); // *** in questo punto si possiede il record da renderizzare
                } catch (NullObjectException e) {
                    e.printStackTrace();
                } catch (TooManyRecordsException e) {
                    e.printStackTrace();
                }
                renderDetailRecord(cr);
            }
        }

    }




    private Cursor getRecordDetailQuakeAsCursor(long idRecord)
    {
        CptQueryHelperClass qhc = new CptQueryHelperClass(db);

        Log.d("DetailRecordActivity", "Dal DB recupera il record con idRecord = " + idRecord);
        Cursor c = null;
        try {
            c = qhc.getRecordById(idRecord);
        }
        catch (NullObjectException ex)
        {
            Log.e(CptConstants.LOG_TAG ,"il cursore non ha restituito record / il DB è vuoto.");
        }
        return  c;
    }

    private void OpenDB()
    {
        cptDatabaseH = new CptHelperClass(this);
        try {
            cptDatabaseH.preparaDbCopiato();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("DetailRecordActivity", "Apre db.");
        db = cptDatabaseH.getWritableDatabase();
    }



    private void renderDetailRecord(CptRecord cr)
    {
        ((TextView)findViewById(R.id.detail_activity_epicentro)).setText(cr.getEPICENTRAL_AREA());

        SimpleDateFormat mioFormato = new SimpleDateFormat("dd/MM/yyyy");

        String temp = null;
        try {
            temp = mioFormato.format (cr.getDateQuake());
        } catch (ParseException e) {
            e.printStackTrace();
            temp = "errore di formato data";
        }

        ((TextView)findViewById(R.id.detail_activity_data)).setText(temp);
        ((TextView)findViewById(R.id.detail_activity_ora)).setText(cr.getTimeQuake());
        ((TextView)findViewById(R.id.detail_activity_magnitudo)).setText(cr.getINTENSITY_DEF());

        if (cr.getDEPTH().trim() != "")
            ((TextView)findViewById(R.id.detail_activity_profondita)).setText(cr.getDEPTH() + " Km");
        else
            ((TextView)findViewById(R.id.detail_activity_profondita)).setText("n.d.");

        if (cr.getDEPTH().trim() != "")
            ((TextView)findViewById(R.id.detail_activity_longitudine)).setText(cr.getLONGITUDE() + "°");
        else
            ((TextView)findViewById(R.id.detail_activity_longitudine)).setText("n.d.");

        if (cr.getDEPTH().trim() != "")
            ((TextView)findViewById(R.id.detail_activity_latitudine)).setText(cr.getLATITUDE() + "°");
        else
            ((TextView)findViewById(R.id.detail_activity_latitudine)).setText("n.d.");
    }



    private long getIdFromIntent(Intent intent)
    {
        long id = -1;
        try
        {
            id = (long)intent.getExtras().get(CptConstants.ITEM_ID);
        }
        catch (Exception ex)
        {
            id = -1;
        }

        return  id;
    }


}






package com.mycompany.capturatest;

import android.util.Pair;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class LogItem {
    int logItemNo;

    @Nullable String discrepancy;   // String detailing the reported discrepancy (disc)
    @Nullable String stationDisc;   // IATA code of the station where the disc was logged
    @Nullable String reportedBy;    // Name of who reported the disc
    @Nullable Date dateReported;    // Date when the discrepancy was reported
    @Nullable String ataDisc;       // ATA Chapter corresponding to the disc
    @Nullable String flightNo;      // Flight no. of the flight in which the disc was reported
    @Nullable String opFolio;       // Folio of the corresponding operation log

    boolean deferred;               // Was the disc deferred?
    @Nullable String deferralBasis; // Under what MEL item was the deferral accepted
    String correctiveAction;        // String detailing the corrective action (corrAct) taken
    String executedBy;              // Name of who executed the corrAct
    String executedByLicense;       // License of who executed the corrAct
    Date dateExecuted;              // Date when the corrective action was executed
    @Nullable String ataCorrAct;    // ATA Chapter corresponding to the corrAct
    Integer oilEng1;                // Oil added to the engine #1
    Integer oilEng2;                // Oil added to the engine #2
    Integer oilAPU;                 // Oil added to the APU

    public LogItem(int logItem) {
        logItemNo = logItem;
    }

    public JSONObject toJSON() {
        JSONObject logItemObject = new JSONObject();    // Base json object, contains 2 sub-items
        JSONObject discObject = new JSONObject();       // Subitem 1, contains data about disc
        JSONObject corrActObject = new JSONObject();    // Subitem 2, contains data about corrAct
        JSONObject techObject = new JSONObject();       // Technician that performed the task
        JSONObject oilObject = new JSONObject();        // Oil added to the aircraft

        try {
            discObject.put("info", discrepancy);
            discObject.put("station", stationDisc);
            discObject.put("reported_by", reportedBy);
            discObject.put("date", dateReported);
            discObject.put("ATA", ataDisc);
            discObject.put("flight_no", flightNo);
            discObject.put("operation_log", opFolio);
            logItemObject.put("discrepancy", discObject);

            corrActObject.put("info", correctiveAction);
            corrActObject.put("deferred", deferred);
            corrActObject.put("deferral_basis", deferralBasis);
            techObject.put("name", executedBy);
            techObject.put("license", executedByLicense);
            corrActObject.put("executed_by", techObject);
            corrActObject.put("date", dateExecuted);
            corrActObject.put("ATA", ataCorrAct);
            oilObject.put("eng1", oilEng1);
            oilObject.put("eng2", oilEng2);
            oilObject.put("apu", oilAPU);
            corrActObject.put("oil", oilObject);
            logItemObject.put("corrective_action", corrActObject);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return logItemObject;
    }
}

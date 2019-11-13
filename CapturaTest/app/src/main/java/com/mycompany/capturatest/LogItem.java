package com.mycompany.capturatest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogItem {
    /* Object representing an entry in a maintenance log
     * Attributes:
     *   - logItemNo (Integer): Number of entry in the maintenance log (max 3).
     *   - discrepancy (BaseLogItem): Represents the discrepancy report in the maint. log.
     *       - station (String): IATA code of the station where the discrepancy was reported.
     *       - ataChapter (String): ATA Chapter corresponding to the discrepancy.
     *       - isCrewReport (bool): Indicates if the report was made by a crew member.
     *       - reportedBy (Class): Object representing who reported the discrepancy.
     *           - name (String): Name of who reports the discrepancy.
     *           - license (String): Their license number.
     *       - dateReported (Date): Date in which the report was made.
     *       - flightNo (String): Flight number of the flight in which the report was made.
     *       - operationLog (Integer): Folio of the corresponding operation log.
     *       - description (String): Description of the discrepancy.
     *   - correctiveAction (BaseLogItem): Represents the corrective action report in log.
     *       - station (String): IATA code of the station where the corrective action was performed.
     *       - ataChapter (String): ATA Chapter corresponding to the corrective action.
     *       - dateReported (Date): Date in which the corrective action was performed.
     *       - deferred (boolean): Indicates if the discrepancy was deferred.
     *       - deferralBasis (String): If it was deferred under which MEL Chapter.
     *       - reportedBy (Class): Object representing who executed the corrective action.
     *           - name (String): Name of the mechanic.
     *           - license (String): Their license number.
     *   - componentChange (Class): Object representing a component change.
     *       - removedPartNumber (String): Removed component's part number
     *       - removedSerialNumber (String): Removed component's serial number.
     *       - installedPartNumber (String): Installed component's part number.
     *       - installedSerialNumber (String): Installed component's serial number.
     *   - componentsChanged (List<>): Containing component changes.
     */
    private int logItemNumber;
    private Discrepancy discrepancy;
    private CorrectiveAction correctiveAction;

    LogItem(int logItemNo) {
        logItemNumber = logItemNo;
        discrepancy = new Discrepancy();
        correctiveAction = new CorrectiveAction();
    }

    private abstract class BaseLogItem {
        private String station;
        private String ataChapter;
        private Date dateReported;
        private Employee reportedBy;
        private String description;
        private boolean canceled;
        final String nullJSONString = "null";
        final String stationJSON = "station";
        final String ataJSON = "ataChapter";
        final String dateReportedJSON = "date";
        final String reportedByJSON = "reportedBy";
        final String descriptionJSON = "description";
        final String canceledJSON = "canceled";

        BaseLogItem() {
            reportedBy = new Employee();
            canceled = false;
        }

        public void setStation(String station) {
            this.station = station;
        }

        public String getStation() {
            return station;
        }

        public String getAtaChapter() {
            return ataChapter;
        }

        public void setAtaChapter(String ataChapter) {
            this.ataChapter = ataChapter;
        }

        public Date getDate() {
            return dateReported;
        }

        public void setDate(Date date) {
            dateReported = date;
        }

        public Employee getEmployee() {
            return reportedBy;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        boolean isCanceled() {
            return canceled;
        }

        public void setCanceled(boolean cancel) {
            canceled = cancel;
        }

        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            try {
                if (!canceled) {
                    jsonObject.put(stationJSON, station);
                    jsonObject.put(ataJSON, ataChapter != null ? ataChapter : nullJSONString);

                    // Convert date to a string
                    String pattern = "dd/MM/yyyy";
                    DateFormat df = new SimpleDateFormat(pattern,
                                                         new Locale("es", "mx"));
                    String dateString = df.format(dateReported);
                    jsonObject.put(dateReportedJSON, dateString);

                    jsonObject.put(reportedByJSON, reportedBy.toJSON());
                    jsonObject.put(descriptionJSON, description);
                } else {
                    jsonObject.put(canceledJSON, canceled);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return jsonObject;
        }
    }

    private class Discrepancy extends BaseLogItem {
        private String flightNumber;
        private String operationLog;
        final String flightNumberJSON = "flightNumber";
        final String operationLogJSON = "operationLog";

        Discrepancy() {
            super();
        }

        public String getFlightNumber() {
            return flightNumber;
        }

        public void setFlightNumber(String flightNumber) {
            this.flightNumber = flightNumber;
        }

        public String getOperationLog() {
            return operationLog;
        }

        public void setOperationLog(String operationLog) {
            this.operationLog = operationLog;
        }

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = super.toJSON();
            if (!isCanceled()) {
                // If it's not canceled add the extra parameters
                try {
                    jsonObject.put(flightNumberJSON, flightNumber != null ? flightNumber : nullJSONString);
                    jsonObject.put(operationLogJSON, operationLog != null ? operationLog : nullJSONString);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            } // If it's canceled there's no point in adding anything else
            return jsonObject;
        }
    }

    private class CorrectiveAction extends BaseLogItem {
        private boolean deferred;
        private String deferralBasis;
        final String deferredJSON = "deferred";
        final String deferralBasisJSON = "deferralBasis";

        CorrectiveAction() {
            super();
            deferred = false;
        }

        public boolean isDeferred() {
            return deferred;
        }

        public void setDeferred(boolean deferred) {
            this.deferred = deferred;
        }

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = super.toJSON();
            if (!isCanceled()) {
                // If it's not canceled add the extra parameters
                try {
                    jsonObject.put(deferredJSON, deferred);
                    if (deferred)
                        jsonObject.put(deferralBasisJSON, deferralBasis);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } // If it's canceled there's no point in adding anything else
            return jsonObject;
        }
    }

    protected class Employee {
        private String employeeName;
        private String employeeLicense;
        private boolean crew;
        final String employeeNameJSON = "name";
        final String employeeLicenseJSON = "license";
        final String employeeIsCrewJSON = "isCrew";

        Employee () {
            crew = false;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String name) {
            // TODO: Add employee name to the local sqlite db and try to retrieve license from name
            employeeName = name;
        }

        public String getEmployeeLicense() {
            return employeeLicense;
        }

        public void setEmployeeLicense(String license) {
            // TODO: Add employee license to the local sqlite db and retrieve name from license
            employeeLicense = license;
        }

        public boolean isCrew() {
            return crew;
        }

        public void setCrew(boolean isCrew) {
            //TODO: Add logic for setting data to sqlite db
            this.crew = isCrew;
        }

        JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(employeeNameJSON, employeeName);
                if (employeeLicense != null)
                    jsonObject.put(employeeLicenseJSON, employeeLicense);
                jsonObject.put(employeeIsCrewJSON, crew);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }
}

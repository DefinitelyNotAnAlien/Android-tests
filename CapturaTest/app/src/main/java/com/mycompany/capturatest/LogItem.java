package com.mycompany.capturatest;

import java.util.Date;

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

        public boolean isCanceled() {
            return canceled;
        }

        public void setCanceled(boolean cancel) {
            canceled = cancel;
        }
    }

    private class Discrepancy extends BaseLogItem {
        private boolean crewReport;
        private String flightNumber;
        private String operationLog;

        Discrepancy() {
            super();
            crewReport = false;
        }

        public boolean isCrewReport() {
            return crewReport;
        }

        public void setCrewReport(boolean crewReport) {
            this.crewReport = crewReport;
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
    }

    private class CorrectiveAction extends BaseLogItem {
        private boolean deferred;
        private String deferralBasis;

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
    }

    protected class Employee {
        private String employeeName;
        private String employeeLicense;

        public void setEmployeeName(String name) {
            // TODO: Logic for adding employee name to the apps local sqlite db
            employeeName = name;
        }

        public void setEmployeeLicense(String license) {
            // TODO: Logic for adding employee license to the apps local sqlite db
            employeeLicense = license;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public String getEmployeeLicense() {
            return employeeLicense;
        }
    }
}
